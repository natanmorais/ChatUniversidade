package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.tiagohm.chatuniversidade.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CriarConviteDialog extends AlertDialog.Builder {

    @BindView(R.id.nomeGrupo)
    public EditText mNomeGrupo;
    @BindView(R.id.emailInput)
    public EditText mEmail;
    @BindView(R.id.criarButton)
    public Button mCriarButton;

    private AlertDialog mDialog;

    public CriarConviteDialog(String nomeDoGrupo, Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_criar_convite, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        mNomeGrupo.setText(nomeDoGrupo);
    }

    public Observable<Boolean> exibir() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mCriarButton.setOnClickListener(new View.OnClickListener() {
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
