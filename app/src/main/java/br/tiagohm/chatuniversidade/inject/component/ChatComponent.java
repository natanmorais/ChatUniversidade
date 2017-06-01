package br.tiagohm.chatuniversidade.inject.component;

import javax.inject.Singleton;

import br.tiagohm.chatuniversidade.inject.module.ChatModule;
import br.tiagohm.chatuniversidade.presentation.presenter.AulaPresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.ContaPresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.HomePresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.InstuticoesPresenter;
import br.tiagohm.chatuniversidade.presentation.view.activity.ContaActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.HomeActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.InstituicoesActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.LoginActivity;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarGrupoDialog;
import dagger.Component;

@Singleton
@Component(modules = {ChatModule.class})
public interface ChatComponent {

    void inject(ContaActivity activity);

    void inject(HomeActivity activity);

    void inject(LoginActivity activity);

    void inject(ContaPresenter presenter);

    void inject(HomePresenter presenter);

    void inject(EditarGrupoDialog dialog);

    void inject(InstituicoesActivity activity);

    void inject(InstuticoesPresenter presenter);

    void inject(AulaPresenter presenter);
}
