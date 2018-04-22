package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class MemberFragment extends android.support.v4.app.Fragment {

    String TAG = "MemberFragment";
    private ProgressDialog pDialog;
    private ListView lv;

    Activity context;
    ArrayList<HashMap<String, String>> contactList;

    FloatingActionButton addPlayerButton;
    String captainId, teamId;

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
                Intent intent = new Intent(context, SearchPlayerActivity.class);
                intent.putExtra("teamId", context.getIntent().getExtras().getString("id"));
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.PREF_NAME, Context.MODE_PRIVATE);
        captainId = sharedPreferences.getString("id","");
        teamId = context.getIntent().getExtras().getString("id");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView playerId = (TextView) view.findViewById(R.id.listPlayerId);
                Toast.makeText(context,playerId.getText().toString(), Toast.LENGTH_SHORT).show();
                new GetProfile(playerId.getText().toString().trim()).execute();
            }
        });

        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.playerList)   {

            MenuInflater inflater = context.getMenuInflater();
            inflater.inflate(R.menu.menu_player_option, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())   {
            case R.id.viewPlayerProfile:
                TextView playerId = info.targetView.findViewById(R.id.listPlayerId);
                new GetProfile(playerId.getText().toString().trim()).execute();
                return true;
            case R.id.deletePlayer:
                TextView playerIdDelete = info.targetView.findViewById(R.id.listPlayerId);
                new DeletePlayer(playerIdDelete.getText().toString(), teamId, captainId).execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }


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
            pDialog = new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
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

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_profile);
            dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;

            TextView fullName = (TextView) dialog.findViewById(R.id.dialogProfileFullName);
            TextView emailT = (TextView) dialog.findViewById(R.id.dialogProfileEmail);
            TextView phoneT = (TextView) dialog.findViewById(R.id.dialogProfilePhone);
            TextView heightWeightT = (TextView) dialog.findViewById(R.id.dialogProfileHeightWeight);
            TextView addressT = (TextView) dialog.findViewById(R.id.dialogProfileAddress);
            TextView birthdayT = (TextView) dialog.findViewById(R.id.dialogProfileBirthday);

            fullName.setText(firstName+" "+lastName);
            emailT.setText(email);
            phoneT.setText(phone);
            heightWeightT.setText(height + " cm." + weight + " kg.");
            birthdayT.setText(birthday);
            addressT.setText(address);

            dialog.show();

            Button close = (Button) dialog.findViewById(R.id.dialogProfileCloseButton);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

    }

    //login with Credentials
    public class DeletePlayer extends AsyncTask<String, Void, String> {

        String playerId;
        String teamId;
        String captainId;

        public DeletePlayer(String playerId, String teamId, String captainId)   {
            this.playerId = playerId;
            this.teamId = teamId;
            this.captainId = captainId;
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
                URL url = new URL(Config.webUrl+captainId+"/teams/"+teamId+"/delete/"+playerId);
                Log.d("YYY",url.toString());

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
                Toast.makeText(context,"Player removed from team",Toast.LENGTH_LONG).show();
            }
            else    {
                Toast.makeText(context,"Error in removing player. Please try again",Toast.LENGTH_LONG).show();
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
