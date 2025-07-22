package ttit.com.shuvo.terraintracker.loginFile;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.AllUrlList;
import ttit.com.shuvo.terraintracker.CenterList;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.dash_board.Dashboard;
import ttit.com.shuvo.terraintracker.loginFile.dialoges.SelectCenterDialogue;
import ttit.com.shuvo.terraintracker.loginFile.interfaces.CallBackListener;
import ttit.com.shuvo.terraintracker.progressBar.WaitProgress;

import static ttit.com.shuvo.terraintracker.Constants.CENTER_API;
import static ttit.com.shuvo.terraintracker.Constants.CompanyName;
import static ttit.com.shuvo.terraintracker.Constants.DATABASE_NAME;
import static ttit.com.shuvo.terraintracker.Constants.DEPT_NAME;
import static ttit.com.shuvo.terraintracker.Constants.DESG_NAME;
import static ttit.com.shuvo.terraintracker.Constants.DESG_PRIORITY;
import static ttit.com.shuvo.terraintracker.Constants.DIV_ID;
import static ttit.com.shuvo.terraintracker.Constants.DIV_NAME;
import static ttit.com.shuvo.terraintracker.Constants.EMP_CONTACT;
import static ttit.com.shuvo.terraintracker.Constants.EMP_F_NAME;
import static ttit.com.shuvo.terraintracker.Constants.EMP_ID;
import static ttit.com.shuvo.terraintracker.Constants.EMP_L_NAME;
import static ttit.com.shuvo.terraintracker.Constants.EMP_MAIL;
import static ttit.com.shuvo.terraintracker.Constants.IS_LOGIN;
import static ttit.com.shuvo.terraintracker.Constants.JOINING_DATE;
import static ttit.com.shuvo.terraintracker.Constants.JSD_ID_LOGIN;
import static ttit.com.shuvo.terraintracker.Constants.JSD_OBJECTIVE;
import static ttit.com.shuvo.terraintracker.Constants.JSM_CODE;
import static ttit.com.shuvo.terraintracker.Constants.JSM_NAME;
import static ttit.com.shuvo.terraintracker.Constants.LOGIN_FILE_NAME;
import static ttit.com.shuvo.terraintracker.Constants.MyPREFERENCES;
import static ttit.com.shuvo.terraintracker.Constants.USR_NAME;
import static ttit.com.shuvo.terraintracker.Constants.api_url_front;
import static ttit.com.shuvo.terraintracker.Constants.checked;
import static ttit.com.shuvo.terraintracker.Constants.user_emp_code;
import static ttit.com.shuvo.terraintracker.Constants.user_password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements CallBackListener {

    TextInputEditText user;
    TextInputEditText pass;

    TextView login_failed;

    Button login;

    CheckBox checkBox;

    String userName = "";
    String password = "";

    WaitProgress waitProgress = new WaitProgress();
    private Boolean conn = false;
    private Boolean infoConnected = false;
    private Boolean connected = false;

    SharedPreferences sharedpreferences;
    SharedPreferences sharedPreferencesForLoginInfo;

    String getUserName = "";
    String getPassword = "";
    boolean getChecked = false;

    public static ArrayList<UserInfoList> userInfoLists;
    public static ArrayList<UserDesignation> userDesignations;

    ArrayList<AllUrlList> urls;
    String text_url = "https://raw.githubusercontent.com/shuvo934/Story/refs/heads/master/hrmServers";
    ArrayList<CenterList> centerLists;
    String emp_id = "";
    String emp_code = "";
    boolean adminFlag = false;

    Logger logger = Logger.getLogger(Login.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfoLists = new ArrayList<>();
        userDesignations = new ArrayList<>();
        user = findViewById(R.id.user_name_given);
        pass = findViewById(R.id.password_given);
        checkBox = findViewById(R.id.remember_checkbox);

        login_failed = findViewById(R.id.email_pass_miss);

        login = findViewById(R.id.login_button);

        sharedPreferencesForLoginInfo = getSharedPreferences(LOGIN_FILE_NAME,MODE_PRIVATE);

        sharedpreferences = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        getUserName = sharedpreferences.getString(user_emp_code,null);
        getPassword = sharedpreferences.getString(user_password,null);
        getChecked = sharedpreferences.getBoolean(checked,false);

        if (getUserName != null) {
            user.setText(getUserName);
        }
        if (getPassword != null) {
            pass.setText(getPassword);
        }
        checkBox.setChecked(getChecked);

        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                login_failed.setVisibility(View.GONE);
            }
        });

        user.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    user.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                login_failed.setVisibility(View.GONE);
            }
        });

        pass.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    pass.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        login.setOnClickListener(v -> {

            closeKeyBoard();

            login_failed.setVisibility(View.INVISIBLE);
            userName = Objects.requireNonNull(user.getText()).toString();
            password = Objects.requireNonNull(pass.getText()).toString();

            if (!userName.isEmpty() && !password.isEmpty()) {
                dynamicLoginCheck();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please Give Email and Password", Toast.LENGTH_SHORT).show();
            }

        });

        readApiText();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Login.this);
                builder.setTitle("EXIT!")
                        .setIcon(R.drawable.tracker_logo)
                        .setMessage("Do you want to EXIT?")
                        .setPositiveButton("YES", (dialog, which) -> finish())
                        .setNegativeButton("NO", (dialog, which) -> {
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    public void readApiText() {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        new Thread(() -> {
            urls = new ArrayList<>();
            try {
                URL url = new URL(text_url);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(60000); // timing out in a minute
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    urls.add(new AllUrlList(str,false));
                }
                in.close();
            }
            catch (Exception e) {
                urls.add(new AllUrlList("http://103.56.208.123:8001/apex/ttrams/",false));
                urls.add(new AllUrlList("http://103.56.208.123:8001/apex/mnm/",false));
                urls.add(new AllUrlList("http://103.56.208.123:8001/apex/tracker/",false));
                Log.d("MyTag",e.toString());
            }

            runOnUiThread(() -> {
                if (urls.isEmpty()) {
                    urls.add(new AllUrlList("http://103.56.208.123:8001/apex/ttrams/",false));
                    urls.add(new AllUrlList("http://103.56.208.123:8001/apex/mnm/",false));
                    urls.add(new AllUrlList("http://103.56.208.123:8001/apex/tracker/",false));
                }
                else {
                    for (int i = 0; i < urls.size(); i++) {
                        System.out.println(urls.get(i).getUrls());
                    }
                }
                waitProgress.dismiss();
            });

        }).start();
    }

    private void closeKeyBoard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event){
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    public void dynamicLoginCheck() {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        userInfoLists = new ArrayList<>();
        userDesignations = new ArrayList<>();
        conn = false;
        connected = false;
        infoConnected = false;
        emp_code = "";
        emp_id = "";
        CompanyName = "";
        adminFlag = false;
        centerLists = new ArrayList<>();
        System.out.println("START");

        checkToGetLoginData();
    }

    public void checkToGetLoginData() {
        boolean allUpdated = false;
        for (int i = 0; i < urls.size(); i++) {
            allUpdated = urls.get(i).isChecked();
            if (!urls.get(i).isChecked()) {
                allUpdated = urls.get(i).isChecked();
                String url = urls.get(i).getUrls();
                System.out.println(i+" Started");
                getLoginData(url,i);
                break;
            }
        }
        if (allUpdated) {
            System.out.println("all clear");
            updateLayout();
        }
    }

    public void getLoginData(String url, int index) {
        emp_code = "";
        CompanyName = "";
        emp_id = "";
        String useridUrl = url + "login/getUserLoginData?p_mail="+userName+"&p_password="+password;
        String companyUrl = url + "utility/getCompanyName";

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        StringRequest getCompanyRequest = new StringRequest(Request.Method.GET, companyUrl, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject companyInfo = array.getJSONObject(i);
                        CompanyName = companyInfo.getString("cim_name").equals("null") ? "No Name Found" : companyInfo.getString("cim_name");
                    }
                }
                centerLists.add(new CenterList(url,CompanyName,emp_code,emp_id));
                urls.get(index).setChecked(true);
                checkToGetLoginData();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                urls.get(index).setChecked(true);
                checkToGetLoginData();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            urls.get(index).setChecked(true);
            checkToGetLoginData();
        });

        StringRequest getUserMessage = new StringRequest(Request.Method.GET, useridUrl, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userIdInfo = array.getJSONObject(i);
                        emp_code = userIdInfo.getString("emp_code")
                                .equals("null") ? "" : userIdInfo.getString("emp_code");
                        emp_id = userIdInfo.getString("emp_id")
                                .equals("null") ? "" : userIdInfo.getString("emp_id");
                    }
                }

                if (emp_code.isEmpty()) {
                    urls.get(index).setChecked(true);
                    checkToGetLoginData();
                }
                else {
                    requestQueue.add(getCompanyRequest);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                urls.get(index).setChecked(true);
                checkToGetLoginData();
            }

        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            urls.get(index).setChecked(true);
            checkToGetLoginData();
        });

        requestQueue.add(getUserMessage);
    }

    private void updateLayout() {
        if (!centerLists.isEmpty()) {
            if (centerLists.size() == 1) {
                api_url_front = centerLists.get(0).getCenter_api();
                emp_code = centerLists.get(0).getUser_emp_code();
                emp_id = centerLists.get(0).getUser_emp_id();
                CompanyName = centerLists.get(0).getCenter_name();
                getUserDetails();
            }
            else {
                waitProgress.dismiss();
                SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,Login.this);
                selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
            }
        }
        else {
            waitProgress.dismiss();
            for (int i = 0; i < urls.size(); i++) {
                urls.get(i).setChecked(false);
            }
            if (conn) {
                if (connected) {
                    login_failed.setVisibility(View.VISIBLE);
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Login.this);
                    alertDialogBuilder.setTitle("Warning!")
                            .setIcon(R.drawable.tracker_logo)
                            .setMessage("No User Found")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Login.this);
                    alertDialogBuilder.setTitle("Warning!")
                            .setIcon(R.drawable.tracker_logo)
                            .setMessage("There is a network issue in the server. Please Try later.")
                            .setPositiveButton("Retry", (dialog, which) -> {
                                dynamicLoginCheck();
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Login.this);
                alertDialogBuilder.setTitle("Warning!")
                        .setIcon(R.drawable.tracker_logo)
                        .setMessage("Server Problem or Internet Not Connected")
                        .setPositiveButton("Retry", (dialog, which) -> {
                            dynamicLoginCheck();
                            dialog.dismiss();
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        }
    }

    @Override
    public void onDismiss(int position) {
        api_url_front = centerLists.get(position).getCenter_api();
        emp_code = centerLists.get(position).getUser_emp_code();
        emp_id = centerLists.get(position).getUser_emp_id();
        CompanyName = centerLists.get(position).getCenter_name();
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        getUserDetails();
    }

    public void getUserDetails() {
        adminFlag = false;
        String adminCheckUrl = api_url_front + "login/getToCheckAdmin?p_emp_id=" + emp_id;
        String userInfoUrl = api_url_front + "login/getUserInfoData?p_emp_code="+emp_code;
        String designationUrl = api_url_front + "login/getUserDesignations/"+emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);

        StringRequest designationRequest = new StringRequest(Request.Method.GET, designationUrl, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject desigInfo = array.getJSONObject(i);
                        String jsm_code = desigInfo.getString("jsm_code")
                                .equals("null") ? "" : desigInfo.getString("jsm_code");
                        String temp_title = desigInfo.getString("temp_title")
                                .equals("null") ? "" : desigInfo.getString("temp_title");
                        String jsd_id = desigInfo.getString("jsd_id")
                                .equals("null") ? "" : desigInfo.getString("jsd_id");
                        String jsd_objective = desigInfo.getString("jsd_objective")
                                .equals("null") ? "" : desigInfo.getString("jsd_objective");
                        String dept_name = desigInfo.getString("dept_name")
                                .equals("null") ? "" : desigInfo.getString("dept_name");
                        String divm_name = desigInfo.getString("divm_name")
                                .equals("null") ? "" : desigInfo.getString("divm_name");
                        String desig_name = desigInfo.getString("desig_name")
                                .equals("null") ? "" : desigInfo.getString("desig_name");
                        String desig_priority = desigInfo.getString("desig_priority")
                                .equals("null") ? "" : desigInfo.getString("desig_priority");
                        String joiningdate = desigInfo.getString("joiningdate")
                                .equals("null") ? "" : desigInfo.getString("joiningdate");
                        String divm_id = desigInfo.getString("divm_id")
                                .equals("null") ? "" : desigInfo.getString("divm_id");

                        userDesignations.add(new UserDesignation(jsm_code,temp_title,jsd_id,jsd_objective,dept_name,divm_name,desig_name,desig_priority,joiningdate,divm_id));
                    }
                }
                infoConnected = true;
                connected = true;
                goToDashboard();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        });

        StringRequest userInfoRequest = new StringRequest(Request.Method.GET, userInfoUrl, response -> {
            conn = true;
            try {
                connected = true;
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userInfo = array.getJSONObject(i);
                        String usr_name = userInfo.getString("emp_name")
                                .equals("null") ? "" : userInfo.getString("emp_name");
                        String usr_n_name = userInfo.getString("emp_nick_name")
                                .equals("null") ? "" : userInfo.getString("emp_nick_name");
                        String usr_email = userInfo.getString("emp_email")
                                .equals("null") ? "" : userInfo.getString("emp_email");
                        String usr_contact = userInfo.getString("emp_contact")
                                .equals("null") ? "" : userInfo.getString("emp_contact");
                        String usr_emp_id = userInfo.getString("emp_id")
                                .equals("null") ? "" : userInfo.getString("emp_id");

                        userInfoLists.add(new UserInfoList(emp_code,usr_name,usr_n_name,usr_email,usr_contact,usr_emp_id));
                    }
                }
                requestQueue.add(designationRequest);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        });

        StringRequest adminCheckRequest = new StringRequest(Request.Method.GET, adminCheckUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userInfo = array.getJSONObject(i);
                        String a_flag = userInfo.getString("a_flag").equals("null") ? "0" : userInfo.getString("a_flag");
                        adminFlag = a_flag.equals("1");
                    }
                }
                if (adminFlag) {
                    requestQueue.add(userInfoRequest);
                }
                else {
                    connected = true;
                    goToDashboard();
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                goToDashboard();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            goToDashboard();
        });

        requestQueue.add(adminCheckRequest);
    }

    public void goToDashboard() {
        waitProgress.dismiss();
        for (int i = 0; i < urls.size(); i++) {
            urls.get(i).setChecked(false);
        }
        if (conn) {
            if (connected) {
                if (adminFlag) {
                    if (infoConnected) {
                        if (checkBox.isChecked()) {
                            System.out.println("Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(user_emp_code);
                            editor.remove(user_password);
                            editor.remove(checked);
                            editor.putString(user_emp_code,userName);
                            editor.putString(user_password,password);
                            editor.putBoolean(checked,true);
                            editor.apply();
                            editor.commit();
                        } else {
                            System.out.println("Not Remembered");
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.remove(user_emp_code);
                            editor.remove(user_password);
                            editor.remove(checked);
                            editor.apply();
                            editor.commit();
                        }

                        SharedPreferences.Editor editor1 = sharedPreferencesForLoginInfo.edit();

                        editor1.remove(USR_NAME);
                        editor1.remove(EMP_F_NAME);
                        editor1.remove(EMP_L_NAME);
                        editor1.remove(EMP_MAIL);
                        editor1.remove(EMP_CONTACT);
                        editor1.remove(EMP_ID);
                        editor1.remove(IS_LOGIN);
                        editor1.remove(DATABASE_NAME);
                        editor1.remove(CENTER_API);

                        editor1.remove(JSM_CODE);
                        editor1.remove(JSM_NAME);
                        editor1.remove(JSD_ID_LOGIN);
                        editor1.remove(JSD_OBJECTIVE);
                        editor1.remove(DEPT_NAME);
                        editor1.remove(DIV_NAME);
                        editor1.remove(DESG_NAME);
                        editor1.remove(DESG_PRIORITY);
                        editor1.remove(JOINING_DATE);
                        editor1.remove(DIV_ID);

                        editor1.putString(USR_NAME,userInfoLists.get(0).getEmp_code());
                        editor1.putString(EMP_F_NAME,userInfoLists.get(0).getUser_name());
                        editor1.putString(EMP_L_NAME,userInfoLists.get(0).getUser_nick_name());
                        editor1.putString(EMP_MAIL,userInfoLists.get(0).getEmail());
                        editor1.putString(EMP_CONTACT,userInfoLists.get(0).getContact());
                        editor1.putString(EMP_ID,userInfoLists.get(0).getEmp_id());
                        editor1.putBoolean(IS_LOGIN,true);
                        editor1.putString(DATABASE_NAME,CompanyName);
                        editor1.putString(CENTER_API,api_url_front);

                        if (!userDesignations.isEmpty()) {
                            editor1.putString(JSM_CODE, userDesignations.get(0).getJsm_code());
                            editor1.putString(JSM_NAME, userDesignations.get(0).getJsm_name());
                            editor1.putString(JSD_ID_LOGIN, userDesignations.get(0).getJsd_id());
                            editor1.putString(JSD_OBJECTIVE, userDesignations.get(0).getJsd_objective());
                            editor1.putString(DEPT_NAME, userDesignations.get(0).getDept_name());
                            editor1.putString(DIV_NAME, userDesignations.get(0).getDiv_name());
                            editor1.putString(DESG_NAME, userDesignations.get(0).getDesg_name());
                            editor1.putString(DESG_PRIORITY, userDesignations.get(0).getDesg_priority());
                            editor1.putString(JOINING_DATE, userDesignations.get(0).getJoining_date());
                            editor1.putString(DIV_ID, userDesignations.get(0).getDiv_id());
                        } else {
                            editor1.putString(JSM_CODE, null);
                            editor1.putString(JSM_NAME, null);
                            editor1.putString(JSD_ID_LOGIN, null);
                            editor1.putString(JSD_OBJECTIVE, null);
                            editor1.putString(DEPT_NAME, null);
                            editor1.putString(DIV_NAME, null);
                            editor1.putString(DESG_NAME, null);
                            editor1.putString(DESG_PRIORITY, null);
                            editor1.putString(JOINING_DATE, null);
                            editor1.putString(DIV_ID, null);
                        }
                        editor1.apply();
                        editor1.commit();

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        dynamicLoginCheck();
                    }
                }
                else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Login.this);
                    builder.setTitle("No Access!")
                            .setIcon(R.drawable.tracker_logo)
                            .setMessage("You have no access to use this app. Please contact with your HR administrator")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                conn = false;
                connected = false;
                infoConnected = false;
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(Login.this)
                        .setMessage("There is a network issue in the server. Please Try later.")
                        .setPositiveButton("Retry", null)
                        .show();

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(v -> {

                    dynamicLoginCheck();
                    dialog.dismiss();
                });
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog dialog = new AlertDialog.Builder(Login.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Retry", null)
                    .show();

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                dynamicLoginCheck();
                dialog.dismiss();
            });
        }
    }

//    public boolean isConnected () {
//        boolean connected = false;
//        boolean isMobile = false;
//        try {
//            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo nInfo = cm.getActiveNetworkInfo();
//            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
//            return connected;
//        } catch (Exception e) {
//            Log.e("Connectivity Exception", e.getMessage());
//        }
//        return connected;
//    }
//
//    public boolean isOnline () {
//
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//
//    public class CheckLogin extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(), "WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                LoginQuery();
//                if (connected) {
//                    conn = true;
//                    message = "Internet Connected";
//                }
//
//            } else {
//                conn = false;
//                message = "Not Connected";
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (conn) {
//
//                if (!userId.equals("-1")) {
//                    if (infoConnected) {
//
//                        if (checkBox.isChecked()) {
//                            System.out.println("Remembered");
//                            SharedPreferences.Editor editor = sharedpreferences.edit();
//                            editor.remove(user_emp_code);
//                            editor.remove(user_password);
//                            editor.remove(checked);
//                            editor.putString(user_emp_code,userName);
//                            editor.putString(user_password,password);
//                            editor.putBoolean(checked,true);
//                            editor.apply();
//                            editor.commit();
//
//
//
//                        } else {
//                            System.out.println("Not Remembered");
//                        }
//
////                        user.setText("");
////                        pass.setText("");
////                        checkBox.setChecked(false);
//
//                        SharedPreferences.Editor editor1 = sharedPreferencesForLoginInfo.edit();
//                        editor1.remove(EMP_NAME);
//                        editor1.remove(EMP_CODE);
//                        editor1.remove(EMP_ID);
//                        editor1.remove(IS_LOGIN);
//                        editor1.remove(DATABASE_NAME);
//
//                        String name = userInfoLists.get(0).getUser_fname() + " " + userInfoLists.get(0).getUser_lname();
//                        editor1.putString(EMP_NAME,name);
//                        editor1.putString(EMP_ID,userInfoLists.get(0).getEmp_id());
//                        editor1.putString(EMP_CODE,userInfoLists.get(0).getUserName());
//                        editor1.putBoolean(IS_LOGIN,true);
//                        editor1.putString(DATABASE_NAME,DEFAULT_USERNAME);
//                        editor1.apply();
//                        editor1.commit();
//
//                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
//                        startActivity(intent);
//                        finish();
//
//                    } else {
//                        new CheckLogin().execute();
//                    }
//
//                } else {
//
//                    login_failed.setVisibility(View.VISIBLE);
//                }
//                conn = false;
//                connected = false;
//                infoConnected = false;
//
//
//            } else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(Login.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .show();
//
////                dialog.setCancelable(false);
////                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new CheckLogin().execute();
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }
//
//    public class Check extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            waitProgress.show(getSupportFragmentManager(), "WaitBar");
//            waitProgress.setCancelable(false);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            if (isConnected() && isOnline()) {
//
//                GettingData();
//                if (getConnected) {
//                    getConn = true;
//                    message = "Internet Connected";
//                }
//
//
//            } else {
//                getConn = false;
//                message = "Not Connected";
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            waitProgress.dismiss();
//            if (getConn) {
//
//                if (CompanyName == null) {
//                    CompanyName = "No Name Found";
//                }
//
//                if (SoftwareName == null) {
//                    SoftwareName = "Name of App";
//                }
//                softName.setText(CompanyName);
//
//                getConnected = false;
//                getConn = false;
//
//
//            } else {
//                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//                AlertDialog dialog = new AlertDialog.Builder(Login.this)
//                        .setMessage("Please Check Your Internet Connection")
//                        .setPositiveButton("Retry", null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new Check().execute();
//                        dialog.dismiss();
//                    }
//                });
//            }
//        }
//    }
//
//    public void LoginQuery () {
//
//
//        try {
//            this.connection = createConnection();
//            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();
//
//            userInfoLists = new ArrayList<>();
//            userDesignations = new ArrayList<>();
//
//
//            Statement stmt = connection.createStatement();
//            StringBuffer stringBuffer = new StringBuffer();
//
//            ResultSet rs = stmt.executeQuery("select VALIDATE_USER_DB('" + userName + "',HAMID_ENCRYPT_DESCRIPTION_PACK.HEDP_ENCRYPT('" + password + "')) val from dual\n");
//
//
//            while (rs.next()) {
//                stringBuffer.append("USER ID: " + rs.getString(1) + "\n");
//                userId = rs.getString(1);
//
//            }
//
//            if (!userId.equals("-1")) {
//                String empCode = "";
//                ResultSet resEmpCode = stmt.executeQuery("select COM_PACK.GET_EMP_CODE_BY_EMP_ID(COM_PACK.GET_EMPLOYEE_ID_BY_USER('"+userName+"')) valu from dual");
//                while (resEmpCode.next()) {
//                    empCode = resEmpCode.getString(1);
//                }
//                resEmpCode.close();
//
//                ResultSet resultSet = stmt.executeQuery("Select USR_NAME, USR_FNAME, USR_LNAME, USR_EMAIL, USR_CONTACT, USR_EMP_ID FROM ISP_USER\n" +
//                        "where USR_ID = " + userId + "\n");
//
//                while (resultSet.next()) {
//                    emp_id = resultSet.getString(6);
//                    userInfoLists.add(new UserInfoList(empCode, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6)));
//                }
//
//                ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT JOB_SETUP_MST.JSM_CODE, JOB_SETUP_MST.JSM_NAME TEMP_TITLE, \n" +
//                        "JOB_SETUP_DTL.JSD_ID, JOB_SETUP_DTL.JSD_OBJECTIVE, DEPT_MST.DEPT_NAME, \n" +
//                        "DIVISION_MST.DIVM_NAME, DESIG_MST.DESIG_NAME, DESIG_MST.DESIG_PRIORITY, (Select TO_CHAR(MAX(EMP_JOB_HISTORY.JOB_ACTUAL_DATE),'DD-MON-YYYY') from EMP_JOB_HISTORY) as JOININGDATE, DIVISION_MST.DIVM_ID \n" +
//                        "FROM JOB_SETUP_MST, JOB_SETUP_DTL, DEPT_MST, DIVISION_MST, DESIG_MST, EMP_JOB_HISTORY\n" +
//                        "WHERE ((JOB_SETUP_DTL.JSD_JSM_ID = JOB_SETUP_MST.JSM_ID)\n" +
//                        " AND (JOB_SETUP_MST.JSM_DIVM_ID = DIVISION_MST.DIVM_ID)\n" +
//                        " AND (DEPT_MST.DEPT_ID = JOB_SETUP_MST.JSM_DEPT_ID)\n" +
//                        " AND (JOB_SETUP_MST.JSM_DESIG_ID = DESIG_MST.DESIG_ID))\n" +
//                        " AND JOB_SETUP_DTL.JSD_ID = (SELECT JOB_JSD_ID\n" +
//                        "                            FROM EMP_JOB_HISTORY\n" +
//                        "                            WHERE JOB_ID = (SELECT MAX(JOB_ID) FROM EMP_JOB_HISTORY WHERE JOB_EMP_ID = " + emp_id + "))" +
//                        "AND EMP_JOB_HISTORY.JOB_JSD_ID = JOB_SETUP_DTL.JSD_ID");
//
//                while (resultSet1.next()) {
//
//                    userDesignations.add(new UserDesignation(resultSet1.getString(1), resultSet1.getString(2), resultSet1.getString(3), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getString(7), resultSet1.getString(8), resultSet1.getString(9), resultSet1.getString(10)));
//                }
//
//
//                infoConnected = true;
//
//            }
//            System.out.println(stringBuffer);
//
//
//            connected = true;
//
//            connection.close();
//
//        } catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//
//    }
//
//    public void GettingData () {
//        try {
//            this.connection = createConnection();
//
//            Statement stmt = connection.createStatement();
//
//            ResultSet rs = stmt.executeQuery("SELECT CIM_NAME --,CIM_LOGO_APPS \n" +
//                    "FROM COMPANY_INFO_MST\n");
//
//            while (rs.next()) {
//                CompanyName = rs.getString(1);
//                System.out.println(CompanyName);
//
//            }
//
//
//            ResultSet resultSet = stmt.executeQuery(" select LIC_SOFTWARE_NAME from isp_runauto where rownum=1\n");
//
//            while (resultSet.next()) {
//                SoftwareName = resultSet.getString(1);
//                System.out.println(SoftwareName);
//            }
//
//
//            getConnected = true;
//            connection.close();
//
//        } catch (Exception e) {
//
//            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
//            Log.i("ERRRRR", e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }

}