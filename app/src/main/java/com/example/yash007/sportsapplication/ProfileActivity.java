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
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.BatchUpdateException;

public class ProfileActivity extends AppCompatActivity {

    private TextView userProfileName, userProfileAddress, userCardEmail, userCardDob, userCardHeight, userCardWeight;
    private TextView userCardPhone, userCardGender, userCardStatus, userCardEmailNotification;
    private ImageView userEmailImageButton;
    private ImageView userCallImageButton;

    private ImageView openChangeEmail, openChangePhone, openChangeDob, openChangeHeight, openChangeWeight;

    public SharedPreferences sharedPreferences;

    //shared prefs variable
    public String firstName, lastName, email, phone, height, weight, dob;

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
        userCardDob = (TextView) findViewById(R.id.userCardDob);
        userCardHeight = (TextView) findViewById(R.id.userCardHeight);
        userCardWeight = (TextView) findViewById(R.id.userCardWeight);

        userEmailImageButton = (ImageView) findViewById(R.id.userEmailImageButton);
        userCallImageButton = (ImageView) findViewById(R.id.userCallImageButton);

        openChangeEmail = (ImageView) findViewById(R.id.openChangeEmail);
        openChangePhone = (ImageView) findViewById(R.id.openChangePhone);
        openChangeDob = (ImageView) findViewById(R.id.openChangeDob);
        openChangeHeight = (ImageView) findViewById(R.id.openChangeHeight);
        openChangeWeight = (ImageView) findViewById(R.id.openChangeWeight);

        sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        firstName = sharedPreferences.getString("pFirstName","John");
        lastName = sharedPreferences.getString("pLastName","Doe");
        email = sharedPreferences.getString("pEmail","johnDoe@gmail.com");
        phone = sharedPreferences.getString("pPhone","");
        height = sharedPreferences.getString("pHeight","");
        weight = sharedPreferences.getString("pWeight","");
        dob = sharedPreferences.getString("pDob","");




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

        userCardDob.setText(sharedPreferences.getString("pDob","").toString().trim());
        userCardHeight.setText(sharedPreferences.getString("pHeight","").toString().trim());
        userCardWeight.setText(sharedPreferences.getString("pWeight","").toString().trim());

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

                        String[] arguments = new String[2];
                        arguments[0] = firstNameEdit.getText().toString().trim();
                        arguments[1] = lastNameEdit.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Name", arguments);
                    }
                });

                dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.show();
            }
        });

        openChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_email);

                EditText emailEdit = (EditText) dialog.findViewById(R.id.dialogEmail);
                emailEdit.setText(email.toString().trim());

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeEmailCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangeEmailOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //API Code will be here

                        String[] arguments = new String[1];
                        arguments[0] = emailEdit.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Email", arguments);
                    }
                });

                dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.show();
            }
        });

        openChangePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_phone);

                EditText phoneEdit = (EditText) dialog.findViewById(R.id.dialogPhone);
                phoneEdit.setText(phone.toString().trim());

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangePhoneCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangePhoneOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okay.setOnClickListener(new View.OnClickListener() {
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

        openChangeDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_dob);

                EditText dob = (EditText) dialog.findViewById(R.id.dialogDob);

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeDobCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangeDobOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okay.setOnClickListener(new View.OnClickListener() {
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

        openChangeHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_height);

                EditText height = (EditText) dialog.findViewById(R.id.dialogHeight);

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeHeightCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangeHeightOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okay.setOnClickListener(new View.OnClickListener() {
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

        openChangeWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_weight);

                EditText height = (EditText) dialog.findViewById(R.id.dialogHeight);

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeWeightCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangeWeightOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okay.setOnClickListener(new View.OnClickListener() {
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
