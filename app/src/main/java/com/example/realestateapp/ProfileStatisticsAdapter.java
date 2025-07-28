package com.example.realestateapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileStatisticsAdapter extends RecyclerView.Adapter<ProfileStatisticsAdapter.StatisticViewHolder> {

    private Context context;
    private List<ProfileStatistic> statistics;

    public ProfileStatisticsAdapter(Context context, List<ProfileStatistic> statistics) {
        this.context = context;
        this.statistics = statistics;
    }

    @NonNull
    @Override
    public StatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_profile_statistic, parent, false);
        return new StatisticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticViewHolder holder, int position) {
        ProfileStatistic statistic = statistics.get(position);

        holder.iconView.setImageResource(statistic.getIconResId());
        holder.valueText.setText(statistic.getValue());
        holder.labelText.setText(statistic.getLabel());

        // Apply tint to icon
        holder.iconView.setColorFilter(context.getResources().getColor(
                R.color.colorPrimary, context.getTheme()));
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }

    static class StatisticViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView valueText, labelText;

        StatisticViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.statisticIcon);
            valueText = itemView.findViewById(R.id.statisticValue);
            labelText = itemView.findViewById(R.id.statisticLabel);
        }
    }
}