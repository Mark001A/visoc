package com.dovar.auto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dovar.common.base.BaseFragment;
import com.dovar.common.utils.ToastUtil;

import static com.dovar.auto.AutoService.TAG;

public class AutoFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private CheckBox cb_assist;
    private CheckBox cb_window;
    private CheckBox cb_lucky_money;
    private CheckBox cb_people_nearby;


    public static AutoFragment instance() {
        AutoFragment mAutoFragment = new AutoFragment();
        return mAutoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.auto_activity_main, null);

        cb_assist = findView(R.id.cb_assist_permission);
        if (cb_assist != null) {
            cb_assist.setOnCheckedChangeListener(this);
        }
        cb_window = findView(R.id.cb_show_window);
        if (cb_window != null) {
            cb_window.setOnCheckedChangeListener(this);
        }
        cb_lucky_money = findView(R.id.cb_lucky_money);
        if (cb_lucky_money != null) {
            cb_lucky_money.setOnCheckedChangeListener(this);
        }
        cb_people_nearby = findView(R.id.cb_people_nearby);
        if (cb_people_nearby != null) {
            cb_people_nearby.setOnCheckedChangeListener(this);
        }
        return mainView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateCheckBox(cb_assist, isAccessibilitySettingsOn());
            updateCheckBox(cb_window, canShowWindow(getContext()));
            if (canShowWindow(getContext())) {
                requestFloatWindowPermissionIfNeeded();
            }
        }
    }

    /**
     * 申请辅助功能权限
     */
    private void requestAssistPermission() {
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            ToastUtil.showShort("找到visoc开启服务即可");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 申请悬浮窗权限
     */
    private void requestFloatWindowPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getContext())) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.dialog_enable_overlay_window_msg)
                    .setPositiveButton(R.string.dialog_enable_overlay_window_positive_btn
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setShowWindow(getContext(), false);
                                    updateCheckBox(cb_window, false);
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            setShowWindow(getContext(), false);
                            updateCheckBox(cb_window, false);
                        }
                    })
                    .create()
                    .show();

        }
    }

/*    private MoveTextView floatBtn1;
    private MoveTextView floatBtn2;
    private WindowManager wm;

    //创建悬浮按钮
    private void createFloatView() {
        WindowManager.LayoutParams pl = new WindowManager.LayoutParams();
        wm = (WindowManager) getSystemService(getApplication().WINDOW_SERVICE);
        pl.type = WindowManager.LayoutParams.TYPE_TOAST;//修改为此TYPE_TOAST，可以不用申请悬浮窗权限就能创建悬浮窗,但在部分手机上会崩溃
        pl.format = PixelFormat.RGBA_8888;
        pl.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        pl.gravity = Gravity.END | Gravity.BOTTOM;
        pl.x = 0;
        pl.y = 0;

        pl.width = WindowManager.LayoutParams.WRAP_CONTENT;
        pl.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        floatBtn1 = (MoveTextView) inflater.inflate(R.layout.floatbtn, null);
        floatBtn1.setText("打招呼");
        floatBtn2 = (MoveTextView) inflater.inflate(R.layout.floatbtn, null);
        floatBtn2.setText("抢红包");
        wm.addView(floatBtn1, pl);
        pl.gravity = Gravity.BOTTOM | Gravity.START;
        wm.addView(floatBtn2, pl);

        floatBtn1.setOnClickListener(this);
        floatBtn2.setOnClickListener(this);
        floatBtn1.setWm(wm, pl);
        floatBtn2.setWm(wm, pl);
    }*/

    /**
     * 检测辅助功能是否开启
     */
    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        String service = getContext().getPackageName() + "/" + AutoService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(getContext().getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getContext().getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.d(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.d(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_assist_permission) {
            if (isChecked && !isAccessibilitySettingsOn()) {
                requestAssistPermission();
            }
        } else if (buttonView.getId() == R.id.cb_show_window) {
            setShowWindow(getContext(), isChecked);

            if (isChecked) {
                requestFloatWindowPermissionIfNeeded();
            }

            if (!isChecked) {
                TasksWindow.dismiss();
            } else {
                TasksWindow.show(getContext(), getContext().getPackageName() + "\n" + getClass().getName());
            }
        } else if (buttonView.getId() == R.id.cb_lucky_money) {
            if (isChecked) {
                if (isAccessibilitySettingsOn()) {
                    AutoService.enableFunc2 = true;
                } else {
                    ToastUtil.showShort("辅助功能未开启", Toast.LENGTH_SHORT);
                    buttonView.setChecked(false);
                }
            } else {
                AutoService.enableFunc2 = false;
            }
        } else if (buttonView.getId() == R.id.cb_people_nearby) {
            if (isChecked) {
                if (isAccessibilitySettingsOn()) {
                    AutoService.enableFunc3 = true;
                } else {
                    ToastUtil.showShort("辅助功能未开启", Toast.LENGTH_SHORT);
                    buttonView.setChecked(false);
                }
            } else {
                AutoService.enableFunc3 = false;
            }
        }
    }

    private void updateCheckBox(CheckBox box, boolean isChecked) {
        if (box != null) {
            box.setChecked(isChecked);
        }
    }

    public static boolean canShowWindow(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("show_window", true);
    }

    public static void setShowWindow(Context context, boolean isShow) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean("show_window", isShow).apply();
    }
}
