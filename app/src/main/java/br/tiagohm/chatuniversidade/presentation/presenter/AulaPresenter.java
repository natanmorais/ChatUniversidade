package br.tiagohm.chatuniversidade.presentation.presenter;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.AulaContract;
import br.tiagohm.chatuniversidade.presentation.contract.InstuticaoContract;
import io.reactivex.functions.Consumer;

/**
 * Created by root on 01/06/17.
 */
public class AulaPresenter extends MvpBasePresenter<AulaContract.View>
        implements AulaContract.Presenter{

    @Inject
    ChatManager chatManager;

    public AulaPresenter() {
        App.getChatComponent().inject(this);
    }

    @Override
    public void novaAula(String titulo, String conteudo) {
        chatManager.criarAula(titulo, conteudo)
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
    public void editarAula(String id, String titulo, String conteudo) {
        chatManager.editarAula(id, titulo, conteudo)
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
    public void removerAula(Aula aula) {
        chatManager.deletarAula(aula.id)
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
}
