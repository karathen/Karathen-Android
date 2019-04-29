package com.raistone.wallet.sealwallet.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chenenyu.router.annotation.Route;
import com.raistone.wallet.sealwallet.R;
import com.raistone.wallet.sealwallet.adapter.ChainAdapter;
import com.raistone.wallet.sealwallet.daoutils.ChainDaoUtils;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import org.greenrobot.eventbus.EventBus;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


@Route(value = "MultiChainManagerActivity")
public class MultiChainManagerActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.recyclerview)
    SwipeMenuRecyclerView recyclerview;

    private List<ChainDataInfo> allChains;

    private ChainAdapter chainAdapter;

    private int showCount=0;

    private HdWallet hdwallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_chain_manager);
        ButterKnife.bind(this);
        setTitle(titleBar, getResources().getString(R.string.multi_chain_management), true);

        hdwallet= (HdWallet) getIntent().getSerializableExtra("hdwallet");

        ActivityManager.getInstance().pushActivity(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerview.setLayoutManager(layoutManager);

        recyclerview.setLongPressDragEnabled(true);

        recyclerview.setOnItemMoveListener(onItemMoveListener);

        initData();


        for (int i=0;i<allChains.size();i++){
            boolean show = allChains.get(i).getIsShow();

            if(!show){
                showCount++;
            }

        }

    }

    public void initData() {

        allChains=ChainDaoUtils.findAllChainByWalletId(hdwallet.getWalletId());
        chainAdapter = new ChainAdapter(allChains);

        recyclerview.setAdapter(chainAdapter);

        chainAdapter.setOnItemClickListener(this);
        chainAdapter.setOnItemChildClickListener(this);
    }



    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        ImageView iconView = view.findViewById(R.id.off_iv);

        ChainDataInfo chainInfo = allChains.get(position);

        boolean show = chainInfo.getIsShow();

        if (show) {
            showCount++;
            if(showCount>=allChains.size()){
                showCount--;
                ToastHelper.showToast(getResources().getString(R.string.show_at_least_one_chain));
                return;
            }
            chainInfo.setIsShow(false);
            iconView.setBackground(getResources().getDrawable(R.drawable.close_icon));
        } else {
            chainInfo.setIsShow(true);
            iconView.setBackground(getResources().getDrawable(R.drawable.open_icon));
            showCount--;
        }


        ChainDaoUtils.updateOrder(chainInfo);

        EventBus.getDefault().post("Msg");

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        for (ChainDataInfo inf : allChains) {
            inf.setIsSelect(false);
        }

        ChainDataInfo chainInfo = allChains.get(position);
        chainInfo.setIsSelect(true);
        adapter.notifyDataSetChanged();
    }


    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();

            ChainDataInfo fromChainInfo = allChains.get(fromPosition);


            int toPosition = targetHolder.getAdapterPosition();

            ChainDataInfo toChainInfo = allChains.get(toPosition);

            toChainInfo.setOrderInfo(fromPosition);
            toChainInfo.update();

            ChainDaoUtils.updateOrder(toChainInfo);

            fromChainInfo.setOrderInfo(toPosition);
            fromChainInfo.update();

            ChainDaoUtils.updateOrder(fromChainInfo);


            Collections.swap(allChains, fromPosition, toPosition);
            chainAdapter.notifyItemMoved(fromPosition, toPosition);

            EventBus.getDefault().post("Msg");

            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();
            allChains.remove(position);
            chainAdapter.notifyItemRemoved(position);
        }

    };
}
