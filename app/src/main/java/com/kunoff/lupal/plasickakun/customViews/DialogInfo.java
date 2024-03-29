package com.kunoff.lupal.plasickakun.customViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;


public class DialogInfo extends Dialog {

    private String title, message;

    public DialogInfo(Context context) {
        super(context);
    }

    public DialogInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogInfo setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);

        ((TextView) findViewById(R.id.label_title)).setText(title);
        ((TextView) findViewById(R.id.label_message)).setText(message);
        ((TextView) findViewById(R.id.btnClose)).setText(MainActivity.appPrefs.languageCz().get() ? "Zavřít" : "Close");

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public static DialogInfo createDialog(Context context) {
        return new DialogInfo(context);
    }
}
