package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.ContaContract;
import br.tiagohm.chatuniversidade.presentation.presenter.ContaPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContaActivity extends MvpActivity<ContaContract.View, ContaContract.Presenter>
        implements ContaContract.View {

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
    @Inject
    ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conta);

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Usuario usuario = chatManager.getUsuario();
        if (usuario != null) {
            mEmail.setText(usuario.email);
            mInstituicao.setText(usuario.instituicao);
            mNome.setText(usuario.nome);
            mMatricula.setText(usuario.matricula);
            mSouProfessor.setChecked(usuario.tipo == 1);
        }
    }

    @NonNull
    @Override
    public ContaContract.Presenter createPresenter() {
        return new ContaPresenter();
    }

    @OnClick(R.id.salvarButton)
    public void salvarConta() {
        presenter.salvarConta(
                mSenha.getText().toString(),
                mInstituicao.getText().toString(),
                mNome.getText().toString(),
                mMatricula.getText().toString());
    }

    @OnClick(R.id.deletarButton)
    public void deletarConta() {
        presenter.deletarConta();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
