package br.tiagohm.chatuniversidade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.tiagohm.chatuniversidade.common.base.BaseActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.HomeActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.LoginActivity;

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
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }
}
