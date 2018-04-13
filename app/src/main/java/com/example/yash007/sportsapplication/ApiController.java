package com.example.yash007.sportsapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class ApiController {

    ProgressDialog pDialog;
    private Context context;
    private String className;
    private String[] arguments;
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefs;

    public ApiController(Context context, String className, String[] arguments) {
        this.context = context;
        this.className = className;
        this.arguments = arguments;

        doClassCall();
    }


    private void doClassCall()   {
        new ApiClassHandler(context, className, arguments).execute();
    }

    private class ApiClassHandler extends AsyncTask<String, Void, String> {

        private Context context1;
        private String className1;
        private String[] arguments1;

        private ApiClassHandler(Context context1, String className1, String[] arguments1)  {
            this.context1 = context1;
            this.className1 = className1;
            this.arguments1 = arguments1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context1,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
            try {
                preferences = context1.getSharedPreferences(Config.PREF_NAME,Context.MODE_PRIVATE);
                String id = preferences.getString("id","");
                JSONObject postDataParams = new JSONObject();
                URL url = null;
                if(className1.equals("Name"))   {
                    url = new URL(Config.webUrl+"player/"+id+"/name");
                    Log.d("URL",url.toString());

                    postDataParams.put("pFirstName",arguments1[0].toString().trim());
                    postDataParams.put("pLastName",arguments1[1].toString().trim());
                }
                else if(className1.equals("Email")) {
                    url = new URL(Config.webUrl+"player/"+id+"/email");
                    Log.d("URL",url.toString());

                    postDataParams.put("pEmail",arguments1[0].toString().trim());
                }
                else if(className1.equals("Phone")) {
                    url = new URL(Config.webUrl+"player/"+id+"/phone");
                    Log.d("URL",url.toString());

                    postDataParams.put("pPhone",arguments1[0].toString().trim());
                }
                else if(className1.equals("Height")) {
                    url = new URL(Config.webUrl+"player/"+id+"/height");
                    Log.d("URL",url.toString());

                    postDataParams.put("pHeight",arguments1[0].toString().trim());
                }
                else if(className1.equals("Weight")) {
                    url = new URL(Config.webUrl+"player/"+id+"/weight");
                    Log.d("URL",url.toString());

                    postDataParams.put("pWeight",arguments1[0].toString().trim());
                }


                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
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

            String temp = "Success";
            if(status.equals(temp)) {
                prefs = context1.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE).edit();
                if(className1.equals("Name")) {
                    prefs.putString("pFirstName", arguments1[0].toString().trim());
                    prefs.putString("pLastName", arguments1[1].toString().trim());
                    prefs.commit();
                    Toast.makeText(context1, "Name has been changed successfully.", Toast.LENGTH_LONG).show();
                }
                else if(className1.equals("Email")) {
                    prefs.putString("pEmail", arguments1[0].toString().trim());
                    prefs.commit();
                    Toast.makeText(context1, "Email has been changed successfully.", Toast.LENGTH_LONG).show();
                }
                else if(className1.equals("Phone")) {
                    prefs.putString("pPhone", arguments1[0].toString().trim());
                    prefs.commit();
                    Toast.makeText(context1, "Phone has been changed successfully.", Toast.LENGTH_LONG).show();
                }
                else if(className1.equals("Height")) {
                    prefs.putString("pHeight", arguments1[0].toString().trim());
                    prefs.commit();
                    Toast.makeText(context1, "Height has been changed successfully.", Toast.LENGTH_LONG).show();
                }
                else if(className1.equals("Weight")) {
                    prefs.putString("pWeight", arguments1[0].toString().trim());
                    prefs.commit();
                    Toast.makeText(context1, "Weight has been changed successfully.", Toast.LENGTH_LONG).show();
                }
            }
            else    {
                Toast.makeText(context1,status.toString(),Toast.LENGTH_LONG).show();
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
