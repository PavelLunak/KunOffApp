package com.kunoff.lupal.plasickakun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.facebook.stetho.Stetho;
import com.kunoff.lupal.plasickakun.customViews.DialogError;
import com.kunoff.lupal.plasickakun.customViews.DialogInfo;
import com.kunoff.lupal.plasickakun.database.DataSource;
import com.kunoff.lupal.plasickakun.fragments.FragmentFiles;
import com.kunoff.lupal.plasickakun.fragments.FragmentFiles_;
import com.kunoff.lupal.plasickakun.fragments.FragmentInfo;
import com.kunoff.lupal.plasickakun.fragments.FragmentInfo_;
import com.kunoff.lupal.plasickakun.fragments.FragmentMain;
import com.kunoff.lupal.plasickakun.fragments.FragmentMain_;
import com.kunoff.lupal.plasickakun.fragments.FragmentSettings;
import com.kunoff.lupal.plasickakun.fragments.FragmentSettings_;
import com.kunoff.lupal.plasickakun.listeners.OnAllPathItemsLoadedListener;
import com.kunoff.lupal.plasickakun.listeners.OnDatabaseChangedListener;
import com.kunoff.lupal.plasickakun.listeners.OnErrorConfirmedListener;
import com.kunoff.lupal.plasickakun.objects.ItemMedia;
import com.kunoff.lupal.plasickakun.utils.Animators;
import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.utils.AppUtils;
import com.kunoff.lupal.plasickakun.utils.RealPathUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Arrays;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements AppConstants, FragmentManager.OnBackStackChangedListener {


    @ViewById
    RelativeLayout toolbar;

    @ViewById
    ImageView img_toolbar_settings, img_toolbar_back, img_info;

    @ViewById
    TextView label_toolbar;

    @Pref
    public static AppPrefs_ appPrefs;

    @InstanceState
    public static int nextPlayPosition;

    DataSource dataSource;

    FragmentManager fragmentManager;
    FragmentFiles fragmentFiles;

    @InstanceState
    public static ArrayList<ItemMedia> itemsMedia;

    @InstanceState
    public static ArrayList<Integer> enabledItemsPositions;

    @InstanceState
    ItemMedia actualPlayedItemMedia;

    @InstanceState
    public static boolean afterIntro, isRunning;

    @InstanceState
    public boolean helpWasShowed;

    public static MediaPlayer m;

    BroadcastReceiver playbackStopReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initStetho();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        dataSource = new DataSource(this);
        dataSource.addDefaultData();

        if (savedInstanceState == null) {
            initData();

            if (!appPrefs.disableInfoOnStart().get()) showFragmentInfo();
            else showMainFragment();
        } else {
            fragmentFiles = (FragmentFiles) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_FILES);
        }

        initAppPrefsFirstRun();

        AlarmActions.createAppNotificationChanel(
                this,
                NOTIFICATION_CHANEL_ID,
                NOTIFICATION_CHANEL_NAME,
                NOTIFICATION_CHANEL_DESCRIPTION,
                NotificationManagerCompat.IMPORTANCE_DEFAULT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isRunning) stopMedia();
        unRegisterplaybackStopReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerplaybackStopReceiver();

        if (appPrefs.error().exists()) {
            stopAfterErrors();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmActions.removeAppNotificationChanel(this);
    }

    @AfterViews
    void afterViews() {
        updateImgSettings();
    }

    public void initData() {
        if (!appPrefs.customPlaylist().get()) {
            dataSource.getAllDefaultItems(new OnAllPathItemsLoadedListener() {
                @Override
                public void onAllPathItemsLoaded(ArrayList<ItemMedia> itemsMedia) {
                    prepareDefaultItemsList();
                }
            });
        } else {
            dataSource.getAllCustomItems(null);
        }
    }

    private void initAppPrefsFirstRun() {
        if (!appPrefs.startDelay().exists()) {
            appPrefs.edit().startDelay().put(1).apply();
            appPrefs.edit().startDelayUnit().put(2).apply();
        }

        if (!appPrefs.delay().exists()) {
            appPrefs.edit().delay().put(10).apply();
            appPrefs.edit().delayUnit().put(2).apply();
        }

        if (!appPrefs.disableHelpCustomPlaylist().exists()) {
            appPrefs.edit().disableHelpCustomPlaylist().put(false).apply();
        }

        if (!appPrefs.customPlaylist().exists()) {
            appPrefs.edit().customPlaylist().put(false).apply();
        }
    }

    @Click(R.id.img_toolbar_settings)
    void clickSettings() {
        showSettingsFragment();
    }

    @Click(R.id.img_toolbar_back)
    void clickBack() {
        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_FILES, fragmentManager))
            Animators.animateButtonClick(img_toolbar_back);
        onBackPressed();
    }

    @Click(R.id.img_info)
    void clickInfo() {
        showFragmentInfo();
    }

    public void animateImgSettings() {
        Animators.animateRotate(img_toolbar_settings, 0);
    }

    void showMainFragment() {
        FragmentMain fragmentMain = (FragmentMain) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_MAIN);

        if (fragmentMain == null) {
            fragmentMain = FragmentMain_.builder().build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentMain, FRAGMENT_NAME_FRAGMENT_MAIN);
            fragmentTransaction.addToBackStack(FRAGMENT_NAME_FRAGMENT_MAIN);
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack(FRAGMENT_NAME_FRAGMENT_MAIN, 0);
        }
    }

    public void showSettingsFragment() {
        FragmentSettings fragmentSettings = (FragmentSettings) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_SETTINGS);

        if (fragmentSettings == null) {
            fragmentSettings = FragmentSettings_.builder().build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentSettings, FRAGMENT_NAME_FRAGMENT_SETTINGS);
            fragmentTransaction.addToBackStack(FRAGMENT_NAME_FRAGMENT_SETTINGS);
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack(FRAGMENT_NAME_FRAGMENT_SETTINGS, 0);
        }
    }

    public void showFragmentAddFile(boolean showDefaultList) {
        fragmentFiles = (FragmentFiles) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_FILES);

        if (fragmentFiles == null) {
            fragmentFiles = FragmentFiles_.builder().showDefaultList(showDefaultList).build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentFiles, FRAGMENT_NAME_FRAGMENT_FILES);
            fragmentTransaction.addToBackStack(FRAGMENT_NAME_FRAGMENT_FILES);
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack(FRAGMENT_NAME_FRAGMENT_FILES, 0);
        }
    }

    public void showFragmentInfo() {
        FragmentInfo fragmentInfo = (FragmentInfo) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_INFO);

        if (fragmentInfo == null) {
            fragmentInfo = FragmentInfo_.builder().build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentInfo, FRAGMENT_NAME_FRAGMENT_INFO);
            fragmentTransaction.addToBackStack(FRAGMENT_NAME_FRAGMENT_INFO);
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack(FRAGMENT_NAME_FRAGMENT_INFO, 0);
        }
    }

    public void stopMedia() {
        if (m != null) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = null;
                } else {
                }
            } catch (IllegalStateException e) {
                DialogInfo.createDialog(this)
                        .setTitle(AppUtils.getTextByLanguage(this, R.string.error, R.string.error_cz))
                        .setMessage(AppUtils.getTextByLanguage(this, R.string.unexpected_error_stop_play_cz, R.string.unexpected_error_stop_play))
                        .show();
            }
        }

        showImgStopOnFragmentFiles(false);
        setActualPlayedMediaItem(null);
        setAllItemsStop();

        if (getFragmentFiles() != null) {
            getFragmentFiles().showStop(false);
            getFragmentFiles().getAdapter().notifyDataSetChanged();
        }
    }

    public void setAllItemsStop() {
        for (ItemMedia itemMedia : getItemsMedia()) {
            itemMedia.setPlay(false);
        }
    }

    @Override
    public void onBackPressed() {
        int fragmentsInStack = fragmentManager.getBackStackEntryCount();

        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_FILES, fragmentManager)) {
            stopMedia();
        }

        if (fragmentsInStack == 1) {
            if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_INFO, fragmentManager)) {
                showMainFragment();
            } else {
                finish();
            }
        } else if (fragmentsInStack == 2) {
            FragmentManager.BackStackEntry be0 = fragmentManager.getBackStackEntryAt(0);
            FragmentManager.BackStackEntry be1 = fragmentManager.getBackStackEntryAt(1);

            if (be0.getName().equals(FRAGMENT_NAME_FRAGMENT_INFO) && be1.getName().equals(FRAGMENT_NAME_FRAGMENT_MAIN)) {
                finish();
            } else super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        fragmentFiles = (FragmentFiles) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_FILES);
        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_INFO, fragmentManager)) {

            FragmentInfo fragmentInfo = (FragmentInfo) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_INFO);
            if (fragmentInfo != null) {
                fragmentInfo.updateLanguage();
                fragmentInfo.updateImage();
                showToolbarIcon(TOOLBAR_ICON_BACK, true, false);
            }
        }

        updateToolbar();
    }

    public void selectMediaFile() {
        Intent intent_upload = new Intent();
        //intent_upload.setType("audio/*");
        intent_upload.setType("*/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, REQUEST_CODE_SELECT_AUDIO);
    }

    public void showImgStopOnFragmentFiles(boolean show) {
        if (fragmentFiles == null) return;
        fragmentFiles.showStop(show);
    }

    public void prepareDefaultItemsList() {
        ArrayList<CharSequence> defaultList = new ArrayList<CharSequence>(Arrays.asList(getResources().getStringArray(R.array.default_sounds_names)));

        if (defaultList.size() != itemsMedia.size())
            DialogInfo.createDialog(this)
                    .setTitle(appPrefs.languageCz().get() ? getString(R.string.error_cz) : getString(R.string.error))
                    .setMessage(appPrefs.languageCz().get() ? getString(R.string.unknown_error_cz) : getString(R.string.unknown_error))
                    .show();

        for (int i = 0, count = itemsMedia.size(); i < count; i++) {
            itemsMedia.get(i).setId(DEFAULT_MEDIA_ITEMS_IDS[i]);
            itemsMedia.get(i).setFileName(defaultList.get(i).toString());
        }
    }

    public void updateToolbar() {
        int beCount = fragmentManager.getBackStackEntryCount();

        if (beCount == 0) {
            label_toolbar.setText(getResources().getString(R.string.app_name));
            img_toolbar_back.setVisibility(View.GONE);
            img_toolbar_settings.setVisibility(View.GONE);
            img_info.setVisibility(View.GONE);
            return;
        }

        String fragmentName = fragmentManager.getBackStackEntryAt(beCount - 1).getName();

        if (fragmentName.equals(FRAGMENT_NAME_FRAGMENT_SETTINGS)) {
            AppUtils.setTextByLanguage(this, getLabelToolbar(), R.string.settings_cz, R.string.settings);
            showToolbarIcon(TOOLBAR_ICON_SETTINGS, false, false);
            showToolbarIcon(TOOLBAR_ICON_INFO, false, false);
            showToolbarIcon(TOOLBAR_ICON_BACK, true, false);
        } else if (fragmentName.equals(FRAGMENT_NAME_FRAGMENT_FILES)) {
            if (appPrefs.customPlaylist().get())
                AppUtils.setTextByLanguage(this, getLabelToolbar(), R.string.custom_playlist_cz, R.string.custom_playlist);
            else
                AppUtils.setTextByLanguage(this, getLabelToolbar(), R.string.default_playlist_cz, R.string.default_playlist);
            showToolbarIcon(TOOLBAR_ICON_SETTINGS, false, true);
            showToolbarIcon(TOOLBAR_ICON_INFO, false, false);
        } else if (fragmentName.equals(FRAGMENT_NAME_FRAGMENT_MAIN)) {
            updateToolbarLabel(getResources().getString(R.string.app_name));
            updateImgSettings();
            showToolbarIcon(TOOLBAR_ICON_BACK, false, false);
            showToolbarIcon(TOOLBAR_ICON_INFO, true, false);
        } else if (fragmentName.equals(FRAGMENT_NAME_FRAGMENT_INFO)) {
            updateToolbarLabel(getResources().getString(R.string.info));
            showToolbarIcon(TOOLBAR_ICON_SETTINGS, false, false);
            showToolbarIcon(TOOLBAR_ICON_INFO, false, false);
        }
    }

    public void showToolbarIcon(int iconType, boolean show, boolean immediately) {
        View view = getViewByType(iconType);
        if (view == null) return;

        if (show) {
            if (view.getVisibility() == View.VISIBLE) {
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                return;
            }

            if (immediately) {
                view.setVisibility(View.VISIBLE);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            } else {
                Animators.animateShow(view);
            }
        } else {
            if (view.getVisibility() == View.GONE) return;

            if (immediately) {
                view.setVisibility(View.GONE);
                view.setScaleX(0.0f);
                view.setScaleY(0.0f);
            } else {
                Animators.animateHide(view);
            }
        }
    }

    public View getViewByType(int viewType) {
        switch (viewType) {
            case TOOLBAR_ICON_INFO:
                return img_info;
            case TOOLBAR_ICON_SETTINGS:
                return img_toolbar_settings;
            case TOOLBAR_ICON_BACK:
                return img_toolbar_back;
            default:
                return null;
        }
    }

    public void updateToolbarLabel(String text) {
        label_toolbar.setText(text);
    }

    public TextView getLabelToolbar() {
        return label_toolbar;
    }

    public ImageView getImgToolbarSettings() {
        return img_toolbar_settings;
    }

    public ImageView getImgToolbarInfo() {
        return img_info;
    }

    public void updateImgSettings() {
        showToolbarIcon(TOOLBAR_ICON_SETTINGS, !isRunning, false);
    }

    public FragmentFiles getFragmentFiles() {
        return fragmentFiles;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ItemMedia getActualPlayedMediaItem() {
        return actualPlayedItemMedia;
    }

    public void setActualPlayedMediaItem(ItemMedia actualPlayedItemMedia) {
        this.actualPlayedItemMedia = actualPlayedItemMedia;
    }

    public FragmentManager getFm() {
        return this.fragmentManager;
    }

    private void initStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    public ArrayList<Integer> setEnabledItemsPositions() {
        ArrayList<Integer> toReturn = new ArrayList<>();

        if (itemsMedia == null) return toReturn;
        if (itemsMedia.isEmpty()) return toReturn;

        for (int i = 0, count = MainActivity.itemsMedia.size(); i < count; i++) {
            if (MainActivity.itemsMedia.get(i).isEnabled()) toReturn.add(new Integer(i));
        }

        return toReturn;
    }

    public ArrayList<ItemMedia> getItemsMedia() {
        return itemsMedia;
    }

    public void setItemsMedia(ArrayList<ItemMedia> itemsMedia) {
        this.itemsMedia = new ArrayList<>(itemsMedia);
        this.enabledItemsPositions = new ArrayList<>(setEnabledItemsPositions());

        if (enabledItemsPositions != null) {
            if (!enabledItemsPositions.isEmpty()) {
                nextPlayPosition = enabledItemsPositions.get(0);
                return;
            }
        }

        nextPlayPosition = 0;
    }

    public void saveItemToDb(ItemMedia itemMedia) {
        dataSource = new DataSource(this);
        final FragmentFiles fragmentFiles = (FragmentFiles) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_FILES);

        dataSource.addItem(false, itemMedia, new OnDatabaseChangedListener() {
            @Override
            public void onItemAdded(final long id) {
                dataSource.getAllCustomItems(new OnAllPathItemsLoadedListener() {
                    @Override
                    public void onAllPathItemsLoaded(ArrayList<ItemMedia> itemsMedia) {
                        if (fragmentFiles != null) {
                            fragmentFiles.getAdapter().notifyDataSetChanged();
                            if (MainActivity.appPrefs.customPlaylist().get())
                                fragmentFiles.updateItemsCountInfo();
                            fragmentFiles.setNoItemsLabelVisibility(false);

                            if (hasHelpItem()) return;

                            if (itemsMedia.size() == 1) {
                                if (!MainActivity.appPrefs.disableHelpCustomPlaylist().get()) {
                                    if (!helpWasShowed) {
                                        itemsMedia.add(new ItemMedia(true));
                                        fragmentFiles.getAdapter().notifyDataSetChanged();
                                        helpWasShowed = true;
                                    }
                                }
                            } else if (itemsMedia.size() == 2) {
                                helpWasShowed = true;
                                MainActivity.appPrefs.edit().disableHelpCustomPlaylist().put(true).apply();
                            }
                        }
                    }
                });
            }

            @Override
            public void onItemRemoved(ItemMedia pathItem) {
            }
        });
    }

    public boolean hasHelpItem() {
        if (itemsMedia == null) return false;
        if (itemsMedia.isEmpty()) return false;
        if (itemsMedia.get(0).isHelp()) return true;

        if (itemsMedia.size() > 1) {
            if (itemsMedia.get(1).isHelp()) return true;
        }

        return false;
    }

    public void registerplaybackStopReceiver() {
        playbackStopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopAfterErrors();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(playbackStopReceiver, new IntentFilter("ACTION_SERVICE_STARTET_BROADCAST"));
    }

    private void unRegisterplaybackStopReceiver() {

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(playbackStopReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void stopAfterErrors() {
        MainActivity.isRunning = false;

        FragmentMain fragmentMain = (FragmentMain) fragmentManager.findFragmentByTag(FRAGMENT_NAME_FRAGMENT_MAIN);
        if (fragmentMain != null) {
            fragmentMain.setBtnToStart();
            if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_MAIN, fragmentManager)) updateImgSettings();
        }

        if (MainActivity.m != null) {
            if (MainActivity.m.isPlaying()) {
                try {
                    MainActivity.m.stop();
                    MainActivity.m.release();
                    MainActivity.m = null;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        DialogError.createDialog(this)
                .setTitle(AppUtils.getTextByLanguage(this, R.string.error_cz, R.string.error))
                .setMessage(AppUtils.getTextByLanguage(this, R.string.too_many_errors_cz, R.string.too_many_errors))
                .setListener(new OnErrorConfirmedListener() {
                    @Override
                    public void onErrorConfirmed() {
                        appPrefs.edit().error().remove().apply();
                        showMainFragment();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_SELECT_AUDIO) {
            if (resultCode == RESULT_OK) {

                Uri uri = data.getData();

                String mime = AppUtils.getMimeType(uri, this);

                if (mime == null) {
                    DialogInfo.createDialog(this)
                            .setTitle(AppUtils.getTextByLanguage(this, R.string.error_cz, R.string.error))
                            .setMessage(AppUtils.getTextByLanguage(this, R.string.unknown_file_format_cz, R.string.unknown_file_format))
                            .show();
                    return;
                } else {
                    if (!mime.contains("audio") && !mime.contains("ogg")) {
                        DialogInfo.createDialog(this)
                                .setTitle(AppUtils.getTextByLanguage(this, R.string.error_cz, R.string.error))
                                .setMessage(AppUtils.getTextByLanguage(this, R.string.incorrect_file_format_cz, R.string.incorrect_file_format))
                                .show();
                        return;
                    }
                }

                String realPath = RealPathUtil.getRealPath(this, uri);
                String fileName = AppUtils.getFileNameFromPath(realPath);

                if (fileName == null) {
                    DialogInfo.createDialog(this)
                            .setTitle(MainActivity.appPrefs.languageCz().get() ? getString(R.string.error_cz) : getString(R.string.error))
                            .setMessage(appPrefs.languageCz().get() ? getString(R.string.error_file_path_cz) : getString(R.string.error_file_path))
                            .show();
                    return;
                }

                ItemMedia itemMedia = new ItemMedia();
                itemMedia.setPath(realPath);
                itemMedia.setFileName(fileName);
                itemMedia.setEnabled(true);
                itemMedia.setPlay(false);

                saveItemToDb(itemMedia);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        showFragmentAddFile(!appPrefs.customPlaylist().get());
                    } else {
                        showNoPermissionGrantedDialog();
                    }
                } else {
                    showNoPermissionGrantedDialog();
                }
            }
        }
    }

    private void showNoPermissionGrantedDialog() {
        DialogInfo.createDialog(this)
                .setTitle(AppUtils.getTextByLanguage(this, R.string.notice_cz, R.string.notice))
                .setMessage(AppUtils.getTextByLanguage(this, R.string.permission_not_granted_cz, R.string.permission_not_granted))
                .show();
    }
}
