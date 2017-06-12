package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class EditarGrupoDialog extends AlertDialog.Builder {

    @BindView(R.id.instituicaoInput)
    public EditText mInstituicao;
    @BindView(R.id.nomeInput)
    public EditText mNome;
    @BindView(R.id.administradorInput)
    public EditText mAdmin;
    @BindView(R.id.editarButton)
    public Button mEditarButton;
    @BindView(R.id.deletarButton)
    public Button mDeletarButton;

    private AlertDialog mDialog;
    private Grupo mGrupo;

    public EditarGrupoDialog(Grupo grupo, Context context) {
        super(context);

        mGrupo = grupo;

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_grupo, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        mNome.setText(mGrupo.nome);
        mInstituicao.setText(mGrupo.instituicao);
        mAdmin.setText(mGrupo.admin.email);
    }

    public Observable<Integer> exibir() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                mEditarButton.setOnClickListener(new View.OnClickListener() {
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
