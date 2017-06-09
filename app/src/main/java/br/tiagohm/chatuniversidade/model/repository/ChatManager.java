package br.tiagohm.chatuniversidade.model.repository;

import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import br.tiagohm.chatuniversidade.common.utils.Utils;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.chatuniversidade.model.entity.Conversa;
import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class ChatManager
        implements FirebaseAuth.AuthStateListener {

    private static final DatabaseReference CHAT = FirebaseDatabase.getInstance().getReference().child("chat");
    //Eventos
    private final UserValueEventListener userValueEventListener = new UserValueEventListener();
    private final InstitutionChildEventListener institutionChildEventListener = new InstitutionChildEventListener();
    private Usuario mUsuario;
    private HashMap<String, Grupo> mGrupos = new HashMap<>();
    private List<ChatManagerListener> mListeners = new ArrayList<>();
    private HashMap<String, Instituicao> mInstituicoes = new HashMap<>();

    public ChatManager() {
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private static boolean validarLogin(String login) {
        return Patterns.EMAIL_ADDRESS.matcher(login).matches();
    }

    private static boolean validarSenha(String senha) {
        return Pattern.compile("[a-zA-Z0-9]{8,32}").matcher(senha).matches();
    }

    private static Observable<Usuario> criarCadastroInicial(String instituicao, String nome, int tipo, String matricula,
                                                            final String email, String senha) {
        Logger.d("criarCadastroInicial()");
        final Usuario usuario = new Usuario(instituicao, nome, tipo, matricula, email);

        return Observable.create(new ObservableOnSubscribe<Usuario>() {
            @Override
            public void subscribe(final ObservableEmitter<Usuario> e) throws Exception {
                CHAT.child("usuarios").child(Utils.gerarHash(email))
                        .setValue(usuario)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(usuario);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                            }
                        });
            }
        });
    }

    /**
     * Loga um usuário usando um email e uma senha.
     */
    public static Observable<Boolean> logar(final String email, final String senha) {
        Logger.d("logar(%s, %s)", email, senha);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    e.onNext(false);
                                    e.onComplete();
                                } else {
                                    e.onNext(true);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Loga um usuário usando um email e uma senha.
     */
    public static Observable<Boolean> registrar(final String instituicao, final String nome, final int tipo, final String matricula,
                                                final String email, final String senha) {
        Logger.d("registrar(%s, %s)", email, senha);

        //Validação do email e da senha.
        //TODO Validar outros campos.
        if (!validarLogin(email))
            return Observable.error(new IllegalArgumentException("O e-mail não é válido"));
        if (!validarSenha(senha))
            return Observable.error(new IllegalArgumentException("A senha não é válida"));

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    e.onError(null);
                                } else {
                                    criarCadastroInicial(instituicao, nome, tipo, matricula, email, senha)
                                            .subscribe(new Consumer<Usuario>() {
                                                           @Override
                                                           public void accept(Usuario usuario) throws Exception {
                                                               e.onNext(true);
                                                               e.onComplete();
                                                           }
                                                       },
                                                    new Consumer<Throwable>() {
                                                        @Override
                                                        public void accept(Throwable t) throws Exception {
                                                            e.onError(t);
                                                        }
                                                    });
                                }
                            }
                        });
            }
        });
    }

    public static Observable<Usuario> getUsuarioByEmail(final String email) {
        Logger.d("getUsuarioByEmail(%s)", email);

        return Observable.create(new ObservableOnSubscribe<Usuario>() {
            @Override
            public void subscribe(final ObservableEmitter<Usuario> e) throws Exception {
                CHAT.child("usuarios").child(Utils.gerarHash(email))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                e.onNext(dataSnapshot.getValue(Usuario.class));
                                e.onComplete();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    public static void deslogar() {
        FirebaseAuth.getInstance().signOut();
    }

    public void add(ChatManagerListener listener) {
        mListeners.add(listener);
    }

    public void remove(ChatManagerListener listener) {
        mListeners.remove(listener);
    }

    private void carregar(FirebaseUser user) {
        getUsuarioByEmail(user.getEmail())
                .subscribe(new Consumer<Usuario>() {
                    @Override
                    public void accept(Usuario usuario) throws Exception {
                        mUsuario = usuario;
                        CHAT.child("usuarios").child(Utils.gerarHash(getUsuario().email))
                                .addValueEventListener(userValueEventListener);
                        CHAT.child("instituicoes").addChildEventListener(institutionChildEventListener);
                        Logger.d(getUsuario());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        //mUsuario = null;
                        t.printStackTrace();
                    }
                });
    }

    public Usuario getUsuario() {
        return mUsuario;
    }

    public List<Grupo> getGrupos() {
        return new ArrayList<>(mGrupos.values());
    }

    public List<Instituicao> getInstituicoes() {
        return new ArrayList<>(mInstituicoes.values());
    }

    public Observable<Boolean> deletarConta() {
        Logger.d("deletarConta()");

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("A conta foi deletada");
                                    CHAT.child("usuarios").child(Utils.gerarHash(getUsuario().email))
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        CHAT.child("usuarios").child(Utils.gerarHash(getUsuario().email))
                                                                .removeEventListener(userValueEventListener);
                                                        //mUsuario = null;
                                                        Logger.d("A conta foi deletada");
                                                        e.onNext(true);
                                                        e.onComplete();
                                                    } else {
                                                        Logger.d("Erro ao deletar a conta");
                                                        e.onNext(false);
                                                        e.onComplete();
                                                    }
                                                }
                                            });
                                } else {
                                    Logger.d("Erro ao deletar a conta");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    public Observable<Boolean> alterarSenha(final String novaSenha) {
        if (!validarSenha(novaSenha))
            return Observable.error(new IllegalArgumentException("A senha não é válida"));

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                FirebaseAuth.getInstance().getCurrentUser()
                        .updatePassword(novaSenha)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("A senha foi alterada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao alterar a senha");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    public Observable<Boolean> atualizarUsuario(String instituicao, String nome, String matricula) {

        final Usuario novoUsuario = new Usuario(instituicao, nome, getUsuario().tipo, matricula, getUsuario().email);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("usuarios").child(Utils.gerarHash(getUsuario().email))
                        .setValue(novoUsuario)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("A conta foi atualizada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao atualizar a conta");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    public Observable<Boolean> criarGrupo(Usuario admin, String instituicao, String nome, int tipo) {
        final Grupo grupo = new Grupo(admin, instituicao, nome, tipo);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").push().getKey();
                CHAT.child("grupos").child(id).setValue(grupo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                ex.printStackTrace();
                                e.onNext(false);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> novoUsuarioDoGrupo(final String grupoId, final Usuario usuario) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").child(grupoId).child("usuarios").push().getKey();
                CHAT.child("grupos").child(grupoId).child("usuarios")
                        .setValue(usuario)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                ex.printStackTrace();
                                e.onNext(false);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> editarGrupo(final String grupoId, final String novoNome, final int tipo) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("nome")
                        .setValue(novoNome)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                ex.printStackTrace();
                                e.onNext(false);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> deletarGrupo(final String grupoId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                ex.printStackTrace();
                                e.onNext(false);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> criarInstituicao(String sigla, String nome, String endereco, String telefone, String email) {
        final Instituicao instituicao = new Instituicao(sigla, nome, endereco, telefone, email);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("instituicoes").push().getKey();
                CHAT.child("instituicoes").child(id).setValue(instituicao)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> editarInstituicao(final String id, String sigla, String nome, String endereco, String telefone, String email) {

        final Instituicao instituicao = new Instituicao(sigla, nome, endereco, telefone, email);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("instituicoes").child(id)
                        .setValue(instituicao)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> deletarInstituicao(final String id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("instituicoes").child(id)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<List<Aula>> verAulas(final String grupoId) {

        return Observable.create(new ObservableOnSubscribe<List<Aula>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<Aula>> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("aulas")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Aula> aulas = new ArrayList<>((int) dataSnapshot.getChildrenCount());
                                for (DataSnapshot aulaSnapshot : dataSnapshot.getChildren()) {
                                    Aula aula = aulaSnapshot.getValue(Aula.class);
                                    if (aula != null) {
                                        aula.id = aulaSnapshot.getKey();
                                        aulas.add(aula);
                                    }
                                }
                                e.onNext(aulas);
                                e.onComplete();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> criarAula(final String grupoId, String titulo, String conteudo) {
        final Aula aula = new Aula(titulo, conteudo);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").child(grupoId).child("aulas").push().getKey();
                CHAT.child("grupos").child(grupoId).child("aulas").child(id)
                        .setValue(aula)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> editarAula(final String grupoId, final String id, String titulo, String conteudo) {
        final Aula aula = new Aula(titulo, conteudo);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("aulas").child(id)
                        .setValue(aula)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> deletarAula(final String grupoId, final String id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("aulas").child(id)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Pair<Integer, Conversa>> monitorarConversas(final String grupoId) {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Conversa>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Conversa>> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("conversas")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                                conversa.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(0, conversa));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                                conversa.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(1, conversa));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                                conversa.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(2, conversa));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Conversa conversa = dataSnapshot.getValue(Conversa.class);
                                conversa.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(3, conversa));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    public Observable<Pair<Integer, Grupo>> monitorarGruposDoUsuario(final String usuarioId) {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Grupo>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Grupo>> e) throws Exception {
                CHAT.child("usuarios").child(usuarioId).child("grupos")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                grupo.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(0, grupo));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                grupo.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(1, grupo));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                grupo.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(2, grupo));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                grupo.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(3, grupo));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    public Observable<Boolean> criarConversa(final String grupoId, String texto, Usuario usuario) {
        final Conversa conversa = new Conversa(usuario, texto, System.currentTimeMillis());

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").child(grupoId).child("conversas").push().getKey();
                CHAT.child("grupos").child(grupoId).child("conversas").child(id)
                        .setValue(conversa)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                ex.printStackTrace();
                                e.onNext(false);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> criarConvite(String nomeGrupo, Usuario remetente, String destinatario) {
        final Convite convite = new Convite(nomeGrupo,remetente,destinatario);

        return Observable.create(new ObservableOnSubscribe<java.lang.Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<java.lang.Boolean> e) throws Exception {
                final String id = CHAT.child("convites").push().getKey();
                CHAT.child("convites").child(id)
                        .setValue(convite)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> deletarConvite(final Convite convite) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("convites").child(convite.id)
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void nada) {
                                e.onNext(true);
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception ex) {
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            carregar(firebaseAuth.getCurrentUser());
        }
    }

    public interface ChatManagerListener {

        void novaInstituicao(Instituicao instituicao);

        void instituicaoModificada(Instituicao instituicao);

        void instituicaoRemovida(Instituicao instituicao);
    }

    public class UserValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Usuario usuario = dataSnapshot.getValue(Usuario.class);
            if (usuario != null) {
                mUsuario = usuario;
            }
            Logger.d("usuario foi alterado: %s", getUsuario());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }

    public class InstitutionChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
            String key = dataSnapshot.getKey();
            if (instituicao != null) {
                if (!mGrupos.containsKey(key)) {
                    Logger.d("Nova Instituicao: %s", instituicao);
                    instituicao.id = key;
                    mInstituicoes.put(key, instituicao);
                    for (ChatManagerListener l : mListeners) l.novaInstituicao(instituicao);
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
            String key = dataSnapshot.getKey();
            if (instituicao != null) {
                instituicao.id = key;
                mInstituicoes.put(key, instituicao);
                for (ChatManagerListener l : mListeners) {
                    l.instituicaoModificada(instituicao);
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
            String key = dataSnapshot.getKey();
            if (instituicao != null) {
                mInstituicoes.remove(key);
                for (ChatManagerListener l : mListeners) l.instituicaoRemovida(instituicao);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
