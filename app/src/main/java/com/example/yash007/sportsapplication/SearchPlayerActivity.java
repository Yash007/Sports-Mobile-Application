package com.example.yash007.sportsapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

public class SearchPlayerActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;
    Button addPlayer;

    String TAG = "SearchPlayerActivity";

    android.support.v7.widget.SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.searchPlayerResult);

        String teamId = getIntent().getExtras().getString("teamId");
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView playerId = (TextView) view.findViewById(R.id.listPlayerId);
                Toast.makeText(SearchPlayerActivity.this,playerId.getText().toString(), Toast.LENGTH_SHORT).show();
                new GetProfile(playerId.getText().toString().trim()).execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_members, menu);
        final MenuItem searchItem = menu.findItem(R.id.searchMember);

        if (searchItem != null) {
            searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return false;
                }
            });

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(SearchPlayerActivity.this, android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //async code will be here
                    contactList.clear();

                    if(query != null && !query.isEmpty())
                        new SearchPlayers(query).execute();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    contactList.clear();
                    //async code will be here

                    if (newText != null && !newText.isEmpty())
                        new SearchPlayers(newText).execute();
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            findViewById(R.id.searchPlayerActivity).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private class SearchPlayers extends AsyncTask<Void, Void, Void> {

        String name;
        public SearchPlayers(String name) {
            this.name = name;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SearchPlayerActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Config.webUrl+"player/search/"+name);


            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("user");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("_id");
                        String firstName = c.getString("pFirstName");
                        String lastName = c.getString("pLastName");
                        String email = c.getString("pEmail");
                        String height = "Height : " + c.getString("pHeight") + " Weight: " + c.getString("pWeight") + " Kg.";

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", firstName + " " + lastName);
                        contact.put("email",email);
                        contact.put("height",height);

                        // adding contact to contact list
                        contactList.add(contact);
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
            ListAdapter adapter = new SimpleAdapter(
                    SearchPlayerActivity.this, contactList,
                    R.layout.list_players, new String[]{"id","name", "email",
                    "mobile"}, new int[]{R.id.listPlayerId,R.id.listPlayerName,
                    R.id.listPlayerEmail, R.id.listPlayerHeightWeight});

            lv.setAdapter(adapter);
        }

    }

    public class GetProfile extends AsyncTask<Void, Void, Void> {

        String playerId;
        String firstName, lastName, email, phone, height, weight, birthday, address, bio;
        public GetProfile(String playerId)  {
            this.playerId = playerId;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SearchPlayerActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(Config.webUrl+"player/"+playerId);

            Log.d("JJJJ",jsonStr.toString());

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("Team");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        firstName = c.getString("pFirstName");
                        lastName = c.getString("pLastName");
                        email = c.getString("pEmail");
                        height = c.getString("pHeight");
                        weight = c.getString("pWeight");
                        phone = c.getString("pPhone");
                        birthday = c.getString("pBirthday");
                        address = c.getString("pAddress");
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchPlayerActivity.this,
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
                        Toast.makeText(SearchPlayerActivity.this,
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

            final Dialog dialog = new Dialog(SearchPlayerActivity.this);
            dialog.setContentView(R.layout.dialog_add_player_profile);
            dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;

            TextView fullName = (TextView) dialog.findViewById(R.id.profileFullName);
            TextView emailT = (TextView) dialog.findViewById(R.id.profileEmail);
            TextView phoneT = (TextView) dialog.findViewById(R.id.profilePhone);
            TextView heightWeightT = (TextView) dialog.findViewById(R.id.profileHeightWeight);
            TextView addressT = (TextView) dialog.findViewById(R.id.profileAddress);
            TextView birthdayT = (TextView) dialog.findViewById(R.id.profileBirthday);

            fullName.setText(firstName+" "+lastName);
            emailT.setText(email);
            phoneT.setText(phone);
            heightWeightT.setText(height + " cm." + weight + " kg.");
            birthdayT.setText(birthday);
            addressT.setText(address);

            dialog.show();

            Button close = (Button) dialog.findViewById(R.id.profileCloseButton);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            Button addPlayer = (Button) dialog.findViewById(R.id.profileAddButton);
            addPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    new AddPlayerToTeam(playerId).execute();
                }
            });
        }

    }

    public class AddPlayerToTeam extends AsyncTask<String, Void, String> {

        String playerId;
        String captainId;
        String teamId;

        public AddPlayerToTeam(String playerId)    {
            SharedPreferences preferences = getSharedPreferences(Config.PREF_NAME,MODE_PRIVATE);
            captainId = preferences.getString("id","");
            teamId = getIntent().getExtras().getString("teamId");
            this.playerId = playerId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchPlayerActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL(Config.webUrl+captainId+"/teams/"+teamId+"/add/"+playerId);
                Log.d("UUU",url.toString());

                JSONObject postDataParams = new JSONObject();


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

            String temp = "Success";
            if(status.equals(temp) == true) {
                Toast.makeText(getApplicationContext(),"Player added to team successfully.",Toast.LENGTH_LONG).show();
            }
            else    {
                Toast.makeText(getApplicationContext(),"Error in adding player in team",Toast.LENGTH_LONG).show();
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
