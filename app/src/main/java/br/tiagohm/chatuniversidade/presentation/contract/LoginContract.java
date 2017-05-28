package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Usuario;

public interface LoginContract {

    interface View extends MvpView {

        LoginContract.Presenter getPresenter();

        void mostrarTelaDeEntrar();

        void mostrarTelaDeRegistrar();

        void mostrarTelaDoUsuario(Usuario usuario);

        void showMessage(String message);
    }

    interface Presenter extends MvpPresenter<LoginContract.View> {

        void mostrarTelaDeEntrar();

        void mostrarTelaDeRegistrar();

        void logadoComSucesso(Usuario usuario);

        void registradoComSucesso(Usuario usuario);

        void erroAoLogar(String message);

        void erroAoRegistrar(String message);
    }
}
