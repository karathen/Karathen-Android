package com.raistone.wallet.sealwallet.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raistone.wallet.sealwallet.R;


public class TitleBar extends FrameLayout {
    private static int EXT_PADDING_TOP;
    private TextView mTitle;
    private TextView mSubmit;
    private ImageView mIcon;
    private ImageView mMsg;
    private RelativeLayout back_ll;
    public TitleBar(@NonNull Context context) {
        super(context);
        init(null, 0, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null, 0, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }
    private  void init(AttributeSet attrs, int defStyleAttr, int defStyleRes){
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.base_title_bar,this, true);
        mTitle = findViewById(R.id.tv_title);
        mSubmit = findViewById(R.id.tv_right_title);
        mIcon = findViewById(R.id.ic_back);
        mMsg = findViewById(R.id.mes_icon);
        back_ll= findViewById(R.id.back_ll);
        if (attrs != null) {
            // Load attributes
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.TitleBar, defStyleAttr, defStyleRes);

            String title = a.getString(R.styleable.TitleBar_aTitle);
            String msg = a.getString(R.styleable.TitleBar_aSub);
            Drawable drawable = a.getDrawable(R.styleable.TitleBar_aIcon);
            Drawable drawable1 = a.getDrawable(R.styleable.TitleBar_aMsg);
            a.recycle();

            mTitle.setText(title);
            mSubmit.setText(msg);

            mIcon.setImageDrawable(drawable);
            mMsg.setImageDrawable(drawable1);
        } else {
            mIcon.setVisibility(GONE);
        }

    }
    public void setTitle( String titleRes) {
        if (titleRes==null)
            return;
        mTitle.setVisibility(VISIBLE);
        mTitle.setText(titleRes);
    }

    public void setRightTitle( String titleRes) {
        if (titleRes==null)
            return;
        mSubmit.setText(titleRes);
    }
    @SuppressLint("ResourceType")
    public void setMsg(@StringRes int titleRes) {
        if (titleRes <= 0)
            return;
        mSubmit.setText(titleRes);
    }
    public void setMsgColor(String colorRes) {
        if (colorRes == "")
            return;
        mSubmit.setTextColor(Color.parseColor(colorRes));
    }
    public void setTipGone(boolean isGones) {
        if (isGones){
            mSubmit.setVisibility(GONE);
        }else {
            mSubmit.setVisibility(VISIBLE);
        }
    }
    @SuppressLint("ResourceType")
    public void setIcon(@DrawableRes int iconRes) {
        if (iconRes <= 0) {
            mIcon.setVisibility(GONE);
            back_ll.setVisibility(GONE);
            return;
        }
        mIcon.setImageResource(iconRes);
        mIcon.setVisibility(VISIBLE);
        back_ll.setVisibility(VISIBLE);
        mMsg.setVisibility(GONE);
    }
    @SuppressLint("ResourceType")
    public void setMsgIcon(@DrawableRes int iconRes) {
        if (iconRes <= 0) {
            mMsg.setVisibility(GONE);
            return;
        }
        mMsg.setImageResource(iconRes);
        mMsg.setVisibility(VISIBLE);
        mIcon.setVisibility(GONE);
    }
    public void setIconOnClickListener(OnClickListener listener) {
        back_ll.setOnClickListener(listener);
    }
    public void setMsgIconOnClickListener(OnClickListener listener) {
        mMsg.setOnClickListener(listener);
    }
    public void setTipOnClickListener(OnClickListener listener) {
        mSubmit.setOnClickListener(listener);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float d = getResources().getDisplayMetrics().density;
       // int minH = (int) (d * 53 + UiUtils.getStatusBarHeight(getContext()));
        int minH = (int) (d * 53 );

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(minH, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
