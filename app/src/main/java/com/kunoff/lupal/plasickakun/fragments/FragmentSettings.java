package com.kunoff.lupal.plasickakun.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.AppPrefs_;
import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.customViews.DialogYesNo;
import com.kunoff.lupal.plasickakun.listeners.YesNoSelectedListener;
import com.kunoff.lupal.plasickakun.utils.Animators;
import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;


@EFragment(R.layout.fragment_settings)
public class FragmentSettings extends Fragment implements AppConstants {

    @ViewById
    ImageView img_cz, img_eng;

    @ViewById
    TextView titleLanguage, titleFirstPlay, titleDelay,
            label_set_playlist;

    @ViewById
    EditText etFirstPlay, etDelay;

    @ViewById
    RadioButton
            rbCzech,
            rbEnglish,
            rbFirstPlaySeconds,
            rbFirstPlayMinutes,
            rbFirstPlayHours,
            rbDelaySeconds,
            rbDelayMinutes,
            rbDelayHours,
            rb_playlist_default,
            rb_playlist_custom;

    @ViewById
    Switch switchRandom;

    @Pref
    public static AppPrefs_ appPrefs;

    MainActivity activity;
    boolean isAfterViews;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) activity = (MainActivity) context;
    }

    @AfterViews
    void afterViews() {
        isAfterViews = true;

        enableControls(MainActivity.isRunning);

        updateLanguage();

        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_SETTINGS, activity.getFm())) {
            activity.showToolbarIcon(TOOLBAR_ICON_BACK, true, false);
            activity.showToolbarIcon(TOOLBAR_ICON_SETTINGS, false, false);
            activity.showToolbarIcon(TOOLBAR_ICON_INFO, false, false);
        }

        etFirstPlay.setText("" + appPrefs.startDelay().get());
        etDelay.setText("" + appPrefs.delay().get());

        switch (appPrefs.startDelayUnit().get()) {
            case DELAY_UNIT_SECONDS:
                rbFirstPlaySeconds.setChecked(true);
                break;
            case DELAY_UNIT_MINUTES:
                rbFirstPlayMinutes.setChecked(true);
                break;
            case DELAY_UNIT_HOURS:
                rbFirstPlayHours.setChecked(true);
                break;
        }

        switch (appPrefs.delayUnit().get()) {
            case DELAY_UNIT_SECONDS:
                rbDelaySeconds.setChecked(true);
                break;
            case DELAY_UNIT_MINUTES:
                rbDelayMinutes.setChecked(true);
                break;
            case DELAY_UNIT_HOURS:
                rbDelayHours.setChecked(true);
                break;
        }

        isAfterViews = false;
    }

    @CheckedChange({
            R.id.rbDelayHours,
            R.id.rbDelayMinutes,
            R.id.rbDelaySeconds,
            R.id.rbFirstPlayHours,
            R.id.rbFirstPlayMinutes,
            R.id.rbFirstPlaySeconds,
            R.id.rb_playlist_default,
            R.id.rb_playlist_custom})

    void clickRb(CompoundButton rb) {
        if (isAfterViews) return;

        switch (rb.getId()) {
            case R.id.rbDelayHours:
                if (rb.isChecked()) appPrefs.edit().delayUnit().put(DELAY_UNIT_HOURS).apply();
                break;
            case R.id.rbDelayMinutes:
                if (rb.isChecked()) appPrefs.edit().delayUnit().put(DELAY_UNIT_MINUTES).apply();
                break;
            case R.id.rbDelaySeconds:
                if (rb.isChecked()) appPrefs.edit().delayUnit().put(DELAY_UNIT_SECONDS).apply();
                break;
            case R.id.rbFirstPlayHours:
                if (rb.isChecked()) appPrefs.edit().startDelayUnit().put(DELAY_UNIT_HOURS).apply();
                break;
            case R.id.rbFirstPlayMinutes:
                if (rb.isChecked())
                    appPrefs.edit().startDelayUnit().put(DELAY_UNIT_MINUTES).apply();
                break;
            case R.id.rbFirstPlaySeconds:
                if (rb.isChecked())
                    appPrefs.edit().startDelayUnit().put(DELAY_UNIT_SECONDS).apply();
                break;
            case R.id.rb_playlist_default:
                if (rb.isChecked()) {
                    appPrefs.edit().customPlaylist().put(false).apply();
                    AppUtils.setTextByLanguage(activity, label_set_playlist, R.string.show_playlist_cz, R.string.show_playlist);
                    activity.initData();
                }
                break;
            case R.id.rb_playlist_custom:
                if (rb.isChecked()) {
                    appPrefs.edit().customPlaylist().put(true).apply();
                    AppUtils.setTextByLanguage(activity, label_set_playlist, R.string.edit_playlist_cz, R.string.edit_playlist);
                    activity.initData();
                }
                break;
        }
    }

    @AfterTextChange({R.id.etFirstPlay, R.id.etDelay})
    void etAfterTextChange(TextView et) {
        if (isAfterViews) return;

        switch (et.getId()) {
            case R.id.etFirstPlay:
                if (et.getText() != null) {
                    try {
                        if (!et.getText().toString().equals("")) {
                            if (rbFirstPlaySeconds.isChecked())
                                appPrefs.edit().startDelay().put(Integer.valueOf(et.getText().toString())).apply();
                            else if (rbFirstPlayMinutes.isChecked())
                                appPrefs.edit().startDelay().put(Integer.valueOf(et.getText().toString())).apply();
                            else if (rbFirstPlayHours.isChecked())
                                appPrefs.edit().startDelay().put(Integer.valueOf(et.getText().toString())).apply();
                        }
                    } catch (NumberFormatException e) {

                    }
                }
                break;
            case R.id.etDelay:
                if (et.getText() != null) {
                    try {
                        if (!et.getText().toString().equals("")) {
                            if (rbDelaySeconds.isChecked())
                                appPrefs.edit().delay().put(Integer.valueOf(et.getText().toString())).apply();
                            else if (rbDelayMinutes.isChecked())
                                appPrefs.edit().delay().put(Integer.valueOf(et.getText().toString())).apply();
                            else if (rbDelayHours.isChecked())
                                appPrefs.edit().delay().put(Integer.valueOf(et.getText().toString())).apply();
                        }
                    } catch (NumberFormatException e) {

                    }
                }
                break;
        }
    }

    @Click(R.id.label_set_playlist)
    void clickCreatePlaylist() {
        if (!rbCzech.isEnabled()) return;
        Animators.animateButtonClick(label_set_playlist);

        if (MainActivity.appPrefs.customPlaylist().get()) checkPermission();
        else activity.showFragmentAddFile(rb_playlist_default.isChecked());
    }

    @Click(R.id.rb_playlist_custom)
    void clickRbCustom() {
        Animators.animateButtonClick(label_set_playlist);
    }

    @Click(R.id.switchRandom)
    void clickSwitchRandom() {
        appPrefs.edit().random().put(switchRandom.isChecked()).apply();
    }

    @Click({R.id.rbCzech, R.id.img_cz})
    void clickCzech() {
        if (!rbEnglish.isEnabled()) return;
        setCz();
        appPrefs.edit().languageCz().put(true).apply();
    }

    @Click({R.id.rbEnglish, R.id.img_eng})
    void clickEnglish() {
        if (!rbCzech.isEnabled()) return;
        setEng();
        appPrefs.edit().languageCz().put(false).apply();
    }

    private void setCz() {
        appPrefs.edit().languageCz().put(true).apply();
        updateLanguage();
    }

    private void setEng() {
        appPrefs.edit().languageCz().put(false).apply();
        updateLanguage();
    }

    private void updateLanguage() {
        AppUtils.setTextByLanguage(activity, activity.getLabelToolbar(), R.string.settings_cz, R.string.settings);

        rbCzech.setChecked(appPrefs.languageCz().get());
        rbEnglish.setChecked(!appPrefs.languageCz().get());

        if (appPrefs.customPlaylist().get()) AppUtils.setTextByLanguage(activity, label_set_playlist, R.string.edit_playlist_cz, R.string.edit_playlist);
        else AppUtils.setTextByLanguage(activity, label_set_playlist, R.string.show_playlist_cz, R.string.show_playlist);

        AppUtils.setTextByLanguage(activity, titleLanguage, R.string.language_cz, R.string.language);
        AppUtils.setTextByLanguage(activity, titleFirstPlay, R.string.title_first_sound_after_cz, R.string.title_first_sound_after);
        AppUtils.setTextByLanguage(activity, titleDelay, R.string.title_pause_between_sounds_cz, R.string.title_pause_between_sounds);

        AppUtils.setTextByLanguage(activity, rbFirstPlaySeconds, R.string.seconds_cz, R.string.seconds);
        AppUtils.setTextByLanguage(activity, rbDelaySeconds, R.string.seconds_cz, R.string.seconds);

        AppUtils.setTextByLanguage(activity, rbFirstPlayMinutes, R.string.minutes_cz, R.string.minutes);
        AppUtils.setTextByLanguage(activity, rbDelayMinutes, R.string.minutes_cz, R.string.minutes);

        AppUtils.setTextByLanguage(activity, rbFirstPlayHours, R.string.hours_cz, R.string.hours);
        AppUtils.setTextByLanguage(activity, rbDelayHours, R.string.hours_cz, R.string.hours);

        AppUtils.setTextByLanguage(activity, rb_playlist_default, R.string.label_default_cz, R.string.label_default);
        AppUtils.setTextByLanguage(activity, rb_playlist_custom, R.string.label_custom_cz, R.string.label_custom);
        AppUtils.setTextByLanguage(activity, switchRandom, R.string.play_random_cz, R.string.play_random);

        switchRandom.setChecked(appPrefs.random().get());

        rb_playlist_custom.setChecked(appPrefs.customPlaylist().get());
        rb_playlist_default.setChecked(!appPrefs.customPlaylist().get());
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                DialogYesNo.createDialog(activity)
                        .setTitle(AppUtils.getTextByLanguage(activity, R.string.notice_cz, R.string.notice))
                        .setMessage(AppUtils.getTextByLanguage(activity, R.string.permission_info_cz, R.string.permission_info))
                        .setListener(new YesNoSelectedListener() {
                            @Override
                            public void yesSelected() {
                                ActivityCompat.requestPermissions(
                                        activity,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_CODE
                                );
                            }

                            @Override public void noSelected() {}
                        }).show();
            } else {
                activity.showFragmentAddFile(rb_playlist_default.isChecked());
            }
        } else {
            activity.showFragmentAddFile(rb_playlist_default.isChecked());
        }
    }

    public void enableControls(boolean enable) {
        rbCzech.setEnabled(!enable);
        rbEnglish.setEnabled(!enable);
        etFirstPlay.setEnabled(!enable);
        etDelay.setEnabled(!enable);
        rbFirstPlaySeconds.setEnabled(!enable);
        rbFirstPlayMinutes.setEnabled(!enable);
        rbFirstPlayHours.setEnabled(!enable);
        rbDelaySeconds.setEnabled(!enable);
        rbDelayMinutes.setEnabled(!enable);
        rbDelayHours.setEnabled(!enable);
        rb_playlist_default.setEnabled(!enable);
        rb_playlist_custom.setEnabled(!enable);
        switchRandom.setEnabled(!enable);

        label_set_playlist.setBackground(activity.getResources().getDrawable(MainActivity.isRunning ? R.drawable.bg_btn_playlist_disabled : R.drawable.bg_btn_playlist));
    }
}
