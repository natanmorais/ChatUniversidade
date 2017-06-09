package br.tiagohm.chatuniversidade.presentation.presenter;

import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.HomeContract;
import io.reactivex.functions.Consumer;

public class HomePresenter extends MvpBasePresenter<HomeContract.View>
        implements HomeContract.Presenter {

    @Inject
    ChatManager chatManager;

    public HomePresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public void deslogar() {
        ChatManager.deslogar();
    }

    @Override
    public void monitorarMeusGrupos() {
        chatManager.monitorarGruposDoUsuario(chatManager.getUsuario().getId())
                .subscribe(new Consumer<Pair<Integer, Grupo>>() {
                    @Override
                    public void accept(Pair<Integer, Grupo> grupo) throws Exception {
                        //Added
                        if (grupo.first == 0) {
                            getView().adicionarGrupo(grupo.second);
                        }
                        //Changed.
                        else if (grupo.first == 1) {
                            //Conversas nao podem ser modificadas.
                        }
                        //Removed
                        else if (grupo.first == 2) {
                            getView().removerGrupo(grupo.second);
                        }
                    }
                });
    }

    @Override
    public void criarGrupo(String nome, String instituicao) {
        chatManager.criarGrupo(chatManager.getUsuario(),
                instituicao,
                nome,
                0)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            getView().showMessage("Erro ao criar o grupo!!!");
                        }
                    }
                });
    }

    @Override
    public void editarGrupo(String grupoId, String nomeNovo) {
        chatManager.editarGrupo(grupoId,
                nomeNovo,
                0)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            getView().showMessage("Erro ao editar o grupo!!!");
                        }
                    }
                });
    }

    @Override
    public void deletarGrupo(String grupoId) {
        chatManager.deletarGrupo(grupoId)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            getView().showMessage("Erro ao remover o grupo!!!");
                        }
                    }
                });
    }
}
