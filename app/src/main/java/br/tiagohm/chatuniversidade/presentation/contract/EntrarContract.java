package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;

public interface EntrarContract {

    interface View extends BaseMvpView {

    }

    interface Presenter extends BaseMvpPresenter<View> {

        LoginContract.Presenter getLoginPresenter();

        void entrar(final String email, final String senha);
    }
}
