package com.gmail.safarov.umid.wcards.activities.wordsselection;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.data.source.DataSource;

public class WordsSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_PACK_ID = "EXTRA_PACK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_words_selection);

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

        // Set up viewer
        WordsSelectionFragment fragment = (WordsSelectionFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content);
        if (fragment == null) {
            fragment = new WordsSelectionFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content, fragment);
            transaction.commit();
        }

        // Set up presenter
        new WordsSelectionPresenter(
                packId,
                fragment,
                DataSource.getInstance(this));
    }

}
