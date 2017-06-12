package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.base.BaseMvpActivity;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.presentation.contract.ContaContract;
import br.tiagohm.chatuniversidade.presentation.presenter.ContaPresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.ConfirmDeleteAccountDialog;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class ContaActivity extends BaseMvpActivity<ContaContract.View, ContaContract.Presenter>
        implements ContaContract.View {

    @BindView(R.id.emailInput)
    EditText mEmail;
    @BindView(R.id.senhaInput)
    EditText mSenha;
    @BindView(R.id.instituicaoInput)
    Spinner mInstituicao;
    @BindView(R.id.nomeInput)
    EditText mNome;
    @BindView(R.id.matriculaInput)
    EditText mMatricula;
    @BindView(R.id.souProfessorCb)
    CheckBox mSouProfessor;

    private List<Instituicao> mInstituicoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_conta;
    }

    @Override
    protected String getTitleString() {
        return "Minha Conta";
    }

    @Override
    protected void onResume() {
        super.onResume();

        Usuario usuario = presenter.getUsuario();
        if (usuario != null) {
            mEmail.setText(usuario.email);
            mNome.setText(usuario.nome);
            mMatricula.setText(usuario.matricula);
            mSouProfessor.setChecked(usuario.tipo == 1);
            mInstituicao.setAdapter(new InstituicaoSpinnerAdapter());
            presenter.obterInstituicoes();
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
                (String) mInstituicao.getSelectedItem(),
                mNome.getText().toString(),
                mMatricula.getText().toString());
    }

    @OnClick(R.id.deletarButton)
    public void deletarConta() {
        final ConfirmDeleteAccountDialog dialog =
                new ConfirmDeleteAccountDialog(this, "Você deseja remover sua conta?");
        dialog.exibir()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean sim) throws Exception {
                        if (sim) {
                            presenter.deletarConta(dialog.mSenhaDeConfirmacao.getText().toString());
                        }
                    }
                });
    }

    @Override
    public void adicionarInstituicao(Instituicao instituicao) {
        if (instituicao.nome.equals(presenter.getUsuario().instituicao)) {
            mInstituicao.setSelection(mInstituicoes.size());
        }
        mInstituicoes.add(instituicao);
    }

    @Override
    public void atualizaListaDeInstituicoes() {
        ((BaseAdapter) mInstituicao.getAdapter()).notifyDataSetChanged();
    }

    private class InstituicaoSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mInstituicoes.size();
        }

        @Override
        public Object getItem(int position) {
            return mInstituicoes.get(position).nome;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ContaActivity.this)
                        .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            ((CheckedTextView) convertView).setText((String) getItem(position));

            return convertView;
        }
    }
}
