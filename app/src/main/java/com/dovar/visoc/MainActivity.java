package com.dovar.visoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    @OnClick(R.id.tv_autoservice)
    void gotoAutoService(View v) {
        Intent mIntent = new Intent(this, com.dovar.auto.MainActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.tv_launcher)
    void gotoLauncher(View v) {
        Intent mIntent = new Intent(this, com.dovar.dlauncher.MainActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.tv_xposed)
    void gotoXposed(View v) {
//        Intent mIntent=new Intent();
//        startActivity(mIntent);
    }

    @OnClick(R.id.tv_border_radius)
    void gotoBorderRadius(){
        Intent mIntent = new Intent(this, com.dovar.borderradius.MainActivity.class);
        startActivity(mIntent);
    }
}
