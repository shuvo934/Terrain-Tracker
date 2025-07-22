package ttit.com.shuvo.terraintracker.dash_board.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.dash_board.model.EmpWiseLeaveList;


public class LeaveHRAdapter extends RecyclerView.Adapter<LeaveHRAdapter.LHRHolder> {
    private final ArrayList<EmpWiseLeaveList> mCategoryItem;
    private final Context myContext;

    public LeaveHRAdapter(Context myContext, ArrayList<EmpWiseLeaveList> mCategoryItem) {
        this.myContext = myContext;
        this.mCategoryItem = mCategoryItem;
    }

    @NonNull
    @Override
    public LHRHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.leave_emp_date_hr_dash_layout, parent, false);
        return new LHRHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LHRHolder holder, int position) {
        EmpWiseLeaveList leaveList = mCategoryItem.get(position);
        holder.emp_name.setText(leaveList.getEmp_name());

        holder.firstLeave.setText(leaveList.getFirst_day_leave_name());
        if (leaveList.getFirst_day_leave_name().isEmpty()) {
            holder.firstLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.firstLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

        holder.secondLeave.setText(leaveList.getSecond_day_leave_name());
        if (leaveList.getSecond_day_leave_name().isEmpty()) {
            holder.secondLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.secondLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

        holder.thirdLeave.setText(leaveList.getThird_day_leave_name());
        if (leaveList.getThird_day_leave_name().isEmpty()) {
            holder.thirdLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.thirdLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

        holder.fourthLeave.setText(leaveList.getFourth_day_leave_name());
        if (leaveList.getFourth_day_leave_name().isEmpty()) {
            holder.fourthLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.fourthLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

        holder.fifthLeave.setText(leaveList.getFifth_day_leave_name());
        if (leaveList.getFifth_day_leave_name().isEmpty()) {
            holder.fifthLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.fifthLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

        holder.sixthLeave.setText(leaveList.getSixth_day_leave_name());
        if (leaveList.getSixth_day_leave_name().isEmpty()) {
            holder.sixthLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.sixthLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

        holder.seventhLeave.setText(leaveList.getSeventh_day_leave_name());
        if (leaveList.getSeventh_day_leave_name().isEmpty()) {
            holder.seventhLeave.setBackgroundColor(myContext.getColor(R.color.white));
        }
        else {
            holder.seventhLeave.setBackgroundColor(myContext.getColor(R.color.disabled));
        }

    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class LHRHolder extends RecyclerView.ViewHolder {
        public TextView emp_name;
        public TextView firstLeave;
        public TextView secondLeave;
        public TextView thirdLeave;
        public TextView fourthLeave;
        public TextView fifthLeave;
        public TextView sixthLeave;
        public TextView seventhLeave;

        public LHRHolder(@NonNull View itemView) {
            super(itemView);
            emp_name = itemView.findViewById(R.id.in_leave_emp_name_in_hr_dashboard);
            firstLeave = itemView.findViewById(R.id.first_day_leave_name_in_hr_dashboard);
            secondLeave = itemView.findViewById(R.id.second_day_leave_name_in_hr_dashboard);
            thirdLeave = itemView.findViewById(R.id.third_day_leave_name_in_hr_dashboard);
            fourthLeave = itemView.findViewById(R.id.fourth_day_leave_name_in_hr_dashboard);
            fifthLeave = itemView.findViewById(R.id.fifth_day_leave_name_in_hr_dashboard);
            sixthLeave = itemView.findViewById(R.id.sixth_day_leave_name_in_hr_dashboard);
            seventhLeave = itemView.findViewById(R.id.seventh_day_leave_name_in_hr_dashboard);
        }
    }
}
