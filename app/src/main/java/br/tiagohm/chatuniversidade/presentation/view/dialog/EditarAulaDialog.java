package br.tiagohm.chatuniversidade.presentation.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import br.tiagohm.chatuniversidade.R;
import br.tiagohm.chatuniversidade.model.entity.Aula;
import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class EditarAulaDialog extends AlertDialog.Builder {

    @BindView(R.id.tituloInput)
    public EditText mTitulo;
    @BindView(R.id.conteudoInput)
    public EditText mConteudo;
    @BindView(R.id.editarButton)
    public Button mSalvarButton;
    @BindView(R.id.deletarButton)
    public Button mDeletarButton;
    @BindView(R.id.calendarView)
    public HorizontalCalendarView mCalendario;

    private AlertDialog mDialog;

    public EditarAulaDialog(Aula aula, Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_aula, null, false);
        setView(view);

        ButterKnife.bind(this, view);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 6);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -6);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {
            }
        });
        horizontalCalendar.selectDate(new Date(aula.data), true);
        mCalendario.setHorizontalCalendar(horizontalCalendar);
        mTitulo.setText(aula.titulo);
        mConteudo.setText(aula.conteudo);
    }

    public Observable<Integer> exibir() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> e) throws Exception {
                mSalvarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(1);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mDeletarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(2);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mDialog = show();
            }
        });
    }
}
