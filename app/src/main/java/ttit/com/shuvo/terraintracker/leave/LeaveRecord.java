package ttit.com.shuvo.terraintracker.leave;

import static ttit.com.shuvo.terraintracker.Constants.api_url_front;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.araylists.BranchList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DepartmentList;
import ttit.com.shuvo.terraintracker.attendance.araylists.DivisionList;
import ttit.com.shuvo.terraintracker.attendance.araylists.EmployeeList;
import ttit.com.shuvo.terraintracker.leave.adapters.LeaveRecordAdapter;
import ttit.com.shuvo.terraintracker.leave.arraylists.LeaveRecList;

public class LeaveRecord extends AppCompatActivity {

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
    ArrayList<DepartmentList> filteredDepartmentLists;

    TextInputLayout employeeSelectLay;
    AppCompatAutoCompleteTextView employeeSelect;
    ArrayList<EmployeeList> employeeLists;
    ArrayList<EmployeeList> filteredEmpLists;

    CardView monthCard;
    TextView selectedMonth;

    RecyclerView lvRecListView;
    RecyclerView.LayoutManager layoutManager;
    LeaveRecordAdapter leaveRecordAdapter;

    ArrayList<LeaveRecList> leaveRecLists;
    ArrayList<LeaveRecList> filteredList;

    TextView allLeaveStatus;
    Spanned all_leave_stat;

    TextView noDa;

    String branch_id = "";
    String first_date = "";
    String last_date = "";
    String selected_div_id = "";
    String selected_dept_id = "";
    String selected_emp_id = "";

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    Logger logger = Logger.getLogger(LeaveRecord.class.getName());

    PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_record);

        fullLayout = findViewById(R.id.leave_record_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_leave_record);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_leave_record);

        branchSelect = findViewById(R.id.branch_selection_for_lv_rec);
        branchName = findViewById(R.id.selected_branch_name_lv_rec);
        branchLists = new ArrayList<>();

        popupMenu = new PopupMenu(LeaveRecord.this, branchSelect);

        divisionSelect = findViewById(R.id.division_select_for_lv_rec);
        divisionLists = new ArrayList<>();

        departmentSelectLay = findViewById(R.id.spinner_layout_department_select_for_lv_rec);
        departmentSelectLay.setEnabled(false);
        departmentSelect = findViewById(R.id.department_select_for_lv_rec);
        departmentLists = new ArrayList<>();
        filteredDepartmentLists = new ArrayList<>();

        employeeSelectLay = findViewById(R.id.spinner_layout_emp_select_for_lv_rec);
        employeeSelectLay.setEnabled(false);
        employeeSelect = findViewById(R.id.emp_select_for_lv_rec);
        employeeLists = new ArrayList<>();
        filteredEmpLists = new ArrayList<>();

        monthCard = findViewById(R.id.month_selection_card_view_lv_rec);
        selectedMonth = findViewById(R.id.selected_month_name_lv_rec);

        lvRecListView = findViewById(R.id.leave_record_recycle_view);
        noDa = findViewById(R.id.no_leave_found_message_lv_rec);
        noDa.setVisibility(View.GONE);

        allLeaveStatus = findViewById(R.id.all_leave_status_emp_lv_rec);

        leaveRecLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        lvRecListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        lvRecListView.setLayoutManager(layoutManager);

        getCurrentDateMonthYear();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            selected_div_id = "";
            divisionSelect.setText("");
            divisionLists = new ArrayList<>();

            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);
            filteredDepartmentLists = new ArrayList<>();

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();

            filteredList = new ArrayList<>();

            String branch_name = "";
            for (int i = 0; i <branchLists.size(); i++) {
                if (String.valueOf(menuItem.getItemId()).equals(branchLists.get(i).getId())) {

                    branch_id = branchLists.get(i).getId();
                    branch_name = branchLists.get(i).getName();
                }
            }

            branchName.setText(branch_name);

            if (branch_id.equals("30")) {
                branch_id = "";
                getAllData();
            }
            else {
                getBranchWiseAllData();
            }
            return false;
        });

        branchSelect.setOnClickListener(view -> popupMenu.show());

        divisionSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);
            filteredDepartmentLists = new ArrayList<>();

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();

            filteredList = new ArrayList<>();

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

            filterAll();

        });

        departmentSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();


            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <filteredDepartmentLists.size(); i++) {
                if (name.equals(filteredDepartmentLists.get(i).getDept_name())) {

                    selected_dept_id = filteredDepartmentLists.get(i).getDept_id();
                }
            }
            if (selected_dept_id.isEmpty()) {
                departmentSelect.setText("");
            }

            filterAll();

        });

        employeeSelect.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            for (int i = 0; i <filteredEmpLists.size(); i++) {
                if (name.equals(filteredEmpLists.get(i).getEmp_name())) {

                    selected_emp_id = filteredEmpLists.get(i).getEmp_id();
                }
            }

            if (selected_emp_id.isEmpty()) {
                employeeSelect.setText("");
            }

            filterAll();
        });

        monthCard.setOnClickListener(v -> {

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
            int lastMonNumb = nowMonNumb - 3;

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

                SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);

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
                    String mo_name = dateFormat.format(today11).toUpperCase(Locale.ENGLISH);
                    selectedMonth.setText(mo_name);
                }

                if (branch_id.isEmpty()) {
                    getLeaveData();
                }
                else {
                    getBranchWiseLeaveData();
                }
            });

        });

        allLeaveStatus.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Leave Status")
                    .setMessage(all_leave_stat)
                    .setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(LeaveRecord.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(LeaveRecord.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getAllData();
    }

    private void getCurrentDateMonthYear() {
        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);

        first_date = simpleDateFormat.format(dd).toUpperCase(Locale.ENGLISH);
        first_date = "01-"+first_date;

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
        }

        String mo_name = dateFormat.format(dd).toUpperCase(Locale.ENGLISH);
        selectedMonth.setText(mo_name);
    }

    public void filterAll() {
        if (!selected_div_id.isEmpty()) {
            if (!selected_dept_id.isEmpty()) {
                if (!selected_emp_id.isEmpty()) {
                    filteredList = new ArrayList<>();

                    for (int i = 0; i < leaveRecLists.size(); i++) {
                        if (selected_div_id.equals(leaveRecLists.get(i).getJsm_divm_id())
                                && selected_dept_id.equals(leaveRecLists.get(i).getJsm_dept_id())
                                && selected_emp_id.equals(leaveRecLists.get(i).getEmp_id())) {

                            String la_id = leaveRecLists.get(i).getLa_id();
                            String la_app_code = leaveRecLists.get(i).getLa_app_code();
                            String la_approved = leaveRecLists.get(i).getLa_approved();
                            String la_date = leaveRecLists.get(i).getLa_date();
                            String leave_type = leaveRecLists.get(i).getLeave_type();
                            String la_from_date = leaveRecLists.get(i).getLa_from_date();
                            String la_to_date = leaveRecLists.get(i).getLa_to_date();
                            String la_leave_days = leaveRecLists.get(i).getLa_leave_days();
                            String emp_name = leaveRecLists.get(i).getEmp_name();
                            String emp_id = leaveRecLists.get(i).getEmp_id();
                            String job_calling_title = leaveRecLists.get(i).getJob_calling_title();
                            String jsm_divm_id = leaveRecLists.get(i).getJsm_divm_id();
                            String jsm_dept_id = leaveRecLists.get(i).getJsm_dept_id();
                            String jsm_desig_id = leaveRecLists.get(i).getJsm_desig_id();
                            String coa_name = leaveRecLists.get(i).getCoa_name();
                            String coa_id = leaveRecLists.get(i).getCoa_id();

                            filteredList.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                    la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
                        }
                    }

                }
                else {
                    selected_emp_id = "";
                    employeeSelect.setText("");
                    employeeSelectLay.setEnabled(true);
                    filteredEmpLists = new ArrayList<>();

                    filteredList = new ArrayList<>();

                    filteredEmpLists.add(new EmployeeList("","","...","","","","",""));
                    for (int i = 0; i < employeeLists.size(); i++) {
                        if (selected_div_id.equals(employeeLists.get(i).getJsm_divm_id()) && selected_dept_id.equals(employeeLists.get(i).getJsm_dept_id())) {
                            String emp_id = employeeLists.get(i).getEmp_id();
                            String emp_code = employeeLists.get(i).getEmp_code();
                            String emp_name = employeeLists.get(i).getEmp_name();
                            String jsm_divm_id = employeeLists.get(i).getJsm_divm_id();
                            String jsm_dept_id = employeeLists.get(i).getJsm_dept_id();
                            String jsm_desig_id = employeeLists.get(i).getJsm_desig_id();
                            String job_calling_title = employeeLists.get(i).getJob_calling_title();

                            filteredEmpLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,""));
                        }
                    }

                    ArrayList<String> type1 = new ArrayList<>();
                    for(int i = 0; i < filteredEmpLists.size(); i++) {
                        type1.add(filteredEmpLists.get(i).getEmp_name());
                    }

                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                    employeeSelect.setAdapter(arrayAdapter1);

                    for (int i = 0; i < leaveRecLists.size(); i++) {
                        if (selected_div_id.equals(leaveRecLists.get(i).getJsm_divm_id()) && selected_dept_id.equals(leaveRecLists.get(i).getJsm_dept_id())) {
                            String la_id = leaveRecLists.get(i).getLa_id();
                            String la_app_code = leaveRecLists.get(i).getLa_app_code();
                            String la_approved = leaveRecLists.get(i).getLa_approved();
                            String la_date = leaveRecLists.get(i).getLa_date();
                            String leave_type = leaveRecLists.get(i).getLeave_type();
                            String la_from_date = leaveRecLists.get(i).getLa_from_date();
                            String la_to_date = leaveRecLists.get(i).getLa_to_date();
                            String la_leave_days = leaveRecLists.get(i).getLa_leave_days();
                            String emp_name = leaveRecLists.get(i).getEmp_name();
                            String emp_id = leaveRecLists.get(i).getEmp_id();
                            String job_calling_title = leaveRecLists.get(i).getJob_calling_title();
                            String jsm_divm_id = leaveRecLists.get(i).getJsm_divm_id();
                            String jsm_dept_id = leaveRecLists.get(i).getJsm_dept_id();
                            String jsm_desig_id = leaveRecLists.get(i).getJsm_desig_id();
                            String coa_name = leaveRecLists.get(i).getCoa_name();
                            String coa_id = leaveRecLists.get(i).getCoa_id();

                            filteredList.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                    la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
                        }
                    }

                }
            }
            else {
                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(true);
                filteredDepartmentLists = new ArrayList<>();

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);
                filteredEmpLists = new ArrayList<>();

                filteredList = new ArrayList<>();

                filteredDepartmentLists.add(new DepartmentList("","...",""));
                for (int i = 0; i < departmentLists.size(); i++) {
                    if (selected_div_id.equals(departmentLists.get(i).getDivm_id())) {
                        String dept_id = departmentLists.get(i).getDept_id();
                        String dept_name = departmentLists.get(i).getDept_name();
                        String divm_id = departmentLists.get(i).getDivm_id();

                        filteredDepartmentLists.add(new DepartmentList(dept_id,dept_name,divm_id));
                    }
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < filteredDepartmentLists.size(); i++) {
                    type.add(filteredDepartmentLists.get(i).getDept_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                departmentSelect.setAdapter(arrayAdapter);

                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < employeeLists.size(); i++) {
                    type1.add(employeeLists.get(i).getEmp_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                employeeSelect.setAdapter(arrayAdapter1);

                for (int i = 0; i < leaveRecLists.size(); i++) {
                    if (selected_div_id.equals(leaveRecLists.get(i).getJsm_divm_id())) {
                        String la_id = leaveRecLists.get(i).getLa_id();
                        String la_app_code = leaveRecLists.get(i).getLa_app_code();
                        String la_approved = leaveRecLists.get(i).getLa_approved();
                        String la_date = leaveRecLists.get(i).getLa_date();
                        String leave_type = leaveRecLists.get(i).getLeave_type();
                        String la_from_date = leaveRecLists.get(i).getLa_from_date();
                        String la_to_date = leaveRecLists.get(i).getLa_to_date();
                        String la_leave_days = leaveRecLists.get(i).getLa_leave_days();
                        String emp_name = leaveRecLists.get(i).getEmp_name();
                        String emp_id = leaveRecLists.get(i).getEmp_id();
                        String job_calling_title = leaveRecLists.get(i).getJob_calling_title();
                        String jsm_divm_id = leaveRecLists.get(i).getJsm_divm_id();
                        String jsm_dept_id = leaveRecLists.get(i).getJsm_dept_id();
                        String jsm_desig_id = leaveRecLists.get(i).getJsm_desig_id();
                        String coa_name = leaveRecLists.get(i).getCoa_name();
                        String coa_id = leaveRecLists.get(i).getCoa_id();

                        filteredList.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
                    }
                }

            }

            leaveRecordAdapter = new LeaveRecordAdapter(filteredList,LeaveRecord.this,last_date);
            lvRecListView.setAdapter(leaveRecordAdapter);
            if (filteredList.isEmpty()) {
                noDa.setVisibility(View.VISIBLE);
            }
            else {
                noDa.setVisibility(View.GONE);
            }
            int p_c = 0;
            int a_c = 0;
            int r_c = 0;
            int c_a_c = 0;
            for (int i = 0; i < filteredList.size(); i++) {
                switch (filteredList.get(i).getLa_approved()) {
                    case "0":
                        p_c++;
                        break;
                    case "1":
                        a_c++;
                        break;
                    case "2":
                        r_c++;
                        break;
                    case "3":
                        c_a_c++;
                        break;
                }
            }
            all_leave_stat = Html.fromHtml("Total Leave Application:   <font color='black'><b>"+ filteredList.size() +"</b></font><br>"+
                    "Pending:   <font color='black'><b>"+ p_c +"</b></font><br>"+
                    "Approved:   <font color='black'><b>"+a_c+"</b></font><br>"+
                    "Rejected:   <font color='black'><b>"+r_c+"</b></font><br>"+
                    "Cancel Approved Leave:   <font color='black'><b>"+c_a_c+"</b></font><br>");
        }
        else {
            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);
            filteredDepartmentLists = new ArrayList<>();

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();

            filteredList = new ArrayList<>();

            ArrayList<String> type = new ArrayList<>();
            for(int i = 0; i < departmentLists.size(); i++) {
                type.add(departmentLists.get(i).getDept_name());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
            departmentSelect.setAdapter(arrayAdapter);

            ArrayList<String> type1 = new ArrayList<>();
            for(int i = 0; i < employeeLists.size(); i++) {
                type1.add(employeeLists.get(i).getEmp_name());
            }

            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
            employeeSelect.setAdapter(arrayAdapter1);

            leaveRecordAdapter = new LeaveRecordAdapter(leaveRecLists,LeaveRecord.this,last_date);
            lvRecListView.setAdapter(leaveRecordAdapter);

            if (leaveRecLists.isEmpty()) {
                noDa.setVisibility(View.VISIBLE);
            }
            else {
                noDa.setVisibility(View.GONE);
            }
            int p_c = 0;
            int a_c = 0;
            int r_c = 0;
            int c_a_c = 0;
            for (int i = 0; i < leaveRecLists.size(); i++) {
                switch (leaveRecLists.get(i).getLa_approved()) {
                    case "0":
                        p_c++;
                        break;
                    case "1":
                        a_c++;
                        break;
                    case "2":
                        r_c++;
                        break;
                    case "3":
                        c_a_c++;
                        break;
                }
            }
            all_leave_stat = Html.fromHtml("Total Leave Application:   <font color='black'><b>"+ leaveRecLists.size() +"</b></font><br>"+
                    "Pending:   <font color='black'><b>"+ p_c +"</b></font><br>"+
                    "Approved:   <font color='black'><b>"+a_c+"</b></font><br>"+
                    "Rejected:   <font color='black'><b>"+r_c+"</b></font><br>"+
                    "Cancel Approved Leave:   <font color='black'><b>"+c_a_c+"</b></font><br>");
        }
    }

    public void getAllData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        branchLists = new ArrayList<>();
        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        filteredDepartmentLists = new ArrayList<>();
        employeeLists = new ArrayList<>();
        filteredEmpLists = new ArrayList<>();
        leaveRecLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        String brnUrl = api_url_front+"hrm_dashboard/getBranches";
        String divUrl = api_url_front+"hrm_dashboard/getDivision";
        String depUrl = api_url_front+"hrm_dashboard/getDepartment";
        String empUrl = api_url_front+"hrm_dashboard/getEmployee";
        String lvRecUrl = api_url_front+"hrm_dashboard/getLeaveRecord?first_date="+first_date+"&last_date="+last_date;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest lvRecReq = new StringRequest(Request.Method.GET, lvRecUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String la_id = info.getString("la_id")
                                .equals("null") ? "" :info.getString("la_id");
                        String la_app_code = info.getString("la_app_code")
                                .equals("null") ? "" :info.getString("la_app_code");
                        String la_approved = info.getString("la_approved")
                                .equals("null") ? "" :info.getString("la_approved");
                        String la_date = info.getString("la_date")
                                .equals("null") ? "" :info.getString("la_date");
                        String leave_type = info.getString("leave_type")
                                .equals("null") ? "" :info.getString("leave_type");
                        String la_from_date = info.getString("la_from_date")
                                .equals("null") ? "" :info.getString("la_from_date");
                        String la_to_date = info.getString("la_to_date")
                                .equals("null") ? "" :info.getString("la_to_date");
                        String la_leave_days = info.getString("la_leave_days")
                                .equals("null") ? "" :info.getString("la_leave_days");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");
                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");

                        leaveRecLists.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
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

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,""));
                    }
                }
                requestQueue.add(lvRecReq);
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

        StringRequest depReq = new StringRequest(Request.Method.GET, depUrl, response -> {
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

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    divisionLists.add(new DivisionList("","..."));
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

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

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

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                filterAll();

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
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(LeaveRecord.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getAllData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getBranchWiseAllData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        filteredDepartmentLists = new ArrayList<>();
        employeeLists = new ArrayList<>();
        filteredEmpLists = new ArrayList<>();
        leaveRecLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        String divUrl = api_url_front+"hrm_dashboard/getDivisionWithCoa?p_coa_id="+branch_id;
        String depUrl = api_url_front+"hrm_dashboard/getDepartmentWithCoa?p_coa_id="+branch_id;
        String empUrl = api_url_front+"hrm_dashboard/getEmployeeWithCoa?p_coa_id="+branch_id;
        String lvRecUrl = api_url_front+"hrm_dashboard/getLeaveRecordWithCoa?first_date="+first_date+"&last_date="+last_date+"&p_coa_id="+branch_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest lvRecReq = new StringRequest(Request.Method.GET, lvRecUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String la_id = info.getString("la_id")
                                .equals("null") ? "" :info.getString("la_id");
                        String la_app_code = info.getString("la_app_code")
                                .equals("null") ? "" :info.getString("la_app_code");
                        String la_approved = info.getString("la_approved")
                                .equals("null") ? "" :info.getString("la_approved");
                        String la_date = info.getString("la_date")
                                .equals("null") ? "" :info.getString("la_date");
                        String leave_type = info.getString("leave_type")
                                .equals("null") ? "" :info.getString("leave_type");
                        String la_from_date = info.getString("la_from_date")
                                .equals("null") ? "" :info.getString("la_from_date");
                        String la_to_date = info.getString("la_to_date")
                                .equals("null") ? "" :info.getString("la_to_date");
                        String la_leave_days = info.getString("la_leave_days")
                                .equals("null") ? "" :info.getString("la_leave_days");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");
                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");

                        leaveRecLists.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
                    }
                }
                connected = true;
                updateInterfaceCoa();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterfaceCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterfaceCoa();
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

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title,""));
                    }
                }
                requestQueue.add(lvRecReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterfaceCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterfaceCoa();
        });

        StringRequest depReq = new StringRequest(Request.Method.GET, depUrl, response -> {
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
                requestQueue.add(empReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterfaceCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterfaceCoa();
        });

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    divisionLists.add(new DivisionList("","..."));
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
                updateInterfaceCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterfaceCoa();
        });

        requestQueue.add(divReq);

    }

    private void updateInterfaceCoa() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LeaveRecord.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                filterAll();

                loading = false;

            }
            else {
                alertMessageCoa();
            }
        }
        else {
            alertMessageCoa();
        }
    }

    public void alertMessageCoa() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(LeaveRecord.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getBranchWiseAllData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getLeaveData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        leaveRecLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        String lvRecUrl = api_url_front+"hrm_dashboard/getLeaveRecord?first_date="+first_date+"&last_date="+last_date;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest lvRecReq = new StringRequest(Request.Method.GET, lvRecUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String la_id = info.getString("la_id")
                                .equals("null") ? "" :info.getString("la_id");
                        String la_app_code = info.getString("la_app_code")
                                .equals("null") ? "" :info.getString("la_app_code");
                        String la_approved = info.getString("la_approved")
                                .equals("null") ? "" :info.getString("la_approved");
                        String la_date = info.getString("la_date")
                                .equals("null") ? "" :info.getString("la_date");
                        String leave_type = info.getString("leave_type")
                                .equals("null") ? "" :info.getString("leave_type");
                        String la_from_date = info.getString("la_from_date")
                                .equals("null") ? "" :info.getString("la_from_date");
                        String la_to_date = info.getString("la_to_date")
                                .equals("null") ? "" :info.getString("la_to_date");
                        String la_leave_days = info.getString("la_leave_days")
                                .equals("null") ? "" :info.getString("la_leave_days");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");
                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");

                        leaveRecLists.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
                    }
                }
                connected = true;
                updateLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLayout();
        });

        requestQueue.add(lvRecReq);

    }

    private void updateLayout() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                filterAll();

                loading = false;

            }
            else {
                alertMessageDa();
            }
        }
        else {
            alertMessageDa();
        }
    }

    public void alertMessageDa() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(LeaveRecord.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getLeaveData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getBranchWiseLeaveData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        leaveRecLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        String lvRecUrl = api_url_front+"hrm_dashboard/getLeaveRecordWithCoa?first_date="+first_date+"&last_date="+last_date+"&p_coa_id="+branch_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest lvRecReq = new StringRequest(Request.Method.GET, lvRecUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String la_id = info.getString("la_id")
                                .equals("null") ? "" :info.getString("la_id");
                        String la_app_code = info.getString("la_app_code")
                                .equals("null") ? "" :info.getString("la_app_code");
                        String la_approved = info.getString("la_approved")
                                .equals("null") ? "" :info.getString("la_approved");
                        String la_date = info.getString("la_date")
                                .equals("null") ? "" :info.getString("la_date");
                        String leave_type = info.getString("leave_type")
                                .equals("null") ? "" :info.getString("leave_type");
                        String la_from_date = info.getString("la_from_date")
                                .equals("null") ? "" :info.getString("la_from_date");
                        String la_to_date = info.getString("la_to_date")
                                .equals("null") ? "" :info.getString("la_to_date");
                        String la_leave_days = info.getString("la_leave_days")
                                .equals("null") ? "" :info.getString("la_leave_days");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String coa_name = info.getString("coa_name")
                                .equals("null") ? "" :info.getString("coa_name");
                        String coa_id = info.getString("coa_id")
                                .equals("null") ? "" :info.getString("coa_id");

                        leaveRecLists.add(new LeaveRecList(la_id,la_app_code,la_approved,la_date,leave_type,la_from_date,la_to_date,
                                la_leave_days,emp_name,emp_id,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,coa_name,coa_id));
                    }
                }
                connected = true;
                updateLayoutCoa();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLayoutCoa();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLayoutCoa();
        });

        requestQueue.add(lvRecReq);

    }

    private void updateLayoutCoa() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                filterAll();

                loading = false;

            }
            else {
                alertMessageDaCoa();
            }
        }
        else {
            alertMessageDaCoa();
        }
    }

    public void alertMessageDaCoa() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(LeaveRecord.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getBranchWiseLeaveData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}