/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1463 $
  * Revision of last commit    :  $Rev: 1463 $
  * Date of last commit     :  $Date: 2015-12-02 16:23:44 +0100 (Wed, 02 Dec 2015) $
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;


import com.st.Fragments.MenuM24SRDemoFragment;
import com.st.Fragments.MenuToolsFragment;
import com.st.Fragments.MenuToolsLRFragment;

import com.st.demo.TagMenuDetailsActivity;
import com.st.demo.WaitForNFCTapM24SRDemo.settingsDemoState;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NFC.NFCActivity;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagHandler;
import com.st.NFC.STNfcTagVHandler;
import com.st.NFC.stnfcTagGenHandler;
import com.st.NFC.stnfchelper;
import com.st.NFC.NFCTag.NfcTagTypes;
import com.st.nfcv.Helper;
import com.st.util.*;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WaitForNFCTapLRDemo extends NFCActivity {

    private settingsDemoState _msettingDemoState = settingsDemoState.UNDEFINED_STATE;

    public enum NfcState {
        STATE_NFC_UNKNOWN, STATE_NFC_NOT_AVAILABLE, STATE_NFC_NOT_ENABLED, STATE_NFC_ENABLED
    }

    public enum settingsDemoState {
        UNDEFINED_STATE
    }

    public enum pingPongDemoState {
        UID_PENDING, // System is waiting to read UID
        ACK_PENDING, DATA_PENDING, END_PENDING, UNDEFINED_STATE
    }

    public enum logsDemoState {
        UID_PENDING, // System is waiting to read UID
        DATA_PENDING, // System is waiting to read LOG (uppon request sent by
                        // Droid system)
        UNDEFINED_STATE
    }

    public enum uploadDemoState {
        UID_PENDING, // System is waiting to read UID
        ACK_PENDING, // System is waiting to read LOG (uppon request sent by
                        // Droid system)
        UNDEFINED_STATE
    }

    public String NDEF_UID_EXT_TYPE = "m24sr_discopeer_uidmsg";
    public String NDEF_ACK_EXT_TYPE = "m24sr_discopeer_ackmsg";
    public String NDEF_CFG_EXT_TYPE = "m24sr_discopeer_cfgmsg";
    public String NDEF_START_EXT_TYPE = "m24sr_discopeer_startmsg";
    public String NDEF_REQ_EXT_TYPE = "m24sr_discopeer_reqmsg";
    public String NDEF_NACK_EXT_TYPE = "m24sr_discopeer_nackmsg";
    public String NDEF_DATA_EXT_TYPE = "m24sr_discopeer_datamsg";
    public String NDEF_END_EXT_TYPE = "m24sr_discopeer_endmsg";

    private final byte[] NDEF_UID_PINGPONG_DEMO = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
            (byte) 0xFF, (byte) 0x11, (byte) 0x44 };

    private NfcAdapter nfcAdapter;
    private NfcState nfcState = NfcState.STATE_NFC_UNKNOWN;
    private PendingIntent nfcPendingIntent;

    private String TAG = "Wait4TapLRDemoActivity";
    private MenuToolsLRFragment.actionType _currentAction;


    private Toast WFNFCTAPToast;
    private boolean _mpingPongDemoInProgress = false; // Ping Pong Demo in
                                                        // progress
    private pingPongDemoState _mpingPongDemoState = pingPongDemoState.UNDEFINED_STATE;

    private int _mpingPongCurrentValue = 0;
    private int _mcurrentloop = 0;
    private int[] _minPingPongValue;
    private int[] _moutPingPongValue;

    private PingPongSet _mpingpongSet;


    private byte PasswordNumber = (byte)0x01;
    private byte[] PasswordData = new byte[4];
    int block = 0;
    byte LockConfig = 0;



    // Audio Management Attributes
    int _mgetfunctionnalVolume = 0;

    // Cosmetics
    private AnimationDrawable nfcWavesAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_tap_screen_lrdemo);
        //final View _curFragmentView = findViewById(R.layout.wait_for_tap_screen_lrdemo);

        _mcurrentloop = 0;


        AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        _mgetfunctionnalVolume = volumeControl.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        // Check for available NFC Adapter
        PackageManager pm = getPackageManager();

        Intent launcherintent = getIntent();
        if (launcherintent == null) {
            // must not happen..
            Log.d("TAG", "Creator INTENT is NULL");
            this.finish();
        }

        // Retrieve parameters from intent
        _currentAction = MenuToolsLRFragment.actionType.UNDEFINED_ACTION;
        _currentAction = (MenuToolsLRFragment.actionType) launcherintent.getSerializableExtra(MenuToolsLRFragment.WAIT_FOR_TAP_ACTION);
         // look for string Password




        if (_currentAction == MenuToolsLRFragment.actionType.UNDEFINED_ACTION) {
            // must not happen..
            Log.d("TAG", "ACTION is UNDEFINED");
            this.finish();
        }

        if (_currentAction == MenuToolsLRFragment.actionType.PRESENT_PASSWORD) { // retrieve
                                                                                // demo
                                                                                // configuration
            PasswordData[0] = (byte) launcherintent.getIntExtra("pwdValue1", 0);
            PasswordData[1] = (byte) launcherintent.getIntExtra("pwdValue2", 0);
            PasswordData[2] = (byte) launcherintent.getIntExtra("pwdValue3", 0);
            PasswordData[3] = (byte) launcherintent.getIntExtra("pwdValue4", 0);
            PasswordNumber  = (byte) launcherintent.getIntExtra("pwdSector", 0);
        }

        if (_currentAction == MenuToolsLRFragment.actionType.LOCK_SECTOR) { // retrieve
            // demo
            // configuration
            block = (int) launcherintent.getIntExtra("block", 0);
            LockConfig = (byte) launcherintent.getIntExtra("LockConfig", 0);
            PasswordNumber = (byte) launcherintent.getIntExtra("PasswordNumber", 0);

        }


        // Check for enabled NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcState = NfcState.STATE_NFC_ENABLED;
        // Create the PendingIntent, Filters and technologies that will be used
        // in onResume, either after NFC_NOT_ENABLED or NFC_ENABLED states
        // nfcPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this,
        // TagInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Intent intent = new Intent(WaitForNFCTapLRDemo.this, WaitForNFCTapLRDemo.class);
        intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    @Override
    protected void onResume() {
        Log.v(this.getClass().getName(), "OnResume Activity");

        super.onResume();

        // In this activity, no more active tag should be stored
        NFCApplication currentApp = (NFCApplication) getApplication();
        // currentApp.setCurrentTag(null);
        // Update application header
        NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) getSupportFragmentManager()
                .findFragmentById(R.id.WcActNFCAppHeaderFragmentId);
        mHeadFrag.onTagChanged(null);

        // Check for available NFC Adapter
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC not available
            nfcState = NfcState.STATE_NFC_NOT_AVAILABLE;
        } else {

            if (!nfcAdapter.isEnabled()) {
                // NFC not enabled
                nfcState = NfcState.STATE_NFC_NOT_ENABLED;
            } else {
                nfcState = NfcState.STATE_NFC_ENABLED;

                // Start the waves animations
                ImageView nfcWavesImg = (ImageView) findViewById(R.id.NfcWavesImgId);
                nfcWavesImg.setBackgroundResource(R.drawable.nfc_waves_anim);
                nfcWavesAnim = (AnimationDrawable) nfcWavesImg.getBackground();
                nfcWavesAnim.start();

                // Route the NFC events to the next activity (Tag Info ?)
                nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null /* nfcFiltersArray */,
                        null /* nfcTechLists */);
                setMainText(nfcState);

            }
        }

        // Enable SettingsDemo mode if required

    }

    @Override
    protected void onPause() {
        Log.v(this.getClass().getName(), "OnPause Activity");

        super.onPause();
        if (nfcState == NfcState.STATE_NFC_ENABLED) {
            // Stop the waves animations... if running
            nfcWavesAnim.stop();
            ImageView nfcWavesImg = (ImageView) findViewById(R.id.NfcWavesImgId);
            nfcWavesImg.setBackgroundResource(R.drawable.nfc_wave_empty);
        }
        nfcAdapter.disableForegroundDispatch(this);
        return;
    }

    private void proccessRequiredActivity(Intent intent) {
        // In this activity, if a new tag is detected, it takes precedence over
        // the current one
        if ((((NFCApplication) getApplication()).getCurrentTag() != null)) {
            // Specific Toast for ST tags
            if (!NFCApplication.getApplication().getCurrentTag().getModel().contains("24SR")
                    && (!NFCApplication.getApplication().getCurrentTag().getModel().contains("SRTAG"))
                    && (!NFCApplication.getApplication().getCurrentTag().getModel().contains("24LR"))
                    && (!NFCApplication.getApplication().getCurrentTag().getModel().contains("ST25TA"))) {
                // showSTToast(_newTag);
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.Wait_For_Tap_Feature_not_available), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                // Now Handle the required Action
                onTagChanged(((NFCApplication) getApplication()).getCurrentTag());
            }
        }
        switch (this._currentAction) {
        case UNDEFINED_ACTION: {
            // must not happen..
            Log.d("TAG", "Creator INTENT is NULL");
            this.finish();
        }
            break;

        case PRESENT_PASSWORD: {
            Log.d("TAG", "Action  PRESENT_PASSWORD Demo required");
            m24LRPresentPassword(null,PasswordData, PasswordNumber);
            this.finish();
            break;
        }
        case LOCK_SECTOR: {
            Log.d("TAG", "Action  LOCK SECTOR Demo required");
            m24LRLockSector(null,block, LockConfig, PasswordNumber);
            this.finish();
            break;
        }
        default: {
            Log.d("TAG", "Action not yet handle by Wait4Tag Activity");
        }
            break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(this.getClass().getName(), "OnNewIntent Activity");

        super.onNewIntent(intent);

        if ((_mpingPongDemoInProgress == true) ) // Exchange
                                                                                        // data
                                                                                        // in
                                                                                        // progress
        {
            // tag already took in account.
            // request a new data exchange
            // restore systemVolume
            AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeControl.setStreamVolume(AudioManager.STREAM_NOTIFICATION, _mgetfunctionnalVolume, 0);
        }
        proccessRequiredActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    private int m24LRLockSector(Ndef ndefTag, int block, byte lLockConfig, byte lPasswordNumber) {
        Log.d(TAG, " updateProtectionLockSector");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        while ((ret = currentTag.pingTag()) != true) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.e(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " presentPassword Action");
            // STNfcTagVHandler tgh =
            // (STNfcTagVHandler)(currentTag.getSTTagHandler());
            byte[] SectorNumberAddress = null;
            byte LockSectorByte = (byte)0x00;

            int intSectorAddress = block * 0x20;
            SectorNumberAddress = Helper.ConvertIntTo2bytesHexaFormat(intSectorAddress);
            LockSectorByte = (byte)((byte)(LockConfig<<1) | (byte)(PasswordNumber<<3) | (byte)0x01);

            GenErrorAppReport err = ((STNfcTagVHandler) (currentTag.getSTTagHandler())).lockSector(SectorNumberAddress,
                    LockSectorByte);
            returncd = reportactionstatus(err.m_err_text, err.m_err_value);
            // this.finish();

        }
        return returncd;
    }

    private int m24LRPresentPassword(Ndef ndefTag, byte[] password, byte sector) {
        Log.d(TAG, " presentPassword");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        while ((ret = currentTag.pingTag()) != true) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.e(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " presentPassword Action");
            // STNfcTagVHandler tgh =
            // (STNfcTagVHandler)(currentTag.getSTTagHandler());
            GenErrorAppReport err = ((STNfcTagVHandler) (currentTag.getSTTagHandler())).presentPassword(sector,
                    password);
            returncd = reportactionstatus(err.m_err_text, err.m_err_value);
            // this.finish();

        }
        return returncd;
    }







    static byte[] toBytes(int i) {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i /* >> 0 */);

        return result;
    }

    private void muteNfcNotification() {
        // mute Notification sound ..
        AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // _mgetfunctionnalVolume =
        // volumeControl.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        volumeControl.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
    }

    private void restoreNfcNotification() {
        AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeControl.setStreamVolume(AudioManager.STREAM_NOTIFICATION, _mgetfunctionnalVolume, 0);
    }

    private void muteNfcNotification(Ndef ndefTag) {
        try {
            ndefTag.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Tag is out of range now... try to perform connection..

        // mute Notification sound ..
        AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        _mgetfunctionnalVolume = volumeControl.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        volumeControl.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);

    }



    private void clearDemoPingPingMode(String Message, boolean success) {
        // Display Dialog Error Message
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        restoreNfcNotification();
        if (success == true) {
            dlgAlert.setTitle("SUCCESS");
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // display Tx RX layout
                    LayoutInflater mInflater = (LayoutInflater) NFCApplication.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    RelativeLayout PingPingRes = (RelativeLayout) findViewById(R.id.PingPongResLayout); // Parent
                                                                                                        // View
                                                                                                        // ->
                                                                                                        // to
                                                                                                        // make
                                                                                                        // it
                                                                                                        // visible

                    for (int i = 0; i < _mpingpongSet.loop; i++) {
                        LinearLayout llItem = (LinearLayout) mInflater.inflate(R.layout.txrxcell, null); // Create
                                                                                                            // View
                        TextView TX = (TextView) llItem.findViewById(R.id.PingPongResTX);
                        TextView RX = (TextView) llItem.findViewById(R.id.PingPongResRX);
                        TX.setText(String.valueOf(_moutPingPongValue[i]));
                        RX.setText(String.valueOf(_minPingPongValue[i]));
                        LinearLayout scrollerLayout = (LinearLayout) findViewById(R.id.txrxcontainer);
                        scrollerLayout.addView(llItem);
                    }

                    PingPingRes.setVisibility(View.VISIBLE);
                    // PingPingRes.invalidate();
                    Button doneButton = (Button) findViewById(R.id.rxtxDone);
                    doneButton.setActivated(true);
                }
            });
        } else {
            dlgAlert.setTitle("ERROR");
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // dismiss the dialog
                    finish();
                }
            });
        }

        dlgAlert.setMessage(Message);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }



    // M24SR Discovery kit - Ping Pong Demo
    // - M24SR Initiate Demonstration by writting a specific UID message in
    // M24SR.
    // - If UID is validated by phone PingPong exchange process is launch.

    protected void m24srDiscoveryKitPingPongDemo(NFCTag nfcTag) {
        Log.v(this.getClass().getName(), "Start Ping Pong Demo Discoverykit Procedure");
        Ndef NdefTypeTag;
        NdefRecord[] NdefRecordsArray = null;
        Ndef ndefTag;
        NdefMessage ndefMessage;
        NdefRecord[] ndefRecord;

        if (_mpingPongDemoInProgress == false) {
            _mpingPongDemoInProgress = true;
            _mpingPongDemoState = pingPongDemoState.UID_PENDING;
            Toast toast = Toast.makeText(getApplicationContext(), "WAIT FOR TAP M24SRDiscovery Kit",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            // pingPongDemoState
            // get Tag Type and check if tag type correspond to the expected
            // one.
            try {
                ndefTag = Ndef.get(nfcTag.getTag());
                ndefTag.connect();
                NdefRecordsArray = ndefTag.getNdefMessage().getRecords();
                ndefTag.close();
            } catch (IOException e) {
                Log.e(TAG, " PingPong Demo Mode - IOException on NdefRecord recovery");
                e.printStackTrace();
            } catch (FormatException e) {
                Log.e(TAG, " PingPong Demo Mode - FormatException on NdefRecord recovery");
                e.printStackTrace();
            }

            String CurrentType = "";

            try {
                CurrentType = new String(NdefRecordsArray[0].getType(), "UTF-8");
            } catch (UnsupportedEncodingException e) {

            }

            // Consider the first record as the demo handle single record in
            // NDEF Message
            if ((NdefRecordsArray != null) && (NdefRecordsArray.length != 0) && (NdefRecordsArray[0] != null)) {
                switch (_mpingPongDemoState) {
                case UID_PENDING: {
                    if (!CurrentType.equals("st.com:" + NDEF_UID_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Setting Demo Mode - Error NDEF message - Expect UID Type Message");
                        // stop Demo
                        clearDemoPingPingMode("Expect UID Type Message", false);
                    } else // parse message
                    {
                        if (!Arrays.equals(NdefRecordsArray[0].getPayload(), NDEF_UID_PINGPONG_DEMO)) {
                            clearDemoPingPingMode("Wrong UID Value - tag not identifed", false);
                        } else {
                            // write ACK message
                            // build Config Payload
                            // offset as u8 and loop as u8
                            byte[] payload = new byte[2];
                            payload[0] = (byte) (_mpingpongSet.offset & 0xFF);
                            payload[1] = (byte) (_mpingpongSet.loop & 0xFF);
                            writeNdefMessage(NDEF_CFG_EXT_TYPE, payload);
                            // set App in wait for request mode
                            _mpingPongDemoState = pingPongDemoState.ACK_PENDING;
                        }
                    }
                }
                    break;
                case ACK_PENDING: {
                    if (!CurrentType.equals("st.com:" + NDEF_ACK_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Ping Pong Demo Mode - Error NDEF message - Expect ACK Type Message");
                        // stop Demo
                        clearDemoPingPingMode("Expect ACK Type Message", false);
                    } else // ACK Settings OK
                    {
                        _mpingPongCurrentValue = _mpingpongSet.start;
                        this._moutPingPongValue[0] = _mpingPongCurrentValue;
                        byte[] payload = new byte[4];
                        payload[0] = (byte) ((_mpingPongCurrentValue & 0xFF000000) >> 24);
                        payload[1] = (byte) ((_mpingPongCurrentValue & 0x00FF0000) >> 16);
                        payload[2] = (byte) ((_mpingPongCurrentValue & 0x0000FF00) >> 8);
                        payload[3] = (byte) ((_mpingPongCurrentValue & 0x000000FF));
                        // write ACK message
                        writeNdefMessage(NDEF_DATA_EXT_TYPE, payload);
                        // set App in wait for request mode
                        _mpingPongDemoState = pingPongDemoState.DATA_PENDING;
                    }
                }
                    break;
                case DATA_PENDING: {
                    if (!CurrentType.equals("st.com:" + NDEF_DATA_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Ping Pong Demo Mode - Error NDEF message - Expect DATA Type Message");
                        // stop Demo
                        clearDemoPingPingMode("Expect DATA Type Message", false);
                    } else // Demo Succeed - Display MSG status
                    {
                        // if loop nb not reached - read data - apply offset -
                        // write back new value

                        byte[] readpayload = NdefRecordsArray[0].getPayload().clone();
                        int value = 0;
                        if (readpayload.length == 4) {
                            value = ((readpayload[0] & 0x000000FF) << 24) + ((readpayload[1] & 0x000000FF) << 16)
                                    + ((readpayload[2] & 0x000000FF) << 8) + ((readpayload[3] & 0x000000FF));

                            this._minPingPongValue[_mcurrentloop] = value;
                            // TODO : need to store received and send value in
                            // array.
                            if (_mcurrentloop < (_mpingpongSet.loop - 1)) {
                                value = value + _mpingpongSet.offset;
                                byte[] payload = new byte[4];
                                payload[0] = (byte) ((value & 0xFF000000) >> 24);
                                payload[1] = (byte) ((value & 0x00FF0000) >> 16);
                                payload[2] = (byte) ((value & 0x0000FF00) >> 8);
                                payload[3] = (byte) ((value & 0x000000FF));
                                // write ACK message
                                writeNdefMessage(NDEF_DATA_EXT_TYPE, payload);
                                Log.d(TAG, " Ping Pong Demo Mode - write new value :" + value);
                                // set App in wait for request mode
                                _mcurrentloop++;
                                _moutPingPongValue[_mcurrentloop] = value;
                                _mpingPongDemoState = pingPongDemoState.DATA_PENDING;
                            } else // write PingPong finish exchange msg
                            {
                                writeNdefMessage(NDEF_END_EXT_TYPE, null);
                                _mpingPongDemoState = pingPongDemoState.END_PENDING;
                                Log.d(TAG, " Ping Pong Demo Mode - End Message Writen");
                            }
                        } else {
                            Log.d(TAG, " Ping Pong Demo Mode - wrong data size");
                            clearDemoPingPingMode("Ping Pong Demo Error - wrong data size !!", false);
                            _mpingPongDemoState = pingPongDemoState.UNDEFINED_STATE;
                        }
                    }

                }
                    break;
                case END_PENDING: {
                    if (!CurrentType.equals("st.com:" + NDEF_ACK_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Ping Pong Demo Mode - Error NDEF message - Expect ACK Type Message");
                        // stop Demo
                        clearDemoPingPingMode("Expect ACK Type Message to terminate process", false);
                    } else // Demo Succeed - Display MSG status
                    {
                        _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
                        clearDemoPingPingMode("M24SR Discovery Ping Pong Demo Done !!", true);
                    }

                }
                    break;
                default:
                    _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
                    clearDemoPingPingMode("M24SR Discovery Unknown State - Demo Terminated!!", false);

                    break;

                }
            } else {
                clearDemoSettingsmode("Cannot read NDEF Record from current Tag - Demo Terminated!!", true);
            }
        }
        muteNfcNotification();
    }


    private void clearDemoSettingsmode(String Message, boolean success) {
        // Display Dialog Error Message
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        restoreNfcNotification();

        if (success == true) {
            dlgAlert.setTitle("SUCCESS");
        } else {
            dlgAlert.setTitle("ERROR");
        }

        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // dismiss the dialog
                finish();
            }
        });
        dlgAlert.setMessage(Message);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

    }

    protected void writeNdefMessage(String extType, byte[] payload) {
        Ndef ndefTag = Ndef.get((NFCApplication.getApplication()).getCurrentTag().getTag());
        NdefRecord extRecord;
        NdefRecord[] records;
        NdefMessage msg;

        if (ndefTag == null)
            return;

        // Create NDEF Message
                extRecord = NdefRecord.createExternal("st.com", extType, payload);

        // Create the msg to be returned
        records = new NdefRecord[] { extRecord };
        msg = new NdefMessage(records);

        while (!ndefTag.isConnected())
            try {
                if (!ndefTag.isConnected()) {
                    ndefTag.connect();
                    Thread.sleep(10);
                }
            } catch (IOException e) {
                Log.e(this.getClass().getName(), "Exchange  Failure - IOException");
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.e(this.getClass().getName(), "Exchange  Failure - Thread Sleep ");
            }

        if (ndefTag.isConnected())
            try {
                ndefTag.writeNdefMessage(msg);
                ndefTag.close();
            } catch (IOException e) {
                Log.e(this.getClass().getName(), "Exchange  Failure - Format Exception");
                e.printStackTrace();
            } catch (FormatException e) {
                Log.e(this.getClass().getName(), "Exchange  Failure - Format Exception");
                e.printStackTrace();
            }

        this.muteNfcNotification(ndefTag);

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

        // Resources res = getResources();
        TextView msgTextView = (TextView) findViewById(R.id.WforTapActMainTxt1Id);
        // msgTextView.setTextSize(res.getInteger(R.integer.wc_act_nfc_not_avail_txt_size));
        msgTextView.setTypeface(font1, style1);
        msgTextView.setTextColor(color1);
        msgTextView.setText(text1);

        msgTextView = (TextView) findViewById(R.id.WforTapActMainTxt2Id);
        // msgTextView.setTextSize(res.getInteger(R.integer.wc_act_nfc_not_avail_txt_size));
        msgTextView.setTypeface(font2, style2);
        msgTextView.setTextColor(color2);
        msgTextView.setText(text2);
    }

    private void onTagChanged(NFCTag newTag) {
        // Empty Method for now
    }

    public void toastStatus(String status) {

        // Create Toast to inform user on the Tool request process.
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        WFNFCTAPToast = Toast.makeText(context, status, duration);
        WFNFCTAPToast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        WFNFCTAPToast.show();

    }

    private int reportactionstatus(String status, int i) {
        Log.d("TAG", status);
        toastStatus(status);
        return i;
    }


}
