package br.tiagohm.chatuniversidade.presentation.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hannesdorfmann.mosby3.mvp.MvpFragment;
import com.orhanobut.logger.Logger;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;
import br.tiagohm.chatuniversidade.presentation.contract.RegistrarContract;
import br.tiagohm.chatuniversidade.presentation.presenter.RegistrarPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrarFragment extends MvpFragment<RegistrarContract.View, RegistrarContract.Presenter>
        implements RegistrarContract.View {

    @BindView(R.id.emailInput)
    EditText mEmail;
    @BindView(R.id.senhaInput)
    EditText mSenha;
    @BindView(R.id.instituicaoInput)
    EditText mInstituicao;
    @BindView(R.id.nomeInput)
    EditText mNome;
    @BindView(R.id.matriculaInput)
    EditText mMatricula;
    @BindView(R.id.souProfessorCb)
    CheckBox mSouProfessor;

    public static RegistrarFragment newInstance() {
        return new RegistrarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registrar, container, false);
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
    public RegistrarContract.Presenter createPresenter() {
        return new RegistrarPresenter(
                ((LoginContract.View) getActivity()).getPresenter());
    }

    @OnClick(R.id.registrarButton)
    public void registrar() {
        Logger.d("registrar()");
        presenter.registrar(mInstituicao.getText().toString(),
                mNome.getText().toString(),
                mSouProfessor.isChecked() ? 1 : 0,
                mMatricula.getText().toString(),
                mEmail.getText().toString(),
                mSenha.getText().toString());
    }

    @OnClick(R.id.queroLogarButton)
    public void queroLogar() {
        Logger.d("queroLogar()");
        presenter.getLoginPresenter().mostrarTelaDeEntrar();
    }
}
