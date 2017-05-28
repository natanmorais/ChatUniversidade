package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.SharedPreferences;
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
import io.reactivex.functions.Consumer;

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
    SharedPreferences preferences;

    private Usuario mUsuario;

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

        String email = preferences.getString("USER_EMAIL", null);
        ChatManager.getUsuarioByEmail(email)
                .subscribe(new Consumer<Usuario>() {
                    @Override
                    public void accept(Usuario usuario) throws Exception {
                        mUsuario = usuario;
                        mEmail.setText(mUsuario.email);
                        mInstituicao.setText(mUsuario.instituicao);
                        mNome.setText(mUsuario.nome);
                        mMatricula.setText(mUsuario.matricula);
                        mSouProfessor.setChecked(mUsuario.tipo == 1);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(ContaActivity.this, "Um erro inesperado ocorreu!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @NonNull
    @Override
    public ContaContract.Presenter createPresenter() {
        return new ContaPresenter();
    }

    @OnClick(R.id.salvarButton)
    public void salvarConta() {
        if (mUsuario == null) return;
        mUsuario.nome = mNome.getText().toString();
        mUsuario.instituicao = mInstituicao.getText().toString();
        mUsuario.matricula = mMatricula.getText().toString();
        presenter.salvarConta(
                mSenha.getText().toString(),
                mUsuario);
    }

    @OnClick(R.id.deletarButton)
    public void deletarConta() {
        if (mUsuario == null) return;
        presenter.deletarConta();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
