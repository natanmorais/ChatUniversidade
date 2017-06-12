package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Conversa;
import br.tiagohm.chatuniversidade.model.entity.Grupo;

public interface GrupoContract {

    interface View extends BaseMvpView {

        void adicionarConversa(Conversa conversa);

        void removerConversa(Conversa conversa);
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void novoConvite(Grupo grupo, String email);

        void monitorarConversas(String grupoId);

        void enviarConversa(String grupoId, String mensagem);

        void sairDoGrupo(Grupo grupo);
    }
}
