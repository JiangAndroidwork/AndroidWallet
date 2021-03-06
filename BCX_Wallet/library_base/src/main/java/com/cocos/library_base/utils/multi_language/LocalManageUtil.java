package com.cocos.library_base.utils.multi_language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cocos.library_base.R;
import com.cocos.library_base.global.SPKeyGlobal;
import com.cocos.library_base.utils.ActivityContainer;
import com.cocos.library_base.utils.SPUtils;
import com.cocos.library_base.utils.Utils;

import java.util.Locale;

public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }


    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {

        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return Locale.CHINA;
            case 1:
                return Locale.ENGLISH;
            default:
                return Locale.CHINA;
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static String getSetLanguageString(Context context) {

        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return Utils.getString(R.string.language_cn);
            case 1:
                return Utils.getString(R.string.language_en);
            default:
                return Utils.getString(R.string.language_cn);
        }
    }


    public static void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.d(TAG, locale.getLanguage());
        SPUtil.getInstance(context).setSystemCurrentLocal(locale);
        String systemLanguage = SPUtils.getString(context, SPKeyGlobal.SYSTEM_LANGUAGE, "");
        if (!TextUtils.isEmpty(systemLanguage) && !TextUtils.equals(locale.getCountry(), systemLanguage)) {
            ActivityContainer.finishAllActivity();
        }
        SPUtils.putString(context, SPKeyGlobal.SYSTEM_LANGUAGE, locale.getCountry());
    }

    public static void onConfigurationChanged(Context context) {
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
}
