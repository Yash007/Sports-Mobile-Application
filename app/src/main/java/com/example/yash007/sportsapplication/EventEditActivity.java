package com.example.yash007.sportsapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Iterator;

public class EventEditActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    String TAG = "Event";
    private ProgressDialog pDialog;
    public String eventType, eventDate, eventTime,  eventVenue,  eventAddress,  eventNotes;

    public Spinner eventEditType;
    public EditText eventEditDate, eventEditTime, eventEditVenue, eventEditAddres, eventEditNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        eventEditType = (Spinner) findViewById(R.id.eventEditType);
        eventEditDate = (EditText) findViewById(R.id.eventEditDate);
        eventEditTime = (EditText) findViewById(R.id.eventEditTime);
        eventEditVenue = (EditText) findViewById(R.id.eventEditVenue);
        eventEditAddres = (EditText) findViewById(R.id.eventEditAddress);
        eventEditNotes = (EditText) findViewById(R.id.eventEditNotes);

        new GetEventById(getIntent().getExtras().getString("teamId"),
                getIntent().getExtras().getString("captainId"),
                getIntent().getExtras().getString("eventId")
                ).execute();
    }

    public class GetEventById extends AsyncTask<Void, Void, Void> {

        String teamId, captainId, eventId;

        String eventType,eventTime, eventDate, eventVenue, eventAddress, eventNotes;
        public  GetEventById(String teamId, String captainId,String eventId)   {
            this.teamId = teamId;
            this.captainId = captainId;
            this.eventId = eventId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(EventEditActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String teamId = getIntent().getExtras().getString("id");


            String jsonStr = sh.makeServiceCall(Config.webUrl + captainId + "/teams/" + this.teamId + "/events/"+eventId);

            Log.d("TTTTT",Config.webUrl + captainId + "/teams/" + this.teamId + "/events/"+eventId);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("Event");

                    // looping through All Contacts

                        JSONObject c = contacts.getJSONObject(0);

                        String eventId = c.getString("_id");
                        //String eventDate = c.getString("eDate");
                        eventType = c.getString("eType");
                        eventTime = c.getString("eTime");
                        eventVenue = c.getString("eVenue");
                        eventAddress = c.getString("eAddress");
                        eventNotes = c.getString("eNotes");

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EventEditActivity.this,
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
                        Toast.makeText(EventEditActivity.this,
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

            setEventData(eventType, eventDate, eventTime, eventVenue, eventAddress, eventNotes);
        }
    }

    public void setEventData(String eventType, String eventDate, String eventTime, String eventVenue, String eventAddress, String eventNotes)   {
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventVenue = eventVenue;
        this.eventAddress = eventAddress;
        this.eventNotes = eventNotes;

        eventEditDate.setText(eventDate);
        eventEditTime.setText(eventTime);
        eventEditVenue.setText(eventVenue);
        eventEditAddres.setText(eventAddress);
        eventEditNotes.setText(eventNotes);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(EventEditActivity.this, R.array.eventType, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventEditType.setAdapter(adapter1);
        if (eventType != null) {
            int spinnerPosition1 = adapter1.getPosition(eventType);
            eventEditType.setSelection(spinnerPosition1);
        }

        eventEditVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });
    }


    public class UpdateEvent extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventEditActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                SharedPreferences preferences = getSharedPreferences(Config.PREF_NAME,MODE_PRIVATE);
                String id = preferences.getString("id","");

                SharedPreferences preferences1 = getSharedPreferences(Config.PREF_TEAM, MODE_PRIVATE);
                String teamId = preferences1.getString("teamId","");

                //URL url = new URL("https://studytutorial.in/post.php");
                URL url = new URL(Config.webUrl+id+"/teams/"+teamId+"/events");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("eName","");
                postDataParams.put("eDate",eventEditDate.getText().toString());
                postDataParams.put("eTime",eventEditTime.getText().toString());
                postDataParams.put("eType",eventEditType.getSelectedItem().toString());
                postDataParams.put("eAddress",eventEditAddres.getText().toString());
                postDataParams.put("eVenue",eventEditVenue.getText().toString());
                postDataParams.put("eNotes",eventEditNotes.getText().toString());
                postDataParams.put("tId",teamId.toString());
                postDataParams.put("eCreator",getIntent().getExtras().getString("captainId").toString());

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

            if(status.equals("Success") == true) {
                Toast.makeText(getApplicationContext(),"Event updated successfully.",Toast.LENGTH_LONG).show();

                //startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),"Error in creating Event. Please try again",Toast.LENGTH_LONG).show();
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

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());

                // Format the place's details and display them in the TextView.
                eventEditVenue.setText(place.getName().toString().trim());
                eventEditAddres.setText(place.getAddress().toString().trim());

                // Display attributions if required.
                CharSequence attributions = place.getAttributions();
//                if (!TextUtils.isEmpty(attributions)) {
//                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
//                } else {
//                    mPlaceAttribution.setText("");
//                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }
}
