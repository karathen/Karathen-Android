package com.raistone.wallet.sealwallet.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chenenyu.router.Router;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.ui.ImportWalletNewsActivity;
import com.raistone.wallet.sealwallet.utils.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * HD钱包
 */
public class HDWalletFragment extends BaseFragment {
    @BindView(R.id.imageView1)
    ImageView imageView1;
    @BindView(R.id.tips_id)
    LinearLayout tipsId;
    @BindView(R.id.ll_index)
    LinearLayout llIndex;
    @BindView(R.id.create_btn)
    Button createBtn;
    @BindView(R.id.import_wallet_tv)
    TextView importWalletTv;
    Unbinder unbinder;

    boolean isAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hd_wallet, container, false);
        unbinder = ButterKnife.bind(this, view);

        isAdd=getActivity().getIntent().getBooleanExtra("isAdd",false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.create_btn, R.id.import_wallet_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            /**
             * 创建钱包
             */
            case R.id.create_btn:
                Router.build("CreateWalletNewsActivity").with("walletType",0).with("isAdd",isAdd).go(this);
                break;
            /**
             * 导入钱包
             */
            case R.id.import_wallet_tv:
                Router.build("ImportWalletNewsActivity").with("walletType",0).with("isAdd",isAdd).go(this);
                break;
        }
    }
}
