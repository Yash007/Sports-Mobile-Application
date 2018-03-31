package com.example.yash007.sportsapplication;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TeamActivity extends AppCompatActivity {

    public String teamTitle;
    public String teamId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        teamTitle = getIntent().getExtras().getString("title");
        teamId = getIntent().getExtras().getString("id");

        teamTitle = teamTitle.substring(0,1).toUpperCase() + teamTitle.substring(1).toLowerCase();

        getSupportActionBar().setTitle(teamTitle);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Team").setIcon(R.drawable.team));
        tabLayout.addTab(tabLayout.newTab().setText("Member").setIcon(R.drawable.user));
        tabLayout.addTab(tabLayout.newTab().setText("Event").setIcon(R.drawable.user));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
