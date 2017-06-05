package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.chatuniversidade.model.entity.Grupo;

public interface AulasContract {

    interface View extends MvpView {

        void showMessage(String message);

        void finish();

        void mostrarAulas(List<Aula> aulas);

        void carregarAulas();
    }

    interface Presenter extends MvpPresenter<AulasContract.View> {

        void novaAula(String grupoId, String titulo, String conteudo);

        void editarAula(String grupoId, String id, String titulo, String conteudo);

        void removerAula(String grupoId, Aula aula);

        void carregarAulas(String grupoId);
    }
}
