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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.dovar.common.callback.ICallback;
import com.dovar.common.utils.ToastUtil;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class MainActivity extends AppCompatActivity implements ColorPicker.OnColorChangedListener {

    private int mborderColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.br_activity_main);

        createFloatWindow();

        findViewById(R.id.tv_func).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                EditText et = (EditText) findViewById(R.id.et_print);

                if (et != null && !TextUtils.isEmpty(et.getText())) {
                    changeCornerRadius(Integer.parseInt(et.getText().toString()));
                }
            }
        });

        findViewById(R.id.tv_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                View content = View.inflate(getApplicationContext(), R.layout.br_color_picker, null);

                new AlertDialog.Builder(MainActivity.this)
                        .setView(content)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {
                                changeCornerColor(mborderColor);
                            }
                        })
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
                picker.setOnColorChangedListener(MainActivity.this);

                //to turn of showing the old color
                picker.setShowOldCenterColor(false);

                //adding onChangeListeners to bars
//                opacityBar.setOnOpacityChangedListener();
//                valueBar.setOnValueChangeListener();
//                saturationBar.setOnSaturationChangeListener();

            }
        });
    }


    private BorderView[] mBorderViews = new BorderView[4];

    public void createFloatWindow() {
        requestFloatWindowPermissionIfNeeded(new ICallback() {
            @Override
            public void onSuccess() {
                WindowManager sWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = getResources().getDisplayMetrics();
                WindowManager.LayoutParams sWindowParams = new WindowManager.LayoutParams(dm.widthPixels, dm.heightPixels,
                        0, -getStatusHeight(), WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        PixelFormat.TRANSLUCENT);
                View infoView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.br_float_window, null);
                mBorderViews[0] = infoView.findViewById(R.id.br_1);
                mBorderViews[1] = infoView.findViewById(R.id.br_2);
                mBorderViews[2] = infoView.findViewById(R.id.br_3);
                mBorderViews[3] = infoView.findViewById(R.id.br_4);
                sWindowManager.addView(infoView, sWindowParams);
            }

            @Override
            public void onFail() {
                ToastUtil.showShort("没有悬浮窗权限！");
            }
        });
    }

    private void changeCornerColor(int color) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_enable_overlay_window_msg)
                    .setPositiveButton(R.string.dialog_enable_overlay_window_positive_btn
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mCallback != null) {
                                        mCallback.onSuccess();
                                    }
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
}
