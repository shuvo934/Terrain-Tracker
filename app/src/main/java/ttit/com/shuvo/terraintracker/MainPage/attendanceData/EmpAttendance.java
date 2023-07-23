package ttit.com.shuvo.terraintracker.MainPage.attendanceData;

import static ttit.com.shuvo.terraintracker.OracleConnection.createConnection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ttit.com.shuvo.terraintracker.MainPage.HomePage;
import ttit.com.shuvo.terraintracker.MainPage.adapters.AttenReportAdapter;
import ttit.com.shuvo.terraintracker.MainPage.lists.AttenReportList;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.progressBar.WaitProgress;

public class EmpAttendance extends AppCompatActivity {

    PieChart pieChart;
    TextView refresh;

    String beginDate = "";
    String lastDate = "";

    String absent = "";
    String present = "";
    String leave = "";
    String holiday = "";
    String late = "";
    String early = "";
    ArrayList<PieEntry> NoOfEmp;

    CardView report;
    RecyclerView reportview;
    RecyclerView.LayoutManager layoutManager;
    AttenReportAdapter attenReportAdapter;

    TextView dateToDate;

    TextView calenderDays;
    TextView totalWorking;
    TextView presentDays;
    TextView absentDays;
    TextView weeklyHolidays;
    TextView holidays;
    TextView workWeekend;
    TextView workHolidays;
    TextView name;
    TextView desig;

    LinearLayout attenData;
    LinearLayout attenDataNot;
    public static ArrayList<AttenReportList> attenReportLists;
    int daysInMonth = 0;
    String present_days = "";
    String absent_days = "";
    String weekend = "";
    String present_weekend = "";
    String hol = "";
    String present_holi = "";
    String coa_id = "";
    String working_days = "";
    String emp_id = "";
    String empName = "";
    String empTitle = "";
    WaitProgress waitProgress = new WaitProgress();
    private Connection connection;
    private Boolean conn = false;
    private Boolean connected = false;
    LinearLayout attendanceLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_attendance);

        pieChart = findViewById(R.id.piechart_attendance);
        refresh = findViewById(R.id.refresh_graph_attendance);

        dateToDate = findViewById(R.id.date_from_report);

        attenData = findViewById(R.id.attendancebefore_text);
        attenDataNot = findViewById(R.id.no_data_msg_attendance);

        report = findViewById(R.id.report_card);

        reportview = findViewById(R.id.attnd_list_view);

        calenderDays = findViewById(R.id.days_in_month);
        totalWorking = findViewById(R.id.working_days_in_month);
        presentDays = findViewById(R.id.present_days_in_month);
        absentDays = findViewById(R.id.absent_days_in_month);
        weeklyHolidays = findViewById(R.id.weekly_holidays_days_in_month);
        holidays = findViewById(R.id.holidays_days_in_month);
        workWeekend = findViewById(R.id.work_weekend_days_in_month);
        workHolidays = findViewById(R.id.work_on_holi_days_in_month);
        name = findViewById(R.id.emp_name_attendance);
        desig = findViewById(R.id.designation_attendance);
        attendanceLayout = findViewById(R.id.attendance_details_layout);


        Intent intent = getIntent();
        emp_id = intent.getStringExtra("EMP_ID");
        empName = intent.getStringExtra("EMP_NAME");
        empTitle = intent.getStringExtra("TITLE");

        name.setText(empName);
        desig.setText(empTitle);

        attenReportLists = new ArrayList<>();

        reportview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        reportview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(reportview.getContext(),DividerItemDecoration.VERTICAL);
        reportview.addItemDecoration(dividerItemDecoration);

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());

        lastDate = df.format(c);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-yy", Locale.getDefault());

        beginDate = sdf.format(c);
        beginDate = "01-"+beginDate;

        SimpleDateFormat mon = new SimpleDateFormat("MMMM",Locale.getDefault());

        String mmm = mon.format(c);

        refresh.setText("Month: "+mmm);

        SimpleDateFormat monthNumb = new SimpleDateFormat("MM",Locale.getDefault());
        String monthNNN = monthNumb.format(c);


        SimpleDateFormat presentYear = new SimpleDateFormat("yyyy",Locale.getDefault());
        String yyyy = presentYear.format(c);

        YearMonth yearMonthObject = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(Integer.parseInt(yyyy), Integer.parseInt(monthNNN));
            daysInMonth = yearMonthObject.lengthOfMonth();
            System.out.println(daysInMonth);
        }

        NoOfEmp = new ArrayList<>();

        pieChart.setCenterText("Attendance");
        pieChart.setDrawEntryLabels(true);
        pieChart.setCenterTextSize(14);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleRadius(40);

        pieChart.setEntryLabelTextSize(11);
        pieChart.setEntryLabelColor(Color.DKGRAY);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setXOffset(20);
        l.setTextSize(12);
        //l.setYOffset(50);
        l.setWordWrapEnabled(false);
        l.setDrawInside(false);
        l.setYOffset(5f);

        pieChart.animateXY(1000, 1000);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date c = Calendar.getInstance().getTime();

                String formattedYear = "";
                String monthValue = "";
                String lastformattedYear = "";
                String lastdateView = "";

                SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("MM",Locale.getDefault());

                formattedYear = df.format(c);
                monthValue = sdf.format(c);
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

                Date today = new Date();

                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(today);

                calendar1.add(Calendar.MONTH, 1);
                calendar1.set(Calendar.DAY_OF_MONTH, 1);
                calendar1.add(Calendar.DATE, -1);

                Date lastDayOfMonth = calendar1.getTime();

                SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.getDefault());
                lastdateView = sdff.format(lastDayOfMonth);

                int yearSelected;
                int monthSelected;
                MonthFormat monthFormat = MonthFormat.LONG;
                String customTitle = "Select Month";
// Use the calendar for create ranges
                Calendar calendar = Calendar.getInstance();
                yearSelected = calendar.get(Calendar.YEAR);
                monthSelected = calendar.get(Calendar.MONTH);
                calendar.clear();
                calendar.set(Integer.parseInt(lastformattedYear), lastMonNumb, 1); // Set minimum date to show in dialog
                long minDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

                calendar.clear();
                calendar.set(Integer.parseInt(formattedYear), nowMonNumb, Integer.parseInt(lastdateView)); // Set maximum date to show in dialog
                long maxDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

// Create instance with date ranges values
                MonthYearPickerDialogFragment dialogFragment =  MonthYearPickerDialogFragment
                        .getInstance(monthSelected, yearSelected, minDate, maxDate, customTitle, monthFormat);



                dialogFragment.show(getSupportFragmentManager(), null);

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {
                        System.out.println(year);
                        System.out.println(monthOfYear);

                        int month = monthOfYear + 1;
                        String monthName = "";
                        String mon = "";
                        String yearName = "";

                        if (month == 1) {
                            monthName = "JANUARY";
                            mon = "JAN";
                        } else if (month == 2) {
                            monthName = "FEBRUARY";
                            mon = "FEB";
                        } else if (month == 3) {
                            monthName = "MARCH";
                            mon = "MAR";
                        } else if (month == 4) {
                            monthName = "APRIL";
                            mon = "APR";
                        } else if (month == 5) {
                            monthName = "MAY";
                            mon = "MAY";
                        } else if (month == 6) {
                            monthName = "JUNE";
                            mon = "JUN";
                        } else if (month == 7) {
                            monthName = "JULY";
                            mon = "JUL";
                        } else if (month == 8) {
                            monthName = "AUGUST";
                            mon = "AUG";
                        } else if (month == 9) {
                            monthName = "SEPTEMBER";
                            mon = "SEP";
                        } else if (month == 10) {
                            monthName = "OCTOBER";
                            mon = "OCT";
                        } else if (month == 11) {
                            monthName = "NOVEMBER";
                            mon = "NOV";
                        } else if (month == 12) {
                            monthName = "DECEMBER";
                            mon = "DEC";
                        }

                        yearName  = String.valueOf(year);
                        yearName = yearName.substring(yearName.length()-2);
                        String year_full = String.valueOf(year);


                        beginDate = "01-"+mon+"-"+yearName;
                        //selected_date = "01-"+mon+"-"+yearName;
                        refresh.setText("Month: "+ monthName);

                        SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

                        Date today = null;
                        try {
                            today = sss.parse(beginDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Calendar calendar1 = Calendar.getInstance();
                        if (today != null) {
                            calendar1.setTime(today);
                            calendar1.add(Calendar.MONTH, 1);
                            calendar1.set(Calendar.DAY_OF_MONTH, 1);
                            calendar1.add(Calendar.DATE, -1);

                            Date lastDayOfMonth = calendar1.getTime();

                            SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.getDefault());
                            String llll = sdff.format(lastDayOfMonth);
                            lastDate =  llll+ "-" + mon +"-"+ yearName;
                        }
                        YearMonth yearMonthObject = null;

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            yearMonthObject = YearMonth.of(Integer.parseInt(year_full), month);
                            daysInMonth = yearMonthObject.lengthOfMonth();
                            System.out.println(daysInMonth);
                        }


                        new AttendanceCheck().execute();
                    }
                });

            }
        });

        new AttendanceCheck().execute();
    }

    public boolean isConnected() {
        boolean connected = false;
        boolean isMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }

    public class AttendanceCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            waitProgress.show(getSupportFragmentManager(),"WaitBar");
            waitProgress.setCancelable(false);
            attendanceLayout.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isConnected() && isOnline()) {

                AttendanceGraph();
                if (connected) {
                    conn = true;
                }

            } else {
                conn = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            waitProgress.dismiss();
            attendanceLayout.setVisibility(View.VISIBLE);
            if (conn) {


                final int[] piecolors = new int[]{


                        Color.rgb(116, 185, 255),
                        Color.rgb(85, 239, 196),
                        Color.rgb(162, 155, 254),
                        Color.rgb(223, 230, 233),
                        Color.rgb(255, 234, 167),

                        Color.rgb(250, 177, 160),
                        Color.rgb(129, 236, 236),
                        Color.rgb(255, 118, 117),
                        Color.rgb(253, 121, 168),
                        Color.rgb(96, 163, 188)};


                if (absent != null) {
                    if (!absent.isEmpty()) {
                        if (!absent.equals("0")) {
                            NoOfEmp.add(new PieEntry(Float.parseFloat(absent), "Absent", 0));
                        }
                    }
                }

                if (present != null) {
                    if (!present.isEmpty()) {
                        if (!present.equals("0")) {
                            NoOfEmp.add(new PieEntry(Float.parseFloat(present),"Present", 1));
                        }
                    }
                }

                if (late != null) {
                    if (!late.isEmpty()) {
                        if (!late.equals("0")) {
                            NoOfEmp.add(new PieEntry(Float.parseFloat(late),"Late", 2));

                        }
                    }
                }

                if (early != null) {
                    if (!early.isEmpty()) {
                        if (!early.equals("0")) {
                            NoOfEmp.add(new PieEntry(Float.parseFloat(early),"Early", 3));
                        }
                    }
                }

                if (leave != null) {
                    if (!leave.isEmpty()) {
                        if (!leave.equals("0")) {
                            NoOfEmp.add(new PieEntry(Float.parseFloat(leave),"Leave", 4));
                        }
                    }
                }

                if (holiday != null) {
                    if (!holiday.isEmpty()) {
                        if (!holiday.equals("0")) {
                            NoOfEmp.add(new PieEntry(Float.parseFloat(holiday),"Holiday/Weekend", 5));
                        }
                    }
                }


                if (NoOfEmp.size() == 0) {
                    NoOfEmp.add(new PieEntry(1,"No Data Found", 6));

                }


                PieDataSet dataSet = new PieDataSet(NoOfEmp, "");
                pieChart.animateXY(1000, 1000);
                pieChart.setEntryLabelColor(Color.TRANSPARENT);

                //pieChart.setExtraRightOffset(50);



                PieData data = new PieData(dataSet);
//                dataSet.setValueFormatter(new PercentFormatter(pieChart));
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });
                String label = dataSet.getValues().get(0).getLabel();
                System.out.println(label);
                if (label.equals("No Data Found")) {
                    dataSet.setValueTextColor(Color.TRANSPARENT);
                } else {
                    dataSet.setValueTextColor(Color.BLACK);
                }
                dataSet.setHighlightEnabled(true);
                dataSet.setValueTextSize(12);

                int[] num = new int[NoOfEmp.size()];
                for (int i = 0; i < NoOfEmp.size(); i++) {
                    int neki = (int) NoOfEmp.get(i).getData();
                    num[i] = piecolors[neki];
                }

                dataSet.setColors(ColorTemplate.createColors(num));


//                pieChart.setUsePercentValues(true);


                pieChart.setData(data);
                pieChart.invalidate();

                report.setVisibility(View.VISIBLE);
                attenReportAdapter = new AttenReportAdapter(attenReportLists, EmpAttendance.this);
                reportview.setAdapter(attenReportAdapter);
                //setListViewHeightBasedOnChildren(reportview);
//                int originalItemSize = attenReportLists.size();
//
//                ViewGroup.LayoutParams params = reportview.getLayoutParams();
//
//                if (attenReportLists.size() > originalItemSize ){
//                    params.height = params.height + 100;
//                    originalItemSize  = attenReportLists.size();
//                    reportview.setLayoutParams(params);
//                }

                if (attenReportLists.size() == 0) {
                    attenDataNot.setVisibility(View.VISIBLE);
                    attenData.setVisibility(View.GONE);
                } else {
                    attenData.setVisibility(View.VISIBLE);
                    attenDataNot.setVisibility(View.GONE);
                }

                dateToDate.setText(beginDate + " to "+ lastDate);
                calenderDays.setText(String.valueOf(daysInMonth));
                totalWorking.setText(working_days);
                presentDays.setText(present_days);
                absentDays.setText(absent_days);
                weeklyHolidays.setText(weekend);
                holidays.setText(hol);
                workWeekend.setText(present_weekend);
                workHolidays.setText(present_holi);

                conn = false;
                connected = false;

            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(EmpAttendance.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AttendanceCheck().execute();
                        dialog.dismiss();
                    }
                });

                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        }
    }

    public void AttendanceGraph() {
        try {
            this.connection = createConnection();
            //    Toast.makeText(MainActivity.this, "Connected",Toast.LENGTH_SHORT).show();

            NoOfEmp = new ArrayList<>();
            absent = "";
            present = "";
            leave = "";
            holiday = "";
            late = "";
            early = "";

//             beginDate = "01-JUN-21";
//             lastDate = "30-JUN-21";
            Statement stmt = connection.createStatement();


            ResultSet rs=stmt.executeQuery("SELECT ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'A') ABSENT,\n" +
                    "       (  ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'P')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'PW')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'PH')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'PL')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'PLH')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'PA')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'PLW'))\n" +
                    "          PRESENT,\n" +
                    "       (  ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'L')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'LW')\n" +
                    "        + ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'LH'))\n" +
                    "          LEAVE,\n" +
                    "       ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'H') +\n" +
                    "       ATTD_HOLIDAY_COUNT ("+emp_id+", '"+ lastDate +"', 'W') HOLIDAY_WEEKEND\n" +
                    "  FROM DUAL");


            while(rs.next()) {

                absent = rs.getString(1);
                present = rs.getString(2);
                leave = rs.getString(3);
                holiday = rs.getString(4);


            }
            rs.close();

            ResultSet resultSet = stmt.executeQuery("SELECT LATE_COUNT_NEW\n" +
                    "("+emp_id+",\n" +
                    " '"+beginDate+"',\n" +
                    " '"+ lastDate +"')\n" +
                    "  FROM DUAL");

            while (resultSet.next()) {
                late = resultSet.getString(1);
            }
            resultSet.close();

            ResultSet resultSet1 = stmt.executeQuery("  SELECT GET_EARLY_COUNT \n" +
                    "("+emp_id+",\n" +
                    " '"+beginDate+"',\n" +
                    " '"+ lastDate +"')\n" +
                    "  FROM DUAL");

            while (resultSet1.next()) {
                early = resultSet1.getString(1);
            }
            resultSet1.close();

            attenReportLists = new ArrayList<>();

            present_days = "";
            absent_days = "";
            weekend = "";
            present_weekend = "";
            hol = "";
            present_holi = "";
            coa_id = "";
            working_days = "";


            ResultSet resultSet3=stmt.executeQuery("SELECT DISTINCT TO_CHAR(DA_CHECK.DAC_IN_DATE_TIME,'HH:MI:SS AM') DAC_IN_DATE_TIME, \n" +
                    "TO_CHAR(DA_CHECK.DAC_LATE_AFTER,'HH:MI:SS AM') DAC_LATE_AFTER, \n" +
                    "TO_CHAR(DA_CHECK.DAC_EARLY_BEFORE,'HH:MI:SS AM') DAC_EARLY_BEFORE, \n" +
                    "DA_CHECK.DAC_END_TIME, \n" +
                    "TO_CHAR(DA_CHECK.DAC_OUT_DATE_TIME,'HH:MI:SS AM') DAC_OUT_DATE_TIME, \n" +
                    "TO_CHAR(DA_CHECK.DAC_DATE,'DD-MON-YY') DAC_DATE1 , DA_CHECK.DAC_ATTN_STATUS, \n" +
                    "TO_DATE(TO_CHAR(DA_CHECK.DAC_DATE, 'MONTH RRRR'), 'MONTH RRRR') MONTH_YEAR, \n" +
                    "DA_CHECK.DAC_OVERTIME_AVAIL_FLAG, LEAVE_CATEGORY.LC_NAME, DA_CHECK.DAC_NOTES, \n" +
                    "OFFICE_SHIFT_MST.OSM_NAME, COMPANY_OFFICE_ADDRESS_A2.COA_NAME, \n" +
                    "DA_CHECK.DAC_AMS_MECHINE_CODE, COMPANY_OFFICE_ADDRESS.COA_ID,NVL(DA_CHECK.DAC_LATE_FLAG,0) DAC_LATE_FLAG,DAC_LEAVE_CONSUM_LC_ID, DAC_LEAVE_TYPE,DA_CHECK.DAC_DATE, ATTENDANCE_MECHINE_SETUP.AMS_COA_ID in_machine_coa_ID, out_machine.AMS_COA_ID out_machine_coa_id,\n" +
                    "DA_CHECK.DAC_IN_ATTD_LATITUDE,DA_CHECK.DAC_IN_ATTD_LONGITUDE, DA_CHECK.DAC_OUT_ATTD_LATITUDE,DA_CHECK.DAC_OUT_ATTD_LONGITUDE,ELR_ID\n" +
                    "FROM EMP_MST,EMP_LOCATION_RECORD, JOB_SETUP_DTL, JOB_SETUP_MST, DEPT_MST, DIVISION_MST, EMP_JOB_HISTORY, EMP_ADOPTED_HISTORY, DESIG_MST, COMPANY_OFFICE_ADDRESS, COMPANY_OFFICE_ADDRESS COMPANY_OFFICE_ADDRESS_A1, DA_CHECK, LEAVE_CATEGORY, OFFICE_SHIFT_MST, ATTENDANCE_MECHINE_SETUP,ATTENDANCE_MECHINE_SETUP out_machine, COMPANY_OFFICE_ADDRESS COMPANY_OFFICE_ADDRESS_A2\n" +
                    "WHERE ((JOB_SETUP_DTL.JSD_JSM_ID = JOB_SETUP_MST.JSM_ID)\n" +
                    "AND (DA_CHECK.DAC_EMP_ID = EMP_LOCATION_RECORD.ELR_EMP_ID(+)\n" +
                    "AND DA_CHECK.DAC_DATE = EMP_LOCATION_RECORD.ELR_DATE(+))\n" +
                    " AND (JOB_SETUP_MST.JSM_DEPT_ID = DEPT_MST.DEPT_ID)\n" +
                    " AND (JOB_SETUP_MST.JSM_DIVM_ID = DIVISION_MST.DIVM_ID)\n" +
                    " AND (EMP_ADOPTED_HISTORY.EAH_JOB_ID(+) = EMP_JOB_HISTORY.JOB_ID)\n" +
                    " AND (JOB_SETUP_MST.JSM_DESIG_ID = DESIG_MST.DESIG_ID)\n" +
                    " AND (EMP_JOB_HISTORY.JOB_PRI_COA_ID = COMPANY_OFFICE_ADDRESS.COA_ID)\n" +
                    " AND (JOB_SETUP_DTL.JSD_ID = EMP_MST.EMP_JSD_ID)\n" +
                    " AND (EMP_JOB_HISTORY.JOB_ID = EMP_MST.EMP_JOB_ID)\n" +
                    " AND (COMPANY_OFFICE_ADDRESS_A1.COA_ID(+) = EMP_JOB_HISTORY.JOB_SEC_COA_ID)\n" +
                    " AND (DA_CHECK.DAC_EMP_ID = EMP_MST.EMP_ID)\n" +
                    " AND (DA_CHECK.DAC_LC_ID = LEAVE_CATEGORY.LC_ID(+))\n" +
                    " AND (DA_CHECK.DAC_OSM_ID = OFFICE_SHIFT_MST.OSM_ID(+))\n" +
                    " AND (DA_CHECK.DAC_AMS_MECHINE_CODE = ATTENDANCE_MECHINE_SETUP.AMS_MECHINE_CODE(+))\n" +
                    " AND (DA_CHECK.DAC_OUT_MACHINE_CODE = out_machine.AMS_MECHINE_CODE(+))\n" +
                    " AND (ATTENDANCE_MECHINE_SETUP.AMS_COA_ID = COMPANY_OFFICE_ADDRESS_A2.COA_ID(+)))\n" +
                    " AND DA_CHECK.DAC_DATE BETWEEN '"+beginDate+"' AND '"+lastDate+"'\n" +
                    "AND EMP_MST.EMP_ID = "+emp_id+"\n" +
                    "ORDER BY DA_CHECK.DAC_DATE");



            while(resultSet3.next()) {

                String date = resultSet3.getString(6);
                String elr_id = resultSet3.getString(26);
                String statusShort = resultSet3.getString(7);
                String status = "";
                String attStatus = "";
                String inCode = resultSet3.getString(20);
                String outCode = resultSet3.getString(21);
                String in_lat = resultSet3.getString(22);
                String in_lon = resultSet3.getString(23);
                String out_lat = resultSet3.getString(24);
                String out_lon = resultSet3.getString(25);

                if (in_lat == null) {
                    in_lat = "";
                }
                if (in_lon == null) {
                    in_lon = "";
                }
                if (out_lat == null) {
                    out_lat = "";
                }
                if (out_lon == null) {
                    out_lon = "";
                }

                if (statusShort != null) {
                    if (resultSet3.getString(1) != null && resultSet3.getString(5) == null) {
                        status = "Out Miss";
                        attStatus = "Out Miss";
                    }else if (statusShort.equals("L") || statusShort.equals("LW") || statusShort.equals("LH")) {
                        status = resultSet3.getString(10);
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
                } else {
                    status = "No Status";
                    attStatus = "White";
                }



                if (inCode != null && outCode != null) {
                    int in = Integer.parseInt(inCode);
                    int out = Integer.parseInt(outCode);
                    if (in != out ) {
                        attStatus = "Multi Station";
                    }
                }

                String shift = resultSet3.getString(12);

                if (shift == null) {
                    shift = "";
                }
                if (resultSet3.getString(10) != null) {
                    shift = "";
                }
                if (status.equals("Weekend")) {
                    shift = "";
                }

                String pl = resultSet3.getString(13);
                if (pl == null) {
                    pl = "";
                }

                String inTime = resultSet3.getString(1);
                String lateAfter = resultSet3.getString(2);

                Date in = null;
                Date late = null;

                String inStatus = "";

                SimpleDateFormat tt = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
                SimpleDateFormat newtt = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                if (inTime == null) {
                    inTime = "";
                    inStatus = "";
                }
                else {
                    try {
                        in = tt.parse(inTime);
                        late = tt.parse(lateAfter);

                    } catch (ParseException e) {
                        e.printStackTrace();
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


                String outTime = resultSet3.getString(5);
                String earlyB = resultSet3.getString(3);

                String outStatus = "";

                if (outTime == null) {
                    outTime = "";
                    outStatus = "";
                } else {
                    try {
                        in = tt.parse(outTime);
                        late = tt.parse(earlyB);

                    } catch (ParseException e) {
                        e.printStackTrace();
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

                Blob blob = null;

                if (elr_id != null) {
                    PreparedStatement ps = connection.prepareStatement("Select ELR_FILE_NAME, ELR_FILETYPE , ELR_LOCATION_FILE from EMP_LOCATION_RECORD where ELR_ID = "+elr_id+"");
                    ResultSet resultSetBlob = ps.executeQuery();

                    while (resultSetBlob.next()) {
                        blob = resultSetBlob.getBlob(3);
                    }
                } else {
                    blob = null;
                }

                if (blob != null) {
                    System.out.println("Ei "+ date+ " e BLOB ase");
                } else if (blob == null){
                    System.out.println("Ei "+ date+ " e BLOB Null");
                }

                SimpleDateFormat sdff = new SimpleDateFormat("dd-MMM-yy",Locale.getDefault());
                SimpleDateFormat ddddd = new SimpleDateFormat("EEEE",Locale.getDefault());
                String dayName = "";
                Date today = null;
                try {
                    today = sdff.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (today != null) {
                    dayName = ddddd.format(today);
                }
                System.out.println(date+": "+dayName);


                attenReportLists.add(new AttenReportList(date,status,shift,pl,inTime,inStatus,outTime,outStatus,attStatus,in_lat,in_lon,out_lat,out_lon,blob,elr_id,dayName));

            }
            resultSet3.close();

            ResultSet resultSet4 = stmt.executeQuery("SELECT SUM (CASE WHEN DAC_ATTN_STATUS IN ('P', 'PA') THEN 1 ELSE 0 END)\n" +
                    "          PRESENT_STATUS,\n" +
                    "       SUM (CASE WHEN DAC_ATTN_STATUS IN ('A') THEN 1 ELSE 0 END)\n" +
                    "          ABSENT_STATUS,\n" +
                    "       SUM (CASE WHEN DAC_ATTN_STATUS IN ('W') THEN 1 ELSE 0 END)\n" +
                    "          WEEKEND_STATUS,\n" +
                    "       SUM (CASE WHEN DAC_ATTN_STATUS IN ('PW') THEN 1 ELSE 0 END)\n" +
                    "          PRESENT_WEEKEND,\n" +
                    "       SUM (CASE WHEN DAC_ATTN_STATUS IN ('H') THEN 1 ELSE 0 END)\n" +
                    "          HOLIDAY_STATUS,\n" +
                    "       SUM (CASE WHEN DAC_ATTN_STATUS IN ('PH') THEN 1 ELSE 0 END)\n" +
                    "          PRESENT_HOLIDAY_STATUS\n" +
                    "  FROM DA_CHECK, EMP_MST\n" +
                    " WHERE     DA_CHECK.DAC_EMP_ID = EMP_MST.EMP_ID\n" +
                    "       AND TRUNC (DA_CHECK.DAC_DATE) BETWEEN TRUNC (TO_DATE ('"+beginDate+"'))\n" +
                    "                                         AND TO_DATE ('"+lastDate+"')\n" +
                    "       AND EMP_MST.EMP_ID = "+emp_id+"");

            while (resultSet4.next()) {
                present_days = resultSet4.getString(1);
                absent_days = resultSet4.getString(2);
                weekend = resultSet4.getString(3);
                present_weekend = resultSet4.getString(4);
                hol = resultSet4.getString(5);
                present_holi = resultSet4.getString(6);
            }
            resultSet4.close();

            ResultSet set = stmt.executeQuery("SELECT DISTINCT \n" +
                    "COMPANY_OFFICE_ADDRESS.COA_ID\n" +
                    "FROM EMP_MST, EMP_JOB_HISTORY, COMPANY_OFFICE_ADDRESS\n" +
                    "WHERE EMP_MST.EMP_ID = "+emp_id+"\n" +
                    "AND (EMP_JOB_HISTORY.JOB_PRI_COA_ID = COMPANY_OFFICE_ADDRESS.COA_ID)\n" +
                    "AND (EMP_JOB_HISTORY.JOB_ID = EMP_MST.EMP_JOB_ID)");

            while (set.next()) {
                coa_id = set.getString(1);
            }
            set.close();

            ResultSet resultSet5 = stmt.executeQuery("SELECT COUNT (HOC_DATE)\n" +
                    "  FROM HRM_OFFICIAL_CALENDAR\n" +
                    " WHERE     HOC_DATE BETWEEN '"+beginDate+"' AND '"+lastDate+"'\n" +
                    "       AND HOC_DAY_CAT = 1\n" +
                    "       AND HOC_COA_ID = '"+coa_id+"'");

            while (resultSet5.next()) {
                working_days = resultSet5.getString(1);
            }

            stmt.close();

            connected = true;

            connection.close();

        }
        catch (Exception e) {

            //   Toast.makeText(MainActivity.this, ""+e,Toast.LENGTH_LONG).show();
            Log.i("ERRRRR", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}