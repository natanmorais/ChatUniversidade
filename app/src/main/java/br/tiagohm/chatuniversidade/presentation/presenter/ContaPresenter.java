package br.tiagohm.chatuniversidade.presentation.presenter;

import android.text.TextUtils;
import android.util.Pair;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.ContaContract;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ContaPresenter extends MvpBasePresenter<ContaContract.View>
        implements ContaContract.Presenter {

    @Inject
    ChatManager chatManager;

    public ContaPresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public void salvarConta(String senha,
                            final String instituicao, final String nome, final String matricula) {
        if (!TextUtils.isEmpty(senha)) {
            chatManager.alterarSenha(senha)
                    .subscribeOn(Schedulers.trampoline())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            if (success) {
                                if (isViewAttached()) {
                                    getView().showMessage("A senha foi alterado com sucesso!");
                                    salvarUsuario(instituicao, nome, matricula);
                                }
                            } else {
                                if (isViewAttached()) {
                                    getView().showMessage("Erro ao alterar a senha!");
                                }
                            }
                        }
                    });
        } else {
            salvarUsuario(instituicao, nome, matricula);
        }
    }

    @Override
    public void deletarConta() {
        chatManager.deletarConta()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            if (isViewAttached()) {
                                getView().showMessage("A conta foi removida com sucesso!");
                                getView().finish();
                            }
                        } else {
                            if (isViewAttached()) {
                                getView().showMessage("Erro ao remover a conta!");
                            }
                        }
                    }
                });
    }

    private void salvarUsuario(String instituicao, String nome, String matricula) {
        chatManager.atualizarUsuario(instituicao, nome, matricula)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            if (isViewAttached()) {
                                getView().showMessage("A conta foi atualizada com sucesso!");
                                getView().finish();
                            }
                        } else {
                            if (isViewAttached()) {
                                getView().showMessage("Erro ao atualizar a conta!");
                            }
                        }
                    }
                });
    }

    @Override
    public void obterInstituicoes() {
        chatManager.monitorarInstituicoes()
                .subscribe(new Consumer<Pair<Integer, Instituicao>>() {
                    @Override
                    public void accept(Pair<Integer, Instituicao> i) throws Exception {
                        if (i.first == 0) {
                            if (isViewAttached()) {
                                getView().adicionarInstituicao(i.second);
                                getView().atualizaListaDeInstituicoes();
                            }
                        }
                    }
                });
    }
}
