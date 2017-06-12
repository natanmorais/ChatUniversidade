package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import javax.inject.Inject;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.common.App;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.contract.GrupoContract;
import br.tiagohm.chatuniversidade.presentation.presenter.GrupoPresenter;
import butterknife.ButterKnife;

public class GrupoActivity extends MvpActivity<GrupoContract.View, GrupoContract.Presenter>
        implements GrupoContract.View {

    @Inject
    ChatManager chatManager;

    private String mGrupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        setTitle("Grupo");

        ButterKnife.bind(this);

        App.getChatComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().hasExtra("GRUPO")) {
            mGrupo = (String) getIntent().getSerializableExtra("GRUPO");
        }

        if (mGrupo == null) {
            finish();
        }
    }

    @NonNull
    @Override
    public GrupoContract.Presenter createPresenter() {
        return new GrupoPresenter();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.grupo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.verAulas) {
            Intent i = new Intent(this, AulasActivity.class);
            i.putExtra("GRUPO", mGrupo);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
