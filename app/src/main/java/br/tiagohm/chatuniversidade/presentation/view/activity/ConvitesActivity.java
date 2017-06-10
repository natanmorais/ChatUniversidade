package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Instituicao;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.ConviteContract;
import br.tiagohm.chatuniversidade.presentation.presenter.ConvitePresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by root on 10/06/17.
 */
public class ConvitesActivity extends MvpActivity<ConviteContract.View, ConviteContract.Presenter>
        implements ConviteContract.View {

    @BindView(R.id.convitesEnviados)
    RecyclerView mConvitesEnviados;
    @BindView(R.id.convitesRecebidos)
    RecyclerView mConvitesRecebidos;
    @Inject
    ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_convites);

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);

        mConvitesEnviados.setLayoutManager(new LinearLayoutManager(this));
        mConvitesRecebidos.setLayoutManager(new LinearLayoutManager(this));
    }
    
}
