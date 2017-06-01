package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;

/**
 * Created by root on 01/06/17.
 */
public interface AulaContract {

    interface View extends MvpView {

        void updateList();
    }

    interface Presenter extends MvpPresenter<AulaContract.View> {

        void novaAula(String titulo, String conteudo);

        void editarAula(String id, String titulo, String conteudo);

        void removerAula(Aula aula);
    }
}
