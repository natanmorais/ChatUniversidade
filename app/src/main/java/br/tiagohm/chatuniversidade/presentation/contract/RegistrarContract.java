package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;

public interface RegistrarContract {

    interface View extends BaseMvpView {

        void adicionarInstituicao(Instituicao instituicao);

        void removerInstituicao(Instituicao instituicao);

        void atualizaListaDeInstituicoes();
    }

    interface Presenter extends BaseMvpPresenter<View> {

        LoginContract.Presenter getLoginPresenter();

        void registrar(String instituicao, String nome, int tipo, String matricula,
                       String email, String senha);

        void obterInstituicoes();
    }
}
