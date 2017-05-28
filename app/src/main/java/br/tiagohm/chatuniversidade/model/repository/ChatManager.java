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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.logger.Logger;

import java.util.regex.Pattern;

import br.tiagohm.chatuniversidade.common.utils.Utils;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class ChatManager {

    private static final DatabaseReference CHAT = FirebaseDatabase.getInstance().getReference().child("chat");

    private static boolean validarLogin(String login) {
        return Patterns.EMAIL_ADDRESS.matcher(login).matches();
    }

    private static boolean validarSenha(String senha) {
        return Pattern.compile("[a-zA-Z0-9]{8,32}").matcher(senha).matches();
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
    public static Observable<Usuario> registrar(final String instituicao, final String nome, final int tipo, final String matricula,
                                                final String email, final String senha) {
        Logger.d("registrar(%s, %s)", email, senha);

        //Validação do email e da senha.
        //TODO Validar outros campos.
        if (!validarLogin(email))
            return Observable.error(new IllegalArgumentException("O e-mail não é válido"));
        if (!validarSenha(senha))
            return Observable.error(new IllegalArgumentException("A senha não é válida"));

        return Observable.create(new ObservableOnSubscribe<Usuario>() {
            @Override
            public void subscribe(final ObservableEmitter<Usuario> e) throws Exception {
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
                                                               e.onNext(usuario);
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

    public static Observable<Boolean> deletarConta() {
        Logger.d("deletarConta()");

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String email = user.getEmail();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Logger.d("A conta foi deletada");
                                    CHAT.child("usuarios").child(Utils.gerarHash(email))
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        deslogar();
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

    public static Observable<Boolean> alterarSenha(final String novaSenha) {
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

    public static Observable<Boolean> atualizarUsuario(final Usuario usuario) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                CHAT.child("usuarios").child(Utils.gerarHash(usuario.email))
                        .setValue(usuario)
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
}
