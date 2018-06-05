package com.wizdanapril.assistantbag.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.models.History;
import com.wizdanapril.assistantbag.utils.Constant;

import java.util.List;
import java.util.Map;

public class HistoryAdapter extends  RecyclerView.Adapter<HistoryAdapter.ViewHolder>  {

    private List<History> historyList;
    private Context context;

    private DatabaseReference catalogReference = FirebaseDatabase.getInstance()
            .getReference(Constant.USER).child(Constant.CATALOG);

    public HistoryAdapter(List<History> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;

    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final History history = historyList.get(position);

        holder.tagDate.setText(history.date);
        holder.tagTime.setText(history.time);

        for (Map.Entry<String, Boolean> entry : history.reference.entrySet()) {
            String key = entry.getKey();
            holder.tagId.setText(key);

            catalogReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Catalog catalog = dataSnapshot.getValue(Catalog.class);
                        String name = catalog != null ? catalog.name : null;
                        holder.tagName.setText(name);
                    } else {
                        holder.tagName.setText(context.getResources().getString(R.string.no_name));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        switch (history.status) {
            case "in":
                holder.statusIn.setVisibility(View.VISIBLE);
                holder.statusOut.setVisibility(View.INVISIBLE);
                holder.tagTime.setTextColor(context.getResources().getColor(R.color.material_green));
                holder.tagDate.setTextColor(context.getResources().getColor(R.color.material_green));
                holder.dotTime.setBackground(context.getResources().getDrawable(R.drawable.circle_green));
                break;
            case "out":
                holder.statusIn.setVisibility(View.INVISIBLE);
                holder.statusOut.setVisibility(View.VISIBLE);
                holder.tagTime.setTextColor(context.getResources().getColor(R.color.material_red));
                holder.tagDate.setTextColor(context.getResources().getColor(R.color.material_red));
                holder.dotTime.setBackground(context.getResources().getDrawable(R.drawable.circle_red));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tagName;
        TextView tagId;
        TextView tagDate;
        TextView tagTime;
        ImageView statusIn;
        ImageView statusOut;
        View dotTime;

        private ViewHolder(View itemView) {
            super(itemView);

            tagName = (TextView) itemView.findViewById(R.id.tv_name);
            tagId = (TextView) itemView.findViewById(R.id.tv_id);
            tagDate = (TextView) itemView.findViewById(R.id.tv_date);
            tagTime = (TextView) itemView.findViewById(R.id.tv_time);
            statusIn = (ImageView) itemView.findViewById(R.id.iv_status_in);
            statusOut = (ImageView) itemView.findViewById(R.id.iv_status_out);
            dotTime = itemView.findViewById(R.id.vi_dot);
        }
    }
}