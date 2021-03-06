package com.bSecure;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class AlarmScheduler {

	static AlarmManager mgr;
	static PendingIntent alarmIntent;
	static SharedPreferences asetting;
	static String sig_msg;
	static Integer tim, ytim, rtim;
	String msg;
	static String cntact;
	static String smsg, gmsg;
	static String ymsg;
	static String rmsg;
	static Database db;
	static GPSTracker gps;
	static double latitude;
	static double longitude;
	static String gm;
	static SharedPreferences.Editor edit;
	static Handler h;
	static Runnable r;
	static Timer t;
	static int xc = 0, start = 0;
	static Context con;

	public AlarmScheduler() {
		// TODO Auto-generated constructor stub
	}

	static void scheduleAlarms(Context ctxt, String msg) {
		SmsManager sms = SmsManager.getDefault();
		con = ctxt;
		asetting = ctxt.getSharedPreferences("signal", 0);
		edit = asetting.edit();
		String sm;
		db = new Database(ctxt);
		Log.v("contxt", String.valueOf(ctxt));
		mgr = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
		// GREEN
		if (msg.equals("green")) {

			set_msg("green");
			sm = get_msg();
			Log.v("hi", "gcreate" + sm);
			gmsg = asetting.getString("gmsg1", "");
			String g = gmsg;
			String s = asetting.getString("gmsg", "") + " " + g;

			Log.v("gmsg", s);

			// Toast.makeText(ctxt, gmsg, Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(ctxt, AudioRecorder.class);
			// add infos for the service which file to download and where to
			// store
			intent.putExtra("audio", "loc");

			ctxt.startService(intent);

			Cursor c1 = db.getContact("green");
			if (c1.getCount() > 0) {
				if (c1.moveToLast()) {
					for (c1.moveToFirst(); !c1.isAfterLast(); c1.moveToNext()) {
						Log.v("con", c1.getString(1));
						try {

							Log.v("msg", c1.getString(1));

						}

						catch (Exception e) {
							Log.v("msg", "send fail!");
						}
					}

				}
			} else {

				Log.v("data", "not found");

				Intent ii = new Intent(ctxt, ApplicationSettings.class);
				ii.putExtra("signal", "green");
				ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctxt.startActivity(ii);
				xc = 1;

			}

			db.close();

			Intent i = new Intent(ctxt, MessageSender.class);
			Log.v("hi", "gcreate");
			alarmIntent = PendingIntent.getService(ctxt, 0, i, 0);

			tim = asetting.getInt("gtime", 0);
			Log.v("tim", tim.toString());
			if (xc == 1) {
				mgr.set(AlarmManager.ELAPSED_REALTIME, 0, alarmIntent);

				xc = 0;
			} else {
				if (tim == 0) {

					Log.v("set", "time");

					mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
							SystemClock.elapsedRealtime() + 5000, 5000,
							alarmIntent);
				} else {
					mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
							SystemClock.elapsedRealtime() + 5000, tim * 1000,
							alarmIntent);
				}
			}

		}

		// YELLOW
		if (msg.equals("yellow")) {
			set_msg("yellow");
			sm = get_msg();
			Intent i = new Intent(ctxt, MessageSender.class);

			Log.v("hi", "ycreate " + msg + sm);
			/*
			 * Intent intent = new Intent(ctxt,AudioRecorder.class); // add infos for
			 * the service which file to download and where to store
			 * intent.putExtra("audio", "loc"); ctxt.startService(intent);
			 */
			LocationFinder gl = new LocationFinder();
			gl.getlc(ctxt, "yellow");
			ymsg = asetting.getString("ymsg1", "");
			String g = ymsg;
			String s = asetting.getString("ymsg", "") + " " + g;
			Log.v("ymsg1", s);
			Cursor c1 = db.getContact("yellow");
			if (c1.getCount() > 0) {
				if (c1.moveToLast()) {
					for (c1.moveToFirst(); !c1.isAfterLast(); c1.moveToNext()) {
						Log.v("con", c1.getString(1));
						try {

							sms.sendTextMessage(c1.getString(1), null, s, null,
									null);
							Log.v("msg", c1.getString(1));
							String timeStamp = new SimpleDateFormat("HHmmss")
									.format(new Date());
							Integer t = Integer.parseInt(String
									.valueOf(timeStamp.charAt(2))
									+ String.valueOf(timeStamp.charAt(3)));
							Log.v("mm", t.toString());
							Log.v("msg", timeStamp);
							edit.putInt("starttime", t);
							edit.commit();
						}

						catch (Exception e) {
							Log.v("msg", "send fail!");
						}
					}

				}
			} else {
				Intent ii = new Intent(ctxt, ApplicationSettings.class);
				ii.putExtra("signal", "yellow");
				ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctxt.startActivity(ii);
				xc = 1;
				Log.v("data", "not found");

			}
			db.close();
			alarmIntent = PendingIntent.getService(ctxt, 0, i, 0);
			if (xc == 1) {
				mgr.set(AlarmManager.ELAPSED_REALTIME, 0, alarmIntent);
				edit.putString("cset", "false");
				edit.commit();
				xc = 0;
			} else {
				edit.putString("cset", "true");
				edit.commit();
				ytim = asetting.getInt("ytime", 0);
				if (ytim == 0) {

					Log.v("set", "time");
					mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
							SystemClock.elapsedRealtime() + 5000, 600 * 1000,
							alarmIntent);
				} else {
					mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
							SystemClock.elapsedRealtime() + 5000, ytim * 1000,
							alarmIntent);
				}
			}

		}

		// RED
		if (msg.equals("red")) {
			set_msg("red");
			sm = get_msg();

			Intent intent1 = new Intent(ctxt, AudioRecorder.class);
			// add infos for the service which file to download and where to
			// store
			intent1.putExtra("audio", "loc");
			ctxt.startService(intent1);

			ctxt.stopService(new Intent(ctxt, AudioRecorder.class));

			Intent intent = new Intent(ctxt, AudioRecorder.class);
			// add infos for the service which file to download and where to
			// store
			intent.putExtra("audio", "start");
			start = 1;
			ctxt.startService(intent);

			Intent i = new Intent(ctxt, MessageSender.class);

			Log.v("hi", "rcreate " + msg + sm);
			rmsg = asetting.getString("rmsg1", "");
			String g = rmsg;
			String s = asetting.getString("rmsg", "") + " " + g;
			Log.v("red", s);

			Cursor c1 = db.getContact("red");
			if (c1.getCount() > 0) {
				if (c1.moveToLast()) {
					for (c1.moveToFirst(); !c1.isAfterLast(); c1.moveToNext()) {
						Log.v("con", c1.getString(1));
						try {

							Log.v("msg", c1.getString(1));
						}

						catch (Exception e) {
							Log.v("msg", "send fail!");
						}
					}

				}
			} else {
				Toast.makeText(con, "Add contact in application_settings page",
						Toast.LENGTH_SHORT).show();
				Intent ii = new Intent(con, ApplicationSettings.class);
				ii.putExtra("signal", "red");
				ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				con.startActivity(ii);
				xc = 1;
				Log.v("data", "not found");

			}
			db.close();
			// r.startRec();
			Log.v("com", "after set no");
			alarmIntent = PendingIntent.getService(ctxt, 0, i, 0);
			if (xc == 1) {
				Log.v("red", "come");
				mgr.set(AlarmManager.ELAPSED_REALTIME, 0, alarmIntent);

				xc = 0;
			} else {
				rtim = asetting.getInt("rtime", 0);
				if (rtim == 0) {

					Log.v("set", "time");
					mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
							SystemClock.elapsedRealtime() + 5000, 900 * 1000,
							alarmIntent);
				} else {
					Log.v("set", "time");
					mgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
							SystemClock.elapsedRealtime() + 5000, rtim * 1000,
							alarmIntent);
				}
			}
		}
	}

	static public void cancelAlarm(Context context) {
		// If the alarm has been set, cancel it.
		if (mgr != null) {
			mgr.cancel(alarmIntent);
			context.stopService(new Intent(context, AudioRecorder.class));
		}
	}

	static public void cancelRAlarm(Context context) {
		// If the alarm has been set, cancel it.
		if (mgr != null) {
			Log.v("v", "com");
			mgr.cancel(alarmIntent);

			context.stopService(new Intent(context, AudioRecorder.class));
			if (start == 1) {
				AudioRecorder.stopRec();
				start = 0;
			}
		}
	}

	static public void set_msg(String msg) {
		sig_msg = msg;

	}

	static public String get_msg() {
		return sig_msg;
	}

	// Get Location

}
