package com.tang.mexicomaven;

import android.app.Application;

public class AppApplication extends Application {


    private static AppApplication appApplication;

    public static AppApplication getInstance() {
        return appApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appApplication = this;
    }


}
