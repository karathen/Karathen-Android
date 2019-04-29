package com.raistone.wallet.sealwallet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.utils.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class HardwareWalletFragment extends BaseFragment {
    @BindView(R.id.imageView1)
    ImageView imageView1;
    @BindView(R.id.tips_id)
    LinearLayout tipsId;
    @BindView(R.id.ll_index)
    LinearLayout llIndex;
    @BindView(R.id.create_btn)
    Button createBtn;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hardware_wallet, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.create_btn)
    public void onViewClicked() {
        ToastHelper.showToast(getResources().getString(R.string.stay_tuned));
    }
}
