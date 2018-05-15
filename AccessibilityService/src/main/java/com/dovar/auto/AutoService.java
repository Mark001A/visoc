package com.dovar.auto;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import com.dovar.common.utils.LogUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dovar66
 * <p>
 * 1.使用此服务需要获取手机特殊权限：部分手机点击本demo页面中“打开辅助服务”按钮进入辅助功能页即可找到名称为“抢红包”的服务，然后打开即可，
 * 其他手机需要在辅助功能中找到“无障碍”项，然后在“无障碍”中找到“抢红包”打开即可.
 * 2.确保手机的微信消息能在通知栏显示.
 * Note:APP获取到辅助功能权限后，一旦APP进程被强杀就会清除该权限，再次进入APP又需要重新申请，正常退出则不会.
 */
public class AutoService extends AccessibilityService {
    public static final String TAG = "autoservice";

    public static boolean enableFunc2;          //标记是否开启抢红包功能
    private TextToSpeech mTts;  //语音
    private boolean enableTextToSpeech;

    private String currentActivity = "";
    private List<String> ids = Arrays.asList("bjj", "bi3", "brt", "c31");      //用于存储微信开红包按钮使用过的id，由于代码混淆原因，不同微信版本此button的id可能不同


    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && AutoFragment.canShowWindow(this)) {
            currentActivity = String.valueOf(event.getClassName());
            TasksWindow.show(this, event.getPackageName() + "\n" + event.getClassName());
        }

        String str_eventType;
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.i(TAG, "==============Start====================");
                str_eventType = "TYPE_VIEW_CLICKED";
                AccessibilityNodeInfo noteInfo = event.getSource();
                Log.i(TAG, noteInfo.toString());
                Log.i(TAG, "=============END=====================");
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                str_eventType = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                str_eventType = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                str_eventType = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                str_eventType = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                str_eventType = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                str_eventType = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                str_eventType = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                str_eventType = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                str_eventType = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                str_eventType = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                str_eventType = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                str_eventType = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                str_eventType = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                str_eventType = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
            default:
                str_eventType = String.valueOf(eventType);
        }
        String action;
        switch (event.getAction()) {
            case AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS:
                action = "ACTION_ACCESSIBILITY_FOCUS";
                break;
            case AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS:
                action = "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
                break;
            case AccessibilityNodeInfo.ACTION_CLEAR_FOCUS:
                action = "ACTION_CLEAR_FOCUS";
                break;
            case AccessibilityNodeInfo.ACTION_CLEAR_SELECTION:
                action = "ACTION_CLEAR_SELECTION";
                break;
            case AccessibilityNodeInfo.ACTION_CLICK:
                action = "ACTION_CLICK";
                break;
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD:
                action = "ACTION_SCROLL_FORWARD";
                break;
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD:
                action = "ACTION_SCROLL_BACKWARD";
                break;
            case AccessibilityNodeInfo.ACTION_FOCUS:
                action = "ACTION_FOCUS";
                break;
            case 0://com.android.systemui 一般是系统标题栏内容发生改变
                //在微信内页但不在聊天详情页时，收到红包不会产生通知栏事件，只有系统标题栏内容发生改变
                //标题栏中已经有微信未读消息图标时：TYPE_WINDOW_CONTENT_CHANGED	package:com.android.systemui	Class:android.widget.ImageView
                //标题栏中尚未有未读消息图标时：TYPE_WINDOW_CONTENT_CHANGED	package:com.android.systemui	Class:android.widget.FrameLayout
            default:
                action = event.getAction() + "";
                break;
        }
        Log.v(TAG, "EventType: " + str_eventType + "\tAction:" + action + "\tpackage:" + event.getPackageName() + "\tClass:" + event.getClassName() + "\t");

        if (enableFunc2 && "com.tencent.mm".equals(event.getPackageName().toString())) {//抢红包
            Log.d(TAG, "EventType: " + str_eventType + "\tAction:" + action + "\tClass:" + event.getClassName() + "\t");

            if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {//通知栏、Toast会触发该类型事件
                if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {//通知栏事件
                    Notification nc = (Notification) event.getParcelableData();
//                    nc.contentIntent.getCreatorPackage() ==> com.tencent.mm
//                    event.getContentDescription() ==>null
//                    event.getBeforeText() ==>null
                    String text = String.valueOf(nc.tickerText);
                    if (text.contains(": [微信红包]")) {
                        //点开通知栏消息
                        PendingIntent pendingIntent = nc.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName().toString())) {
                        //当前在红包待开页面，去拆红包
                        getLuckyMoney(event);
                    } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName().toString())) {
                        //拆完红包后看详细纪录的界面
                        if (enableTextToSpeech) {
                            enableTextToSpeech = false;
                            mTts.speak("抢到红包了", TextToSpeech.QUEUE_FLUSH, null);
                        }
//                        openNext("查看我的红包记录");
                    } else if ("com.tencent.mm.ui.LauncherUI".equals(currentActivity)) {
                        //当前在聊天界面,去点中红包
                        openLuckyEnvelope();
                    }
                } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                    if ("com.tencent.mm.ui.LauncherUI".equals(currentActivity)) {
                        //当前在聊天界面,去点中红包
                        openLuckyEnvelope();
                    }
                }
            }
        }
    }

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    private void openNext(String str) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Toast.makeText(this, "rootWindow为空", Toast.LENGTH_SHORT).show();
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        if (list != null && list.size() > 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            Toast.makeText(this, "找不到有效的节点", Toast.LENGTH_SHORT).show();
        }
    }

    //延迟打开界面
    private void openDelay(final int delaytime, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException mE) {
                    mE.printStackTrace();
                }
                openNext(text);
            }
        }).start();
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "服务已中断", Toast.LENGTH_SHORT).show();
        mTts.shutdown();
    }

    //每次调用Android Device Monitor去dump view hierarchy时都会导致服务重新onServiceConnected()
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "服务已开启", Toast.LENGTH_SHORT).show();
        mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTts.setLanguage(Locale.CHINESE);
                }
            }
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {
        TasksWindow.dismiss();
        return super.onUnbind(intent);
    }

    //    private void sendNotificationEvent() {
//        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
//        if (!manager.isEnabled()) {
//            return;
//        }
//        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
//        event.setPackageName(WECHAT_PACKAGENAME);
//        event.setClassName(Notification.class.getName());
//        CharSequence tickerText = PUSH_TEXT_KEY;
//        event.getText().add(tickerText);
//        manager.sendAccessibilityEvent(event);
//    }


    //开红包
    private void getLuckyMoney(final AccessibilityEvent event) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            List<AccessibilityWindowInfo> nodeInfos = getWindows();
            for (AccessibilityWindowInfo window : nodeInfos
                    ) {
                AccessibilityNodeInfo nodeInfo = window.getRoot();
                if (nodeInfo == null) {
                    break;
                }
                Toast.makeText(this, "getWindows()不为空", Toast.LENGTH_SHORT).show();
//                for (String id : ids
//                        ) {
//                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(id);
//                    if (list != null && list.size() > 0) {
//                        list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        LogUtil.d("clickBy getWindows()");
//                        return;
//                    }
//                }

                if (clickChild(nodeInfo)) return;
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  //获得整个窗口对象
            if (nodeInfo == null) {
                Toast.makeText(this, "getLuckyMoney rootWindow为空", Toast.LENGTH_SHORT).show();
                return;
            }
            //bi3是本人写代码时微信拆红包的button的id,该id可能会在更新微信版本后发生变更,可通过Android Device Monitor查看获取
            //可创建一个hashMap,在微信发生版本变更时储存对应微信版本号与id值，用于适配多个微信版本
//            for (String id : ids
//                    ) {
//                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(id);
//                if (list != null && list.size() > 0) {
//                    list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    LogUtil.d("clickBy getRootInActiveWindow()");
//                    return;
//                }
//            }

            //如果没找到拆红包的button，则将界面上所有子节点都点击一次
            if (clickChild(nodeInfo)) return;
            LogUtil.d("未找到开红包按钮");
        }
    }

    private boolean clickChild(AccessibilityNodeInfo nodeInfo) {
        boolean clicked = false;
        LogUtil.d("getLuckyMoney: " + nodeInfo.getClassName());
        if (nodeInfo.getChildCount() == 0) return clicked;
        for (int i = nodeInfo.getChildCount() - 1; i >= 0; i--) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (("android.widget.Button").equals(child.getClassName().toString())) {
                //已找到开红包按钮，模拟点击
                nodeInfo.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                LogUtil.d("clickBy android.widget.Button");
                clicked = true;
                enableTextToSpeech = true;
                return clicked;
            } else {
                if (clickChild(child)) {
                    clicked = true;
                }
            }
        }
        return clicked;
    }

    //点击进入红包待开启界面
    private void openLuckyEnvelope() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            LogUtil.d("openLuckyEnvelope rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        if (!"com.tencent.mm.ui.LauncherUI".equals(currentActivity)) return;
        if (list.isEmpty()) {
            openChatPage(nodeInfo);
        } else {
            //选择聊天记录中最新的红包
            for (int i = list.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = list.get(i).getParent();
                if (parent != null) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    //打开聊天内页
    private void openChatPage(AccessibilityNodeInfo nodeInfo) {
        //微信页能获取到4个Tab_Fragment的内容节点，但是获取不到"Tab_微信"的聊天列表item中用于显示最新一条聊天记录的那个TextView节点
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");
        if (!list.isEmpty()) {
            for (int j = 0; j < list.size(); j++) {
                AccessibilityNodeInfo node = list.get(j);
                while (node != null && !node.isClickable()) {
                    node = node.getParent();
                }
                if (node != null) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        } else {
            LogUtil.d("openChatPage: 没有新红包");
        }
    }
}
