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

public class CriarInstituicaoDialog extends AlertDialog.Builder {

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
    @BindView(R.id.registrarButton)
    public Button mRegistrarButton;

    private AlertDialog mDialog;

    public CriarInstituicaoDialog(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_criar_instituicao, null, false);
        setView(view);

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

    public Observable<Boolean> exibir() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mRegistrarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(true);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mDialog = show();
            }
        });
    }
}
