package br.tiagohm.chatuniversidade.model.repository;

import android.support.annotation.NonNull;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import br.tiagohm.chatuniversidade.common.utils.Utils;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
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
    private final GroupChildEventListener groupChildEventListener = new GroupChildEventListener();
    private Usuario mUsuario;
    private HashMap<String, Grupo> mGrupos = new HashMap<>();
    private List<ChatManagerListener> mListeners = new ArrayList<>();

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
                                Logger.d("getUsuarioByEmail() = %b", dataSnapshot.exists());
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

    //TODO Carregar está aqui!!!
    private void carregar(FirebaseUser user) {
        getUsuarioByEmail(user.getEmail())
                .subscribe(new Consumer<Usuario>() {
                    @Override
                    public void accept(Usuario usuario) throws Exception {
                        mUsuario = usuario;
                        CHAT.child("usuarios").child(Utils.gerarHash(getUsuario().email))
                                .addValueEventListener(userValueEventListener);
                        CHAT.child("grupos").addChildEventListener(groupChildEventListener);
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

    public Collection<Grupo> getGrupos() {
        return mGrupos.values();
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
                                e.onError(ex);
                                e.onComplete();
                            }
                        });
            }
        });
    }

    public Observable<Boolean> editarGrupo(final Grupo grupo, final String novoNome, final int tipo) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("grupos").child(grupo.id)
                        .setValue(new Grupo(mUsuario, grupo.instituicao, novoNome, tipo))
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

        void novoGrupo(Grupo grupo);

        void grupoRemovido(Grupo grupo);

        void grupoModificado(Grupo grupo);
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

    public class GroupChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Grupo grupo = dataSnapshot.getValue(Grupo.class);
            String key = dataSnapshot.getKey();
            if (grupo != null && grupo.admin.equals(mUsuario.email)) {
                if (!mGrupos.containsKey(key)) {
                    Logger.d("Novo grupo: %s", grupo);
                    grupo.id = key;
                    mGrupos.put(key, grupo);
                    for (ChatManagerListener l : mListeners) l.novoGrupo(grupo);
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Grupo grupo = dataSnapshot.getValue(Grupo.class);
            String key = dataSnapshot.getKey();
            if (grupo != null && mGrupos.containsKey(key)) {
                grupo.id = key;
                mGrupos.put(key, grupo);
                for (ChatManagerListener l : mListeners) {
                    l.grupoModificado(grupo);
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Grupo grupo = dataSnapshot.getValue(Grupo.class);
            String key = dataSnapshot.getKey();
            if (grupo != null) {
                mGrupos.remove(key);
                for (ChatManagerListener l : mListeners) l.grupoRemovido(grupo);
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
