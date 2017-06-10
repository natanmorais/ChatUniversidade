package br.tiagohm.chatuniversidade.presentation.presenter;

import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.InstuticaoContract;
import io.reactivex.functions.Consumer;

public class InstuticoesPresenter extends MvpBasePresenter<InstuticaoContract.View>
        implements InstuticaoContract.Presenter {

    @Inject
    ChatManager chatManager;

    public InstuticoesPresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public Usuario getUsuario() {
        return chatManager.getUsuario();
    }

    @Override
    public void novaInstituicao(String sigla, String nome, String endereco, String telefone, String email) {
        chatManager.criarInstituicao(sigla, nome, endereco, telefone, email)
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
    public void editarInstituicao(String id, String sigla, String nome, String endereco, String telefone, String email) {
        chatManager.editarInstituicao(id, sigla, nome, endereco, telefone, email)
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
    public void removerInstituicao(Instituicao instituicao) {
        chatManager.deletarInstituicao(instituicao.id)
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
    public void monitorarInstituicao() {
        chatManager.monitorarInstituicoes()
                .subscribe(new Consumer<Pair<Integer, Instituicao>>() {
                    @Override
                    public void accept(Pair<Integer, Instituicao> i) throws Exception {
                        //Added
                        if (i.first == 0) {
                            if (isViewAttached()) {
                                getView().adicionarInstituicao(i.second);
                                getView().updateList();
                            }
                        }
                        //Deleted
                        else if (i.first == 2) {
                            if (isViewAttached()) {
                                getView().removerInstituicao(i.second);
                                getView().updateList();
                            }
                        }
                    }
                });
    }
}
