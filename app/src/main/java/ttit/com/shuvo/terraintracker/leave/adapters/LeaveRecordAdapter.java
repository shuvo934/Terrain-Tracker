package ttit.com.shuvo.terraintracker.leave.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import ttit.com.shuvo.terraintracker.leave.LeaveApproval;
import ttit.com.shuvo.terraintracker.leave.LeaveBalance;
import ttit.com.shuvo.terraintracker.leave.arraylists.LeaveRecList;

public class LeaveRecordAdapter extends RecyclerView.Adapter<LeaveRecordAdapter.LRAHolder> {
    public ArrayList<LeaveRecList> mCategoryItem;
    public Context myContext;
    public String last_date;

    public LeaveRecordAdapter(ArrayList<LeaveRecList> mCategoryItem, Context myContext,String last_date) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.last_date = last_date;
    }

    @NonNull
    @Override
    public LRAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.leave_record_details_view, parent, false);
        return new LRAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LRAHolder holder, int position) {
        LeaveRecList leaveRecList = mCategoryItem.get(position);

        holder.empName.setText(leaveRecList.getEmp_name());
        holder.empDesignation.setText(leaveRecList.getJob_calling_title());
        if (leaveRecList.getCoa_name().isEmpty()) {
            holder.empLocation.setVisibility(View.GONE);
            holder.empLocation.setText("");
        }
        else {
            holder.empLocation.setVisibility(View.VISIBLE);
            String tt = "("+leaveRecList.getCoa_name()+")";
            holder.empLocation.setText(tt);
        }

        holder.leaveType.setText(leaveRecList.getLeave_type());
        String ld;
        if (leaveRecList.getLa_from_date().equals(leaveRecList.getLa_to_date())) {
            ld = leaveRecList.getLa_from_date();
            String date = ld.substring(0, 2);
            String dateOrder = getDateOrdinal(date);
            String mon = ld.substring(ld.length() - 3);
            ld = dateOrder + " " + mon;
        }
        else {
            String fd = leaveRecList.getLa_from_date();
            String l_d = leaveRecList.getLa_to_date();
            String f_mon = fd.substring(fd.length() - 3);
            String l_mon = l_d.substring(l_d.length() - 3);
            String f_date = fd.substring(0, 2);
            String l_date = l_d.substring(0, 2);
            String f_dateOrder = getDateOrdinal(f_date);
            String l_dateOrder = getDateOrdinal(l_date);
            if (f_mon.equals(l_mon)) {
                ld = f_dateOrder + " to " + l_dateOrder + " ("+f_mon+")";
            }
            else {
                ld = f_dateOrder + " " + f_mon + " to " + l_dateOrder + " " + l_mon;
            }

        }
        holder.leaveDate.setText(ld);
        String lds;
        if (leaveRecList.getLa_leave_days().equals("1")) {
            lds = "("+leaveRecList.getLa_leave_days()+" Day)";
        }
        else {
            lds = "("+leaveRecList.getLa_leave_days()+" Days)";
        }
        holder.leaveDays.setText(lds);

        String stat = leaveRecList.getLa_approved();

        switch (stat) {
            case "0":
                stat = "PENDING";
                holder.leaveStatus.setTextColor(myContext.getColor(R.color.back_color));
                break;
            case "1":
                stat = "APPROVED";
                holder.leaveStatus.setTextColor(myContext.getColor(R.color.teal_200));
                break;
            case "2":
                stat = "REJECTED";
                holder.leaveStatus.setTextColor(myContext.getColor(R.color.red_dark));
                break;
            case "3":
                stat = "CANCEL APPROVED LEAVE";
                holder.leaveStatus.setTextColor(Color.parseColor("#ff7675"));
                break;
        }
        holder.leaveStatus.setText(stat);
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public String getDateOrdinal(String date) {
        String dateOrder;
        switch (date) {
            case "01":
                dateOrder = "01st";
                break;
            case "02":
                dateOrder = "02nd";
                break;
            case "03":
                dateOrder = "03rd";
                break;
            case "21":
                dateOrder = "21st";
                break;
            case "22":
                dateOrder = "22nd";
                break;
            case "23":
                dateOrder = "23rd";
                break;
            case "31":
                dateOrder = "31st";
                break;
            default:
                dateOrder = date + "th";
                break;
        }
        if (dateOrder.startsWith("0")) {
            dateOrder = dateOrder.substring(1);
        }
        return dateOrder;
    }

    public class LRAHolder extends RecyclerView.ViewHolder {
        TextView empName;
        TextView empDesignation;
        TextView empLocation;
        TextView leaveType;
        TextView leaveDate;
        TextView leaveDays;
        TextView leaveStatus;
        MaterialCardView lvCard;

        public LRAHolder(@NonNull View itemView) {
            super(itemView);
            empName = itemView.findViewById(R.id.emp_name_lv_rec);
            empDesignation = itemView.findViewById(R.id.emp_designation_lv_rec);
            empLocation = itemView.findViewById(R.id.emp_pri_location_lv_rec);
            leaveType = itemView.findViewById(R.id.leave_type_lv_rec);
            leaveDate = itemView.findViewById(R.id.leave_date_lv_rec);
            leaveDays = itemView.findViewById(R.id.leave_days_lv_rec);
            leaveStatus = itemView.findViewById(R.id.leave_status_lv_rec);
            lvCard = itemView.findViewById(R.id.lv_record_details_card_view);

            lvCard.setOnClickListener(view -> {
                if (mCategoryItem.get(getAdapterPosition()).getLa_approved().equals("0")) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Intent intent = new Intent(myContext, LeaveApproval.class);
                    intent.putExtra("LA_ID", mCategoryItem.get(getAdapterPosition()).getLa_id());
                    intent.putExtra("LA_EMP_ID", mCategoryItem.get(getAdapterPosition()).getEmp_id());
                    intent.putExtra("LA_APP_CODE", mCategoryItem.get(getAdapterPosition()).getLa_app_code());
                    intent.putExtra("FLAG", 1);
                    activity.startActivity(intent);
                }
                else {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Intent intent = new Intent(myContext, LeaveBalance.class);
                    intent.putExtra("COA_ID", mCategoryItem.get(getAdapterPosition()).getCoa_id());
                    intent.putExtra("DIV_ID", mCategoryItem.get(getAdapterPosition()).getJsm_divm_id());
                    intent.putExtra("DEPT_ID", mCategoryItem.get(getAdapterPosition()).getJsm_dept_id());
                    intent.putExtra("DESIG_ID", mCategoryItem.get(getAdapterPosition()).getJsm_desig_id());
                    intent.putExtra("EMP_ID", mCategoryItem.get(getAdapterPosition()).getEmp_id());
                    intent.putExtra("LAST_DATE", last_date);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
