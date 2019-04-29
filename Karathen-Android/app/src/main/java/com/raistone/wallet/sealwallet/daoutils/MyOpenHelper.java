package com.raistone.wallet.sealwallet.daoutils;

import android.content.Context;

import com.raistone.wallet.sealwallet.greendao.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        switch (oldVersion){
            case 1:
                db.execSQL("ALTER TABLE 'HD_WALLET' ADD 'KEYSTORE' TEXT;");
                break;
        }
    }
}
