package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.tiagohm.chatuniversidade.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ConfirmDeleteAccountDialog extends AlertDialog.Builder {

    @BindView(R.id.naoButton)
    public Button mNaoButton;
    @BindView(R.id.simButton)
    public Button mSimButton;
    @BindView(R.id.textoDaConfirmacao)
    public TextView mTextoDaConfirmacao;
    @BindView(R.id.senhaDeConfirmacao)
    public EditText mSenhaDeConfirmacao;

    private AlertDialog mDialog;

    public ConfirmDeleteAccountDialog(Context context, String mensagem) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete_account, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        mTextoDaConfirmacao.setText(mensagem);
    }

    public Observable<Boolean> exibir() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mNaoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(false);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mSimButton.setOnClickListener(new View.OnClickListener() {
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
