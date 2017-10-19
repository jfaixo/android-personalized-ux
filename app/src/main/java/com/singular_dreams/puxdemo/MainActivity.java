package com.singular_dreams.puxdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.singular_dreams.pux.PuxBaseActivity;
import com.singular_dreams.pux.debug.PuxDebugActivity;
import com.singular_dreams.pux.debug.PuxDebugFragment;

public class MainActivity extends PuxBaseActivity {

    public MainActivity() {
        super();

        getJourneyManager().addSteps(new Class[] { DemoFragment.class});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startActivity(new Intent(getApplicationContext(), PuxDebugActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    public void onSettingsAction(MenuItem mi) {
        startActivity(new Intent(getApplicationContext(), PuxDebugActivity.class));
    }
}
