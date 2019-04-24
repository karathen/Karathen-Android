package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.TransferDetailInfo;
import com.raistone.wallet.sealwallet.ui.GuideActivity;
import com.raistone.wallet.sealwallet.ui.MainActivity;
import com.raistone.wallet.sealwallet.ui.SplashActivity;
import com.raistone.wallet.sealwallet.ui.WalletItemDetailActivity;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.MultiChainCreateManager;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;

import java.math.BigDecimal;
import java.util.List;

public class TransferAdapter extends BaseQuickAdapter<TransferDetailInfo.ResultBean, BaseViewHolder> {

    public TransferAdapter(@Nullable List<TransferDetailInfo.ResultBean> data) {
        super(R.layout.transfer_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TransferDetailInfo.ResultBean item) {
        helper.setText(R.id.content_tv, item.getTxId())
                //.setText(R.id.value_tv,item.getValue())
                .setText(R.id.time_tv, CommonUtils.conversionTime(item.getBlockTime()))
                .setText(R.id.status_tv, item.getTxReceiptStatus());

        TextView view = helper.getView(R.id.status_tv);

        String tokenDecimal = WalletItemDetailActivity.assetsTokenDecimal;//精度

        String tokenSynbol = WalletItemDetailActivity.assetsTokenSynbol;//币种
        String tokenType = WalletItemDetailActivity.assetsTokenType;//币种


        //value_tv


        ImageView icView = helper.getView(R.id.icon_iv);

        String addressTo = item.getAddressFrom();

        TextView tvValue = helper.getView(R.id.value_tv);

        String value = item.getValue();

        //如果精度为空代表是721资产
        if (!TextUtils.isEmpty(tokenDecimal)) {

            double value2 = Math.pow(10, Double.parseDouble(tokenDecimal));//币种精度

            //代表是出
            if (!WalletItemDetailActivity.walletsAddress.toLowerCase().equals(addressTo.toLowerCase())) {
                Glide.with(mContext).load(R.drawable.out_icon).into(icView);

                if (TextUtils.isEmpty(tokenDecimal)) {

                    tvValue.setText("+ " + AssetsAdapter.subZeroAndDot(item.getValue()) + " " + tokenSynbol);

                } else {

                    if(tokenType.equals("NEP-5")){
                        tvValue.setText("+ " + AssetsAdapter.subZeroAndDot(item.getValue()) + " " + tokenSynbol);
                    }else {

                        BigDecimal bigDecimal = BigDecimalUtils.div(value, value2 + "");

                        tvValue.setText("+ " + AssetsAdapter.subZeroAndDot(bigDecimal.toPlainString()) + " " + tokenSynbol);
                    }
                }

            } else {
                Glide.with(mContext).load(R.drawable.in_icon).into(icView);

                if (TextUtils.isEmpty(tokenDecimal)) {

                    tvValue.setText("- " + AssetsAdapter.subZeroAndDot(item.getValue()) + " " + tokenSynbol);
                } else {


                    if(tokenType.equals("NEP-5")){
                        tvValue.setText("- " + AssetsAdapter.subZeroAndDot(item.getValue()) + " " + tokenSynbol);
                    }else {

                        BigDecimal bigDecimal = BigDecimalUtils.div(value, value2 + "");

                        tvValue.setText("- " + AssetsAdapter.subZeroAndDot(bigDecimal.toPlainString()) + " " + tokenSynbol);
                    }

                }
            }
        } else {
            //代表是出
            if (!WalletItemDetailActivity.walletsAddress.toLowerCase().equals(addressTo.toLowerCase())) {


                Glide.with(mContext).load(R.drawable.in_icon).into(icView);


                tvValue.setText("- " + AssetsAdapter.subZeroAndDot(item.getValue()) + " " + tokenSynbol);


            } else {

                Glide.with(mContext).load(R.drawable.out_icon).into(icView);
                tvValue.setText("+ " + AssetsAdapter.subZeroAndDot(item.getValue()) + " " + tokenSynbol);

            }
        }

        Long number = SharePreUtil.getLongValue(mContext, "blockNumber", 0l);

        long blockNumber = (number - Long.parseLong(item.getBlockNumber())) + 1;

        if (WalletItemDetailActivity.assetsCoinType.equals(MultiChainCreateManager.ETH_COIN_TYPE)) {
            if (item.getTxReceiptStatus().equals("1")) {

                if (blockNumber >= 12) {
                    view.setText(mContext.getResources().getString(R.string.success_string));
                } else {
                    view.setText(blockNumber + "/12");
                }

            } else {
                view.setText(mContext.getResources().getString(R.string.failure_string));

            }
        }

        if (WalletItemDetailActivity.assetsCoinType.equals(MultiChainCreateManager.NEO_COIN_TYPE)) {

            if (blockNumber >= 12) {
                view.setText(mContext.getResources().getString(R.string.success_string));
            } else {
                view.setText(blockNumber + "/12");
            }

        }

        if (WalletItemDetailActivity.assetsCoinType.equals(MultiChainCreateManager.ONT_COIN_TYPE)) {

            if (Long.parseLong(item.getBlockNumber()) != 0) {
                view.setText(mContext.getResources().getString(R.string.success_string));
            } else {
                view.setText(blockNumber + "/1");
            }

        }
    }
}
