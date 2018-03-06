package com.example.yash007.sportsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.healthRecords:
                Intent intent = new Intent(this,HealthActivity.class);
                startActivity(intent);
                break;
            case R.id.profile:

                break;
            case R.id.signOut:

                break;
            default:
                break;
        }
        return true;
    }

    public void openCreateTeamActivity(View v)  {
        Intent intent = new Intent(this, CreateTeamActivity.class);
        startActivity(intent);
    }
}
