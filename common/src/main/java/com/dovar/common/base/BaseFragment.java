package com.dovar.common.base;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by heweizong on 2018/4/13.
 */

public class BaseFragment extends Fragment {
    protected View mainView;


    /**
     * 通过viewId获取控件
     */
    public <V extends View> V findView(int viewId) {
        if (mainView == null) return null;
        return (V) mainView.findViewById(viewId);
    }

    public <V extends View> V findView(int viewId, View parent) {
        if (parent == null) return null;
        return (V) parent.findViewById(viewId);
    }

    public <V extends View> V addViewClickEvent(int viewId, View.OnClickListener onClickImp) {
        V view = findView(viewId);
        if (view == null) return null;
        view.setOnClickListener(onClickImp);
        return view;
    }

    public void addViewClickEvent(View view, View.OnClickListener onClickImp) {
        if (view == null) return;
        view.setOnClickListener(onClickImp);
    }
}
