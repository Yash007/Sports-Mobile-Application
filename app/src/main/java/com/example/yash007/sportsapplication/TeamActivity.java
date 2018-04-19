package com.example.yash007.sportsapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TeamActivity extends AppCompatActivity {

    public String teamTitle;
    public String teamId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        teamTitle = getIntent().getExtras().getString("title");
        teamId = getIntent().getExtras().getString("id");

        SharedPreferences.Editor editor = getSharedPreferences(Config.PREF_TEAM,MODE_PRIVATE).edit();
        editor.putString("teamName",teamTitle);
        editor.putString("teamId",teamId);
        editor.commit();

        teamTitle = teamTitle.substring(0,1).toUpperCase() + teamTitle.substring(1).toLowerCase();

        getSupportActionBar().setTitle(teamTitle);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Team").setIcon(R.drawable.team));
        tabLayout.addTab(tabLayout.newTab().setText("Members").setIcon(R.drawable.user));
        tabLayout.addTab(tabLayout.newTab().setText("Events").setIcon(R.drawable.calendar));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_team,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.teamHealthSettings:
                startActivity(new Intent(TeamActivity.this, HealthActivity.class));
                break;
            case R.id.teamUserProfileSettings:
                startActivity(new Intent(TeamActivity.this, ProfileActivity.class));
                break;
            case R.id.teamProfileSettings:
                //Team Profile Edit Activity
                Intent intent = new Intent(TeamActivity.this, TeamEditActivity.class);
                intent.putExtra("teamId",teamId);
                startActivity(intent);
                break;
            case R.id.teamSignOut:
                startActivity(new Intent(TeamActivity.this, MainActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}
