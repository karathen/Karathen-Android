package com.raistone.wallet.sealwallet.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.widget.CustomProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class BaseFragment extends Fragment {

    private CustomProgressDialog progressDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    public void startProgressDialog() {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(getActivity());
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void startProgressDialog(boolean value) {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(getActivity());
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(value);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void startProgressDialog(String message) {
        try{
            if (progressDialog == null) {
                progressDialog = CustomProgressDialog.createDialog(getActivity());
                progressDialog.setMessage(message);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.show();
        }catch(Exception e){
        }

    }
    public void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public static void  toggleSoftInput(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0,0);
        }
    }
}
