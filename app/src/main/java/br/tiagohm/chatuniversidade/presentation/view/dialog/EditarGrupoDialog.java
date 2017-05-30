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
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class EditarGrupoDialog extends AlertDialog.Builder {

    @BindView(R.id.instituicaoEditable)
    public EditText mInstituicao;
    @BindView(R.id.nomeEditable)
    public EditText mNome;
    @BindView(R.id.editarButton)
    public Button mEditarButton;
    @Inject
    ChatManager chatManager;

    private AlertDialog mDialog;
    private Grupo mGrupo;

    public EditarGrupoDialog(Grupo grupo, Context context) {
        super(context);

        mGrupo = grupo;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_grupo, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        App.getChatComponent().inject(this);

        mNome.setText(mGrupo.nome);
        mInstituicao.setText(mGrupo.instituicao);
    }

    public Observable<Boolean> exibir() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mEditarButton.setOnClickListener(new View.OnClickListener() {
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
