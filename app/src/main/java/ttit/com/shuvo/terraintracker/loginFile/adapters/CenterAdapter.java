package ttit.com.shuvo.terraintracker.loginFile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.CenterList;
import ttit.com.shuvo.terraintracker.R;

public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.CENTERHolder> {

    private final ArrayList<CenterList> mCategory;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public CenterAdapter(ArrayList<CenterList> mCategory, Context myContext, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public CENTERHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.center_list_details_view, parent, false);
        return new CENTERHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CENTERHolder holder, int position) {
        CenterList centerList = mCategory.get(position);
        holder.centerName.setText(centerList.getCenter_name());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class CENTERHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView centerName;
        ClickedItem mClickedItem;

        public CENTERHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            centerName = itemView.findViewById(R.id.data_center_name);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onCategoryClicked(getAdapterPosition());
        }
    }
    public interface ClickedItem {
        void onCategoryClicked(int CategoryPosition);
    }
}
