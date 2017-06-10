package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Instituicao;

public interface RegistrarContract {

    interface View extends MvpView {

        void adicionarInstituicao(Instituicao instituicao);

        void removerInstituicao(Instituicao instituicao);

        void atualizaListaDeInstituicoes();
    }

    interface Presenter extends MvpPresenter<RegistrarContract.View> {

        LoginContract.Presenter getLoginPresenter();

        void registrar(String instituicao, String nome, int tipo, String matricula,
                       String email, String senha);

        void obterInstituicoes();
    }
}
