package br.tiagohm.chatuniversidade.common.base;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface BaseMvpView extends MvpView {

    void showProgess(boolean show);

    void showMessage(String message);

    void finish();
}
