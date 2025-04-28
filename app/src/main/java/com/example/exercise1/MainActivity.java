package com.example.exercise1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "67d9558044f7461ca4ad3c8a222d8c13";
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private TextClassifier textClassifier;
    private List<Article> allArticles = new ArrayList<>();
    private Button btnAll, btnPositive, btnNegative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TFLite classifier
        textClassifier = new TextClassifier(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize filter buttons
        btnAll = findViewById(R.id.btnAllArticles);
        btnPositive = findViewById(R.id.btnPositiveArticles);
        btnNegative = findViewById(R.id.btnNegativeArticles);

        setupFilterButtons();

        // Fetch news articles
        fetchNews();
    }

    private void setupFilterButtons() {
        btnAll.setOnClickListener(v -> showAllArticles());

        btnPositive.setOnClickListener(v -> {
            List<Article> positiveArticles = allArticles.stream()
                    .filter(Article::isPositive)
                    .collect(Collectors.toList());

            updateArticlesList(positiveArticles);
            Toast.makeText(this, "Showing positive articles", Toast.LENGTH_SHORT).show();
        });

        btnNegative.setOnClickListener(v -> {
            List<Article> negativeArticles = allArticles.stream()
                    .filter(article -> !article.isPositive())
                    .collect(Collectors.toList());

            updateArticlesList(negativeArticles);
            Toast.makeText(this, "Showing negative articles", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAllArticles() {
        updateArticlesList(allArticles);
        Toast.makeText(this, "Showing all articles", Toast.LENGTH_SHORT).show();
    }

    private void updateArticlesList(List<Article> articles) {
        adapter = new NewsAdapter(articles);
        recyclerView.setAdapter(adapter);
    }

    private void fetchNews() {
        NewsRepository repository = new NewsRepository();
        repository.getTopHeadlines("us", API_KEY).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Status: " + response.body().getStatus());
                    Log.d(TAG, "Total Results: " + response.body().getTotalResults());

                    allArticles = response.body().getArticles();

                    // Classify all articles
                    for (Article article : allArticles) {
                        article.classifyContent(MainActivity.this, textClassifier);
                    }

                    // Display all articles initially
                    showAllArticles();

                } else {
                    Log.e(TAG, "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failed to fetch news", Toast.LENGTH_SHORT).show();
            }
        });
    }
}