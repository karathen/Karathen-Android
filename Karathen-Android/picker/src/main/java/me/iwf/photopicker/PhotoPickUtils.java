package me.iwf.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import me.iwf.photopicker.interfacess.HolderGenerator;
import me.iwf.photopicker.utils.Initer;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class PhotoPickUtils {



    public static HolderGenerator holderGenerator;

    public static void onActivityResult(int requestCode, int resultCode, Intent data,PickHandler pickHandler ) {

        if(resultCode == Activity.RESULT_CANCELED) {
            pickHandler.onPickCancle( requestCode);
            return;
        }
        if (requestCode == PhotoPreview.REQUEST_CODE) {//如果是预览与删除后返回
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (photos != null) {
                    pickHandler.onPreviewBack(photos, requestCode);
                } else {
                    pickHandler.onPickFail("选择图片失败", requestCode);
                }
            } else {
                pickHandler.onPickFail("选择图片失败",requestCode);
            }
            return;

        }

        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                pickHandler.onPickFail("选择图片失败", requestCode);
                return;
            }
            ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            if (photos != null) {
                pickHandler.onPickSuccess(photos, requestCode);
            } else {
                pickHandler.onPickFail("选择图片失败", requestCode);
            }
        }

    }

    public static PhotoPicker.PhotoPickerBuilder startPick(){
      return   PhotoPicker.builder();
    }



    public interface  PickHandler{
        void onPickSuccess(ArrayList<String> photos,int requestCode);
        void onPreviewBack(ArrayList<String> photos,int requestCode);
        void onPickFail(String error,int requestCode);
        void onPickCancle(int requestCode);
    }

    /**
     *
     * @param appContext

     */
    public static void init(Context appContext,Initer initer){
        initer.init(appContext);
    }
}
