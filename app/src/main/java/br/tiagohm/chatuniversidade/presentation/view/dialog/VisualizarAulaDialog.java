package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VisualizarAulaDialog extends AlertDialog.Builder {

    @BindView(R.id.conteudoAula)
    public MarkdownView mConteudo;

    private AlertDialog mDialog;

    public VisualizarAulaDialog(Aula aula, Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_ver_aula, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        mConteudo.addStyleSheet(new Github());
        mConteudo.loadMarkdown(aula.conteudo);
    }

    public void exibir() {
        mDialog = show();
    }
}
