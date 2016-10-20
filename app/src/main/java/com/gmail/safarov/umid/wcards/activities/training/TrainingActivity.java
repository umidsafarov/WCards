package com.gmail.safarov.umid.wcards.activities.training;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.source.DataSource;

public class TrainingActivity extends AppCompatActivity {

    public static final String EXTRA_PACK_ID = "EXTRA_PACK_ID";
    public static final String EXTRA_TRAINING_MODE = "EXTRA_TRAINING_MODE";

    public static final int TRAINING_MODE_EN_RU = 1;
    public static final int TRAINING_MODE_RU_EN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_training);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(false);
        }

        // Set up viewer
        long packId = getIntent().getLongExtra(EXTRA_PACK_ID, 0);
        int trainingMode = getIntent().getIntExtra(EXTRA_TRAINING_MODE, TRAINING_MODE_EN_RU);
        TrainingFragment fragment = (TrainingFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content);
        if (fragment == null) {
            fragment = new TrainingFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content, fragment);
            transaction.commit();
        }

        // Set up presenter
        new TrainingPresenter(
                this,
                trainingMode,
                packId,
                fragment,
                DataSource.getInstance(this));
    }
}
