package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.HomeContract;

public class HomePresenter extends MvpBasePresenter<HomeContract.View>
        implements HomeContract.Presenter {

    @Override
    public void deslogar() {
        ChatManager.deslogar();
    }
}
