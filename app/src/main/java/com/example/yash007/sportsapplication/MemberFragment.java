package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MemberFragment extends android.support.v4.app.Fragment {

    String TAG = "MemberFragment";
    private ProgressDialog pDialog;
    private ListView lv;

    Activity context;
    ArrayList<HashMap<String, String>> contactList;

    FloatingActionButton addPlayerButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        contactList = new ArrayList<>();

        lv = (ListView) context.findViewById(R.id.playerList);
        new GetPlayers().execute();

        addPlayerButton = (FloatingActionButton) context.findViewById(R.id.addPlayerButton);

        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,SearchPlayerActivity.class));
            }
        });
    }

    public class GetPlayers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String teamId = context.getIntent().getExtras().getString("id");
            String jsonStr = sh.makeServiceCall(Config.webUrl+"team/"+teamId+"/players");

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("Players");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String id = c.getString("_id");
                        String firstName = c.getString("pFirstName");
                        String lastName = c.getString("pLastName");
                        String email = c.getString("pEmail");
                        String height = "Height : " + c.getString("pHeight") + " cm. Weight: " + c.getString("pWeight") + " k g.";

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
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,
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
                    context, contactList,
                    R.layout.list_players, new String[]{"id","name", "email",
                    "height"}, new int[]{R.id.listPlayerId,R.id.listPlayerName,
                    R.id.listPlayerEmail, R.id.listPlayerHeightWeight});

            lv.setAdapter(adapter);
        }

    }
}
