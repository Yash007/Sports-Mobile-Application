package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class TeamFragment extends android.support.v4.app.Fragment {

    String TAG = "MemberFragment";
    private ProgressDialog pDialog;

    TextView teamName, teamAgeGroup, teamSports, teamLocation, teamGender;
    ImageView teamPic;
    Activity context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        context = getActivity();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        teamName = (TextView) getView().findViewById(R.id.teamFragmentName);
        teamSports = (TextView) getView().findViewById(R.id.teamFragmentSports);
        teamGender = (TextView) getView().findViewById(R.id.teamFragmentGender);
        teamAgeGroup = (TextView) getView().findViewById(R.id.teamFragmentAgeGroup);
        teamLocation = (TextView) getView().findViewById(R.id.teamFragmentLocation);
        teamPic = (ImageView) getView().findViewById(R.id.teamFragmentPic);

        teamName.setText(context.getIntent().getExtras().getString("title"));
        new GetTeamInfo().execute();
    }

    public class GetTeamInfo extends AsyncTask<Void, Void, Void> {

        String tempTeamName, tempTeamSports, tempTeamGender, tempTeamAgeGroup, tempTeamLocation;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tempTeamSports = "cricket";
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
            String jsonStr = sh.makeServiceCall(Config.webUrl+"team/"+teamId);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("Team");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        tempTeamName = c.getString("tName");
                        tempTeamSports = c.getString("tSports");
                        tempTeamGender = c.getString("tGender");
                        tempTeamAgeGroup = c.getString("tAgeGroup");
                        tempTeamLocation = c.getString("tAddress");

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

            if (pDialog.isShowing())
                pDialog.dismiss();

            teamName.setText(tempTeamName);
            teamAgeGroup.setText(tempTeamAgeGroup);
            teamGender.setText(tempTeamGender);
            teamLocation.setText(tempTeamLocation);
            teamSports.setText(tempTeamSports);


            switch (tempTeamSports)    {
                case "cricket":
                    teamPic.setImageResource(R.drawable.cricket);
                    break;
                case "Cricket":
                    teamPic.setImageResource(R.drawable.cricket);
                    break;
                case "Badminton":
                    teamPic.setImageResource(R.drawable.badminton);
                    break;
                case "Baseball":
                    teamPic.setImageResource(R.drawable.baseball);
                    break;
                case "Basketball":
                    teamPic.setImageResource(R.drawable.basketball);
                    break;
                case "Chess":
                    teamPic.setImageResource(R.drawable.chess);
                    break;
                case "Football":
                    teamPic.setImageResource(R.drawable.football);
                    break;
                case "Golf":
                    teamPic.setImageResource(R.drawable.golf);
                    break;
                case "Gymnastics":
                    teamPic.setImageResource(R.drawable.gymnastics);
                    break;
                case "Hockey(Ice)":
                    teamPic.setImageResource(R.drawable.ice_hockey);
                    break;
                case "Hockey(Field)":
                    teamPic.setImageResource(R.drawable.hockey);
                    break;
                case "Polo":
                    teamPic.setImageResource(R.drawable.polo);
                    break;
                case "Water Polo":
                    teamPic.setImageResource(R.drawable.water);
                    break;
                case "Soccer":
                    teamPic.setImageResource(R.drawable.soccer);
                    break;
                case "Sailing":
                    teamPic.setImageResource(R.drawable.sailboat);
                    break;
                case "Tennis":
                    teamPic.setImageResource(R.drawable.tennis);
                    break;
                case "Rugby":
                    teamPic.setImageResource(R.drawable.rugby);
                    break;
                case "Volleyball":
                    teamPic.setImageResource(R.drawable.volleyball);
                    break;
                case "Wrestling":
                    teamPic.setImageResource(R.drawable.wrestling);
                    break;
                default:
                    teamPic.setImageResource(R.drawable.team);
                    break;
            }
        }
    }
}
