package ttit.com.shuvo.terraintracker;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static ttit.com.shuvo.terraintracker.Constants.IS_LOGIN;
import static ttit.com.shuvo.terraintracker.Constants.LOGIN_FILE_NAME;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.UpdateAvailability;

import ttit.com.shuvo.terraintracker.dash_board.Dashboard;
import ttit.com.shuvo.terraintracker.loginFile.Login;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();

    SharedPreferences loginOrNot;
    boolean login = false;

    AppUpdateManager appUpdateManager;

    boolean perm = false;

    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {

                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setTitle("Update Failed!")
                                    .setMessage("Failed to update the app. Please try again.")
                                    .setIcon(R.drawable.tracker_logo)
                                    .setPositiveButton("Retry", (dialog, which) -> getAppUpdate())
                                    .setNegativeButton("Cancel", (dialog, which) -> finish());
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });

    private final ActivityResultLauncher<String[]> locationResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && Boolean.TRUE.equals(result.get(key));
        }
        if (allGranted) {
            System.out.println("HOLA1");
            goToActivityMap();
        }
        else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showDialog("Location Permission!", "This app needs the precise location permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                    perm = true;
                });
            }
            else {
                System.out.println("HOLA2");
                enableLocation();
            }
        }
    });


    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.navigationBars() | WindowInsets.Type.statusBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Android 4.4 to 10 (API 19–29)
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        loginOrNot = getSharedPreferences(LOGIN_FILE_NAME,MODE_PRIVATE);
        login = loginOrNot.getBoolean(IS_LOGIN,false);

        hideSystemUI();
        perm = false;
        getAppUpdate();
    }

    private void getAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE))  {

                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                        activityResultLauncher, AppUpdateOptions
                                .newBuilder(IMMEDIATE)
                                .build());
            }
            else {
                System.out.println("No update available");
                enableLocation();
            }
        });
        appUpdateInfoTask.addOnFailureListener(e -> {
            System.out.println("FAILED TO LISTEN");
            enableLocation();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                                        activityResultLauncher,AppUpdateOptions
                                                .newBuilder(IMMEDIATE)
                                                .build());
                            }
                        });
        if (perm) {
            perm = false;
            enableLocation();
        }
    }

    private void goToActivityMap() {
        mHandler.postDelayed(() -> {
            Intent intent;
            if (login) {
                intent = new Intent(MainActivity.this, Dashboard.class);
            } else {
                intent = new Intent(MainActivity.this, Login.class);
            }
            startActivity(intent);
            finish();

        }, 1500);
    }

    public void showDialog(String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveListener) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setIcon(R.drawable.tracker_logo)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Log.i("Ekhane", "1");

//            enableBackgroundLocation();
            goToActivityMap();

        }
        else {
            Log.i("Ekhane", "2");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Log.i("Ekhane", "3");
                showDialog("Location Permission!", "This app needs the location permission for functioning.", "OK", (dialogInterface, i) -> locationResultLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}));
            }
            else {
                Log.i("Ekhane", "4");
                locationResultLauncher.launch(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION});
            }
        }
    }
}