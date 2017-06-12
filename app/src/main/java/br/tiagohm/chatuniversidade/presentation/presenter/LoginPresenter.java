package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;

import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;

public class LoginPresenter extends MvpBasePresenter<LoginContract.View>
        implements LoginContract.Presenter {

    @Override
    public void mostrarTelaDeEntrar() {
        Logger.d("mostrarTelaDeEntrar()");
        getView().mostrarTelaDeEntrar();
    }

    @Override
    public void mostrarTelaDeRegistrar() {
        Logger.d("mostrarTelaDeRegistrar()");
        getView().mostrarTelaDeRegistrar();
    }

    @Override
    public void logadoComSucesso() {
        Logger.d("logadoComSucesso()");
        getView().mostrarTelaDoUsuario();
    }

    @Override
    public void registradoComSucesso() {
        Logger.d("registradoComSucesso(%s)");
        getView().mostrarTelaDoUsuario();
    }

    @Override
    public void erroAoLogar(String message) {
        getView().showMessage(message);
        ChatManager.deslogar();
    }

    @Override
    public void erroAoRegistrar(String message) {
        getView().showMessage(message);
        ChatManager.deslogar();
    }

    @Override
    public Usuario getUsuario() {
        return null;
    }
}
