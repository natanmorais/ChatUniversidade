package br.tiagohm.chatuniversidade.presentation.presenter;

import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

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
    public void monitorarAulas(String grupoId) {
        chatManager.monitorarAulas(grupoId)
                .subscribe(new Consumer<Pair<Integer, Aula>>() {
                    @Override
                    public void accept(Pair<Integer, Aula> aula) throws Exception {
                        //Added
                        if (aula.first == 0) {
                            getView().adicionarAula(aula.second);
                            getView().atualizarLista();
                        }
                        //Removed
                        else if (aula.first == 2) {
                            getView().removerAula(aula.second);
                            getView().atualizarLista();
                        }
                    }
                });
    }

    @Override
    public void novaAula(String grupoId, String titulo, String conteudo) {
        chatManager.criarAula(grupoId, titulo, conteudo)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            getView().showMessage("Erro ao criar a aula");
                        }
                    }
                });
    }

    @Override
    public void editarAula(String grupoId, String id, String titulo, String conteudo) {
        chatManager.editarAula(grupoId, id, titulo, conteudo)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            getView().showMessage("Erro ao editar a aula");
                        }
                    }
                });
    }

    @Override
    public void removerAula(String grupoId, Aula aula) {
        chatManager.deletarAula(grupoId, aula.id)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (!success) {
                            getView().showMessage("Erro ao remover a aula");
                        }
                    }
                });
    }
}
