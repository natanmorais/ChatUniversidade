package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.presentation.contract.ConviteContract;
import br.tiagohm.chatuniversidade.presentation.presenter.ConvitePresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConvitesActivity extends MvpActivity<ConviteContract.View, ConviteContract.Presenter>
        implements ConviteContract.View {

    @BindView(R.id.convitesEnviados)
    RecyclerView mConvitesEnviados;
    @BindView(R.id.convitesRecebidos)
    RecyclerView mConvitesRecebidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_convites);

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);

        mConvitesEnviados.setLayoutManager(new LinearLayoutManager(this));
        mConvitesRecebidos.setLayoutManager(new LinearLayoutManager(this));
    }

    @NonNull
    @Override
    public ConviteContract.Presenter createPresenter() {
        return new ConvitePresenter();
    }

    @Override
    public void atualizarListaRemetente() {

    }

    @Override
    public void atualizarListaDestinatario() {

    }
}
