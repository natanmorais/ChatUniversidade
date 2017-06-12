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
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.base.BaseMvpActivity;
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.presentation.contract.HomeContract;
import br.tiagohm.chatuniversidade.presentation.presenter.HomePresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.CriarGrupoDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarGrupoDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class HomeActivity extends BaseMvpActivity<HomeContract.View, HomeContract.Presenter>
        implements HomeContract.View, FirebaseAuth.AuthStateListener {

    @BindView(R.id.novoGrupo)
    FloatingActionButton mCriarGrupoButton;
    @BindView(R.id.meusGrupos)
    RecyclerView mMeusGrupos;

    private List<Grupo> mGrupos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeusGrupos.setLayoutManager(new LinearLayoutManager(this));
        mMeusGrupos.setAdapter(new GruposAdapter());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected String getTitleString() {
        return "Grupos";
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGrupos.clear();
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
        if (presenter.getUsuario() == null) return false;

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
            case R.id.sair:
                presenter.deslogar();
                break;
            case R.id.verConvites:
                Intent c = new Intent(this, ConvitesActivity.class);
                startActivity(c);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        //Deslogado.
        if (firebaseAuth.getCurrentUser() == null) {
            Logger.d("Deslogou");
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            Logger.d("logou");
            presenter.carregar(firebaseAuth.getCurrentUser().getEmail());
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
                            if (dialog.mNome.length() > 0) {
                                presenter.criarGrupo(dialog.mNome.getText().toString(),
                                        (String) dialog.mInstituicao.getSelectedItem());
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
                            //O nome foi alterado.
                            if (dialog.mNome.length() > 0) {
                                presenter.editarGrupo(grupo.id, dialog.mNome.getText().toString());
                            } else {
                                showMessage("Campos inválidos ou em branco");
                                return;
                            }

                            //O Admin vai ser alterado.
                            if (dialog.mAdmin.length() > 0 &&
                                    !dialog.mAdmin.getText().toString().equals(grupo.admin.email)) {

                            }
                        } else if (opt == 2) {
                            presenter.deletarGrupo(grupo.id);
                        }
                    }
                });
    }

    @Override
    public void atualizarLista() {
        mMeusGrupos.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void adicionarGrupo(Grupo grupo) {
        mGrupos.add(grupo);
    }

    @Override
    public void removerGrupo(Grupo grupo) {
        mGrupos.remove(grupo);
    }

    @Override
    public void atualizarGrupo(Grupo grupo) {
        for (int i = 0; i < mGrupos.size(); i++) {
            if (mGrupos.get(i).id.equals(grupo.id)) {
                if (!mGrupos.get(i).admin.equals(grupo.admin)) {
                    mGrupos.remove(i);
                    mMeusGrupos.getAdapter().notifyDataSetChanged();
                    return;
                } else {
                    mGrupos.set(i, grupo);
                    mMeusGrupos.getAdapter().notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Override
    public void usuarioEncontrado() {
        mCriarGrupoButton.setVisibility(presenter.getUsuario().isUser() ?
                View.GONE : View.VISIBLE);
    }

    public class GruposAdapter extends RecyclerView.Adapter<GruposAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(getBaseContext());
            return new Holder(li.inflate(R.layout.grupo_item, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Grupo grupo = mGrupos.get(position);
            holder.mView.setTag(grupo);
            holder.mNomeDoGrupo.setText(grupo.nome);
            holder.mInstituicaoDoGrupo.setText(grupo.instituicao);
        }

        @Override
        public int getItemCount() {
            return mGrupos.size();
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
                        i.putExtra("GRUPO", (Grupo) mView.getTag());
                        startActivity(i);
                    }
                });
            }
        }
    }
}
