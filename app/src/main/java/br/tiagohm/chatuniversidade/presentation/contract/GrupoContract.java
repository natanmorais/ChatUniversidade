package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Conversa;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.entity.Usuario;

public interface GrupoContract {

    interface View extends MvpView {

        void showMessage(String message);

        void finish();

        void adicionarConversa(Conversa conversa);

        void removerConversa(Conversa conversa);
    }

    interface Presenter extends MvpPresenter<GrupoContract.View> {

        void novoConvite(Grupo grupo, String email);

        void monitorarConversas(String grupoId);

        Usuario getUsuario();

        void enviarConversa(String grupoId, String mensagem);

        void sairDoGrupo(Grupo grupo);
    }
}
