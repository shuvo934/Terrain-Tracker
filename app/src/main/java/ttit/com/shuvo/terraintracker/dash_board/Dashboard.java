package ttit.com.shuvo.terraintracker.dash_board;

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
import static ttit.com.shuvo.terraintracker.Constants.USR_NAME;
import static ttit.com.shuvo.terraintracker.Constants.api_url_front;
import static ttit.com.shuvo.terraintracker.loginFile.Login.userDesignations;
import static ttit.com.shuvo.terraintracker.loginFile.Login.userInfoLists;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hdodenhof.circleimageview.CircleImageView;
import ttit.com.shuvo.terraintracker.MainPage.HomePage;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.AttendanceUpdateReqApproval;
import ttit.com.shuvo.terraintracker.attendance.DailyAttendance;
import ttit.com.shuvo.terraintracker.attendance.EmpWiseAttendance;
import ttit.com.shuvo.terraintracker.dash_board.adapters.LeaveHRAdapter;
import ttit.com.shuvo.terraintracker.dash_board.model.DateWiseLeaveList;
import ttit.com.shuvo.terraintracker.dash_board.model.EmpWiseLeaveList;
import ttit.com.shuvo.terraintracker.leave.LeaveApproval;
import ttit.com.shuvo.terraintracker.leave.LeaveBalance;
import ttit.com.shuvo.terraintracker.leave.LeaveRecord;
import ttit.com.shuvo.terraintracker.loginFile.Login;
import ttit.com.shuvo.terraintracker.loginFile.UserDesignation;
import ttit.com.shuvo.terraintracker.loginFile.UserInfoList;
import ttit.com.shuvo.terraintracker.progressBar.WaitProgress;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ImageView logOut;

    TextView welcomeText;
    CircleImageView userImage;
    TextView userName;
    TextView designation;
    TextView department;
    TextView comp;

    Bitmap selectedImage;
    boolean imageFound = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView hr_menu;

    TextView totalDepts;
    TextView totalEmp;

    String tot_depts = "";
    String tot_emp = "";

    LinearLayout attTabFullLayout;
    TabLayout attTab;
    boolean attTabUpdateNeeded = false;
    String attTabFirstDate = "";
    String attTabEndDate = "";

    TextView dateRangAtt;

    PieChart attChart;
    ArrayList<PieEntry> attPieEntry;
    String present_tot = "";
    String absent_tot = "";
    String leave_tot = "";
    String holiday_tot = "";
    String late_tot = "";
    String early_tot = "";
    String both_late_early_tot = "";

    CircularProgressIndicator attCircularProgress;
    ImageView attRefresh;

    LinearLayout leaveTabFullLayout;
    TabLayout leaveTab;
    boolean leaveTabUpdateNeeded = false;
    String leaveTabFirstDate = "";
    String leaveTabEndDate = "";

    TextView dateRangLeave;

    TextView leaveWeekFirstDate;
    TextView leaveWeekSecondDate;
    TextView leaveWeekThirdDate;
    TextView leaveWeekFourthDate;
    TextView leaveWeekFifthDate;
    TextView leaveWeekSixthDate;
    TextView leaveWeekSeventhDate;

    RecyclerView leaveView;
    RecyclerView.LayoutManager layoutManager;
    LeaveHRAdapter leaveHRAdapter;

    ArrayList<String> dateList;
    ArrayList<EmpWiseLeaveList> empWiseLeaveLists;
    ArrayList<DateWiseLeaveList> dateWiseLeaveLists;

    TextView noLeaveMsg;
    CardView showLeaveDetails;

    CircularProgressIndicator leaveCircularProgress;
    ImageView leaveRefresh;

    LinearLayout payRollFullLayout;
    TextView payRollMonth;
    TextView payRollWd;
    TextView workingHour;
    TextView salaryGen;
    TextView allowanceTotal;
    TextView salDeduction;
    TextView salRefund;
    TextView netPayable;

    String working_hour = "";
    String salary_gen = "";
    String sal_deduct = "";
    String sal_refund = "";
    String allowance_total = "";
    String net_pay = "";
    String work_days = "";

    String payRollFirstDate = "";
    String payRollEndDate = "";

    CircularProgressIndicator payRollCircularProgress;
    ImageView payRollRefresh;

    SharedPreferences sharedpreferences;

    String emp_code = "";
    String emp_id = "";

    WaitProgress waitProgress = new WaitProgress();
    private Boolean conn = false;
    private Boolean connected = false;
    boolean onResumeLoad = true;
    String parsing_message = "";
    boolean loading = false;

    String model = "";
    String brand = "";
    String ipAddress = "";
    String hostUserName = "";
    String osName = "";

    boolean adminFlag = false;
    public static ArrayList<String> testDataBlob = new ArrayList<>();
    Logger logger = Logger.getLogger(Dashboard.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logOut = findViewById(R.id.log_out_icon);
        welcomeText = findViewById(R.id.welcome_text_view);
        userName = findViewById(R.id.user_name_dashboard);
        userImage = findViewById(R.id.user_pic_dashboard);
        department = findViewById(R.id.user_depart_dashboard);
        designation = findViewById(R.id.user_desg_dashboard);
        comp = findViewById(R.id.company_name_dashboard);

        drawerLayout = findViewById(R.id.hr_dashboard_drawer);
        navigationView = findViewById(R.id.hr_menu_navigation);
        hr_menu = findViewById(R.id.hr_menu_icon);

        totalDepts = findViewById(R.id.total_depts_count_in_hr);
        totalEmp = findViewById(R.id.total_employee_count_in_hr);

        attTabFullLayout = findViewById(R.id.att_tab_full_layout);
        attTabFullLayout.setVisibility(View.VISIBLE);
        attTab = findViewById(R.id.hr_tab_layout);
        dateRangAtt = findViewById(R.id.date_range_tab_text_att_hrm_dashboard);
        attChart = findViewById(R.id.att_perc_hr_overview_pie_chart);

        attCircularProgress = findViewById(R.id.progress_indicator_hr_dashboard_att_chart_tab_layout);
        attCircularProgress.setVisibility(View.GONE);
        attRefresh = findViewById(R.id.hr_dashboard_att_chart_tab_refresh_button);
        attRefresh.setVisibility(View.GONE);

        leaveTabFullLayout = findViewById(R.id.leave_tab_full_layout);
        leaveTabFullLayout.setVisibility(View.VISIBLE);
        leaveTab = findViewById(R.id.hr_leave_tab_layout);
        dateRangLeave = findViewById(R.id.date_range_tab_text_leave_hrm_dashboard);

        leaveWeekFirstDate = findViewById(R.id.leave_date_week_calendar_first_day);
        leaveWeekSecondDate = findViewById(R.id.leave_date_week_calendar_second_day);
        leaveWeekThirdDate = findViewById(R.id.leave_date_week_calendar_third_day);
        leaveWeekFourthDate = findViewById(R.id.leave_date_week_calendar_fourth_day);
        leaveWeekFifthDate = findViewById(R.id.leave_date_week_calendar_fifth_day);
        leaveWeekSixthDate = findViewById(R.id.leave_date_week_calendar_sixth_day);
        leaveWeekSeventhDate = findViewById(R.id.leave_date_week_calendar_seventh_day);

        leaveView = findViewById(R.id.leave_employees_list_view);
        leaveView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        leaveView.setLayoutManager(layoutManager);

        noLeaveMsg = findViewById(R.id.no_leave_found_msg_leave_hrm_dashboard);
        noLeaveMsg.setVisibility(View.GONE);
        showLeaveDetails = findViewById(R.id.show_more_leave_details_button);
        showLeaveDetails.setVisibility(View.VISIBLE);

        leaveCircularProgress = findViewById(R.id.progress_indicator_hr_dashboard_leave_list_tab_layout);
        leaveCircularProgress.setVisibility(View.GONE);
        leaveRefresh = findViewById(R.id.hr_dashboard_leave_list_tab_refresh_button);
        leaveRefresh.setVisibility(View.GONE);

        payRollFullLayout = findViewById(R.id.pay_roll_dashboard_full_layout);
        payRollMonth = findViewById(R.id.select_month_for_pay_roll_dashboard);
        payRollWd = findViewById(R.id.total_working_days_for_payroll_dashboard);
        workingHour = findViewById(R.id.working_hours_total_payroll_dashboard);
        salaryGen = findViewById(R.id.salary_gen_total_payroll_dashboard);
        salRefund = findViewById(R.id.refundable_total_payroll_dashboard);
        allowanceTotal = findViewById(R.id.allowance_total_payroll_dashboard);
        salDeduction = findViewById(R.id.deduction_total_payroll_dashboard);
        netPayable = findViewById(R.id.net_payable_total_payroll_dashboard);

        payRollCircularProgress = findViewById(R.id.progress_indicator_hr_dashboard_payroll_layout);
        payRollCircularProgress.setVisibility(View.GONE);
        payRollRefresh = findViewById(R.id.hr_dashboard_payroll_refresh_button);
        payRollRefresh.setVisibility(View.GONE);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        hr_menu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            drawerLayout.openDrawer(GravityCompat.END);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout , R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);

        ImageView imageView = headerView.findViewById(R.id.close_icon_hr_menu);

        imageView.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        sharedpreferences = getSharedPreferences(LOGIN_FILE_NAME, MODE_PRIVATE);
        boolean loginfile = sharedpreferences.getBoolean(IS_LOGIN, false);

        if (loginfile) {
            String userName = sharedpreferences.getString(USR_NAME, null);
            String userFname = sharedpreferences.getString(EMP_F_NAME, null);
            String userLname = sharedpreferences.getString(EMP_L_NAME, null);
            String email = sharedpreferences.getString(EMP_MAIL, null);
            String contact = sharedpreferences.getString(EMP_CONTACT, null);
            String emp_id_login = sharedpreferences.getString(EMP_ID, null);

            userInfoLists = new ArrayList<>();
            userInfoLists.add(new UserInfoList(userName, userFname, userLname, email, contact, emp_id_login));

            CompanyName = sharedpreferences.getString(DATABASE_NAME, null);
            api_url_front = sharedpreferences.getString(CENTER_API,null);

            String jsm_code = sharedpreferences.getString(JSM_CODE, null);
            String jsm_name = sharedpreferences.getString(JSM_NAME, null);
            String jsd_id = sharedpreferences.getString(JSD_ID_LOGIN, null);
            String jsd_obj = sharedpreferences.getString(JSD_OBJECTIVE, null);
            String dept_name = sharedpreferences.getString(DEPT_NAME, null);
            String div_name = sharedpreferences.getString(DIV_NAME, null);
            String desg_name = sharedpreferences.getString(DESG_NAME, null);
            String desg_priority = sharedpreferences.getString(DESG_PRIORITY, null);
            String joining = sharedpreferences.getString(JOINING_DATE, null);
            String div_id = sharedpreferences.getString(DIV_ID, null);

            userDesignations = new ArrayList<>();
            userDesignations.add(new UserDesignation(jsm_code, jsm_name, jsd_id, jsd_obj, dept_name, div_name, desg_name, desg_priority, joining, div_id));
        }
        else {
            onResumeLoad = false;
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Dashboard.this);
            builder.setTitle("FORCED LOG OUT!")
                    .setIcon(R.drawable.tracker_logo)
                    .setMessage("Some data may have been changed and could not be found due to server maintenance. We are sorry for the disturbance. Please Login again to continue the app.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        userInfoLists.clear();
                        userDesignations.clear();
                        userInfoLists = new ArrayList<>();
                        userDesignations = new ArrayList<>();

                        SharedPreferences.Editor editor1 = sharedpreferences.edit();
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

                        editor1.apply();
                        editor1.commit();

                        Intent intent = new Intent(Dashboard.this, Login.class);
                        startActivity(intent);
                        finish();
                    });
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }

        attChartInit();

        attTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!attTabUpdateNeeded) {
                    attTabUpdateNeeded = true;
                }
                else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
                    SimpleDateFormat moY = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
                    SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);
                    SimpleDateFormat only_month_full_date_format = new SimpleDateFormat("MMMM, yy", Locale.ENGLISH);
                    attTabFirstDate = dateFormat.format(Calendar.getInstance().getTime());
                    attTabEndDate = dateFormat.format(Calendar.getInstance().getTime());
                    switch (tab.getPosition()) {
                        case 0:
                            // --- Current Day ---
                            attTabFirstDate = dateFormat.format(Calendar.getInstance().getTime());
                            attTabEndDate = dateFormat.format(Calendar.getInstance().getTime());
                            String date_range_text = full_date_format.format(Calendar.getInstance().getTime());
                            dateRangAtt.setText(date_range_text);
                            getAttChart();
                            break;
                        case 1:
                            // --- Last Day ---
                            Calendar ccc = Calendar.getInstance();
                            ccc.add(Calendar.DAY_OF_MONTH, -1);

                            attTabFirstDate = dateFormat.format(ccc.getTime());
                            String date_range = full_date_format.format(ccc.getTime());
                            attTabEndDate = dateFormat.format(ccc.getTime());
                            dateRangAtt.setText(date_range);
                            getAttChart();
                            break;
                        case 2:
                            // --- Last Week ---
                            Calendar week_ccc = Calendar.getInstance();
                            week_ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                            week_ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

                            week_ccc.add(Calendar.DATE,-7);

                            attTabFirstDate = dateFormat.format(week_ccc.getTime());
                            String first_date_range = full_date_format.format(week_ccc.getTime());
                            week_ccc.add(Calendar.DATE, 6);
                            attTabEndDate = dateFormat.format(week_ccc.getTime());
                            String last_date_range = full_date_format.format(week_ccc.getTime());

                            String text = first_date_range + "  --  " + last_date_range;
                            dateRangAtt.setText(text);
                            getAttChart();
                            break;
                        case 3:
                            // --- Last Month ---
                            Calendar monthcc = Calendar.getInstance();
                            monthcc.set(Calendar.DAY_OF_MONTH, 1);
                            monthcc.add(Calendar.DATE, -1);

                            Date lastDayOfMonth = monthcc.getTime();

                            String llll = sdff.format(lastDayOfMonth);
                            String llmmmyy = moY.format(lastDayOfMonth);
                            attTabEndDate = llll + "-" + llmmmyy;
                            attTabFirstDate = "01-"+ llmmmyy;
                            String month_only_full = only_month_full_date_format.format(lastDayOfMonth);

                            String text1 = "01 " + month_only_full + "  --  " + llll + " " + month_only_full;

                            dateRangAtt.setText(text1);
                            getAttChart();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!attTabUpdateNeeded) {
                    attTabUpdateNeeded = true;
                }
            }
        });

        attRefresh.setOnClickListener(v -> getAttChart());

        attChart.setOnClickListener(view -> {
            onResumeLoad = true;
            Intent intent = new Intent(Dashboard.this, DailyAttendance.class);
            startActivity(intent);
        });

        leaveTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!leaveTabUpdateNeeded) {
                    leaveTabUpdateNeeded = true;
                }
                else{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);
                    SimpleDateFormat date_format_range = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
                    leaveTabFirstDate = dateFormat.format(Calendar.getInstance().getTime());
                    leaveTabEndDate = dateFormat.format(Calendar.getInstance().getTime());
                    switch (tab.getPosition()) {
                        case 0:
                            // --- Last Week ---
                            dateList = new ArrayList<>();
                            ArrayList<String> dateToViewList = new ArrayList<>();

                            Calendar l_week_ccc = Calendar.getInstance();
                            l_week_ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                            l_week_ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

                            l_week_ccc.add(Calendar.DATE,-7);

                            leaveTabFirstDate = dateFormat.format(l_week_ccc.getTime());
                            String l_first_date_range = full_date_format.format(l_week_ccc.getTime());

                            String date_leave = date_format_range.format(l_week_ccc.getTime());
                            String date_list_val = dateFormat.format(l_week_ccc.getTime());

                            dateList.add(date_list_val.toUpperCase(Locale.ENGLISH));
                            dateToViewList.add(date_leave);

                            for (int i = 1; i < 7; i++) {
                                l_week_ccc.add(Calendar.DATE, 1);
                                date_leave = date_format_range.format(l_week_ccc.getTime());
                                date_list_val = dateFormat.format(l_week_ccc.getTime());

                                dateList.add(date_list_val.toUpperCase(Locale.ENGLISH));
                                dateToViewList.add(date_leave);
                            }

                            leaveTabEndDate = dateFormat.format(l_week_ccc.getTime());
                            String l_last_date_range = full_date_format.format(l_week_ccc.getTime());

                            String l_text = l_first_date_range + "  --  " + l_last_date_range;
                            dateRangLeave.setText(l_text);

                            leaveWeekFirstDate.setText(dateToViewList.get(0));
                            leaveWeekSecondDate.setText(dateToViewList.get(1));
                            leaveWeekThirdDate.setText(dateToViewList.get(2));
                            leaveWeekFourthDate.setText(dateToViewList.get(3));
                            leaveWeekFifthDate.setText(dateToViewList.get(4));
                            leaveWeekSixthDate.setText(dateToViewList.get(5));
                            leaveWeekSeventhDate.setText(dateToViewList.get(6));


                            getLeaveData();
                            break;
                        case 1:
                            // --- This Week ---
                            dateList = new ArrayList<>();
                            ArrayList<String> dateToViewList_1 = new ArrayList<>();

                            Calendar week_ccc = Calendar.getInstance();
                            week_ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                            week_ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

                            leaveTabFirstDate = dateFormat.format(week_ccc.getTime());
                            String first_date_range = full_date_format.format(week_ccc.getTime());

                            String date_leave_t = date_format_range.format(week_ccc.getTime());
                            String date_list_val_t = dateFormat.format(week_ccc.getTime());

                            dateList.add(date_list_val_t.toUpperCase(Locale.ENGLISH));
                            dateToViewList_1.add(date_leave_t);

                            for (int i = 1; i < 7; i++) {
                                week_ccc.add(Calendar.DATE, 1);
                                date_leave_t = date_format_range.format(week_ccc.getTime());
                                date_list_val_t = dateFormat.format(week_ccc.getTime());

                                dateList.add(date_list_val_t.toUpperCase(Locale.ENGLISH));
                                dateToViewList_1.add(date_leave_t);
                            }

                            leaveTabEndDate = dateFormat.format(week_ccc.getTime());
                            String last_date_range = full_date_format.format(week_ccc.getTime());

                            String text = first_date_range + "  --  " + last_date_range;
                            dateRangLeave.setText(text);

                            leaveWeekFirstDate.setText(dateToViewList_1.get(0));
                            leaveWeekSecondDate.setText(dateToViewList_1.get(1));
                            leaveWeekThirdDate.setText(dateToViewList_1.get(2));
                            leaveWeekFourthDate.setText(dateToViewList_1.get(3));
                            leaveWeekFifthDate.setText(dateToViewList_1.get(4));
                            leaveWeekSixthDate.setText(dateToViewList_1.get(5));
                            leaveWeekSeventhDate.setText(dateToViewList_1.get(6));

                            getLeaveData();
                            break;
                        case 2:
                            // --- Next Week ---
                            dateList = new ArrayList<>();
                            ArrayList<String> dateToViewList_2 = new ArrayList<>();

                            Calendar n_week_ccc = Calendar.getInstance();
                            n_week_ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                            n_week_ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

                            n_week_ccc.add(Calendar.DATE,7);

                            leaveTabFirstDate = dateFormat.format(n_week_ccc.getTime());
                            String n_first_date_range = full_date_format.format(n_week_ccc.getTime());

                            String date_leave_n = date_format_range.format(n_week_ccc.getTime());
                            String date_list_val_n = dateFormat.format(n_week_ccc.getTime());

                            dateList.add(date_list_val_n.toUpperCase(Locale.ENGLISH));
                            dateToViewList_2.add(date_leave_n);

                            for (int i = 1; i < 7; i++) {
                                n_week_ccc.add(Calendar.DATE, 1);
                                date_leave_n = date_format_range.format(n_week_ccc.getTime());
                                date_list_val_n = dateFormat.format(n_week_ccc.getTime());

                                dateList.add(date_list_val_n.toUpperCase(Locale.ENGLISH));
                                dateToViewList_2.add(date_leave_n);
                            }

                            leaveTabEndDate = dateFormat.format(n_week_ccc.getTime());
                            String n_last_date_range = full_date_format.format(n_week_ccc.getTime());

                            String n_text = n_first_date_range + "  --  " + n_last_date_range;
                            dateRangLeave.setText(n_text);

                            leaveWeekFirstDate.setText(dateToViewList_2.get(0));
                            leaveWeekSecondDate.setText(dateToViewList_2.get(1));
                            leaveWeekThirdDate.setText(dateToViewList_2.get(2));
                            leaveWeekFourthDate.setText(dateToViewList_2.get(3));
                            leaveWeekFifthDate.setText(dateToViewList_2.get(4));
                            leaveWeekSixthDate.setText(dateToViewList_2.get(5));
                            leaveWeekSeventhDate.setText(dateToViewList_2.get(6));

                            getLeaveData();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!leaveTabUpdateNeeded) {
                    leaveTabUpdateNeeded = true;
                }
            }
        });

        leaveRefresh.setOnClickListener(v -> getLeaveData());

        showLeaveDetails.setOnClickListener(view -> {
            onResumeLoad = true;
            Intent intent = new Intent(Dashboard.this, LeaveRecord.class);
            startActivity(intent);
        });

        payRollMonth.setOnClickListener(v -> {

            Date c1 = Calendar.getInstance().getTime();

            String formattedYear;
            String monthValue;
            String lastformattedYear;
            String lastdateView;

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("MM",Locale.ENGLISH);

            formattedYear = df1.format(c1);
            monthValue = sdf.format(c1);
            int nowMonNumb = Integer.parseInt(monthValue);
            nowMonNumb = nowMonNumb - 2;
            int lastMonNumb = nowMonNumb - 5;

            if (lastMonNumb < 0) {
                lastMonNumb = lastMonNumb + 12;
                int formatY = Integer.parseInt(formattedYear);
                formatY = formatY - 1;
                lastformattedYear = String.valueOf(formatY);
            } else {
                lastformattedYear = formattedYear;
            }

            Date today1 = new Date();

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(today1);

            //calendar2.add(Calendar.MONTH, 1);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            calendar2.add(Calendar.DATE, -1);

            Date lastDayOfMonth = calendar2.getTime();

            SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
            lastdateView = sdff.format(lastDayOfMonth);

            int yearSelected;
            int monthSelected;
            MonthFormat monthFormat = MonthFormat.LONG;
            String customTitle = "Select Month";

            Calendar calendar = Calendar.getInstance();
            if (!payRollFirstDate.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                Date date = null;
                try {
                    date = dateFormat.parse(payRollFirstDate);
                }
                catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                if (date != null) {
                    calendar.setTime(date);
                }
            }
            yearSelected = calendar.get(Calendar.YEAR);
            monthSelected = calendar.get(Calendar.MONTH);
            calendar.clear();
            calendar.set(Integer.parseInt(lastformattedYear), lastMonNumb, 1); // Set minimum date to show in dialog
            long minDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

            calendar.clear();
            calendar.set(Integer.parseInt(formattedYear), nowMonNumb, Integer.parseInt(lastdateView)); // Set maximum date to show in dialog
            long maxDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

            MonthYearPickerDialogFragment dialogFragment =  MonthYearPickerDialogFragment
                    .getInstance(monthSelected, yearSelected, minDate, maxDate, customTitle, monthFormat);

            dialogFragment.show(getSupportFragmentManager(), null);

            dialogFragment.setOnDateSetListener((year, monthOfYear) -> {
                System.out.println(year);
                System.out.println(monthOfYear);

                int month = monthOfYear + 1;
                String mon = "";
                String yearName;

                if (month == 1) {
                    mon = "JAN";
                } else if (month == 2) {
                    mon = "FEB";
                } else if (month == 3) {
                    mon = "MAR";
                } else if (month == 4) {
                    mon = "APR";
                } else if (month == 5) {
                    mon = "MAY";
                } else if (month == 6) {
                    mon = "JUN";
                } else if (month == 7) {
                    mon = "JUL";
                } else if (month == 8) {
                    mon = "AUG";
                } else if (month == 9) {
                    mon = "SEP";
                } else if (month == 10) {
                    mon = "OCT";
                } else if (month == 11) {
                    mon = "NOV";
                } else if (month == 12) {
                    mon = "DEC";
                }

                yearName = String.valueOf(year);
                yearName = yearName.substring(yearName.length()-2);


                payRollFirstDate = "01-"+mon+"-"+yearName;
                String s = "MONTH: " + mon+"-"+yearName;
                payRollMonth.setText(s);

                SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                Date today11 = null;
                try {
                    today11 = sss.parse(payRollFirstDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                Calendar calendar11 = Calendar.getInstance();
                if (today11 != null) {
                    calendar11.setTime(today11);
                    calendar11.add(Calendar.MONTH, 1);
                    calendar11.set(Calendar.DAY_OF_MONTH, 1);
                    calendar11.add(Calendar.DATE, -1);

                    Date lastDayOfMonth1 = calendar11.getTime();

                    SimpleDateFormat sdff1 = new SimpleDateFormat("dd",Locale.ENGLISH);
                    String s1 = sdff1.format(lastDayOfMonth1);
                    payRollEndDate =  s1+ "-" + mon +"-"+ yearName;
                }
                getPayRollData();
            });

        });

        payRollRefresh.setOnClickListener(view -> getPayRollData());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
                else {
                    if (loading) {
                        Toast.makeText(Dashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this)
                                .setTitle("EXIT!")
                                .setMessage("Do You Want to Exit?")
                                .setIcon(R.drawable.tracker_logo)
                                .setPositiveButton("Yes", (dialog, which) -> finish())
                                .setNegativeButton("No", (dialog, which) -> {
                                    //Do nothing
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        });

        logOut.setOnClickListener(v -> {
            if (loading) {
                Toast.makeText(Dashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Dashboard.this);
                builder.setTitle("LOG OUT!")
                        .setIcon(R.drawable.tracker_logo)
                        .setMessage("Do you want to Log Out?")
                        .setPositiveButton("YES", (dialog, which) -> {
                            userInfoLists.clear();
                            userDesignations.clear();
                            userInfoLists = new ArrayList<>();
                            userDesignations = new ArrayList<>();

                            SharedPreferences.Editor editor1 = sharedpreferences.edit();
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

                            editor1.apply();
                            editor1.commit();

                            Intent intent = new Intent(Dashboard.this, Login.class);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> {

                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loading) {
            if (onResumeLoad) {
                onResumeLoad = false;
                getAllData();
            }
        }
    }

    private void attChartInit() {
        attChart.setCenterText("");
        attChart.setDrawEntryLabels(true);
        attChart.setCenterTextSize(14);
        attChart.setHoleRadius(40);
        attChart.setTransparentCircleRadius(40);

        attChart.setEntryLabelTextSize(11);
        attChart.setEntryLabelColor(Color.DKGRAY);
        attChart.getDescription().setEnabled(false);

        attChart.setTouchEnabled(true);
        attChart.setClickable(true);
        attChart.setHighlightPerTapEnabled(true);
        attChart.setOnTouchListener(null);

        Legend l = attChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setForm(Legend.LegendForm.CIRCLE);

        l.setXOffset(4f);
        l.setTextSize(12);
        l.setWordWrapEnabled(false);
        l.setDrawInside(false);
        l.setYOffset(5f);

        attChart.animateXY(1000, 1000);
    }

    public void initData() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String wt;
        if (currentHour >= 4 && currentHour <= 11) {
            wt = "GOOD MORNING,";
        } else if (currentHour >= 12 && currentHour <= 16) {
            wt = "GOOD AFTERNOON,";
        } else if (currentHour >= 17 && currentHour <= 22) {
            wt = "GOOD EVENING,";
        } else {
            wt = "HELLO,";
        }
        welcomeText.setText(wt);

        if (!userInfoLists.isEmpty()) {
            emp_code = userInfoLists.get(0).getEmp_code();
            emp_id = userInfoLists.get(0).getEmp_id();
            String firstname = userInfoLists.get(0).getUser_name();
            if (firstname == null) {
                firstname = "";
            }
            userName.setText(firstname);
        }

        if (!userDesignations.isEmpty()) {
            String jsmName = userDesignations.get(0).getJsm_name();
            if (jsmName == null) {
                jsmName = "";
            }
            designation.setText(jsmName);

            String deptName = userDesignations.get(0).getDiv_name();
            if (deptName == null) {
                deptName = "";
            }
            department.setText(deptName);
        }

        comp.setText(CompanyName);
        model = Build.MODEL;

        brand = Build.BRAND;

        ipAddress = getIPAddress(true);

        hostUserName = getHostName("localhost");

        StringBuilder builder = new StringBuilder();
        builder.append("ANDROID: ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(": ").append(fieldName);
            }
        }

        System.out.println("OS: " + builder);

        osName = builder.toString();
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.WARNING,ex.getLocalizedMessage(),ex);
        } // for now eat exceptions
        return "";
    }

    public static String getHostName(String defValue) {
        try {
            @SuppressLint("DiscouragedPrivateApi") Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (loading) {
            Toast.makeText(getApplicationContext(), "Please wait while loading", Toast.LENGTH_SHORT).show();
        }
        else {
            if (item.getItemId() == R.id.daily_attendance_hr_menu) {
                Intent intent = new Intent(Dashboard.this, DailyAttendance.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.emp_wise_attendance_hr_menu) {
                Intent intent = new Intent(Dashboard.this, EmpWiseAttendance.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.leave_record_hr_menu) {
                Intent intent = new Intent(Dashboard.this, LeaveRecord.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.leave_approve_hr_menu) {
                Intent intent = new Intent(Dashboard.this, LeaveApproval.class);
                intent.putExtra("FLAG", 0);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.leave_balance_hr_menu) {
                Intent intent = new Intent(Dashboard.this, LeaveBalance.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.att_update_approve_hr_menu) {
                Intent intent = new Intent(Dashboard.this, AttendanceUpdateReqApproval.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.emp_track_rec_hr_menu) {
                Intent intent = new Intent(Dashboard.this, HomePage.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else {
                Toast.makeText(getApplicationContext(), "No Menu Selected", Toast.LENGTH_SHORT).show();
            }
            onResumeLoad = true;
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    public void getAllData() {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        connected = false;
        conn = false;
        loading = true;
        adminFlag = false;

        attCircularProgress.setVisibility(View.GONE);
        leaveCircularProgress.setVisibility(View.GONE);
        payRollCircularProgress.setVisibility(View.GONE);
        attRefresh.setVisibility(View.GONE);
        leaveRefresh.setVisibility(View.GONE);
        payRollRefresh.setVisibility(View.GONE);

        tot_depts = "0";
        tot_emp = "0";
        attPieEntry = new ArrayList<>();
        present_tot = "0";
        absent_tot = "0";
        leave_tot = "0";
        holiday_tot = "0";
        late_tot = "0";
        early_tot = "0";
        both_late_early_tot = "0";

        dateList = new ArrayList<>();
        empWiseLeaveLists = new ArrayList<>();
        dateWiseLeaveLists = new ArrayList<>();

        working_hour = "";
        salary_gen = "";
        sal_deduct = "";
        sal_refund = "";
        allowance_total = "";
        net_pay = "";
        work_days = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        String date_now = dateFormat.format(Calendar.getInstance().getTime());

        Calendar ccc = Calendar.getInstance();
        ccc.setFirstDayOfWeek(Calendar.SATURDAY);
        ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

        String leave_first_date = dateFormat.format(ccc.getTime());

        ccc.add(Calendar.DATE, 6);

        String leave_end_date = dateFormat.format(ccc.getTime());

        Calendar n_monthcc = Calendar.getInstance();
        n_monthcc.set(Calendar.DAY_OF_MONTH, 1);
        n_monthcc.add(Calendar.DATE, -1);

        Date lastDayOf_l_Month = n_monthcc.getTime();

        SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
        SimpleDateFormat moY = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
        String llll_n = sdff.format(lastDayOf_l_Month);
        String n_llmmmyy = moY.format(lastDayOf_l_Month);
        String month_last_date = llll_n + "-" + n_llmmmyy;
        String month_first_date = "01-"+ n_llmmmyy;
        payRollFirstDate = month_first_date;
        payRollEndDate = month_last_date;

        String adminCheckUrl = api_url_front + "login/getToCheckAdmin?p_emp_id=" + emp_id;
        String userImageUrl = api_url_front + "utility/getUserImage/" + emp_code;
        String loginLogUrl = api_url_front + "dashboard/loginLog";
        String countUrl = api_url_front+"hrm_dashboard/getDeptEmpCount";
        String attUrl = api_url_front+"hrm_dashboard/getAttendancePerc?first_date="+date_now+"&last_date="+date_now;
        String leaveDataUrl = api_url_front+"hrm_dashboard/getEmpWithLeaveDate?first_date="+leave_first_date+"&last_date="+leave_end_date;
        String leaveEmpDataUrl = api_url_front+"hrm_dashboard/getEmpInLeave?first_date="+leave_first_date+"&last_date="+leave_end_date;
        String payDataUrl = api_url_front+"hrm_dashboard/getPayrollData?first_date="+month_first_date+"&last_date="+month_last_date;
        String workDaysUrl = api_url_front+"hrm_dashboard/getAttWorkingDays?first_date="+month_first_date+"&last_date="+month_last_date;

        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard.this);

        StringRequest workingDaysReq = new StringRequest(Request.Method.GET, workDaysUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject workDaysInfo = array.getJSONObject(i);

                        work_days = workDaysInfo.getString("work_days")
                                .equals("null") ? "0" : workDaysInfo.getString("work_days");
                    }
                }
                connected = true;
                updateInterface();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest payDataReq = new StringRequest(Request.Method.GET, payDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        working_hour = docInfo.getString("total")
                                .equals("null") ? "0" : docInfo.getString("total");
                        salary_gen = docInfo.getString("salary")
                                .equals("null") ? "0" : docInfo.getString("salary");
                        sal_refund = docInfo.getString("refundable")
                                .equals("null") ? "0" : docInfo.getString("refundable");
                        allowance_total = docInfo.getString("other_allowance")
                                .equals("null") ? "0" : docInfo.getString("other_allowance");
                        sal_deduct = docInfo.getString("deduction")
                                .equals("null") ? "0" : docInfo.getString("deduction");
                        net_pay = docInfo.getString("net_payable")
                                .equals("null") ? "0" : docInfo.getString("net_payable");

                    }
                }

                requestQueue.add(workingDaysReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest leaveEmpReq = new StringRequest(Request.Method.GET, leaveEmpDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String emp_name = docInfo.getString("emp_name")
                                .equals("null") ? "0" : docInfo.getString("emp_name");

                        empWiseLeaveLists.add(new EmpWiseLeaveList(emp_name,"",
                                "","","","",
                                "",""));
                        if (i==4) {
                            break;
                        }
                    }
                }

                requestQueue.add(payDataReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest leaveDataReq = new StringRequest(Request.Method.GET, leaveDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String d_date = docInfo.getString("d_date")
                                .equals("null") ? "0" : docInfo.getString("d_date");
                        String date_day = docInfo.getString("date_day")
                                .equals("null") ? "0" : docInfo.getString("date_day");
                        String emp_id = docInfo.getString("emp_id")
                                .equals("null") ? "0" : docInfo.getString("emp_id");
                        String emp_name = docInfo.getString("emp_name")
                                .equals("null") ? "0" : docInfo.getString("emp_name");
                        String leave = docInfo.getString("leave")
                                .equals("null") ? "0" : docInfo.getString("leave");
                        String h_flag = docInfo.getString("h_flag")
                                .equals("null") ? "0" : docInfo.getString("h_flag");

                        dateWiseLeaveLists.add(new DateWiseLeaveList(d_date,date_day,emp_id,emp_name,leave,h_flag));

                    }
                }

                requestQueue.add(leaveEmpReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest attReq = new StringRequest(Request.Method.GET, attUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        absent_tot = docInfo.getString("absent")
                                .equals("null") ? "0" : docInfo.getString("absent");
                        present_tot = docInfo.getString("present")
                                .equals("null") ? "0" : docInfo.getString("present");
                        leave_tot = docInfo.getString("leave")
                                .equals("null") ? "0" : docInfo.getString("leave");
                        holiday_tot = docInfo.getString("holiday_weekend")
                                .equals("null") ? "0" : docInfo.getString("holiday_weekend");
                        late_tot = docInfo.getString("late_count")
                                .equals("null") ? "0" : docInfo.getString("late_count");
                        early_tot = docInfo.getString("early_count")
                                .equals("null") ? "0" : docInfo.getString("early_count");
                        both_late_early_tot = docInfo.getString("both_late_early_count")
                                .equals("null") ? "0" : docInfo.getString("both_late_early_count");

                    }
                }

                requestQueue.add(leaveDataReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest countReq = new StringRequest(Request.Method.GET, countUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        tot_depts = docInfo.getString("total_depts")
                                .equals("null") ? "0" : docInfo.getString("total_depts");
                        tot_emp = docInfo.getString("total_emp")
                                .equals("null") ? "0" : docInfo.getString("total_emp");

                    }
                }

                requestQueue.add(attReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest loginLogReq = new StringRequest(Request.Method.POST, loginLogUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    requestQueue.add(countReq);
                }
                else {
                    connected = false;
                    updateInterface();
                }
            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING, e.getMessage(), e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING, error.getMessage(), error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_IULL_USER_ID", emp_code);
                headers.put("P_IULL_CLIENT_HOST_NAME", brand + " " + model);
                headers.put("P_IULL_CLIENT_IP_ADDR", ipAddress);
                headers.put("P_IULL_CLIENT_HOST_USER_NAME", hostUserName);
                headers.put("P_IULL_SESSION_USER_ID", emp_id);
                headers.put("P_IULL_OS_NAME", osName);
                headers.put("P_LOG_TYPE", "4");
                return headers;
            }
        };

        StringRequest imageReq = new StringRequest(Request.Method.GET, userImageUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject userImageInfo = array.getJSONObject(i);
                        String emp_image = userImageInfo.getString("emp_image");
                        if (emp_image.equals("null") || emp_image.isEmpty()) {
                            System.out.println("NULL IMAGE");
                            imageFound = false;
                        } else {
                            byte[] decodedString = Base64.decode(emp_image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            if (bitmap != null) {
                                imageFound = true;
                                selectedImage = bitmap;
                            } else {
                                imageFound = false;
                            }
                        }
                    }
                }
                requestQueue.add(loginLogReq);
            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING, e.getMessage(), e);
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING, error.getMessage(), error);
            updateInterface();
        });

        StringRequest empFlagCheckReq = new StringRequest(Request.Method.GET, adminCheckUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String a_flag = info.getString("a_flag").equals("null") ? "0" : info.getString("a_flag");
                        adminFlag = a_flag.equals("1");
                    }
                }

                if (adminFlag) {
                    requestQueue.add(imageReq);
                } else {
                    connected = true;
                    updateInterface();
                }
            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING, e.getMessage(), e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING, error.getMessage(), error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(empFlagCheckReq);

    }

    public void updateInterface() {
        waitProgress.dismiss();
        loading = false;
        if (conn) {
            if (connected) {
                if (adminFlag) {
                    conn = false;
                    connected = false;

                    //--- Pie chart initialization ---

                    totalEmp.setText(tot_emp);
                    totalDepts.setText(tot_depts);

                    SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);
                    Date dateRangCc = Calendar.getInstance().getTime();
                    String date_range_text = full_date_format_range.format(dateRangCc);
                    dateRangAtt.setText(date_range_text);

                    TabLayout.Tab tabAt = attTab.getTabAt(0);
                    attTabUpdateNeeded = false;
                    attTab.selectTab(tabAt);

                    final int[] piecolors = new int[]{
                            Color.rgb(85, 239, 196),
                            Color.rgb(116, 185, 255),
                            Color.rgb(162, 155, 254),
                            Color.rgb(223, 230, 233),
                            Color.rgb(255, 234, 167),

                            Color.rgb(250, 177, 160),
                            Color.rgb(178, 190, 195),
                            Color.rgb(129, 236, 236),
                            Color.rgb(255, 118, 117),
                            Color.rgb(253, 121, 168),
                            Color.rgb(96, 163, 188)};

                    if (present_tot != null) {
                        if (!present_tot.isEmpty()) {
                            if (!present_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(present_tot), "On-Time Present", 0));
                            }
                        }
                    }

                    if (absent_tot != null) {
                        if (!absent_tot.isEmpty()) {
                            if (!absent_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(absent_tot), "Absent", 1));
                            }
                        }
                    }

                    if (leave_tot != null) {
                        if (!leave_tot.isEmpty()) {
                            if (!leave_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(leave_tot), "On Leave", 2));
                            }
                        }
                    }

                    if (holiday_tot != null) {
                        if (!holiday_tot.isEmpty()) {
                            if (!holiday_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(holiday_tot), "Holiday/Weekend", 3));
                            }
                        }
                    }

                    if (late_tot != null) {
                        if (!late_tot.isEmpty()) {
                            if (!late_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(late_tot), "Late Arrival", 4));
                            }
                        }
                    }

                    if (early_tot != null) {
                        if (!early_tot.isEmpty()) {
                            if (!early_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(early_tot), "Early Departure", 5));
                            }
                        }
                    }

                    if (both_late_early_tot != null) {
                        if (!both_late_early_tot.isEmpty()) {
                            if (!both_late_early_tot.equals("0")) {
                                attPieEntry.add(new PieEntry(Float.parseFloat(both_late_early_tot), "Both Late & Early", 6));
                            }
                        }
                    }

                    if (attPieEntry.isEmpty()) {
                        attPieEntry.add(new PieEntry(1,"No Data", 9));
                    }

                    PieDataSet dataSet = new PieDataSet(attPieEntry, "");
                    attChart.animateXY(1000, 1000);
                    attChart.setEntryLabelColor(Color.TRANSPARENT);

                    attChart.setUsePercentValues(true);

                    PieData data = new PieData(dataSet);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return Math.round(value) + " %";
                        }
                    });

                    String label = dataSet.getValues().get(0).getLabel();
                    System.out.println(label);
                    if (label.equals("No Data")) {
                        dataSet.setValueTextColor(Color.TRANSPARENT);
                    } else {
                        dataSet.setValueTextColor(Color.BLACK);
                        int tt = 0;
                        for (int i = 0; i < attPieEntry.size(); i++) {
                            tt =  tt +  (int) attPieEntry.get(i).getValue();
                        }
                        if (tt != 0) {
                            for (int i = 0; i < attPieEntry.size(); i++) {
                                String lv = dataSet.getValues().get(i).getLabel();
                                float val = dataSet.getValues().get(i).getValue();
                                float pp = (val / tt) * 100;
                                DecimalFormat df = new DecimalFormat("#.#");
                                String p_val = df.format(pp);
                                dataSet.getValues().get(i).setLabel(lv + " (" + p_val + " %)" );
                            }
                        }
                    }
                    dataSet.setHighlightEnabled(true);
                    dataSet.setValueTextSize(12);

                    int[] num = new int[attPieEntry.size()];
                    for (int i = 0; i < attPieEntry.size(); i++) {
                        int neki = (int) attPieEntry.get(i).getData();
                        num[i] = piecolors[neki];
                    }

                    dataSet.setColors(ColorTemplate.createColors(num));

                    attChart.setData(data);
                    attChart.invalidate();

                    // --- Leave Initialization ---

                    ArrayList<String> dateToViewList = new ArrayList<>();
                    Calendar ccc = Calendar.getInstance();
                    ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                    ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

                    SimpleDateFormat date_format_range = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
                    SimpleDateFormat date_format = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                    String first_date_range = full_date_format_range.format(ccc.getTime());

                    String date_leave = date_format_range.format(ccc.getTime());
                    String date_list_val = date_format.format(ccc.getTime());

                    dateList.add(date_list_val.toUpperCase(Locale.ENGLISH));
                    dateToViewList.add(date_leave);

                    for (int i = 1; i < 7; i++) {
                        ccc.add(Calendar.DATE, 1);
                        date_leave = date_format_range.format(ccc.getTime());
                        date_list_val = date_format.format(ccc.getTime());

                        dateList.add(date_list_val.toUpperCase(Locale.ENGLISH));
                        dateToViewList.add(date_leave);
                    }

                    String last_date_range = full_date_format_range.format(ccc.getTime());

                    String text = first_date_range + "  --  " + last_date_range;
                    dateRangLeave.setText(text);

                    leaveWeekFirstDate.setText(dateToViewList.get(0));
                    leaveWeekSecondDate.setText(dateToViewList.get(1));
                    leaveWeekThirdDate.setText(dateToViewList.get(2));
                    leaveWeekFourthDate.setText(dateToViewList.get(3));
                    leaveWeekFifthDate.setText(dateToViewList.get(4));
                    leaveWeekSixthDate.setText(dateToViewList.get(5));
                    leaveWeekSeventhDate.setText(dateToViewList.get(6));


                    for (int e = 0; e < empWiseLeaveLists.size(); e++) {
                        String emp_name = empWiseLeaveLists.get(e).getEmp_name();
                        for (int j = 0; j < dateWiseLeaveLists.size(); j++) {
                            if (emp_name.equals(dateWiseLeaveLists.get(j).getEmp_name())) {
                                for (int i = 0; i < dateList.size(); i++) {
                                    String dd = dateList.get(i);
                                    if (dd.equals(dateWiseLeaveLists.get(j).getD_date())) {
                                        if (i == 0) {
                                            empWiseLeaveLists.get(e).setFirst_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            break;
                                        }
                                        else if (i == 1) {
                                            empWiseLeaveLists.get(e).setSecond_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            break;
                                        }
                                        else if (i == 2) {
                                            empWiseLeaveLists.get(e).setThird_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            break;
                                        }
                                        else if (i == 3) {
                                            empWiseLeaveLists.get(e).setFourth_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            break;
                                        }
                                        else if (i == 4) {
                                            empWiseLeaveLists.get(e).setFifth_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            break;
                                        }
                                        else if (i == 5) {
                                            empWiseLeaveLists.get(e).setSixth_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            break;
                                        }
                                        else if (i == 6) {
                                            if (dateWiseLeaveLists.get(j).getH_flag().equals("1")) {
                                                empWiseLeaveLists.get(e).setSeventh_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (dateWiseLeaveLists.isEmpty()) {
                        noLeaveMsg.setVisibility(View.VISIBLE);
                    }
                    else {
                        noLeaveMsg.setVisibility(View.GONE);
                    }

                    leaveHRAdapter = new LeaveHRAdapter(Dashboard.this, empWiseLeaveLists);
                    leaveView.setAdapter(leaveHRAdapter);

                    TabLayout.Tab tabLv = leaveTab.getTabAt(1);
                    leaveTabUpdateNeeded = false;
                    leaveTab.selectTab(tabLv);

                    // --- payroll initialization---

                    DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                    double s_g = Double.parseDouble(salary_gen);
                    double rfn = Double.parseDouble(sal_refund);
                    double allow = Double.parseDouble(allowance_total);
                    double dc = Double.parseDouble(sal_deduct);
                    double n_s = Double.parseDouble(net_pay);

                    String w_h = working_hour + " h";
                    workingHour.setText(w_h);

                    String sg = "৳ " + formatter.format(s_g);
                    salaryGen.setText(sg);

                    String ref = "৳ " + formatter.format(rfn);
                    salRefund.setText(ref);

                    String at = "৳ " + formatter.format(allow);
                    allowanceTotal.setText(at);

                    String ded = "৳ " + formatter.format(dc);
                    salDeduction.setText(ded);

                    String ns = "৳ " + formatter.format(n_s);
                    netPayable.setText(ns);

                    Calendar n_monthcc = Calendar.getInstance();
                    n_monthcc.set(Calendar.DAY_OF_MONTH, 1);
                    n_monthcc.add(Calendar.DATE, -1);

                    Date date = n_monthcc.getTime();

                    SimpleDateFormat moY = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
                    String month = moY.format(date).toUpperCase(Locale.ENGLISH);

                    String ms = "MONTH: " + month;
                    payRollMonth.setText(ms);

                    String wd = "WD: " + work_days;
                    payRollWd.setText(wd);

                    if (imageFound) {
                        Glide.with(Dashboard.this)
                                .load(selectedImage)
                                .fitCenter()
                                .into(userImage);
                    }
                    else {
                        userImage.setImageResource(R.drawable.profile);
                    }

                }
                else {
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this)
                            .setTitle("FORCED LOGOUT!")
                            .setMessage("Some Important Data have been changed according to your profile. That's why you have been forced logout from application. To continue please login again.")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                userInfoLists.clear();
                                userDesignations.clear();
                                userInfoLists = new ArrayList<>();
                                userDesignations = new ArrayList<>();

                                SharedPreferences.Editor editor1 = sharedpreferences.edit();
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

                                editor1.apply();
                                editor1.commit();

                                Intent intent = new Intent(Dashboard.this, Login.class);
                                startActivity(intent);
                                finish();
                            });
                    AlertDialog alert = dialogBuilder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.setCancelable(false);
                    alert.show();
                }
            }
            else {
                if (parsing_message != null) {
                    if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                        parsing_message = "Server problem or Internet not connected";
                    }
                }
                else {
                    parsing_message = "Server problem or Internet not connected";
                }
                AlertDialog dialog = new AlertDialog.Builder(Dashboard.this)
                        .setTitle("Error!")
                        .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(v -> {
                    getAllData();
                    dialog.dismiss();
                });

                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(v -> {
                    userInfoLists.clear();
                    userDesignations.clear();
                    userInfoLists = new ArrayList<>();
                    userDesignations = new ArrayList<>();
                    dialog.dismiss();
                    finish();
                });
            }
        }
        else {
            if (parsing_message != null) {
                if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                    parsing_message = "Server problem or Internet not connected";
                }
            }
            else {
                parsing_message = "Server problem or Internet not connected";
            }
            AlertDialog dialog = new AlertDialog.Builder(Dashboard.this)
                    .setTitle("Error!")
                    .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                    .setPositiveButton("Retry", null)
                    .setNegativeButton("Cancel", null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                getAllData();
                dialog.dismiss();
            });

            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> {
                userInfoLists.clear();
                userDesignations.clear();
                userInfoLists = new ArrayList<>();
                userDesignations = new ArrayList<>();
                dialog.dismiss();
                finish();
            });
        }
    }

    public void getAttChart() {
        attTabFullLayout.setVisibility(View.GONE);
        attCircularProgress.setVisibility(View.VISIBLE);
        attRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        attPieEntry = new ArrayList<>();
        present_tot = "0";
        absent_tot = "0";
        leave_tot = "0";
        holiday_tot = "0";
        late_tot = "0";
        early_tot = "0";
        both_late_early_tot = "0";

        String attUrl = api_url_front+"hrm_dashboard/getAttendancePerc?first_date="+attTabFirstDate+"&last_date="+attTabEndDate;

        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard.this);

        StringRequest attReq = new StringRequest(Request.Method.GET, attUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        absent_tot = docInfo.getString("absent")
                                .equals("null") ? "0" : docInfo.getString("absent");
                        present_tot = docInfo.getString("present")
                                .equals("null") ? "0" : docInfo.getString("present");
                        leave_tot = docInfo.getString("leave")
                                .equals("null") ? "0" : docInfo.getString("leave");
                        holiday_tot = docInfo.getString("holiday_weekend")
                                .equals("null") ? "0" : docInfo.getString("holiday_weekend");
                        late_tot = docInfo.getString("late_count")
                                .equals("null") ? "0" : docInfo.getString("late_count");
                        early_tot = docInfo.getString("early_count")
                                .equals("null") ? "0" : docInfo.getString("early_count");
                        both_late_early_tot = docInfo.getString("both_late_early_count")
                                .equals("null") ? "0" : docInfo.getString("both_late_early_count");

                    }
                }

                connected = true;
                updateAttLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAttLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAttLayout();
        });

        requestQueue.add(attReq);
    }

    private void updateAttLayout() {
        if (conn) {
            if (connected) {
                attTabFullLayout.setVisibility(View.VISIBLE);
                attCircularProgress.setVisibility(View.GONE);
                attRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                final int[] piecolors = new int[]{
                        Color.rgb(85, 239, 196),
                        Color.rgb(116, 185, 255),
                        Color.rgb(162, 155, 254),
                        Color.rgb(223, 230, 233),
                        Color.rgb(255, 234, 167),

                        Color.rgb(250, 177, 160),
                        Color.rgb(178, 190, 195),
                        Color.rgb(129, 236, 236),
                        Color.rgb(255, 118, 117),
                        Color.rgb(253, 121, 168),
                        Color.rgb(96, 163, 188)};

                if (present_tot != null) {
                    if (!present_tot.isEmpty()) {
                        if (!present_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(present_tot), "On-Time Present", 0));
                        }
                    }
                }

                if (absent_tot != null) {
                    if (!absent_tot.isEmpty()) {
                        if (!absent_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(absent_tot), "Absent", 1));
                        }
                    }
                }

                if (leave_tot != null) {
                    if (!leave_tot.isEmpty()) {
                        if (!leave_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(leave_tot), "On Leave", 2));
                        }
                    }
                }

                if (holiday_tot != null) {
                    if (!holiday_tot.isEmpty()) {
                        if (!holiday_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(holiday_tot), "Holiday/Weekend", 3));
                        }
                    }
                }

                if (late_tot != null) {
                    if (!late_tot.isEmpty()) {
                        if (!late_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(late_tot), "Late Arrival", 4));
                        }
                    }
                }

                if (early_tot != null) {
                    if (!early_tot.isEmpty()) {
                        if (!early_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(early_tot), "Early Departure", 5));
                        }
                    }
                }

                if (both_late_early_tot != null) {
                    if (!both_late_early_tot.isEmpty()) {
                        if (!both_late_early_tot.equals("0")) {
                            attPieEntry.add(new PieEntry(Float.parseFloat(both_late_early_tot), "Both Late & Early", 6));
                        }
                    }
                }

                if (attPieEntry.isEmpty()) {
                    attPieEntry.add(new PieEntry(1,"No Data", 9));
                }

                PieDataSet dataSet = new PieDataSet(attPieEntry, "");
                attChart.animateXY(1000, 1000);
                attChart.setEntryLabelColor(Color.TRANSPARENT);

                attChart.setUsePercentValues(true);

                PieData data = new PieData(dataSet);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return Math.round(value) + " %";
                    }
                });


                String label = dataSet.getValues().get(0).getLabel();
                System.out.println(label);
                if (label.equals("No Data")) {
                    dataSet.setValueTextColor(Color.TRANSPARENT);
                } else {
                    dataSet.setValueTextColor(Color.BLACK);
                    int tt = 0;
                    for (int i = 0; i < attPieEntry.size(); i++) {
                        tt =  tt +  (int) attPieEntry.get(i).getValue();
                    }
                    if (tt != 0) {
                        for (int i = 0; i < attPieEntry.size(); i++) {
                            String lv = dataSet.getValues().get(i).getLabel();
                            float val = dataSet.getValues().get(i).getValue();
                            float pp = (val / tt) * 100;
                            DecimalFormat df = new DecimalFormat("#.#");
                            String p_val = df.format(pp);
                            dataSet.getValues().get(i).setLabel(lv + " (" + p_val + " %)" );
                        }
                    }
                }
                dataSet.setHighlightEnabled(true);
                dataSet.setValueTextSize(10);

                int[] num = new int[attPieEntry.size()];
                for (int i = 0; i < attPieEntry.size(); i++) {
                    int neki = (int) attPieEntry.get(i).getData();
                    num[i] = piecolors[neki];
                }

                dataSet.setColors(ColorTemplate.createColors(num));

                attChart.setData(data);
                attChart.invalidate();
                loading = false;

            }
            else {
                alertMessage3();
            }
        }
        else {
            alertMessage3();
        }
    }

    public void alertMessage3() {
        attTabFullLayout.setVisibility(View.GONE);
        attCircularProgress.setVisibility(View.GONE);
        attRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getLeaveData() {
        leaveTabFullLayout.setVisibility(View.GONE);
        leaveCircularProgress.setVisibility(View.VISIBLE);
        leaveRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        empWiseLeaveLists = new ArrayList<>();
        dateWiseLeaveLists = new ArrayList<>();

        String leaveDataUrl = api_url_front+"hrm_dashboard/getEmpWithLeaveDate?first_date="+leaveTabFirstDate+"&last_date="+leaveTabEndDate;
        String leaveEmpDataUrl = api_url_front+"hrm_dashboard/getEmpInLeave?first_date="+leaveTabFirstDate+"&last_date="+leaveTabEndDate;

        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard.this);

        StringRequest leaveEmpReq = new StringRequest(Request.Method.GET, leaveEmpDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String emp_name = docInfo.getString("emp_name")
                                .equals("null") ? "0" : docInfo.getString("emp_name");

                        empWiseLeaveLists.add(new EmpWiseLeaveList(emp_name,"",
                                "","","","",
                                "",""));

                        if (i==4) {
                            break;
                        }
                    }
                }

                connected = true;
                updateLeaveLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLeaveLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLeaveLayout();
        });

        StringRequest leaveDataReq = new StringRequest(Request.Method.GET, leaveDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String d_date = docInfo.getString("d_date")
                                .equals("null") ? "0" : docInfo.getString("d_date");
                        String date_day = docInfo.getString("date_day")
                                .equals("null") ? "0" : docInfo.getString("date_day");
                        String emp_id = docInfo.getString("emp_id")
                                .equals("null") ? "0" : docInfo.getString("emp_id");
                        String emp_name = docInfo.getString("emp_name")
                                .equals("null") ? "0" : docInfo.getString("emp_name");
                        String leave = docInfo.getString("leave")
                                .equals("null") ? "0" : docInfo.getString("leave");
                        String h_flag = docInfo.getString("h_flag")
                                .equals("null") ? "0" : docInfo.getString("h_flag");

                        dateWiseLeaveLists.add(new DateWiseLeaveList(d_date,date_day,emp_id,emp_name,leave,h_flag));

                    }
                }

                requestQueue.add(leaveEmpReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLeaveLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLeaveLayout();
        });

        requestQueue.add(leaveDataReq);
    }

    private void updateLeaveLayout() {
        if (conn) {
            if (connected) {
                leaveTabFullLayout.setVisibility(View.VISIBLE);
                leaveCircularProgress.setVisibility(View.GONE);
                leaveRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                for (int e = 0; e < empWiseLeaveLists.size(); e++) {
                    String emp_name = empWiseLeaveLists.get(e).getEmp_name();
                    for (int j = 0; j < dateWiseLeaveLists.size(); j++) {
                        if (emp_name.equals(dateWiseLeaveLists.get(j).getEmp_name())) {
                            for (int i = 0; i < dateList.size(); i++) {
                                String dd = dateList.get(i);
                                if (dd.equals(dateWiseLeaveLists.get(j).getD_date())) {
                                    if (i == 0) {
                                        empWiseLeaveLists.get(e).setFirst_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        break;
                                    }
                                    else if (i == 1) {
                                        empWiseLeaveLists.get(e).setSecond_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        break;
                                    }
                                    else if (i == 2) {
                                        empWiseLeaveLists.get(e).setThird_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        break;
                                    }
                                    else if (i == 3) {
                                        empWiseLeaveLists.get(e).setFourth_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        break;
                                    }
                                    else if (i == 4) {
                                        empWiseLeaveLists.get(e).setFifth_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        break;
                                    }
                                    else if (i == 5) {
                                        empWiseLeaveLists.get(e).setSixth_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        break;
                                    }
                                    else if (i == 6) {
                                        if (dateWiseLeaveLists.get(j).getH_flag().equals("1")) {
                                            empWiseLeaveLists.get(e).setSeventh_day_leave_name(dateWiseLeaveLists.get(j).getLeave());
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (dateWiseLeaveLists.isEmpty()) {
                    noLeaveMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noLeaveMsg.setVisibility(View.GONE);
                }

                leaveHRAdapter = new LeaveHRAdapter(Dashboard.this, empWiseLeaveLists);
                leaveView.setAdapter(leaveHRAdapter);

                loading = false;

            }
            else {
                alertMessage4();
            }
        }
        else {
            alertMessage4();
        }
    }

    public void alertMessage4() {
        leaveTabFullLayout.setVisibility(View.GONE);
        leaveCircularProgress.setVisibility(View.GONE);
        leaveRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getPayRollData() {
        payRollFullLayout.setVisibility(View.GONE);
        payRollCircularProgress.setVisibility(View.VISIBLE);
        payRollRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        working_hour = "";
        salary_gen = "";
        sal_deduct = "";
        sal_refund = "";
        allowance_total = "";
        net_pay = "";
        work_days = "";

        String payDataUrl = api_url_front+"hrm_dashboard/getPayrollData?first_date="+payRollFirstDate+"&last_date="+payRollEndDate;
        String workDaysUrl = api_url_front+"hrm_dashboard/getAttWorkingDays?first_date="+payRollFirstDate+"&last_date="+payRollEndDate;

        RequestQueue requestQueue = Volley.newRequestQueue(Dashboard.this);

        StringRequest workingDaysReq = new StringRequest(Request.Method.GET, workDaysUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject workDaysInfo = array.getJSONObject(i);

                        work_days = workDaysInfo.getString("work_days")
                                .equals("null") ? "0" : workDaysInfo.getString("work_days");
                    }
                }
                connected = true;
                updatePayRollLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updatePayRollLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updatePayRollLayout();
        });

        StringRequest payDataReq = new StringRequest(Request.Method.GET, payDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        working_hour = docInfo.getString("total")
                                .equals("null") ? "0" : docInfo.getString("total");
                        salary_gen = docInfo.getString("salary")
                                .equals("null") ? "0" : docInfo.getString("salary");
                        sal_refund = docInfo.getString("refundable")
                                .equals("null") ? "0" : docInfo.getString("refundable");
                        allowance_total = docInfo.getString("other_allowance")
                                .equals("null") ? "0" : docInfo.getString("other_allowance");
                        sal_deduct = docInfo.getString("deduction")
                                .equals("null") ? "0" : docInfo.getString("deduction");
                        net_pay = docInfo.getString("net_payable")
                                .equals("null") ? "0" : docInfo.getString("net_payable");

                    }
                }

                requestQueue.add(workingDaysReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updatePayRollLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updatePayRollLayout();
        });

        requestQueue.add(payDataReq);
    }

    private void updatePayRollLayout() {
        if (conn) {
            if (connected) {
                payRollFullLayout.setVisibility(View.VISIBLE);
                payRollCircularProgress.setVisibility(View.GONE);
                payRollRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double s_g = Double.parseDouble(salary_gen);
                double rfn = Double.parseDouble(sal_refund);
                double allow = Double.parseDouble(allowance_total);
                double dc = Double.parseDouble(sal_deduct);
                double n_s = Double.parseDouble(net_pay);

                String w_h = working_hour + " h";
                workingHour.setText(w_h);

                String sg = "৳ " + formatter.format(s_g);
                salaryGen.setText(sg);

                String ref = "৳ " + formatter.format(rfn);
                salRefund.setText(ref);

                String at = "৳ " + formatter.format(allow);
                allowanceTotal.setText(at);

                String ded = "৳ " + formatter.format(dc);
                salDeduction.setText(ded);

                String ns = "৳ " + formatter.format(n_s);
                netPayable.setText(ns);

                String wd = "WD: " + work_days;
                payRollWd.setText(wd);

                loading = false;

            }
            else {
                alertMessage5();
            }
        }
        else {
            alertMessage5();
        }
    }

    public void alertMessage5() {
        payRollFullLayout.setVisibility(View.GONE);
        payRollCircularProgress.setVisibility(View.GONE);
        payRollRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(Dashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}