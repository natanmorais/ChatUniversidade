package br.tiagohm.chatuniversidade.presentation.contract;

import com.hannesdorfmann.mosby3.mvp.MvpPresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import br.tiagohm.chatuniversidade.model.entity.Usuario;

public interface ContaContract {

    interface View extends MvpView {

        void showMessage(String message);

        void finish();
    }

    interface Presenter extends MvpPresenter<ContaContract.View> {

        void salvarConta(String senha, String instituicao, String nome, String matricula);

        void deletarConta();
    }
}
