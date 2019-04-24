package com.raistone.wallet.sealwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.chenenyu.router.annotation.Route;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.raistone.wallet.sealwallet.entity.NeoResultInfo;
import com.raistone.wallet.sealwallet.entity.RemarkInfo;
import com.raistone.wallet.sealwallet.httputils.HttpUtils;
import com.raistone.wallet.sealwallet.ui.ActivityManager;
import com.raistone.wallet.sealwallet.ui.BaseActivity;
import com.raistone.wallet.sealwallet.utils.GsonUtils;
import com.raistone.wallet.sealwallet.utils.LogUtils;
import com.raistone.wallet.sealwallet.utils.SealUtils;
import com.raistone.wallet.sealwallet.utils.StatusBarUtil;
import com.raistone.wallet.sealwallet.utils.ToastHelper;
import com.raistone.wallet.sealwallet.widget.TitleBar;
import com.unistrong.yang.zb_permission.Permission;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPickUtils;
import me.iwf.photopicker.widget.MultiPickResultView;
import okhttp3.RequestBody;

/**
 * 技术支持
 */

@Route(value = "TechnicalSupportActivity")
public class TechnicalSupportActivity extends BaseActivity implements View.OnTouchListener {

    @BindView(R.id.titleBar)
    TitleBar titleBar;
    @BindView(R.id.em_tv)
    TextView emTv;
    @BindView(R.id.email_ed)
    EditText emailEd;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_ed)
    EditText titleEd;
    @BindView(R.id.phone_tv)
    TextView phoneTv;
    @BindView(R.id.phone_ed)
    EditText phoneEd;
    @BindView(R.id.depict_tv)
    TextView depictTv;
    @BindView(R.id.depict_ed)
    EditText depictEd;
    @BindView(R.id.commit_btn)
    Button commitBtn;
    @BindView(R.id.select_iv)
    ImageView selectIv;
    @BindView(R.id.recycler_view)
    MultiPickResultView recyclerView;


    private String tokenInfo;

    private String uploadUrl;

    private List<String> results = new ArrayList<>();


    private String emailValue;

    private String telephoneValue;

    private String titleValue;

    private String contentValue;


    private final int REQUEST_PHON = 500;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_support);
        ButterKnife.bind(this);
        //StatusBarUtil.setTransparent(this);

        ActivityManager.getInstance().pushActivity(this);

        setTitle(titleBar, getString(R.string.technical_support_string), true);
        depictEd.setOnTouchListener(this);

        context = this;

        recyclerView.init(TechnicalSupportActivity.this, MultiPickResultView.ACTION_SELECT, null);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        results.clear();
        recyclerView.onActivityResult(requestCode, resultCode, data);

        PhotoPickUtils.onActivityResult(requestCode, resultCode, data, new PhotoPickUtils.PickHandler() {

            //选择成功
            @Override
            public void onPickSuccess(ArrayList<String> photos, int requestCode) {
                if (photos != null) {

                }
            }

            //没有选择
            @Override
            public void onPreviewBack(ArrayList<String> photos, int requestCode) {
                //ToastHelper.showToast("选择返回"+photos.size());
            }

            //选择失败
            @Override
            public void onPickFail(String error, int requestCode) {
                //ToastHelper.showToast("选择失败"+error);
            }

            //选择取消
            @Override
            public void onPickCancle(int requestCode) {
                //ToastHelper.showToast("选择取消");
            }
        });

    }

    @OnClick(R.id.commit_btn)
    public void onViewClicked() {
        //ToastHelper.showToast("提交");
        emailValue=emailEd.getText().toString();

        telephoneValue=phoneEd.getText().toString();

        titleValue=titleEd.getText().toString();

        contentValue=depictEd.getText().toString();

        if(TextUtils.isEmpty(emailValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_email_address));
            return;
        }

        if(TextUtils.isEmpty(telephoneValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_phone_number));
            return;
        }

        if(TextUtils.isEmpty(titleValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_title_number));
            return;
        }

        if(TextUtils.isEmpty(contentValue)){
            ToastHelper.showToast(getResources().getString(R.string.input_depict_string));
            return;
        }

        ZbPermission.needPermission(this, REQUEST_PHON, Permission.PHONE, new ZbPermission.ZbPermissionCallback() {
            @Override
            public void permissionSuccess(int i) {
                //Router.build("ScannerActivity").with("coinType", chainDataInfo.getChainType()).requestCode(0).go(context);

                ToastHelper.showToast(getResources().getString(R.string.submitted_string));
            }

            @Override
            public void permissionFail(int i) {
                ToastHelper.showToast(getResources().getString(R.string.please_open_permissions));
            }
        });


    }


    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.depict_ed && canVerticalScroll(depictEd))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }


}
