package com.example.realestateapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {
    private List<FAQItem> faqItems;

    public FAQAdapter(List<FAQItem> faqItems) {
        this.faqItems = faqItems;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem item = faqItems.get(position);
        holder.questionText.setText(item.getQuestion());
        holder.answerText.setText(item.getAnswer());

        // Set visibility based on expanded state
        holder.answerText.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
        holder.expandIcon.setRotation(item.isExpanded() ? 180 : 0); // Rotate arrow based on state

        // Set click listener for question container
        holder.itemView.setOnClickListener(v -> {
            // Toggle expanded state
            boolean expanded = !item.isExpanded();
            item.setExpanded(expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    static class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, answerText;
        ImageView expandIcon;

        FAQViewHolder(View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            answerText = itemView.findViewById(R.id.answerText);
            expandIcon = itemView.findViewById(R.id.expandIcon);
        }
    }
}
