package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;

public interface HomeContract {

    interface View extends MvpView {

        void adicionarGrupo(Grupo grupo);

        void removerGrupo(Grupo grupo);

        void atualizarGrupo(Grupo grupo);

        void showMessage(String message);

        void atualizarLista();
    }

    interface Presenter extends MvpPresenter<View> {

        void carregar(String email);

        void deslogar();

        void criarGrupo(String nome, String instituicao);

        void editarGrupo(String grupoId, String nomeNovo);

        void deletarGrupo(String grupoId);

        void monitorarMeusGrupos();

        ChatManager getChatManager();
    }
}
