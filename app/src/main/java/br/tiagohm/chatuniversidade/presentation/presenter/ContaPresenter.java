package br.tiagohm.chatuniversidade.presentation.presenter;

import android.text.TextUtils;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.ContaContract;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ContaPresenter extends MvpBasePresenter<ContaContract.View>
        implements ContaContract.Presenter {

    @Override
    public void salvarConta(String senha,
                            final Usuario usuario) {
        if (!TextUtils.isEmpty(senha)) {
            ChatManager.alterarSenha(senha)
                    .subscribeOn(Schedulers.trampoline())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            if (success) {
                                getView().showMessage("A senha foi alterado com sucesso!");
                                salvarUsuario(usuario);
                            } else {
                                getView().showMessage("Erro ao alterar a senha!");
                            }
                        }
                    });
        } else {
            salvarUsuario(usuario);
        }
    }

    @Override
    public void deletarConta() {
        ChatManager.deletarConta()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().showMessage("A conta foi removida com sucesso!");
                            getView().finish();
                        } else {
                            getView().showMessage("Erro ao remover a conta!");
                        }
                    }
                });
    }

    private void salvarUsuario(Usuario usuario) {
        ChatManager.atualizarUsuario(usuario)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            getView().showMessage("A conta foi atualizada com sucesso!");
                            getView().finish();
                        } else {
                            getView().showMessage("Erro ao atualizar a conta!");
                        }
                    }
                });
    }
}
