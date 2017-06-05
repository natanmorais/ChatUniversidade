package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.GrupoContract;

public class GrupoPresenter extends MvpBasePresenter<GrupoContract.View>
        implements GrupoContract.Presenter {

    @Inject
    ChatManager chatManager;

    public GrupoPresenter() {
        App.getChatComponent().inject(this);
    }

    public void obterConversas(Grupo grupo) {

    }
}
