package br.tiagohm.chatuniversidade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.tiagohm.chatuniversidade.common.base.BaseActivity;
import br.tiagohm.chatuniversidade.model.entity.Usuario;
import br.tiagohm.chatuniversidade.model.repository.ChatManager;
import br.tiagohm.chatuniversidade.presentation.view.activity.HomeActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.LoginActivity;
import io.reactivex.functions.Consumer;

public class EmptyActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //O Usuário está deslogado.
        if (user == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        //O usuário está logado.
        else {
            ChatManager.getUsuarioByEmail(user.getEmail())
                    .subscribe(new Consumer<Usuario>() {
                        @Override
                        public void accept(Usuario usuario) throws Exception {
                            Intent i = new Intent(EmptyActivity.this, HomeActivity.class);
                            i.putExtra("USUARIO", usuario);
                            EmptyActivity.this.startActivity(i);
                            EmptyActivity.this.finish();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(EmptyActivity.this, "Um erro inesperado aconteceu", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
