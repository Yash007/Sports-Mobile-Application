package com.example.yash007.sportsapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private TextView userProfileName, userProfileAddress, userCardEmail;
    private TextView userCardPhone, userCardGender, userCardStatus, userCardEmailNotification;
    private ImageButton userEmailImageButton;
    private ImageButton userCallImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfileName = (TextView) findViewById(R.id.userProfileName);
        userProfileAddress = (TextView) findViewById(R.id.userProfileAddress);

        userCardEmail = (TextView) findViewById(R.id.userCardEmail);
        userCardPhone = (TextView) findViewById(R.id.userCardPhone);
        userCardGender = (TextView) findViewById(R.id.userCardGender);
        userCardStatus = (TextView) findViewById(R.id.userCardStatus);
        userCardEmailNotification = (TextView) findViewById(R.id.userCardEmailNot);

        userEmailImageButton = (ImageButton) findViewById(R.id.userEmailImageButton);
        userCallImageButton = (ImageButton) findViewById(R.id.userCallImageButton);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        userProfileName.setText(sharedPreferences.getString("pFirstName", "John") + " " + sharedPreferences.getString("pLastName", " Doe"));
        userCardEmail.setText(sharedPreferences.getString("pEmail","johndoe@gmail.com"));
        userCardPhone.setText(sharedPreferences.getString("pPhone","+1(647)920-7670"));
        Boolean auth = sharedPreferences.getBoolean("pAuthenticated",false);
        if(auth == false)   {
            userCardStatus.setText("Not authorized");
        }
        else    {
            userCardStatus.setText("Authenticated");
        }


        userEmailImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sharedPreferences.getString("pEmail", "")});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sporites");
                intent.putExtra(Intent.EXTRA_TEXT, "Regards, \n Sporties Team");
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        userCallImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + sharedPreferences.getString("pPhoneNumber", "6479207670")));

                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted

                    ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);


                } else {
                    startActivity(intent);
                }
            }
        });
    }
}
