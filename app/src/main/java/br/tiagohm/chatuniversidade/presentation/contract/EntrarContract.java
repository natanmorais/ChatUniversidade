package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface EntrarContract {

    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<EntrarContract.View> {

        LoginContract.Presenter getLoginPresenter();

        void entrar(final String email, final String senha);
    }
}
