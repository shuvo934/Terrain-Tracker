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
import ttit.com.shuvo.terraintracker.leave.arraylists.LeaveBalanceList;

public class LeaveBalanceAdapter extends RecyclerView.Adapter<LeaveBalanceAdapter.LBAHolder> {

    public ArrayList<LeaveBalanceList> leaveBalanceLists;
    public Context myContext;

    public LeaveBalanceAdapter(ArrayList<LeaveBalanceList> leaveBalanceLists, Context myContext) {
        this.leaveBalanceLists = leaveBalanceLists;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public LBAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.leave_balance_list_view, parent, false);
        return new LBAHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LBAHolder holder, int position) {
        LeaveBalanceList aRtl = leaveBalanceLists.get(position);

        String c_c = aRtl.getCategory() + "("+aRtl.getCode()+")";
        holder.category.setText(c_c);
        holder.open.setText(aRtl.getOpening_qty());
        holder.earn.setText(aRtl.getCurrent_qty());
        holder.consumed.setText(aRtl.getConsumed());
        holder.transfer.setText(aRtl.getTransferred());
        holder.cash.setText(aRtl.getCash_taken());
        holder.balance.setText(aRtl.getBalance_qty());
    }

    @Override
    public int getItemCount() {
        return leaveBalanceLists != null ? leaveBalanceLists.size() : 0;
    }

    public static class LBAHolder extends RecyclerView.ViewHolder {

        TextView category;
        TextView open;
        TextView earn;
        TextView consumed;
        TextView transfer;
        TextView cash;
        TextView balance;

        public LBAHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.leave_category);
            open = itemView.findViewById(R.id.opening_qty);
            earn = itemView.findViewById(R.id.earn);
            consumed = itemView.findViewById(R.id.consumed);
            transfer = itemView.findViewById(R.id.transferred);
            cash = itemView.findViewById(R.id.cash_taken);
            balance = itemView.findViewById(R.id.balance_qty);

        }
    }
}
