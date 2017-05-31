package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class EditarInstituicaoDialog extends AlertDialog.Builder {

    @BindView(R.id.siglaInput)
    public EditText mSigla;
    @BindView(R.id.nomeInstituicaoInput)
    public EditText mNome;
    @BindView(R.id.enderecoInput)
    public EditText mEndereco;
    @BindView(R.id.telefoneInput)
    public EditText mTelefone;
    @BindView(R.id.emailInstituicaoInput)
    public EditText mEmail;
    @BindView(R.id.salvarButton)
    public Button mSalvarButton;
    @BindView(R.id.deletarButton)
    public Button mDeletarButton;

    private AlertDialog mDialog;

    public EditarInstituicaoDialog(Instituicao instituicao, Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_instituicao, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        mSigla.setText(instituicao.sigla);
        mNome.setText(instituicao.nome);
        mEndereco.setText(instituicao.endereco);
        mTelefone.setText(instituicao.telefone);
        mEmail.setText(instituicao.email);

        ButterKnife.bind(this, view);
    }

    public Instituicao getInstituicao() {
        return new Instituicao(
                mSigla.getText().toString(),
                mNome.getText().toString(),
                mEndereco.getText().toString(),
                mTelefone.getText().toString(),
                mEmail.getText().toString());
    }

    public Observable<Integer> exibir() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                mSalvarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(1);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mDeletarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(2);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mDialog = show();
            }
        });
    }
}
