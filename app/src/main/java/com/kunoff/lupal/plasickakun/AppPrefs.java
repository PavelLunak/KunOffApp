package com.kunoff.lupal.plasickakun;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface AppPrefs {
    int startDelay();
    int delay();
    int startDelayUnit();
    int delayUnit();
    boolean customPlaylist();
    boolean disableInfoOnStart();
    boolean disableHelpCustomPlaylist();
    boolean disableHelpDefaultPlaylist();
    boolean random();
    boolean languageCz();
    int error();
}
