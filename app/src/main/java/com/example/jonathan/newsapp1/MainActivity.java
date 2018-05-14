package com.example.jonathan.newsapp1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {


    //URL for news from the Guardian API
    private static final String REQUEST_GUARDIAN_URL =
            "https://content.guardianapis.com/search?";

    //Constant value for the news loader ID. We can choose any integer.
    //this really only comes into play if you're using multiple loaders.
    private static final int NEWS_LOADER_ID = 1;

    //Adapter for the list of news articles
    private ArticleAdapter mAdapter;

    //A TextView that is displayed only when the parsed list is empty
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find List View by ID
        ListView articleListView = findViewById(R.id.list_view);

        //Find the Empty View for when there are no news articles to be displayed
        mEmptyStateTextView = findViewById(R.id.empty_text_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        // Adapter initialization with context and an empty Array List
        mAdapter = new ArticleAdapter(this, new ArrayList<News>());


        //Set the adapter on the {@link ListView}
        //so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);


        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current news article that was clicked on
                News currentArticle = mAdapter.getItem(position);

                //Convert the String URL into a URI object (to pass into the constructor)
                Uri articleUri = Uri.parse(currentArticle.getmArticleUrl());

                //Create a new intent to view the news article's URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                //Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //Get a reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //Get a reference to the LoaderManager, in order to interact with the Loaders
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader. Pass in the int ID constant defined above and pass in null
            //for the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            //because this activity implements the LoaderCallbacks Interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {

            //Otherwise display error
            //First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    //Loader methods
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBySection = sharedPrefs.getString(
                getString(R.string.settings_order_by_section_key),
                getString(R.string.settings_order_by_section_value));
//        String mostRecent = sharedPrefs.getString(
//                getString(R.string.settings_most_recent_key),
//                getString(R.string.settings_most_recent_default));
//        String orderBy = sharedPrefs.getString(
//                getString(R.string.settings_order_by_key),
//                getString(R.string.settings_order_by_default));


        Uri baseUri = Uri.parse(REQUEST_GUARDIAN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //Url is appended by query parameters and their values
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", "9b43d099-66a2-439f-8632-83c5740629ae");
        uriBuilder.appendQueryParameter("orderby", orderBySection);




        //Create a new loader for the given URL
        return new ArticleLoader(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> articles) {

        //Hide the progress bar because the data has been loaded
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles found"
        mEmptyStateTextView.setText(R.string.no_articles);

        //Clear the adapter of any previous news articles
        mAdapter.clear();

        //If there is a valid list of {@link News} then add them to the adapter
        //data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //Loader reset, so we can clear out our existing data
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    //This method passes the MenuItem that is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        //To determine which item was selected and what action to take we call getItemId
        int id = item.getItemId();
        //Match the Id to known menu items to perform the appropriate action.
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



