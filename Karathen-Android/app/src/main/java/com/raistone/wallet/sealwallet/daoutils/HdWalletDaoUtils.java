package com.raistone.wallet.sealwallet.daoutils;

import android.text.TextUtils;

import com.raistone.wallet.sealwallet.WalletApplication;
import com.raistone.wallet.sealwallet.datavases.ChainInfoDaoUtils;
import com.raistone.wallet.sealwallet.factory.AssetsDeatilInfo;
import com.raistone.wallet.sealwallet.factory.ChainAddressInfo;
import com.raistone.wallet.sealwallet.factory.ChainDataInfo;
import com.raistone.wallet.sealwallet.factory.HdWallet;
import com.raistone.wallet.sealwallet.greendao.ChainAddressInfoDao;
import com.raistone.wallet.sealwallet.greendao.HdWalletDao;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;


public class HdWalletDaoUtils {

    public static HdWalletDao hdWalletDao = WalletApplication.getsInstance().getDaoSession().getHdWalletDao();


    public static void insertNewWallet(HdWallet wallet) {
        hdWalletDao.insert(wallet);
    }

    public static List<HdWallet> findAllWallet() {
        List<HdWallet> hdWallets = hdWalletDao.loadAll();

        return hdWallets;

    }

    public static HdWallet findWalletBySelect() {

        QueryBuilder<HdWallet> queryBuilder = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.IsCurrent.eq(true));


        Query<HdWallet> build = queryBuilder.build();

        HdWallet addressInfo = build.unique();

        return addressInfo;

    }


    public static HdWallet updateCurrent(long id) {
        List<HdWallet> addressInfos = findAllWallet();
        if (addressInfos != null) {
            HdWallet hdWallet = null;
            for (HdWallet wallet : addressInfos) {
                if (id != -1 && wallet.getWalletId() == id) {
                    wallet.setIsCurrent(true);
                    hdWallet = wallet;
                } else {
                    wallet.setIsCurrent(false);
                }
                hdWalletDao.update(wallet);
            }
            return hdWallet;
        }
        return null;
    }


    public static HdWallet findAllWalletById(Long walletId) {
        HdWallet hdWallet = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.WalletId.eq(walletId)).unique();
        return hdWallet;

    }

    public static String getAccountId(Long walletId) {
        HdWallet load = hdWalletDao.load(walletId);
        String accountId = load.getAccountId();
        return accountId;
    }

    public static String getWalletPwd(Long walletId) {

        HdWallet walletInfo = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.WalletId.eq(walletId)).unique();

        String walletPwd = walletInfo.getWalletPwd();
        return walletPwd;
    }


    public static void updateWalletPwd(HdWallet hdWallet) {

        hdWalletDao.update(hdWallet);
    }

    public static void updateWallet(HdWallet hdWallet) {

        hdWalletDao.update(hdWallet);
    }

    public static List<HdWallet> deleteWalletById(Long walletId) {

        HdWallet hdWallet = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.WalletId.eq(walletId)).unique();
        List<HdWallet> hdWallets=new ArrayList<>();

        if (hdWallet != null) {

            if (hdWallet.getIsCurrent()) {

                hdWallets = findAllWallet();
                if (hdWallets != null && hdWallets.size() > 0) {
                    hdWallets.get(0).setIsCurrent(true);
                    hdWalletDao.update(hdWallets.get(0));
                    hdWalletDao.delete(hdWallet);
                }
            } else {


                hdWalletDao.delete(hdWallet);
            }

            List<ChainDataInfo> chainDataInfos = hdWallet.getChainDataInfos();

            if (chainDataInfos != null && chainDataInfos.size() > 0)
                for (ChainDataInfo infos : chainDataInfos) {
                    ChainDaoUtils.deleteDataById(infos);

                    List<ChainAddressInfo> addressInfos = infos.getChainAddressInfos();
                    if (chainDataInfos != null && chainDataInfos.size() > 0) {
                        for (ChainAddressInfo addressInfo : addressInfos) {
                            ChainAddressDaoUtils.deleteAddressById(addressInfo.getId());
                        }
                    }

                }
        }
        hdWallets.clear();

        hdWalletDao.detachAll();

        hdWallets=findAllWallet();

        if (hdWallet.getIsCurrent()) {
            if (hdWallets != null && hdWallets.size() > 0) {
                hdWallets.get(0).setIsCurrent(true);
                hdWalletDao.update(hdWallets.get(0));
            }
        }
        return hdWallets;

    }

    public static boolean checkRepeatByMenmonic(String mnemonic) {
        List<HdWallet> ethWallets = findAllWallet();

        for (HdWallet ethWallet : ethWallets) {
            if (!TextUtils.isEmpty(ethWallet.getMnemonic())) {
                if (TextUtils.equals(ethWallet.getMnemonic().trim(), mnemonic.trim())) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

}
