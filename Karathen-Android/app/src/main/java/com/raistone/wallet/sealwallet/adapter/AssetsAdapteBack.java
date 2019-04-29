package com.raistone.wallet.sealwallet.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.entity.AssetsInfo;
import com.raistone.wallet.sealwallet.utils.BigDecimalUtils;
import com.raistone.wallet.sealwallet.utils.SharePreUtil;

import java.math.BigDecimal;
import java.util.List;

public class AssetsAdapteBack extends BaseQuickAdapter<AssetsInfo.DataBean, BaseViewHolder> {
    public AssetsAdapteBack(@Nullable List<AssetsInfo.DataBean> data) {
        super(R.layout.assets_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AssetsInfo.DataBean item) {
        helper.setText(R.id.assets_name_tv, item.getTokenSynbol())
                .setText(R.id.token_name_tv, item.getTokenName())
                .addOnClickListener(R.id.more_ll);

        TextView balance_tv = helper.getView(R.id.assets_price_tv);

        String tokenDecimal = item.getTokenDecimal();
        if (!TextUtils.isEmpty(tokenDecimal)) {

            if (item.getBalance() != null) {


                BigDecimal balance = item.getBalance();


                if (!balance.equals(BigDecimal.ZERO)) {

                    BigDecimal bigRes = BigDecimalUtils.div(balance.toString(), String.valueOf(Math.pow(10, Double.parseDouble(tokenDecimal))), 8);

                    BigDecimal bigDecimal = new BigDecimal(subZeroAndDot(bigRes + ""));

                    balance_tv.setText(subZeroAndDot(bigDecimal + ""));
                } else {
                    balance_tv.setText("0");
                }


            } else {
                balance_tv.setText(item.getBalance() + "");
            }
        } else {
            balance_tv.setText(item.getBalance() + "");
        }


        TextView prive_tv = helper.getView(R.id.assets_rate_price_tv);


        Boolean unit = SharePreUtil.getBoolean(mContext, "CurrencyUnit", true);

        if (unit) {

            if (item.getPrice() != null && !item.getPrice().equals(BigDecimal.ZERO)) {


                if (item.getPriceFlag().equals(BigDecimal.ZERO)) {
                    prive_tv.setText("--");
                } else {

                    prive_tv.setText("¥ " + item.getPrice() + "");
                }

            } else {
                prive_tv.setText("--");
            }

        } else {
            if (item.getPriceUSD() != null && !item.getPrice().equals(BigDecimal.ZERO)) {

                if (item.getPriceFlag().equals(BigDecimal.ZERO)) {
                    prive_tv.setText("--");
                } else {
                    prive_tv.setText("$ " + item.getPriceUSD() + "");
                }

            } else {
                prive_tv.setText("--");
            }
        }

        ImageView imageView = helper.getView(R.id.assets_icon_iv);

        if (!TextUtils.isEmpty(item.getTokenIcon())) {
            Glide.with(mContext).load(item.getTokenIcon()).into(imageView);
        } else {
            String synbol = item.getTokenSynbol();
            if (synbol.equals("ETH")) {
                Glide.with(mContext).load(R.drawable.ehd_def_icon).into(imageView);
            } else if (synbol.equals("WBA")) {
                Glide.with(mContext).load(R.drawable.wba_def_icon).into(imageView);
            } else if (synbol.equals("WBT")) {
                Glide.with(mContext).load(R.drawable.wbt_def_icon).into(imageView);
            } else if (synbol.equals("TNC")) {
                Glide.with(mContext).load(R.drawable.tnc_def_icon).into(imageView);
            } else if (synbol.equals("USDT")) {
                Glide.with(mContext).load(R.drawable.usdt_def_icon).into(imageView);
            } else if (synbol.equals("GUSD")) {
                Glide.with(mContext).load(R.drawable.gusd_def_icon).into(imageView);
            } else if (synbol.equals("ONT")) {
                Glide.with(mContext).load(R.drawable.ont_def_icon).into(imageView);
            } else if (synbol.equals("ONG")) {
                Glide.with(mContext).load(R.drawable.ont_def_icon).into(imageView);
            } else if (synbol.equals("NEO")) {
                Glide.with(mContext).load(R.drawable.neo_def_icon).into(imageView);
            } else if (synbol.equals("IFOOD")) {
                Glide.with(mContext).load(R.drawable.ifood_def_icon).into(imageView);
            } else if (synbol.equals("PAX")) {
                Glide.with(mContext).load(R.drawable.pax_def_icon).into(imageView);
            } else if (synbol.equals("CK")) {
                Glide.with(mContext).load(R.drawable.cat_def_icon).into(imageView);
            } else if (synbol.equals("GAS")) {
                Glide.with(mContext).load(R.drawable.neo_def_icon).into(imageView);
            } else {
                Glide.with(mContext).load(R.drawable.eth_defalult_icon).into(imageView);
            }
        }
    }

    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

}
