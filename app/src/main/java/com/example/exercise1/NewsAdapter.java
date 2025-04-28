package com.example.exercise1;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<Article> articles;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public NewsAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);

        if (article.getSource() != null) {
            holder.sourceName.setText(article.getSource().getName());
        } else {
            holder.sourceName.setText("Unknown Source");
        }

        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());

        // Format and set the publication date
        try {
            Date date = inputFormat.parse(article.getPublishedAt());
            holder.publishedAt.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.publishedAt.setText(article.getPublishedAt());
        }

        // Load image with Glide
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(article.getUrlToImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.articleImage);
        } else {
            holder.articleImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Display sentiment information
        holder.sentimentText.setText("Sentiment: " + article.getSentimentText());

        // Set sentiment text color based on positive/negative classification
        // Update the color setting with proper resource resolution:
        // Update in NewsAdapter.java
        if (article.isPositive()) {
            holder.sentimentText.setText("Positive");
            holder.sentimentText.getBackground().setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.positive),
                    PorterDuff.Mode.SRC_IN);
        } else {
            holder.sentimentText.setText("Negative");
            holder.sentimentText.getBackground().setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.negative),
                    PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView sourceName;
        TextView title;
        TextView description;
        TextView publishedAt;
        TextView sentimentText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.article_image);
            sourceName = itemView.findViewById(R.id.source_name);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            publishedAt = itemView.findViewById(R.id.published_at);
            sentimentText = itemView.findViewById(R.id.sentiment_text);
        }
    }
}