package com.martinkondor.mooncalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Main activity with the calendar shown
 *
 */
public class CalendarActivity extends AppCompatActivity {

    // Stores the events per days
    private ArrayMap<Integer, ArrayList<Event>> eventsPerDays;
    private DatabaseManager db;

    // Visible elements
    private LinearLayout eventContainer;
    private LinearLayout dayNumbers1Layout;
    private ArrayList<TextView> dayNumbers1;
    private LinearLayout dayNumbers2Layout;
    private ArrayList<TextView> dayNumbers2;
    private LinearLayout dayNumbers3Layout;
    private ArrayList<TextView> dayNumbers3;
    private LinearLayout dayNumbers4Layout;
    private ArrayList<TextView> dayNumbers4;
    private LinearLayout dayNumbers5Layout;
    private ArrayList<TextView> dayNumbers5;
    private LinearLayout dayNumbers6Layout;
    private ArrayList<TextView> dayNumbers6;
    private TextView calendarTitle;
    private TextView eventDayName;
    private Button plusEventBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initialize();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rebuildUI();
    }

    private void rebuildUI() {
        fillCalendar();
        attachClickListeners();
    }

    private void initialize() {
        eventsPerDays = new ArrayMap<>();
        db = new DatabaseManager(this);

        eventContainer = (LinearLayout) findViewById(R.id.events_container);

        dayNumbers1Layout = (LinearLayout) findViewById(R.id.day_numbers_1);
        dayNumbers1 = Utils.getTextViewChildren(dayNumbers1Layout);

        dayNumbers2Layout = (LinearLayout) findViewById(R.id.day_numbers_2);
        dayNumbers2 = Utils.getTextViewChildren(dayNumbers2Layout);

        dayNumbers3Layout = (LinearLayout) findViewById(R.id.day_numbers_3);
        dayNumbers3 = Utils.getTextViewChildren(dayNumbers3Layout);

        dayNumbers4Layout = (LinearLayout) findViewById(R.id.day_numbers_4);
        dayNumbers4 = Utils.getTextViewChildren(dayNumbers4Layout);

        dayNumbers5Layout = (LinearLayout) findViewById(R.id.day_numbers_5);
        dayNumbers5 = Utils.getTextViewChildren(dayNumbers5Layout);

        dayNumbers6Layout = (LinearLayout) findViewById(R.id.day_numbers_6);
        dayNumbers6 = Utils.getTextViewChildren(dayNumbers6Layout);

        eventDayName = (TextView) findViewById(R.id.event_day_name);
        calendarTitle = findViewById(R.id.calendar_title);
        plusEventBtn = (Button) findViewById(R.id.plus_event_btn);

        fillCalendar();
        attachClickListeners();
    }

    /**
     * Set click listeners on days and buttons
     */
    private void attachClickListeners() {

        // Attach click listeners for all the days in the calendar
        for (TextView tv : getAllDayNumbers()) {

            // Skip non days
            if (tv.getText().length() == 0) {
                continue;
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setActiveDay(tv);
                }
            });
        }

        // Add an event button
        plusEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLastOpenedDay(getActiveDay());
                Intent intent = new Intent(
                        CalendarActivity.this,
                        CreateEventActivity.class
                );
                startActivity(intent);
            }
        });
    }

    private int getLastOpenedDay() {
        return SharedPreference.getSharedPreferenceInt(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_LAST_OPENED_DAY)
        );
    }

    private void saveLastOpenedDay(int lod) {
        SharedPreference.saveSharedPreference(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_LAST_OPENED_DAY),
                lod
        );
    }

    private void saveActiveDay(int day) {
        SharedPreference.saveSharedPreference(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_ACTIVE_DAY),
                day
        );
    }

    private int getActiveDay() {
        return SharedPreference.getSharedPreferenceInt(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_ACTIVE_DAY)
        );
    }

    private ArrayList<TextView> getAllDayNumbers() {
        ArrayList<TextView> dayNumbersAll = new ArrayList<>();
        dayNumbersAll.addAll(dayNumbers1);
        dayNumbersAll.addAll(dayNumbers2);
        dayNumbersAll.addAll(dayNumbers3);
        dayNumbersAll.addAll(dayNumbers4);
        dayNumbersAll.addAll(dayNumbers5);
        dayNumbersAll.addAll(dayNumbers6);
        return dayNumbersAll;
    }

    /**
     * Fills the calendar with the current month's days
     * shows the current day's events
     */
    @SuppressLint("NewApi")
    private void fillCalendar() {

        // Get the current date info
        LocalDate now = LocalDate.now();

        // Set the calendar title (in the format "YEAR. MONTH")
        String monthNameInHu = Utils.getMonthNameInHu(now.getMonthValue());
        calendarTitle.setText(
                String.valueOf(now.getYear())
                        .concat(". ")
                        .concat(monthNameInHu)
        );

        clearDays();
        int monthStartDay = Utils.getStartDayOfMonth(now);
        int numberOfDays = Utils.getNumberOfDaysForMonth(now.getYear(), now.getMonthValue());
        fillDays(numberOfDays, monthStartDay);
        loadEvents(now.getYear(), now.getMonthValue(), numberOfDays);

        //int lod = getLastOpenedDay();
        TextView ad = calcActiveDay(now.getDayOfMonth() + monthStartDay);
        setActiveDay(ad);

        // Save current year and month
        SharedPreference.saveSharedPreference(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_CURRENT_YEAR),
                now.getYear());
        SharedPreference.saveSharedPreference(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_CURRENT_MONTH),
                now.getMonthValue());
    }

    private void showEvents(int day) {

        // Save the last opened day for the CreateEventActivity
        SharedPreference.saveSharedPreference(
                CalendarActivity.this,
                getResources().getString(R.string.SPNAME_LAST_OPENED_DAY),
                day);

        // Get the events saved for this day
        if (!eventsPerDays.containsKey(day)) {
            eventsPerDays.put(day, new ArrayList<>());
        }
        ArrayList<Event> events = eventsPerDays.get(day);

        // Remove previously shown events
        eventContainer.removeAllViews();

        // events != null &&
        if (!events.isEmpty()) {

            // If the day has events then load them in
            for (Event e : events) {
                View.OnLongClickListener onLongClkList = getOnLongClickListenerForEvent(e);
                View.OnClickListener onClkList = getOnClickListenerForEvent(e);

                LinearLayout eventStore = getCurrentEventView(e, onLongClkList, onClkList);
                eventContainer.addView(eventStore);
            }
        }  // / if (!events.isEmpty())
    }

    private View.OnClickListener getOnClickListenerForEvent(Event e) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this, EditEventActivity.class);
                intent.putExtra("e.id", e.getId());
                intent.putExtra("e.title", e.getTitle());
                intent.putExtra("e.desc", e.getDesc());
                intent.putExtra("e.time", e.getTime());
                intent.putExtra("e.year", e.getYear());
                intent.putExtra("e.month", e.getMonth());
                intent.putExtra("e.day", e.getDay());
                startActivity(intent);
            }
        };
    }

    private View.OnLongClickListener getOnLongClickListenerForEvent(Event e) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                builder.setTitle("Esemény törlése");
                builder.setMessage(
                        "Biztos benne hogy törölni szeretné a(z) \"" +
                                e.getTitle() +
                                "\" nevű eseményt?"
                );

                builder.setPositiveButton("IGEN", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CalendarActivity.this.db.deleteEvent(e.getId());
                        CalendarActivity.this.rebuildUI();

                        Toast.makeText(
                                CalendarActivity.this,
                                "Az eseményt sikeresen töröltük!",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NEM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        };
    }

    /**
     * Set an event's view and return it as a LinearLayout
     * @return m
     */
    private LinearLayout getCurrentEventView(
            Event e, View.OnLongClickListener onLongClkList, View.OnClickListener onClkList) {

        // Create the rows for an event to be shown
        LinearLayout eventStore = new LinearLayout(this);
        eventStore.setClickable(true);
        eventStore.setOrientation(LinearLayout.VERTICAL);
        eventStore.setOnLongClickListener(onLongClkList);
        eventStore.setOnClickListener(onClkList);

        // The time row
        TextView timeView = new TextView(this);
        timeView.setText(e.getTime());
        timeView.setTextAppearance(R.style.event_time);
        timeView.setClickable(true);
        timeView.setOnLongClickListener(onLongClkList);
        timeView.setOnClickListener(onClkList);
        //timeView.setBackgroundColor(getResources().getColor(R.color.gray_200));
        timeView.setPadding(5, 10, 5, 10);
        timeView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // The title row
        TextView titleView = new TextView(this);
        titleView.setText(e.getTitle());
        titleView.setTextAppearance(R.style.event_title);
        titleView.setClickable(true);
        titleView.setOnLongClickListener(onLongClkList);
        titleView.setOnClickListener(onClkList);

        // The description row
        TextView descView = new TextView(this);
        descView.setText(e.getDesc());
        descView.setTextAppearance(R.style.event_desc);
        descView.setClickable(true);
        descView.setOnLongClickListener(onLongClkList);
        descView.setOnClickListener(onClkList);

        // A divider for "margin" between events
        View divider = new View(this);
        divider.setMinimumHeight(1);
        divider.setBackgroundColor(Color.GRAY);
        TextView dividerGap = new TextView(this);
        dividerGap.setTextSize(15);
        dividerGap.setText(" ");
        dividerGap.setBackgroundColor(Color.TRANSPARENT);

        // Add all the elements to the view
        eventStore.addView(timeView);
        eventStore.addView(titleView);
        eventStore.addView(descView);
        eventStore.addView(divider);
        eventStore.addView(dividerGap);
        return eventStore;
    }

    /**
     * Load events from the database into the eventsPerDays object
     * @param year y
     * @param month m
     * @param numberOfDays n
     */
    private void loadEvents(int year, int month, int numberOfDays) {
        db.loadEvents(eventsPerDays, year, month, numberOfDays);
    }

    /**
     * Fill day textviews in the calendar with their corresponding
     * day's text.
     * @param numberOfDays the number of days needed to be filled
     * @param startDayInWeekIndex the starting day's index
     * @return m
     */
    private boolean fillDays(int numberOfDays, int startDayInWeekIndex) {
        int dayCounter = 1;
        int innerCounter = 1;

        for (int i = 0; i < dayNumbers1.size(); i++) {
            if (innerCounter++ < startDayInWeekIndex+1) {
                continue;
            }

            dayNumbers1.get(i).setText(String.valueOf(dayCounter++));
            if (dayCounter == numberOfDays+1) return true;
        }
        for (int i = 0; i < dayNumbers2.size(); i++) {
            dayNumbers2.get(i).setText(String.valueOf(dayCounter++));
            if (dayCounter == numberOfDays+1) return true;
        }
        for (int i = 0; i < dayNumbers3.size(); i++) {
            dayNumbers3.get(i).setText(String.valueOf(dayCounter++));
            if (dayCounter == numberOfDays+1) return true;
        }
        for (int i = 0; i < dayNumbers4.size(); i++) {
            dayNumbers4.get(i).setText(String.valueOf(dayCounter++));
            if (dayCounter == numberOfDays+1) return true;
        }
        for (int i = 0; i < dayNumbers5.size(); i++) {
            dayNumbers5.get(i).setText(String.valueOf(dayCounter++));
            if (dayCounter == numberOfDays+1) return true;
        }
        for (int i = 0; i < dayNumbers6.size(); i++) {
            dayNumbers6.get(i).setText(String.valueOf(dayCounter++));
            if (dayCounter == numberOfDays+1) return true;
        }
        return true;
    }

    /**
     * Remove the current active day's style
     */
    private void removeActiveDay() {
        for (TextView tv : getAllDayNumbers()) {
            tv.setTextAppearance(R.style.day_number);
        }
    }

    /**
     * Sets the active day's style
     * @param day d
     */
    @SuppressLint("NewApi")
    private void setActiveDay(TextView day) {
        String dayS = day.getText().toString();

        removeActiveDay();
        day.setTextAppearance(R.style.day_number_active);
        eventDayName.setText(
                "Események "
                        .concat(dayS)
                        .concat(".:")
        );

        LocalDate now = LocalDate.now();
        loadEvents(
                now.getYear(),
                now.getMonthValue(),
                Utils.getNumberOfDaysForMonth(now.getYear(), now.getMonthValue())
        );
        showEvents(Integer.parseInt(day.getText().toString()));
        saveActiveDay(Integer.parseInt(dayS));
    }

    /**
     * Returns the dayNumbers that the given day sits in
     * @param day d
     * @return m
     */
    private ArrayList<TextView> getCurrentWeekDays(int day) {
        double div = day / 7.0;
        if (div <= 1.0) return dayNumbers1;
        if (div <= 2.0) return dayNumbers2;
        if (div <= 3.0) return dayNumbers3;
        if (div <= 4.0) return dayNumbers4;
        if (div <= 5.0) return dayNumbers5;
        return dayNumbers6;
    }

    /**
     * Calculates the active day's and returns the TextView
     * @param day d
     * @return m
     */
    private TextView calcActiveDay(int day) {
        int dayIndex = day % 7;
        if (dayIndex == 0) {
            dayIndex = 7;
        }
        return getCurrentWeekDays(day).get(dayIndex - 1);
    }

    /**
     * Remove all day's text from the calendar view
     */
    private void clearDays() {
        for (TextView tv : getAllDayNumbers()) {
            tv.setText("");
            tv.setTextAppearance(R.style.day_number);
        }
    }

}
