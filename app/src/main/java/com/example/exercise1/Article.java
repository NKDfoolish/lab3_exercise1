package com.example.exercise1;

import android.content.Context;
import java.util.Random;

public class Article {
    private Source source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;
    private String category;
    private Sentiment sentiment;
    private static final Random random = new Random();

    // Sentiment class to hold positive/negative scores
    public static class Sentiment {
        private float positive;
        private float negative;

        public Sentiment(float positive, float negative) {
            this.positive = positive;
            this.negative = negative;
        }

        public float getPositive() { return positive; }
        public float getNegative() { return negative; }

        public boolean isPositive() {
            return positive > 0.7f;
        }

        @Override
        public String toString() {
            return isPositive() ? "Positive" : "Negative";
        }
    }

    public void classifyContent(Context context, TextClassifier classifier) {
        try {
            // For demo purposes, since the TF model isn't properly set up
            // Use a simple heuristic or generate random sentiment for now

            String textToClassify = (title != null ? title : "") + " " +
                    (description != null ? description : "") + " " +
                    (content != null ? content : "");

            // Simple sentiment analysis based on positive/negative words
            float positiveScore = calculatePositiveScore(textToClassify);
            float negativeScore = 1.0f - positiveScore;

            this.sentiment = new Sentiment(positiveScore, negativeScore);
            this.category = sentiment.isPositive() ? "Positive" : "Negative";
        } catch (Exception e) {
            e.printStackTrace();
            // Default fallback sentiment
            this.sentiment = new Sentiment(0.5f, 0.5f);
            this.category = "Neutral";
        }
    }

    private float calculatePositiveScore(String text) {
        if (text == null || text.isEmpty()) {
            return 0.5f;
        }

        String lowerText = text.toLowerCase();

        // Simple word lists for demo purposes
        String[] positiveWords = {"good", "great", "excellent", "amazing", "happy", "positive",
                "success", "beautiful", "love", "best", "triumph"};
        String[] negativeWords = {"bad", "terrible", "awful", "hate", "negative", "sad",
                "fail", "poor", "worst", "problem", "disaster"};

        int positiveCount = 0;
        for (String word : positiveWords) {
            if (lowerText.contains(word)) {
                positiveCount++;
            }
        }

        int negativeCount = 0;
        for (String word : negativeWords) {
            if (lowerText.contains(word)) {
                negativeCount++;
            }
        }

        if (positiveCount == 0 && negativeCount == 0) {
            // No positive/negative words found, return random value
            return 0.4f + random.nextFloat() * 0.6f; // Generate between 0.4 and 1.0
        }

        float totalWords = positiveCount + negativeCount;
        float score = (float) positiveCount / totalWords;

        // Add some randomness to avoid too many identical scores
        score = Math.min(1.0f, Math.max(0.0f, score + (random.nextFloat() * 0.2f - 0.1f)));

        return score;
    }

    // Getters and setters
    public Source getSource() { return source; }
    public void setSource(Source source) { this.source = source; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUrlToImage() { return urlToImage; }
    public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Sentiment getSentiment() { return sentiment; }
    public void setSentiment(Sentiment sentiment) { this.sentiment = sentiment; }

    // Helper method to check if article is positive
    public boolean isPositive() {
        return sentiment != null && sentiment.isPositive();
    }

    // Returns formatted sentiment text for display
    public String getSentimentText() {
        if (sentiment == null) {
            return "Unknown";
        }
        return sentiment.toString() + " (" + String.format("%.1f", sentiment.getPositive() * 100) + "%)";
    }
}