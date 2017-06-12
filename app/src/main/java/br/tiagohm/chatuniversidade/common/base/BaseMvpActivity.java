package br.tiagohm.chatuniversidade.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpPresenter;

import butterknife.ButterKnife;

public abstract class BaseMvpActivity<V extends BaseMvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P>
        implements BaseMvpView {

    private ProgressDialog mProgressDialog;

    protected abstract int getLayoutId();

    protected abstract String getTitleString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        setTitle(getTitleString());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        ButterKnife.bind(this);
    }

    @Override
    public void showProgess(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.hide();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
