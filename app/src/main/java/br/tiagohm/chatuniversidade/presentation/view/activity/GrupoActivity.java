package br.tiagohm.chatuniversidade.presentation.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Conversa;
import br.tiagohm.chatuniversidade.presentation.contract.GrupoContract;
import br.tiagohm.chatuniversidade.presentation.presenter.GrupoPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GrupoActivity extends MvpActivity<GrupoContract.View, GrupoContract.Presenter>
        implements GrupoContract.View {

    @BindView(R.id.conversas)
    MessagesList mConversas;
    @BindView(R.id.textoMensagem)
    MessageInput mTextoMensagem;

    private String mGrupoId;
    private MessageAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        setTitle("Grupo");

        ButterKnife.bind(this);

        mTextoMensagem.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence mensagem) {
                presenter.enviarConversa(mGrupoId, mensagem.toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().hasExtra("GRUPO")) {
            mGrupoId = (String) getIntent().getSerializableExtra("GRUPO");
        }

        if (mGrupoId == null) {
            finish();
        }

        mMessageAdapter = new MessageAdapter();
        mConversas.setAdapter(mMessageAdapter);
        presenter.monitorarConversas(mGrupoId);
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
            i.putExtra("GRUPO", mGrupoId);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void adicionarConversa(Conversa conversa) {
        mMessageAdapter.addToStart(conversa, true);
    }

    @Override
    public void removerConversa(Conversa conversa) {
        mMessageAdapter.delete(conversa);
    }

    private class MessageAdapter extends MessagesListAdapter<Conversa> {

        public MessageAdapter() {
            super(presenter.getUsuario().getId(), null);
        }
    }
}
