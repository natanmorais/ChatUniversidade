package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Usuario;

public interface LoginContract {

    interface View extends MvpView {

        LoginContract.Presenter getPresenter();

        void mostrarTelaDeEntrar();

        void mostrarTelaDeRegistrar();

        void mostrarTelaDoUsuario();

        void showMessage(String message);
    }

    interface Presenter extends MvpPresenter<LoginContract.View> {

        void mostrarTelaDeEntrar();

        void mostrarTelaDeRegistrar();

        void logadoComSucesso();

        void registradoComSucesso();

        void erroAoLogar(String message);

        void erroAoRegistrar(String message);
    }
}
