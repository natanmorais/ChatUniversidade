package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.presentation.contract.LoginContract;
import br.tiagohm.chatuniversidade.presentation.presenter.LoginPresenter;
import br.tiagohm.chatuniversidade.presentation.view.fragment.EntrarFragment;
import br.tiagohm.chatuniversidade.presentation.view.fragment.RegistrarFragment;
import butterknife.ButterKnife;

public class LoginActivity extends MvpActivity<LoginContract.View, LoginContract.Presenter>
        implements LoginContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);

        mostrarTelaDeEntrar();
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

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
