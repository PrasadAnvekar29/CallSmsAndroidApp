package com.example.vadi.phonecall;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.klinker.android.logger.OnLogListener;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.BroadcastUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;
import com.klinker.android.send_message.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class OnHookActivity extends AppCompatActivity {

  //  EditText number;
    Button add_btn;
    RecyclerView rvList;
    Boolean permission = false;

    private EditText msg;
    private Button send;

   public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 99;
    private static final int PERMISSION_SEND_SMS = 99;

    private static final int RECORD = 1;
 //   private Handler mRepeatHandler;
//    private Runnable mRepeatRunnable;
//    private final static int UPDATE_INTERVAL = 5000;

    final Handler handler = new Handler();                                      //Handler for timer task
    TimerTask timerTask;
    Timer timer;

    String no1="";
    private Button refresh;


    private Settings settings;

    private MediaRecorder mRecorder;
    private long mStartTime = 0;

    private int[] amplitudes = new int[100];
    private int i = 0;

    private Handler mHandler = new Handler();
    private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor,100);
        }
    };
    private File mOutputFile;

    public int Recording=0;
    private ImageView imagebutton;
//    private Button record,stop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_hook);
        rvList = (RecyclerView) findViewById(R.id.rvList);
        msg = (EditText) findViewById(R.id.msg);
        send = (Button) findViewById(R.id.send);
        imagebutton = (ImageView) findViewById(R.id.imagebutton);

    //    record = (Button) findViewById(R.id.record);
     //   stop = (Button) findViewById(R.id.stop);




    //    Intent sms_intent = getIntent();
   //     Bundle b = sms_intent.getExtras();

        MainActivity.NO = MainActivity.NO.substring(Math.max(0, MainActivity.NO.length() - 10));

    //    if(Recording==0){
   //         record.setVisibility(View.VISIBLE);
   //         stop.setVisibility(View.GONE);
    //    }

        if (ActivityCompat.checkSelfPermission(OnHookActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(OnHookActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD);

        } else {
            startRecording();
        }

      /*  record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording(false);
                setResult(RESULT_CANCELED);
                finish();
            }
        });*/



    //    no1=b.getString("address");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (msg.getText().toString().isEmpty()) {

                } else {

                    if (ActivityCompat.checkSelfPermission(OnHookActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(OnHookActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        try {
                            MainActivity.NO = MainActivity.NO.substring(Math.max(0, MainActivity.NO.length() - 10));
                      //      SmsManager smsManager = SmsManager.getDefault();
                      //      smsManager.sendTextMessage(MainActivity.NO, null, msg.getText().toString(), null, null);
                            sendMessage();
                            msg.setText("");
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }


                }
            }
        });
        Start();
        startTimer();

        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   MainActivity mainActivity=(MainActivity)getActivity();
                //  mainActivity.change();
                takeImageFromCamera();
            }
        });


        initSettings();
    //    initViews();
    //    initActions();
        initLogging();

        BroadcastUtils.sendExplicitBroadcast(this, new Intent(), "test action");


    }
    private void initSettings() {
        settings = Settings.get(this);

        if (TextUtils.isEmpty(settings.getMmsc()) &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initApns();
        }
    }

    private void initApns() {
        ApnUtils.initDefaultApns(this, new ApnUtils.OnApnFinishedListener() {
            @Override
            public void onFinished() {
                settings = Settings.get(OnHookActivity.this, true);
            }
        });
    }


    private void initLogging() {
        com.klinker.android.logger.Log.setDebug(true);
        com.klinker.android.logger.Log.setPath("messenger_log.txt");
        com.klinker.android.logger.Log.setLogListener(new OnLogListener() {
            @Override
            public void onLogged(String tag, String message) {
                //logAdapter.addItem(tag + ": " + message);
            }
        });
    }

    public void sendMessage() {

        try{
       /* new Thread(new Runnable() {
            @Override
            public void run() {*/
                com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
                sendSettings.setMmsc(settings.getMmsc());
                sendSettings.setProxy(settings.getMmsProxy());
                sendSettings.setPort(settings.getMmsPort());
                sendSettings.setUseSystemSending(true);

                Transaction transaction = new Transaction(OnHookActivity.this, sendSettings);

                Message message = new Message(msg.getText().toString(), MainActivity.NO);

             /*   if (imageToSend.isEnabled()) {
                    message.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.android));
                }*/

                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
           /* }
        }).start();*/
        }catch (Exception e){

        }
    }





    public void Start() {

        try {

            if (!MainActivity.NO.isEmpty()) {


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permission = checkContactsPermission();
                    if (permission) {
                        List<ListItem> list = new ArrayList();
                        ListItem listItem;
                        //   final String num = MainActivity.NO;
                        Uri uriSms = Uri.parse("content://sms");
                    //    MainActivity.NO = MainActivity.NO.substring(Math.max(0, MainActivity.NO.length() - 10));
                    //    String no = "+91" + MainActivity.NO;
                    //    String[] phoneNumber = new String[]{no};
                        final Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);

                        while (cursor.moveToNext()) {
                            String address = cursor.getString(1);

                            if (address.contains(MainActivity.NO)) {
                                //    if (address.equals(num)) {
                                String ads = cursor.getString(1);
                                String dt = cursor.getString(2);
                                String msg = cursor.getString(3);
                                listItem = new ListItem();
                                //     listItem.setNumber(ads);
                                listItem.setMessage(msg);
                                list.add(listItem);
                            }

                        }
                        //  Comparator comparator = Collections.reverseOrder();
                        //     Collections.sort(list, comparator);

                        ListAdapter listAdapter = new ListAdapter(list, getBaseContext());
                        //    rvList.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
                        mLayoutManager.setReverseLayout(true);
                        //   mLayoutManager.setStackFromEnd(true);
                        rvList.setLayoutManager(mLayoutManager);
                        rvList.setAdapter(listAdapter);
                    }

                }

            }

        }catch (Exception e){

        }
    //    startTimer();
    }

    public boolean checkContactsPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.READ_SMS)) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{android.Manifest.permission.READ_SMS},
                                    MY_PERMISSIONS_REQUEST_READ_SMS);
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{android.Manifest.permission.READ_SMS},
                                    MY_PERMISSIONS_REQUEST_READ_SMS);
                        }
                        return false;
        } else {
            return true;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults != null) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {


                        return;
                    }/*else {

                        if(requestCode ==PERMISSION_SEND_SMS) {
                            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                //do send or read sms
                                return;
                            }
                        }
                    }*/
                } else {
                    Toast.makeText(getApplicationContext(), "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            /*case PERMISSION_SEND_SMS:
                if(grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //do send or read sms
                    return;
                }*/
              //  break;
        }
    }

    @Override
    protected void onStop() {
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        //to store prevous data if somehow stopped the application


        super.onStop();

    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, TimerTask will run every 30000ms
        timer.schedule(timerTask, 1000, 1*60*100);//5*60*    1*60*
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                      //      Start();
                        try {



                                List<ListItem> list = new ArrayList();
                                ListItem listItem;
                                //   final String num = MainActivity.NO;
                                Uri uriSms = Uri.parse("content://sms");
                        //        MainActivity.NO = MainActivity.NO.substring(Math.max(0, MainActivity.NO.length() - 10));
                        //        String no = "+91" + MainActivity.NO;
                        //        String[] phoneNumber = new String[]{no};
                                final Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);

                                while (cursor.moveToNext()) {
                                    String address = cursor.getString(1);

                                    if (address.contains(MainActivity.NO)) {
                                        //    if (address.equals(num)) {
                                        String ads = cursor.getString(1);
                                        String dt = cursor.getString(2);
                                        String msg = cursor.getString(3);
                                        listItem = new ListItem();
                                        //     listItem.setNumber(ads);
                                        listItem.setMessage(msg);
                                        list.add(listItem);
                                    }

                                }
                                //  Comparator comparator = Collections.reverseOrder();
                                //     Collections.sort(list, comparator);

                                ListAdapter listAdapter = new ListAdapter(list, getBaseContext());
                                //    rvList.setLayoutManager(new LinearLayoutManager(getBaseContext()));

                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
                                mLayoutManager.setReverseLayout(true);
                                //   mLayoutManager.setStackFromEnd(true);
                                rvList.setLayoutManager(mLayoutManager);
                                rvList.setAdapter(listAdapter);





                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    }
                });
            }

        };

    }

    @Override
    public void onBackPressed() {
  //     super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording(false);
        setResult(RESULT_CANCELED);
        finish();
    }

    private void startRecording() {
        try{
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mRecorder.setAudioEncodingBitRate(48000);
        } else {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(64000);
        }
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        try {
            Recording=1;

      //      record.setVisibility(View.GONE);
      //      stop.setVisibility(View.VISIBLE);
            mRecorder.prepare();
            mRecorder.start();
            mStartTime = SystemClock.elapsedRealtime();
            mHandler.postDelayed(mTickExecutor, 100);
            Log.d("Voice Recorder","started recording to "+mOutputFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("Voice Recorder", "prepare() failed "+e.getMessage());
            Recording=0;
     //       record.setVisibility(View.VISIBLE);
     //       stop.setVisibility(View.GONE);
        }


    }catch (Exception e){

    }
    }

    protected  void stopRecording(boolean saveFile) {

        Recording=0;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mStartTime = 0;
        mHandler.removeCallbacks(mTickExecutor);
        if (!saveFile && mOutputFile != null) {
            mOutputFile.delete();
        }
    //    record.setVisibility(View.VISIBLE);
    //    stop.setVisibility(View.GONE);
    }

    private File getOutputFile() {
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh-mm");
        Date resultdate = new Date(yourmilliseconds);
        System.out.println(sdf.format(resultdate));
   //     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/VoiceRecorder/Call"
                + sdf.format(resultdate)
                + ".m4a");
    }

    private void tick() {
        long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
  //      mTimerTextView.setText(minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds);
        if (mRecorder != null) {
            amplitudes[i] = mRecorder.getMaxAmplitude();
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
            if (i >= amplitudes.length -1) {
                i = 0;
            } else {
                ++i;
            }
        }
    }

    /*@Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                stopRecording(false);
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.share_button:
                stopRecording(true);
                Uri uri = Uri.parse("file://" + mOutputFile.getAbsolutePath());
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(uri);
                sendBroadcast(scanIntent);
                setResult(Activity.RESULT_OK, new Intent().setData(uri));
                finish();
                break;
        }
    }*/





    public void takeImageFromCamera() {

        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, 100);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && null != data ) {

            Uri contentURI = data.getData();
              Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(OnHookActivity.this.getContentResolver(), contentURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
         //   String path = saveImage(bitmap);
          //       pic.setImageBitmap(bitmap);

       try{

            MainActivity.NO = MainActivity.NO.substring(Math.max(0, MainActivity.NO.length() - 10));

            final Bitmap finalBitmap = bitmap;

         //   Bitmap immagex=image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b,Base64.DEFAULT);

         //  String encodedImage =encoded;
            //    return encoded;
            final byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
       //    Bitmap bm= BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);


           /* new Thread(new Runnable() {
                @Override
                public void run() {*/

                    com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
                    sendSettings.setMmsc(settings.getMmsc());
                    sendSettings.setProxy(settings.getMmsProxy());
                    sendSettings.setPort(settings.getMmsPort());
                    sendSettings.setUseSystemSending(true);

                    Transaction transaction = new Transaction(OnHookActivity.this, sendSettings);

                    Message message = new Message(msg.getText().toString(), MainActivity.NO);


                //    message.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.android));
                    message.setImage(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                    transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
             /*   }
            }).start();*/

        }catch (Exception e){

        }


        }
    }




    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


   /* public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        byte[] b = bytes.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
        encodedImage = encoded;
        return "";
    }*/


}
