package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.model.entity.Grupo;

public interface ConviteContract {

    interface View extends MvpView {

        void adicionarConviteRecebido(Convite convite);

        void removerConviteRecebido(Convite convite);

        void adicionarConviteEnviado(Convite convite);

        void removerConviteEnviado(Convite convite);

        void atualizarListaRecebidos();

        void atualizarListaEnviados();
    }

    interface Presenter extends MvpPresenter<ConviteContract.View> {

        void aceitarConvite(Convite convite);

        void revogarConvite(Convite convite);

        void recusarConvite(Convite convite);

        void obterOsConvites();
    }
}
