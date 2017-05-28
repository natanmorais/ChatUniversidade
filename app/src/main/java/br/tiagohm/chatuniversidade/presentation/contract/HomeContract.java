package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface HomeContract {

    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<View> {

        void deslogar();
    }
}
