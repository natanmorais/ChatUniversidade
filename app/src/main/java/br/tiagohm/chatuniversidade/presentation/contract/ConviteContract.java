package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Convite;

/**
 * Created by root on 09/06/17.
 */
public interface ConviteContract {

    interface View extends MvpView {

        void updateList();
    }

    interface Presenter extends MvpPresenter<ConviteContract.View> {

        void novoConvite(String nomeGrupo, String email);

        void aceitarConvite(Convite convite);

        void removerConvite(Convite convite);
    }
}
