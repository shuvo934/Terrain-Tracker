package ttit.com.shuvo.terraintracker.attendance;

import static ttit.com.shuvo.terraintracker.Constants.api_url_front;
import static ttit.com.shuvo.terraintracker.loginFile.Login.userInfoLists;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.dialogs.AttUpReqSelectionDialog;
import ttit.com.shuvo.terraintracker.attendance.interfaces.AttendanceUpReqSelectedListener;

public class AttendanceUpdateReqApproval extends AppCompatActivity implements AttendanceUpReqSelectedListener {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    TextInputLayout requestCodeLay;
    TextInputEditText requestCode;

    CardView afterSelecting;
    LinearLayout afterSelectingButton;
    LinearLayout inLay;
    LinearLayout outLay;
    LinearLayout forLay;

    TextInputEditText name;
    TextInputEditText empCode;
    TextInputEditText appDate;
    TextInputEditText title;
    TextInputEditText requestType;
    TextInputEditText shiftUpdated;
    TextInputEditText dateUpdated;
    TextInputEditText inUpdated;
    TextInputEditText outUpdated;
    TextInputEditText machineIn;
    TextInputEditText machineOut;
    TextInputEditText shift;
    TextInputEditText reason;
    TextInputEditText reasonDesc;
    TextInputLayout commentsLay;
    TextInputEditText comments;
    TextInputEditText forwardedBy;
    TextInputEditText forwardComm;

    Button approve;
    //    Button forward;
    Button reject;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    private Boolean isApprovedd = false;
    private Boolean isApprovedChecked = false;
    private Boolean appppppprrrrr = false;
    String approveSuccess = "";

    private Boolean isRejected = false;
    private Boolean isRejectedChecked = false;
    private Boolean rrreeejjjeecctt = false;
    String rejectSuccess = "";

    String req_code = "";
    String darm_id = "";
    String darm_emp_id = "";

    String emp_name = "";
    String emp_code = "";
    String app_date = "";
    String call_title = "";
    String req_type = "";
    String shift_update = "";
    String date_update = "";
    String arr_time = "";
    String dep_time = "";
    String mac_in = "";
    String mac_out = "";
    String current_shift = "";
    String reason_type = "";
    String reason_desc = "";
    String forwarded_by = "";
    String forward_comm = "";
    String comment_text = "";

    String user_name = "";

    String approvedEmpId = "";
    String jobCalling = "";
    String jsmID = "";
    String deptId = "";
    String divmId = "";
    String nowUpdateDate = "";
    String jobEmail = "";

    Logger logger = Logger.getLogger(AttendanceUpdateReqApproval.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_update_req_approval);

        fullLayout = findViewById(R.id.att_up_approval_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_att_up_approval);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_att_up_approval);

        requestCodeLay = findViewById(R.id.request_code_layout_att_approve);
        requestCode = findViewById(R.id.request_code_att_approve);

        afterSelecting = findViewById(R.id.after_request_selecting_att_approve);
        afterSelecting.setVisibility(View.GONE);
        afterSelectingButton = findViewById(R.id.linearLayout50_att_approve);
        afterSelectingButton.setVisibility(View.GONE);

        inLay = findViewById(R.id.in_time_lay_att_approve);
        inLay.setVisibility(View.GONE);
        outLay = findViewById(R.id.out_time_lay_att_approve);
        outLay.setVisibility(View.GONE);
        forLay = findViewById(R.id.forward_layout);
        forLay.setVisibility(View.GONE);

        name = findViewById(R.id.name_att_att_approve);
        empCode = findViewById(R.id.id_att_att_approve);
        appDate = findViewById(R.id.now_date_att_att_approve);
        title = findViewById(R.id.calling_title_att_approve);
        requestType = findViewById(R.id.req_type_att_approve);
        shiftUpdated = findViewById(R.id.shift_update_att_approve);
        dateUpdated = findViewById(R.id.date_to_be_updated_att_approve);
        inUpdated = findViewById(R.id.arrival_time_to_be_updated_att_approve);
        outUpdated = findViewById(R.id.departure_time_to_be_updated_att_approve);
        machineIn = findViewById(R.id.machine_in_time_att_approve);
        machineOut = findViewById(R.id.machine_out_time_att_approve);
        shift = findViewById(R.id.current_shift_att_approve);
        reason = findViewById(R.id.reason_type_att_approve);
        reasonDesc = findViewById(R.id.reason_description_att_approve);
        comments = findViewById(R.id.comments_given_att_approve);
        commentsLay = findViewById(R.id.comments_given_layout_att_approve);
        forwardedBy = findViewById(R.id.forwarded_by_att_approve);
        forwardComm = findViewById(R.id.forward_comment_att_approve);

        approve = findViewById(R.id.approve_button_att);
//        forward = findViewById(R.id.forward_button_att);
        reject = findViewById(R.id.reject_button_att);

        if (userInfoLists != null) {
            if (!userInfoLists.isEmpty()) {
                user_name = userInfoLists.get(0).getEmp_code();
            }
        }

        requestCode.setOnClickListener(v -> {
            AttUpReqSelectionDialog selectRequest = new AttUpReqSelectionDialog(AttendanceUpdateReqApproval.this);
            selectRequest.show(getSupportFragmentManager(),"Request");
        });

        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    String text = "Write Approve/Reject Comments:";
                    commentsLay.setHint(text);
                }
                else {
                    String text = "Approve/Reject Comments:";
                    commentsLay.setHint(text);
                }
            }
        });

        comments.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    comments.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        approve.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendanceUpdateReqApproval.this);
            builder.setTitle("Approve Request!")
                    .setMessage("Do you want to approve this request?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        comment_text = Objects.requireNonNull(comments.getText()).toString();
                        approveAttReq();
                    })
                    .setNegativeButton("NO", (dialog, which) -> {

                    });
            AlertDialog alert = builder.create();
            alert.show();
        });

        reject.setOnClickListener(v -> {
            comment_text = Objects.requireNonNull(comments.getText()).toString();
            if (comment_text.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please mention reason for reject", Toast.LENGTH_SHORT).show();
            }
            else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendanceUpdateReqApproval.this);
                builder.setTitle("Reject Request!")
                        .setMessage("Do you want to reject this request?")
                        .setPositiveButton("YES", (dialog, which) -> rejectAttReq())
                        .setNegativeButton("NO", (dialog, which) -> {

                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        AttUpReqSelectionDialog selectRequest = new AttUpReqSelectionDialog(AttendanceUpdateReqApproval.this);
        selectRequest.show(getSupportFragmentManager(),"Request");
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onSelected(String request_code, String darm_id, String darm_emp_id) {
        req_code = request_code;
        this.darm_id = darm_id;
        this.darm_emp_id = darm_emp_id;
        requestCode.setText(req_code);
        requestCodeLay.setHint("Attendance Update Request Code");
        getReqData();
    }

    public void getReqData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        emp_name = "";
        emp_code = "";
        app_date = "";
        call_title = "";
        req_type = "";
        shift_update = "";
        date_update = "";
        arr_time = "";
        dep_time = "";
        mac_in = "";
        mac_out = "";
        current_shift = "";
        reason_type = "";
        reason_desc = "";
        forwarded_by = "";
        forward_comm = "";

        String reqDataUrl = api_url_front+"hrm_dashboard/getAttUpdateData?p_darm_app_code="+req_code+"&p_usr_name="+user_name;

        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceUpdateReqApproval.this);

        StringRequest reqDataReq = new StringRequest(Request.Method.GET, reqDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject reqDataInfo = array.getJSONObject(i);

                        emp_code = reqDataInfo.getString("emp_code")
                                .equals("null") ? "" : reqDataInfo.getString("emp_code");
                        emp_name = reqDataInfo.getString("emp_name")
                                .equals("null") ? "" : reqDataInfo.getString("emp_name");
                        call_title = reqDataInfo.getString("job_calling_title")
                                .equals("null") ? "" : reqDataInfo.getString("job_calling_title");
                        app_date = reqDataInfo.getString("darm_date")
                                .equals("null") ? "" : reqDataInfo.getString("darm_date");
                        req_type = reqDataInfo.getString("darm_req_type")
                                .equals("null") ? "" : reqDataInfo.getString("darm_req_type");
                        shift_update = reqDataInfo.getString("shift")
                                .equals("null") ? "" : reqDataInfo.getString("shift");
                        date_update = reqDataInfo.getString("darm_update_date")
                                .equals("null") ? "" : reqDataInfo.getString("darm_update_date");
                        arr_time = reqDataInfo.getString("arrival_time")
                                .equals("null") ? "" : reqDataInfo.getString("arrival_time");
                        dep_time = reqDataInfo.getString("departure_time")
                                .equals("null") ? "" : reqDataInfo.getString("departure_time");
                        reason_type = reqDataInfo.getString("reason")
                                .equals("null") ? "" : reqDataInfo.getString("reason");
                        reason_desc = reqDataInfo.getString("darm_reason")
                                .equals("null") ? "" : reqDataInfo.getString("darm_reason");
                        mac_in = reqDataInfo.getString("mac_in_time")
                                .equals("null") ? "" : reqDataInfo.getString("mac_in_time");
                        mac_out = reqDataInfo.getString("mac_out_time")
                                .equals("null") ? "" : reqDataInfo.getString("mac_out_time");
                        current_shift = reqDataInfo.getString("current_shift")
                                .equals("null") ? "" : reqDataInfo.getString("current_shift");
                        forward_comm = reqDataInfo.getString("dard_recommendation")
                                .equals("null") ? "" : reqDataInfo.getString("dard_recommendation");
                        forwarded_by = reqDataInfo.getString("forwarder_name")
                                .equals("null") ? "" : reqDataInfo.getString("forwarder_name");
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

        requestQueue.add(reqDataReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                afterSelecting.setVisibility(View.VISIBLE);
                afterSelectingButton.setVisibility(View.VISIBLE);
                if (emp_name.isEmpty()) {
                    name.setText("");
                } else {
                    name.setText(emp_name);
                }

                if (emp_code.isEmpty()) {
                    empCode.setText("");
                } else {
                    empCode.setText(emp_code);
                }

                if (app_date.isEmpty()) {
                    appDate.setText("");
                } else {
                    appDate.setText(app_date);
                }

                if (call_title.isEmpty()) {
                    title.setText("");
                } else {
                    title.setText(call_title);
                }

                if (req_type.isEmpty()) {
                    requestType.setText("");
                } else {
                    requestType.setText(req_type);
                }

                if (shift_update.isEmpty()) {
                    shiftUpdated.setText("");
                } else {
                    shiftUpdated.setText(shift_update);
                }

                if (date_update.isEmpty()) {
                    dateUpdated.setText("");
                } else {
                    dateUpdated.setText(date_update);
                }

                if (arr_time.isEmpty()) {
                    inUpdated.setText("");
                    inLay.setVisibility(View.GONE);
                } else {
                    inUpdated.setText(arr_time);
                    inLay.setVisibility(View.VISIBLE);
                }

                if (dep_time.isEmpty()) {
                    outUpdated.setText("");
                    outLay.setVisibility(View.GONE);
                } else {
                    outUpdated.setText(dep_time);
                    outLay.setVisibility(View.VISIBLE);
                }

                if (mac_in.isEmpty()) {
                    machineIn.setText("");
                } else {
                    machineIn.setText(mac_in);
                }

                if (mac_out.isEmpty()) {
                    machineOut.setText("");
                } else {
                    machineOut.setText(mac_out);
                }

                if (current_shift.isEmpty()) {
                    shift.setText("");
                } else {
                    shift.setText(current_shift);
                }

                if (reason_type.isEmpty()) {
                    reason.setText("");
                } else {
                    reason.setText(reason_type);
                }

                if (reason_desc.isEmpty()) {
                    reasonDesc.setText("");
                } else {
                    reasonDesc.setText(reason_desc);
                }

                if (forwarded_by.isEmpty()) {
                    forLay.setVisibility(View.GONE);
                    forwardedBy.setText("");
                }
                else {
                    forwardedBy.setText(forwarded_by);
                    forLay.setVisibility(View.VISIBLE);
                }

                if (forward_comm.isEmpty()) {
                    forwardComm.setText("");
                } else {
                    forwardComm.setText(forward_comm);
                }

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AttendanceUpdateReqApproval.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getReqData();
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

//    public void approveAttReq() {
//        fullLayout.setVisibility(View.GONE);
//        circularProgressIndicator.setVisibility(View.VISIBLE);
//        loading = true;
//        appppppprrrrr = false;
//        isApprovedd = false;
//        isApprovedChecked = false;
//
//        approveSuccess = "";
//
//        String url = api_url_front+"hrm_dashboard/approveAttUpReq";
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        StringRequest approveReq = new StringRequest(Request.Method.POST, url, response -> {
//            appppppprrrrr = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String string_out = jsonObject.getString("string_out");
//                System.out.println(string_out);
//                String updated_req = jsonObject.getString("updated_req");
//                if (string_out.equals("Successfully Created")) {
//                    isApprovedd = true;
//                    isApprovedChecked = updated_req.toLowerCase(Locale.ENGLISH).equals("ok");
//                    approveSuccess = updated_req;
//                }
//                else {
//                    System.out.println(string_out);
//                    isApprovedd = false;
//                }
//                updateLayout();
//            }
//            catch (JSONException e) {
//                isApprovedd = false;
//                logger.log(Level.WARNING,e.getMessage(),e);
//                parsing_message = e.getLocalizedMessage();
//                updateLayout();
//            }
//        }, error -> {
//            logger.log(Level.WARNING,error.getMessage(),error);
//            parsing_message = error.getLocalizedMessage();
//            appppppprrrrr = false;
//            isApprovedd = false;
//            updateLayout();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("P_DARM_ID",darm_id);
//                headers.put("P_USR_NAME",user_name);
//                headers.put("P_DARM_EMP_ID",darm_emp_id);
//                headers.put("P_TEXT_COMMENTS",comment_text);
//                headers.put("P_UPDATE_DATE",date_update);
//                return headers;
//            }
//        };
//
//        requestQueue.add(approveReq);
//    }
    public void approveAttReq() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        appppppprrrrr = false;
        isApprovedd = false;
        isApprovedChecked = false;

        approvedEmpId = "";
        jobCalling = "";
        jsmID = "";
        deptId = "";
        divmId = "";
        nowUpdateDate = "";
        jobEmail = "";

        String approverDataUrl = api_url_front + "attendanceUpdateReq/getApproverData/"+user_name;
        String approveAttUrl = api_url_front + "attendanceUpdateReq/approveAttReq";

        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceUpdateReqApproval.this);

        StringRequest approveReq = new StringRequest(Request.Method.POST, approveAttUrl, response -> {
            appppppprrrrr = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                String updated_req = jsonObject.getString("updated_req");
                if (string_out.equals("Successfully Created")) {
                    isApprovedChecked = true;
                    isApprovedd = updated_req.equals("true");
                }
                else {
                    System.out.println(string_out);
                    isApprovedChecked = false;
                }
                updateLayout();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                isApprovedChecked = false;
                updateLayout();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            appppppprrrrr = false;
            isApprovedChecked = false;
            updateLayout();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DARM_ID",darm_id);
                headers.put("P_EMP_ID",approvedEmpId);
                headers.put("P_CALLING_TITLE",jobCalling);
                headers.put("P_JSM_ID",jsmID);
                headers.put("P_DEPT_ID",deptId);
                headers.put("P_DIVM_ID",divmId);
                headers.put("P_NOW_UPDATE_DATE",nowUpdateDate);
                headers.put("P_COMMENTS",comment_text);
                headers.put("P_DARM_EMP_ID",darm_emp_id);
                headers.put("P_UPDATE_DATE",date_update);
                return headers;
            }
        };

        StringRequest appDataReq = new StringRequest(Request.Method.GET, approverDataUrl, response -> {
            appppppprrrrr = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject appDataInfo = array.getJSONObject(i);

                        approvedEmpId = appDataInfo.getString("emp_id");
                        jobCalling = appDataInfo.getString("job_calling_title");
                        jsmID = appDataInfo.getString("jsm_id");
                        deptId = appDataInfo.getString("dept_id");
                        divmId = appDataInfo.getString("divm_id");
                        nowUpdateDate = appDataInfo.getString("update_date");
                        jobEmail = appDataInfo.getString("job_email");
                    }
                }

                requestQueue.add(approveReq);
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                isApprovedChecked = false;
                updateLayout();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            appppppprrrrr = false;
            isApprovedChecked = false;
            updateLayout();
        });

        requestQueue.add(appDataReq);
    }

    private void updateLayout() {
        if (appppppprrrrr) {
            if (isApprovedd) {
                fullLayout.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                if (isApprovedChecked) {
                    appppppprrrrr = false;
                    isApprovedd = false;
                    isApprovedChecked = false;
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendanceUpdateReqApproval.this);
                    builder.setMessage("Attendance Update Approved Successfully")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            });

                    AlertDialog alert = builder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Already Updated by Another User", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                alertMessageApLeave();
            }
        }
        else {
            alertMessageApLeave();
        }
    }

    public void alertMessageApLeave() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    approveAttReq();
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

//    public void rejectAttReq() {
//        fullLayout.setVisibility(View.GONE);
//        circularProgressIndicator.setVisibility(View.VISIBLE);
//        loading = true;
//        rejectSuccess = "";
//
//        rrreeejjjeecctt = false;
//        isRejected = false;
//        isRejectedChecked = false;
//
//        String url = api_url_front+"hrm_dashboard/rejectAttUpReq";
//
//        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceUpdateReqApproval.this);
//
//        StringRequest rejectReq = new StringRequest(Request.Method.POST, url, response -> {
//            rrreeejjjeecctt = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String string_out = jsonObject.getString("string_out");
//                String updated_req = jsonObject.getString("updated_req");
//                if (string_out.equals("Successfully Created")) {
//                    isRejected = true;
//                    isRejectedChecked = updated_req.toLowerCase(Locale.ENGLISH).equals("ok");
//                    rejectSuccess = updated_req;
//                }
//                else {
//                    System.out.println(string_out);
//                    isRejected = false;
//                }
//                updateAfterReject();
//            }
//            catch (JSONException e) {
//                logger.log(Level.WARNING,e.getMessage(),e);
//                parsing_message = e.getLocalizedMessage();
//                isRejected = false;
//                updateAfterReject();
//            }
//        }, error -> {
//            logger.log(Level.WARNING,error.getMessage(),error);
//            parsing_message = error.getLocalizedMessage();
//            rrreeejjjeecctt = false;
//            isRejected = false;
//            updateAfterReject();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("P_DARM_ID",darm_id);
//                headers.put("P_USR_NAME",user_name);
//                headers.put("P_TEXT_COMMENTS",comment_text);
//                return headers;
//            }
//        };
//
//        requestQueue.add(rejectReq);
//    }
    public void rejectAttReq() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        rrreeejjjeecctt = false;
        isRejected = false;
        isRejectedChecked = false;

        approvedEmpId = "";
        jobCalling = "";
        jsmID = "";
        deptId = "";
        divmId = "";
        nowUpdateDate = "";
        jobEmail = "";

        String rejecterDataUrl = api_url_front + "attendanceUpdateReq/getApproverData/"+user_name;
        String rejectAttUrl = api_url_front + "attendanceUpdateReq/rejectAttReq";

        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceUpdateReqApproval.this);

        StringRequest rejectReq = new StringRequest(Request.Method.POST, rejectAttUrl, response -> {
            rrreeejjjeecctt = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                String updated_req = jsonObject.getString("updated_req");
                if (string_out.equals("Successfully Created")) {
                    isRejectedChecked = true;
                    isRejected = updated_req.equals("true");
                }
                else {
                    System.out.println(string_out);
                    isRejectedChecked = false;
                }
                updateAfterReject();
            }
            catch (JSONException e) {
                isRejectedChecked = false;
                updateAfterReject();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            rrreeejjjeecctt = false;
            isRejectedChecked = false;
            updateAfterReject();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DARM_ID",darm_id);
                headers.put("P_EMP_ID",approvedEmpId);
                headers.put("P_CALLING_TITLE",jobCalling);
                headers.put("P_JSM_ID",jsmID);
                headers.put("P_DEPT_ID",deptId);
                headers.put("P_DIVM_ID",divmId);
                headers.put("P_NOW_UPDATE_DATE",nowUpdateDate);
                headers.put("P_COMMENTS",comment_text);
                return headers;
            }
        };

        StringRequest rejDataReq = new StringRequest(Request.Method.GET, rejecterDataUrl, response -> {
            rrreeejjjeecctt = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject rejDataInfo = array.getJSONObject(i);

                        approvedEmpId = rejDataInfo.getString("emp_id");
                        jobCalling = rejDataInfo.getString("job_calling_title");
                        jsmID = rejDataInfo.getString("jsm_id");
                        deptId = rejDataInfo.getString("dept_id");
                        divmId = rejDataInfo.getString("divm_id");
                        nowUpdateDate = rejDataInfo.getString("update_date");
                        jobEmail = rejDataInfo.getString("job_email");
                    }
                }

                requestQueue.add(rejectReq);
            }
            catch (JSONException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                isRejectedChecked = false;
                updateAfterReject();
            }
        }, error -> {
            logger.log(Level.WARNING, error.getMessage(), error);
            rrreeejjjeecctt = false;
            isRejectedChecked = false;
            updateAfterReject();
        });

        requestQueue.add(rejDataReq);
    }

    private void updateAfterReject() {
        if (rrreeejjjeecctt) {
            if (isRejected) {
                fullLayout.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                if (isRejectedChecked) {
                    rrreeejjjeecctt = false;
                    isRejected = false;
                    isRejectedChecked = false;
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AttendanceUpdateReqApproval.this);
                    builder
                            .setMessage("Attendance Update Rejected Successfully")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            });

                    AlertDialog alert = builder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Already Updated by Another User", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                alertMessageReject();
            }
        }
        else {
            alertMessageReject();
        }
    }

    public void alertMessageReject() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);

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
                    rejectAttReq();
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
}