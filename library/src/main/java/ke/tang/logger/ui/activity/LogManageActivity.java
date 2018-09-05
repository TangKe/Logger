package ke.tang.logger.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

import ke.tang.logger.R;
import ke.tang.logger.ui.fragment.LogManageFragment;
import ke.tang.logger.util.Common;

/**
 * @author tangke
 */

public class LogManageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;

    private ActionBarDrawerToggle mDrawerToggle;
    private LogManageFragment mContinuedLogManageFragment;
    private LogManageFragment mTraceLogManageFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logger_activity_log_manage);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mDrawer = findViewById(R.id.drawer);
        mNavigation = findViewById(R.id.navigation);
        mNavigation.inflateMenu(R.menu.logger_menu_log_manage_navigation);
        mNavigation.setNavigationItemSelectedListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContinuedLogManageFragment = LogManageFragment.newInstance(this, Common.getContinuedFileDirectory(this));
        mTraceLogManageFragment = LogManageFragment.newInstance(this, Common.getTraceLogFileDirectory(this));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTraceLogManageFragment).commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (R.id.traceLog == itemId) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mTraceLogManageFragment).commit();
        } else if (R.id.continuedLog == itemId) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mContinuedLogManageFragment).commit();
        }
        mDrawer.closeDrawer(Gravity.LEFT);
        return false;
    }
}
