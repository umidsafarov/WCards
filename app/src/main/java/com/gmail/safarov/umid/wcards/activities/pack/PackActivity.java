package com.gmail.safarov.umid.wcards.activities.pack;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.source.DataSource;

public class PackActivity extends AppCompatActivity {

    public static final String EXTRA_PACK_ID = "EXTRA_PACK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pack);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(false);
        }

        // Get params
        long packId = getIntent().getLongExtra(EXTRA_PACK_ID, 0);

        PackFragment fragment = (PackFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content);

        if (fragment == null) {
            fragment = new PackFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content, fragment);
            transaction.commit();
        }

        // Create the presenter
        new PackPresenter(
                this,
                packId,
                fragment,
                DataSource.getInstance(this));
    }
}
