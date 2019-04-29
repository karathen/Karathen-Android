package com.raistone.wallet.sealwallet.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.raistone.wallet.sealwallet.R;


public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
    private static Animation animation;
    private static ImageView imageView;

    public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	public static CustomProgressDialog createDialog(Context context){
		customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.customprogressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        animation = AnimationUtils.loadAnimation(context,R.anim.loading);
        return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	if (customProgressDialog == null){
    		return;
    	}

    }
 

    public CustomProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }

    public CustomProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	return customProgressDialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( animation != null) {
            imageView.startAnimation(animation);
        }
        }

    @Override
    protected void onStop() {
        super.onStop();

        imageView.clearAnimation();
    }
}

