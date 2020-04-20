package com.example.mobCW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

/**
 * @author Merin Haslacher, S1624420
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AbstractFragment currentFragment;
    private String currentRoadworksURL = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String plannedRoadworksURL = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String currentIncidentsURL = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private DataTask dataTask;


    /**
     * Called when activity is created. Sets up toolbar and navigation drawer. Loads previous fragment if it exists
     * @param savedInstanceState Contains information from previous state if there is one
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = findViewById(R.id.toolbarTitle);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Checks if previous fragment exists and loads the appropriate fragment
        if(savedInstanceState == null) {
            toolbarTitle.setText("List View");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment = new ListFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_list);
        }else{
            currentFragment = (AbstractFragment) getSupportFragmentManager().getFragment(savedInstanceState, "myFragment");

            if(currentFragment instanceof ListFragment){
                toolbarTitle.setText("List View");
                navigationView.setCheckedItem(R.id.nav_list);
            }
            else if(currentFragment instanceof MapFragment){
                toolbarTitle.setText("Map View");
                navigationView.setCheckedItem(R.id.nav_map);
            }
        }

        dataTask = new DataTask();
        dataTask.execute();

    }

    /**
     * Called when an item in the navigation drawer is selected
     * @param item The item which is selected by the user
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_list:
                toolbarTitle.setText("List View");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,currentFragment = new ListFragment()).commit();
                break;
            case R.id.nav_map:
                toolbarTitle.setText("Map View");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,currentFragment = new MapFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Closes the drawer if back button is pressed while it is open
     */
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    /**
     * Cancels asyncTask if activity is exited to prevent unnecessary usage of cellular data
     */
    @Override
    protected void onPause() {
        super.onPause();
        dataTask.cancel(true);
    }

    /**
     * Restarts the loading of data when activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(dataTask.getStatus() == AsyncTask.Status.FINISHED){
            dataTask = new DataTask();
            dataTask.execute();
        }
    }

    /**
     * Saves the fragment that is currently selected.
     * @param outState Used to save current fragment
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragment", currentFragment);
    }


    /**
     * This class is used to load data regularly, using AsyncTask
     */
    public class DataTask extends AsyncTask<Void,String,Void> {

        private boolean connected = false;
        String[] urls = {currentIncidentsURL, currentRoadworksURL, plannedRoadworksURL};

        /**
         * What is called when the task is in progress. Is completely separate from UI thread
         */
        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()) {//loop that loads data again if it isn't canceled
                publishProgress("loading");//calls onProgressUpdate, to notify user that the info is loading
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";
                String currentI = "", currentR = "", plannedR = "";
                boolean error = false;
                for (String url : urls) {
                    //Checks for internet connection
                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                        connected = true;
                    } else {
                        connected = false;
                    }
                    if (connected) {
                        try {
                            String response = "";
                            aurl = new URL(url);
                            yc = aurl.openConnection();
                            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                            in.readLine();
                            while ((inputLine = in.readLine()) != null) {//reads data as string and adds it to response variable
                                response = response + inputLine;
                            }
                            in.close();
                            //Updates data
                            if (url.equals(currentIncidentsURL))
                                currentI = response;
                            else if (url.equals(currentRoadworksURL))
                                currentR = response;
                            else if (url.equals(plannedRoadworksURL))
                                plannedR = response;
                        } catch (IOException ae) {
                            if(isCancelled())
                                return null;
                            publishProgress("search error");
                            error = true;
                        }
                    } else {
                        publishProgress("search error");
                        error = true;
                    }
                    if(isCancelled()) //Check if cancel() was called
                        return null;
                }
                if (!error)
                    publishProgress(currentI, currentR, plannedR); //publishes data, calling onProgressUpdate
                try {
                    Thread.sleep(30000); //Pauses thread for 30 seconds, so data is updated every 30 seconds
                } catch (InterruptedException e) {
                    if(isCancelled())
                        return null;
                }
            }
            return null;
        }

        /**
         * Called when publishProgress() is called in doInBackground. Used to update UI
         * @param response Data that is passed to publishProgress(). Contains info on progress or the xml data
         */
        @Override
        protected void onProgressUpdate(String... response) {
            if (response[0].equals("search error")){
                currentFragment.uiErrorLoading();
            }
            else if (response[0].equals("loading")) {
                currentFragment.uiLoading();
            }
            else if (response.length == 3) {//When data is loaded info from three urls are provided as 3 parameters
                currentFragment.parseIncidents(response[0]);
                currentFragment.parseRoadworks(response[1], currentRoadworksURL);
                currentFragment.parseRoadworks(response[2], plannedRoadworksURL);
                currentFragment.uiFinishLoading();

            }
            else{
                currentFragment.uiErrorLoading();
            }
        }

    }


}



