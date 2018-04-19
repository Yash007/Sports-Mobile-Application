package com.example.yash007.sportsapplication;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
                    //some operation
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
                    R.layout.list_search_players, new String[]{"id","name", "email",
                    "mobile"}, new int[]{R.id.listPlayerId,R.id.listPlayerName,
                    R.id.listPlayerEmail, R.id.listPlayerHeightWeight});

            lv.setAdapter(adapter);
        }

    }


}
