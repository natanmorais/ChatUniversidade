package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.orhanobut.logger.Logger;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.presentation.contract.HomeContract;
import br.tiagohm.chatuniversidade.presentation.presenter.HomePresenter;
import butterknife.ButterKnife;

public class HomeActivity extends MvpActivity<HomeContract.View, HomeContract.Presenter>
        implements HomeContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            Usuario u = (Usuario) getIntent().getSerializableExtra("USUARIO");
            Logger.d(u);
        }
    }

    @NonNull
    @Override
    public HomeContract.Presenter createPresenter() {
        return new HomePresenter();
    }
}
