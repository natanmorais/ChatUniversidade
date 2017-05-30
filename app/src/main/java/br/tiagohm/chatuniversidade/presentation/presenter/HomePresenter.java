package br.tiagohm.chatuniversidade.presentation.presenter;

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
    public void criarGrupo(String nome, String instituicao) {
        chatManager.criarGrupo(chatManager.getUsuario(),
                instituicao,
                nome,
                0)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().showGrupos(chatManager.getGrupos());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public void editarGrupo(Grupo grupo, String nomeNovo) {
        chatManager.editarGrupo(grupo,
                nomeNovo,
                0)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().showGrupos(chatManager.getGrupos());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public void deletarGrupo(Grupo grupo) {
        chatManager.deletarGrupo(grupo)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().showGrupos(chatManager.getGrupos());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
