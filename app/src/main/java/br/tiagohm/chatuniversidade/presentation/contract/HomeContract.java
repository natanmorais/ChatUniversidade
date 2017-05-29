package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

import br.tiagohm.chatuniversidade.model.entity.Grupo;

public interface HomeContract {

    interface View extends MvpView {

        void showGrupos(List<Grupo> grupos);
    }

    interface Presenter extends MvpPresenter<View> {

        void deslogar();

        void criarGrupo(String nome, String instituicao);
    }
}
