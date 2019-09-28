package com.kunoff.lupal.plasickakun.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.utils.Animators;
import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_info)
public class FragmentInfo extends Fragment implements AppConstants {

    @ViewById
    TextView textView4, textView5, textView6, textView7, textView9, labelVersion, titleSourceCode, labelSourceCode;

    @ViewById
    ImageView imageView6;

    @ViewById
    CheckBox checkBox2;

    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) activity = (MainActivity) context;
    }

    @AfterViews
    void afterViews() {

        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_INFO, activity.getFm())) {
            activity.showToolbarIcon(TOOLBAR_ICON_INFO, false, false);
            activity.showToolbarIcon(TOOLBAR_ICON_SETTINGS, false, false);
            activity.showToolbarIcon(TOOLBAR_ICON_BACK, true, false);
        }

        activity.updateToolbarLabel(getResources().getString(R.string.info));
        checkBox2.setChecked(MainActivity.appPrefs.disableInfoOnStart().get());
        updateLanguage();
        updateImage();
        labelVersion.setText(AppUtils.getVersion());
    }

    @Click(R.id.imageView6)
    void clickImage() {
        String url = "https://www.itnetwork.cz/programovani/programatorske-souteze/itnetwork-summer-2019";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.checkBox2)
    void clickCheckBox() {
        MainActivity.appPrefs.edit().disableInfoOnStart().put(checkBox2.isChecked()).apply();
    }

    @Click(R.id.textView9)
    void clickITnetwork() {
        Animators.animateButtonClick(textView9);
        String url = "https://www.itnetwork.cz/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Click(R.id.labelSourceCode)
    void clickSourceCode() {
        Animators.animateButtonClick(labelSourceCode);
        String url = "https://github.com/PavelLunak/KunOffApp";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void updateLanguage() {
        AppUtils.setTextByLanguage(activity, textView4, R.string.text_info_1_cz, R.string.text_info_1);
        AppUtils.setTextByLanguage(activity, textView6, R.string.text_info_2_cz, R.string.text_info_2);
        AppUtils.setTextByLanguage(activity, textView7, R.string.text_info_3_cz, R.string.text_info_3);
        AppUtils.setTextByLanguage(activity, checkBox2, R.string.not_show_again_cz, R.string.not_show_again);
        AppUtils.setTextByLanguage(activity, titleSourceCode, R.string.source_code_cz, R.string.source_code);
        AppUtils.setTextByLanguage(activity, labelSourceCode, R.string.here_cz, R.string.here);
        AppUtils.setTextByLanguage(activity, textView5, R.string.version_cz, R.string.version);
    }

    public void updateImage() {
        if (MainActivity.appPrefs.languageCz().get()) imageView6.setImageDrawable(activity.getResources().getDrawable(R.drawable.summer2019));
        else imageView6.setImageDrawable(activity.getResources().getDrawable(R.drawable.summer2019_en));
    }
}
