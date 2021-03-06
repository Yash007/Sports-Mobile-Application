package com.example.yash007.sportsapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DashboardActivity extends AppCompatActivity {

    public FloatingActionButton createTeam;

    private static final String TAG = DashboardActivity.class.getSimpleName();

    private GridView mGridView;

    private TeamGridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private ProgressDialog pDialog;

    private String USER_ID;
    String captainId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        createTeam = (FloatingActionButton) findViewById(R.id.createTeamButton);

        SharedPreferences preferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
        captainId = preferences.getString("id","");
        mGridView = (GridView) findViewById(R.id.teamGridListView);

        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CreateTeamActivity.class);
                startActivity(intent);
            }
        });

        mGridData = new ArrayList<>();
        mGridAdapter = new TeamGridViewAdapter(this, R.layout.gridview_team, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()  {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(DashboardActivity.this, TeamActivity.class);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("id",item.getId());

                startActivity(intent);

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("id","");

        new GetTeams().execute();

        registerForContextMenu(mGridView);

        //Toast.makeText(DashboardActivity.this,USER_ID,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.teamGridListView)   {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_option_team, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())   {
            case R.id.teamOptionDelete:
                TextView id = info.targetView.findViewById(R.id.teamIdGrid);
                new DeleteTeam(id.getText().toString().trim(), captainId).execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
                startActivity(new Intent(DashboardActivity.this, HealthActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                break;
            case R.id.fingerprint:
                startActivity(new Intent(DashboardActivity.this, FingerprintActivity.class));
                break;
            case R.id.signOut:
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                break;
            default:
                break;
        }
        return true;
    }


    private class GetTeams extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Config.webUrl + captainId + "/view/teams");
            Log.d("TEAMS",Config.webUrl + captainId + "/view/teams");
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("team");

                    mGridData.clear();
                    GridItem item;
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("_id");
                        String teamName = c.getString("tName");
                        String teamPic = c.getString("tSports");
                        String teamAge = c.getString("tAgeGroup");

                        // tmp hash map for single contact

                        item = new GridItem();
                        // adding each child node to HashMap key => value
                        item.setTitle(teamName);
                        item.setImage(teamPic);
                        item.setId(id);
                        item.setAge(teamAge);

                        // adding contact to contact list
                        mGridData.add(item);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            mGridAdapter.setGridData(mGridData);
        }

    }

    public class DeleteTeam extends AsyncTask<String, Void, String> {

        String teamId;
        String captainId;

        public DeleteTeam(String teamId, String captainId)   {
            this.teamId = teamId;
            this.captainId = captainId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DashboardActivity.this, R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL(Config.webUrl+captainId+"/teams/"+teamId+"/terminate");
                Log.d("YASHSOMPURA",url.toString());

                JSONObject postDataParams = new JSONObject();
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("DELETE");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        Log.e("+++++", "line: "+line);
                        sb.append(line);
                        //break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e) {
                Log.e("~~~", e.toString());
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG",result);
            String status = null;

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("Status");
                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("user");
                Log.d("LOGIN_RESULT",profile.toString());


            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Success") == true) {
                Toast.makeText(DashboardActivity.this,"Team terminated successfully.",Toast.LENGTH_LONG).show();
                new GetTeams().execute();
            }
            else    {
                Toast.makeText(DashboardActivity.this,"Error in termination of team. Please try again",Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            Log.d("TAG",result.toString());
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }


}
