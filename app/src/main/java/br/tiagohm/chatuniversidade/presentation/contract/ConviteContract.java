package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Convite;

public interface ConviteContract {

    interface View extends BaseMvpView {

        void adicionarConviteRecebido(Convite convite);

        void removerConviteRecebido(Convite convite);

        void adicionarConviteEnviado(Convite convite);

        void removerConviteEnviado(Convite convite);

        void atualizarListaRecebidos();

        void atualizarListaEnviados();
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void aceitarConvite(Convite convite);

        void revogarConvite(Convite convite);

        void recusarConvite(Convite convite);

        void obterOsConvites();
    }
}
