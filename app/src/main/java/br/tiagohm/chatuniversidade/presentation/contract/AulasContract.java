package br.tiagohm.chatuniversidade.presentation.contract;

import br.tiagohm.chatuniversidade.common.base.BaseMvpPresenter;
import br.tiagohm.chatuniversidade.common.base.BaseMvpView;
import br.tiagohm.chatuniversidade.model.entity.Aula;

public interface AulasContract {

    interface View extends BaseMvpView {

        void adicionarAula(Aula aula);

        void atualizarListaDeAulas();

        void removerAula(Aula aula);

        void atualizarAula(Aula aula);
    }

    interface Presenter extends BaseMvpPresenter<View> {

        void novaAula(String grupoId, String titulo, String conteudo, long data);

        void editarAula(String grupoId, String id, String titulo, String conteudo, long data);

        void removerAula(String grupoId, Aula aula);

        void monitorarAulas(String grupoId);
    }
}
