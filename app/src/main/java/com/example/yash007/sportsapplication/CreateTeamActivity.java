package com.example.yash007.sportsapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Iterator;

public class CreateTeamActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public EditText teamName, teamSports, teamLocation;
    public Spinner teamAgeGroup, teamGender;

    private String USER_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        teamName = (EditText) findViewById(R.id.teamName);
        teamSports = (EditText) findViewById(R.id.teamSports);
        teamLocation = (EditText) findViewById(R.id.teamLocation);
        teamAgeGroup = (Spinner) findViewById(R.id.teamAgeGroup);
        teamGender = (Spinner) findViewById(R.id.teamGender);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("id","");
    }

    public void doCreateTeam(View v)    {
        new CreateTeamPost().execute();
    }

    public class CreateTeamPost extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateTeamActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                //URL url = new URL("https://studytutorial.in/post.php");
                URL url = new URL(Config.webUrl+USER_ID+"/teams");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("tName", teamName.getText().toString());
                postDataParams.put("tSports", teamSports.getText().toString());
                postDataParams.put("tGender",teamGender.getSelectedItem().toString());
                postDataParams.put("tAgeGroup",teamAgeGroup.getSelectedItem().toString());
                postDataParams.put("tAddress",teamLocation.getText().toString());
                postDataParams.put("tPic","teamPic");

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Success") == true) {
                Toast.makeText(getApplicationContext(),"Team Created successfully.",Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),"Error in creating team. Please try again",Toast.LENGTH_LONG).show();
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
