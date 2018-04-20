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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

import com.st.Fragments.MenuM24SRDemoFragment;

import com.st.demo.TagMenuDetailsActivity;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NFC.NFCActivity;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagHandler;
import com.st.NFC.stnfchelper;
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
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
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

public class WaitForNFCTapM24SRDemo extends NFCActivity {
    // Constants for NFC feature state
    // - state is detected at class creation
    // - STATE_NFC_UNKNOWN is the initial state
    // - STATE_NFC_NOT_AVAILABLE is a final state (no NFC chip in current
    // device)
    // - STATE_NFC_NOT_ENABLED is a transient state: NFC activation can be
    // detected in onResume method (if user switched to paremeters menu and came
    // back to the application)
    // - STATE_NFC_ENABLED is a transient state: NFC can be deactivated by end
    // user, then need to be detected

    public enum NfcState {
        STATE_NFC_UNKNOWN, STATE_NFC_NOT_AVAILABLE, STATE_NFC_NOT_ENABLED, STATE_NFC_ENABLED
    }

    public enum settingsDemoState {
        UID_PENDING, REQUESTSETTINGSPENDING, SETTINGSACKPENDING, UNDEFINED_STATE
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

    private final byte[] NDEF_UID_UPLOAD_DEMO = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
            (byte) 0xFF, (byte) 0x11, (byte) 0x00 };
    private final byte[] NDEF_UID_SETTINGS_DEMO = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
            (byte) 0xFF, (byte) 0x11, (byte) 0x22 };
    private final byte[] NDEF_UID_LOGS_DEMO = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
            (byte) 0xFF, (byte) 0x11, (byte) 0x33 };
    private final byte[] NDEF_UID_PINGPONG_DEMO = { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
            (byte) 0xFF, (byte) 0x11, (byte) 0x44 };
    private final byte[] NDEF_REQ_SETTINGS_DEMO = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01 };

    private NfcAdapter nfcAdapter;
    private NfcState nfcState = NfcState.STATE_NFC_UNKNOWN;
    private PendingIntent nfcPendingIntent;

    private String TAG = "Wait4TapM24SRDemoActivity";
    private MenuM24SRDemoFragment.actionType _currentAction;

    private boolean _HZState;
    private int _mnbNdefFiles;

    private Toast WFNFCTAPToast;
    private boolean _useCurrentTag = false;

    private NDEFDiscoveryKitCtrlMessage _mdefDiscoveryKitCtrlMessage;
    byte[] _msettingsPayload;

    private int _mcounter = 0;
    private boolean _mexchangeInProgress = false; // Firmware Upload in progress
    private boolean _msettingDemoInProgress = false; // Settings Demo in
                                                        // progress
    private boolean _mpingPongDemoInProgress = false; // Ping Pong Demo in
                                                        // progress
    private boolean _mlogsDemoInProgress = false; // Logs Demo in progress
    private boolean _muploadDemoInProgress = false; // Logs Demo in progress

    private settingsDemoState _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
    private pingPongDemoState _mpingPongDemoState = pingPongDemoState.UNDEFINED_STATE;
    private logsDemoState _mlogsDemoState = logsDemoState.UNDEFINED_STATE;
    private uploadDemoState _muploadDemoState = uploadDemoState.UNDEFINED_STATE;

    private int _mpingPongCurrentValue = 0;
    private int _mcurrentloop = 0;
    private int[] _minPingPongValue;
    private int[] _moutPingPongValue;

    private PingPongSet _mpingpongSet;

    // private logs attributes used to handle logs message in logsDemoMessage
    private String _mlogTitle;
    private String _mXlabel;
    private String _mY1label;
    private String _mY2label;
    private int _mNBSample;
    private int _mX1ValueSize;
    private int _mY1ValueSize;
    private int _mY2ValueSize;
    private String[] _mXArrayValue;
    private String[] _mY1ArrayValue;
    private String[] _mY2ArrayValue;

    private byte[] _mlogspayload;

    // Data Upload
    int _mdataLength = 1 * 1024; // 23 kBytes
    int _mchunkSize = 250; // 250 Bytes
    int _muploadedData = 0;
    byte[] _muploadBinary = null;

    /**
     * Folder of the FW natively provided with the rx95hf demo application
     */
    public static File firmwareRepo = null;
    public static String FirmwareApplicationDirPath;

    // Audio Management Attributes
    int _mgetfunctionnalVolume = 0;

    // Cosmetics
    private AnimationDrawable nfcWavesAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_tap_screen_m24srdemo);
    //    final View _curFragmentView = findViewById(R.layout.wait_for_tap_screen_m24srdemo);

        _mpingpongSet = new PingPongSet();
        _mexchangeInProgress = false;
        _msettingDemoInProgress = false;
        _mlogsDemoInProgress = false;
        _mpingPongDemoInProgress = false;
        _muploadDemoInProgress = false;

        // State machin Init
        _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
        _mpingPongDemoState = pingPongDemoState.UNDEFINED_STATE;
        _mlogsDemoState = logsDemoState.UNDEFINED_STATE;
        _muploadDemoState = uploadDemoState.UNDEFINED_STATE;

        // PingPong Default settings
        _mpingPongCurrentValue = 0;
        _mcurrentloop = 0;
        _mnbNdefFiles = 1;

        Button doneButton = (Button) findViewById(R.id.rxtxDone);
        doneButton.setActivated(true);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // store data from Fragment DiscoveryKitCtrl

                // View rlLayout = v.findViewById(R.id.PingPongResLayout);
                // rlLayout.setVisibility(View.GONE);
                finish();
            }

        });

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
        _currentAction = MenuM24SRDemoFragment.actionType.UNDEFINED_ACTION;
        _currentAction = (MenuM24SRDemoFragment.actionType) launcherintent
                .getSerializableExtra(MenuM24SRDemoFragment.WAIT_FOR_TAP_ACTION);

        // retrieve nb Ndef files
        if (_currentAction == MenuM24SRDemoFragment.actionType.FORMAT_NDEF) {
            _mnbNdefFiles = (int) launcherintent.getIntExtra("NbFiles", 1);
        }

        if (_currentAction == MenuM24SRDemoFragment.actionType.UNDEFINED_ACTION) {
            // must not happen..
            Log.d("TAG", "ACTION is UNDEFINED");
            this.finish();
        }

        if (_currentAction == MenuM24SRDemoFragment.actionType.PINGPONGDEMO) { // retrieve
                                                                                // demo
                                                                                // configuration
            _mpingpongSet.loop = launcherintent.getIntExtra("loopValue", 0);
            _mpingpongSet.start = launcherintent.getIntExtra("startValue", 0);
            _mpingpongSet.offset = launcherintent.getIntExtra("offsetValue", 0);
            _minPingPongValue = (int[]) new int[_mpingpongSet.loop];
            _moutPingPongValue = (int[]) new int[_mpingpongSet.loop];
        }

        if (_currentAction == MenuM24SRDemoFragment.actionType.START_EXCHANGE) {
            // Data Upload

            _mchunkSize = 4 * 1024; // 250 Bytes
            _muploadedData = 0;

            // initialize buffer..
            try {
                // = assetManager.open("m24srFirmware.bin");
                AssetManager assetManager = getResources().getAssets();
                InputStream stream = null;
                stream = assetManager.open("m24srfirmware.bin");
                _muploadBinary = IOUtil.toByteArray(stream);
                _mdataLength = _muploadBinary.length;
            } catch (IOException e) {
                _muploadBinary = new byte[_mdataLength];
                Random random = new Random();
                for (int i = 0; i < _muploadBinary.length; i++) {
                    _muploadBinary[i] = (byte) random.nextInt(255);
                }
                _mdataLength = 23 * 1024; // 7 kBytes
            }
        }

        // Retrieve payload for M24SR Settings demo
        _msettingsPayload = launcherintent.getByteArrayExtra("serializePayload");
        this._mdefDiscoveryKitCtrlMessage = new NDEFDiscoveryKitCtrlMessage();
        if ((_msettingsPayload != null) && (_msettingsPayload.length != 0)) {
            _mdefDiscoveryKitCtrlMessage.parseAndSetNDEFMessage(_msettingsPayload);
        }

        // Check for enabled NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcState = NfcState.STATE_NFC_ENABLED;
        // Create the PendingIntent, Filters and technologies that will be used
        // in onResume, either after NFC_NOT_ENABLED or NFC_ENABLED states
        // nfcPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this,
        // TagInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Intent intent = new Intent(WaitForNFCTapM24SRDemo.this, WaitForNFCTapM24SRDemo.class);
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
        if ((this._currentAction == MenuM24SRDemoFragment.actionType.START_SETTINGS_DEMO)
                && (_msettingDemoInProgress == false)) {
            m24srDiscoveryKitSettingsDemo(null);
        } else if ((this._currentAction == MenuM24SRDemoFragment.actionType.PINGPONGDEMO)
                && (this._mpingPongDemoInProgress == false)) {
            m24srDiscoveryKitPingPongDemo(null);
        } else if ((this._currentAction == MenuM24SRDemoFragment.actionType.LOGSDEMO)
                && (this._mlogsDemoInProgress == false)) {
            m24srDiscoveryKitLogsDemo(null);
        } else if ((this._currentAction == MenuM24SRDemoFragment.actionType.START_EXCHANGE)
                && (this._muploadDemoInProgress == false)) {
            m24srDiscoveryKitUploadDemo(null);
        }
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
        case FORMAT_NDEF: {
            Log.d("TAG", "Action  Format required");
//            m24srFormatNDEF(_mnbNdefFiles);
            xxxFormatNDEF(_mnbNdefFiles);
            this.finish();
        }
            break;
        case DRIVE_IT: {
            Log.d("TAG", "Action  Drive IT required");
            m24srSendIT();
            this.finish();
        }
            break;

        case START_EXCHANGE: {
            Log.d("TAG", "Action  Exchange Data required");
            // m24srExchangeData(null);
            m24srDiscoveryKitUploadDemo(((NFCApplication) getApplication()).getCurrentTag());
        }
            break;

        case START_SETTINGS_DEMO: {
            Log.d("TAG", "Action  Settings Demo required");
            m24srDiscoveryKitSettingsDemo(((NFCApplication) getApplication()).getCurrentTag());
            break;
        }
        case LOGSDEMO: {
            Log.d("TAG", "Action  Logs Demo required");
            m24srDiscoveryKitLogsDemo(((NFCApplication) getApplication()).getCurrentTag());
            break;
        }
        case PINGPONGDEMO: {
            Log.d("TAG", "Action  Settings Demo required");
            m24srDiscoveryKitPingPongDemo(((NFCApplication) getApplication()).getCurrentTag());
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

        if ((_mexchangeInProgress == true) || (_msettingDemoInProgress == true) || (_mpingPongDemoInProgress == true)
                || (_mlogsDemoInProgress == true) || (_muploadDemoInProgress == true)) // Exchange
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

    private void clearDemologsmode(String Message, boolean success) {
        // Display Dialog Error Message
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        restoreNfcNotification();
        if (success == true) {
            dlgAlert.setTitle("SUCCESS");
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // dismiss the dialog
                    Intent intent = new Intent(NFCApplication.getContext(), PlotingActivity.class);// mContext
                                                                                                    // is
                                                                                                    // the
                                                                                                    // Context
                                                                                                    // variable
                                                                                                    // over
                                                                                                    // here.
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("datalogs", _mlogspayload);
                    startActivity(intent);
                    finish();
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

    private void parseLogs(byte[] payload) {

        if (payload.length != 0) {
            int payloadOffset = 0;
            _mlogspayload = payload.clone();

            // retrieve Log Title
            byte[] logTitle = new byte[payload[0] & 0xFF];
            System.arraycopy(payload, 1, logTitle, 0, payload[0] & 0xFF);
            payloadOffset = ((int) payload[0]) + 1;

            try {
                _mlogTitle = new String(logTitle, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            ;

            // extract X unit label
            byte[] xlabel = new byte[payload[payloadOffset]];
            System.arraycopy(payload, payloadOffset + 1, xlabel, 0, payload[payloadOffset]);
            payloadOffset = (payload[payloadOffset]) + 1 + payloadOffset;

            try {
                _mXlabel = new String(xlabel, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            ;

            // extract Y1 unit label
            byte[] y1label = new byte[payload[payloadOffset] & 0xFF];
            System.arraycopy(payload, payloadOffset + 1, y1label, 0, payload[payloadOffset]);
            payloadOffset = (payload[payloadOffset]) + 1 + payloadOffset;

            try {
                _mY1label = new String(y1label, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            ;

            // extract Y2 unit label
            byte[] y2label = new byte[payload[payloadOffset] & 0xFF];
            System.arraycopy(payload, payloadOffset + 1, y2label, 0, payload[payloadOffset]);
            payloadOffset = (payload[payloadOffset]) + 1 + payloadOffset;
            try {
                _mY2label = new String(y2label, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            ;

            // retrieve nb samples value
            _mNBSample = (payload[payloadOffset]);
            payloadOffset = payloadOffset + 1;

            // retrieve X1 value size
            _mX1ValueSize = (payload[payloadOffset]);
            payloadOffset = payloadOffset + 1;

            // retrieve Y1 value size
            _mY1ValueSize = (payload[payloadOffset]);
            payloadOffset = payloadOffset + 1;

            // retrieve Y2 value size
            _mY2ValueSize = (payload[payloadOffset]);
            payloadOffset = payloadOffset + 1;

            // instantiate Value array

            _mXArrayValue = (String[]) new String[_mNBSample];
            _mY1ArrayValue = (String[]) new String[_mNBSample];
            _mY2ArrayValue = (String[]) new String[_mNBSample];

            byte[] tempXArray = new byte[_mX1ValueSize];
            byte[] tempY1Array = new byte[_mY1ValueSize];
            byte[] tempY2Array = new byte[_mY2ValueSize];

            for (int i = 0; i < _mNBSample; i++) {
                System.arraycopy(payload, payloadOffset, tempXArray, 0, _mX1ValueSize);
                payloadOffset = payloadOffset + _mX1ValueSize;
                try {
                    _mXArrayValue[i] = new String(tempXArray, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    _mXArrayValue[i] = new String("0");
                }
                ;
            }
            for (int i = 0; i < _mNBSample; i++) {
                System.arraycopy(payload, payloadOffset, tempY1Array, 0, _mY1ValueSize);
                payloadOffset = payloadOffset + _mY1ValueSize;
                try {
                    _mY1ArrayValue[i] = new String(tempY1Array, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    _mY1ArrayValue[i] = new String("0");
                }
                ;
            }
            for (int i = 0; i < _mNBSample; i++) {
                System.arraycopy(payload, payloadOffset, tempY2Array, 0, _mY2ValueSize);
                payloadOffset = payloadOffset + _mY2ValueSize;
                try {
                    _mY2ArrayValue[i] = new String(tempY2Array, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    _mY2ArrayValue[i] = new String("0");
                }
                ;
            }
        }

    }

    protected void m24srDiscoveryKitLogsDemo(NFCTag nfcTag) {
        Log.v(this.getClass().getName(), "Start Logs Discoverykit Procedure");
        Ndef NdefTypeTag;
        NdefRecord[] NdefRecordsArray = null;

        Ndef ndefTag;
        NdefMessage ndefMessage;
        NdefRecord[] ndefRecord;

        if (_mlogsDemoInProgress == false) {
            _mlogsDemoInProgress = true;
            _mlogsDemoState = logsDemoState.UID_PENDING;
            Toast toast = Toast.makeText(getApplicationContext(), "WAIT FOR TAP M24SRDiscovery Kit",
                    Toast.LENGTH_SHORT);
            toast.show();
            // App is waiting for UID Message
        } else {
            // get Tag Type and check if tag type correspond to the expected
            // one.
            try {
                ndefTag = Ndef.get(nfcTag.getTag());
                ndefTag.connect();
                NdefRecordsArray = ndefTag.getNdefMessage().getRecords();
                ndefTag.close();
            } catch (IOException e) {
                Log.e(TAG, " Logs Demo Mode - IOException on NdefRecord recovery");
                e.printStackTrace();
            } catch (FormatException e) {
                Log.e(TAG, " Logs Demo Mode - FormatException on NdefRecord recovery");
                e.printStackTrace();
            }

            // Log.d(TAG,"current Type : "+ CurrentType);
            // Consider the first record as the demo handle single record in
            // NDEF Message
            if ((NdefRecordsArray != null) && (NdefRecordsArray.length != 0) && (NdefRecordsArray[0] != null)) {
                String CurrentType = "";

                try {
                    CurrentType = new String(NdefRecordsArray[0].getType(), "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                switch (_mlogsDemoState) {
                case UID_PENDING: {
                    if (!CurrentType.equals("st.com:" + NDEF_UID_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Logs Demo Mode - Error NDEF message - Expect UID Type Message");
                        // stop Demo
                        clearDemologsmode("Expect UID Type Message", false);
                    } else // parse message
                    {
                        if (!Arrays.equals(NdefRecordsArray[0].getPayload(), NDEF_UID_LOGS_DEMO)) {
                            clearDemologsmode("Wrong UID Value - tag not identifed", false);
                        } else {
                            // write REQ LOGS message
                            writeNdefMessage(NDEF_REQ_EXT_TYPE, null);
                            // set App in wait for request mode
                            _mlogsDemoState = logsDemoState.DATA_PENDING;
                        }
                    }
                }
                    break;
                case DATA_PENDING: {
                    if (!CurrentType.equals("st.com:" + NDEF_DATA_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Logs Demo Mode - Error NDEF message - Expect DATA Type Message");
                        // stop Demo
                        clearDemologsmode("Expect DATA Type Message", false);
                    } else // DATA Settings OK
                    {
                        parseLogs(NdefRecordsArray[0].getPayload());

                        // write ACK message
                        writeNdefMessage(NDEF_ACK_EXT_TYPE, null);
                        // set App in wait for request mode
                        _mlogsDemoState = logsDemoState.UNDEFINED_STATE;
                        // display values ... here
                        // clearDemoSettingsmode("M24SR Discovery Done - Logs
                        // Demo Terminated!!");
                        clearDemologsmode("M24SR Discovery Done - Logs Demo Terminated!!", true);
                    }
                }
                    break;
                default:
                    _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
                    clearDemologsmode("M24SR Discovery Unknown State - Demo Terminated!!", false);

                    break;

                }
            } else {
                clearDemologsmode("Cannot read NDEF Record from current Tag - Demo Terminated!!", false);
            }
        }

        muteNfcNotification();
    }

    // M24SR Discovery Kit Settings Demonstration use case.
    // - M24SR Discovery Kit initiate Process by writting an UID message in
    // M24SR
    // - if UID is recognized to be a

    protected void m24srDiscoveryKitSettingsDemo(NFCTag nfcTag) {
        Log.v(this.getClass().getName(), "Start Setting-In Discoverykit Procedure");
        Ndef NdefTypeTag;
        NdefRecord[] NdefRecordsArray = null;

        Ndef ndefTag;
        NdefMessage ndefMessage;
        NdefRecord[] ndefRecord;

        if (_msettingDemoInProgress == false) {
            _msettingDemoInProgress = true;
            _msettingDemoState = settingsDemoState.UID_PENDING;
            Toast toast = Toast.makeText(getApplicationContext(), "WAIT FOR TAP M24SRDiscovery Kit",
                    Toast.LENGTH_SHORT);
            toast.show();
            // App is waiting for UID Message
        } else {
            // get Tag Type and check if tag type correspond to the expected
            // one.

            try {
                ndefTag = Ndef.get(nfcTag.getTag());
                ndefTag.connect();
                NdefRecordsArray = ndefTag.getNdefMessage().getRecords();
                ndefTag.close();
            } catch (IOException e) {
                Log.e(TAG, " Setting Demo Mode - IOException on NdefRecord recovery");
                e.printStackTrace();
            } catch (FormatException e) {
                Log.e(TAG, " Setting Demo Mode - FormatException on NdefRecord recovery");
                e.printStackTrace();
            }

            muteNfcNotification();
            // Consider the first record as the demo handle single record in
            // NDEF Message
            if ((NdefRecordsArray != null) && (NdefRecordsArray.length != 0) && (NdefRecordsArray[0] != null)) {
                switch (_msettingDemoState) {
                case UID_PENDING: {
                    if ((NdefRecordsArray[0].getType().toString()).equals("st.com:" + NDEF_UID_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Setting Demo Mode - Error NDEF message - Expect UID Type Message");
                        // stop Demo
                        clearDemoSettingsmode("Expect UID Type Message", false);
                    } else // parse message
                    {
                        if (!Arrays.equals(NdefRecordsArray[0].getPayload(), NDEF_UID_SETTINGS_DEMO)) {
                            clearDemoSettingsmode("Wrong UID Value - tag not identifed", false);
                        } else {
                            // write ACK message
                            writeNdefMessage(NDEF_ACK_EXT_TYPE, null);
                            // set App in wait for request mode
                            _msettingDemoState = settingsDemoState.REQUESTSETTINGSPENDING;
                        }
                    }
                }
                    break;
                case REQUESTSETTINGSPENDING: {
                    if (NdefRecordsArray[0].getType().toString().equals("st.com:" + NDEF_REQ_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Setting Demo Mode - Error NDEF message - Expect REQ Type Message");
                        // stop Demo
                        clearDemoSettingsmode("Expect REQ Type Message", false);
                    } else // CFG Settings OK
                    {
                        if (!Arrays.equals(NdefRecordsArray[0].getPayload(), NDEF_REQ_SETTINGS_DEMO)) {
                            clearDemoSettingsmode("Wrong REQ Value - Request Not identified", false);
                        } else {
                            // write ACK message
                            writeNdefMessage(NDEF_CFG_EXT_TYPE, _msettingsPayload);
                            // set App in wait for request mode
                            _msettingDemoState = settingsDemoState.SETTINGSACKPENDING;
                        }
                    }
                }
                    break;
                case SETTINGSACKPENDING: {
                    if (NdefRecordsArray[0].getType().toString().equals("st.com:" + NDEF_ACK_EXT_TYPE)) {
                        // received unexpected Message
                        Log.e(TAG, " Setting Demo Mode - Error NDEF message - Expect ACK Type Message");
                        // stop Demo
                        clearDemoSettingsmode("Expect ACK Type Message", false);
                    } else // Demo Succeed - Display MSG status
                    {
                        _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
                        clearDemoSettingsmode("M24SR Discovery Configuration Done !!", true);
                    }

                }
                    break;
                default:
                    _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
                    clearDemoSettingsmode("M24SR Discovery Unknown State - Demo Terminated!!", false);

                    break;

                }
            } else {
                clearDemoSettingsmode("Cannot read NDEF Record from current Tag - Demo Terminated!!", false);
            }
        }

    }

    // M24SR Discovery Kit Settings Demonstration use case.
    // - M24SR Discovery Kit initiate Process by writting an UID message in
    // M24SR
    // - if UID is recognized to be a

    protected void m24srDiscoveryKitUploadDemo(NFCTag nfcTag) {
        Log.v(this.getClass().getName(), "Start Upload-In Discoverykit Procedure");
        Ndef NdefTypeTag;
        NdefRecord[] NdefRecordsArray = null;

        Ndef ndefTag;
        NdefMessage ndefMessage;
        NdefRecord[] ndefRecord;

        if (_muploadDemoInProgress == false) {
            _muploadDemoInProgress = true;
            _muploadDemoState = uploadDemoState.UID_PENDING;
            Toast toast = Toast.makeText(getApplicationContext(),
                    "WAIT FOR TAP M24SRDiscovery Kit to start upload process", Toast.LENGTH_SHORT);
            toast.show();
            // App is waiting for UID Message
        } else {
            // get Tag Type and check if tag type correspond to the expected
            // one.

            try {
                ndefTag = Ndef.get(nfcTag.getTag());
                ndefTag.connect();
                NdefRecordsArray = ndefTag.getNdefMessage().getRecords();
                Log.d(TAG, " NDEF MAX SIZE : " + ndefTag.getMaxSize());
                ndefTag.close();
            } catch (IOException e) {
                Log.e(TAG, " upload Demo Mode - IOException on NdefRecord recovery");
                e.printStackTrace();
            } catch (FormatException e) {
                Log.e(TAG, " upload Demo Mode - FormatException on NdefRecord recovery");
                e.printStackTrace();
            }

            muteNfcNotification();

            // Log.d(TAG,"current Type : "+ CurrentType);
            // Consider the first record as the demo handle single record in
            // NDEF Message
            if ((NdefRecordsArray != null) && (NdefRecordsArray.length != 0) && (NdefRecordsArray[0] != null)) {
                String CurrentType = "";

                try {
                    CurrentType = new String(NdefRecordsArray[0].getType(), "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }

                // Consider the first record as the demo handle single record in
                // NDEF Message
                if ((NdefRecordsArray != null) && (NdefRecordsArray.length != 0) && (NdefRecordsArray[0] != null)) {
                    switch (_muploadDemoState) {
                    case UID_PENDING: {
                        if (!CurrentType.equals("st.com:" + NDEF_UID_EXT_TYPE)) {
                            // received unexpected Message
                            Log.e(TAG, " Upload Demo Mode - Error NDEF message - Expect UID Type Message");
                            // stop Demo
                            clearDemoSettingsmode("Expect UID Type Message", false);
                        } else // parse message
                        {
                            if (!Arrays.equals(NdefRecordsArray[0].getPayload(), NDEF_UID_UPLOAD_DEMO)) {
                                clearDemoSettingsmode("Wrong UID Value - tag not identifed", false);
                            } else // Start To send CFG
                            {
                                // write START message

                                byte[] payload = new byte[4];
                                payload[0] = (byte) ((_mdataLength & 0xFF00) >> 8);
                                payload[1] = (byte) (_mdataLength & 0x00FF);
                                payload[2] = (byte) ((_mchunkSize & 0xFF00) >> 8);
                                payload[3] = (byte) ((_mchunkSize & 0x00FF));
                                writeNdefMessage(NDEF_START_EXT_TYPE, payload);

                                // set App in wait for request mode
                                _muploadDemoState = uploadDemoState.ACK_PENDING;
                            }
                        }
                    }
                        break;
                    case ACK_PENDING: {
                        if ((!CurrentType.equals("st.com:" + NDEF_ACK_EXT_TYPE))
                                && (!CurrentType.equals("st.com:" + NDEF_NACK_EXT_TYPE))) {
                            // received unexpected Message
                            Log.e(TAG, " Upload Demo Mode - Error NDEF message - Expect NACK/ACK Type Message");
                            // stop Demo
                            clearDemoSettingsmode("Expect NACK/ACK Type Message", false);
                        } else // CFG Settings OK
                        {
                            // NACK USE case
                            if (CurrentType.equals("st.com:" + NDEF_NACK_EXT_TYPE)) {
                                // must not occurs for now
                                clearDemoSettingsmode(" NACK Type Message received", false);
                            }

                            byte[] payload = null;
                            if ((_muploadedData + _mchunkSize) < _muploadBinary.length) {
                                payload = new byte[_mchunkSize];
                                System.arraycopy(_muploadBinary, _muploadedData, payload, 0, _mchunkSize);
                                _muploadedData += _mchunkSize;
                            } else {
                                int remainDataSize = _muploadBinary.length - _muploadedData;
                                if (remainDataSize != 0) {
                                    payload = new byte[remainDataSize];
                                    System.arraycopy(_muploadBinary, _muploadedData, payload, 0, remainDataSize);
                                }
                                _muploadedData = _muploadBinary.length;
                            }
                            if (payload == null) {
                                // write end message
                                writeNdefMessage(NDEF_END_EXT_TYPE, null);
                                clearDemoSettingsmode("Upload Finised", true);

                            } else {
                                // write ACK message
                                writeNdefMessage(NDEF_DATA_EXT_TYPE, payload);
                                // set App in wait for request mode
                                _muploadDemoState = uploadDemoState.ACK_PENDING;// waiting
                                                                                // for
                                                                                // ACK
                                                                                // to
                                                                                // send
                                                                                // new
                                                                                // data
                            }

                        }
                    }
                        break;
                    default:
                        _msettingDemoState = settingsDemoState.UNDEFINED_STATE;
                        clearDemoSettingsmode("M24SR Discovery Unknown State - Demo Terminated!!", false);

                        break;

                    }
                } else {
                    clearDemoSettingsmode("Cannot read NDEF Record from current Tag - Demo Terminated!!", false);
                }
            }
        }

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

    protected void m24srExchangeData(Ndef ndefTag) {
        Log.v(this.getClass().getName(), "Start Exchange Data Procedure");
        NdefMessage msg = null;

        int chunksize = 7500;
        int datatoSendSize = chunksize * 10; // we will send 10 buffers of 255
                                                // byte each
        int nbChunck = datatoSendSize / chunksize + (((datatoSendSize % chunksize) != 0) ? 1 : 0);
        byte[] Arraychunkvalue = new byte[chunksize];
        java.util.Arrays.fill(Arraychunkvalue, (byte) 0x55);

        NdefRecord extRecord;
        NdefRecord[] records;

        // Write NDEF Message to M24SRDiscovery Kit
        // Wait ACK message from M24SRDiscovery Kit
        // Write a new message
        // Loop until End of procedure is reached
        // -- Reached End By app : End Message
        // -- Reached End By M24SR : End Message
        // Several Kind of message
        String StartTypeMsg = "m24sr_discopeer_startmsg";

        // Data Type Message
        String DataTypeMsg = "m24sr_discopeer_datamsg";
        // ACK Type Message
        String AckTypeMsg = "m24sr_discopeer_ackmsg";
        // NACK Type Message
        String NackTypeMsg = "m24sr_discopeer_nackmsg";
        // END Type Message
        String EndTypeMsg = "m24sr_discopeer_endmsg";
        // Do we need a Start Message ?

        byte[] payload = toBytes(_mcounter);

        if (_mcounter == 0) {
            // send the Start Message initiator with size of data and size of
            // chunk send per Ndef message
            byte[] startpayload = new byte[4];
            startpayload[0] = (byte) ((datatoSendSize & 0xFF00) >> 8);
            startpayload[1] = (byte) ((datatoSendSize & 0xFF));
            startpayload[2] = (byte) ((chunksize & 0xFF00) >> 8);
            startpayload[3] = (byte) ((chunksize & 0xFF));
            extRecord = NdefRecord.createExternal("st.com", StartTypeMsg, startpayload);
            // Create the msg to be returned
            records = new NdefRecord[] { extRecord };
            msg = new NdefMessage(records);
            _mcounter++;
        } else if (_mcounter <= nbChunck) {
            // Create NDEF Message - with counter
            extRecord = NdefRecord.createExternal("st.com", DataTypeMsg, Arraychunkvalue);
            // Create the msg to be returned
            records = new NdefRecord[] { extRecord };
            msg = new NdefMessage(records);
            _mcounter++;
        } else // end of communication
        {
            // Create NDEF Message - with counter
            extRecord = NdefRecord.createExternal("st.com", EndTypeMsg, payload);
            // Create the msg to be returned
            records = new NdefRecord[] { extRecord };
            msg = new NdefMessage(records);
            _mcounter = 0; // if == 0 Exchange data not in progress
            _mexchangeInProgress = false;
        }

        Log.v(this.getClass().getName(), "Current Counter value : " + _mcounter);

        if (ndefTag == null) {
            NFCTag currentTag = ((NFCApplication) getApplication()).getCurrentTag();
            ndefTag = Ndef.get(currentTag.getTag());
        }
        if (ndefTag == null)
            return;
        // Message is prepared - now write it
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
                // TODO Auto-generated catch block
                Log.e(this.getClass().getName(), "Exchange  Failure - Thread Sleep ");
            }
        // close NdefTech
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.e(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ndefTag.isConnected())
            try {
                ndefTag.writeNdefMessage(msg);
            } catch (IOException e) {
                Log.e(this.getClass().getName(), "Exchange  Failure - Format Exception");
                e.printStackTrace();
            } catch (FormatException e) {
                Log.e(this.getClass().getName(), "Exchange  Failure - Format Exception");
                e.printStackTrace();
            }

        if ((_mcounter <= (nbChunck + 1)) && _mexchangeInProgress)
            this.muteNfcNotification(ndefTag);
        else
            this.finish();
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


    public int xxxFormatNDEF(int nbNdefFiles) {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        // Check that the TAG is a M24SR
                if (!(currentTag.getModel().contains("24SR")))
                {
                    return reportactionstatus("Requested Format not supported by the presented Tag", 0);
                } else {

                    GenErrorAppReport err = currentTag.getSTTagHandler().FormatNDEF(currentTag,nbNdefFiles);
                    return reportactionstatus(err.m_err_text,err.m_err_value);
                }
    }

    public int m24srSendIT()
    {

        // Retrieve current TAG
        String reportStatus;
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        // Check that the TAG is a M24SR
        if (!(currentTag.getModel().contains("24SR")) && !(currentTag.getModel().contains("SRTAG")))
        {
            return reportactionstatus("Requested Lock Write not supported by the presented Tag", 0);
        }

        // Put the  system in a Select SYS File Mode.
        if (currentTag.setInSelectNDEFState(currentTag.getCurrentValideTLVBlokID()) != 1)
        {
            return reportactionstatus("Can not put the tag in SYS selected State", 0);
        }

        // SyS file  is now selected - we may check RF GPO config in SysFIle GPO
        STNfcTagHandler stTagHandler  = (STNfcTagHandler) currentTag.getSTTagHandler();

        if (!stTagHandler.m24srSTSendInterrupt())
        {
            return reportactionstatus(stTagHandler.getError().translate(),0);
        }

        return reportactionstatus("SEND IT REQUEST SUCCEEDED!",1);


    }



}
