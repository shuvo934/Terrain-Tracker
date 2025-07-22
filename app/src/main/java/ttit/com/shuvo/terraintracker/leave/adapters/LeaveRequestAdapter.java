package ttit.com.shuvo.terraintracker.leave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.leave.arraylists.LeaveRequestList;

public class LeaveRequestAdapter extends RecyclerView.Adapter<LeaveRequestAdapter.LRAHolder> {
    private ArrayList<LeaveRequestList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public LeaveRequestAdapter(Context myContext, ClickedItem myClickedItem, ArrayList<LeaveRequestList> mCategoryItem) {
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
        this.mCategoryItem = mCategoryItem;
    }

    @NonNull
    @Override
    public LRAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.att_up_req_app_details, parent, false);
        return new LRAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull LRAHolder holder, int position) {
        LeaveRequestList leaveRequestList = mCategoryItem.get(position);
        holder.applicationCode.setText(leaveRequestList.getLa_app_code());
        holder.empName.setText(leaveRequestList.getEmp_name());
        holder.empCode.setText(leaveRequestList.getEmp_code());
        holder.appDate.setText(leaveRequestList.getLa_date());
        holder.leaveDays.setText(leaveRequestList.getLa_leave_days());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class LRAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView applicationCode;
        TextView empName;
        TextView empCode;
        TextView appDate;
        TextView leaveDays;
        ClickedItem mClickedItem;
        public LRAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            applicationCode = itemView.findViewById(R.id.prescription_code_search_payment);
            empName = itemView.findViewById(R.id.patient_name_search_payment);
            empCode = itemView.findViewById(R.id.patient_code_search_payment);
            appDate = itemView.findViewById(R.id.patient_phone_search_payment);
            leaveDays = itemView.findViewById(R.id.patient_date_search_payment);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onItemClicked(getAdapterPosition());
        }
    }

    public interface ClickedItem {
        void onItemClicked(int Position);
    }

    public void filterList(ArrayList<LeaveRequestList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
