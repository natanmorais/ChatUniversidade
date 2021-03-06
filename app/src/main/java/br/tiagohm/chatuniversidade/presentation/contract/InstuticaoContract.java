package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Instituicao;

public interface InstuticaoContract {

    interface View extends MvpView {

        void updateList();
    }

    interface Presenter extends MvpPresenter<InstuticaoContract.View> {

        void novaInstituicao(String sigla, String nome, String endereco, String telefone, String email);

        void editarInstituicao(String id, String sigla, String nome, String endereco, String telefone, String email);

        void removerInstituicao(Instituicao instituicao);
    }
}
