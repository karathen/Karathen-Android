package com.raistone.wallet.sealwallet.utils;

        import android.content.ClipData;
        import android.content.ClipboardManager;
        import android.content.Context;
        import android.content.res.AssetManager;
        import android.content.res.Resources;
        import android.graphics.drawable.Drawable;
        import android.support.v4.content.ContextCompat;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.ViewParent;
        import android.widget.TextView;


        import com.raistone.wallet.sealwallet.R;
        import com.raistone.wallet.sealwallet.WalletApplication;
        import com.raistone.wallet.sealwallet.ui.MainActivity;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.math.BigDecimal;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;


public class CommonUtils {

    /**
     * 得到年月日的"日"
     */
    private String getDate() {
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("dd");
        return dateFm.format(date);
    }

    public static Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(WalletApplication.getAppContext(), resId);
    }

    public static int getColor(int resId) {
        return getResource().getColor(resId);
    }

    public static Resources getResource() {
        return WalletApplication.getAppContext().getResources();
    }

    public static String[] getStringArray(int resId) {
        return getResource().getStringArray(resId);
    }

    public static String getString(int resId) {
        return getResource().getString(resId);
    }

    public static float getDimens(int resId) {
        return getResource().getDimension(resId);
    }

    public static void removeSelfFromParent(View child) {

        if (child != null) {

            ViewParent parent = child.getParent();

            if (parent instanceof ViewGroup) {

                ViewGroup group = (ViewGroup) parent;

                group.removeView(child);
            }
        }
    }

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copy(String content, Context context) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //cmb.setText(content.trim());
        if (cmb != null) {
            cmb.setPrimaryClip(ClipData.newPlainText(null, content.trim()));
            ToastHelper.showToast(getResource().getString(R.string.copy_success_string));
        }
    }

    /**
     * 实现粘贴功能
     * add by wangqianzhou
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);


        return cmb.getText().toString().trim();
    }

    public static void copyString(TextView textView) {
        ClipboardManager cm = (ClipboardManager) textView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, textView.getText()));
            ToastHelper.showToast(getResource().getString(R.string.copy_success_string));
        }
    }

    public static void copyString(TextView textView, String message) {
        ClipboardManager cm = (ClipboardManager) textView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, message));
            ToastHelper.showToast(getResource().getString(R.string.copy_success_string));
        }
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 16进制转换
     *
     * @param converStr 16进制字符
     */
    public static Long binaryConversion(String converStr) {

        String substring = converStr.substring(2);
        //转换后的gasPrice
        long parseLong = Long.parseLong(substring, 16);

        return parseLong;
    }


    /**
     * 除以10 N 次方
     */
    public static String getDecimalValue(String value,int tokenDecimal){

        double powThree = Math.pow(10, -tokenDecimal);

        BigDecimal decimal=new BigDecimal(value);

        decimal.divide(new BigDecimal(powThree));

        return decimal.toString();

    }


    /**
     * 获取当前时间
     * @return
     */
    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }

    /**
     * 时间戳转时间
     */
    public static String conversionTime(String timeData){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long date_temp = Long.valueOf(timeData);
        String date_string = sdf.format(new Date(date_temp * 1000L));


       return date_string;
    }





    /**
     * 获取转换后的balance
     * value 要转换的值
     * decimal 精度
     */
    public static String getBalance(BigDecimal value,String decimal){
        BigDecimal balance = BigDecimalUtils.div(value.toString(), String.valueOf(Math.pow(10, Double.parseDouble(decimal))));
        return balance.toString();
    }

    public static void main(String[] args){
        Long aLong = binaryConversion("0x1");
        System.out.println(aLong);
    }

}
