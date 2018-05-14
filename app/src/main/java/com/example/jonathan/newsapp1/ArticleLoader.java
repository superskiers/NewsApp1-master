package com.example.jonathan.newsapp1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<News>> {

        //Query URL
        private String mUrl;

        //Constructs a new {@link ArticleLoader}.
        //@param context of the activity
        //@param url to load data from
        public ArticleLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading(){
            super.onStartLoading();
            forceLoad();
        }


        //This is on a background thread.
        @Override
        public List<News> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            // Perform the network request, parse the response, and extract a list of news articles.
            List<News> articles = QueryUtils.fetchNewsItems(mUrl);
            return articles;
        }
    }

