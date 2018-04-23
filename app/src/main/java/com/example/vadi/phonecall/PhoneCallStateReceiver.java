package com.example.vadi.phonecall;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.vadi.phonecall.OnHookActivity.MY_PERMISSIONS_REQUEST_READ_SMS;

public class PhoneCallStateReceiver extends BroadcastReceiver {
    private TelephonyManager mTelephonyManager;
    public static boolean isListening = false;
    Context mContext;

  /*  private MediaPlayer mediaPlayer;
    AudioManager audioManager;


    public static int lastState = TelephonyManager.CALL_STATE_IDLE;
    public static Date callStartTime;
    public static boolean isIncoming;
    public static String savedNumber;

    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor;
    private Telephony telephonyService;


    public static long starttime=0;
    public static long endtime=0;


    Boolean permission = false;*/
  private String state;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        mContext=context;



      /*  mTelephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        int state1;
        final String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);*/

        Bundle bundle = intent.getExtras();
        if(bundle != null){
            state = bundle.getString(TelephonyManager.EXTRA_STATE);

          //      switch (state) {
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //          case TelephonyManager.CALL_STATE_RINGING:
                Toast.makeText(context, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
                //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //     state1=1;


                //     String packageName = "com.example.vadi.phonecall";
                //     launchApp(packageName);

                MainActivity.NO = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //            Toast.makeText(context,"CALL_STATE_RINGING",Toast.LENGTH_SHORT).show();
                //      android.os.Process.killProcess(android.os.Process.myPid());

            }
            if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //       case TelephonyManager.CALL_STATE_IDLE:

                    /*    endtime= System.currentTimeMillis();
                        Log.e("log", ""+endtime);*/
                Toast.makeText(context, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());

                //     OnHookActivity.delete();

                       /* if(!MainActivity.NO.isEmpty()){



                                    ContentResolver contentResolver;
                                    contentResolver = context.getContentResolver();

                                    Uri uriSms = Uri.parse("content://sms/");
                                    MainActivity.NO = MainActivity.NO.substring(Math.max(0, MainActivity.NO.length() - 10));
                                    String no = "+91" + MainActivity.NO;
                                    String[] phoneNumber = new String[]{no};
                                    String[] dat = new String[]{String.valueOf(PhoneCallStateReceiver.starttime)};
                                    final Cursor cursor = contentResolver.query(uriSms, new String[]{"_id", "address", "date", "body"},
                                            null, null, null);

                                    try {

                                        while (cursor.moveToNext()) {
                                            String address = cursor.getString(1);

                                            if (address.contains(MainActivity.NO)) {
                                                //    if (address.equals(num)) {
                                                String id = cursor.getString(0);
                                                String ads = cursor.getString(1);
                                                String dt = cursor.getString(2);
                                                String msg = cursor.getString(3);


                                                if (PhoneCallStateReceiver.starttime <= (Long.parseLong(dt))) {
                                                    String[] i = new String[]{id};
                                                    //  String[] date = new String[] {MainActivity.NO};

                                                    //     ContentValues values = new ContentValues();
                                                    //    values.put("read", true);
                                                    //    context.getContentResolver().update(Uri.parse("content://sms"), values, "_id=?"  , i);
                                                    String uri = "content://sms/"+id;
                                                    String mSelectionClause = "_id = ? AND address= ? AND body= ?";

                                                    String[] mSelectionArgs = new String[3];
                                                    mSelectionArgs[0] = id;
                                                    mSelectionArgs[1] = ads;
                                                    mSelectionArgs[2] = msg;


                                                    int count = contentResolver.delete(Uri.parse(uri), mSelectionClause, mSelectionArgs);

                                                    if (count > 0) {
                                                        Toast.makeText(context, " deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                        }

                                    } catch (Exception e) {

                                    }


                        }*/

            }
            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //       case TelephonyManager.CALL_STATE_OFFHOOK:
                MainActivity.NO = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //      starttime= System.currentTimeMillis();
                //       Log.e("log", ""+starttime);
                //           SharedPreferences prefs = context.getSharedPreferences("X", MODE_PRIVATE);
                //          SharedPreferences.Editor editor = prefs.edit();
                //           editor.putString("lastActivity", getClass().getName());
                //           editor.commit();
                //                  Toast.makeText(context,"CALL_STATE_OFFHOOK",Toast.LENGTH_SHORT).show();


                Intent i = new Intent(context, OnHookActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("address", intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
                context.startActivity(i);


            }
                }
               /* super.onCallStateChanged(state, incomingNumber);

            }
        };*/

       /* if(!isListening) {
            mTelephonyManager.listen(state, PhoneStateListener.LISTEN_CALL_STATE);
            isListening = true;
        }*/


    }









    protected void launchApp(String packageName){


        PackageManager pm = mContext.getPackageManager();
        try{
            Intent intent = pm.getLaunchIntentForPackage(packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            if(intent == null){
                // Throw PackageManager NameNotFoundException
                throw new PackageManager.NameNotFoundException();
            }else{
                // Start the app
                mContext.startActivity(intent);
            }
        }catch(PackageManager.NameNotFoundException e){

            Log.e("Launch",e.getMessage());
        }
    }
}


