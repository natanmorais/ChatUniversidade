package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface GrupoContract {

    interface View extends MvpView {

        void showMessage(String message);

        void finish();
    }

    interface Presenter extends MvpPresenter<GrupoContract.View> {

    }
}
