package ttit.com.shuvo.terraintracker.attendance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.attendance.araylists.AttendanceUpReqList;


public class AttUpReqAdapter extends RecyclerView.Adapter<AttUpReqAdapter.AURAHolder> {
    private ArrayList<AttendanceUpReqList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public AttUpReqAdapter(Context myContext, ClickedItem myClickedItem, ArrayList<AttendanceUpReqList> mCategoryItem) {
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
        this.mCategoryItem = mCategoryItem;
    }

    @NonNull
    @Override
    public AURAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.att_up_req_app_details, parent, false);
        return new AURAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AURAHolder holder, int position) {
        AttendanceUpReqList attendance = mCategoryItem.get(position);
        holder.requestCode.setText(attendance.getDarm_app_code());
        holder.empName.setText(attendance.getEmp_name());
        holder.empCode.setText(attendance.getEmp_code());
        holder.appDate.setText(attendance.getDarm_date());
        holder.updateDate.setText(attendance.getDarm_update_date());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class AURAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView requestCode;
        TextView empName;
        TextView empCode;
        TextView appDate;
        TextView updateDate;
        ClickedItem mClickedItem;

        public AURAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            requestCode = itemView.findViewById(R.id.prescription_code_search_payment);
            empName = itemView.findViewById(R.id.patient_name_search_payment);
            empCode = itemView.findViewById(R.id.patient_code_search_payment);
            appDate = itemView.findViewById(R.id.patient_phone_search_payment);
            updateDate = itemView.findViewById(R.id.patient_date_search_payment);
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

    public void filterList(ArrayList<AttendanceUpReqList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }

}
