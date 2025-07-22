package ttit.com.shuvo.terraintracker.attendance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.araylists.AttendanceReportList;

public class AttendanceReportAdapter extends RecyclerView.Adapter<AttendanceReportAdapter.ARHolder> {
    public ArrayList<AttendanceReportList> mCategoryItem;
    public Context myContext;

    public AttendanceReportAdapter(ArrayList<AttendanceReportList> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public ARHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.emp_wise_att_report_list_view, parent, false);
        return new ARHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ARHolder holder, int position) {
        AttendanceReportList aRtl = mCategoryItem.get(position);

        holder.date.setText(aRtl.getDate());
        holder.shift.setText(aRtl.getShift());
        holder.punch.setText(aRtl.getPunchLoc());
        holder.inTime.setText(aRtl.getInTime());

        String ininini = aRtl.getInStatus();
        String outtttt = aRtl.getOutStatus();

        if (ininini.isEmpty() && outtttt.isEmpty()) {
            holder.status.setText(aRtl.getStatus());
        } else if (ininini.isEmpty()) {
            String sss = aRtl.getStatus() + "\n(" + outtttt + ")";
            holder.status.setText(sss);
        } else if (outtttt.isEmpty()) {
            String sss = aRtl.getStatus() + "\n(" + aRtl.getInStatus() + ")";
            holder.status.setText(sss);
        } else {
            String sss = aRtl.getStatus() + "\n(" + aRtl.getInStatus() + ")" + "\n(" + aRtl.getOutStatus() + ")";
            holder.status.setText(sss);
        }

        holder.out.setText(aRtl.getOutTime());

        String attStatus = aRtl.getAttStatusColor();

        switch (attStatus) {
            case "Multi Station":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#C0C000"));
                holder.status.setTextColor(myContext.getColor(R.color.black));
                break;
            case "Out Miss":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#00C080"));
                holder.status.setTextColor(myContext.getColor(R.color.white));
                break;
            case "In Leave":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#8F8F8F"));
                holder.status.setTextColor(myContext.getColor(R.color.white));
                break;
            case "Off Day":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#7FDFFE"));
                break;
            case "Present on Leave Day":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#4080C0"));
                holder.status.setTextColor(myContext.getColor(R.color.white));
                break;
            case "Present on Off Day":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#E0C080"));
                holder.status.setTextColor(myContext.getColor(R.color.black));
                break;
            case "Absent":
                holder.linearLayout.setBackgroundColor(Color.parseColor("#C000FF"));
                holder.status.setTextColor(myContext.getColor(R.color.white));
                break;
            case "White":
                holder.linearLayout.setBackgroundColor(Color.WHITE);
                holder.status.setTextColor(myContext.getColor(R.color.black));
                break;
            default:
                holder.linearLayout.setBackgroundColor(Color.WHITE);
                holder.status.setTextColor(myContext.getColor(R.color.black));
                break;
        }

        if (aRtl.getDate().equals("Date")) {
            holder.date.setBackgroundColor(Color.parseColor("#ced6e0"));
        }
        else {
            holder.date.setBackgroundColor(Color.WHITE);
        }
        if (aRtl.getShift().equals("Shift")) {
            holder.shift.setBackgroundColor(Color.parseColor("#ced6e0"));
        }
        else {
            holder.shift.setBackgroundColor(Color.WHITE);
        }
        if (aRtl.getPunchLoc().equals("Punch Location")) {
            holder.punch.setBackgroundColor(Color.parseColor("#ced6e0"));
        }
        else {
            holder.punch.setBackgroundColor(Color.WHITE);
        }
        if (aRtl.getInTime().equals("In Time")) {
            holder.inTime.setBackgroundColor(Color.parseColor("#ced6e0"));
        }
        else {
            holder.inTime.setBackgroundColor(Color.WHITE);
        }
        if (aRtl.getOutTime().equals("Out Time")) {
            holder.out.setBackgroundColor(Color.parseColor("#ced6e0"));
        }
        else {
            holder.out.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class ARHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView status;
        TextView shift;
        TextView punch;
        TextView inTime;
        TextView out;
        LinearLayout linearLayout;

        public ARHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_0f_att);
            shift = itemView.findViewById(R.id.shift_of_att);
            status = itemView.findViewById(R.id.status_of_att);
            punch = itemView.findViewById(R.id.punch_loc_of_att);
            inTime = itemView.findViewById(R.id.in_time_of_att);
            out = itemView.findViewById(R.id.out_time_of_att);
            linearLayout = itemView.findViewById(R.id.status_of_att_lay);

        }
    }
}
