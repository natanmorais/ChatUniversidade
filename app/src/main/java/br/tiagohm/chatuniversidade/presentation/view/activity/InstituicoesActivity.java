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

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.InstuticaoContract;
import br.tiagohm.chatuniversidade.presentation.presenter.InstuticoesPresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.CriarInstituicaoDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarGrupoDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarInstituicaoDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class InstituicoesActivity extends MvpActivity<InstuticaoContract.View, InstuticaoContract.Presenter>
        implements InstuticaoContract.View, ChatManager.ChatManagerListener {

    @BindView(R.id.novaInstituicao)
    FloatingActionButton mAdicionarInstituicaoButton;
    @BindView(R.id.minhasInstituicoes)
    RecyclerView mMinhasInstituicoes;
    @Inject
    ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_instituicoes);

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);

        if (chatManager.getUsuario().tipo != 1) {
            mAdicionarInstituicaoButton.setVisibility(View.GONE);
        }

        mMinhasInstituicoes.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMinhasInstituicoes.setAdapter(new InstituicaoAdapter());
    }

    @Override
    protected void onStart() {
        super.onStart();
        chatManager.add(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatManager.remove(this);
    }

    @NonNull
    @Override
    public InstuticaoContract.Presenter createPresenter() {
        return new InstuticoesPresenter();
    }

    @Override
    public void novoGrupo(Grupo grupo) {

    }

    @Override
    public void grupoRemovido(Grupo grupo) {

    }

    @Override
    public void grupoModificado(Grupo grupo) {

    }

    @Override
    public void novaInstituicao(Instituicao instituicao) {
        updateList();
    }

    @Override
    public void instituicaoModificada(Instituicao instituicao) {
        updateList();
    }

    @Override
    public void instituicaoRemovida(Instituicao instituicao) {
        updateList();
    }

    @Override
    public void updateList() {
        mMinhasInstituicoes.getAdapter().notifyDataSetChanged();
    }

    @OnClick(R.id.novaInstituicao)
    public void novaInstituicao() {
        final CriarInstituicaoDialog dialog = new CriarInstituicaoDialog(this);
        dialog.exibir()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        presenter.novaInstituicao(
                                dialog.mSigla.getText().toString(),
                                dialog.mNome.getText().toString(),
                                dialog.mEndereco.getText().toString(),
                                dialog.mTelefone.getText().toString(),
                                dialog.mEmail.getText().toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        Toast.makeText(InstituicoesActivity.this, "Erro ao criar uma instituição", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void editarInstituicao(final Instituicao instituicao) {
        final EditarInstituicaoDialog dialog = new EditarInstituicaoDialog(instituicao, this);
        dialog.exibir()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer opt) throws Exception {
                        if (opt == 1) {
                            if (dialog.mNome.length() > 0) {
                                presenter.editarInstituicao(
                                        instituicao.id,
                                        dialog.mSigla.getText().toString(),
                                        dialog.mNome.getText().toString(),
                                        dialog.mEndereco.getText().toString(),
                                        dialog.mTelefone.getText().toString(),
                                        dialog.mEmail.getText().toString());
                            } else {
                                Toast.makeText(InstituicoesActivity.this, "Campos inválidos ou em branco", Toast.LENGTH_SHORT).show();
                            }
                        } else if (opt == 2) {
                            presenter.removerInstituicao(instituicao);
                        }
                    }
                });
    }

    public class InstituicaoAdapter extends RecyclerView.Adapter<InstituicoesActivity.InstituicaoAdapter.Holder> {

        @Override
        public InstituicoesActivity.InstituicaoAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(getBaseContext());
            return new InstituicoesActivity.InstituicaoAdapter.Holder(li.inflate(R.layout.instituicao_item, parent, false));
        }

        @Override
        public void onBindViewHolder(InstituicoesActivity.InstituicaoAdapter.Holder holder, int position) {
            Instituicao instituicao = chatManager.getInstituicoes().get(position);
            holder.mView.setTag(instituicao);
            holder.mNome.setText(instituicao.nome);
            holder.mSigla.setText(instituicao.sigla);
        }

        @Override
        public int getItemCount() {
            return chatManager.getInstituicoes().size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public final View mView;
            @BindView(R.id.siglaDaInstituicao)
            public TextView mSigla;
            @BindView(R.id.nomeDaInstituicao)
            public TextView mNome;

            public Holder(final View itemView) {
                super(itemView);
                mView = itemView;

                ButterKnife.bind(this, mView);

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        editarInstituicao((Instituicao) mView.getTag());
                        return true;
                    }
                });
            }
        }
    }
}
