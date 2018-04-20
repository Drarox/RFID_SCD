/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1616 $
  * Revision of last commit    :  $Rev: 1616 $
  * Date of last commit     :  $Date: 2016-02-03 19:03:03 +0100 (Wed, 03 Feb 2016) $ 
  *
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2015 STMicroelectronics</center></h2>
  *
  * Licensed under ST MYLIBERTY SOFTWARE LICENSE AGREEMENT (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/myliberty
  *
  * Unless required by applicable law or agreed to in writing, software 
  * distributed under the License is distributed on an "AS IS" BASIS, 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied,
  * AND SPECIFICALLY DISCLAIMING THE IMPLIED WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
*/

package com.st.demo;

import java.io.File;
import java.io.IOException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.st.demo.MMYDemoWriteNDEFActivity;
import com.st.demo.TagMenuDetailsActivity;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;

/**
 * @author MMY team
 *
 */
public class WelcomeActivity extends FragmentActivity {
    // Constants for NFC feature state
    // - state is detected at class creation
    // - STATE_NFC_UNKNOWN is the initial state
    // - STATE_NFC_NOT_AVAILABLE is a final state (no NFC chip in current device)
    // - STATE_NFC_NOT_ENABLED is a transient state: NFC activation can be detected in onResume method (if user switched to paremeters menu and came back to the application)
    // - STATE_NFC_ENABLED is a transient state: NFC can be deactivated by end user, then need to be detected
    public enum NfcState {
        STATE_NFC_UNKNOWN,
        STATE_NFC_NOT_AVAILABLE,
        STATE_NFC_NOT_ENABLED,
        STATE_NFC_ENABLED
    }

    private NfcAdapter nfcAdapter;
    private NfcState nfcState = NfcState.STATE_NFC_UNKNOWN;
    private PendingIntent nfcPendingIntent;
//    private IntentFilter[] nfcFiltersArray;
//    private IntentFilter nfcFilter;
//    private String[][] nfcTechLists;

    // Cosmetics
    private AnimationDrawable nfcWavesAnim;

    private static int RETURN_FROM_TAGMenuDetailActivity = 1001;

    private CheckBox enableDemoFeatures;
    private CheckBox enableSalonFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Log init
        try {
            File logFile = new File(Environment.getExternalStorageDirectory() +"/ST25Demo/logfile.log");
            logFile.createNewFile();
            String logCmd = "logcat -f " + logFile.getAbsolutePath();
            //Runtime.getRuntime().exec(logCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v(getApplication().getClass().getName(), "Log for \"" + getString(R.string.app_name) + "\" " + getString(R.string.app_version_prefix) + " " + getString(R.string.app_version));
        Log.v(this.getClass().getName(), "OnCreate Activity");

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_welcome);

        // Check for available NFC Adapter
        PackageManager pm = getPackageManager();
        if(!pm.hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            nfcState = NfcState.STATE_NFC_NOT_AVAILABLE;

        } else {
            // Check for enabled NFC Adapter
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (!nfcAdapter.isEnabled())
            {
                nfcState = NfcState.STATE_NFC_NOT_ENABLED;

            } else {
                nfcState = NfcState.STATE_NFC_ENABLED;

                /** Test purpose */
/*                Button testButton = (Button) findViewById(R.id.TestBtnId);
                testButton.setText("To Tag Info");
                testButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent(WelcomeActivity.this, TagInfoActivity.class);
                        intent.putExtra(TagInfoActivity.ARG_TAG, 0);
                        startActivity(intent);
                    }
                });*/
            }

            // Create the PendingIntent, Filters and technologies that will be used in onResume, either after NFC_NOT_ENABLED or NFC_ENABLED states
            //nfcPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, TagInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            // Intent intent = new Intent(WelcomeActivity.this, TagMenuDetailsActivity.class);
            Intent intent = new Intent(this, TagMenuDetailsActivity.class);
            intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
            //nfcPendingIntent = PendingIntent.getActivity(this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            nfcPendingIntent = PendingIntent.getActivity(this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

            //startActivityForResult(intent, RETURN_FROM_TAGMenuDetailActivity);

//FBE Test            Intent intent = new Intent(this, MMYDemoNDEFReadActivity.class);
//FBE Test            intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
//FBE Test            nfcPendingIntent = PendingIntent.getActivity(this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


//            nfcFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//            try {
//                nfcFilter.addDataType("*/*");
//            }
//            catch  (MalformedMimeTypeException e) {
//                throw new RuntimeException("fail", e);
//            }
//            nfcFiltersArray = new IntentFilter[] {nfcFilter};
//            nfcTechLists = new String[][] { new String[] { android.nfc.tech.Ndef.class.getName()} };
        }

        Button goBtn = (Button) this.findViewById(R.id.welcome_go_btn);
        goBtn.setText(getResources().getString(R.string.st_web));

        // "Write" button
        Button writeBtn = (Button) this.findViewById(R.id.bComposeBtn);
        // writeBtn.setText(R.strinMMYDemoWriteNDEFActivityg.mnf_frag_write_menu_btn_txt);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MMYDemoWriteNDEFActivity.class);
                intent.putExtra("fromWelcomScreen", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //TBDONE : Activate the tools access from welcome Screen ?
        Button toolsBtn = (Button) this.findViewById(R.id.bToolsBtn);
        // writeBtn.setText(R.string.mnf_frag_write_menu_btn_txt);
        toolsBtn.setVisibility(4);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MMYDemoWriteNDEFActivity.class);
                intent.putExtra("fromWelcomScreen", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        enableDemoFeatures   = (CheckBox) findViewById(R.id.checkEnableDemoFeatures);
        enableSalonFeatures   = (CheckBox) findViewById(R.id.checkEnableSalonFeatures);



    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //SecondActivity closed
        if(requestCode == RETURN_FROM_TAGMenuDetailActivity){
            Intent intent = new Intent(this, TagMenuDetailsActivity.class);
            intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
            startActivity(intent); //reload MainActivity
            finish();

        }
     }*/

    public void onCheckboxEnableDemoFeaturesClicked(View v) {
        NFCApplication currentApp = (NFCApplication) getApplication();
        if(enableDemoFeatures.isChecked()){
            currentApp.setEnableDemoFeature(true);
        } else {
            currentApp.setEnableDemoFeature(false);
        }
    }
    public void onCheckboxEnableSalonFeaturesClicked(View v) {
        NFCApplication currentApp = (NFCApplication) getApplication();
        if(enableSalonFeatures.isChecked()){
            currentApp.setEnableSalonFeature(true);
        } else {
            currentApp.setEnableSalonFeature(false);
        }
    }

    @Override
    protected void onResume() 
    {
        Log.v(this.getClass().getName(), "OnResume Activity");

        // TODO Auto-generated method stub
        super.onResume();

        // In this activity, no more active tag should be stored
        NFCApplication currentApp = (NFCApplication) getApplication();
        currentApp.setCurrentTag(null);
        currentApp.setFileID(-1);
        // Update application header
        NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.WcActNFCAppHeaderFragmentId);
        mHeadFrag.onTagChanged(null);

        // Check for available NFC Adapter
        PackageManager pm = getPackageManager();
        if(!pm.hasSystemFeature(PackageManager.FEATURE_NFC))
        {
            // NFC not available
            nfcState = NfcState.STATE_NFC_NOT_AVAILABLE;

        }
        else
        {

            if (!nfcAdapter.isEnabled())
            {
                // NFC not enabled
                nfcState = NfcState.STATE_NFC_NOT_ENABLED;

            }
            else
            {
                nfcState = NfcState.STATE_NFC_ENABLED;

                // Start the waves animations
                ImageView nfcWavesImg = (ImageView) findViewById(R.id.NfcWavesImgId);
                nfcWavesImg.setBackgroundResource(R.drawable.nfc_waves_anim);
                nfcWavesAnim = (AnimationDrawable) nfcWavesImg.getBackground();
                nfcWavesAnim.start();

                // Route the NFC events to the next activity (Tag Info ?)
                nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null /*nfcFiltersArray*/, null /*nfcTechLists*/);

            }
        }

        // Set the text of the main TextView
        setMainText(nfcState);
    }


    @Override
    protected void onPause() 
    {
        Log.v(this.getClass().getName(), "OnPause Activity");

        // TODO Auto-generated method stub
        super.onPause();

        if (nfcState == NfcState.STATE_NFC_ENABLED) {
            // Stop the waves animations... if running
            nfcWavesAnim.stop();
            ImageView nfcWavesImg = (ImageView) findViewById(R.id.NfcWavesImgId);
            nfcWavesImg.setBackgroundResource(R.drawable.nfc_wave_empty);
        }

        if (nfcAdapter!=null) nfcAdapter.disableForegroundDispatch(this);

        return;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.welcome_menu, menu);
        return true;
    }

    /** Called when the user clicks on the "www.st.com" button */
    public void onBtnWWWButtonClick(View v) {
        String url = "http://www.st.com/memories";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void onComposeButtonClick(View v)
    {

    }


    /** Set the text of the 2 main TextViews of this activity */
    private void setMainText(NfcState actState) {
        Typeface font1 = Typeface.SERIF;
        Typeface font2 = Typeface.SERIF;
        int style1 = Typeface.NORMAL;
        int style2 = Typeface.NORMAL;
        int color1 = Color.BLACK;
        int color2 = Color.BLACK;
        int text1 = R.string.wc_act_default_main_txt_1;
        int text2 = R.string.wc_act_default_main_txt_2;

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        float heightPosition = dpHeight/12; // approximatively 1/12 from Top
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) heightPosition;

        // Set the local variable according to the state
        switch (actState) {
            case STATE_NFC_NOT_AVAILABLE:
                font1 = Typeface.SERIF;
                font2 = Typeface.SERIF;
                style1 = Typeface.ITALIC;
                style2 = Typeface.ITALIC;
                color1 = Color.RED;
                color2 = Color.RED;
                text1 = R.string.wc_act_nfc_not_avail_str_1;
                text2 = R.string.wc_act_nfc_not_avail_str_2;
                break;
            case STATE_NFC_NOT_ENABLED:
                font1 = Typeface.SERIF;
                font2 = Typeface.SERIF;
                style1 = Typeface.ITALIC;
                style2 = Typeface.ITALIC;
                color1 = Color.RED;
                color2 = Color.RED;
                text1 = R.string.wc_act_nfc_not_enabled_str_1;
                text2 = R.string.wc_act_nfc_not_enabled_str_2;
                break;
            case STATE_NFC_ENABLED:
                font1 = Typeface.SERIF;
                font2 = Typeface.SERIF;
                style1 = Typeface.BOLD;
                style2 = Typeface.NORMAL;
                color1 = Color.BLACK;
                color2 = Color.BLACK;
                text1 = R.string.wc_act_tag_wait_str_1;
                text2 = R.string.wc_act_tag_wait_str_2;
                break;
            default:
                // already set in local variables initialization
                break;
        }

//        Resources res = getResources();
        TextView msgTextView = (TextView) findViewById(R.id.WcActMainTxt1Id);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        msgTextView.setLayoutParams(params);
        msgTextView.setGravity(Gravity.CENTER);

//        msgTextView.setTextSize(res.getInteger(R.integer.wc_act_nfc_not_avail_txt_size));
        msgTextView.setTypeface(font1, style1);
        msgTextView.setTextColor(color1);
        msgTextView.setText(text1);

        msgTextView = (TextView) findViewById(R.id.WcActMainTxt2Id);
//        msgTextView.setTextSize(res.getInteger(R.integer.wc_act_nfc_not_avail_txt_size));
        msgTextView.setTypeface(font2, style2);
        msgTextView.setTextColor(color2);
        msgTextView.setText(text2);
    }

}
