package com.kunoff.lupal.plasickakun.customViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.listeners.OnErrorConfirmedListener;


public class DialogError extends Dialog {

    private String title, message;
    private OnErrorConfirmedListener listener;

    public DialogError(Context context) {
        super(context);
    }

    public DialogError setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogError setMessage(String message) {
        this.message = message;
        return this;
    }

    public DialogError setListener(OnErrorConfirmedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_error);

        ((TextView) findViewById(R.id.label_title)).setText(title);
        ((TextView) findViewById(R.id.label_message)).setText(message);
        ((TextView) findViewById(R.id.btnClose)).setText(MainActivity.appPrefs.languageCz().get() ? "Zavřít" : "Close");

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onErrorConfirmed();
                dismiss();
            }
        });
    }

    public static DialogError createDialog(Context context) {
        return new DialogError(context);
    }
}
