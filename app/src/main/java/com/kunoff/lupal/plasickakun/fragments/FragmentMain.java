package com.kunoff.lupal.plasickakun.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;

import com.kunoff.lupal.plasickakun.AlarmActions;
import com.kunoff.lupal.plasickakun.AppPrefs_;
import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.customViews.DialogInfo;
import com.kunoff.lupal.plasickakun.objects.ItemMedia;
import com.kunoff.lupal.plasickakun.utils.Animators;
import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;


@EFragment(R.layout.fragment_main)
public class FragmentMain extends Fragment implements AppConstants {

    @ViewById
    ImageView imgLogo, imgStop;

    @ViewById
    Button btnStartStop;

    @Pref
    public static AppPrefs_ appPrefs;

    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) activity = (MainActivity) context;
    }

    @AfterViews
    void afterViews() {
        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_MAIN, activity.getFm())) {
            activity.showToolbarIcon(TOOLBAR_ICON_BACK, false, false);
            activity.showToolbarIcon(TOOLBAR_ICON_INFO, true, false);
            activity.updateImgSettings();
        }

        activity.updateToolbarLabel(getResources().getString(R.string.app_name));

        if (MainActivity.isRunning) setBtnToStop();
        else setBtnToStart();

        if (MainActivity.afterIntro) {
            imgLogo.setScaleX(1f);
            imgLogo.setScaleY(1f);
            imgStop.setScaleX(1f);
            imgStop.setScaleY(1f);
            imgStop.setAlpha(1f);
        } else {
            MainActivity.afterIntro = true;
            Animators.AnimateLogoMarten(imgLogo);
            Animators.AnimateLogoStop(imgStop);
            Animators.animateVibrate(btnStartStop);
            Animators.animateVibrate(imgLogo);
            Animators.animateVibrate(activity.getLabelToolbar());
            Animators.animateVibrate(activity.getImgToolbarInfo());
            Animators.animateVibrate(activity.getImgToolbarSettings());
        }
    }

    @Click(R.id.btnStartStop)
    void clickStart() {
        Animators.animateButtonClick(btnStartStop);
        if (MainActivity.isRunning) stopFrightening();
        else startFrightening();
        activity.updateImgSettings();
    }

    private void startFrightening() {
        Animators.animateButtonClick(btnStartStop);
        boolean hasData = true;

        if (activity.getItemsMedia() == null) hasData = false;
        if (activity.getItemsMedia().isEmpty()) hasData = false;

        if (!hasData) {
            DialogInfo.createDialog(activity)
                    .setTitle(MainActivity.appPrefs.languageCz().get() ? getString(R.string.error_cz) : getString(R.string.error))
                    .setMessage(MainActivity.appPrefs.languageCz().get() ? getString(R.string.empty_playlist_cz) : getString(R.string.empty_playlist))
                    .show();

            activity.animateImgSettings();
            return;
        }

        if (getCountOfEnabled() <= 0) {
            DialogInfo.createDialog(activity)
                    .setTitle(MainActivity.appPrefs.languageCz().get() ? getString(R.string.error_cz) : getString(R.string.error))
                    .setMessage(MainActivity.appPrefs.languageCz().get() ? getString(R.string.all_items_disabled_cz) : getString(R.string.all_items_disabled))
                    .show();

            activity.animateImgSettings();
            return;
        }

        setBtnToStop();
        MainActivity.isRunning = true;
        AlarmActions.enableAlarm(activity, AppUtils.getTimeValueInMillis(appPrefs.startDelay().get(), appPrefs.startDelayUnit().get()));
    }

    private void stopFrightening() {
        MainActivity.isRunning = false;
        setBtnToStart();

        if (MainActivity.m != null) {
            if (MainActivity.m.isPlaying()) {
                try {
                    MainActivity.m.stop();
                    MainActivity.m.release();
                    MainActivity.m = null;
                } catch (IllegalStateException e) {
                    DialogInfo.createDialog(activity)
                            .setTitle(AppUtils.getTextByLanguage(activity, R.string.error, R.string.error_cz))
                            .setMessage(AppUtils.getTextByLanguage(activity, R.string.unexpected_error_stop_play_cz, R.string.unexpected_error_stop_play))
                            .show();
                }
            }
        }

        AlarmActions.disableAlarm(activity);
    }

    private int getCountOfEnabled() {
        if (activity.getItemsMedia() == null) return 0;
        if (activity.getItemsMedia().isEmpty()) return 0;

        int toReturn = 0;

        for (ItemMedia im : activity.getItemsMedia()) {
            if (im.isEnabled()) toReturn += 1;
        }

        return toReturn;
    }

    public void setBtnToStart() {
        btnStartStop.setBackgroundResource(R.drawable.btn_start_bg);
        btnStartStop.setTextColor(activity.getResources().getColor(R.color.btn_start_text_color));
        btnStartStop.setText(R.string.start);
    }

    public void setBtnToStop() {
        btnStartStop.setBackgroundResource(R.drawable.btn_stop_bg);
        btnStartStop.setTextColor(activity.getResources().getColor(R.color.btn_stop_text_color));
        btnStartStop.setText(R.string.stop);
    }
}
