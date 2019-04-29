package com.raistone.wallet.sealwallet.ui;

import android.app.Activity;

import java.util.Stack;

public class ActivityManager {

    private static Stack<Activity> activityStack = new Stack<Activity>();

    private static ActivityManager activityManager = new ActivityManager();

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        return activityManager;
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }


    public void endActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty())
            activity = activityStack.lastElement();
        return activity;
    }


    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }


    public void popAllActivityExceptOne(Class<? extends Activity> cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }


    public void finishAllActivityExceptOne(Class<? extends Activity> cls) {
        while (!activityStack.empty()) {
            Activity activity = currentActivity();
            if (activity.getClass().equals(cls)) {
                popActivity(activity);
            } else {
                endActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        if (activityStack != null) {
            while (!activityStack.empty()) {
                Activity activity = currentActivity();
                endActivity(activity);
            }
        }
    }
}
