package com.johnbohne.okcupid;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John Bohne on 1/15/16.
 *
 * This fragment holds the grid view of matches.
 */
public class MainActivityFragment extends Fragment{

    private static final String URL = "http://www.okcupid.com/matchSample.json";
    private static final String TAG = "MainActivityFragment";
    private Context mContext;
    private GridView mGridView;
    private AsyncTask<Void, Void, List<Person>> mAsyncTask;
    private View mRootView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mContext = this.getContext();
        mRootView = rootView;
        mGridView = (GridView) rootView.findViewById(R.id.grid_view);
        //similar to how the real app works on orientation change
        checkConfiguration();
        return rootView;
    }
    private class GetPersonInformation extends AsyncTask<Void, Void, List<Person>> {
        @Override
        protected List<Person> doInBackground(Void... params) {
            try {
                return getPeople();
            } catch (IOException e) {
                Log.e("GetPersonInformation", "IOException in doInBackground");
                //cancel asynctask
                cancel(true);
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Person> people) {
            RelativeLayout progressLayout = (RelativeLayout) mRootView.findViewById(R.id.progress_layout);
            progressLayout.setVisibility(View.GONE);
            if (people != null) {
                ItemAdapter itemAdapter = new ItemAdapter(mContext, R.id.grid_view, people);
                mGridView.setAdapter(itemAdapter);
                mGridView.setVisibility(View.VISIBLE);
            } else {
                mGridView.setEmptyView(mRootView.findViewById(android.R.id.empty));
            }
        }
        @Override
        protected void onCancelled(List<Person> people) {
            mGridView.setEmptyView(mRootView.findViewById(android.R.id.empty));
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        checkConfiguration();
        //onDestroy() not called when doing configChanges so putting the below here
        AsyncTask.Status status = mAsyncTask.getStatus();
        if (status == AsyncTask.Status.PENDING || status == AsyncTask.Status.RUNNING) {
            mAsyncTask.cancel(true);
        }
    }
    public void checkConfiguration() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridView.setNumColumns(3);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridView.setNumColumns(2);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

        mAsyncTask = new GetPersonInformation();
        mAsyncTask.execute();
    }
    public List<Person> getPeople() throws IOException {
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Create a new HTTP connection to download JSON
            HttpURLConnection urlConnection = null;
            String result = null;
            int resCode;
            try {
                java.net.URL url = new URL(URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setInstanceFollowRedirects(true);

                resCode = urlConnection.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    result = sb.toString();
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Malformed URL");
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            } finally {
                if (urlConnection != null) {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e) {
                        Log.e(TAG, getResources().getString(R.string.disconnect_error));
                    }
                }
            }
            if (result == null) {
                mGridView.setEmptyView(mRootView.findViewById(android.R.id.empty));
                return null;
            }
            return readJSON(result);
        }
        return null;
    }
    public List<Person> readJSON(String response) {
        try {
            JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray jsondataArray = json.getJSONArray("data");
            List<Person> people = new ArrayList<Person>();
            int jsondataArrayLength = jsondataArray.length();
            String imageURL = null;
            String city = null;
            String state = null;
            String userName = null;
            int matchPercentage = 0;
            int age = 0;

            for (int i = 0; i < jsondataArrayLength; i++) {
                JSONObject o = jsondataArray.getJSONObject(i);
                if (o.optJSONObject("location") != null) {
                    JSONObject location = o.getJSONObject("location");
                    if (!location.optString("city_name").equals("")) {
                        city = location.getString("city_name");
                    }
                    if (!location.optString("state_code").equals("")) {
                        state = location.getString("state_code");
                    }
                }
                if (o.optInt("match") != 0) {
                    matchPercentage = o.getInt("match");
                }
                if (o.optInt("age") != 0) {
                    age = o.getInt("age");
                }
                if (!o.optString("username").equals("")) {
                    userName = o.getString("username");
                }
                if (o.optJSONObject("photo") != null) {
                    JSONObject photos = o.getJSONObject("photo");
                    if (photos.optJSONObject("thumb_paths") != null) {
                        JSONObject thumbPaths = photos.getJSONObject("thumb_paths");
                        if (!thumbPaths.optString("large").equals("")) {
                            imageURL = thumbPaths.getString("large");
                        }
                    }
                }
                people.add(new Person(userName, imageURL, age, city, state, matchPercentage));
                imageURL = null;
                city = null;
                state = null;
                matchPercentage = 0;
                userName = null;
                age = 0;
            }
            return people;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
