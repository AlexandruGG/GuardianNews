package com.example.android.guardiannews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


/**
 * Loads a list of news by using an AsyncTask to perform the network request to the given URL.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * Query URL
     */
    private String newsUrl;

    private Context newsContext;

    /**
     * Constructs a new NewsLoader.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        newsUrl = url;
        newsContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This runs on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (newsUrl == null) {
            return null;
        }

        // Pass the context
        NewsQueryUtils.myContext(newsContext);

        // Perform the network request, parse the response, and extract a list of news stories.
        return NewsQueryUtils.fetchNewsData(newsUrl);
    }
}