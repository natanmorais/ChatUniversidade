package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;

import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.EntrarContract;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;
import io.reactivex.functions.Consumer;

public class EntrarPresenter extends MvpBasePresenter<EntrarContract.View>
        implements EntrarContract.Presenter {

    private ChatManager mChatManager;
    private LoginContract.Presenter mLoginPresenter;

    public EntrarPresenter(LoginContract.Presenter presenter) {
        mLoginPresenter = presenter;
    }

    @Override
    public void entrar(final String email, final String senha) {
        Logger.d("entrar(%s, %s)", email, senha);
        ChatManager.logar(email, senha).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean success) throws Exception {
                Logger.d("entrou: %b", success);
                if (!success) {
                    getLoginPresenter().erroAoLogar("Senha ou e-mail incorreto");
                } else {
                    Logger.d("logou com sucesso!");
                    getLoginPresenter().logadoComSucesso();
                }
            }
        });
    }

    @Override
    public LoginContract.Presenter getLoginPresenter() {
        return mLoginPresenter;
    }
}
