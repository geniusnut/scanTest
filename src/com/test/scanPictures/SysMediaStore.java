package com.test.scanPictures;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yw07 on 14-12-5.
 */
public class SysMediaStore {
	public static final Uri URI_BASE_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	public static final Uri URI_BASE_VIDEO = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

	private final ContentResolver mResolver;

	public static final String QUERY_LOW_DATA_LIKE = "LOWER(" + MediaStore.MediaColumns.DATA + ") LIKE ?";
	public static final String[] QUERY_SCANNER = {MediaStore.MEDIA_SCANNER_VOLUME};
	public static final String[] QUERY_BUCKET = {MediaStore.Images.Media.BUCKET_ID};
	public static final String[] QUERY_DATA = {MediaStore.MediaColumns.DATA};
	public static final String[] QUERY_ID = {MediaStore.MediaColumns._ID};
	public static final String[] QUERY_ID_DATA = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA};
	public static final String[] QUERY_INFO = {
			MediaStore.MediaColumns.DATE_MODIFIED,
			MediaStore.MediaColumns.SIZE,
			MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC,
			MediaStore.MediaColumns.MIME_TYPE
	};
	public static final Uri[] BASE_URIS = {URI_BASE_IMAGE, URI_BASE_VIDEO};

	public SysMediaStore(Context context) {
		mResolver = context.getContentResolver();

	/*	final File dir = new File(FOLDER_THUMBNAILS);
		if (!dir.exists())
			dir.mkdirs();*/
	}

	public static String pathLike(String path) {
		return Utilities.addSeparatorToPath(path).toLowerCase() + '%';
	}

	public int rescanFolder(String folder, final ArrayList<String> images) {
		int count = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
				&& !Utilities.pathIsHidden(folder)) {
			//	Can be restored only in 4.1+
			final Uri baseUri = MediaStore.Files.getContentUri("external");
			final String[] selection = {MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.MediaColumns.DATA};
			final String[] columns = {MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.MediaColumns.DATA};
			final String group = "1) GROUP BY (1";
			final String[] args = {pathLike(folder)};
			Cursor c = null;
			try {
				//c = mResolver.query(baseUri, columns, QUERY_LOW_DATA_LIKE, args, null);
				c = mResolver.query(URI_BASE_IMAGE, selection, group, null, null);
				if (c != null && c.moveToFirst()) {
					do {
						Log.d("SysMediaStore", "path is " + c.getString(1));
						images.add(c.getString(1));
						final int type = c.getInt(0);
						final String path;
						if (type == 0 && (path = c.getString(1)) != null) {
							//mScanner.request(path);
							count++;
						}
					} while (c.moveToNext());
				}
			} catch (Throwable e) {
				Log.e("SysMediaStore", "rescan: ", e);
			} finally {
				if (c != null)
					c.close();
			}
		}
		Log.d("SysMediaStore", "rescan: " + count);
		return count;
	}

}
