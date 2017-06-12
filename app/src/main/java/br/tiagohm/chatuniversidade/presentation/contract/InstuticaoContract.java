package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;

public interface InstuticaoContract {

    interface View extends BaseMvpView {

        void updateList();

        void adicionarInstituicao(Instituicao instituicao);

        void removerInstituicao(Instituicao instituicao);
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void novaInstituicao(String sigla, String nome, String endereco, String telefone, String email);

        void editarInstituicao(String id, String sigla, String nome, String endereco, String telefone, String email);

        void removerInstituicao(Instituicao instituicao);

        void monitorarInstituicao();
    }
}
