package com.test.scanPictures;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Build;
import android.provider.Settings;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Pattern;

public class Utilities {
	private static final Random mRandom = new Random(System.nanoTime());

	public static int nextRandomInt(int max) {
		synchronized (mRandom) {
			return mRandom.nextInt(max);
		}
	}

	public static long nextRandomLong() {
		synchronized (mRandom) {
			return mRandom.nextLong();
		}
	}

	public static int stringToInteger(String str, int defVal) {
		try {
			return Integer.valueOf(str);
		} catch (Throwable e) {
		}
		return defVal;
	}

	public static long stringToLong(String str, long defVal) {
		try {
			return Long.valueOf(str);
		} catch (Throwable e) {
		}
		return defVal;
	}

	public static int getLinesOfString(String str) {
		if (str == null)
			return 0;
		int lines = 1;
		for (int pos = str.indexOf('\n'); pos != -1; pos = str.indexOf('\n', pos + 1))
			lines++;
		return lines;
	}

	public static String trimString(String str, char c) {
		int start = 0, end = str.length();
		while (start < end) {
			if (str.charAt(start) == c)
				start++;
			else
				break;
		}
		while (start < end) {
			if (str.charAt(end - 1) == c)
				end--;
			else
				break;
		}
		if (start < end)
			return str.substring(start, end);
		return "";
	}

	public static boolean fastEqualStrings(String str1, String str2) {
		if (str1 == str2)
			return true;
		else if (str1 == null || str2 == null)
			return false;
		else if (str1.length() != str2.length())
			return false;
		return str1.equals(str2);
	}

	@SuppressLint("DefaultLocale")
	public static String formatMediaTime(long timeMs) {
		final int totalSeconds = (int) ((timeMs + 500) / 1000);
		final int seconds = totalSeconds % 60;
		final int minutes = (totalSeconds / 60) % 60;
		final int hours = totalSeconds / 3600;
		if (hours > 0)
			return String.format("%d:%02d:%02d", hours, minutes, seconds);
		else
			return String.format("%02d:%02d", minutes, seconds);
	}

	public static String formatLatitudeLongitude(String format, double latitude, double longitude) {
		//	We need to specify the locale otherwise it may go wrong in some language
		// (e.g. Locale.FRENCH)
		return String.format(Locale.ENGLISH, format, latitude, longitude);
	}

	@SuppressLint("DefaultLocale")
	public static String formatResolution(int pixels) {
		float result = pixels;
		String suffix = "P";
		if (result > 900) {
			suffix = "KP";
			result = result / 1000;
		}
		if (result > 900) {
			suffix = "MP";
			result = result / 1000;
		}
		if (result > 900) {
			suffix = "GP";
			result = result / 1000;
		}
		String value;
		if (result < 1)
			value = String.format("%.2f", result);
		else if (result < 10)
			value = String.format("%.1f", result);
		else
			value = String.format("%.0f", result);
		return value + suffix;
	}

	public static String formatAddress(Address addr) {
		final String[] items = {
				addr.getAdminArea(),
				addr.getLocality(),
				addr.getThoroughfare(),
				addr.getFeatureName()
		};

		final StringBuilder sbuild = new StringBuilder(256);
		String strLast = "";
		for (String str : items) {
			if (str != null && !str.equals(strLast))
				sbuild.append(str).append(' ');
			strLast = str;
		}
		return sbuild.toString();
	}

	/*	public static String getBucketId(String path) {
			return Integer.toString(path.toLowerCase().hashCode());
		}
	*/

	public static boolean checkSignature(PackageInfo packageInfo, final byte[] digest1, final byte[] digest2) {
		try {
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			for (Signature signature : packageInfo.signatures) {
				md5.update(signature.toByteArray());
				final byte[] digest = md5.digest();
				if (Arrays.equals(digest, digest1) || Arrays.equals(digest, digest2))
					return true;
				md5.reset();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasClass(String className) {
		try {
			return Class.forName(className) != null;
		} catch (Throwable e) {
		}
		return false;
	}

	public static boolean hasPackage(Context context, String packageName) {
		try {
			return context.getPackageManager().getPackageInfo(packageName, 0) != null;
		} catch (Throwable e) {
		}
		return false;
	}

	public static int getAndroidResourceId(Resources res, String name, String type) {
		try {
			return res.getIdentifier(name, type, "android");
		} catch (Throwable e) {
		}
		return 0;
	}

	public static Drawable getAndroidDrawable(Resources res, String name) {
		try {
			return res.getDrawable(res.getIdentifier(name, "drawable", "android"));
		} catch (Throwable e) {
		}
		return null;
	}

	public static int getAndroidDimensionPixelSize(Resources res, String name) {
		try {
			return res.getDimensionPixelSize(res.getIdentifier(name, "dimen", "android"));
		} catch (Throwable e) {
		}
		return 0;
	}

	public static String getAndroidString(Resources res, String name) {
		try {
			return res.getString(res.getIdentifier(name, "string", "android"));
		} catch (Throwable e) {
		}
		return null;
	}

	public static Animation getAndroidTransitionAnimation(Context context, boolean enter) {
		try {
			final String name = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
					? (enter ? "activity_open_enter" : "activity_close_exit") : (enter ? "dialog_enter" : "dialog_exit");
			final int anim = context.getResources().getIdentifier(name, "anim", "android");
			if (anim != 0)
				return AnimationUtils.loadAnimation(context, anim);
		} catch (Throwable e) {
		}
		return new AlphaAnimation(enter ? 0 : 1, enter ? 1 : 0);
	}

	public static Drawable getDrawable(Context context, int attr) {
		final int[] attrs = {attr};
		final TypedArray a = context.obtainStyledAttributes(attrs);
		final Drawable drawable = a.getDrawable(0);
		a.recycle();
		return drawable;
	}

	private static Field mArrayOfList;

	static {
		try {
			mArrayOfList = ArrayList.class.getDeclaredField("array");
			mArrayOfList.setAccessible(true);
		} catch (Throwable e) {
		}
	}

	public static <T> boolean quickSort(ArrayList<T> list, Comparator<? super T> comparator) {
		try {
			//	Try sort the array directly, avoid copying array items
			@SuppressWarnings("unchecked")
			final T[] a = (T[]) mArrayOfList.get(list);
			if (a == null)
				throw new NoSuchFieldException();
			Arrays.sort(a, 0, list.size(), comparator);
			return true;
		} catch (Throwable e) {
		}
		return safeSort(list, comparator);
	}

	public static <T> boolean safeSort(List<T> list, Comparator<? super T> comparator) {
		try {
			Collections.sort(list, comparator);
			return true;
		} catch (Throwable e) {    //	IllegalArgumentException???
		}
		return false;
	}

	public static String getNameOfFilePath(String path) {
		if (path != null)
			return path.substring(path.lastIndexOf(File.separatorChar) + 1);
		return "";
	}

	public static String getExtOfFilePath(String path, boolean toLowCase) {
		final int pos = path != null ? path.lastIndexOf('.') : -1;
		if (pos == -1)
			return "";

		String ext = path.substring(pos + 1);
		if (toLowCase)
			ext = ext.toLowerCase(Locale.ENGLISH);
		return ext;
	}

	public static String getTitleOfFilePath(String path) {
		final String name = getNameOfFilePath(path);
		final int pos = name.lastIndexOf('.');
		if (pos != -1)
			return name.substring(0, pos);
		return name;
	}

	public static String getParentPath(String path) {
		if (path != null) {
			final int pos = path.lastIndexOf(File.separatorChar);
			if (pos >= 0)
				return path.substring(0, pos);
		}
		return path;
	}

	public static String addSeparatorToPath(String path) {
		final int len = path != null ? path.length() : 0;
		if (len > 0 && path.charAt(len - 1) != File.separatorChar)
			path += File.separatorChar;
		return path == null ? File.separator : path;
	}

	public static String buildPath(String parent, String name) {
		final int len = parent != null ? parent.length() : 0;
		if (len > 0 && parent.charAt(len - 1) != File.separatorChar)
			return parent + File.separatorChar + name;
		return parent == null ? File.separator + name : parent + name;
	}

	public static boolean pathIsSelfOrChild(String parent, String child) {
		if (parent == null || child == null)
			return false;

		final int plen = parent.length();
		final int clen = child.length();
		return (plen <= clen) && child.startsWith(parent) && (plen == clen || child.charAt(plen) == File.separatorChar);
	}

	public static boolean pathIsHidden(String path) {
		return path != null && path.contains("/.");
	}

	private static final Pattern mFilePattern = Pattern.compile("[\\\\/:*?\"<>|]");

	public static String fixFileName(String name) {
		return name == null ? "" : mFilePattern.matcher(name).replaceAll("_");
	}

	public static final String FILE_NOMEDIA = ".nomedia";



	public static File getAvailableNewFile(File path, String ext) {
		if (!path.exists())
			return path;

		final File parent = path.getParentFile();
		String title = path.getName();
		final int pos = title.lastIndexOf('.');
		if (pos > 0) {
			if (ext == null)
				ext = title.substring(pos);
			title = title.substring(0, pos);
		}

		synchronized (Utilities.class) {
			for (int i = 1; ; i++) {
				final File file = new File(parent, title + (i < 10 ? "~0" : "~") + Integer.toString(i) + ext);
				if (!file.exists())
					return file;
			}
		}
	}

	public static void emptyFolder(final File folder) {
		folder.listFiles(new FileFilter() {
			public boolean accept(File path) {
				if (path.isDirectory())
					emptyFolder(path);
				else
					path.delete();
				return false;
			}
		});
	}


	public static void showErrorMessage(Context context, Throwable error) {
		try {
			final Throwable cause = error.getCause();
			if (cause != null)
				error = cause;
			Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		} catch (Throwable e) {
		}
	}
}
