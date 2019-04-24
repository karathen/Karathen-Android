package me.iwf.photopicker.interfacess;

import android.content.Context;

import me.iwf.photopicker.adapter.PhotoGridAdapter;
import me.iwf.photopicker.adapter.PopupDirectoryListAdapter;
import me.iwf.photopicker.adapter.PhotoAdapter;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public interface HolderGenerator {


    PhotoGridAdapter.PhotoViewHolder newGridViewHolder(Context mContext);

    PhotoAdapter.PhotoViewHolder newGridViewHolder2(Context mContext);

    PopupDirectoryListAdapter.ViewHolder newDirViewHolder(Context mContext);


}
