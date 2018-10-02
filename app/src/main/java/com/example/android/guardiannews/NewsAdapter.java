/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.guardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The NewsAdapter creates a list item layout for each news story in the data source
 **/
public class NewsAdapter extends ArrayAdapter<News> {


    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news    is the list of news, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about the news story at the given position in the list of news.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout
                .news_list_item, parent, false);

        // Find the news object at the given position in the list of news
        News currentNews = getItem(position);

        if (currentNews != null) {

            // Display the news story title in the appropriate TextView
            TextView newsTitleView = convertView.findViewById(R.id.text_title);

            newsTitleView.setText(currentNews.getNewsTitle());

            // Display the news story section in the appropriate TextView
            TextView newsSectionView = convertView.findViewById(R.id.text_section);

            newsSectionView.setText(currentNews.getNewsSection());

            // Display the news story contributor in the appropriate TextView
            TextView newsContributorView = convertView.findViewById(R.id.text_contributor);

            if (!currentNews.getNewsContributor().equals(""))
                newsContributorView.setText(currentNews.getNewsContributor());
            else
                newsContributorView.setText(R.string.no_contributor);

            //Find the TextView that will display the date
            TextView newsDateView = convertView.findViewById(R.id.text_date);

            // Find the TextView that will display the time
            TextView newsTimeView = convertView.findViewById(R.id.text_time);

            // Using the info from the below source to first convert the date string from the API to a Date object with my chosen format, then back
            // to a String so it can be displayed in the TextView. Same for the time of publication.
            // https://stackoverflow.com/questions/8573250/android-how-can-i-convert-string-to-date

            SimpleDateFormat stringDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat finalDateFormat = new SimpleDateFormat("EEE dd MMM", Locale.ENGLISH);

            try {
                Date initialDate = stringDate.parse(currentNews.getNewsDate());
                String finalDate = finalDateFormat.format(initialDate);
                newsDateView.setText(finalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat stringTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat finalTimeFormat = new SimpleDateFormat("HH mm", Locale.ENGLISH);

            try {
                Date initialTime = stringTime.parse(currentNews.getNewsDate());
                String finalTime = finalTimeFormat.format(initialTime);
                newsTimeView.setText(finalTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Return the list item view that is now showing the appropriate data
        return convertView;
    }
}