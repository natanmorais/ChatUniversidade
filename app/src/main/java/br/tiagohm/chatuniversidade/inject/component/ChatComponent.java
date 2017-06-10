package br.tiagohm.chatuniversidade.inject.component;

import javax.inject.Singleton;

import br.tiagohm.chatuniversidade.inject.module.ChatModule;
import br.tiagohm.chatuniversidade.presentation.presenter.AulasPresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.ContaPresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.ConvitePresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.GrupoPresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.HomePresenter;
import br.tiagohm.chatuniversidade.presentation.presenter.InstuticoesPresenter;
import br.tiagohm.chatuniversidade.presentation.view.activity.AulasActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.ContaActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.ConvitesActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.InstituicoesActivity;
import br.tiagohm.chatuniversidade.presentation.view.activity.LoginActivity;
import br.tiagohm.chatuniversidade.presentation.view.dialog.AcceptConviteDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.EditarGrupoDialog;
import br.tiagohm.chatuniversidade.presentation.view.dialog.RevogarConviteDialog;
import dagger.Component;

@Singleton
@Component(modules = {ChatModule.class})
public interface ChatComponent {

    void inject(ContaActivity activity);

    void inject(LoginActivity activity);

    void inject(ContaPresenter presenter);

    void inject(HomePresenter presenter);

    void inject(EditarGrupoDialog dialog);

    void inject(InstituicoesActivity activity);

    void inject(InstuticoesPresenter presenter);

    void inject(AulasPresenter presenter);

    void inject(GrupoPresenter presenter);

    void inject(AulasActivity activity);

    void inject(RevogarConviteDialog dialog);

    void inject(AcceptConviteDialog dialog);

    void inject(ConvitePresenter presenter);

    void inject(ConvitesActivity activity);
}
