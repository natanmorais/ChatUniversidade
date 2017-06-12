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
import br.tiagohm.chatuniversidade.model.entity.Grupo;
import br.tiagohm.chatuniversidade.presentation.contract.GrupoContract;
import br.tiagohm.chatuniversidade.presentation.presenter.GrupoPresenter;
import br.tiagohm.chatuniversidade.presentation.view.dialog.ConfirmDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.CriarConviteDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class GrupoActivity extends MvpActivity<GrupoContract.View, GrupoContract.Presenter>
        implements GrupoContract.View {

    @BindView(R.id.conversas)
    MessagesList mConversas;
    @BindView(R.id.textoMensagem)
    MessageInput mTextoMensagem;

    private Grupo mGrupo;
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
                presenter.enviarConversa(mGrupo.id, mensagem.toString());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().hasExtra("GRUPO")) {
            mGrupo = (Grupo) getIntent().getSerializableExtra("GRUPO");
        }

        if (mGrupo == null) {
            finish();
        }

        mMessageAdapter = new MessageAdapter();
        mConversas.setAdapter(mMessageAdapter);
        presenter.monitorarConversas(mGrupo.id);
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
        menu.findItem(R.id.adicionarUsuario).setVisible(!presenter.getUsuario().isUser());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.verAulas) {
            Intent i = new Intent(this, AulasActivity.class);
            i.putExtra("GRUPO", mGrupo);
            startActivity(i);
        } else if (id == R.id.adicionarUsuario) {
            final CriarConviteDialog dialog = new CriarConviteDialog(mGrupo.nome, this);
            dialog
                    .exibir()
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean success) throws Exception {
                            if (success) {
                                String email = dialog.mEmail.getText().toString();
                                presenter.novoConvite(mGrupo, email);
                            }
                        }
                    });
        } else if (id == R.id.sairDoGrupo) {
            String mensagem = String.format("Deseja sair deste grupo?\n%s",
                    mGrupo.admin.equals(presenter.getUsuario()) ?
                            "Lembre-se que todos os dados deste grupo serão removidos!" :
                            "");

            new ConfirmDialog(this, mensagem)
                    .exibir()
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean sim) throws Exception {
                            if (sim) {
                                presenter.sairDoGrupo(mGrupo);
                            }
                        }
                    });
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
