package com.kunoff.lupal.plasickakun.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.kunoff.lupal.plasickakun.BuildConfig;
import com.kunoff.lupal.plasickakun.MainActivity;

public class AppUtils implements AppConstants {
    public static String getFileNameFromPath(String path) {
        if (path == null) return null;
        if (path.isEmpty()) return null;
        if (path.equals("")) return null;

        int lastSlashPosition = 0;

        for (int i = 0; i < path.length(); i ++) {
            char ch = path.charAt(i);
            if (ch == '/') lastSlashPosition = i;
        }

        return path.substring(lastSlashPosition + 1);
    }

    public static boolean isFragmentCurrent(String name, FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            FragmentManager.BackStackEntry be = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            return be.getName().equals(name);
        }
        return false;
    }

    public static void setTextByLanguage(MainActivity activity, TextView view, int textCzResourceId, int textEngResourceId) {
        view.setText(activity.getString(MainActivity.appPrefs.languageCz().get() ? textCzResourceId : textEngResourceId));
    }

    public static String getTextByLanguage(MainActivity activity, int textCzResourceId, int textEngResourceId) {
        return MainActivity.appPrefs.languageCz().get() ? activity.getResources().getString(textCzResourceId) : activity.getResources().getString(textEngResourceId);
    }

    public static String getVersion() {
        return BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
    }

    public static long getTimeValueInMillis(int value, int unit) {
        switch (unit) {
            case DELAY_UNIT_SECONDS:
                return value * 1000;
            case DELAY_UNIT_MINUTES:
                return value * 60 * 1000;
            case DELAY_UNIT_HOURS:
                return value * 60 * 60 * 1000;
            default:
                return 0;
        }
    }

    public static boolean isLollipop() {
        return (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP
                || android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static String getMimeType(Uri uri, MainActivity activity) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = activity.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }

        return mimeType;
    }
}
