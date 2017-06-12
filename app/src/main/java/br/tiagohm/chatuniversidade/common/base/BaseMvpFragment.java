package br.tiagohm.chatuniversidade.common.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpFragment;

public abstract class BaseMvpFragment<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends MvpFragment<V, P>
        implements BaseMvpView {

    private ProgressDialog mProgressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void showProgess(boolean show) {
        if (show) mProgressDialog.show();
        else mProgressDialog.hide();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
