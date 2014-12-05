package com.test.scanPictures;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {
	/**
	 * Called when the activity is first created.
	 */
	public static final String FOLDER_SD = Environment.getExternalStorageDirectory().getPath();
	public static SysMediaStore mSysMediaStore;
	public static Handler mHandler;
	private ListView list;
	protected ArrayAdapter<String> mAdapter;
	protected ArrayList<String> images;
	private ScanFolderTask mScanTask;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//mHandler = new Handler();
		mSysMediaStore = new SysMediaStore(this);

		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, images);
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(mAdapter);
		final Button btn = (Button) findViewById(R.id.button);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				images = new ArrayList<String>(64);
				mScanTask = new ScanFolderTask();
				mScanTask.execute();
			}
		});
	}
	class ScanFolderTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Log.d("Main", "rescanFolder");
			mSysMediaStore.rescanFolder(FOLDER_SD, images);
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
