package com.example.mobCW;

import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * Provides functionality for the map fragment.
 * @author Merin Haslacher, S1624420
 */
public class MapFragment extends AbstractFragment implements OnMapReadyCallback {
    private static final String TAG = "MapFragment";
    private ScrollView scrollView;
    private DatePickerDialog datePicker;
    private ViewSwitcher viewSwitcher;
    private Button startButton;
    private Button backButton;
    private Button changeDate;
    private LinearLayout searchPage;
    private CheckBox checkRoadworks;
    private CheckBox checkIncidents;
    private EditText searchText;
    private String selectedType = "";
    private int selectedIndex = 0;
    private TextView addInfo;
    private String displayString = "";
    private Calendar selectedDate = Calendar.getInstance();
    private GoogleMap map;


    /**
     * Called when view is being created. Specifies which layout file is to be used for fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map,container,false);
    }

    /**
     * Called when view creation process is completed. Sets up the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        View vieww = activity.getCurrentFocus(); //temp variable used to hide the keyboard when fragment is loaded
        if (vieww!=null){
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(vieww.getWindowToken(),0);
        }

        //Sets up the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        scrollView = view.findViewById(R.id.scrollView);

        searchPage = view.findViewById(R.id.searchPage);

        searchText = view.findViewById(R.id.searchText);

        //Updates the map when text is changed
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                display(selectedDate, checkRoadworks.isChecked(), checkIncidents.isChecked(), text);
            }
        });

        //Updates map when check buttons are checked/unchecked
        checkRoadworks = view.findViewById(R.id.roadworksCheck);
        checkRoadworks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                display(selectedDate,isChecked, checkIncidents.isChecked(), searchText.getText().toString());
            }
        });
        checkIncidents = view.findViewById(R.id.incidentsCheck);
        checkIncidents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                display(selectedDate,checkRoadworks.isChecked(), isChecked, searchText.getText().toString());
            }
        });

        Calendar date = Calendar.getInstance();
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);

        changeDate = view.findViewById(R.id.changeDate);
        changeDate.setText(day+"/"+month+"/"+year);

        //Shows DatePickerDialog once button is pressed and allows user to specify date, which updates button text
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        changeDate.setText(dayOfMonth+"/"+month+"/"+year);
                        selectedDate.set(year,month,dayOfMonth);
                        display(selectedDate,checkRoadworks.isChecked(), checkIncidents.isChecked(), searchText.getText().toString());
                    }
                },year,month,day);
                datePicker.show();
            }
        });

        addInfo = view.findViewById(R.id.addInfo);

        searchInfo = view.findViewById(R.id.searchInfo);
        loadingInfo = view.findViewById(R.id.loadingInfo);
        searchError = view.findViewById(R.id.searchError);

        viewSwitcher = view.findViewById(R.id.viewSwitcher);

        //Sets up backButton, which is displayed when a map marker is selected
        backButton = view.findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showPrevious();
            }
        });


    }

    /**
     * Called when map is ready. Sets map variable and displays data.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.864239,-4.251806), 10.0f));

        display(selectedDate,checkRoadworks.isChecked(),checkIncidents.isChecked(),searchText.getText().toString());

    }

    /**
     * Called when data is finished loading. Displays data.
     */
    @Override
    public void finishLoad(){
        display(selectedDate, checkRoadworks.isChecked(), checkIncidents.isChecked(), searchText.getText().toString());
    }

    /**
     * Displays roadworks and incidents on a map with markers.
     * @param date The date that was selected with the datepicker
     * @param checkRoadworks  If roadworks checkbox is checked
     * @param checkIncidents If incidents checkbox is checked
     * @param searchText Text that was put into EditText
     */
    public void display(Calendar date, boolean checkRoadworks, boolean checkIncidents, String searchText){
        if(map!=null)
            map.clear();
        else
            return;
        int markerCount = 0; //keeps track of number of items displayed

        if(checkRoadworks){
            if(Repository.getCurrentRoadworks()!=null)
                for (Roadwork r : Repository.getCurrentRoadworks()) {
                    if (!(date.before(r.getStartDate()) || date.after(r.getEndDate())) && r.getTitle().toUpperCase().contains(searchText.toUpperCase())) {
                        displayItem(r,"currentRoadworks",Repository.getCurrentRoadworks().indexOf(r));
                        markerCount++;
                    }
                }
            if(Repository.getPlannedRoadworks()!=null)
                for (Roadwork r : Repository.getPlannedRoadworks()) {
                    if (!(date.before(r.getStartDate()) || date.after(r.getEndDate()))&& r.getTitle().toUpperCase().contains(searchText.toUpperCase())) {
                        displayItem(r,"plannedRoadworks",Repository.getPlannedRoadworks().indexOf(r));
                        markerCount++;
                    }
                }
        }
        if(checkIncidents){
            if(Repository.getIncidents()!=null)
                for (Incident i : Repository.getIncidents()) {
                    if ((date.get(Calendar.DAY_OF_YEAR) == i.getPubDate().get(Calendar.DAY_OF_YEAR) && date.get(Calendar.YEAR) == i.getPubDate().get(Calendar.YEAR)) && i.getTitle().toUpperCase().contains(searchText.toUpperCase())) {
                        displayItem(i,"incidents",Repository.getIncidents().indexOf(i));
                        markerCount++;
                    }
                }
        }

        if(markerCount==0){
            searchInfo.setText("No results found.");
        }
        else{
            searchInfo.setText("");
        }


    }


    /**
     * Displays individual item on map. Called by display().
     * @param i The item to be displayed. Can be incident or roadwork
     * @param type  What the source for the item was. Current/planned roadwork or incident
     * @param index Used to uniquely identify the item
     */
    public <T extends Item> void displayItem(final T i, final String type, final int index){
        //Adds marker with appropriate image
        if(i instanceof Roadwork) {
            map.addMarker(new MarkerOptions().position(i.getCoords()).title(i.getTitle()).icon(createBitmap(R.drawable.ic_roadwork))).setTag(i);
        }
        else if(i instanceof Incident){
            map.addMarker(new MarkerOptions().position(i.getCoords()).title(i.getTitle()).icon(createBitmap(R.drawable.ic_incident))).setTag(i);
        }
        //Listener for when marker is clicked
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                displayString = marker.getTag().toString();
                addInfo.setText(marker.getTag().toString());
                viewSwitcher.showNext();
                selectedType = type;
                selectedIndex = index;
                return true;
            }
        });


    }

    /**
     * Creates bitmap from drawable so that roadworks and incidents can use images instead of default markers.
     * @param source Source for the image
     */
    private BitmapDescriptor createBitmap(int source) {
        Drawable drawable = ContextCompat.getDrawable(activity, source);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth()+20,drawable.getIntrinsicHeight()+20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Used to keep track of where the user is in viewswitcher and what information is being displayed.
     * Important for orientation change, as activity is destroyed and created again. This keeps track of the state of the application.
     * @param outState Used to save information about state so it can be loaded later.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int position = viewSwitcher.getDisplayedChild();
        outState.putInt("VIEW_SWITCHER", position);
        outState.putInt("CALENDAR_YEAR", selectedDate.get(Calendar.YEAR));
        outState.putInt("CALENDAR_MONTH", selectedDate.get(Calendar.MONTH));
        outState.putInt("CALENDAR_DAY",selectedDate.get(Calendar.DAY_OF_MONTH));
        outState.putString("DISPLAY_STRING",displayString);
    }

    /**
     * Called when activity is created. If there was a previous instance, update display to previous state.
     * @param savedInstanceState Contains information from previous state if previous state exists
     */
    @Override
    public void onActivityCreated(@NonNull Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null){
        }
        else{
            int switcherPosition = savedInstanceState.getInt("VIEW_SWITCHER");
            viewSwitcher.setDisplayedChild(switcherPosition);
            int calendarYear = savedInstanceState.getInt("CALENDAR_YEAR");
            int calendarMonth = savedInstanceState.getInt("CALENDAR_MONTH");
            int calendarDay = savedInstanceState.getInt("CALENDAR_DAY");
            changeDate.setText(calendarDay+"/"+calendarMonth+"/"+calendarYear);
            selectedDate.set(calendarYear,calendarMonth,calendarDay);
            displayString = savedInstanceState.getString("DISPLAY_STRING");
            addInfo.setText(savedInstanceState.getString("DISPLAY_STRING"));
        }

    }






}
