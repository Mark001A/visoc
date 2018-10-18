package com.dovar.visoc;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dovar.auto.AutoFragment;
import com.dovar.borderradius.BorderFragment;
import com.dovar.common.base.BaseActivity;
import com.dovar.common.vsview.DRecyclerView;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    DRecyclerView mDRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Nothing!Haha!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        addFragment(AutoFragment.instance(), R.id.fl_auto_service);
        addFragment(BorderFragment.instance(), R.id.fl_border_radius);

//        mDRecyclerView = findView(R.id.vs_list);
//        mDRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mDRecyclerView.setAdapter(new Adapter() {
//            @Override
//            public DRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View item = View.inflate(mContext, R.layout.item_layout, null);
//                return new DViewHolder(item);
//            }
//
//            @Override
//            public void onBindViewHolder(DRecyclerView.ViewHolder holder, int position) {
//
//            }
//
//            @Override
//            public int getItemCount() {
//                return 10;
//            }
//        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  /*  @OnClick(R.id.tv_launcher)
    void gotoLauncher(View v) {
        Intent mIntent = new Intent(this, com.dovar.dlauncher.MainActivity.class);
        startActivity(mIntent);
    }*/

/*    @OnClick(R.id.tv_xposed)
    void gotoXposed(View v) {
//        Intent mIntent=new Intent();
//        startActivity(mIntent);
    }*/

    private void addFragment(Fragment child, @IdRes int resId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(resId, child);
        ft.commit();
    }

    public static class DViewHolder extends DRecyclerView.ViewHolder {

        public DViewHolder(View itemView) {
            super(itemView);
        }
    }
}
