package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface RegistrarContract {

    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<RegistrarContract.View> {

        LoginContract.Presenter getLoginPresenter();

        void registrar(String instituicao, String nome, int tipo, String matricula,
                       String email, String senha);
    }
}
