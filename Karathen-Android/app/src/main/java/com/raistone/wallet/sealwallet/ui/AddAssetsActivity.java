package com.raistone.wallet.sealwallet.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.AssetsAddressAdapter;
import com.raistone.wallet.sealwallet.daoutils.AssetsDetailDaoUtils;
import com.raistone.wallet.sealwallet.entity.AddAssetsInfo;
import com.raistone.wallet.sealwallet.entity.ETHResponseInfo;
import com.raistone.wallet.sealwallet.entity.MessageEvent;
import com.raistone.wallet.sealwallet.entity.PriceInfo;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.raistone.wallet.sealwallet.httputils.SealApi;
import com.raistone.wallet.sealwallet.utils.CommonUtils;
import com.raistone.wallet.sealwallet.utils.Constant;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.LocalManageUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 添加资产
 */

@Route(value = "AddAssetsActivity")
public class AddAssetsActivity extends AppCompatActivity implements TextWatcher, BaseQuickAdapter.OnItemChildClickListener,BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.assets_name_ed)
    EditText assetsNameEd;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.ic_back)
    ImageView icBack;
    @BindView(R.id.back_ll)
    RelativeLayout backLl;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String coinType = "ETH";

    private AssetsAddressAdapter assetsAddressAdapter;

    private List<AddAssetsInfo.ResultBean> datas = new ArrayList<>();


    private ChainAddressInfo ethWallet;

    public String addresss;

    //根据地址查询所有资产信息
    List<AssetsDeatilInfo> localAsstes;

    //private AssetsInfo.DataBean insertData;

    private AssetsDeatilInfo insertData;

    ArrayList<String> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assets);
        ButterKnife.bind(this);

        ethWallet = (ChainAddressInfo) getIntent().getSerializableExtra("ethWallet");

        coinType = ethWallet.getCoinType();

        addresss = ethWallet.getAddress();

        ActivityManager.getInstance().pushActivity(this);


        //localAsstes = AssetsDaoUtils.findAllAssetsByWalletAddress(addresss);

        localAsstes= AssetsDetailDaoUtils.findAllAssetsByAddress(addresss);


        assetsNameEd.addTextChangedListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerview.setLayoutManager(layoutManager);

        assetsAddressAdapter = new AssetsAddressAdapter(datas);

        assetsAddressAdapter.setOnItemChildClickListener(this);
        assetsAddressAdapter.setOnItemClickListener(this);
        recyclerview.setAdapter(assetsAddressAdapter);

    }

    @OnClick(R.id.back_ll)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String input = s.toString();

        if(!TextUtils.isEmpty(input)) {
            addAssets(input, coinType);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 添加资产
     *
     * @param assetsName
     * @param coinType
     */
    public void addAssets(String assetsName, String coinType) {

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(assetsName);
        arrayList.add(coinType);

        RequestBody tokenInfo = SealApi.toRequestBody("getTokenInfo_2", arrayList);

        EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.BASE_MAIN_URL).post("/")
                .requestBody(tokenInfo)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        //showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(String response) {

                        AddAssetsInfo assetsInfo = GsonUtils.decodeJSON(response, AddAssetsInfo.class);

                        datas = assetsInfo.getResult();

                        if (datas != null && datas.size() > 0) {

                            isAdd(localAsstes, datas);
                        } else {
                            assetsAddressAdapter.setNewData(datas);
                            assetsAddressAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    public void isAdd(List<AssetsDeatilInfo> localAsstes, List<AddAssetsInfo.ResultBean> infoResult) {

        boolean flag = false;//默认是没有添加
        for (int i = 0; i < infoResult.size(); i++) {
            //实时数据
            AddAssetsInfo.ResultBean bean = infoResult.get(i);
            String lowerCase = bean.getTokenAddress().toLowerCase();

            //本地数据
            for (int j = 0; j < localAsstes.size(); j++) {
                AssetsDeatilInfo dataBean = localAsstes.get(j);

                String tokenAddress = dataBean.getTokenAddress().toLowerCase();

                //代表查到该条数据
                if (tokenAddress.equals(lowerCase) && dataBean.getStatus()==0) {
                    flag = true;
                    break;
                } else {
                    flag = false;
                }

            }
            if (flag) {
                bean.setAddFlag(0);
            } else {
                bean.setAddFlag(1);
            }

        }

        assetsAddressAdapter.setNewData(infoResult);
        assetsAddressAdapter.notifyDataSetChanged();
    }

    AssetsDeatilInfo addressIsEx;
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.assets_add_tv:

                TextView textView=view.findViewById(R.id.assets_add_tv);

                String tips = textView.getText().toString();

                if(tips.equals("已添加") || tips.equals("Added")){
                    return;
                }

                String accountId = ethWallet.getAccountId();

                AddAssetsInfo.ResultBean bean = datas.get(position);

                String toLowerCase = bean.getTokenAddress().toLowerCase();


                addressIsEx = AssetsDetailDaoUtils.addressIsEx(ethWallet.getAddress(),toLowerCase, accountId);

                if(addressIsEx!=null){
                    //ToastHelper.showToast("有这条记录");
                    addressIsEx.setStatus(0);
                    AssetsDetailDaoUtils.updatePrice(addressIsEx);

                    addressList.clear();

                    datas.get(position).setAddFlag(0);

                    addressList.add(datas.get(position).getTokenName());

                    ethWallet.resetAssetsInfoDataList();

                    //EventBus.getDefault().post(ethWallet);
                    EventBus.getDefault().post(new MessageEvent(addresss));


                    //findAsseterMoney(datas.get(position).getTokenAddress());
                    //getPrice_2(addressList);

                    assetsAddressAdapter.setNewData(datas);

                    assetsAddressAdapter.notifyDataSetChanged();

                    return;
                }else {
                    addressList.clear();

                    insertData = new AssetsDeatilInfo();

                    datas.get(position).setAddFlag(0);

                    addressList.add(datas.get(position).getTokenName());


                    AddAssetsInfo.ResultBean resultBean = datas.get(position);

                    insertData.setBalance("0");
                    insertData.setPrice("0");
                    insertData.setPriceFlag("0");

                    insertData.setPriceUSD("0");
                    insertData.setPriceUsdFlag("0");

                    insertData.setTokenAddress(resultBean.getTokenAddress());

                    insertData.setTokenDecimal(resultBean.getTokenDecimal());


                    insertData.setTokenIcon(resultBean.getTokenIcon());

                    insertData.setChainAddressId(ethWallet.getId());

                    insertData.setAsset_name(ethWallet.getAccountId());

                    insertData.setTokenName(resultBean.getTokenName());
                    insertData.setTokenSynbol(resultBean.getTokenSynbol());

                    insertData.setTokenType(resultBean.getTokenType());

                    insertData.setWalletAddress(addresss);
                    insertData.setCoinType(coinType);

                    //insertData.insert();

                    AssetsDetailDaoUtils.insertNewAssets(insertData);

                    ethWallet.resetAssetsInfoDataList();

                    //EventBus.getDefault().post(ethWallet);
                    EventBus.getDefault().post(new MessageEvent(addresss));


                    findAsseterMoney(datas.get(position).getTokenAddress());
                    getPrice_2(addressList);

                    assetsAddressAdapter.setNewData(datas);

                    assetsAddressAdapter.notifyDataSetChanged();
                }




                break;
        }
    }


    /**
     * 查询资产余额
     */
    // TODO: 2018/11/13
    public void findAsseterMoney(String tokenAddress) {
        String addre = ethWallet.getAddress();
        EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.MAIN_URL).get("api")
                .addConverterFactory(GsonConverterFactory.create())
                .params("module", "account")
                .params("action", "tokenbalance")
                .params("address", addre)
                .params("contractaddress", tokenAddress)
                .params("tag", "latest")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String s) {
                        ETHResponseInfo responseInfo = GsonUtils.decodeJSON(s, ETHResponseInfo.class);
                        if (responseInfo.getStatus().equals("1")) {
                            String result = responseInfo.getResult();

                            if(addressIsEx!=null){
                                addressIsEx.setBalance(result);

                                AssetsDetailDaoUtils.updatePrice(addressIsEx);
                            }else {
                                insertData.setBalance(result);

                                AssetsDetailDaoUtils.updatePrice(insertData);
                            }

                            //insertData.update();
                        }
                    }
                });
    }


    /**
     * 获取代币价格
     */
    public void getPrice_2(List<String> tokenSynbos) {

        String[] params = tokenSynbos.toArray(new String[tokenSynbos.size()]);
        String[][] newStrings = new String[1][];
        newStrings[0] = params;

        final RequestBody body = HttpUtils.toRequestBody("getPrice_2", newStrings);


        EasyHttp.getInstance().setBaseUrl(Constant.HttpServiceUrl.BASE_MAIN_URL).post("/")
                .addConverterFactory(GsonConverterFactory.create())
                .requestBody(body).execute(new SimpleCallBack<String>() {
            @Override
            public void onError(ApiException e) {

            }

            @Override
            public void onSuccess(String content) {
                PriceInfo priceInfo = GsonUtils.decodeJSON(content, PriceInfo.class);

                if (priceInfo != null) {
                    if (priceInfo.getResult() != null && priceInfo.getResult().size() > 0) {
                        PriceInfo.ResultBean bean = priceInfo.getResult().get(0);

                        if(addressIsEx!=null){
                            addressIsEx.setPrice(bean.getCny_price().toPlainString());
                            addressIsEx.setPriceFlag(bean.getCny_price().toPlainString());

                            addressIsEx.setPriceUSD(bean.getUsd_price().toPlainString());
                            addressIsEx.setPriceUsdFlag(bean.getUsd_price().toPlainString());

                            addressIsEx.setWalletAddress(addresss);
                            addressIsEx.setCoinType(coinType);


                            AssetsDetailDaoUtils.updatePrice(addressIsEx);
                        }else {

                            insertData.setPrice(bean.getCny_price().toPlainString());
                            insertData.setPriceFlag(bean.getCny_price().toPlainString());

                            insertData.setPriceUSD(bean.getUsd_price().toPlainString());
                            insertData.setPriceUsdFlag(bean.getUsd_price().toPlainString());

                            insertData.setWalletAddress(addresss);
                            insertData.setCoinType(coinType);


                            AssetsDetailDaoUtils.updatePrice(insertData);
                        }

                        //insertData.update();

                    }
                }
            }
        });


    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        CommonUtils.copy(datas.get(position).getTokenAddress(),this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }

}
