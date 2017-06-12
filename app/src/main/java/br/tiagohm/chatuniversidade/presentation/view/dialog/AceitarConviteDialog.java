package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by root on 09/06/17.
 */
public class AceitarConviteDialog extends AlertDialog.Builder{

    @BindView(R.id.nomeGrupo)
    public EditText mNomeGrupo;
    @BindView(R.id.remetenteConvite)
    public EditText mRemetenteConvite;
    @BindView(R.id.aceitarButton)
    public Button mAceitarButton;
    @BindView(R.id.recusarButton)
    public Button mRecusarButton;
    @Inject
    ChatManager chatManager;

    private AlertDialog mDialog;
    private Convite mConvite;

    public AceitarConviteDialog(Convite convite, Context context) {
        super(context);

        mConvite = convite;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_accept_convite, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        App.getChatComponent().inject(this);

        mNomeGrupo.setText(mConvite.nomeDoGrupo);
        mRemetenteConvite.setText(mConvite.remetente);
    }

    public Observable<Integer> exibir() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                mAceitarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(1);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mRecusarButton.setOnClickListener(new View.OnClickListener() {
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
