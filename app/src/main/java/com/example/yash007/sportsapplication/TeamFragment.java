package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class TeamFragment extends android.support.v4.app.Fragment {

    TextView teamName, teamAgeGroup, teamSports, teamLocation, teamGender;
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

        teamName.setText(context.getIntent().getExtras().getString("title"));
    }
}
