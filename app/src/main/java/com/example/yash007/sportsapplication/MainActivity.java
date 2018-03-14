package com.example.yash007.sportsapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public EditText userName, userPassword;
    public static final String PREF_NAME = "SportsData";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText) findViewById(R.id.userName);
        userPassword = (EditText) findViewById(R.id.userPassword);
    }

    public void openSignUpActivity(View v)  {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void openForgetPasswordActivity(View v)  {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void doLogin(View v)  {
        new LoginPost().execute();
    }

    public class LoginPost extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Config.webUrl+"player/login");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pEmail", userName.getText().toString());
                postDataParams.put("pPassword", userPassword.getText().toString());
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
            String firstName = null;
            String lastName = null;
            String email = null;
            String id = null;
            String address = null;
            String bio = null;
            String pHeight = null;
            String pWeight = null;
            Boolean pAccountStatus = null;
            Boolean pAuthenticated = null;
            String pLoginType = null;
            String pBirthday = null;

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("Status");
                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("user");
                Log.d("LOGIN_RESULT",profile.toString());

                firstName = profile.getString("pFirstName");
                lastName = profile.getString("pLastName");
                email = profile.getString("pEmail");
                id = profile.getString("_id");
                address = profile.getString("pAddress");
                bio = profile.getString("pBio");
                pHeight = profile.getString("pHeight");
                pWeight = profile.getString("pWeight");
                pAccountStatus = profile.getBoolean("pAccountStatus");
                pAuthenticated =profile.getBoolean("pAuthenticated");
                pLoginType = profile.getString("pLoginType");
                pBirthday = profile.getString("pBirthday");

                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("pFirstName",firstName);
                editor.putString("pLastName",lastName);
                editor.putString("pEmail",email);
                editor.putString("id",id);
                editor.putString("pAddress",address);
                editor.putString("pBio",bio);
                editor.putString("pHeight",pHeight);
                editor.putString("pWeight",pWeight);
                editor.putBoolean("pAccountStatus",pAccountStatus);
                editor.putBoolean("pAuthenticated",pAuthenticated);
                editor.putString("pLoginType",pLoginType);
                editor.putString("pBirthday",pBirthday);
                editor.apply();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Success") == true) {
                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),"Incorrect username or password",Toast.LENGTH_LONG).show();
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
