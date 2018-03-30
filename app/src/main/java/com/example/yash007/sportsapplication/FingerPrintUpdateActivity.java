package com.example.yash007.sportsapplication;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class FingerPrintUpdateActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public String pFirstName, pLastName, pEmail, pPassword, pPhone, pLoginType, pAddress, pBio, pAndroidId;
    public int pHeight, pWeight;
    public String id;
    public boolean pAuthenticated, pAccountStatus;
    String uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_update);

        String status1 = getIntent().getExtras().getString("status");

        String temp = "success";
        SharedPreferences sharedPreferences = getSharedPreferences(Config.PREF_UNIQUE_ID, MODE_PRIVATE);
        uniqueId = sharedPreferences.getString(Config.PREF_UNIQUE_ID,null);

        Log.d("TTTT",status1);
        if(status1.equals(temp) == true) {
            Log.d("TTT",status1.toString());
            new AddFingerPrint().execute();
        }
        else    {
            Log.d("TTT","Not Matched" + status1);
        }


    }

    public class AddFingerPrint extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FingerPrintUpdateActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
            try {

                SharedPreferences sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
                pFirstName = sharedPreferences.getString("pFirstName","");
                pLastName = sharedPreferences.getString("pLastName","");
                pEmail = sharedPreferences.getString("pEmail","");
                pPassword = sharedPreferences.getString("pPassword","");
                pLoginType = sharedPreferences.getString("pLoginType","");
                pPhone = sharedPreferences.getString("pPhone","");
                pAuthenticated = sharedPreferences.getBoolean("pAuthenticated",false);
                pAccountStatus = sharedPreferences.getBoolean("pAccountStatus",true);
                pAddress = sharedPreferences.getString("pAddress","");
                pBio = sharedPreferences.getString("pBio","");
                pHeight = sharedPreferences.getInt("pHeight",0);
                pWeight = sharedPreferences.getInt("pWeight",0);
                pAndroidId = uniqueId;
                id = sharedPreferences.getString("_id","");


                //URL url = new URL("https://studytutorial.in/post.php");
                URL url = new URL(Config.webUrl+"player/"+id);

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("pFirstName", pFirstName );
                postDataParams.put("pLastName", pLastName);
                postDataParams.put("pEmail", pEmail);
                postDataParams.put("pPassword",pPassword);
                postDataParams.put("pPhone",pPhone);
                postDataParams.put("pLoginType",pLoginType);
                postDataParams.put("pAuthenticated",pAuthenticated);
                postDataParams.put("pAccountStatus",pAccountStatus);
                postDataParams.put("pAddress",pAddress);
                postDataParams.put("pBio",pBio);
                postDataParams.put("pHeight",pHeight);
                postDataParams.put("pWeight",pWeight);
                postDataParams.put("pAndroidId",pAndroidId);
                postDataParams.put("pBirthday","");

                Log.e("params111",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("PUT");
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
                    BufferedReader in= new BufferedReader(new
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
                Toast.makeText(getApplicationContext(),"Fingerprint has been added.",Toast.LENGTH_LONG).show();

            }
            else    {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_LONG).show();
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
