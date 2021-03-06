package com.example.yash007.sportsapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.BatchUpdateException;

public class ProfileActivity extends AppCompatActivity {

    private TextView userProfileName, userProfileAddress, userCardEmail, userCardDob, userCardHeight, userCardWeight, userCardAddress, userCardBio;
    private TextView userCardPhone, userCardStatus;
    private ImageView userEmailImageButton;
    private ImageView userCallImageButton;

    private ImageView openChangeEmail, openChangePhone, openChangeDob, openChangeHeight, openChangeWeight, openChangePassword, openChangeAddress, openChangeBio;

    public SharedPreferences sharedPreferences;

    //shared prefs variable
    public String firstName, lastName, email, phone, height, weight, dob, address, bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfileName = (TextView) findViewById(R.id.userProfileName);
        userProfileAddress = (TextView) findViewById(R.id.userProfileAddress);

        userCardEmail = (TextView) findViewById(R.id.userCardEmail);
        userCardPhone = (TextView) findViewById(R.id.userCardPhone);
        userCardStatus = (TextView) findViewById(R.id.userCardStatus);
        userCardDob = (TextView) findViewById(R.id.userCardDob);
        userCardHeight = (TextView) findViewById(R.id.userCardHeight);
        userCardWeight = (TextView) findViewById(R.id.userCardWeight);
        userCardAddress = (TextView) findViewById(R.id.userCardAddress);
        userCardBio = (TextView) findViewById(R.id.userCardBio);


        userEmailImageButton = (ImageView) findViewById(R.id.userEmailImageButton);
        userCallImageButton = (ImageView) findViewById(R.id.userCallImageButton);

        openChangeEmail = (ImageView) findViewById(R.id.openChangeEmail);
        openChangePhone = (ImageView) findViewById(R.id.openChangePhone);
        openChangeDob = (ImageView) findViewById(R.id.openChangeDob);
        openChangeHeight = (ImageView) findViewById(R.id.openChangeHeight);
        openChangeWeight = (ImageView) findViewById(R.id.openChangeWeight);
        openChangePassword = (ImageView) findViewById(R.id.openChangePassword);
        openChangeAddress = (ImageView) findViewById(R.id.openChangeAddress);
        openChangeBio = (ImageView) findViewById(R.id.openChangeBio);


        sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        firstName = sharedPreferences.getString("pFirstName","John");
        lastName = sharedPreferences.getString("pLastName","Doe");
        email = sharedPreferences.getString("pEmail","johnDoe@gmail.com");
        phone = sharedPreferences.getString("pPhone","");
        height = sharedPreferences.getString("pHeight","");
        weight = sharedPreferences.getString("pWeight","");
        dob = sharedPreferences.getString("pDob","");
        address = sharedPreferences.getString("pAddress","");
        bio = sharedPreferences.getString("pBio","");

        userProfileAddress.setText(address);
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
        userCardAddress.setText(address);
        userCardBio.setText(bio);
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
                        updateValues();
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
                        String[] arguments = new String[1];
                        arguments[0] = phoneEdit.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Phone", arguments);
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

                EditText heightEdit = (EditText) dialog.findViewById(R.id.dialogHeight);
                heightEdit.setText(height.toString().trim());
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
                        String[] arguments = new String[1];
                        arguments[0] = heightEdit.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Height", arguments);
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

                EditText weightEdit = (EditText) dialog.findViewById(R.id.dialogWeight);
                weightEdit.setText(weight.toString().trim());

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
                        String[] arguments = new String[1];
                        arguments[0] = weightEdit.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Weight", arguments);
                    }
                });

                dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.show();

            }
        });


        openChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_password);

                EditText oldPasswordEdit = (EditText) dialog.findViewById(R.id.dialogOldPassword);
                EditText newPasswordEdit = (EditText) dialog.findViewById(R.id.dialogNewPassword);
                EditText confirmPasswordEdit = (EditText) dialog.findViewById(R.id.dialogConfirmPassword);

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangePasswordCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangePasswordOkayButton);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String newPassword, confirmPassword;
                        newPassword = newPasswordEdit.getText().toString().trim();
                        confirmPassword = confirmPasswordEdit.getText().toString().trim();

                        if(newPassword.equals(confirmPassword)) {
                            dialog.dismiss();
                            //API Code will be here
                            String[] arguments = new String[3];
                            arguments[0] = email;
                            arguments[1] = oldPasswordEdit.getText().toString().trim();
                            arguments[2] = newPassword;

                            new ApiController(ProfileActivity.this, "Password", arguments);
                        }
                        else    {
                            Toast.makeText(ProfileActivity.this, "New and Confirm password must be same", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.show();

            }
        });


        openChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_address);

                EditText addressEdit = (EditText) dialog.findViewById(R.id.dialogAddress);
                addressEdit.setText(address.toString().trim());

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeAddressCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangeAddressOkayButton);

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
                        arguments[0] = addressEdit.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Address", arguments);
                    }
                });

                dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.show();
            }
        });

        openChangeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_change_bio);

                EditText bioText = (EditText) dialog.findViewById(R.id.dialogBio);
                bioText.setText(bio.toString().trim());

                Button cancel = (Button) dialog.findViewById(R.id.dialogChangeBioCancelButton);
                Button okay = (Button) dialog.findViewById(R.id.dialogChangeBioOkayButton);

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
                        arguments[0] = bioText.getText().toString().trim();

                        new ApiController(ProfileActivity.this, "Bio", arguments);
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

    public void updateValues()  {
        sharedPreferences = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
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
        userCardAddress.setText(sharedPreferences.getString("pAddress",""));
        userCardBio.setText(sharedPreferences.getString("pBio",""));
        userProfileAddress.setText(sharedPreferences.getString("pAddress",""));
        firstName = sharedPreferences.getString("pFirstName","John");
        lastName = sharedPreferences.getString("pLastName","Doe");
        email = sharedPreferences.getString("pEmail","johnDoe@gmail.com");
        phone = sharedPreferences.getString("pPhone","");
        height = sharedPreferences.getString("pHeight","");
        weight = sharedPreferences.getString("pWeight","");
        dob = sharedPreferences.getString("pDob","");
        address = sharedPreferences.getString("pAddress","");
        bio = sharedPreferences.getString("pBio","");
    }
}
