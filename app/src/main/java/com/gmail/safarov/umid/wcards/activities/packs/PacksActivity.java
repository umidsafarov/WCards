package com.gmail.safarov.umid.wcards.activities.packs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gmail.safarov.umid.wcards.R;
import com.gmail.safarov.umid.wcards.activities.about.AboutActivity;
import com.gmail.safarov.umid.wcards.activities.instructions.InstructionsActivity;
import com.gmail.safarov.umid.wcards.data.source.DataSource;
import com.gmail.safarov.umid.wcards.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PacksActivity extends AppCompatActivity {

    private static final String STATE_KEY_IS_ACTIVE_PACKS_SHOWN = "STATE_KEY_IS_ACTIVE_PACKS_SHOWN";

    PacksPresenter mPacksPresenter;

    @BindView(R.id.drawer)
    DrawerLayout mDrawer;
    @Nullable
    @BindView(R.id.drawer_navigation)
    NavigationView mDrawerNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packs);
        ButterKnife.bind(this);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_list);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Set up drawer
        mDrawer.setStatusBarBackground(R.color.colorPrimaryDark);
        if (mDrawerNavigation != null) {
            mDrawerNavigation.setNavigationItemSelectedListener(navigationItemSelectedListener);
        }

        // Set up view
        PacksFragment fragment =
                (PacksFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment == null) {
            fragment = new PacksFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content, fragment);
            transaction.commit();
        }

        // Load previously saved state
        boolean isActivePacksShown = true;
        if (savedInstanceState != null)
            isActivePacksShown = (boolean) savedInstanceState.getSerializable(STATE_KEY_IS_ACTIVE_PACKS_SHOWN);

        // Set up presenter
        mPacksPresenter = new PacksPresenter(this, fragment, DataSource.getInstance(this), isActivePacksShown);

        // Show instructions on firstRun
        if (SharedPreferencesUtils.isFirstRun(this))
            startActivity(new Intent(PacksActivity.this, InstructionsActivity.class));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_KEY_IS_ACTIVE_PACKS_SHOWN, mPacksPresenter.getIsActivePacksShown());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.packs_drawer_active:
                    mPacksPresenter.showActivePacks();
                    break;
                case R.id.packs_drawer_completed:
                    mPacksPresenter.showCompletedPacks();
                    break;
                case R.id.packs_drawer_instructions:
                    startActivity(new Intent(PacksActivity.this, InstructionsActivity.class));
                    break;
                case R.id.packs_drawer_about:
                    startActivity(new Intent(PacksActivity.this, AboutActivity.class));
                    break;
                default:
                    break;
            }
            mDrawer.closeDrawers();
            return true;
        }
    };
}
