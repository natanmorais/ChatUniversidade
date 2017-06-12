package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.base.BaseMvpActivity;
import br.tiagohm.chatuniversidade.model.entity.Convite;
import br.tiagohm.chatuniversidade.presentation.contract.ConviteContract;
import br.tiagohm.chatuniversidade.presentation.presenter.ConvitePresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.AceitarConviteDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.RevogarConviteDialog;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

public class ConvitesActivity extends BaseMvpActivity<ConviteContract.View, ConviteContract.Presenter>
        implements ConviteContract.View {

    @BindView(R.id.convitesEnviados)
    RecyclerView mConvitesEnviados;
    @BindView(R.id.convitesRecebidos)
    RecyclerView mConvitesRecebidos;

    private List<Convite> mConvitesRecebidosList = new ArrayList<>();
    private List<Convite> mConvitesEnviadosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConvitesEnviados.setLayoutManager(new LinearLayoutManager(this));
        mConvitesRecebidos.setLayoutManager(new LinearLayoutManager(this));
        mConvitesEnviados.setAdapter(new ConvitesEnviadosAdapter());
        mConvitesRecebidos.setAdapter(new ConvitesRecebidosAdapter());

        presenter.obterOsConvites();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_convites;
    }

    @Override
    protected String getTitleString() {
        return "Convites";
    }

    @NonNull
    @Override
    public ConviteContract.Presenter createPresenter() {
        return new ConvitePresenter();
    }

    @Override
    public void adicionarConviteRecebido(Convite convite) {
        Logger.d("adicionarConviteRecebido");
        mConvitesRecebidosList.add(convite);
    }

    @Override
    public void removerConviteRecebido(Convite convite) {
        Logger.d("removerConviteRecebido");
        mConvitesRecebidosList.remove(convite);
    }

    @Override
    public void adicionarConviteEnviado(Convite convite) {
        Logger.d("adicionarConviteEnviado");
        mConvitesEnviadosList.add(convite);
    }

    @Override
    public void removerConviteEnviado(Convite convite) {
        Logger.d("removerConviteEnviado");
        mConvitesEnviadosList.remove(convite);
    }

    @Override
    public void atualizarListaRecebidos() {
        Logger.d("atualizarListaRecebidos");
        (mConvitesRecebidos.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void atualizarListaEnviados() {
        Logger.d("atualizarListaEnviados");
        (mConvitesEnviados.getAdapter()).notifyDataSetChanged();
    }

    public class ConvitesEnviadosAdapter extends RecyclerView.Adapter<ConvitesEnviadosAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(ConvitesActivity.this).inflate(
                    android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.itemView.setTag(mConvitesEnviadosList.get(position));
            ((TextView) holder.itemView).setText(mConvitesEnviadosList.get(position).nomeDoGrupo);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return mConvitesEnviadosList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public Holder(View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Convite convite = (Convite) v.getTag();
                        RevogarConviteDialog dialog = new RevogarConviteDialog(convite, v.getContext());
                        dialog.exibir()
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer opt) throws Exception {
                                        //Revogar
                                        if (opt == 1) {
                                            presenter.revogarConvite(convite);
                                        }
                                    }
                                });
                    }
                });
            }
        }
    }

    private class ConvitesRecebidosAdapter extends RecyclerView.Adapter<ConvitesRecebidosAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(ConvitesActivity.this).inflate(
                    android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.itemView.setTag(mConvitesRecebidosList.get(position));
            ((TextView) holder.itemView).setText(mConvitesRecebidosList.get(position).nomeDoGrupo);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return mConvitesRecebidosList.size();
        }

        public class Holder extends RecyclerView.ViewHolder {

            public Holder(final View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Convite convite = (Convite) v.getTag();
                        AceitarConviteDialog dialog = new AceitarConviteDialog(convite, v.getContext());
                        dialog.exibir()
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer opt) throws Exception {
                                        //Aceitar
                                        if (opt == 1) {
                                            presenter.aceitarConvite(convite);
                                        }
                                        //Recusar
                                        else if (opt == 2) {
                                            presenter.recusarConvite(convite);
                                        }
                                    }
                                });
                    }
                });
            }
        }
    }
}
