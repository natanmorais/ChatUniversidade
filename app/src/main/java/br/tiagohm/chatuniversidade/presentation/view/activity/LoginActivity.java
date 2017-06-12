package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.base.BaseMvpActivity;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;
import br.tiagohm.chatuniversidade.presentation.presenter.LoginPresenter;
import br.tiagohm.chatuniversidade.presentation.view.fragment.EntrarFragment;
import br.tiagohm.chatuniversidade.presentation.view.fragment.RegistrarFragment;

public class LoginActivity extends BaseMvpActivity<LoginContract.View, LoginContract.Presenter>
        implements LoginContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mostrarTelaDeEntrar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected String getTitleString() {
        return "Login";
    }

    @NonNull
    @Override
    public LoginContract.Presenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void mostrarTelaDeEntrar() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragContainer, EntrarFragment.newInstance());
        ft.commit();
    }

    @Override
    public void mostrarTelaDeRegistrar() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragContainer, RegistrarFragment.newInstance());
        ft.commit();
    }

    @Override
    public void mostrarTelaDoUsuario() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
