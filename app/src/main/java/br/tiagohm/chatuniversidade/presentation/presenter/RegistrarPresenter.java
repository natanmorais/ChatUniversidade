package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;

import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;
import br.tiagohm.chatuniversidade.presentation.contract.RegistrarContract;
import io.reactivex.functions.Consumer;

public class RegistrarPresenter extends MvpBasePresenter<RegistrarContract.View>
        implements RegistrarContract.Presenter {

    private ChatManager mChatManager;
    private LoginContract.Presenter mLoginPresenter;

    public RegistrarPresenter(LoginContract.Presenter presenter) {
        mLoginPresenter = presenter;
    }

    @Override
    public void registrar(final String instituicao, final String nome, final int tipo, final String matricula,
                          final String email, final String senha) {
        Logger.d("registrar(...)", email, senha);
        ChatManager.registrar(instituicao, nome, tipo, matricula, email, senha)
                .subscribe(new Consumer<Usuario>() {
                               @Override
                               public void accept(Usuario usuario) throws Exception {
                                   Logger.d("usuário criado!");
                                   getLoginPresenter().registradoComSucesso(usuario);
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                Logger.d("erro ao criar o usuário");
                                getLoginPresenter().erroAoRegistrar(t.getMessage());
                            }
                        });
    }

    @Override
    public LoginContract.Presenter getLoginPresenter() {
        return mLoginPresenter;
    }
}
