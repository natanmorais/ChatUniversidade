package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import br.tiagohm.chatuniversidade.model.entity.Aula;

public interface AulasContract {

    interface View extends MvpView {

        void showMessage(String message);

        void adicionarAula(Aula aula);

        void atualizarLista();

        void removerAula(Aula aula);
    }

    interface Presenter extends MvpPresenter<AulasContract.View> {

        void novaAula(String grupoId, String titulo, String conteudo);

        void editarAula(String grupoId, String id, String titulo, String conteudo);

        void removerAula(String grupoId, Aula aula);

        void monitorarAulas(String grupoId);
    }
}
