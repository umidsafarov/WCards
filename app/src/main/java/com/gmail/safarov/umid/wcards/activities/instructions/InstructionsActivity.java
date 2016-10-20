package com.gmail.safarov.umid.wcards.activities.instructions;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.gmail.safarov.umid.wcards.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class InstructionsActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager mPager;
    @BindView(R.id.indicator)
    CircleIndicator mIndicator;
    @BindView(R.id.close_button)
    Button mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        ButterKnife.bind(this);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(false);
        }

        // Set up the pager
        InstructionsPagerAdapter adapter = new InstructionsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mIndicator.setViewPager(mPager);
    }
}