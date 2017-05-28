package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.HomeContract;
import br.tiagohm.chatuniversidade.presentation.presenter.HomePresenter;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class HomeActivity extends MvpActivity<HomeContract.View, HomeContract.Presenter>
        implements HomeContract.View, FirebaseAuth.AuthStateListener {

    @Inject
    SharedPreferences preferences;

    private Usuario mUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);
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

        String email = preferences.getString("USER_EMAIL", null);
        ChatManager.getUsuarioByEmail(email)
                .subscribe(new Consumer<Usuario>() {
                    @Override
                    public void accept(Usuario usuario) throws Exception {
                        mUsuario = usuario;
                        Logger.d(mUsuario);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        t.printStackTrace();
                    }
                });
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
                Intent i = new Intent(this, ContaActivity.class);
                i.putExtra("USUARIO", mUsuario);
                startActivity(i);
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
}
