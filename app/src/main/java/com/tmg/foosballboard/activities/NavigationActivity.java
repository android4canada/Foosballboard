package com.tmg.foosballboard.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.tmg.foosballboard.R;
import com.tmg.foosballboard.fragments.HistoryFragment;
import com.tmg.foosballboard.fragments.RankingFragment;

public class NavigationActivity extends BaseActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String tag = "";
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_ranking:
                    fragment = new RankingFragment();
                    tag = "Ranking";
                    break;
                case R.id.navigation_historical:

                    fragment = new HistoryFragment();
                    tag = "History";
                    break;

            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, fragment, tag);
            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //navigation.setItemIconTintList(null);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new RankingFragment(), "Ranking");
        transaction.commit();
    }

}
