package com.example.realestateapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {

    public interface OnDocumentClickListener {
        void onDocumentClick(Document document);
    }

    private List<Document> documents;
    private OnDocumentClickListener listener;

    public DocumentsAdapter(List<Document> documents, OnDocumentClickListener listener) {
        this.documents = documents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        holder.bind(document, listener);
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, typeText, sizeText, dateText;
        private ImageView documentIcon;

        DocumentViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.documentTitle);
            typeText = itemView.findViewById(R.id.documentType);
            sizeText = itemView.findViewById(R.id.documentSize);
            dateText = itemView.findViewById(R.id.documentDate);
            documentIcon = itemView.findViewById(R.id.documentIcon);
        }

        void bind(Document document, OnDocumentClickListener listener) {
            titleText.setText(document.getTitle());
            typeText.setText(document.getType());
            sizeText.setText(document.getSize());
            dateText.setText(document.getDate());

            // Set icon based on document type
            if (document.getType().equalsIgnoreCase("PDF")) {
                documentIcon.setImageResource(R.drawable.ic_pdf);
            } else if (document.getType().equalsIgnoreCase("DOC") || document.getType().equalsIgnoreCase("DOCX")) {
                documentIcon.setImageResource(R.drawable.ic_doc);
            } else {
                documentIcon.setImageResource(R.drawable.ic_document);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDocumentClick(document);
                }
            });
        }
    }
}
