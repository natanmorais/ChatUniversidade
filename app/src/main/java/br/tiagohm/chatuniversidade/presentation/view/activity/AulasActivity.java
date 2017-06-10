package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.chatuniversidade.presentation.contract.AulasContract;
import br.tiagohm.chatuniversidade.presentation.presenter.AulasPresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.CriarAulaDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarAulaDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.VisualizarAulaDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class AulasActivity extends MvpActivity<AulasContract.View, AulasContract.Presenter>
        implements AulasContract.View {

    private final List<Aula> mAulas = new ArrayList<>();
    @BindView(R.id.novaAula)
    FloatingActionButton mCriarGrupoButton;
    @BindView(R.id.minhasAulas)
    RecyclerView mAulasList;
    private String mGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas);
        setTitle("Aulas");

        ButterKnife.bind(this);

        mAulasList.setLayoutManager(new LinearLayoutManager(this));
        mAulasList.setAdapter(new AulasAdapter());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().hasExtra("GRUPO")) {
            mGrupo = (String) getIntent().getSerializableExtra("GRUPO");
        }

        if (mGrupo == null) {
            finish();
        }

        presenter.monitorarAulas(mGrupo);
    }

    @NonNull
    @Override
    public AulasContract.Presenter createPresenter() {
        return new AulasPresenter();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.novaAula)
    public void novaAula() {
        final CriarAulaDialog dialog = new CriarAulaDialog(this);
        dialog.exibir()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean ok) throws Exception {
                        if (ok) {
                            presenter.novaAula(mGrupo,
                                    dialog.mTitulo.getText().toString(),
                                    dialog.mConteudo.getText().toString());
                        }
                    }
                });
    }

    @Override
    public void adicionarAula(Aula aula) {
        mAulas.add(aula);
    }

    @Override
    public void removerAula(Aula aula) {
        mAulas.remove(aula);
    }

    @Override
    public void atualizarLista() {
        mAulasList.getAdapter().notifyDataSetChanged();
    }

    public class AulasAdapter extends RecyclerView.Adapter<AulasAdapter.Holder> {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(getBaseContext());
            return new Holder(li.inflate(R.layout.aula_item, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Aula aula = mAulas.get(position);
            holder.mView.setTag(aula);
            holder.mTitulo.setText(aula.titulo);
            holder.mData.setText(dateFormat.format(new Date(aula.data)));
        }

        @Override
        public int getItemCount() {
            return mAulas.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public final View mView;
            @BindView(R.id.tituloDaAula)
            public TextView mTitulo;
            @BindView(R.id.dataDaAula)
            public TextView mData;

            public Holder(final View itemView) {
                super(itemView);
                mView = itemView;

                ButterKnife.bind(this, mView);

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Aula aula = (Aula) v.getTag();
                        final EditarAulaDialog dialog =
                                new EditarAulaDialog(aula, AulasActivity.this);
                        dialog.exibir().subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer flag) throws Exception {
                                if (flag == 1) {
                                    presenter.editarAula(mGrupo, aula.id,
                                            dialog.mTitulo.getText().toString(),
                                            dialog.mConteudo.getText().toString());
                                } else if (flag == 2) {
                                    presenter.removerAula(mGrupo, aula);
                                }
                            }
                        });
                        return true;
                    }
                });

                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Aula aula = (Aula) v.getTag();
                        new VisualizarAulaDialog(aula, AulasActivity.this).exibir();
                    }
                });
            }
        }
    }
}
