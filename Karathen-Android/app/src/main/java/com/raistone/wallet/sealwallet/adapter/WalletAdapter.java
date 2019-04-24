package com.raistone.wallet.sealwallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenenyu.router.Router;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.MultiChainInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.ui.WalletAddressManageActivity;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.ToastHelper;

import java.util.List;

public class WalletAdapter extends PagerAdapter {

    private Context context;
    private boolean unitFlag;

    private List<ChainAddressInfo> datas;

    public WalletAdapter(Context context,List<ChainAddressInfo> datas, boolean unitFlag) {
        this.context = context;
        this.datas = datas;
        this.unitFlag = unitFlag;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.one_item_layout, null);

        TextView walletName = view.findViewById(R.id.wallet_name_tv);

        TextView price_tv = view.findViewById(R.id.price_tv);
        TextView get_tv = view.findViewById(R.id.get_tv);

        RelativeLayout one_item_layout = view.findViewById(R.id.one_item_layout);

        //price_tv.setText(price);

        final TextView address_tv = view.findViewById(R.id.address_tv);

        LinearLayout copy_iv = view.findViewById(R.id.copy_ll);

        final ChainAddressInfo wallet = datas.get(position);

        if (wallet != null) {
            walletName.setText(wallet.getName());
            address_tv.setText(wallet.getAddress());
            if(unitFlag) {
                price_tv.setText("￥ " + AssetsAdapter.subZeroAndDot(wallet.getCnyTotalPrice()));
            }else {
                price_tv.setText("$ " + AssetsAdapter.subZeroAndDot(wallet.getUsdtTotalPrice()));
            }
            copy_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtils.copyString(address_tv);
                }
            });
            String coinType = wallet.getCoinType();

            if (coinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {
                one_item_layout.setBackground(context.getResources().getDrawable(R.drawable.eth_bg_icon));
                get_tv.setVisibility(View.GONE);
            }
            if (coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {
                one_item_layout.setBackground(context.getResources().getDrawable(R.drawable.crad_bg_top_icon));
                get_tv.setText(context.getResources().getString(R.string.claim_gas_string));
                get_tv.setVisibility(View.VISIBLE);
            }
            if (coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {
                one_item_layout.setBackground(context.getResources().getDrawable(R.drawable.crad_bg_center_icon));
                get_tv.setText(context.getResources().getString(R.string.claim_ong_string));
                get_tv.setVisibility(View.VISIBLE);
            }

        }

        //RelativeLayout ll=view.findViewById(R.id.one_item_layout);
        one_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //WalletAddressManageActivity
                Router.build("WalletAddressManageActivity").with("ethWallet", wallet).requestCode(1).go(context);
            }
        });
        get_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coinType = wallet.getCoinType();
                if (coinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {
                    //ToastHelper.showToast("提取 GAS");
                    Router.build("ClaimActivity").with("ethWallet", wallet).requestCode(1).go(context);
                    return;
                }
                if (coinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {
                    //ToastHelper.showToast("提取 ONT");
                    Router.build("ClaimActivity").with("ethWallet", wallet).requestCode(1).go(context);
                    return;
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // container.removeView(mViews.get(position));
        /*container.removeView((View) object);*/
        RelativeLayout view = (RelativeLayout) object;
        container.removeView(view);
    }

    public void changUnit(boolean unit){
        this.unitFlag=unit;
        this.notifyDataSetChanged();
    }
}
