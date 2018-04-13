package com.dovar.borderradius;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dovar.common.callback.ICallback;
import com.dovar.common.utils.ToastUtil;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class BorderFragment extends Fragment implements ColorPicker.OnColorChangedListener {

    private int mborderColor;
    private View floatView;
    private BorderView[] mBorderViews = new BorderView[4];

    private View mainView;


    public static BorderFragment instance() {
        BorderFragment mAutoFragment = new BorderFragment();
        return mAutoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.br_activity_main, null);

        createFloatWindow();

        mainView.findViewById(R.id.tv_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                View content = View.inflate(getContext().getApplicationContext(), R.layout.br_color_picker, null);

                new AlertDialog.Builder(getContext())
                        .setView(content)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {
                                changeCornerColor(mborderColor);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();

                ColorPicker picker = (ColorPicker) content.findViewById(R.id.picker);
                SVBar svBar = (SVBar) content.findViewById(R.id.svbar);
                OpacityBar opacityBar = (OpacityBar) content.findViewById(R.id.opacitybar);
                SaturationBar saturationBar = (SaturationBar) content.findViewById(R.id.saturationbar);
                ValueBar valueBar = (ValueBar) content.findViewById(R.id.valuebar);

                picker.addSVBar(svBar);
                picker.addOpacityBar(opacityBar);
                picker.addSaturationBar(saturationBar);
                picker.addValueBar(valueBar);

                //To get the color
                picker.getColor();

                //To set the old selected color u can do it like this
                picker.setOldCenterColor(picker.getColor());
                // adds listener to the colorpicker which is implemented
                //in the activity
                picker.setOnColorChangedListener(BorderFragment.this);

                //to turn of showing the old color
                picker.setShowOldCenterColor(false);

                //adding onChangeListeners to bars
//                opacityBar.setOnOpacityChangedListener();
//                valueBar.setOnValueChangeListener();
//                saturationBar.setOnSaturationChangeListener();

            }
        });

        SeekBar sb_size = mainView.findViewById(R.id.sb_size);
        final TextView tv_size_num = mainView.findViewById(R.id.tv_size_num);
        sb_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar mSeekBar, int mI, boolean mB) {
                if (tv_size_num != null) {
                    tv_size_num.setText(String.valueOf(mSeekBar.getProgress()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar mSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar mSeekBar) {
                changeCornerRadius(mSeekBar.getProgress());
            }
        });
        return mainView;
    }

    public void createFloatWindow() {
        requestFloatWindowPermissionIfNeeded(new ICallback() {
            @Override
            public void onSuccess() {
                showFloatWindow();
            }

            @Override
            public void onFail() {
                ToastUtil.showShort("没有悬浮窗权限！");
            }
        });
    }

    private void showFloatWindow() {
        WindowManager sWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (floatView == null) {
            floatView = LayoutInflater.from(getContext()).inflate(R.layout.br_float_window, null);
            mBorderViews[0] = floatView.findViewById(R.id.br_1);
            mBorderViews[1] = floatView.findViewById(R.id.br_2);
            mBorderViews[2] = floatView.findViewById(R.id.br_3);
            mBorderViews[3] = floatView.findViewById(R.id.br_4);
        }
        if (floatView.getParent() == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            WindowManager.LayoutParams sWindowParams = new WindowManager.LayoutParams(
                    dm.widthPixels, dm.heightPixels,
                    0, -getStatusHeight() / 2,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS //内容会覆盖到标题栏和导航栏
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE//不使用此FLAG时悬浮窗会拦截屏幕TOUCH事件
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//悬浮窗适配8.0,8.0后悬浮窗类型不得使用被标记为过时的TYPE值
                sWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            try {
                sWindowManager.addView(floatView, sWindowParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void changeCornerColor(int color) {
        if (color == 0) return;
        if (mBorderViews != null && mBorderViews.length > 0) {
            for (BorderView bv : mBorderViews) {
                bv.setBorderColor(color);
            }
        }
    }

    private void changeCornerRadius(int radius) {
        if (mBorderViews != null && mBorderViews.length > 0) {
            for (BorderView bv : mBorderViews) {
                ViewGroup.LayoutParams lp = bv.getLayoutParams();
                lp.width = radius;
                lp.height = radius;
                bv.setLayoutParams(lp);
            }
        }
    }

    public int getStatusHeight() {
        int statusBarHeight2 = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight2 = getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight2;
    }

    /**
     * 申请悬浮窗权限
     */
    private void requestFloatWindowPermissionIfNeeded(final ICallback mCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.dialog_enable_overlay_window_msg)
                    .setPositiveButton(R.string.dialog_enable_overlay_window_positive_btn
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                    startActivityForResult(intent, 66);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (mCallback != null) {
                                mCallback.onFail();
                            }
                        }
                    })
                    .create()
                    .show();

        } else {
            if (mCallback != null) {
                mCallback.onSuccess();
            }
        }
    }

    @Override
    public void onColorChanged(int color) {
        mborderColor = color;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getContext())) {
                    showFloatWindow();
                }
            }
        }
    }
}
