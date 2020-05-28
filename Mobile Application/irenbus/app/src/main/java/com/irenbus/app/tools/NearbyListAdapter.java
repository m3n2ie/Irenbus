package com.irenbus.app.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.irenbus.app.R;
import com.irenbus.app.model.Bus;
import com.irenbus.app.model.BusInfo;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NearbyListAdapter extends RecyclerView.Adapter<NearbyListAdapter.TempViewHolder> {

    private List<BusInfo> buses;

    public NearbyListAdapter(List<BusInfo> buses){
        this.buses = buses;
    }

    public static class TempViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView tvName, tvDistance, tvDuration;
        ImageView ivIcon;

        TempViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.NearbycardView);
            tvName = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDuration = (TextView) itemView.findViewById(R.id.tvDuration);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
        }
    }

    @NonNull
    @Override
    public TempViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_list_item, parent, false);
        TempViewHolder tvh = new TempViewHolder(view);
        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TempViewHolder holder, int position) {
        Set<BusInfo> set = new HashSet<>(buses);
        buses.clear();
        buses.addAll(set);
        if( Double.parseDouble( buses.get(position).getDistanceFromUser().split(" ")[0]) < 5.0 ){ //Only adds online buses within a 5km radius from user
            holder.tvName.setText(buses.get(position).getBusRoute());
            System.out.println("View Holder "+buses.get(position).getDistanceFromUser()+" away "+buses.get(position).getTimeFromUser()+" away");
            holder.tvDistance.setText(buses.get(position).getDistanceFromUser()+" away");
            holder.tvDuration.setText(buses.get(position).getTimeFromUser()+" away");
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return buses.size();
    }
}
