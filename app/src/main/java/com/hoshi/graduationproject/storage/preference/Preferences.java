package com.hoshi.graduationproject.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.hoshi.graduationproject.R;

/**
 * SharedPreferences工具类
 * Created by HoshI on 2018/3/25.
 */
public class Preferences {
  private static final String PLAY_POSITION = "play_position";
  private static final String PLAY_MODE = "play_mode";
  private static final String SPLASH_URL = "splash_url";
  private static final String NIGHT_MODE = "night_mode";

  private static Context sContext;

  public static void init(Context context) {
    sContext = context.getApplicationContext();
  }

  public static int getPlayPosition() {
    return getInt(PLAY_POSITION, 0);
  }

  public static void savePlayPosition(int position) {
    saveInt(PLAY_POSITION, position);
  }

  public static int getPlayMode() {
    return getInt(PLAY_MODE, 0);
  }

  public static void savePlayMode(int mode) {
    saveInt(PLAY_MODE, mode);
  }

  public static String getSplashUrl() {
    return getString(SPLASH_URL, "");
  }

  public static void saveSplashUrl(String url) {
    saveString(SPLASH_URL, url);
  }

  public static boolean enableMobileNetworkPlay() {
    return getBoolean(sContext.getString(R.string.setting_key_mobile_network_play), false);
  }

  public static void saveMobileNetworkPlay(boolean enable) {
    saveBoolean(sContext.getString(R.string.setting_key_mobile_network_play), enable);
  }

  public static boolean enableMobileNetworkDownload() {
    return getBoolean(sContext.getString(R.string.setting_key_mobile_network_download), false);
  }

  public static boolean isNightMode() {
    return getBoolean(NIGHT_MODE, false);
  }

  public static void saveNightMode(boolean on) {
    saveBoolean(NIGHT_MODE, on);
  }

  public static String getFilterSize() {
    return getString(sContext.getString(R.string.setting_key_filter_size), "0");
  }

  public static void saveFilterSize(String value) {
    saveString(sContext.getString(R.string.setting_key_filter_size), value);
  }

  public static String getFilterTime() {
    return getString(sContext.getString(R.string.setting_key_filter_time), "0");
  }

  public static void saveFilterTime(String value) {
    saveString(sContext.getString(R.string.setting_key_filter_time), value);
  }

  //存取个人信息
  public static void saveId(int value) {
    saveInt("id", value);
  }

  public static int getId() {
    return getInt("id", 0);
  }

  public static void saveSessionId(String value) {
    saveString("sessionId", value);
  }

  public static String getSessionId() {
    return getString("sessionId", "");
  }

  public static void savePhone(String value) {
    saveString("phone", value);
  }

  public static String getPhone() {
    return getString("phone", "");
  }

  public static String getNickname() {
    return getString("nickname", "");
  }

  public static void saveAvatar(String value) {
    saveString("avatar", value);
  }

  public static String getAvatar() {
    return getString("avatar", "");
  }

  public static void saveSex(int value) {
    saveInt("sex", value);
  }

  public static int getSex() {
    return getInt("sex", 0);
  }

  public static void saveBirthday(String value) {
    saveString("birthday", value);
  }

  public static String getBirthday() {
    return getString("birthday", "");
  }

  public static void saveProfile(String value) {
    saveString("profile", value);
  }

  public static String getProfile() {
    return getString("profile", "");
  }

  public static void savePassword(String value) {
    saveString("password", value);
  }

  public static String getPassword() {
    return getString("password", "");
  }

  //存取朋友信息
  public static void saveFriends(int trends, int follows, int fans) {
    saveInt("trends", trends);
    saveInt("follows", follows);
    saveInt("fans", fans);
  }

  //存取朋友信息
  public static int[] getFriends() {
    int friends[] = {getInt("trends", 0), getInt("follows", 0), getInt("fans", 0)};
    return friends;
  }

  public static void saveNickname(String value) {
    saveString("nickname", value);
  }

  // 存取应用主题皮肤
  public static void saveTheme(int value) {
    saveInt("theme", value);
  }

  public static int getTheme() {
    return getInt("theme", R.style.default_color);
  }

  public static void clear() {
    getPreferences().edit().clear().apply();
  }

  public static void clearSpecified(String key) {
    getPreferences().edit().remove(key);
  }

  private static boolean getBoolean(String key, boolean defValue) {
    return getPreferences().getBoolean(key, defValue);
  }

  private static void saveBoolean(String key, boolean value) {
    getPreferences().edit().putBoolean(key, value).apply();
  }

  private static int getInt(String key, int defValue) {
    return getPreferences().getInt(key, defValue);
  }

  private static void saveInt(String key, int value) {
    getPreferences().edit().putInt(key, value).apply();
  }

  private static long getLong(String key, long defValue) {
    return getPreferences().getLong(key, defValue);
  }

  private static void saveLong(String key, long value) {
    getPreferences().edit().putLong(key, value).apply();
  }

  private static String getString(String key, @Nullable String defValue) {
    return getPreferences().getString(key, defValue);
  }

  private static void saveString(String key, @Nullable String value) {
    getPreferences().edit().putString(key, value).apply();
  }

  private static SharedPreferences getPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(sContext);
  }
}
