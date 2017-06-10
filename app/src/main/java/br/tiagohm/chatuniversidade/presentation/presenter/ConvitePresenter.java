package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.ConviteContract;
import io.reactivex.functions.Consumer;

/**
 * Created by root on 09/06/17.
 */
public class ConvitePresenter extends MvpBasePresenter<ConviteContract.View>
        implements ConviteContract.Presenter {

    @Inject
    ChatManager chatManager;

    public ConvitePresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public void novoConvite(String nomeGrupo, String email) {
        chatManager.criarConvite(nomeGrupo, chatManager.getUsuario(), email)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public void aceitarConvite(Convite convite) {
        aceitarConvite(convite);
        removerConvite(convite);
    }

    @Override
    public void removerConvite(Convite convite) {
        chatManager.deletarConvite(convite)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

}
