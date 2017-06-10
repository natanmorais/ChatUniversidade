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

public class ChatManager {

    private static final DatabaseReference CHAT = FirebaseDatabase.getInstance().getReference().child("chat");
    private Usuario mUsuario;

    public ChatManager() {
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
                                Logger.d("Usuario encontrado");
                                e.onNext(dataSnapshot.getValue(Usuario.class));
                                e.onComplete();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Logger.d("Usuario nao encontrado");
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    /**
     * Desloga o usuário.
     */
    public static void deslogar() {
        FirebaseAuth.getInstance().signOut();
    }

    public Observable<Boolean> carregar(final String email) {
        Logger.d("carregar()");
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                getUsuarioByEmail(email)
                        .subscribe(new Consumer<Usuario>() {
                            @Override
                            public void accept(Usuario usuario) throws Exception {
                                mUsuario = usuario;
                                e.onNext(true);
                                e.onComplete();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                t.printStackTrace();
                                mUsuario = null;
                                e.onNext(false);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Usuario getUsuario() {
        return mUsuario;
    }

    /**
     * Remove a conta.
     */
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
                                                        mUsuario = null;
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

    /**
     * Altera a senha.
     */
    public Observable<Boolean> alterarSenha(final String novaSenha) {
        if (!validarSenha(novaSenha)) {
            return Observable.error(new IllegalArgumentException("A senha não é válida"));
        }

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

    /**
     * Modifica alguns dados do usuario.
     */
    public Observable<Boolean> atualizarUsuario(String instituicao, String nome, String matricula) {

        mUsuario.instituicao = instituicao;
        mUsuario.matricula = matricula;
        mUsuario.nome = nome;

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("usuarios").child(Utils.gerarHash(getUsuario().email))
                        .setValue(mUsuario)
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

    /**
     * Cria um grupo com o usuario como administrador.
     */
    public Observable<Boolean> criarGrupo(String instituicao, String nome, int tipo) {
        return criarGrupo(mUsuario, instituicao, nome, tipo);
    }

    /**
     * Cria um grupo com um usuario como administrador.
     */
    public Observable<Boolean> criarGrupo(Usuario admin, String instituicao, String nome, int tipo) {
        final Grupo grupo = new Grupo(admin, instituicao, nome, tipo);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").push().getKey();
                CHAT.child("grupos").child(id).setValue(grupo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Grupo criado");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao criar grupo");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Adiciona um usuario ao grupo.
     */
    public Observable<Boolean> adicionarUsuarioAoGrupo(final String grupoId, final Usuario usuario) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("usuarios").child(usuario.getIdAsHash())
                        .setValue(usuario) //ou true?
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Usuario adicionado ao grupo");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao adicionar o usuario ao grupo");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Modifica os dados de um grupo.
     */
    public Observable<Boolean> editarGrupo(final String grupoId, final String novoNome, final int tipo) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("nome")
                        .setValue(novoNome)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Grupo alterado");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao modificar os dados do grupo");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Remove um grupo.
     */
    public Observable<Boolean> deletarGrupo(final String grupoId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Grupo removido");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao remover o grupo");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Eventos de criação, modificação e remoção.
     */
    public Observable<Pair<Integer, Instituicao>> monitorarInstituicoes() {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Instituicao>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Instituicao>> e) throws Exception {
                CHAT.child("instituicoes")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
                                instituicao.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(0, instituicao));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
                                instituicao.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(1, instituicao));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
                                instituicao.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(2, instituicao));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Instituicao instituicao = dataSnapshot.getValue(Instituicao.class);
                                instituicao.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(3, instituicao));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    /**
     * Cria uma instituicao.
     */
    public Observable<Boolean> criarInstituicao(String sigla, String nome, String endereco, String telefone, String email) {
        final Instituicao instituicao = new Instituicao(sigla, nome, endereco, telefone, email);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("instituicoes").push().getKey();
                CHAT.child("instituicoes").child(id).setValue(instituicao)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Instituicao criada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao criar a instituicao");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Modifica os dados de um instituicao.
     */
    public Observable<Boolean> editarInstituicao(final String instituicaoId, String sigla, String nome, String endereco, String telefone, String email) {

        final Instituicao instituicao = new Instituicao(sigla, nome, endereco, telefone, email);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("instituicoes").child(instituicaoId)
                        .setValue(instituicao)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Instituicao modificada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao modificar a instituicao");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Remove uma instituição.
     */
    public Observable<Boolean> deletarInstituicao(final String instituicaoId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("instituicoes").child(instituicaoId)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Instituicao removida");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao remover a instituicao");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Eventos de criação, modificação e remoção.
     */
    public Observable<Pair<Integer, Aula>> monitorarAulas(final String grupoId) {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Aula>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Aula>> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("aulas")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Aula aula = dataSnapshot.getValue(Aula.class);
                                aula.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(0, aula));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Aula aula = dataSnapshot.getValue(Aula.class);
                                aula.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(1, aula));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Aula aula = dataSnapshot.getValue(Aula.class);
                                aula.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(2, aula));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Aula aula = dataSnapshot.getValue(Aula.class);
                                aula.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(3, aula));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    /**
     * Cria uma aula.
     */
    public Observable<Boolean> criarAula(final String grupoId, String titulo, String conteudo) {
        final Aula aula = new Aula(titulo, conteudo);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").child(grupoId).child("aulas").push().getKey();
                CHAT.child("grupos").child(grupoId).child("aulas").child(id)
                        .setValue(aula)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Aula criada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao criar a aula");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Modifica os dados da aula.
     */
    public Observable<Boolean> editarAula(final String grupoId, final String aulaId, String titulo, String conteudo) {
        final Aula aula = new Aula(titulo, conteudo);

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("aulas").child(aulaId)
                        .setValue(aula)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Aula modificada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao modificar a aula");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Remove uma aula.
     */
    public Observable<Boolean> deletarAula(final String grupoId, final String aulaId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupoId).child("aulas").child(aulaId)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Aula removida");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao remover a aula");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Eventos de criação, modificão e remoção.
     */
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

    /**
     * Eventos de criação, modificão e remoção.
     */
    public Observable<Pair<Integer, Grupo>> monitorarMeusGrupos() {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Grupo>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Grupo>> e) throws Exception {
                CHAT.child("grupos")
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                if (grupo.admin.equals(mUsuario)) {
                                    grupo.id = dataSnapshot.getKey();
                                    e.onNext(new Pair<>(0, grupo));
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                if (grupo.admin.equals(mUsuario)) {
                                    grupo.id = dataSnapshot.getKey();
                                    e.onNext(new Pair<>(1, grupo));
                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                if (grupo.admin.equals(mUsuario)) {
                                    grupo.id = dataSnapshot.getKey();
                                    e.onNext(new Pair<>(2, grupo));
                                }
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                                if (grupo.admin.equals(mUsuario)) {
                                    grupo.id = dataSnapshot.getKey();
                                    e.onNext(new Pair<>(3, grupo));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    /**
     * Cria uma conversa.
     */
    public Observable<Boolean> criarConversa(final String grupoId, String texto, Usuario usuario) {
        final Conversa conversa = new Conversa(usuario, texto, System.currentTimeMillis());

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                final String id = CHAT.child("grupos").child(grupoId).child("conversas").push().getKey();
                CHAT.child("grupos").child(grupoId).child("conversas").child(id)
                        .setValue(conversa)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Conversa criada");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao criar a conversa");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    public Observable<Boolean> criarConvite(Grupo grupo, String destinatario) {
        return criarConvite(grupo, mUsuario.email, destinatario);
    }

    public Observable<Pair<Integer, Convite>> monitorarConvitesRemetente(final String emailDoUsuario) {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Convite>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Convite>> e) throws Exception {
                CHAT.child("convites").orderByChild("remetente")
                        .equalTo(emailDoUsuario)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(0, convite));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(1, convite));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(2, convite));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(3, convite));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    public Observable<Pair<Integer, Convite>> monitorarConvitesDestinatario(final String emailDoUsuario) {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Convite>>() {
            @Override
            public void subscribe(final ObservableEmitter<Pair<Integer, Convite>> e) throws Exception {
                CHAT.child("convites").orderByChild("destinatario")
                        .equalTo(emailDoUsuario)
                        .addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(0, convite));
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(1, convite));
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(2, convite));
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                Convite convite = dataSnapshot.getValue(Convite.class);
                                convite.id = dataSnapshot.getKey();
                                e.onNext(new Pair<>(3, convite));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                e.onError(databaseError.toException());
                            }
                        });
            }
        });
    }

    /**
     * Cria um convite.
     */
    public Observable<Boolean> criarConvite(final Grupo grupo, String remetente, String destinatario) {
        final Convite convite = new Convite(grupo, remetente, destinatario);

        return Observable.create(new ObservableOnSubscribe<java.lang.Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<java.lang.Boolean> e) throws Exception {
                final String id = CHAT.child("convites").push().getKey();
                CHAT.child("convites").child(id)
                        .setValue(convite)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Convite criado");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao criar o convite");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Remove um convite.
     */
    public Observable<Boolean> deletarConvite(final String conviteId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("convites").child(conviteId)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("Convite removido");
                                    e.onNext(true);
                                    e.onComplete();
                                } else {
                                    Logger.d("Erro ao remover o convite");
                                    e.onNext(false);
                                    e.onComplete();
                                }
                            }
                        });
            }
        });
    }

    /**
     * Aceita um convite.
     */
    public Observable<Boolean> aceitarConvite(final Convite convite) {
        return adicionarUsuarioAoGrupo(convite.grupo.id, mUsuario);
    }

    public Observable<Boolean> alterarAdminDoGrupo(final Grupo grupo, Usuario admin) {
        return null;
    }
}
