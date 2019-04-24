
/**************************************************************************************
* [Project]
*       MyProgressDialog
* [Package]
*       com.lxd.widgets
* [FileName]
*       CustomProgressDialog.java
* [Copyright]
*       Copyright 2012 LXD All Rights Reserved.
* [History]
*       Version          Date              Author                        Record
*--------------------------------------------------------------------------------------
*       1.0.0           2012-4-27         lxd (rohsuton@gmail.com)        Create
**************************************************************************************/
	
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
    //旋转动画

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
        //加载动画资源
        animation = AnimationUtils.loadAnimation(context,R.anim.loading);
        //动画完成后，是否保留动画最后的状态，设为true
      //  animation.setFillAfter(true);
        return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	if (customProgressDialog == null){
    		return;
    	}
//
//        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile ����
     * @param strTitle
     * @return
     *
     */
    public CustomProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage ��ʾ����
     * @param strMessage
     * @return
     *
     */
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
    /**
     * 在AlertDialog的onStop()生命周期里面执行停止动画
     */
    @Override
    protected void onStop() {
        super.onStop();

        imageView.clearAnimation();
    }
}

