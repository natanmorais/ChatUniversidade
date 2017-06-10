package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.model.entity.Grupo;

public interface ConviteContract {

    interface View extends MvpView {

        void atualizarListaRemetente();

        void atualizarListaDestinatario();
    }

    interface Presenter extends MvpPresenter<ConviteContract.View> {

        void novoConvite(Grupo grupo, String email);

        void aceitarConvite(Convite convite);

        void revogarConvite(Convite convite);

        void recusarConvite(Convite convite);
    }
}
