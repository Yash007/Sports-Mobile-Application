package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.Dialog;
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

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

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

import static android.content.Context.MODE_PRIVATE;


public class EventFragment extends android.support.v4.app.Fragment {

    String TAG = "Event";
    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> contactList;

    private FloatingActionButton addEventButton;

    Activity context;

    public String USER_ID;
    String captainId, teamId;

    public String eventType, eventDate, eventTime,  eventVenue,  eventAddress,  eventNotes;

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
        captainId = USER_ID;
        teamId = context.getIntent().getExtras().getString("id");
        
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
                    TextView eventIdEdit = info.targetView.findViewById(R.id.listEventId);
                    Intent intent = new Intent(context, EventEditActivity.class);
                    Log.d("TTTTT",teamId);
                        intent.putExtra("teamId",teamId);
                        intent.putExtra("captainId",captainId);
                        intent.putExtra("eventId",eventIdEdit.getText().toString().trim());
                    startActivity(intent);
                return true;
            case R.id.eventOptionDelete:
                    TextView eventId = info.targetView.findViewById(R.id.listEventId);
                    new DeleteEvent(teamId, captainId, eventId.getText().toString().trim()).execute();
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
                        //contact.put("eventDate",eventDate);
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
//            ListAdapter adapter = new SimpleAdapter(
//                    context, contactList,
//                    R.layout.list_events, new String[]{"eventId", "eventType","eventDate", "eventTime",
//                    "eventVenue","eventAddress","eventNotes"}, new int[]{R.id.listEventId, R.id.listEventType, R.id.listEventDate,
//                    R.id.listEventTime, R.id.listEventVenue,R.id.listEventAddress, R.id.listEventNotes});

            ListAdapter adapter = new SimpleAdapter(
                    context, contactList,
                    R.layout.list_events, new String[]{"eventId", "eventType", "eventTime",
                    "eventVenue","eventAddress","eventNotes"}, new int[]{R.id.listEventId, R.id.listEventType,
                    R.id.listEventTime, R.id.listEventVenue,R.id.listEventAddress, R.id.listEventNotes});

            lv.setAdapter(adapter);
        }
    }

    public class DeleteEvent extends AsyncTask<String, Void, String> {

        String teamId;
        String captainId;
        String eventId;

        public DeleteEvent(String teamId, String captainId,String eventId)   {
            this.teamId = teamId;
            this.captainId = captainId;
            this.eventId = eventId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL(Config.webUrl+captainId+"/teams/"+teamId+"/events/"+eventId);
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
                Toast.makeText(context,"Event removed successfully.",Toast.LENGTH_LONG).show();
            }
            else    {
                Toast.makeText(context,"Error in removing event. Please try again",Toast.LENGTH_LONG).show();
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
