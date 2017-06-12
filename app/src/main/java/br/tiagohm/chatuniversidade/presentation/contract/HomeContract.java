package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Grupo;

public interface HomeContract {

    interface View extends BaseMvpView {

        void adicionarGrupo(Grupo grupo);

        void removerGrupo(Grupo grupo);

        void atualizarGrupo(Grupo grupo);

        void atualizarLista();

        void usuarioEncontrado();
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void carregar(String email);

        void deslogar();

        void criarGrupo(String nome, String instituicao);

        void editarGrupo(String grupoId, String nomeNovo);

        void deletarGrupo(String grupoId);

        void monitorarMeusGrupos();
    }
}
