package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import java.util.List;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.HomeContract;
import br.tiagohm.chatuniversidade.presentation.presenter.HomePresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.CriarGrupoDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarGrupoDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class HomeActivity extends MvpActivity<HomeContract.View, HomeContract.Presenter>
        implements HomeContract.View, FirebaseAuth.AuthStateListener, ChatManager.ChatManagerListener {

    @Inject
    ChatManager chatManager;
    @BindView(R.id.novoGrupo)
    FloatingActionButton mCriarGrupoButton;
    @BindView(R.id.meusGrupos)
    RecyclerView mMeusGrupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Meus Grupos");

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);

        mMeusGrupos.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
        chatManager.add(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        chatManager.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMeusGrupos.setAdapter(new GruposAdapter());
    }

    @NonNull
    @Override
    public HomeContract.Presenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        switch (id) {
            case R.id.verMinhaConta:
                Intent a = new Intent(this, ContaActivity.class);
                startActivity(a);
                break;
            case R.id.verInstituicoes:
                Intent b = new Intent(this, InstituicoesActivity.class);
                startActivity(b);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        //Deslogado.
        if (firebaseAuth.getCurrentUser() == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @OnClick(R.id.novoGrupo)
    public void novoGrupo() {
        final CriarGrupoDialog dialog = new CriarGrupoDialog(this);
        dialog.exibir()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (success) {
                            if (dialog.mNome.length() > 0 &&
                                    dialog.mInstituicao.length() > 0) {
                                presenter.criarGrupo(dialog.mNome.getText().toString(),
                                        dialog.mInstituicao.getText().toString());
                            } else {
                                Toast.makeText(HomeActivity.this, "Campos inválidos ou em branco", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void editarGrupo(final Grupo grupo) {
        final EditarGrupoDialog dialog = new EditarGrupoDialog(grupo, this);
        dialog.exibir()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer opt) throws Exception {
                        if (opt == 1) {
                            if (dialog.mNome.length() > 0) {
                                presenter.editarGrupo(grupo, dialog.mNome.getText().toString());
                            } else {
                                Toast.makeText(HomeActivity.this, "Campos inválidos ou em branco", Toast.LENGTH_SHORT).show();
                            }
                        } else if (opt == 2) {
                            presenter.deletarGrupo(grupo);
                        }
                    }
                });
    }

    public void grupoModificado(Grupo g) {
        mMeusGrupos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void novaInstituicao(Instituicao instituicao) {

    }

    @Override
    public void instituicaoModificada(Instituicao instituicao) {

    }

    @Override
    public void instituicaoRemovida(Instituicao instituicao) {

    }

    @Override
    public void showGrupos(List<Grupo> grupos) {
        //mMeusGrupos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void novoGrupo(Grupo grupo) {
        mMeusGrupos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void grupoRemovido(Grupo grupo) {
        mMeusGrupos.getAdapter().notifyDataSetChanged();
    }

    public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(getBaseContext());
            return new Holder(li.inflate(R.layout.grupo_item, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Grupo grupo = chatManager.getGrupos().get(position);
            holder.mView.setTag(grupo);
            holder.mNomeDoGrupo.setText(grupo.nome);
            holder.mInstituicaoDoGrupo.setText(grupo.instituicao);
        }

        @Override
        public int getItemCount() {
            return chatManager.getGrupos().size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public final View mView;
            @BindView(R.id.nomeDoGrupo)
            public TextView mNomeDoGrupo;
            @BindView(R.id.instituicaoDoGrupo)
            public TextView mInstituicaoDoGrupo;

            public Holder(final View itemView) {
                super(itemView);
                mView = itemView;

                ButterKnife.bind(this, mView);

                mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        editarGrupo((Grupo) mView.getTag());
                        return true;
                    }
                });

                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HomeActivity.this, GrupoActivity.class);
                        i.putExtra("GRUPO", ((Grupo) mView.getTag()).id);
                        startActivity(i);
                    }
                });
            }
        }
    }
}
