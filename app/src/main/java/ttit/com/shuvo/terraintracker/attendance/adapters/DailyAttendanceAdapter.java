package ttit.com.shuvo.terraintracker.attendance.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.EmpWiseAttendance;
import ttit.com.shuvo.terraintracker.attendance.araylists.DailyAttendanceList;

public class DailyAttendanceAdapter extends RecyclerView.Adapter<DailyAttendanceAdapter.DAHolder> {

    private final ArrayList<DailyAttendanceList> mCategoryItem;
    private final Context myContext;
    private final String coa_id;

    public DailyAttendanceAdapter(Context myContext, ArrayList<DailyAttendanceList> mCategoryItem, String coa_id) {
        this.myContext = myContext;
        this.mCategoryItem = mCategoryItem;
        this.coa_id = coa_id;
    }

    @NonNull
    @Override
    public DAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.daily_attendance_details_view, parent, false);
        return new DAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DAHolder holder, int position) {
        DailyAttendanceList dailyAttendanceList = mCategoryItem.get(position);

        holder.empName.setText(dailyAttendanceList.getEmp_name());
        holder.empDesignation.setText(dailyAttendanceList.getJob_calling_title());
        holder.shiftName.setText(dailyAttendanceList.getOsm_name());
        holder.inTime.setText(dailyAttendanceList.getIn_time());
        holder.outTime.setText(dailyAttendanceList.getOut_time());

        if (dailyAttendanceList.getCoa_name().isEmpty()) {
            holder.locationName.setVisibility(View.GONE);
            holder.locationName.setText("");
        }
        else {
            holder.locationName.setVisibility(View.VISIBLE);
            String tt = "("+dailyAttendanceList.getCoa_name()+")";
            holder.locationName.setText(tt);
        }

        if (dailyAttendanceList.getLate_status().isEmpty()) {
            holder.lateStatus.setVisibility(View.GONE);
        }
        else {
            holder.lateStatus.setVisibility(View.VISIBLE);
        }

        if (dailyAttendanceList.getEarly_status().isEmpty()) {
            holder.earlyStatus.setVisibility(View.GONE);
        }
        else {
            holder.earlyStatus.setVisibility(View.VISIBLE);
        }

        holder.attStatus.setText(dailyAttendanceList.getStatus());

        switch (dailyAttendanceList.getStatus()) {
            case "Out Miss":
                holder.attStatus.setTextColor(myContext.getColor(R.color.red_dark));
                break;
            case "In Leave":
                holder.attStatus.setTextColor(myContext.getColor(R.color.leave_color));
                break;
            case "Off Day":
                holder.attStatus.setTextColor(myContext.getColor(R.color.off_day));
                break;
            case "Present on Leave Day":
                holder.attStatus.setTextColor(myContext.getColor(R.color.pres_on_leave));
                break;
            case "Present on Off Day":
                holder.attStatus.setTextColor(myContext.getColor(R.color.pres_on_off));
                break;
            case "Absent":
                holder.attStatus.setTextColor(myContext.getColor(R.color.absent_color));
                break;
            case "Present":
                holder.attStatus.setTextColor(myContext.getColor(R.color.present_color));
                break;
            default:
                holder.attStatus.setTextColor(myContext.getColor(R.color.progress_back_color));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public class DAHolder extends RecyclerView.ViewHolder {
        TextView empName;
        TextView empDesignation;
        TextView locationName;
        TextView shiftName;
        TextView inTime;
        TextView lateStatus;
        TextView outTime;
        TextView earlyStatus;
        TextView attStatus;
        MaterialCardView daCard;

        public DAHolder(@NonNull View itemView) {
            super(itemView);
            empName = itemView.findViewById(R.id.emp_name_da);
            empDesignation = itemView.findViewById(R.id.emp_designation_da);
            locationName = itemView.findViewById(R.id.att_punch_location_da);
            shiftName = itemView.findViewById(R.id.shift_name_da);
            inTime = itemView.findViewById(R.id.in_time_da);
            lateStatus = itemView.findViewById(R.id.late_status_da);
            outTime = itemView.findViewById(R.id.out_time_da);
            earlyStatus = itemView.findViewById(R.id.early_status_da);
            attStatus = itemView.findViewById(R.id.attendance_status_da);
            daCard = itemView.findViewById(R.id.emp_daily_att_card_view);

            daCard.setOnClickListener(view -> {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Intent intent = new Intent(myContext, EmpWiseAttendance.class);
                intent.putExtra("DIV_ID",mCategoryItem.get(getAdapterPosition()).getJsm_divm_id());
                intent.putExtra("DEPT_ID",mCategoryItem.get(getAdapterPosition()).getJsm_dept_id());
                intent.putExtra("DESIG_ID",mCategoryItem.get(getAdapterPosition()).getJsm_desig_id());
                intent.putExtra("EMP_ID",mCategoryItem.get(getAdapterPosition()).getEmp_id());
                intent.putExtra("COA_ID",coa_id);
                activity.startActivity(intent);
            });
        }
    }
}
