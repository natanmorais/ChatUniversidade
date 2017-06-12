package br.tiagohm.chatuniversidade.common.base;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

import br.tiagohm.chatuniversidade.model.entity.Usuario;

public interface BaseMvpPresenter<V extends BaseMvpView> extends MvpPresenter<V> {

    Usuario getUsuario();
}
