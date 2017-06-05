package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import java.util.List;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.AulasContract;
import io.reactivex.functions.Consumer;

public class AulasPresenter extends MvpBasePresenter<AulasContract.View>
        implements AulasContract.Presenter {

    @Inject
    ChatManager chatManager;

    public AulasPresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public void novaAula(String grupoId, String titulo, String conteudo) {
        chatManager.criarAula(grupoId, titulo, conteudo)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().showMessage("Aula criada!");
                            getView().carregarAulas();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        t.printStackTrace();
                        getView().showMessage("Erro ao criar a aula: " + t.getMessage());
                    }
                });
    }

    @Override
    public void editarAula(String grupoId, String id, String titulo, String conteudo) {
        chatManager.editarAula(grupoId, id, titulo, conteudo)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().carregarAulas();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getView().showMessage("Erro ao editar aula!");
                    }
                });
    }

    @Override
    public void removerAula(String grupoId, Aula aula) {
        chatManager.deletarAula(grupoId, aula.id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().carregarAulas();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getView().showMessage("Erro ao remover aula!");
                    }
                });
    }

    @Override
    public void carregarAulas(String grupoId) {
        chatManager.verAulas(grupoId)
                .subscribe(new Consumer<List<Aula>>() {
                    @Override
                    public void accept(List<Aula> aulas) throws Exception {
                        getView().mostrarAulas(aulas);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
