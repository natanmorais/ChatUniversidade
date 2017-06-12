package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class CriarGrupoDialog extends AlertDialog.Builder {

    private final List<Instituicao> mInstituicoes = new ArrayList<>();

    @BindView(R.id.instituicaoInput)
    public Spinner mInstituicao;
    @BindView(R.id.nomeInput)
    public EditText mNome;
    @BindView(R.id.criarButton)
    public Button mCriarButton;
    @Inject
    ChatManager chatManager;
    private AlertDialog mDialog;

    public CriarGrupoDialog(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_criar_grupo, null, false);
        setView(view);

        App.getChatComponent().inject(this);

        ButterKnife.bind(this, view);

        mInstituicao.setAdapter(new InstituicaoSpinnerAdapter());

        chatManager.monitorarInstituicoes()
                .subscribe(new Consumer<Pair<Integer, Instituicao>>() {
                    @Override
                    public void accept(Pair<Integer, Instituicao> i) throws Exception {
                        if (i.first == 0) {
                            mInstituicoes.add(i.second);
                            ((BaseAdapter) mInstituicao.getAdapter()).notifyDataSetChanged();
                        }
                    }
                });
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
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            ((CheckedTextView) convertView).setText((String) getItem(position));

            return convertView;
        }
    }
}
