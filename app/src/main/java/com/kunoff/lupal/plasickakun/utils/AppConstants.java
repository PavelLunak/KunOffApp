package com.kunoff.lupal.plasickakun.utils;


import com.kunoff.lupal.plasickakun.R;


public interface AppConstants {

    String TAG_H = "hkbsvdhf";

    int animShowFragment = R.anim.anim_fragment_show;
    int animHideFragment = R.anim.anim_fragment_hide;

    String FRAGMENT_NAME_FRAGMENT_MAIN = "FragmentMain_";
    String FRAGMENT_NAME_FRAGMENT_SETTINGS = "FragmentSettings_";
    String FRAGMENT_NAME_FRAGMENT_FILES = "FragmentFiles_";
    String FRAGMENT_NAME_FRAGMENT_INFO = "FragmentInfo_";

    int NOTIFICATION_ID = 1982343;
    String NOTIFICATION_CHANEL_ID = "1982";
    String NOTIFICATION_CHANEL_NAME = "kun_off_chanel";
    String NOTIFICATION_CHANEL_DESCRIPTION = "KunOFF";

    int DELAY_UNIT_SECONDS = 1;
    int DELAY_UNIT_MINUTES = 2;
    int DELAY_UNIT_HOURS = 3;

    int NONE = -1;
    int REQUEST_CODE_SELECT_AUDIO = 1;

    int PERMISSION_REQUEST_CODE = 2;

    int TOOLBAR_ICON_INFO = 1;
    int TOOLBAR_ICON_SETTINGS = 2;
    int TOOLBAR_ICON_BACK = 3;

    public static String PREFS_NAME = "MainActivity__AppPrefs";
    int MAX_NUMBER_OF_CONSECUTIVE_PLAYBACK_ERRORS = 100;

    int[] DEFAULT_MEDIA_ITEMS_IDS = new int[] {
            R.raw.autohavarie,
            R.raw.bzucak,
            R.raw.cikada,
            R.raw.kapka,
            R.raw.klakson,
            R.raw.klaksony,
            R.raw.kone,
            R.raw.krava,
            R.raw.kroky,
            R.raw.kukacka,
            R.raw.kukackahodiny,
            R.raw.lev,
            R.raw.mechanickybudik,
            R.raw.medved,
            R.raw.neexistujicicislo,
            R.raw.obsazenecislo,
            R.raw.pes,
            R.raw.piskani,
            R.raw.rozbijeniskla,
            R.raw.sirena,
            R.raw.smeckapsu,
            R.raw.starytelefon,
            R.raw.stekotmalypes,
            R.raw.stekotvelkypes,
            R.raw.vlk,
            R.raw.vystrel,
            R.raw.vytivlku,
            R.raw.zaba,
            R.raw.zvony,
            R.raw.tone1,
            R.raw.tone2,
            R.raw.tone3,
            R.raw.tone4,
            R.raw.tone5,
            R.raw.tone6,
            R.raw.tone7,
            R.raw.tone8};
}
