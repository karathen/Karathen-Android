package com.raistone.wallet.sealwallet.datavases;

import com.raistone.wallet.sealwallet.entity.ETHWallet;
import com.raistone.wallet.sealwallet.entity.TransferInfo;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Database(name=AppDataBase.NAME,version = AppDataBase.VSERSION)
public class AppDataBase {
    public static final int VSERSION=2;
    public static final String NAME="seal_wallet";

    @Migration(version = 2,database = AppDataBase.class)
    public static class Migration2 extends AlterTableMigration<TransferInfo> {

        public Migration2(Class<TransferInfo> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            super.onPreMigrate();
            addColumn(SQLiteType.TEXT,"nonce");
        }
    }
}
