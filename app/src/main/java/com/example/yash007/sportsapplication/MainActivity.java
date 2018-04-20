package com.example.yash007.sportsapplication;

import android.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private EditText userName, userPassword;

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    private static String uniqueId = null;

    private static final String KEY_NAME = "example_key";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String id = id(getApplicationContext());
        if (isOnline()) {
            userName = (EditText) findViewById(R.id.userName);
            userPassword = (EditText) findViewById(R.id.userPassword);
            findViewById(R.id.googleSignIn).setOnClickListener(this);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();

            SignInButton signInButton = (SignInButton) findViewById(R.id.googleSignIn);
            signInButton.setSize(SignInButton.SIZE_STANDARD);

            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        }
        else    {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available! Please check your connection");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("Okay", (dialogInterface, i) -> alertDialog.dismiss());
                alertDialog.show();
            }
            catch (Exception e) {
                Log.d("Sporties", e.toString());
            }
        }

        SharedPreferences preferences = getSharedPreferences(Config.PREF_UNIQUE_ID, MODE_PRIVATE);
        String androidId = preferences.getString(Config.PREF_UNIQUE_ID,null);

        if(androidId.isEmpty() == false)    {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_fingerprint);
            dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.MATCH_PARENT;
            dialog.show();

            Button logout = dialog.findViewById(R.id.dialogFingerPrintLogout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            generateKey();

            if (cipherInit()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                FingerprintLoginHandler helper = new FingerprintLoginHandler(this);

                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);

    }

    //Google login method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //Google login supporting method
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null) {
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();

                Log.d("--------",personName);
                Log.d("--------",personGivenName);
                Log.d("--------",personFamilyName);
                Log.d("--------",personEmail);
                Log.d("--------",personId);
                //Log.d("--------",personPhoto.toString());


                new GoogleLoginPost(personName,personFamilyName,personEmail).execute();

            }
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    //Google login supporting method
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //google login supporting method
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleSignIn:
                signIn();
                break;
        }
    }


    public void openSignUpActivity(View v)  {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void openForgetPasswordActivity(View v)  {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void doLogin(View v)  {
        new LoginPost().execute();
    }

    //login with Credentials
    public class LoginPost extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Config.webUrl+"player/login");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("pEmail", userName.getText().toString());
                postDataParams.put("pPassword", userPassword.getText().toString());
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
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
            String firstName = null;
            String lastName = null;
            String email = null;
            String id = null;
            String address = null;
            String bio = null;
            String pHeight = null;
            String pWeight = null;
            Boolean pAccountStatus = null;
            Boolean pAuthenticated = null;
            String pLoginType = null;
            String pBirthday = null;
            String pPhone = null;

            try {
                JSONObject jsonObject = new JSONObject(result);
                status = jsonObject.getString("Status");
                //JSONObject profile = jsonObject.

                JSONObject profile = jsonObject.getJSONObject("user");
                Log.d("LOGIN_RESULT",profile.toString());

                firstName = profile.getString("pFirstName");
                lastName = profile.getString("pLastName");
                email = profile.getString("pEmail");
                id = profile.getString("_id");
                address = profile.getString("pAddress");
                bio = profile.getString("pBio");
                pHeight = profile.getString("pHeight");
                pWeight = profile.getString("pWeight");
                pAccountStatus = profile.getBoolean("pAccountStatus");
                pAuthenticated =profile.getBoolean("pAuthenticated");
                pLoginType = profile.getString("pLoginType");
                pBirthday = profile.getString("pBirthday");
                pPhone = profile.getString("pPhone");

                SharedPreferences.Editor editor = getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("pFirstName",firstName);
                editor.putString("pLastName",lastName);
                editor.putString("pEmail",email);
                editor.putString("id",id);
                editor.putString("pAddress",address);
                editor.putString("pBio",bio);
                editor.putString("pHeight",pHeight);
                editor.putString("pWeight",pWeight);
                editor.putBoolean("pAccountStatus",pAccountStatus);
                editor.putBoolean("pAuthenticated",pAuthenticated);
                editor.putString("pLoginType",pLoginType);
                editor.putString("pBirthday",pBirthday);
                editor.putString("pPhone",pPhone);
                editor.apply();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();

            if(status.equals("Success") == true) {
                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),"Incorrect username or password",Toast.LENGTH_LONG).show();
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

    //Internet connection checking method
    public boolean isOnline()   {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(MainActivity.this, "No internet connection!!",Toast.LENGTH_LONG).show();
            return false;
        }
        return  true;
    }

    //Method for generating unique id for device
    public synchronized static String id(Context context) {
        if (uniqueId == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(Config.PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueId = sharedPrefs.getString(Config.PREF_UNIQUE_ID,null);
            if(uniqueId == null)    {
                uniqueId = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(Config.PREF_UNIQUE_ID, uniqueId);
                editor.commit();
            }
        }
        return uniqueId;
    }

    //Google Login REST API with Our server

    //login with Credentials
    public class GoogleLoginPost extends AsyncTask<String, Void, String> {

        String firstName, lastName, email;

        public GoogleLoginPost(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this,R.style.AppCompatAlertDialogStyle);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {
            try {
                //URL url = new URL("https://studytutorial.in/post.php");
                URL url = new URL(Config.webUrl+"player/login/social");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("pFirstName", firstName);
                postDataParams.put("pLastName", lastName);
                postDataParams.put("pEmail",email);
                postDataParams.put("pBirthday","");
                postDataParams.put("pPassword","");
                postDataParams.put("pPhone","");
                postDataParams.put("pLoginType","Google");
                postDataParams.put("pAuthenticated","false");
                postDataParams.put("pAccountStatus","true");
                postDataParams.put("pAddress","");
                postDataParams.put("pBio","");
                postDataParams.put("pHeight","");
                postDataParams.put("pWeight","");
                postDataParams.put("pAdnroidId","");

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
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
                Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
            else    {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }


    //Fingerprint METHODS WILL BE HERE
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected  void generateKey()   {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        }
        catch (Exception e) {
            Log.d("Sporites",e.toString());
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        }
        catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            Log.d("Sporties",e.toString());
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}
