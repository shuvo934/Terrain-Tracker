package ttit.com.shuvo.terraintracker.leave.dialogs;

import static ttit.com.shuvo.terraintracker.Constants.api_url_front;
import static ttit.com.shuvo.terraintracker.loginFile.Login.userInfoLists;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.leave.adapters.LeaveRequestAdapter;
import ttit.com.shuvo.terraintracker.leave.arraylists.LeaveRequestList;
import ttit.com.shuvo.terraintracker.leave.interfaces.LeaveSelectedListener;

public class LeaveReqSelectionDialog extends AppCompatDialogFragment implements LeaveRequestAdapter.ClickedItem {
    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;

    TextInputLayout searchLay;
    TextInputEditText search;

    RecyclerView recyclerView;
    LeaveRequestAdapter leaveRequestAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noRequestFound;
    AlertDialog alertDialog;

    String searchingName = "";
    ArrayList<LeaveRequestList> leaveRequestLists;
    ArrayList<LeaveRequestList> filteredLists;
    String parsing_message = "";

    AppCompatActivity activity;
    Context mContext;

    public LeaveReqSelectionDialog(Context context) {
        this.mContext = context;
    }

    String user_name = "";
    Boolean isfiltered = false;

    private LeaveSelectedListener leaveSelectedListener;

    Logger logger = Logger.getLogger("LeaveReqSelectionDialog");

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof LeaveSelectedListener)
            leaveSelectedListener = (LeaveSelectedListener) getActivity();

        View view = inflater.inflate(R.layout.leave_request_search_dialog_layout,null);

        activity = (AppCompatActivity) view.getContext();

        leaveRequestLists = new ArrayList<>();
        filteredLists = new ArrayList<>();

        fullLayout = view.findViewById(R.id.search_leave_request_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_search_leave_request);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_page_button_leave_request);
        reload.setVisibility(View.GONE);
        noRequestFound = view.findViewById(R.id.no_leave_req_found_message_for_leave_approve);
        noRequestFound.setVisibility(View.GONE);

        searchLay = view.findViewById(R.id.search_leave_req_code_layout_for_leave_approve);
        search = view.findViewById(R.id.search_leave_req_code_for_leave_approve);

        recyclerView = view.findViewById(R.id.leave_req_list_view_for_leave_approve);

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        if (userInfoLists != null) {
            if (!userInfoLists.isEmpty()) {
                user_name = userInfoLists.get(0).getEmp_code();
            }
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchingName = s.toString();
                filter(searchingName);
            }
        });

        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    search.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        reload.setOnClickListener(v -> getList());

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            if (loading) {
                Toast.makeText(mContext,"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                dialog.dismiss();
            }
        });

        getList();

        return alertDialog;
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getList() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        leaveRequestLists = new ArrayList<>();

        String url = api_url_front+"hrm_dashboard/getLeaveRequestList?p_usr_name="+user_name;

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String la_app_code = docInfo.getString("la_app_code")
                                .equals("null") ? "" : docInfo.getString("la_app_code");
                        String la_emp_id = docInfo.getString("la_emp_id")
                                .equals("null") ? "" : docInfo.getString("la_emp_id");
                        String emp_code = docInfo.getString("emp_code")
                                .equals("null") ? "" : docInfo.getString("emp_code");
                        String emp_name = docInfo.getString("emp_name")
                                .equals("null") ? "" : docInfo.getString("emp_name");
                        String la_date = docInfo.getString("la_date")
                                .equals("null") ? "" : docInfo.getString("la_date");
                        String la_leave_days = docInfo.getString("la_leave_days")
                                .equals("null") ? "" : docInfo.getString("la_leave_days");
                        String la_id = docInfo.getString("la_id")
                                .equals("null") ? "" : docInfo.getString("la_id");

                        leaveRequestLists.add(new LeaveRequestList(la_app_code,la_emp_id,emp_code,emp_name,la_date
                                ,la_leave_days,la_id));
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    private void updateLayout() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                conn = false;
                connected = false;

                searchingName = "";

                if (leaveRequestLists.isEmpty()) {
                    noRequestFound.setVisibility(View.VISIBLE);
                }
                else {
                    noRequestFound.setVisibility(View.GONE);
                }

                leaveRequestAdapter = new LeaveRequestAdapter( mContext, this,leaveRequestLists);
                recyclerView.setAdapter(leaveRequestAdapter);

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
        reload.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        Toast.makeText(mContext, parsing_message, Toast.LENGTH_SHORT).show();
        loading = false;
    }

    private void filter(String text) {
        filteredLists = new ArrayList<>();
        for (LeaveRequestList item : leaveRequestLists) {
            if (item.getLa_app_code().toLowerCase().contains(text.toLowerCase()) || item.getEmp_name().toLowerCase().contains(text.toLowerCase())) {
                filteredLists.add(item);
                isfiltered = true;
            }
        }
        leaveRequestAdapter.filterList(filteredLists);
    }

    @Override
    public void onItemClicked(int Position) {
        String la_id;
        String la_app_code;
        String la_emp_id;
        if (isfiltered) {
            la_id = filteredLists.get(Position).getLa_id();
            la_app_code = filteredLists.get(Position).getLa_app_code();
            la_emp_id = filteredLists.get(Position).getLa_emp_id();
        } else {
            la_id = leaveRequestLists.get(Position).getLa_id();
            la_app_code = leaveRequestLists.get(Position).getLa_app_code();
            la_emp_id = leaveRequestLists.get(Position).getLa_emp_id();
        }

        if(leaveSelectedListener != null)
            leaveSelectedListener.onLeaveSelected(la_id,la_app_code,la_emp_id);

        alertDialog.dismiss();
    }
}
