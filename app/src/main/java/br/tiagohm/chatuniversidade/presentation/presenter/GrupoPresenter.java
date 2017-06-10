package br.tiagohm.chatuniversidade.presentation.presenter;

import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Conversa;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.GrupoContract;
import io.reactivex.functions.Consumer;

public class GrupoPresenter extends MvpBasePresenter<GrupoContract.View>
        implements GrupoContract.Presenter {

    @Inject
    ChatManager chatManager;

    public GrupoPresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public Usuario getUsuario() {
        return chatManager.getUsuario();
    }

    @Override
    public void monitorarConversas(String grupoId) {
        chatManager.monitorarConversas(grupoId)
                .subscribe(new Consumer<Pair<Integer, Conversa>>() {
                    @Override
                    public void accept(Pair<Integer, Conversa> conversa) throws Exception {
                        //Added
                        if (conversa.first == 0) {
                            if (isViewAttached()) {
                                getView().adicionarConversa(conversa.second);
                            }
                        }
                        //Changed.
                        else if (conversa.first == 1) {
                            //Conversas nao podem ser modificadas.
                        }
                        //Removed
                        else if (conversa.first == 2) {
                            if (isViewAttached()) {
                                getView().removerConversa(conversa.second);
                            }
                        }
                    }
                });
    }

    @Override
    public void enviarConversa(String grupoId, String mensagem) {
        chatManager.criarConversa(grupoId, mensagem, getUsuario())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            if (isViewAttached()) {
                                getView().showMessage("Erro ao enviar a mensagem!!!");
                            }
                        }
                    }
                });
    }
}
