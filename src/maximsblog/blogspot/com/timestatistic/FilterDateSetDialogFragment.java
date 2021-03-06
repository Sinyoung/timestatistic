package maximsblog.blogspot.com.timestatistic;

import java.text.SimpleDateFormat;
import java.util.Date;

import maximsblog.blogspot.com.timestatistic.SplitRecordDialogFragment.CustomDateTimePickerFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FilterDateSetDialogFragment extends DialogFragment implements android.view.View.OnClickListener, IdateChange {

	private IRecordDialog mListener;
	private Button mStart;
	private Button mEnd;
	private Button mOk;
	private Button mCancel;

	private SimpleDateFormat mSimpleDateFormat;
	private long mSelectStartItem;
	private long mSelectEndItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mSelectStartItem = savedInstanceState.getLong("mSelectStartItem");
			mSelectEndItem = savedInstanceState.getLong("mSelectEndItem");
			FilterDialogFragment filterDialogFragment = (FilterDialogFragment) getActivity()
					.getSupportFragmentManager()
					.findFragmentByTag("filterPicker");
			if (filterDialogFragment != null) {
				filterDialogFragment.setDialogListener(this);
			}
		} else {
			mSelectStartItem = getArguments().getLong("start");
			mSelectEndItem = getArguments().getLong("stop");
		}

	};
	
	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putLong("mSelectStartItem", mSelectStartItem);
		arg0.putLong("mSelectEndItem", mSelectEndItem);
	};

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.filterdateset);
		View v = inflater.inflate(R.layout.fragment_filter_date_set_dialog,
				null);
		mStart = (Button) v.findViewById(R.id.start_Button);
		mEnd = (Button) v.findViewById(R.id.end_Button);
		mOk = (Button) v.findViewById(R.id.filter_ok);
		mCancel = (Button) v.findViewById(R.id.filter_cancel);
		mStart.setOnClickListener(this);
		mEnd.setOnClickListener(this);
		mOk.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		setValues();
		return v;
	};

	public void setDialogListener(IRecordDialog listener) {
		mListener = listener;
	}

	private void setValues() {

		Date startdate;
		int index;
		if (mSelectStartItem < 6) {
			startdate = new Date();
			index = (int)mSelectStartItem;
		} else {
			startdate = new Date(mSelectStartItem);
			index = 6;
		}
		String[] items = getResources().getStringArray(R.array.StartFilters);
		mSimpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
		items[6] = items[6] + " " + mSimpleDateFormat.format(startdate);
		mStart.setText(items[index]);
		
		items = getResources().getStringArray(R.array.EndFilters);
		
		Date enddate;
		if (mSelectEndItem < 6) {
			enddate = new Date();
			index = (int)mSelectEndItem;
		} else {
			enddate = new Date(mSelectEndItem);
			index = 6;
		}
		items[6] = items[6] + " " + mSimpleDateFormat.format(enddate);
		mEnd.setText(items[index]);
	}
	

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.filter_ok) {
			/*String setting = SettingsActivity.STARTTIMEFILTER;
			Editor editor = PreferenceManager.getDefaultSharedPreferences(
					FilterDateSetDialogFragment.this.getActivity()).edit();
			editor.putLong(setting, mSelectStartItem);
			setting = SettingsActivity.ENDTIMEFILTER;
			editor.putLong(setting, mSelectEndItem);
			editor.commit();
			FilterDateSetDialogFragment.this.dismiss();
			mListener.onRefreshFragmentsValue();*/
			FilterDateSetDialogFragment.this.dismiss();
			mListener.onFilterDateSet(mSelectStartItem, mSelectEndItem);
		} else if(v.getId() == R.id.filter_cancel) {
			FilterDateSetDialogFragment.this.dismiss();
		} else if(v.getId() == R.id.start_Button) {
			FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
			Bundle args = new Bundle();
			args.putBoolean("start", true);
			args.putLong("f", mSelectStartItem);
			filterDialogFragment.setArguments(args);
			filterDialogFragment.setDialogListener(this);
			filterDialogFragment.show(getActivity()
											.getSupportFragmentManager(),
											"filterPicker");
		} else if(v.getId() == R.id.end_Button) {
			FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
			Bundle args = new Bundle();
			args.putBoolean("start", false);
			args.putLong("f", mSelectEndItem);
			filterDialogFragment.setArguments(args);
			filterDialogFragment.setDialogListener(this);
			filterDialogFragment.show(getActivity()
											.getSupportFragmentManager(),
											"filterPicker");
		}
	}

	@Override
	public void timeChange(int id, long newvalue) {
		if(id==1) {
			mSelectStartItem = newvalue;
		} else {
			mSelectEndItem = newvalue;
		}
		setValues();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
	
}
