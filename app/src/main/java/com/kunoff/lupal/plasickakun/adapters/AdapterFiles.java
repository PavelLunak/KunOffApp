package com.kunoff.lupal.plasickakun.adapters;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.customViews.DialogInfo;
import com.kunoff.lupal.plasickakun.utils.Animators;
import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.customViews.DialogYesNo;
import com.kunoff.lupal.plasickakun.listeners.OnAllPathItemsLoadedListener;
import com.kunoff.lupal.plasickakun.listeners.OnItemDeletedListener;
import com.kunoff.lupal.plasickakun.listeners.OnItemUpdatedListener;
import com.kunoff.lupal.plasickakun.listeners.YesNoSelectedListener;
import com.kunoff.lupal.plasickakun.objects.ItemMedia;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import java.io.IOException;
import java.util.ArrayList;


public class AdapterFiles extends RecyclerView.Adapter<AdapterFiles.DataViewHolder> implements AppConstants {

    final int VIEW_TYPE_NORMAL = 1;
    final int VIEW_TYPE_HELP = 2;

    MainActivity activity;
    boolean showDefaultList;

    public AdapterFiles(MainActivity activity, boolean showDefaultList) {
        this.activity = activity;
        this.showDefaultList = showDefaultList;
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout root;
        ImageView img_play_stop;
        ImageView img_check_box;
        ImageView img_remove;
        TextView label_number;
        TextView label_name;
        TextView label_path;

        RelativeLayout layoutHelpDelete;
        TextView labelHelpDelete;
        TextView labelHelpEnable;
        TextView labelHelpPlay;
        TextView btnClose;


        public DataViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DataViewHolder vh;

        if (viewType == VIEW_TYPE_NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
            vh = new DataViewHolder(v);

            vh.root = (RelativeLayout) v.findViewById(R.id.root);
            vh.img_play_stop = (ImageView) v.findViewById(R.id.img_play_stop);
            vh.img_check_box = (ImageView) v.findViewById(R.id.img_check_box);
            vh.img_remove = (ImageView) v.findViewById(R.id.img_remove);
            vh.label_number = (TextView) v.findViewById(R.id.label_number);
            vh.label_name = (TextView) v.findViewById(R.id.label_name);
            vh.label_path = (TextView) v.findViewById(R.id.label_path);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help_custom_playlist, parent, false);
            vh = new DataViewHolder(v);

            vh.layoutHelpDelete = (RelativeLayout) v.findViewById(R.id.layoutHelpDelete);
            vh.labelHelpDelete = (TextView) v.findViewById(R.id.labelHelpDelete);
            vh.labelHelpEnable = (TextView) v.findViewById(R.id.labelHelpEnable);
            vh.labelHelpPlay = (TextView) v.findViewById(R.id.labelHelpPlay);
            vh.btnClose = (TextView) v.findViewById(R.id.btnClose);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, final int position) {
        final ItemMedia itemMedia = activity.getItemsMedia().get(position);

        if (itemMedia.isHelp()) {

            if (showDefaultList) {
                holder.layoutHelpDelete.setVisibility(View.GONE);
            } else {
                holder.layoutHelpDelete.setVisibility(View.VISIBLE);
                holder.labelHelpDelete.setText(AppUtils.getTextByLanguage(activity, R.string.help_remove_item_from_playlist_cz, R.string.help_remove_item_from_playlist));
            }

            holder.labelHelpEnable.setText(AppUtils.getTextByLanguage(activity, R.string.help_disable_playlist_item_cz, R.string.help_disable_playlist_item));
            holder.labelHelpPlay.setText(AppUtils.getTextByLanguage(activity, R.string.help_play_item_now_cz, R.string.help_play_item_now));

            holder.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (showDefaultList)activity.getItemsMedia().remove(0);
                    else activity.getItemsMedia().remove(1);

                    notifyDataSetChanged();
                    activity.helpWasShowed = true;
                    MainActivity.appPrefs.edit().disableHelpCustomPlaylist().put(true).apply();
                }
            });

            return;
        }

        if (showDefaultList) holder.img_remove.setVisibility(View.GONE);
        if (itemMedia.getPath() == null) holder.label_path.setVisibility(View.GONE);

        if (activity.getItemsMedia().get(0).isHelp()) holder.label_number.setText("" + (position));
        else holder.label_number.setText("" + (position + 1));

        holder.label_name.setText(itemMedia.getFileName());
        holder.label_path.setText(itemMedia.getPath());

        if (activity.getActualPlayedMediaItem() != null) {
            if (itemMedia.getId() == activity.getActualPlayedMediaItem().getId()) itemMedia.setPlay(true);
        }

        if (itemMedia.isEnabled())
            holder.img_check_box.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_check_box_on));
        else
            holder.img_check_box.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_check_box_off));

        if (itemMedia.isPlay()) {
            holder.img_play_stop.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_stop));
            holder.root.setBackgroundColor(activity.getResources().getColor(R.color.color_item_play));
        } else {
            holder.img_play_stop.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_play));
            setItemBackground(holder.root, position);
        }

        holder.img_play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animators.animateButtonClick(holder.img_play_stop);

                if (activity.getItemsMedia().get(position).isPlay()) setItemPlay(NONE);
                else setItemPlay(position);

                notifyDataSetChanged();
            }
        });

        holder.img_check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animators.animateButtonClick(holder.img_check_box);

                if (activity.getItemsMedia().get(position).isEnabled()) {
                    activity.getItemsMedia().get(position).setEnabled(false);
                } else {
                    activity.getItemsMedia().get(position).setEnabled(true);
                }

                if (showDefaultList) {
                    activity.getDataSource().updateDefaultItemEnabled(
                            position + 1,
                            activity.getItemsMedia().get(position).isEnabled(),
                            new OnItemUpdatedListener() {
                                @Override
                                public void onItemUpdated() {
                                    activity.getDataSource().getAllDefaultItems(new OnAllPathItemsLoadedListener() {
                                        @Override
                                        public void onAllPathItemsLoaded(ArrayList<ItemMedia> itemsMedia) {
                                            activity.prepareDefaultItemsList();
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                } else {
                    activity.getDataSource().updateCustomItemEnabled(
                            activity.getItemsMedia().get(position).getId(),
                            activity.getItemsMedia().get(position).isEnabled(),
                            new OnItemUpdatedListener() {
                                @Override
                                public void onItemUpdated() {
                                    activity.getDataSource().getAllCustomItems(new OnAllPathItemsLoadedListener() {
                                        @Override
                                        public void onAllPathItemsLoaded(ArrayList<ItemMedia> itemsMedia) {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                }
            }
        });

        if (!showDefaultList) {
            holder.img_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogYesNo.createDialog(activity)
                            .setTitle("Delete")
                            .setMessage(AppUtils.getTextByLanguage(activity, R.string.question_delet_item_cz, R.string.question_delet_item))
                            .setListener(new YesNoSelectedListener() {
                                @Override
                                public void yesSelected() {
                                    activity.getDataSource().removeitem(activity.getItemsMedia().get(position).getId(), new OnItemDeletedListener() {
                                        @Override
                                        public void onItemDeleted() {
                                            activity.stopMedia();
                                            activity.getDataSource().getAllCustomItems(new OnAllPathItemsLoadedListener() {
                                                @Override
                                                public void onAllPathItemsLoaded(ArrayList<ItemMedia> ItemMedia) {
                                                    notifyDataSetChanged();
                                                    if (MainActivity.appPrefs.customPlaylist().get()) activity.getFragmentFiles().updateItemsCountInfo();
                                                    if (activity.getItemsMedia().isEmpty()) {
                                                        if (activity.getFragmentFiles() != null) {
                                                            activity.getFragmentFiles().setNoItemsLabelVisibility(true);
                                                            if (MainActivity.appPrefs.customPlaylist().get()) activity.getFragmentFiles().updateItemsCountInfo();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override public void noSelected() {}
                            })
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (activity.getItemsMedia().get(position).isHelp()) return VIEW_TYPE_HELP;
        else return VIEW_TYPE_NORMAL;
    }

    private void setItemBackground(View view, int position) {
        if (position % 2 == 0)
            view.setBackgroundColor(activity.getResources().getColor(R.color.color_item_even));
        else view.setBackgroundColor(activity.getResources().getColor(R.color.color_item_odd));
    }

    private void setItemPlay(final int position) {
        if (activity.getItemsMedia() == null) return;
        if (activity.getItemsMedia().isEmpty()) return;

        activity.stopMedia();

        if (position == NONE) return;

        ItemMedia itemMediaToPlay;

        if (position != NONE) {
            itemMediaToPlay = activity.getItemsMedia().get(position);
            activity.setActualPlayedMediaItem(itemMediaToPlay);

            if (showDefaultList) {
                MainActivity.m = MediaPlayer.create(activity, itemMediaToPlay.getId());
            } else {
                try {
                    MainActivity.m = new MediaPlayer();
                    MainActivity.m.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    MainActivity.m.setDataSource(activity, Uri.parse(itemMediaToPlay.getPath()));
                    MainActivity.m.prepare();
                } catch (IOException e) {
                    DialogInfo.createDialog(activity).setTitle(AppUtils.getTextByLanguage(activity, R.string.error_cz, R.string.error)).setMessage(AppUtils.getTextByLanguage(activity, R.string.file_error_cz, R.string.file_error)).show();
                    stopPlayByError();
                    return;
                } catch (NullPointerException e) {
                    DialogInfo.createDialog(activity).setTitle(AppUtils.getTextByLanguage(activity, R.string.error_cz, R.string.error)).setMessage(AppUtils.getTextByLanguage(activity, R.string.file_error_cz, R.string.file_error)).show();
                    stopPlayByError();
                    return;
                } catch (IllegalArgumentException e) {
                    DialogInfo.createDialog(activity).setTitle(AppUtils.getTextByLanguage(activity, R.string.error_cz, R.string.error)).setMessage(AppUtils.getTextByLanguage(activity, R.string.file_error_cz, R.string.file_error)).show();
                    stopPlayByError();
                    return;
                }
            }

            MainActivity.m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    activity.stopMedia();
                    if (activity.getFragmentFiles() != null) activity.getFragmentFiles().showStop(false);
                    activity.setAllItemsStop();
                    notifyDataSetChanged();
                }
            });

            MainActivity.m.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    stopPlayByError();
                    return false;
                }
            });
        }

        if (position != NONE) {
            activity.getItemsMedia().get(position).setPlay(true);
            notifyDataSetChanged();
            activity.showImgStopOnFragmentFiles(true);
            MainActivity.m.start();
        }
    }

    @Override
    public int getItemCount() {
        if (activity.getItemsMedia() == null) return 0;
        if (activity.getItemsMedia().isEmpty()) return 0;
        return activity.getItemsMedia().size();
    }

    private void stopPlayByError() {
        MainActivity.m = null;
        activity.setActualPlayedMediaItem(null);
        if (activity.getFragmentFiles() != null) activity.getFragmentFiles().showStop(false);
        activity.setAllItemsStop();
        notifyDataSetChanged();
    }
}
