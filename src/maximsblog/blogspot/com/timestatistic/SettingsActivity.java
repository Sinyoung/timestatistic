package maximsblog.blogspot.com.timestatistic;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

public class SettingsActivity extends SherlockPreferenceActivity implements
		OnPreferenceChangeListener {

	
	
	public static final String STARTTIMEFILTER = "startdatetimefilter";
	public static final String ENDTIMEFILTER = "enddatetimefilter";

	public static final String STARTTIMEFILTERPERIOD = "startdatetimefilterperiod";

	public static final String ENDTIMEFILTERPERIOD = "enddatetimefilterperiod";

	public static final String ENDTIMEFILTEREXPORT = "enddatetimefilterexport";
	public static final String STARTTIMEFILTEREXPORT = "startdatetimefilterexport";
	
	public static final String ENDTIMEFILTEREXPORTCSV = "enddatetimefilterexportcsv";
	public static final String STARTTIMEFILTEREXPORTCSV = "startdatetimefilterexportcsv";
	
	public static final class STARTTIMEFILTERS
	{
		public static final int TODAY = 0;
		public static final int YESTERDAY = 1;
		public static final int WEEK = 2;
		public static final int MOUNTH = 3;
		public static final int YEAR = 4;
		public static final int ALLTIME = 5;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		Preference p = findPreference("visible_notif");
		p.setOnPreferenceChangeListener(this);
		p = findPreference("alarm");
		p.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals("alarm")) {
			if ((Boolean) newValue) {
				app.setRunningCounterAlarmSettings(getApplicationContext());
			} else {
				app.delAlarm(getApplicationContext());
			}
			 Editor e = preference.getEditor();
			 e.putBoolean("alarm", (Boolean)newValue);
			 e.commit();
			 app.setStatusBar(this);
		} else {
			Editor e = preference.getEditor();
			e.putBoolean("visible_notif", (Boolean)newValue);
			e.commit();
			app.setStatusBar(this);
		}
		return true;
	}

}
