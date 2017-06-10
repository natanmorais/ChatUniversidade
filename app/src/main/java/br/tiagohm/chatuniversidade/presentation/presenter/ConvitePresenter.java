package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.ConviteContract;
import io.reactivex.functions.Consumer;

public class ConvitePresenter extends MvpBasePresenter<ConviteContract.View>
        implements ConviteContract.Presenter {

    @Inject
    ChatManager chatManager;

    public ConvitePresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public void novoConvite(Grupo grupo, String email) {
        chatManager.criarConvite(grupo, chatManager.getUsuario().email, email)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                    }
                });
    }

    @Override
    public void aceitarConvite(final Convite convite) {
        chatManager.aceitarConvite(convite)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            removerConvite(convite);
                        }
                    }
                });
    }

    @Override
    public void revogarConvite(Convite convite) {
        removerConvite(convite);
    }

    @Override
    public void recusarConvite(Convite convite) {
        removerConvite(convite);
    }

    /**
     * Remove um convite.
     */
    private void removerConvite(Convite convite) {
        chatManager.deletarConvite(convite.id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                    }
                });
    }

}
