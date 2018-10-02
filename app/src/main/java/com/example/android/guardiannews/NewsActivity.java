
package com.example.android.guardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    // Retrieve hidden API key
    String apiKey = BuildConfig.GUARDIAN_API_KEY;

    /**
     * URL for news stories data from The Guardian API: query is "science", most relevant since 01 May 2018
     */

    private String GUARDIAN_REQUEST_URL;

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news stories
     */
    private NewsAdapter newsAdapterObject;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView noDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Get the base URL from String resources in preparation of the API request
        GUARDIAN_REQUEST_URL = getString(R.string.GuardianBaseURL);

        // Pass the context
        NewsQueryUtils.myContext(this);

        // Find a reference to the ListView in the layout
        ListView newsListView = findViewById(R.id.list);

        noDataTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(noDataTextView);

        // Create a new adapter that takes an empty list of news as input
        newsAdapterObject = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the ListView so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapterObject);

        // Set an item click listener on the ListView, which sends an intent to a web browser to open a website with the full news story.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news story that was clicked on
                News currentNews = newsAdapterObject.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = null;
                if (currentNews != null) {
                    newsUri = Uri.parse(currentNews.getNewsUrl());
                }

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            noDataTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String searchTopic = sharedPrefs.getString(
                getString(R.string.settings_topic_key),
                getString(R.string.settings_topic_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.query), searchTopic);
        uriBuilder.appendQueryParameter(getString(R.string.showAuthor), getString(R.string.authorUrlValue));
        uriBuilder.appendQueryParameter(getString(R.string.newsOrder), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.newsApiKey), apiKey);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        noDataTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        newsAdapterObject.clear();

        // If there is a valid list of News, then add them to the adapter's data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapterObject.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        newsAdapterObject.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method sets up the action that occurs when any items in the Options Menu are selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}