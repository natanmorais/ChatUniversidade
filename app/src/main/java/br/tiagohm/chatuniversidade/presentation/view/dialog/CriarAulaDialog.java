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
import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class CriarAulaDialog extends AlertDialog.Builder {
    @BindView(R.id.tituloInput)
    public EditText mTitulo;
    @BindView(R.id.conteudoInput)
    public EditText mConteudo;
    @BindView(R.id.criarButton)
    public Button mCriarButton;
    @BindView(R.id.calendarView)
    public HorizontalCalendarView mCalendario;

    private AlertDialog mDialog;

    public CriarAulaDialog(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_criar_aula, null, false);
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
        mCalendario.setHorizontalCalendar(horizontalCalendar);
    }

    public Observable<Boolean> exibir() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                mCriarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.onNext(true);
                        e.onComplete();
                        mDialog.dismiss();
                    }
                });
                mDialog = show();
            }
        });
    }
}
