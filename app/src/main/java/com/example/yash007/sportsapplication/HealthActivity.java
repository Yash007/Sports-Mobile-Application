package com.example.yash007.sportsapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HealthActivity extends Activity {

    public static final String APP_TAG = "SimpleHealth";
    private static HealthActivity mInstance = null;
    private HealthDataStore mStore;
    private HealthConnectionErrorResult mConnError;
    private Set<HealthPermissionManager.PermissionKey> mKeySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        mInstance = this;
        mKeySet = new HashSet<HealthPermissionManager.PermissionKey>();
        mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
        HealthDataService healthDataService = new HealthDataService();

        try {
            healthDataService.initialize(this);
            Log.d(APP_TAG,"onCreate() initialized");
            Toast.makeText(getApplicationContext(),"onCreate() initialized",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.d(APP_TAG,"onCreate: error:{}", e);
            Toast.makeText(getApplicationContext(),"onCreate:Error " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        mStore = new HealthDataStore(this, mConnectionLister);
        mStore.connectService();
        Log.d(APP_TAG,"onCreate() connected");
        Toast.makeText(getApplicationContext(),"onCreate() Connected :" ,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mStore.disconnectService();
        super.onDestroy();
    }

    private final HealthDataStore.ConnectionListener mConnectionLister = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            Log.d(APP_TAG, "Health data service is connected.");
            Toast.makeText(getApplicationContext(),"onConnected() ConnectionListener :" ,Toast.LENGTH_SHORT).show();
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
            try {
                // Check whether the permissions that this application needs are acquired
                Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);
                if (resultMap.containsValue(Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    Log.d(APP_TAG, "Health data service is connect.");
                    pmsManager.requestPermissions(mKeySet, HealthActivity.this).setResultListener(mPermissionListener);
                }
                else {
                    // Get the current step count and display it // ...
                }
            }
            catch (Exception e) {
                Log.e(APP_TAG, e.getClass().getName() + " - " + e.getMessage());
                Log.e(APP_TAG, "Permission setting fails.");
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult healthConnectionErrorResult) {
            Log.d(APP_TAG, "Health data service is not available.");
            Toast.makeText(HealthActivity.this,"onConnectionFailed(): Error",Toast.LENGTH_SHORT).show();
            showConnectionFailureDialog(healthConnectionErrorResult);
        }

        @Override
        public void onDisconnected() {
            Log.d(APP_TAG, "Health data service is disconnected.");
        }
    };

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener = new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
        @Override
        public void onResult(HealthPermissionManager.PermissionResult result) {
            Log.d(APP_TAG, "Permission callback is received.");
            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();
            if (resultMap.containsValue(Boolean.FALSE)) {
                // Requesting permission fails

            }
            else {
                // Get the current step count and display it
            }
        }
    };

    private void showConnectionFailureDialog(HealthConnectionErrorResult errorResult)   {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        mConnError = errorResult;
        String message = "Connection with S Health is not available";

        if (mConnError.hasResolution()) {
            switch(errorResult.getErrorCode()) {
                case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
                    message = "Please install S Health";
                    break;
                case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
                    message = "Please upgrade S Health";
                    break;
                case HealthConnectionErrorResult.PLATFORM_DISABLED:
                    message = "Please enable S Health";
                    break;
                case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
                    message = "Please agree with S Health policy";
                    break;
                default:
                    message = "Please make S Health available";
                    break;
            }
        }

        alert.setMessage(message);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (mConnError.hasResolution()) {
                    mConnError.resolve(mInstance);
                }
            }
        });

        if (errorResult.hasResolution()) {
            alert.setNegativeButton("Cancel", null);
        }
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_health,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:

                break;
            case R.id.healthConnect:

                break;
            default:
                break;
        }
        return true;
    }
}
