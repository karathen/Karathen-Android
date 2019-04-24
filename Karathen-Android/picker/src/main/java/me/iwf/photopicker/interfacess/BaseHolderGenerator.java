package me.iwf.photopicker.interfacess;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.iwf.photopicker.R;
import me.iwf.photopicker.adapter.PhotoGridAdapter;
import me.iwf.photopicker.adapter.PopupDirectoryListAdapter;
import me.iwf.photopicker.adapter.PhotoAdapter;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class BaseHolderGenerator implements HolderGenerator {
    @Override
    public PhotoGridAdapter.PhotoViewHolder newGridViewHolder(Context mContext) {
        ViewGroup viewGroup = (ViewGroup) View.inflate(mContext, R.layout.__picker_item_photo,null);
        PhotoGridAdapter.PhotoViewHolder holder = new PhotoGridAdapter.PhotoViewHolder(viewGroup){
            @Override
            public void assignViews() {
                ivPhoto   = (ImageView) itemView.findViewById(R.id.iv_photo);
                vSelected = itemView.findViewById(R.id.v_selected);
                cover = itemView.findViewById(R.id.cover);
                //deleteBtn = itemView.findViewById(R.id.v_delete);
                //deleteBtn.setVisibility(View.GONE);
            }
        };
        holder.assignViews();
        return holder;
    }

    @Override
    public PhotoAdapter.PhotoViewHolder newGridViewHolder2(Context mContext) {
        ViewGroup viewGroup = (ViewGroup) View.inflate(mContext, R.layout.__picker_item_photo,null);
        PhotoAdapter.PhotoViewHolder holder = new PhotoAdapter.PhotoViewHolder(viewGroup){
            @Override
            public void assiginView() {
                ivPhoto   = (ImageView) itemView.findViewById(R.id.iv_photo);
                vSelected = itemView.findViewById(R.id.v_selected);
                vSelected.setVisibility(View.GONE);
                cover = itemView.findViewById(R.id.cover);
                cover.setVisibility(View.GONE);
                deleteBtn = itemView.findViewById(R.id.v_delete);
                deleteBtn.setVisibility(View.GONE);
            }
        };
        holder.assiginView();
        return holder;
    }

    @Override
    public PopupDirectoryListAdapter.ViewHolder newDirViewHolder(Context mContext) {
        final ViewGroup viewGroup = (ViewGroup) View.inflate(mContext, R.layout.__picker_item_directory,null);
        PopupDirectoryListAdapter.ViewHolder holder = new PopupDirectoryListAdapter.ViewHolder(viewGroup){
            @Override
            public void assignView() {
                ivCover = (ImageView) rootView.findViewById(R.id.iv_dir_cover);
                tvName  = (TextView)  rootView.findViewById(R.id.tv_dir_name);
                tvCount = (TextView)  rootView.findViewById(R.id.tv_dir_count);
            }
        };
        holder.assignView();
        return holder;
    }
}
