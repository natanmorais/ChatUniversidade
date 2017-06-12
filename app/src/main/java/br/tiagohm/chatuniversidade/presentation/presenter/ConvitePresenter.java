package br.tiagohm.chatuniversidade.presentation.presenter;

import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Convite;
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

    @Override
    public void obterOsConvites() {
        Logger.d("obterOsConvites");

        chatManager.monitorarConvitesRemetente(chatManager.getUsuario().email)
                .subscribe(new Consumer<Pair<Integer, Convite>>() {
                    @Override
                    public void accept(Pair<Integer, Convite> c) throws Exception {
                        if (c.first == 0) {
                            if (isViewAttached()) {
                                getView().adicionarConviteEnviado(c.second);
                                getView().atualizarListaEnviados();
                            }
                        } else if (c.first == 2) {
                            if (isViewAttached()) {
                                getView().removerConviteEnviado(c.second);
                                getView().atualizarListaEnviados();
                            }
                        }
                    }
                });
        chatManager.monitorarConvitesDestinatario(chatManager.getUsuario().email)
                .subscribe(new Consumer<Pair<Integer, Convite>>() {
                    @Override
                    public void accept(Pair<Integer, Convite> c) throws Exception {
                        if (c.first == 0) {
                            if (isViewAttached()) {
                                getView().adicionarConviteRecebido(c.second);
                                getView().atualizarListaRecebidos();
                            }
                        } else if (c.first == 2) {
                            if (isViewAttached()) {
                                getView().removerConviteRecebido(c.second);
                                getView().atualizarListaRecebidos();
                            }
                        }
                    }
                });
    }
}
