package ttit.com.shuvo.terraintracker.loginFile.dialoges;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.terraintracker.CenterList;
import ttit.com.shuvo.terraintracker.R;
import ttit.com.shuvo.terraintracker.loginFile.adapters.CenterAdapter;
import ttit.com.shuvo.terraintracker.loginFile.interfaces.CallBackListener;

public class SelectCenterDialogue extends AppCompatDialogFragment implements CenterAdapter.ClickedItem {
    RecyclerView centerView;
    CenterAdapter centerAdapter;
    RecyclerView.LayoutManager layoutManager;

    ImageView close;
    AlertDialog dialog;

    String centerAPI = "";
    String d_code = "";

    ArrayList<CenterList> centerLists;
    Context mContext;

    public SelectCenterDialogue(ArrayList<CenterList> centerLists, Context mContext) {
        this.centerLists = centerLists;
        this.mContext = mContext;
    }
    private CallBackListener callBackListener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof CallBackListener)
            callBackListener = (CallBackListener) getActivity();

        View view = inflater.inflate(R.layout.center_selectable_view, null);

        centerView = view.findViewById(R.id.center_list_view);
        close = view.findViewById(R.id.close_center_choice);

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        centerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        centerView.setLayoutManager(layoutManager);

        centerAdapter = new CenterAdapter(centerLists,mContext,this);
        centerView.setAdapter(centerAdapter);
        close.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {

        if(callBackListener != null)
            callBackListener.onDismiss(CategoryPosition);

        dialog.dismiss();
    }
}
