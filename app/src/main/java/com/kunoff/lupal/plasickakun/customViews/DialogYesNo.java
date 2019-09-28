package com.kunoff.lupal.plasickakun.customViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.listeners.YesNoSelectedListener;


public class DialogYesNo extends Dialog {

    private String title, message;
    private YesNoSelectedListener listener;

    @ColorRes
    private int colorHeader;
    private int colorMessage;

    public DialogYesNo(Context context) {
        super(context);
    }

    public DialogYesNo setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogYesNo setMessage(String message) {
        this.message = message;
        return this;
    }

    public DialogYesNo setListener(YesNoSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    public DialogYesNo setHeaderColor(@ColorRes int color) {
        colorHeader = color;
        return this;
    }

    public DialogYesNo setMessageColor(@ColorRes int color) {
        colorMessage = color;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_yes_no);

        ((TextView) findViewById(R.id.label_title)).setText(title);
        ((TextView) findViewById(R.id.label_message)).setText(message);
        ((TextView) findViewById(R.id.label_message)).setTextColor(colorMessage);

        ((TextView) findViewById(R.id.btnYes)).setText(MainActivity.appPrefs.languageCz().get() ? "ANO" : "YES");
        ((TextView) findViewById(R.id.btnNo)).setText(MainActivity.appPrefs.languageCz().get() ? "NE" : "NO");

        findViewById(R.id.layout_head).setBackgroundResource(colorHeader);
        findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.noSelected();
                dismiss();
            }
        });

        findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.yesSelected();
                dismiss();
            }
        });
    }

    public static DialogYesNo createDialog(Context context) {
        return new DialogYesNo(context)
                .setHeaderColor(R.color.colorPrimary)
                .setMessageColor(R.color.colorPrimary);
    }
}
