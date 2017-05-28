package br.tiagohm.chatuniversidade.inject.component;

import javax.inject.Singleton;

import br.tiagohm.chatuniversidade.inject.module.ChatModule;
import br.tiagohm.chatuniversidade.presentation.view.activity.ContaActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.HomeActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.LoginActivity;
import dagger.Component;

@Singleton
@Component(modules = {ChatModule.class})
public interface ChatComponent {

    void inject(ContaActivity activity);

    void inject(HomeActivity activity);

    void inject(LoginActivity activity);
}
