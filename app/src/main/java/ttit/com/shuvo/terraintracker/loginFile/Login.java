package ttit.com.shuvo.terraintracker.loginFile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.rosemaryapp.amazingspinner.AmazingSpinner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.MainPage.HomePage;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.progressBar.WaitProgress;

import static ttit.com.shuvo.terraintracker.OracleConnection.DEFAULT_USERNAME;
import static ttit.com.shuvo.terraintracker.OracleConnection.createConnection;

public class Login extends AppCompatActivity {

    TextInputEditText user;
    TextInputEditText pass;

    TextView login_failed;
    TextView softName;

    Button login;

    CheckBox checkBox;

    String userName = "";
    String password = "";
    public static String CompanyName = "";
    public static String SoftwareName = "";

    WaitProgress waitProgress = new WaitProgress();
    private String message = null;
    private Boolean conn = false;
    private Boolean infoConnected = false;
    private Boolean connected = false;
    AmazingSpinner database;

    private Boolean getConn = false;
    private Boolean getConnected = false;

    private Connection connection;

    public static final String MyPREFERENCES = "UserPass" ;
    public static final String user_emp_code = "nameKey";
    public static final String user_password = "passKey";
    public static final String checked = "trueFalse";

    public static final String LOGIN_FILE_NAME = "LOGIN_INFO";
    public static final String EMP_NAME = "EMP_NAME";
    public static final String EMP_CODE = "EMP_CODE";
    public static final String EMP_ID = "EMP_ID";
    public static final String IS_LOGIN = "TRUE_FALSE";
    public static final String DATABASE_NAME = "DATABASE_NAME";

    SharedPreferences sharedpreferences;
    SharedPreferences sharedPreferencesForLoginInfo;

    String getUserName = "";
    String getPassword = "";
    boolean getChecked = false;

    String userId = "";
    public static ArrayList<UserInfoList> userInfoLists;
    public static ArrayList<UserDesignation> userDesignations;

    String emp_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfoLists = new ArrayList<>();
        userDesignations = new ArrayList<>();

        softName = findViewById(R.id.name_of_soft_login);
        user = findViewById(R.id.user_name_given);
        pass = findViewById(R.id.password_given);
        checkBox = findViewById(R.id.remember_checkbox);
        database = findViewById(R.id.database_spinner);

        login_failed = findViewById(R.id.email_pass_miss);

        login = findViewById(R.id.login_button);

        sharedPreferencesForLoginInfo = getSharedPreferences(LOGIN_FILE_NAME,MODE_PRIVATE);

        sharedpreferences = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        getUserName = sharedpreferences.getString(user_emp_code,null);
        getPassword = sharedpreferences.getString(user_password,null);
        getChecked = sharedpreferences.getBoolean(checked,false);

        database.setText(DEFAULT_USERNAME);
        ArrayList<String> type = new ArrayList<>();
        type.add("IKGL");
        type.add("TTRAMS");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);

        database.setAdapter(arrayAdapter);

        database.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DEFAULT_USERNAME = parent.getItemAtPosition(position).toString();
                new Check().execute();
            }
        });

        if (getUserName != null) {
            user.setText(getUserName);
        }
        if (getPassword != null) {
            pass.setText(getPassword);
        }
        checkBox.setChecked(getChecked);

        new Check().execute();

        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyBoard();

                login_failed.setVisibility(View.INVISIBLE);
                userName = user.getText().toString();
                password = pass.getText().toString();

                if (!userName.isEmpty() && !password.isEmpty()) {


                    new CheckLogin().execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Please Give User Name and Password", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onBackPressed () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Do You Want to Exit?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        System.exit(0);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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

    public boolean isConnected () {
        boolean connected = false;
        boolean isMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public boolean isOnline () {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public class CheckLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(), "WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                LoginQuery();
                if (connected) {
                    conn = true;
                    message = "Internet Connected";
                }

            } else {
                conn = false;
                message = "Not Connected";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (conn) {

                if (!userId.equals("-1")) {
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
                        }

//                        user.setText("");
//                        pass.setText("");
//                        checkBox.setChecked(false);

                        SharedPreferences.Editor editor1 = sharedPreferencesForLoginInfo.edit();
                        editor1.remove(EMP_NAME);
                        editor1.remove(EMP_CODE);
                        editor1.remove(EMP_ID);
                        editor1.remove(IS_LOGIN);
                        editor1.remove(DATABASE_NAME);

                        String name = userInfoLists.get(0).getUser_fname() + " " + userInfoLists.get(0).getUser_lname();
                        editor1.putString(EMP_NAME,name);
                        editor1.putString(EMP_ID,userInfoLists.get(0).getEmp_id());
                        editor1.putString(EMP_CODE,userInfoLists.get(0).getUserName());
                        editor1.putBoolean(IS_LOGIN,true);
                        editor1.putString(DATABASE_NAME,DEFAULT_USERNAME);
                        editor1.apply();
                        editor1.commit();

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        startActivity(intent);
                        finish();

                    } else {
                        new CheckLogin().execute();
                    }

                } else {

                    login_failed.setVisibility(View.VISIBLE);
                }
                conn = false;
                connected = false;
                infoConnected = false;


            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(Login.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .show();

//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new CheckLogin().execute();
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    public class Check extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(), "WaitBar");
            waitProgress.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                GettingData();
                if (getConnected) {
                    getConn = true;
                    message = "Internet Connected";
                }


            } else {
                getConn = false;
                message = "Not Connected";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            if (getConn) {

                if (CompanyName == null) {
                    CompanyName = "No Name Found";
                }

                if (SoftwareName == null) {
                    SoftwareName = "Name of App";
                }
                softName.setText(CompanyName);

                getConnected = false;
                getConn = false;


            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(Login.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Check().execute();
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    public void LoginQuery () {


        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            userInfoLists = new ArrayList<>();
            userDesignations = new ArrayList<>();


            Statement stmt = connection.createStatement();
            StringBuffer stringBuffer = new StringBuffer();

            ResultSet rs = stmt.executeQuery("select VALIDATE_USER_DB('" + userName + "',HAMID_ENCRYPT_DESCRIPTION_PACK.HEDP_ENCRYPT('" + password + "')) val from dual\n");


            while (rs.next()) {
                stringBuffer.append("USER ID: " + rs.getString(1) + "\n");
                userId = rs.getString(1);

            }

            if (!userId.equals("-1")) {
                String empCode = "";
                ResultSet resEmpCode = stmt.executeQuery("select COM_PACK.GET_EMP_CODE_BY_EMP_ID(COM_PACK.GET_EMPLOYEE_ID_BY_USER('"+userName+"')) valu from dual");
                while (resEmpCode.next()) {
                    empCode = resEmpCode.getString(1);
                }
                resEmpCode.close();

                ResultSet resultSet = stmt.executeQuery("Select USR_NAME, USR_FNAME, USR_LNAME, USR_EMAIL, USR_CONTACT, USR_EMP_ID FROM ISP_USER\n" +
                        "where USR_ID = " + userId + "\n");

                while (resultSet.next()) {
                    emp_id = resultSet.getString(6);
                    userInfoLists.add(new UserInfoList(empCode, resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6)));
                }

                ResultSet resultSet1 = stmt.executeQuery("SELECT DISTINCT JOB_SETUP_MST.JSM_CODE, JOB_SETUP_MST.JSM_NAME TEMP_TITLE, \n" +
                        "JOB_SETUP_DTL.JSD_ID, JOB_SETUP_DTL.JSD_OBJECTIVE, DEPT_MST.DEPT_NAME, \n" +
                        "DIVISION_MST.DIVM_NAME, DESIG_MST.DESIG_NAME, DESIG_MST.DESIG_PRIORITY, (Select TO_CHAR(MAX(EMP_JOB_HISTORY.JOB_ACTUAL_DATE),'DD-MON-YYYY') from EMP_JOB_HISTORY) as JOININGDATE, DIVISION_MST.DIVM_ID \n" +
                        "FROM JOB_SETUP_MST, JOB_SETUP_DTL, DEPT_MST, DIVISION_MST, DESIG_MST, EMP_JOB_HISTORY\n" +
                        "WHERE ((JOB_SETUP_DTL.JSD_JSM_ID = JOB_SETUP_MST.JSM_ID)\n" +
                        " AND (JOB_SETUP_MST.JSM_DIVM_ID = DIVISION_MST.DIVM_ID)\n" +
                        " AND (DEPT_MST.DEPT_ID = JOB_SETUP_MST.JSM_DEPT_ID)\n" +
                        " AND (JOB_SETUP_MST.JSM_DESIG_ID = DESIG_MST.DESIG_ID))\n" +
                        " AND JOB_SETUP_DTL.JSD_ID = (SELECT JOB_JSD_ID\n" +
                        "                            FROM EMP_JOB_HISTORY\n" +
                        "                            WHERE JOB_ID = (SELECT MAX(JOB_ID) FROM EMP_JOB_HISTORY WHERE JOB_EMP_ID = " + emp_id + "))" +
                        "AND EMP_JOB_HISTORY.JOB_JSD_ID = JOB_SETUP_DTL.JSD_ID");

                while (resultSet1.next()) {

                    userDesignations.add(new UserDesignation(resultSet1.getString(1), resultSet1.getString(2), resultSet1.getString(3), resultSet1.getString(4), resultSet1.getString(5), resultSet1.getString(6), resultSet1.getString(7), resultSet1.getString(8), resultSet1.getString(9), resultSet1.getString(10)));
                }


                infoConnected = true;

            }
            System.out.println(stringBuffer);


            connected = true;

            connection.close();

        } catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    public void GettingData () {
        try {
            this.connection = createConnection();

            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT CIM_NAME --,CIM_LOGO_APPS \n" +
                    "FROM COMPANY_INFO_MST\n");

            while (rs.next()) {
                CompanyName = rs.getString(1);
                System.out.println(CompanyName);

            }


            ResultSet resultSet = stmt.executeQuery(" select LIC_SOFTWARE_NAME from isp_runauto where rownum=1\n");

            while (resultSet.next()) {
                SoftwareName = resultSet.getString(1);
                System.out.println(SoftwareName);
            }


            getConnected = true;
            connection.close();

        } catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}