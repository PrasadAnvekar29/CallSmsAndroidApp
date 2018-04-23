package com.example.vadi.phonecall;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import android.view.View.OnClickListener;

import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import static com.example.vadi.phonecall.PhoneCallStateReceiver.isListening;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;
    public static EditText mPhoneNoEt;
    public static Button mDialButton,recordlist;
    private Button answer_call;
    private Context context=MainActivity.this;

    private PhoneCallStateReceiver PhoneCallStateReceiver;
    private IntentFilter mIntentFilter;


    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;
    public static int button;

    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();

    EditText toNumber=null;
    String toNumberValue="";

    public static String NO="";


    private TelephonyManager mTelephonyManager;

    private FilePickerDialog dialog;

   public static   int state;
    private String myPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDialButton = (Button) findViewById(R.id.call);
        recordlist = (Button) findViewById(R.id.recordlist);
        mPhoneNoEt = (EditText) findViewById(R.id.et_phone_no);
        myPackageName = getPackageName();

        mDialButton.setText("CAll");
        mDialButton.setBackgroundResource(R.drawable.button1);

        final DialogProperties properties=new DialogProperties();

      //  setSupportActionBar(findViewById(R.id.my_toolbar));


        dialog=new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Select a File");

            setTitle("PhoneCall");

        recordlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                properties.root=new File("/mnt/sdcard/VoiceRecorder/");
                dialog.show();
                }catch (Exception e){

                }
            }
        });


        mDialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        } else {
                            String phoneNo = mPhoneNoEt.getText().toString();
                            phoneNo = phoneNo.substring(Math.max(0,  phoneNo.length() - 10));
                            NO =phoneNo;
                            if (!TextUtils.isEmpty(phoneNo)) {
                             String   dial = "tel:+91" + phoneNo;
                                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

                            } else {
                                Toast.makeText(MainActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                            }
                        }
            }
        });





        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        //Create adapter
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);


        readContactData();

        File myDirectory = new File(Environment.getExternalStorageDirectory(), "VoiceRecorder");

        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.recordlist) {



            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        String phoneNo = mPhoneNoEt.getText().toString();
                        if (!TextUtils.isEmpty(phoneNo)) {
                            String dial = "tel:+91" + phoneNo;
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

                        } else {
                            Toast.makeText(MainActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog!=null) {
                        //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(MainActivity.this,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void readContactData() {

        try {

            /*********** Reading Contacts Name And Number **********/

            String phoneNumber = "";
            ContentResolver cr = getBaseContext()
                    .getContentResolver();

            //Query to get contact name

            Cursor cur = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            // If data data found in contacts
            if (cur.getCount() > 0) {

                Log.i("AutocompleteContacts", "Reading   contacts........");

                int k=0;
                String name = "";

                while (cur.moveToNext())
                {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                        //Create query to get phone number by contact id
                        Cursor pCur = cr
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = ?",
                                        new String[] { id },
                                        null);
                        int j=0;

                        while (pCur
                                .moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber =""+pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                // Add contacts names to adapter
                                adapter.add(name+"\n" +phoneNumber);
                       //         adapter.add(phoneNumber+"\n"+name);

                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                      //      nameValueArr.add(name.toString());

                                j++;
                                k++;
                            }
                        }  // End while loop
                        pCur.close();
                    } // End if

                }  // End while loop

            } // End Cursor value check
            cur.close();


        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {



        // TODO Auto-generated method stub
        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
     //   Toast.makeText(this, arg2+""+arg3, Toast.LENGTH_SHORT).show();
        // Get Array index value for selected name
        int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));
        String s=  textView.getText().toString();
        s=s.replace(" ","");
        s=s.replace("-","");
        String numbers = s.substring(Math.max(0, s.length() - 10));
        mPhoneNoEt.setText(numbers);

        // If name exist in name ArrayList
        if (i >= 0) {

            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        }

    }




}
