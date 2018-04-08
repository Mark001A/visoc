package com.dovar.dlauncher;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Dovar on 2016/12/28 0028.
 * Email:xiaohe0949@163.com
 * 通用RecyclerView的ViewHolder
 */
public class RCommenViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;


    public RCommenViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

//    public static RCommenViewHolder get(Context context, ViewGroup parent, int layoutId) {
//
//        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
//        return new RCommenViewHolder(itemView);
//    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public RCommenViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public RCommenViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public RCommenViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageDrawable(drawable);
        }
        return this;
    }

    /**
     * 使用glide加载网络图片
     */
    public void setImageUrl(Context context, int id, String url) {
        ImageView iv = getView(id);
        if (iv != null && url != null) {
//            Glide.with(context).load(url).error(R.mipmap.ic_launcher).into(iv);
        }
    }

    /**
     * 给itemView中的子View添加点击事件
     */
    public RCommenViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

}
