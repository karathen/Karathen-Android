package com.raistone.wallet.sealwallet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raistone.wallet.sealwallet.ui.MainActivity;

public class GuideAdapter extends PagerAdapter {

    private int[] layouts;

    private LayoutInflater layoutInflater;

    private View currentView;

    private Activity activity;

    public GuideAdapter(Activity activity, int[] ns) {
        layouts = ns;
        this.activity = activity;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (layouts == null) {
            return 0;
        } else {
            return layouts.length;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        currentView = (View) object;
        return view.equals(currentView);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(layouts[position], null);
        container.addView(view, 0);

        return view;
    }

    private void moveToIndex() {
        Intent intent = new Intent();
        intent.setClass(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
