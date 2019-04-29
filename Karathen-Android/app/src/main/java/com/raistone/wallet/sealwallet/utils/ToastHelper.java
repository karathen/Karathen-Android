package com.raistone.wallet.sealwallet.utils;

import com.raistone.wallet.sealwallet.WalletApplication;
import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
	private static Toast mToast = null;
	private static Context context = null;
	private static final int DEFAULT_TOAST_DURATION = Toast.LENGTH_SHORT;

	public static void showToast(int resId) {
		if (resId <= 0) {
			return;
		}

		showToast(getContext().getResources().getString(resId), DEFAULT_TOAST_DURATION);
	}

	public static void showToast(String tips) {
		if (tips == null) {
			return;
		}

		showToast(tips, DEFAULT_TOAST_DURATION);
	}

	public static void showToast(int resId, int duration) {
		if (resId <= 0) {
			return;
		}
		if (duration <= 0) {
			duration = DEFAULT_TOAST_DURATION;
		}
		showToast(getContext().getResources().getString(resId), duration);
	}

	public static void showToast(String tips, int duration) {
		if (tips == null) {
			return;
		}

		if (mToast == null) {
			mToast = Toast.makeText(getContext(), tips, duration);
		} else {
			mToast.setText(tips);
		}
		mToast.show();
	}

	public static void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	private static Context getContext() {
		if (context == null) {
			context = WalletApplication.getAppContext();
		}
		return context;
	}

}