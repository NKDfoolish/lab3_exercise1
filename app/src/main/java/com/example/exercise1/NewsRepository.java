package com.example.exercise1;

import retrofit2.Call;

public class NewsRepository {
    private final NewsApiService apiService;

    public NewsRepository() {
        apiService = RetrofitInstance.getRetrofitInstance().create(NewsApiService.class);
    }

    public Call<NewsResponse> getTopHeadlines(String country, String apiKey) {
        return apiService.getTopHeadlines(country, apiKey);
    }
}
