package br.tiagohm.chatuniversidade.presentation.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hannesdorfmann.mosby3.mvp.MvpFragment;
import com.orhanobut.logger.Logger;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.base.BaseMvpFragment;
import br.tiagohm.chatuniversidade.presentation.contract.EntrarContract;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;
import br.tiagohm.chatuniversidade.presentation.presenter.EntrarPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EntrarFragment extends BaseMvpFragment<EntrarContract.View, EntrarContract.Presenter>
        implements EntrarContract.View {

    @BindView(R.id.emailInput)
    EditText mEmail;
    @BindView(R.id.senhaInput)
    EditText mSenha;

    public static EntrarFragment newInstance() {
        return new EntrarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrar, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @NonNull
    @Override
    public EntrarContract.Presenter createPresenter() {
        return new EntrarPresenter(
                ((LoginContract.View) getActivity()).getPresenter());
    }

    @OnClick(R.id.entrarButton)
    public void entrar() {
        Logger.d("entrar()");
        presenter.entrar(mEmail.getText().toString(),
                mSenha.getText().toString());
    }

    @OnClick(R.id.queroCriarContaButton)
    public void queroCriarConta() {
        Logger.d("queroCriarConta()");
        presenter.getLoginPresenter().mostrarTelaDeRegistrar();
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public void finish() {
    }
}
