package com.example.yash007.sportsapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.BatchUpdateException;

public class ProfileActivity extends AppCompatActivity {

    private TextView userProfileName, userProfileAddress, userCardEmail;
    private TextView userCardPhone, userCardGender, userCardStatus, userCardEmailNotification;
    private ImageView userEmailImageButton;
    private ImageView userCallImageButton;

    public SharedPreferences sharedPreferences;

    //shared prefs variable
    public String firstName, lastName;

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

        userEmailImageButton = (ImageView) findViewById(R.id.userEmailImageButton);
        userCallImageButton = (ImageView) findViewById(R.id.userCallImageButton);

        sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        firstName = sharedPreferences.getString("pFirstName","John");
        lastName = sharedPreferences.getString("pLastName","Doe");

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


        userProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(ProfileActivity.this);

                dialog.setContentView(R.layout.dialog_change_name);
                EditText firstNameEdit = (EditText) dialog.findViewById(R.id.dialogFirstName);
                EditText lastNameEdit = (EditText) dialog.findViewById(R.id.dialogLastName);

                firstNameEdit.setText(firstName.toString().trim());
                lastNameEdit.setText(lastName.toString().trim());

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeNameCancelButton);
                Button okaye = (Button) dialog.findViewById(R.id.dialogChangeNameOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okaye.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //API Code will be here
                    }
                });

                dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.show();
            }
        });
    }

    public void openFingerPrint(View view)    {
        startActivity(new Intent(ProfileActivity.this, FingerprintActivity.class));
    }

    public void openChangePassword(View view)   {
        startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class ));
    }
}
