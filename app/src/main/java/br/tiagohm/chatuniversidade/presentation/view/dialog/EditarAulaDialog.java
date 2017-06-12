package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class EditarAulaDialog extends AlertDialog.Builder {

    @BindView(R.id.tituloInput)
    public EditText mTitulo;
    @BindView(R.id.conteudoInput)
    public EditText mConteudo;
    @BindView(R.id.editarButton)
    public Button mSalvarButton;
    @BindView(R.id.deletarButton)
    public Button mDeletarButton;

    private AlertDialog mDialog;

    public EditarAulaDialog(Aula aula, Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_aula, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        mTitulo.setText(aula.titulo);
        mConteudo.setText(aula.conteudo);
    }

    public Aula getAula() {
        return new Aula(
                mTitulo.getText().toString(),
                mConteudo.getText().toString());
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
