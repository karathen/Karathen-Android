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

    /**
     * 插入新数据
     *
     * @param wallet 新创建钱包
     */
    public static void insertNewWallet(HdWallet wallet) {
        hdWalletDao.insert(wallet);
    }


    /**
     * 查询所有钱包
     */
    public static List<HdWallet> findAllWallet() {
        List<HdWallet> hdWallets = hdWalletDao.loadAll();

        return hdWallets;

    }


    /**
     * 获取当前选中钱包
     */
    public static HdWallet findWalletBySelect() {
        //List<HdWallet> hdWallets = hdWalletDao.loadAll();

        QueryBuilder<HdWallet> queryBuilder = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.IsCurrent.eq(true));


        Query<HdWallet> build = queryBuilder.build();

        HdWallet addressInfo = build.unique();

        return addressInfo;

    }


    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
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

    /**
     * 根据钱包Id查询钱包
     *
     * @param walletId 钱包ID
     */
    public static HdWallet findAllWalletById(Long walletId) {
        HdWallet hdWallet = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.WalletId.eq(walletId)).unique();
        return hdWallet;

    }

    /**
     * 获取账户id
     */
    public static String getAccountId(Long walletId) {
        HdWallet load = hdWalletDao.load(walletId);
        String accountId = load.getAccountId();
        return accountId;
    }

    /**
     * 根据账户Id 查询钱包信息
     *
     * @param accountId 账户id
     */
    public static HdWallet findWalletByAccount(String accountId) {

        HdWallet walletInfo = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.AccountId.eq(accountId)).unique();
        return walletInfo;
    }

    /**
     * 获取钱包密码
     */
    public static String getWalletPwd(Long walletId) {

        HdWallet walletInfo = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.WalletId.eq(walletId)).unique();

        String walletPwd = walletInfo.getWalletPwd();
        return walletPwd;
    }

    /**
     * 修改钱包密码
     */
    public static void updateWalletPwd(HdWallet hdWallet) {

        hdWalletDao.update(hdWallet);
    }

    /**
     * 修改钱包密码
     */
    public static void updateWallet(HdWallet hdWallet) {

        hdWalletDao.update(hdWallet);
    }

    /**
     * 删除所有钱包信息
     */
    public static void deleteAllData() {
        //SQLite.delete(WalletInfo.class);
        hdWalletDao.deleteAll();
    }

    /**
     * 删除钱包
     */
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

    /**
     * 删除钱包
     */
    public static void deleteWalletByIdTwo(Long walletId) {

        HdWallet hdWallet = hdWalletDao.queryBuilder().where(HdWalletDao.Properties.WalletId.eq(walletId)).unique();

        if (hdWallet != null) {

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

    }

    /**
     * 以助记词检查钱包是否存在
     *
     * @param mnemonic
     * @return
     */
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
