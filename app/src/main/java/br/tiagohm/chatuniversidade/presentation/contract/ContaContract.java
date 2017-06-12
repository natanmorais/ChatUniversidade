package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;

public interface ContaContract {

    interface View extends BaseMvpView {

        void adicionarInstituicao(Instituicao instituicao);

        void atualizaListaDeInstituicoes();
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void salvarConta(String senha, String instituicao, String nome, String matricula);

        void deletarConta(String password);

        void obterInstituicoes();
    }
}
