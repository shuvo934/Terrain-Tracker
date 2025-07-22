package ttit.com.shuvo.terraintracker.attendance;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ttit.com.shuvo.terraintracker.Constants.api_url_front;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.adapters.AttendanceReportAdapter;
import ttit.com.shuvo.terraintracker.attendance.araylists.AttendanceReportList;
import ttit.com.shuvo.terraintracker.attendance.araylists.BranchList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DepartmentList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DesignationList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DivisionList;
import ttit.com.shuvo.terraintracker.attendance.araylists.EmployeeList;
import ttit.com.shuvo.terraintracker.timeline.TimeLineActivity;

public class EmpWiseAttendance extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    LinearLayout branchSelect;
    TextView branchName;
    ArrayList<BranchList> branchLists;

    AppCompatAutoCompleteTextView divisionSelect;
    ArrayList<DivisionList> divisionLists;

    TextInputLayout departmentSelectLay;
    AppCompatAutoCompleteTextView departmentSelect;
    ArrayList<DepartmentList> departmentLists;

    TextInputLayout designationSelectLay;
    AppCompatAutoCompleteTextView designationSelect;
    ArrayList<DesignationList> designationLists;

    TextInputLayout employeeSelectLay;
    AppCompatAutoCompleteTextView employeeSelect;
    ArrayList<EmployeeList> employeeLists;

    ScrollView empDataLay;

    TextView empName;
    TextView empCode;
    TextView empDesignation;
    TextView empCenter;
    MaterialButton trackRecord;

    String emp_name = "";
    String emp_code = "";
    String emp_designation = "";
    String emp_center = "";

    TextView monthSelection;
    String first_date = "";
    String last_date = "";
    String firstDateToView = "";
    String lastDateToView = "";

    PieChart attChart;
    ArrayList<PieEntry> attPieEntry;
    String present_tot = "";
    String absent_tot = "";
    String leave_tot = "";
    String holiday_tot = "";
    String late_tot = "";
    String early_tot = "";
    String both_late_early_tot = "";

    String selected_coa_id = "";
    String selected_div_id = "";
    String selected_dept_id = "";
    String selected_desig_id = "";
    String selected_emp_id = "";

    TextView dateToDate;

    TextView calenderDays;
    TextView totalWorking;
    TextView presentDays;
    TextView absentDays;
    TextView weeklyHolidays;
    TextView holidays;
    TextView workWeekend;
    TextView workHolidays;
    TextView leaveDays;
    TextView workLeaveDays;

    LinearLayout attenData;
    LinearLayout attenDataNot;

    String present_days = "";
    String absent_days = "";
    String weekend = "";
    String present_weekend = "";
    String hol = "";
    String present_holi = "";
    String working_days = "";
    String leave_days = "";
    String work_leave_days = "";
    String trackerAvailable = "0";
    int daysInMonth = 0;

    RecyclerView reportView;
    RecyclerView.LayoutManager layoutManager;
    AttendanceReportAdapter attendanceReportAdapter;

    ArrayList<AttendanceReportList> attendanceReportLists;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    Logger logger = Logger.getLogger(EmpWiseAttendance.class.getName());

    PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_wise_attendance);
        fullLayout = findViewById(R.id.emp_wise_attendance_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_emp_wise_attendance);
        circularProgressIndicator.setVisibility(GONE);

        backButton = findViewById(R.id.back_logo_of_emp_wise_attendance);

        branchSelect = findViewById(R.id.branch_selection_for_emp_att);
        branchName = findViewById(R.id.selected_branch_name_emp_att);
        branchLists = new ArrayList<>();

        popupMenu = new PopupMenu(EmpWiseAttendance.this, branchSelect);

        divisionSelect = findViewById(R.id.division_select_for_emp_att);
        divisionLists = new ArrayList<>();

        departmentSelectLay = findViewById(R.id.spinner_layout_department_select_for_emp_att);
        departmentSelectLay.setEnabled(false);
        departmentSelect = findViewById(R.id.department_select_for_emp_att);
        departmentLists = new ArrayList<>();

        designationSelectLay = findViewById(R.id.spinner_layout_designation_select_for_emp_att);
        designationSelectLay.setEnabled(false);
        designationSelect = findViewById(R.id.designation_select_for_emp_att);
        designationLists = new ArrayList<>();

        employeeSelectLay = findViewById(R.id.spinner_layout_emp_select_for_emp_att);
        employeeSelectLay.setEnabled(false);
        employeeSelect = findViewById(R.id.emp_select_for_emp_att);
        employeeLists = new ArrayList<>();

        empDataLay = findViewById(R.id.emp_att_data_layout);
        empDataLay.setVisibility(GONE);

        empName = findViewById(R.id.emp_name_in_emp_wise_attendace);
        empCode = findViewById(R.id.emp_code_in_emp_wise_attendace);
        empDesignation = findViewById(R.id.emp_designation_in_emp_wise_attendace);
        empCenter = findViewById(R.id.emp_pri_loc_in_emp_wise_attendace);
        trackRecord = findViewById(R.id.track_record_button);
        trackRecord.setVisibility(GONE);

        monthSelection = findViewById(R.id.select_month_for_att_graph_emp_wise);

        attChart = findViewById(R.id.emp_wise_attendance_pie_chart);
        attPieEntry = new ArrayList<>();

        dateToDate = findViewById(R.id.date_from_report);

        attenData = findViewById(R.id.attendancebefore_text);
        attenDataNot = findViewById(R.id.no_data_msg_attendance);

        reportView = findViewById(R.id.attnd_list_view);

        calenderDays = findViewById(R.id.days_in_month);
        totalWorking = findViewById(R.id.working_days_in_month);
        presentDays = findViewById(R.id.present_days_in_month);
        absentDays = findViewById(R.id.absent_days_in_month);
        weeklyHolidays = findViewById(R.id.weekly_holidays_days_in_month);
        holidays = findViewById(R.id.holidays_days_in_month);
        workWeekend = findViewById(R.id.work_weekend_days_in_month);
        workHolidays = findViewById(R.id.work_on_holi_days_in_month);
        leaveDays = findViewById(R.id.leave_days_in_month);
        workLeaveDays = findViewById(R.id.work_on_leave_days_in_month);

        attendanceReportLists = new ArrayList<>();

        reportView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        reportView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(reportView.getContext(),DividerItemDecoration.VERTICAL);
        reportView.addItemDecoration(dividerItemDecoration);

        Intent intent = getIntent();
        selected_coa_id = intent.getStringExtra("COA_ID") != null ? intent.getStringExtra("COA_ID") : "";
        selected_div_id = intent.getStringExtra("DIV_ID") != null ? intent.getStringExtra("DIV_ID") : "";
        selected_dept_id = intent.getStringExtra("DEPT_ID") != null ? intent.getStringExtra("DEPT_ID") : "";
        selected_desig_id = intent.getStringExtra("DESIG_ID") != null ? intent.getStringExtra("DESIG_ID") : "";
        selected_emp_id = intent.getStringExtra("EMP_ID") != null ? intent.getStringExtra("EMP_ID") : "";

        attChartInit();

        getCurrentDateMonthYear();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String branch_name = "";
            for (int i = 0; i <branchLists.size(); i++) {
                if (String.valueOf(menuItem.getItemId()).equals(branchLists.get(i).getId())) {

                    selected_coa_id = branchLists.get(i).getId();
                    branch_name = branchLists.get(i).getName();
                }
            }

            branchName.setText(branch_name);

            if (selected_coa_id.equals("30")) {
                getDivisions();
            }
            else {
                getDivisionsCoa();
            }
            return false;
        });

        branchSelect.setOnClickListener(view -> popupMenu.show());

        divisionSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);

            selected_desig_id = "";
            designationSelect.setText("");
            designationSelectLay.setEnabled(false);

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);

            empDataLay.setVisibility(GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <divisionLists.size(); i++) {
                if (name.equals(divisionLists.get(i).getDivm_name())) {

                    selected_div_id = divisionLists.get(i).getDivm_id();
                }
            }

            if (selected_div_id.isEmpty()) {
                divisionSelect.setText("");
            }

            getDepartment();

        });

        departmentSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_desig_id = "";
            designationSelect.setText("");
            designationSelectLay.setEnabled(false);

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);

            empDataLay.setVisibility(GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <departmentLists.size(); i++) {
                if (name.equals(departmentLists.get(i).getDept_name())) {

                    selected_dept_id = departmentLists.get(i).getDept_id();
                }
            }

            if (selected_dept_id.isEmpty()) {
                departmentSelect.setText("");
            }

            getDesignations();

        });

        designationSelect.setOnItemClickListener((parent, view, position, id) -> {

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);

            empDataLay.setVisibility(GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <designationLists.size(); i++) {
                if (name.equals(designationLists.get(i).getDesig_name())) {

                    selected_desig_id = designationLists.get(i).getDesig_id();
                }
            }

            if (selected_desig_id.isEmpty()) {
                designationSelect.setText("");
            }

            getEmployees();

        });

        employeeSelect.setOnItemClickListener((parent, view, position, id) -> {

            empDataLay.setVisibility(GONE);
            emp_name = "";
            emp_code = "";
            emp_designation = "";
            emp_center = "";

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <employeeLists.size(); i++) {
                if (name.equals(employeeLists.get(i).getEmp_name())) {

                    selected_emp_id = employeeLists.get(i).getEmp_id();
                    emp_name = employeeLists.get(i).getEmp_name();
                    emp_code = employeeLists.get(i).getEmp_code();
                    emp_designation = employeeLists.get(i).getJob_calling_title();
                    emp_center = employeeLists.get(i).getCoa_name();
                }
            }

            if (selected_emp_id.isEmpty()) {
                employeeSelect.setText("");
            }

            getAttendanceData();

        });

        monthSelection.setOnClickListener(v -> {

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
            nowMonNumb = nowMonNumb - 1;
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

            calendar2.add(Calendar.MONTH, 1);
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
            if (!first_date.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                Date date = null;
                try {
                    date = dateFormat.parse(first_date);
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


                first_date = "01-"+mon+"-"+yearName;
                firstDateToView = "01 "+mon.toUpperCase(Locale.ENGLISH) +", "+ year;
                String s = "MONTH: " + mon+"-"+yearName;
                monthSelection.setText(s);

                SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                Date today11 = null;
                try {
                    today11 = sss.parse(first_date);
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
                    last_date =  s1+ "-" + mon +"-"+ yearName;
                    lastDateToView = s1 + " " + mon.toUpperCase(Locale.ENGLISH) + ", " + year;
                }

                YearMonth yearMonthObject;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    yearMonthObject = YearMonth.of(year, month);
                    daysInMonth = yearMonthObject.lengthOfMonth();
                }
                else {
                    int iDay = 1;
                    Calendar mycal = new GregorianCalendar(year, monthOfYear, iDay);
                    daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
                }

                getAttendanceData();
            });

        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(EmpWiseAttendance.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(EmpWiseAttendance.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        if (!selected_div_id.isEmpty() && !selected_dept_id.isEmpty() && !selected_desig_id.isEmpty() && !selected_emp_id.isEmpty()) {
            if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
                getAllData();
            }
            else {
                getAllDataCoa();
            }
        }
        else {
            getDivisions();
        }

        trackRecord.setOnClickListener(v -> {
            Intent intent_1 = new Intent(EmpWiseAttendance.this, TimeLineActivity.class);
            intent_1.putExtra("EMP_ID", selected_emp_id);
            startActivity(intent_1);
        });
    }

    private void getCurrentDateMonthYear() {
        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM, yyyy",Locale.ENGLISH);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM, yyyy",Locale.ENGLISH);

        first_date = simpleDateFormat.format(dd).toUpperCase(Locale.ENGLISH);
        first_date = "01-"+first_date;
        firstDateToView = "01 " +dateFormat1.format(dd).toUpperCase(Locale.ENGLISH);

        Date today = null;
        try {
            today = df.parse(first_date);
        } catch (ParseException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
        }

        Calendar calendar1 = Calendar.getInstance();
        if (today != null) {
            calendar1.setTime(today);
            calendar1.add(Calendar.MONTH, 1);
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            calendar1.add(Calendar.DATE, -1);

            Date lastDayOfMonth = calendar1.getTime();

            last_date = df.format(lastDayOfMonth).toUpperCase(Locale.ENGLISH);
            lastDateToView = dateFormat2.format(lastDayOfMonth).toUpperCase(Locale.ENGLISH);
        }

        YearMonth yearMonthObject;
        SimpleDateFormat presentYear = new SimpleDateFormat("yyyy",Locale.ENGLISH);
        String yyyy = presentYear.format(dd);
        SimpleDateFormat monthNumb = new SimpleDateFormat("MM",Locale.ENGLISH);
        String monthNNN = monthNumb.format(dd);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(Integer.parseInt(yyyy), Integer.parseInt(monthNNN));
            daysInMonth = yearMonthObject.lengthOfMonth();
        }
        else {
            int iDay = 1;
            Calendar mycal = new GregorianCalendar(Integer.parseInt(yyyy), Integer.parseInt(monthNNN)-1, iDay);
            daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        String mo_name = simpleDateFormat.format(dd);
        mo_name = mo_name.toUpperCase(Locale.ENGLISH);
        String ms = "MONTH: " + mo_name;
        monthSelection.setText(ms);
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

    // -- Divisions
    public void getDivisions() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        empDataLay.setVisibility(GONE);
        conn = false;
        connected = false;
        loading = true;
        divisionLists = new ArrayList<>();
        branchLists = new ArrayList<>();

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivision";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                connected = true;
                updateDivisionLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDivisionLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDivisionLay();
        });

        StringRequest brnReq = new StringRequest(Request.Method.GET, brnUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    branchLists.add(new BranchList("30","None"));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        branchLists.add(new BranchList(coa_id,coa_name));
                    }
                }
                requestQueue.add(divReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDivisionLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDivisionLay();
        });

        requestQueue.add(brnReq);
    }

    public void updateDivisionLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                selected_div_id = "";
                divisionSelect.setText("");

                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(false);

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(false);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(GONE);

                popupMenu.getMenuInflater().inflate(R.menu.branch_menu, popupMenu.getMenu());

                Menu menu = popupMenu.getMenu();
                menu.clear();
                for(int i = 0; i < branchLists.size(); i++) {
                    menu.add(0,Integer.parseInt(branchLists.get(i).getId()),Menu.NONE,branchLists.get(i).getName());
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageDiv();
            }
        }
        else {
            alertMessageDiv();
        }
    }

    public void alertMessageDiv() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDivisions();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // -- Divisions with COA
    public void getDivisionsCoa() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        empDataLay.setVisibility(GONE);
        conn = false;
        connected = false;
        loading = true;
        divisionLists = new ArrayList<>();

        String divUrl = api_url_front+"hrm_dashboard/getDivisionWithCoa?p_coa_id="+selected_coa_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                connected = true;
                updateDivisionLayCoa();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDivisionLayCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDivisionLayCoa();
        });

        requestQueue.add(divReq);
    }

    public void updateDivisionLayCoa() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                selected_div_id = "";
                divisionSelect.setText("");

                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(false);

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(false);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageDivCoa();
            }
        }
        else {
            alertMessageDivCoa();
        }
    }

    public void alertMessageDivCoa() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDivisionsCoa();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // --- Departments
    public void getDepartment() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        departmentLists = new ArrayList<>();

        String deptUrl;
        if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
            deptUrl = api_url_front + "hrm_dashboard/getDepartmentByDiv?p_div_id=" + selected_div_id;
        }
        else {
            deptUrl = api_url_front+"hrm_dashboard/getDepartmentByDivCoa?p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest depReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                connected = true;
                updateDeptLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDeptLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDeptLay();
        });

        requestQueue.add(depReq);
    }

    public void updateDeptLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(true);

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(false);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < departmentLists.size(); i++) {
                    type.add(departmentLists.get(i).getDept_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                departmentSelect.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageDept();
            }
        }
        else {
            alertMessageDept();
        }
    }

    public void alertMessageDept() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDepartment();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // --- Designations
    public void getDesignations() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        designationLists = new ArrayList<>();

        String desigUrl;
        if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
            desigUrl = api_url_front + "hrm_dashboard/getDesignationsByDept?p_dep_Id=" + selected_dept_id + "&p_div_id=" + selected_div_id;
        }
        else {
            desigUrl = api_url_front+"hrm_dashboard/getDesignationsByDeptCoa?p_dep_Id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest desigReq = new StringRequest(Request.Method.GET, desigUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" :info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" :info.getString("desig_name");

                        designationLists.add(new DesignationList(desig_id,desig_name));
                    }
                }
                connected = true;
                updateDesigLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDesigLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDesigLay();
        });

        requestQueue.add(desigReq);
    }

    public void updateDesigLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                selected_desig_id = "";
                designationSelect.setText("");
                designationSelectLay.setEnabled(true);

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);

                empDataLay.setVisibility(GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < designationLists.size(); i++) {
                    type.add(designationLists.get(i).getDesig_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                designationSelect.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageDesig();
            }
        }
        else {
            alertMessageDesig();
        }
    }

    public void alertMessageDesig() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDesignations();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // --- Employees
    public void getEmployees() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        employeeLists = new ArrayList<>();

        String empUrl;
        if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
            empUrl = api_url_front + "hrm_dashboard/getEmployeeByDesig?p_dep_id=" + selected_dept_id + "&p_div_id=" + selected_div_id + "&p_desig_id=" + selected_desig_id;
        }
        else {
            empUrl = api_url_front+"hrm_dashboard/getEmployeeByDesigCoa?p_dep_id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_desig_id="+selected_desig_id+"&p_coa_id="+selected_coa_id;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,coa_name));
                    }
                }
                connected = true;
                updateEmpLay();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateEmpLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateEmpLay();
        });

        requestQueue.add(empReq);
    }

    public void updateEmpLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(true);

                empDataLay.setVisibility(GONE);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < employeeLists.size(); i++) {
                    type.add(employeeLists.get(i).getEmp_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                employeeSelect.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                alertMessageEmp();
            }
        }
        else {
            alertMessageEmp();
        }
    }

    public void alertMessageEmp() {
        fullLayout.setVisibility(VISIBLE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getEmployees();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // --- Attendance Data
    public void getAttendanceData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
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

        attendanceReportLists = new ArrayList<>();

        present_days = "";
        absent_days = "";
        weekend = "";
        present_weekend = "";
        hol = "";
        present_holi = "";
        working_days = "";
        leave_days = "";
        work_leave_days = "";
        trackerAvailable = "0";

        String attUrl = api_url_front+"hrm_dashboard/getAttendanceCountEmp?first_date="+first_date+"&last_date="+last_date+"&p_emp_id="+selected_emp_id;
        String attReportUrl = api_url_front+"hrm_dashboard/getAttReportData?first_date="+first_date+"&last_date="+last_date+"&emp_id="+selected_emp_id;
        String attStatUrl =api_url_front+"hrm_dashboard/getAttStatus?first_date="+first_date+"&last_date="+last_date+"&emp_id="+selected_emp_id;
        String workDaysUrl = api_url_front+"hrm_dashboard/getAttWorkingDays?first_date="+first_date+"&last_date="+last_date;
        String trackerFlagUrl = api_url_front + "utility/getTrackerFlag/" + selected_emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest trackerFlagReq = new StringRequest(Request.Method.GET, trackerFlagUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject trackerInfo = array.getJSONObject(i);
                        trackerAvailable = trackerInfo.getString("emp_timeline_tracker_flag")
                                        .equals("null") ? "0" : trackerInfo.getString("emp_timeline_tracker_flag");
                    }
                }
                connected = true;
                updateAttendanceLay();
            } catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING, e.getMessage(), e);
                updateAttendanceLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING, error.getMessage(), error);
            updateAttendanceLay();
        });

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

                        working_days = workDaysInfo.getString("work_days")
                                .equals("null") ? "0" : workDaysInfo.getString("work_days");
                    }
                }
                requestQueue.add(trackerFlagReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAttendanceLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAttendanceLay();
        });

        StringRequest attStatReq = new StringRequest(Request.Method.GET, attStatUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attStatInfo = array.getJSONObject(i);

                        present_days = attStatInfo.getString("present_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_status");
                        absent_days = attStatInfo.getString("absent_status")
                                .equals("null") ? "0" : attStatInfo.getString("absent_status");
                        weekend = attStatInfo.getString("weekend_status")
                                .equals("null") ? "0" : attStatInfo.getString("weekend_status");
                        present_weekend = attStatInfo.getString("present_weekend")
                                .equals("null") ? "0" : attStatInfo.getString("present_weekend");
                        hol = attStatInfo.getString("holiday_status")
                                .equals("null") ? "0" : attStatInfo.getString("holiday_status");
                        present_holi = attStatInfo.getString("present_holiday_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_holiday_status");
                        leave_days = attStatInfo.getString("leave_status")
                                .equals("null") ? "0" : attStatInfo.getString("leave_status");
                        work_leave_days = attStatInfo.getString("present_leave_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_leave_status");
                    }
                }

                requestQueue.add(workingDaysReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAttendanceLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAttendanceLay();
        });

        StringRequest attendanceReportReq = new StringRequest(Request.Method.GET, attReportUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attRepInfo = array.getJSONObject(i);

                        String dac_in_date_time = attRepInfo.getString("dac_in_date_time")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_date_time");

                        String dac_late_after = attRepInfo.getString("dac_late_after")
                                .equals("null") ? "" : attRepInfo.getString("dac_late_after");
                        String dac_early_before = attRepInfo.getString("dac_early_before")
                                .equals("null") ? "" : attRepInfo.getString("dac_early_before");

                        String dac_out_date_time = attRepInfo.getString("dac_out_date_time")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_date_time");

                        String dac_date1 = attRepInfo.getString("dac_date1")
                                .equals("null") ? "" : attRepInfo.getString("dac_date1");

                        String statusShort = attRepInfo.getString("dac_attn_status")
                                .equals("null") ? "" : attRepInfo.getString("dac_attn_status");

                        String lc_name = attRepInfo.getString("lc_name")
                                .equals("null") ? "" : attRepInfo.getString("lc_name");

                        String osm_name = attRepInfo.getString("osm_name")
                                .equals("null") ? "" : attRepInfo.getString("osm_name");
                        String coa_name = attRepInfo.getString("coa_name")
                                .equals("null") ? "" : attRepInfo.getString("coa_name");

                        String inCode = attRepInfo.getString("in_machine_coa_id")
                                .equals("null") ? "" : attRepInfo.getString("in_machine_coa_id");
                        String outCode = attRepInfo.getString("out_machine_coa_id")
                                .equals("null") ? "" : attRepInfo.getString("out_machine_coa_id");

                        String in_lat = attRepInfo.getString("dac_in_attd_latitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_attd_latitude");
                        String in_lon = attRepInfo.getString("dac_in_attd_longitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_attd_longitude");
                        String out_lat = attRepInfo.getString("dac_out_attd_latitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_attd_latitude");
                        String out_lon = attRepInfo.getString("dac_out_attd_longitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_attd_longitude");

                        String status;
                        String attStatus;

                        if (!statusShort.isEmpty()) {
                            if (!dac_in_date_time.isEmpty() && dac_out_date_time.isEmpty()) {
                                status = "Out Miss";
                                attStatus = "Out Miss";
                            }else if (statusShort.equals("L") || statusShort.equals("LW") || statusShort.equals("LH")) {
                                status = lc_name;
                                if (status.isEmpty()) {
                                    status = "In Leave";
                                }
                                attStatus = "In Leave";
                            } else if (statusShort.equals("H")) {
                                status = "Holiday";
                                attStatus = "Off Day";
                            } else if (statusShort.equals("W")) {
                                status = "Weekend";
                                attStatus = "Off Day";
                            } else if (statusShort.equals("PL") || statusShort.equals("PLH") || statusShort.equals("PLW")) {
                                status = "Present & Leave";
                                attStatus = "Present on Leave Day";
                            } else if (statusShort.equals("PAT")) {
                                status = "Attended training";
                                attStatus = "White";
                            } else if (statusShort.equals("PHD") || statusShort.equals("PWD") || statusShort.equals("PLHD") || statusShort.equals("PLWD")) {
                                status = "Present & Day Off Taken";
                                attStatus = "White";
                            } else if (statusShort.equals("P") || statusShort.equals("PWWO") || statusShort.equals("PHWC") || statusShort.equals("PWWC") || statusShort.equals("PA")) {
                                status = "Present";
                                attStatus = "White";
                            } else if (statusShort.equals("PW") || statusShort.equals("PH")) {
                                status = "Present & Off Day";
                                attStatus = "Present on Off Day";
                            } else if (statusShort.equals("A")) {
                                status = "Absent";
                                attStatus = "Absent";
                            } else if (statusShort.equals("PT")) {
                                status = "Tour";
                                attStatus = "White";
                            } else if (statusShort.equals("SP")) {
                                status = "Suspend";
                                attStatus = "White";
                            } else {
                                status = "Absent";
                                attStatus = "Absent";
                            }
                        }
                        else {
                            status = "No Status";
                            attStatus = "White";
                        }

                        if (!inCode.isEmpty() && !outCode.isEmpty()) {
                            int in = Integer.parseInt(inCode);
                            int out = Integer.parseInt(outCode);
                            if (in != out ) {
                                attStatus = "Multi Station";
                            }
                        }

                        String shift = osm_name;

                        if (!lc_name.isEmpty()) {
                            shift = "";
                        }
                        if (status.equals("Weekend")) {
                            shift = "";
                        }

                        String inTime = dac_in_date_time;

                        Date in = null;
                        Date late = null;

                        String inStatus = "";

                        SimpleDateFormat tt = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                        SimpleDateFormat newtt = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);

                        if (inTime.isEmpty()) {
                            inStatus = "";
                        }
                        else {
                            try {
                                in = tt.parse(inTime);
                                late = tt.parse(dac_late_after);

                            } catch (ParseException e) {
                                logger.log(Level.WARNING,e.getMessage(),e);
                            }

                            if (in != null && late != null) {
                                inTime = newtt.format(in);
                                if (late.after(in)) {
                                    inStatus = "";
                                } else {
                                    inStatus = "Late";
                                }
                            }
                        }

                        String outTime = dac_out_date_time;

                        String outStatus = "";

                        if (outTime.isEmpty()) {
                            outStatus = "";
                        }
                        else {
                            try {
                                in = tt.parse(outTime);
                                late = tt.parse(dac_early_before);

                            } catch (ParseException e) {
                                logger.log(Level.WARNING,e.getMessage(),e);
                            }

                            if (in != null && late != null) {
                                outTime = newtt.format(in);
                                if (late.after(in)) {
                                    outStatus = "Early";
                                } else {
                                    outStatus = "";
                                }
                            }
                        }

                        attendanceReportLists.add(new AttendanceReportList(dac_date1,status,shift,coa_name,
                                inTime,inStatus,outTime,outStatus,attStatus,in_lat,in_lon,out_lat,out_lon));
                    }
                }

                requestQueue.add(attStatReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAttendanceLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAttendanceLay();
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

                requestQueue.add(attendanceReportReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAttendanceLay();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAttendanceLay();
        });

        requestQueue.add(attReq);
    }

    public void updateAttendanceLay() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                empDataLay.setVisibility(VISIBLE);

                empName.setText(emp_name);
                String tt = "("+emp_code+")";
                empCode.setText(tt);
                empDesignation.setText(emp_designation);
                empCenter.setText(emp_center);

                if (trackerAvailable.equals("1")) {
                    trackRecord.setVisibility(VISIBLE);
                }
                else {
                    trackRecord.setVisibility(GONE);
                }

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

                PieData data = new PieData(dataSet);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });

                String label = dataSet.getValues().get(0).getLabel();
                System.out.println(label);
                if (label.equals("No Data")) {
                    dataSet.setValueTextColor(Color.TRANSPARENT);
                } else {
                    dataSet.setValueTextColor(Color.BLACK);
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

                if (attendanceReportLists.isEmpty()) {
                    attenDataNot.setVisibility(VISIBLE);
                    attenData.setVisibility(GONE);
                }
                else {
                    attenData.setVisibility(VISIBLE);
                    attenDataNot.setVisibility(GONE);
                }

                String date_tot_date = firstDateToView + " -- "+ lastDateToView;
                dateToDate.setText(date_tot_date);

                attendanceReportAdapter = new AttendanceReportAdapter(attendanceReportLists, EmpWiseAttendance.this);
                reportView.setAdapter(attendanceReportAdapter);

                calenderDays.setText(String.valueOf(daysInMonth));
                totalWorking.setText(working_days);
                presentDays.setText(present_days);
                absentDays.setText(absent_days);
                weeklyHolidays.setText(weekend);
                holidays.setText(hol);
                workWeekend.setText(present_weekend);
                workHolidays.setText(present_holi);
                leaveDays.setText(leave_days);
                workLeaveDays.setText(work_leave_days);

                loading = false;

            }
            else {
                alertMessageAttendance();
            }
        }
        else {
            alertMessageAttendance();
        }
    }

    public void alertMessageAttendance() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getAttendanceData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // - getting All Data
    public void getAllData() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        empDataLay.setVisibility(GONE);
        conn = false;
        connected = false;
        loading = true;

        branchLists = new ArrayList<>();
        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        designationLists = new ArrayList<>();
        employeeLists = new ArrayList<>();

        attPieEntry = new ArrayList<>();
        present_tot = "0";
        absent_tot = "0";
        leave_tot = "0";
        holiday_tot = "0";
        late_tot = "0";
        early_tot = "0";
        both_late_early_tot = "0";

        attendanceReportLists = new ArrayList<>();

        present_days = "";
        absent_days = "";
        weekend = "";
        present_weekend = "";
        hol = "";
        present_holi = "";
        working_days = "";
        leave_days = "";
        work_leave_days = "";
        trackerAvailable = "0";

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivision";
        String deptUrl = api_url_front+"hrm_dashboard/getDepartmentByDiv?p_div_id="+selected_div_id;
        String desigUrl = api_url_front+"hrm_dashboard/getDesignationsByDept?p_dep_Id="+selected_dept_id+"&p_div_id="+selected_div_id;
        String empUrl = api_url_front+"hrm_dashboard/getEmployeeByDesig?p_dep_id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_desig_id="+selected_desig_id;
        String attUrl = api_url_front+"hrm_dashboard/getAttendanceCountEmp?first_date="+first_date+"&last_date="+last_date+"&p_emp_id="+selected_emp_id;
        String attReportUrl = api_url_front+"hrm_dashboard/getAttReportData?first_date="+first_date+"&last_date="+last_date+"&emp_id="+selected_emp_id;
        String attStatUrl =api_url_front+"hrm_dashboard/getAttStatus?first_date="+first_date+"&last_date="+last_date+"&emp_id="+selected_emp_id;
        String workDaysUrl = api_url_front+"hrm_dashboard/getAttWorkingDays?first_date="+first_date+"&last_date="+last_date;
        String trackerFlagUrl = api_url_front + "utility/getTrackerFlag/" + selected_emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest trackerFlagReq = new StringRequest(Request.Method.GET, trackerFlagUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject trackerInfo = array.getJSONObject(i);
                        trackerAvailable = trackerInfo.getString("emp_timeline_tracker_flag")
                                .equals("null") ? "0" : trackerInfo.getString("emp_timeline_tracker_flag");
                    }
                }
                connected = true;
                updateInterface();
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

                        working_days = workDaysInfo.getString("work_days")
                                .equals("null") ? "0" : workDaysInfo.getString("work_days");
                    }
                }
                requestQueue.add(trackerFlagReq);
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

        StringRequest attStatReq = new StringRequest(Request.Method.GET, attStatUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attStatInfo = array.getJSONObject(i);

                        present_days = attStatInfo.getString("present_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_status");
                        absent_days = attStatInfo.getString("absent_status")
                                .equals("null") ? "0" : attStatInfo.getString("absent_status");
                        weekend = attStatInfo.getString("weekend_status")
                                .equals("null") ? "0" : attStatInfo.getString("weekend_status");
                        present_weekend = attStatInfo.getString("present_weekend")
                                .equals("null") ? "0" : attStatInfo.getString("present_weekend");
                        hol = attStatInfo.getString("holiday_status")
                                .equals("null") ? "0" : attStatInfo.getString("holiday_status");
                        present_holi = attStatInfo.getString("present_holiday_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_holiday_status");
                        leave_days = attStatInfo.getString("leave_status")
                                .equals("null") ? "0" : attStatInfo.getString("leave_status");
                        work_leave_days = attStatInfo.getString("present_leave_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_leave_status");
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

        StringRequest attendanceReportReq = new StringRequest(Request.Method.GET, attReportUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attRepInfo = array.getJSONObject(i);

                        String dac_in_date_time = attRepInfo.getString("dac_in_date_time")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_date_time");

                        String dac_late_after = attRepInfo.getString("dac_late_after")
                                .equals("null") ? "" : attRepInfo.getString("dac_late_after");
                        String dac_early_before = attRepInfo.getString("dac_early_before")
                                .equals("null") ? "" : attRepInfo.getString("dac_early_before");

                        String dac_out_date_time = attRepInfo.getString("dac_out_date_time")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_date_time");

                        String dac_date1 = attRepInfo.getString("dac_date1")
                                .equals("null") ? "" : attRepInfo.getString("dac_date1");

                        String statusShort = attRepInfo.getString("dac_attn_status")
                                .equals("null") ? "" : attRepInfo.getString("dac_attn_status");

                        String lc_name = attRepInfo.getString("lc_name")
                                .equals("null") ? "" : attRepInfo.getString("lc_name");

                        String osm_name = attRepInfo.getString("osm_name")
                                .equals("null") ? "" : attRepInfo.getString("osm_name");
                        String coa_name = attRepInfo.getString("coa_name")
                                .equals("null") ? "" : attRepInfo.getString("coa_name");

                        String inCode = attRepInfo.getString("in_machine_coa_id")
                                .equals("null") ? "" : attRepInfo.getString("in_machine_coa_id");
                        String outCode = attRepInfo.getString("out_machine_coa_id")
                                .equals("null") ? "" : attRepInfo.getString("out_machine_coa_id");

                        String in_lat = attRepInfo.getString("dac_in_attd_latitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_attd_latitude");
                        String in_lon = attRepInfo.getString("dac_in_attd_longitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_attd_longitude");
                        String out_lat = attRepInfo.getString("dac_out_attd_latitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_attd_latitude");
                        String out_lon = attRepInfo.getString("dac_out_attd_longitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_attd_longitude");

                        String status;
                        String attStatus;

                        if (!statusShort.isEmpty()) {
                            if (!dac_in_date_time.isEmpty() && dac_out_date_time.isEmpty()) {
                                status = "Out Miss";
                                attStatus = "Out Miss";
                            } else if (statusShort.equals("L") || statusShort.equals("LW") || statusShort.equals("LH")) {
                                status = lc_name;
                                if (status.isEmpty()) {
                                    status = "In Leave";
                                }
                                attStatus = "In Leave";
                            } else if (statusShort.equals("H")) {
                                status = "Holiday";
                                attStatus = "Off Day";
                            } else if (statusShort.equals("W")) {
                                status = "Weekend";
                                attStatus = "Off Day";
                            } else if (statusShort.equals("PL") || statusShort.equals("PLH") || statusShort.equals("PLW")) {
                                status = "Present & Leave";
                                attStatus = "Present on Leave Day";
                            } else if (statusShort.equals("PAT")) {
                                status = "Attended training";
                                attStatus = "White";
                            } else if (statusShort.equals("PHD") || statusShort.equals("PWD") || statusShort.equals("PLHD") || statusShort.equals("PLWD")) {
                                status = "Present & Day Off Taken";
                                attStatus = "White";
                            } else if (statusShort.equals("P") || statusShort.equals("PWWO") || statusShort.equals("PHWC") || statusShort.equals("PWWC") || statusShort.equals("PA")) {
                                status = "Present";
                                attStatus = "White";
                            } else if (statusShort.equals("PW") || statusShort.equals("PH")) {
                                status = "Present & Off Day";
                                attStatus = "Present on Off Day";
                            } else if (statusShort.equals("A")) {
                                status = "Absent";
                                attStatus = "Absent";
                            } else if (statusShort.equals("PT")) {
                                status = "Tour";
                                attStatus = "White";
                            } else if (statusShort.equals("SP")) {
                                status = "Suspend";
                                attStatus = "White";
                            } else {
                                status = "Absent";
                                attStatus = "Absent";
                            }
                        }
                        else {
                            status = "No Status";
                            attStatus = "White";
                        }

                        if (!inCode.isEmpty() && !outCode.isEmpty()) {
                            int in = Integer.parseInt(inCode);
                            int out = Integer.parseInt(outCode);
                            if (in != out ) {
                                attStatus = "Multi Station";
                            }
                        }

                        String shift = osm_name;

                        if (!lc_name.isEmpty()) {
                            shift = "";
                        }
                        if (status.equals("Weekend")) {
                            shift = "";
                        }

                        String inTime = dac_in_date_time;

                        Date in = null;
                        Date late = null;

                        String inStatus = "";

                        SimpleDateFormat tt = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                        SimpleDateFormat newtt = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);

                        if (inTime.isEmpty()) {
                            inStatus = "";
                        }
                        else {
                            try {
                                in = tt.parse(inTime);
                                late = tt.parse(dac_late_after);

                            } catch (ParseException e) {
                                logger.log(Level.WARNING,e.getMessage(),e);
                            }

                            if (in != null && late != null) {
                                inTime = newtt.format(in);
                                if (late.after(in)) {
                                    inStatus = "";
                                } else {
                                    inStatus = "Late";
                                }
                            }
                        }

                        String outTime = dac_out_date_time;

                        String outStatus = "";

                        if (outTime.isEmpty()) {
                            outStatus = "";
                        }
                        else {
                            try {
                                in = tt.parse(outTime);
                                late = tt.parse(dac_early_before);

                            } catch (ParseException e) {
                                logger.log(Level.WARNING,e.getMessage(),e);
                            }

                            if (in != null && late != null) {
                                outTime = newtt.format(in);
                                if (late.after(in)) {
                                    outStatus = "Early";
                                } else {
                                    outStatus = "";
                                }
                            }
                        }

                        attendanceReportLists.add(new AttendanceReportList(dac_date1,status,shift,coa_name,
                                inTime,inStatus,outTime,outStatus,attStatus,in_lat,in_lon,out_lat,out_lon));
                    }
                }

                requestQueue.add(attStatReq);
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

                requestQueue.add(attendanceReportReq);
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

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,coa_name));
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

        StringRequest desigReq = new StringRequest(Request.Method.GET, desigUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" :info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" :info.getString("desig_name");

                        designationLists.add(new DesignationList(desig_id,desig_name));
                    }
                }
                requestQueue.add(empReq);
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

        StringRequest depReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                requestQueue.add(desigReq);
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

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                requestQueue.add(depReq);
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

        StringRequest brnReq = new StringRequest(Request.Method.GET, brnUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    branchLists.add(new BranchList("30","None"));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        branchLists.add(new BranchList(coa_id,coa_name));
                    }
                }
                requestQueue.add(divReq);
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

        requestQueue.add(brnReq);
    }

    public void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(VISIBLE);
                circularProgressIndicator.setVisibility(GONE);
                conn = false;
                connected = false;

                popupMenu.getMenuInflater().inflate(R.menu.branch_menu, popupMenu.getMenu());
                Menu menu = popupMenu.getMenu();
                menu.clear();
                for(int i = 0; i < branchLists.size(); i++) {
                    menu.add(0,Integer.parseInt(branchLists.get(i).getId()),Menu.NONE,branchLists.get(i).getName());
                }

                if (selected_coa_id.isEmpty()) {
                    String bb = "None";
                    branchName.setText(bb);
                }
                else {
                    String brn = "";
                    for (int i = 0; i < branchLists.size(); i++) {
                        if (selected_coa_id.equals(branchLists.get(i).getId())) {
                            brn = branchLists.get(i).getName();
                        }
                    }
                    branchName.setText(brn);
                }
                // --- Division
                String div_name = "";
                for (int i = 0; i < divisionLists.size(); i++) {
                    if (selected_div_id.equals(divisionLists.get(i).getDivm_id())) {
                        div_name = divisionLists.get(i).getDivm_name();
                    }
                }
                divisionSelect.setText(div_name);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                // --- Department
                String dept_name = "";
                for (int i = 0; i < departmentLists.size(); i++) {
                    if (selected_dept_id.equals(departmentLists.get(i).getDept_id())) {
                        dept_name = departmentLists.get(i).getDept_name();
                    }
                }
                departmentSelect.setText(dept_name);
                departmentSelectLay.setEnabled(true);

                ArrayList<String> dept_type = new ArrayList<>();
                for(int i = 0; i < departmentLists.size(); i++) {
                    dept_type.add(departmentLists.get(i).getDept_name());
                }

                ArrayAdapter<String> dept_arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,dept_type);
                departmentSelect.setAdapter(dept_arrayAdapter);

                // --- Designation
                String desig_name = "";
                for (int i = 0; i < designationLists.size(); i++) {
                    if (selected_desig_id.equals(designationLists.get(i).getDesig_id())) {
                        desig_name = designationLists.get(i).getDesig_name();
                    }
                }
                designationSelect.setText(desig_name);
                designationSelectLay.setEnabled(true);

                ArrayList<String> desig_type = new ArrayList<>();
                for(int i = 0; i < designationLists.size(); i++) {
                    desig_type.add(designationLists.get(i).getDesig_name());
                }

                ArrayAdapter<String> desig_arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,desig_type);
                designationSelect.setAdapter(desig_arrayAdapter);

                // --- Employees
                emp_name = "";
                emp_code = "";
                emp_designation = "";
                emp_center = "";
                for (int i = 0; i < employeeLists.size(); i++) {
                    if (selected_emp_id.equals(employeeLists.get(i).getEmp_id())) {
                        emp_name = employeeLists.get(i).getEmp_name();
                        emp_code = employeeLists.get(i).getEmp_code();
                        emp_designation = employeeLists.get(i).getJob_calling_title();
                        emp_center = employeeLists.get(i).getCoa_name();
                    }
                }
                employeeSelect.setText(emp_name);
                employeeSelectLay.setEnabled(true);

                ArrayList<String> emp_type = new ArrayList<>();
                for(int i = 0; i < employeeLists.size(); i++) {
                    emp_type.add(employeeLists.get(i).getEmp_name());
                }

                ArrayAdapter<String> emp_arrayAdapter = new ArrayAdapter<>(EmpWiseAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,emp_type);
                employeeSelect.setAdapter(emp_arrayAdapter);


                empDataLay.setVisibility(VISIBLE);
                empName.setText(emp_name);
                String tt = "("+emp_code+")";
                empCode.setText(tt);
                empDesignation.setText(emp_designation);
                empCenter.setText(emp_center);

                if (trackerAvailable.equals("1")) {
                    trackRecord.setVisibility(VISIBLE);
                }
                else {
                    trackRecord.setVisibility(GONE);
                }

                // --- Pie chart Initialization
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

                PieData data = new PieData(dataSet);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });

                String label = dataSet.getValues().get(0).getLabel();
                System.out.println(label);
                if (label.equals("No Data")) {
                    dataSet.setValueTextColor(Color.TRANSPARENT);
                } else {
                    dataSet.setValueTextColor(Color.BLACK);
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

                if (attendanceReportLists.isEmpty()) {
                    attenDataNot.setVisibility(VISIBLE);
                    attenData.setVisibility(GONE);
                }
                else {
                    attenData.setVisibility(VISIBLE);
                    attenDataNot.setVisibility(GONE);
                }

                String date_tot_date = firstDateToView + " -- "+ lastDateToView;
                dateToDate.setText(date_tot_date);

                attendanceReportAdapter = new AttendanceReportAdapter(attendanceReportLists, EmpWiseAttendance.this);
                reportView.setAdapter(attendanceReportAdapter);

                calenderDays.setText(String.valueOf(daysInMonth));
                totalWorking.setText(working_days);
                presentDays.setText(present_days);
                absentDays.setText(absent_days);
                weeklyHolidays.setText(weekend);
                holidays.setText(hol);
                workWeekend.setText(present_weekend);
                workHolidays.setText(present_holi);
                leaveDays.setText(leave_days);
                workLeaveDays.setText(work_leave_days);

                loading = false;

            }
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    if (selected_coa_id.isEmpty() || selected_coa_id.equals("30")) {
                        getAllData();
                    }
                    else {
                        getAllDataCoa();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getAllDataCoa() {
        fullLayout.setVisibility(GONE);
        circularProgressIndicator.setVisibility(VISIBLE);
        empDataLay.setVisibility(GONE);
        conn = false;
        connected = false;
        loading = true;

        branchLists = new ArrayList<>();
        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        designationLists = new ArrayList<>();
        employeeLists = new ArrayList<>();

        attPieEntry = new ArrayList<>();
        present_tot = "0";
        absent_tot = "0";
        leave_tot = "0";
        holiday_tot = "0";
        late_tot = "0";
        early_tot = "0";
        both_late_early_tot = "0";

        attendanceReportLists = new ArrayList<>();

        present_days = "";
        absent_days = "";
        weekend = "";
        present_weekend = "";
        hol = "";
        present_holi = "";
        working_days = "";
        leave_days = "";
        work_leave_days = "";
        trackerAvailable = "0";

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivisionWithCoa?p_coa_id="+selected_coa_id;
        String deptUrl = api_url_front+"hrm_dashboard/getDepartmentByDivCoa?p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        String desigUrl = api_url_front+"hrm_dashboard/getDesignationsByDeptCoa?p_dep_Id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_coa_id="+selected_coa_id;
        String empUrl = api_url_front+"hrm_dashboard/getEmployeeByDesigCoa?p_dep_id="+selected_dept_id+"&p_div_id="+selected_div_id+"&p_desig_id="+selected_desig_id+"&p_coa_id="+selected_coa_id;
        String attUrl = api_url_front+"hrm_dashboard/getAttendanceCountEmp?first_date="+first_date+"&last_date="+last_date+"&p_emp_id="+selected_emp_id;
        String attReportUrl = api_url_front+"hrm_dashboard/getAttReportData?first_date="+first_date+"&last_date="+last_date+"&emp_id="+selected_emp_id;
        String attStatUrl =api_url_front+"hrm_dashboard/getAttStatus?first_date="+first_date+"&last_date="+last_date+"&emp_id="+selected_emp_id;
        String workDaysUrl = api_url_front+"hrm_dashboard/getAttWorkingDays?first_date="+first_date+"&last_date="+last_date;
        String trackerFlagUrl = api_url_front + "utility/getTrackerFlag/" + selected_emp_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest trackerFlagReq = new StringRequest(Request.Method.GET, trackerFlagUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject trackerInfo = array.getJSONObject(i);
                        trackerAvailable = trackerInfo.getString("emp_timeline_tracker_flag")
                                .equals("null") ? "0" : trackerInfo.getString("emp_timeline_tracker_flag");
                    }
                }
                connected = true;
                updateInterface();
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

                        working_days = workDaysInfo.getString("work_days")
                                .equals("null") ? "0" : workDaysInfo.getString("work_days");
                    }
                }
                requestQueue.add(trackerFlagReq);
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

        StringRequest attStatReq = new StringRequest(Request.Method.GET, attStatUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attStatInfo = array.getJSONObject(i);

                        present_days = attStatInfo.getString("present_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_status");
                        absent_days = attStatInfo.getString("absent_status")
                                .equals("null") ? "0" : attStatInfo.getString("absent_status");
                        weekend = attStatInfo.getString("weekend_status")
                                .equals("null") ? "0" : attStatInfo.getString("weekend_status");
                        present_weekend = attStatInfo.getString("present_weekend")
                                .equals("null") ? "0" : attStatInfo.getString("present_weekend");
                        hol = attStatInfo.getString("holiday_status")
                                .equals("null") ? "0" : attStatInfo.getString("holiday_status");
                        present_holi = attStatInfo.getString("present_holiday_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_holiday_status");
                        leave_days = attStatInfo.getString("leave_status")
                                .equals("null") ? "0" : attStatInfo.getString("leave_status");
                        work_leave_days = attStatInfo.getString("present_leave_status")
                                .equals("null") ? "0" : attStatInfo.getString("present_leave_status");
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

        StringRequest attendanceReportReq = new StringRequest(Request.Method.GET, attReportUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attRepInfo = array.getJSONObject(i);

                        String dac_in_date_time = attRepInfo.getString("dac_in_date_time")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_date_time");

                        String dac_late_after = attRepInfo.getString("dac_late_after")
                                .equals("null") ? "" : attRepInfo.getString("dac_late_after");
                        String dac_early_before = attRepInfo.getString("dac_early_before")
                                .equals("null") ? "" : attRepInfo.getString("dac_early_before");

                        String dac_out_date_time = attRepInfo.getString("dac_out_date_time")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_date_time");

                        String dac_date1 = attRepInfo.getString("dac_date1")
                                .equals("null") ? "" : attRepInfo.getString("dac_date1");

                        String statusShort = attRepInfo.getString("dac_attn_status")
                                .equals("null") ? "" : attRepInfo.getString("dac_attn_status");

                        String lc_name = attRepInfo.getString("lc_name")
                                .equals("null") ? "" : attRepInfo.getString("lc_name");

                        String osm_name = attRepInfo.getString("osm_name")
                                .equals("null") ? "" : attRepInfo.getString("osm_name");
                        String coa_name = attRepInfo.getString("coa_name")
                                .equals("null") ? "" : attRepInfo.getString("coa_name");

                        String inCode = attRepInfo.getString("in_machine_coa_id")
                                .equals("null") ? "" : attRepInfo.getString("in_machine_coa_id");
                        String outCode = attRepInfo.getString("out_machine_coa_id")
                                .equals("null") ? "" : attRepInfo.getString("out_machine_coa_id");

                        String in_lat = attRepInfo.getString("dac_in_attd_latitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_attd_latitude");
                        String in_lon = attRepInfo.getString("dac_in_attd_longitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_in_attd_longitude");
                        String out_lat = attRepInfo.getString("dac_out_attd_latitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_attd_latitude");
                        String out_lon = attRepInfo.getString("dac_out_attd_longitude")
                                .equals("null") ? "" : attRepInfo.getString("dac_out_attd_longitude");

                        String status;
                        String attStatus;

                        if (!statusShort.isEmpty()) {
                            if (!dac_in_date_time.isEmpty() && dac_out_date_time.isEmpty()) {
                                status = "Out Miss";
                                attStatus = "Out Miss";
                            } else if (statusShort.equals("L") || statusShort.equals("LW") || statusShort.equals("LH")) {
                                status = lc_name;
                                if (status.isEmpty()) {
                                    status = "In Leave";
                                }
                                attStatus = "In Leave";
                            } else if (statusShort.equals("H")) {
                                status = "Holiday";
                                attStatus = "Off Day";
                            } else if (statusShort.equals("W")) {
                                status = "Weekend";
                                attStatus = "Off Day";
                            } else if (statusShort.equals("PL") || statusShort.equals("PLH") || statusShort.equals("PLW")) {
                                status = "Present & Leave";
                                attStatus = "Present on Leave Day";
                            } else if (statusShort.equals("PAT")) {
                                status = "Attended training";
                                attStatus = "White";
                            } else if (statusShort.equals("PHD") || statusShort.equals("PWD") || statusShort.equals("PLHD") || statusShort.equals("PLWD")) {
                                status = "Present & Day Off Taken";
                                attStatus = "White";
                            } else if (statusShort.equals("P") || statusShort.equals("PWWO") || statusShort.equals("PHWC") || statusShort.equals("PWWC") || statusShort.equals("PA")) {
                                status = "Present";
                                attStatus = "White";
                            } else if (statusShort.equals("PW") || statusShort.equals("PH")) {
                                status = "Present & Off Day";
                                attStatus = "Present on Off Day";
                            } else if (statusShort.equals("A")) {
                                status = "Absent";
                                attStatus = "Absent";
                            } else if (statusShort.equals("PT")) {
                                status = "Tour";
                                attStatus = "White";
                            } else if (statusShort.equals("SP")) {
                                status = "Suspend";
                                attStatus = "White";
                            } else {
                                status = "Absent";
                                attStatus = "Absent";
                            }
                        }
                        else {
                            status = "No Status";
                            attStatus = "White";
                        }

                        if (!inCode.isEmpty() && !outCode.isEmpty()) {
                            int in = Integer.parseInt(inCode);
                            int out = Integer.parseInt(outCode);
                            if (in != out ) {
                                attStatus = "Multi Station";
                            }
                        }

                        String shift = osm_name;

                        if (!lc_name.isEmpty()) {
                            shift = "";
                        }
                        if (status.equals("Weekend")) {
                            shift = "";
                        }

                        String inTime = dac_in_date_time;

                        Date in = null;
                        Date late = null;

                        String inStatus = "";

                        SimpleDateFormat tt = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
                        SimpleDateFormat newtt = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);

                        if (inTime.isEmpty()) {
                            inStatus = "";
                        }
                        else {
                            try {
                                in = tt.parse(inTime);
                                late = tt.parse(dac_late_after);

                            } catch (ParseException e) {
                                logger.log(Level.WARNING,e.getMessage(),e);
                            }

                            if (in != null && late != null) {
                                inTime = newtt.format(in);
                                if (late.after(in)) {
                                    inStatus = "";
                                } else {
                                    inStatus = "Late";
                                }
                            }
                        }

                        String outTime = dac_out_date_time;

                        String outStatus = "";

                        if (outTime.isEmpty()) {
                            outStatus = "";
                        }
                        else {
                            try {
                                in = tt.parse(outTime);
                                late = tt.parse(dac_early_before);

                            } catch (ParseException e) {
                                logger.log(Level.WARNING,e.getMessage(),e);
                            }

                            if (in != null && late != null) {
                                outTime = newtt.format(in);
                                if (late.after(in)) {
                                    outStatus = "Early";
                                } else {
                                    outStatus = "";
                                }
                            }
                        }

                        attendanceReportLists.add(new AttendanceReportList(dac_date1,status,shift,coa_name,
                                inTime,inStatus,outTime,outStatus,attStatus,in_lat,in_lon,out_lat,out_lon));
                    }
                }

                requestQueue.add(attStatReq);
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

                requestQueue.add(attendanceReportReq);
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

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,coa_name));
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

        StringRequest desigReq = new StringRequest(Request.Method.GET, desigUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" :info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" :info.getString("desig_name");

                        designationLists.add(new DesignationList(desig_id,desig_name));
                    }
                }
                requestQueue.add(empReq);
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

        StringRequest depReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                requestQueue.add(desigReq);
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

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                requestQueue.add(depReq);
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

        StringRequest brnReq = new StringRequest(Request.Method.GET, brnUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    branchLists.add(new BranchList("30","None"));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");

                        branchLists.add(new BranchList(coa_id,coa_name));
                    }
                }
                requestQueue.add(divReq);
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

        requestQueue.add(brnReq);

    }
}