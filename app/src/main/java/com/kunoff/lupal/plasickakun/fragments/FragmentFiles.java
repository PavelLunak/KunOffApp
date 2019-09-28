package com.kunoff.lupal.plasickakun.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.adapters.AdapterFiles;
import com.kunoff.lupal.plasickakun.objects.ItemMedia;
import com.kunoff.lupal.plasickakun.utils.Animators;
import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_media)
public class FragmentFiles extends Fragment implements AppConstants {

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    TextView labelNoItems, label_info, label_count_of_items;

    @ViewById
    ImageView img_add_item, img_stop_cur_media;

    @ViewById
    RelativeLayout layout_footer;

    @FragmentArg
    boolean showDefaultList;


    MainActivity activity;
    AdapterFiles adapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) activity = (MainActivity) context;
    }

    @AfterViews
    void afterViews() {

        if (AppUtils.isFragmentCurrent(FRAGMENT_NAME_FRAGMENT_FILES, activity.getFm())) {
            activity.showToolbarIcon(TOOLBAR_ICON_SETTINGS, false, false);
            activity.showToolbarIcon(TOOLBAR_ICON_INFO, false, false);
            activity.showToolbarIcon(TOOLBAR_ICON_BACK, true, false);
        }

        if (MainActivity.appPrefs.customPlaylist().get()) AppUtils.setTextByLanguage(activity, activity.getLabelToolbar(), R.string.custom_playlist_cz, R.string.custom_playlist);
        else AppUtils.setTextByLanguage(activity, activity.getLabelToolbar(), R.string.default_playlist_cz, R.string.default_playlist);

        updateAdapter();
        updateLanguage();

        if (activity.getActualPlayedMediaItem() != null) img_stop_cur_media.setVisibility(View.VISIBLE);
        else img_stop_cur_media.setVisibility(View.GONE);

        if (MainActivity.appPrefs.customPlaylist().get()) {
            layout_footer.setVisibility(View.VISIBLE);

            if (activity.getItemsMedia() == null) Animators.animateRotate(img_add_item, 1000);
            else if (activity.getItemsMedia().isEmpty()) Animators.animateRotate(img_add_item, 300);
        } else {
            layout_footer.setVisibility(View.GONE);
        }
    }

    @Click({R.id.img_add_item, R.id.labelNoItems})
    void clickAddItem() {
        Animators.animateButtonClick(img_add_item);
        activity.selectMediaFile();
    }

    @Click(R.id.img_stop_cur_media)
    void clickStopMedia() {
        activity.stopMedia();

        if (MainActivity.m != null) {
            if (MainActivity.m.isPlaying()) {
                MainActivity.m.stop();
                MainActivity.m.release();
                MainActivity.m = null;
                showStop(false);
                activity.setActualPlayedMediaItem(null);

                if (activity.getFragmentFiles() != null) {
                    activity.setAllItemsStop();
                }
            }
        }
    }

    public void updateAdapter() {
        if (recyclerView == null) return;

        if (activity.getItemsMedia() == null) {
            labelNoItems.setVisibility(View.VISIBLE);
        } else if (activity.getItemsMedia().isEmpty()) {
            labelNoItems.setVisibility(View.VISIBLE);
        } else {
            labelNoItems.setVisibility(View.GONE);
        }

        if (!activity.hasHelpItem()) {
            if (showDefaultList) {
                if (!MainActivity.appPrefs.disableHelpCustomPlaylist().get()) {
                    if (!activity.helpWasShowed) {
                        activity.getItemsMedia().add(0, new ItemMedia(true));
                    }
                }
            } else {
                if (activity.getItemsMedia().size() == 1) {
                    if (!MainActivity.appPrefs.disableHelpCustomPlaylist().get()) {
                        if (!activity.helpWasShowed) {
                            activity.getItemsMedia().add(new ItemMedia(true));
                        }
                    }
                }
            }
        }

        adapter = new AdapterFiles(activity, showDefaultList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        if (MainActivity.appPrefs.customPlaylist().get()) updateItemsCountInfo();
    }

    public void updateItemsCountInfo() {
        if (activity.getItemsMedia() == null) {
            label_count_of_items.setText("" + 0);
            return;
        }

        if (activity.getItemsMedia().isEmpty()) {
            label_count_of_items.setText("" + 0);
            return;
        }

        AppUtils.setTextByLanguage(activity, label_info, R.string.number_of_items_cz, R.string.number_of_items);
        label_count_of_items.setText("" + activity.getItemsMedia().size());

        if (showDefaultList) {
            if (activity.getItemsMedia().get(0).isHelp()) {
                label_count_of_items.setText("" + (activity.getItemsMedia().size() - 1));
            }
        } else {
            if (activity.getItemsMedia().size() == 2) {
                if (activity.getItemsMedia().get(1).isHelp()) {
                    label_count_of_items.setText("" + (activity.getItemsMedia().size() - 1));
                }
            }
        }
    }

    public void showStop(boolean show) {
        if (!MainActivity.appPrefs.customPlaylist().get()) return;
        if (img_stop_cur_media == null) return;
        img_stop_cur_media.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public AdapterFiles getAdapter() {
        return adapter;
    }

    public void setNoItemsLabelVisibility(boolean visible) {
        labelNoItems.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void updateLanguage() {
        if (MainActivity.appPrefs.languageCz().get()) labelNoItems.setText(Html.fromHtml(getString(R.string.no_items_cz)));
        else labelNoItems.setText(Html.fromHtml(getString(R.string.no_items)));

        AppUtils.setTextByLanguage(activity, label_info, R.string.number_of_items_cz, R.string.number_of_items);
    }
}
