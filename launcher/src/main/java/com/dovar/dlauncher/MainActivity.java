package com.dovar.dlauncher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.widget.Space;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private PackageManager manager;
    private RecyclerView recycler;
    private RCommenAdapter<ResolveInfo> adapter;
    private ArrayList<String> hideApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity_main);

        findUI();
        init();
        setRecycler();
        setLiveWallpaper();
    }

    /**
     * 浏览器 ActivityInfo{97fc4a9 com.android.browser.BrowserActivity}
     * 一步 ActivityInfo{b679106 com.smartisanos.sidebar.setting.SettingActivity}
     * 邮件 ActivityInfo{4c82cb7 com.android.email.activity.Welcome}
     * 音乐 ActivityInfo{66a9e9a com.smartisanos.music.activities.MusicMain}
     * 安全中心 ActivityInfo{e1162c1 com.smartisanos.securitycenter.SecurityCenterActivity}
     * Dlauncher ActivityInfo{cb4cc6d com.dovar.dlauncher.MainActivity}
     * 欢喜云服务 ActivityInfo{a31bcf0 com.smartisanos.cloudsync.AccountsActivityLauncher}
     * 屏幕录像 ActivityInfo{41efd8f com.smartisanos.screenrecorder.EmptyActivity}
     * 搜索 ActivityInfo{96f0f87 com.android.quicksearchbox.SearchActivity}
     * 录音机 ActivityInfo{6dc4652 com.smartisanos.recorder.activity.EmptyActivity}
     * 游戏中心 ActivityInfo{2259513 com.smartisanos.gamestore.GameStoreActivity}
     * 远程协助 ActivityInfo{f57824e com.smartisanos.handinhand.activity.HomeActivity}
     * WPS Office ActivityInfo{57fee57 cn.wps.moffice.documentmanager.PreStartActivity}
     * 扫描全能王 ActivityInfo{4e99512 com.intsig.camscanner.WelcomeActivity}
     */

    private ArrayList<String> addHideApps() {
        ArrayList<String> hideApps = new ArrayList<>();
        hideApps.add("com.android.browser.BrowserActivity");
        hideApps.add("com.smartisanos.sidebar.setting.SettingActivity");
        hideApps.add("com.android.email.activity.Welcome");
        hideApps.add("com.smartisanos.music.activities.MusicMain");
        hideApps.add("com.smartisanos.securitycenter.SecurityCenterActivity");
        hideApps.add("com.dovar.dlauncher.MainActivity");
        hideApps.add("com.smartisanos.cloudsync.AccountsActivityLauncher");
        hideApps.add("com.smartisanos.screenrecorder.EmptyActivity");
        hideApps.add("com.android.quicksearchbox.SearchActivity");
        hideApps.add("com.smartisanos.recorder.activity.EmptyActivity");
        hideApps.add("com.smartisanos.gamestore.GameStoreActivity");
        hideApps.add("com.smartisanos.handinhand.activity.HomeActivity");
        hideApps.add("cn.wps.moffice.documentmanager.PreStartActivity");
        hideApps.add("com.intsig.camscanner.WelcomeActivity");

        hideApps.add("浏览器");
        hideApps.add("一步");
        hideApps.add("邮件");
        hideApps.add("音乐");
        hideApps.add("安全中心");
        hideApps.add("Dlauncher");
        hideApps.add("欢喜云服务");
        hideApps.add("屏幕录像");
        hideApps.add("搜索");
        hideApps.add("录音机");
        hideApps.add("游戏中心");
        hideApps.add("远程协助");
        hideApps.add("WPS Office");
        hideApps.add("扫描全能王");
        return hideApps;
    }

    private void setRecycler() {
        hideApps = addHideApps();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = manager.queryIntentActivities(intent, 0);

        List<ResolveInfo> apps = new ArrayList<>();
        for (ResolveInfo app : resolveInfos
                ) {
            if (!hideApps.contains(app.loadLabel(manager).toString())) {
                apps.add(app);
            }
        }
        adapter = new MultiCommonAdapter<ResolveInfo>(this, apps) {
            @Override
            public int getItemType(int position) {
                return R.layout.item_home;
            }

            @Override
            public void convert(RCommenViewHolder vh, int position) {
                ResolveInfo bean = mDatas.get(position);
                vh.setText(R.id.tv_text, bean.loadLabel(manager).toString());
                Log.d("test", "convert: " + bean.loadLabel(manager).toString() + bean.activityInfo);
                vh.setImageDrawable(R.id.iv_icon, bean.activityInfo.loadIcon(manager));
            }

//            //设置item跨列
//            @Override
//            public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//                super.onAttachedToRecyclerView(recyclerView);
//                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//                if (manager instanceof GridLayoutManager) {
//                    final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//                    gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                        @Override
//                        public int getSpanSize(int position) {
//                            switch (getItemViewType(position)) {
//                                case TYPE_HEADER:
//                                case TYPE_FOOTER:
//                                    //设置跨列
//                                    return gridManager.getSpanCount();
//                                default:
//                                    return 1;
//                            }
//                        }
//                    });
//                }
//            }

        };

        Space space = new Space(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
        space.setLayoutParams(lp);
        adapter.addFooterView(space);
        recycler.setLayoutManager(new GridLayoutManager(this, 5));
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new RCommenAdapter.OnItemClickListener<ResolveInfo>() {
            @Override
            public void onItemClick(ResolveInfo bean, int position) {
                String pkg = bean.activityInfo.packageName;
                String cls = bean.activityInfo.name;
                ComponentName component = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(component);
                startActivity(intent);
            }
        });


    }

    private void init() {
        manager = getPackageManager();
    }

    private void findUI() {
        recycler = (RecyclerView) findViewById(R.id.recycler);
    }

    @Override
    public void onClick(View v) {

    }

    private void setLiveWallpaper() {
        //设置动态壁纸
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, VideoLiveWallpaper.class));
        startActivity(intent);
    }
}
