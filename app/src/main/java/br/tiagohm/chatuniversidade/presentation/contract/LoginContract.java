package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;

public interface LoginContract {

    interface View extends BaseMvpView {

        LoginContract.Presenter getPresenter();

        void mostrarTelaDeEntrar();

        void mostrarTelaDeRegistrar();

        void mostrarTelaDoUsuario();
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void mostrarTelaDeEntrar();

        void mostrarTelaDeRegistrar();

        void logadoComSucesso();

        void registradoComSucesso();

        void erroAoLogar(String message);

        void erroAoRegistrar(String message);
    }
}
