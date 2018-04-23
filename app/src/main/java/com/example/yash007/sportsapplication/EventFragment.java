package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class EventFragment extends android.support.v4.app.Fragment {

    String TAG = "Event";
    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> contactList;

    private FloatingActionButton addEventButton;

    Activity context;

    public String USER_ID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        context = getActivity();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        contactList = new ArrayList<>();

        lv = (ListView) context.findViewById(R.id.eventsList);


        addEventButton = (FloatingActionButton) context.findViewById(R.id.addEventButton);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences preferences = this.getActivity().getSharedPreferences(Config.PREF_NAME,MODE_PRIVATE);
        USER_ID = preferences.getString("id","");
        new GetEvents(USER_ID).execute();

        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.eventsList)   {

            MenuInflater inflater = context.getMenuInflater();
            inflater.inflate(R.menu.menu_event_option, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())   {
            case R.id.eventOptionView:

                return true;
            case R.id.eventOptionMap:

                return true;
            case R.id.eventOptionEdit:

                return true;
            case R.id.eventOptionDelete:
                    TextView id = info.targetView.findViewById(R.id.listEventId);
                    Toast.makeText(context, id.getText().toString(), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class GetEvents extends AsyncTask<Void, Void, Void> {

        String uId;
        public  GetEvents(String uId)   {
            this.uId = uId;
        }
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


            String jsonStr = sh.makeServiceCall(Config.webUrl + uId + "/teams/" + teamId + "/events");

            Log.d("TTTTT",Config.webUrl + uId + "/teams/" + teamId + "/events");
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("Profile");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String eventId = c.getString("_id");
                        //String eventDate = c.getString("eDate");
                        String eventType = c.getString("eType");
                        String eventTime = c.getString("eTime");
                        String eventVenue = c.getString("eVenue");
                        String eventAddress = c.getString("eAddress");
                        String eventNotes = c.getString("eNotes");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("eventId",eventId);
                        contact.put("eventType",eventType);
                        contact.put("eventTime",eventTime);
                        contact.put("eventVenue",eventVenue);
                        contact.put("eventAddress",eventAddress);
                        contact.put("eventNotes",eventNotes);

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
                    R.layout.list_events, new String[]{"eventId", "eventType", "eventTime",
                    "eventVenue","eventAddress","eventNotes"}, new int[]{R.id.listEventId, R.id.listEventType,
                    R.id.listEventTime, R.id.listEventVenue,R.id.listEventAddress, R.id.listEventNotes});

            lv.setAdapter(adapter);
        }
    }

}
