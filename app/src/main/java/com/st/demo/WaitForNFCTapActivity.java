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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.st.Fragments.MenuToolsFragment;

import com.st.demo.TagMenuDetailsActivity;
import com.st.util.GenErrorAppReport;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NFC.NFCActivity;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagHandler;
import com.st.NFC.stnfchelper;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.os.Bundle;
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

public class WaitForNFCTapActivity extends NFCActivity {
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



    static final byte [] DEFAULT_PASSWORD = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
                                             0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    private NfcAdapter nfcAdapter;
    private NfcState nfcState = NfcState.STATE_NFC_UNKNOWN;
    private PendingIntent nfcPendingIntent;

    private String TAG = "Wait4TapActivity";
    private MenuToolsFragment.actionType _currentAction;
    private String _password;
    private byte[] _password128bitslong;
    private String _modificationpassword;
    private byte[] _modificationpassword128bitslong;
    private boolean _HZState;
    private int     _msetupCounter;
    private int _gpo_config ;
    private Toast WFNFCTAPToast;

    private NDEFDiscoveryKitCtrlMessage _mdefDiscoveryKitCtrlMessage;
    byte [] _msettingsPayload;



    // Audio Management Attributes
    int _mgetfunctionnalVolume = 0;


    // Cosmetics
    private AnimationDrawable nfcWavesAnim;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_for_tap_screen);
    //    final View _curFragmentView = findViewById(R.layout.wait_for_tap_screen);



        Button doneButton =(Button) findViewById(R.id.rxtxDone);
        doneButton.setActivated(true);
        doneButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                // store data from Fragment DiscoveryKitCtrl

                // View rlLayout = v.findViewById(R.id.PingPongResLayout);
                // rlLayout.setVisibility(View.GONE);
                finish();
            }

          });




          AudioManager volumeControl = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
          _mgetfunctionnalVolume = volumeControl.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        // Check for available NFC Adapter
        PackageManager pm = getPackageManager();

        Intent launcherintent = getIntent();
        if (launcherintent==null)
        {
            // must not happen..
            Log.d("TAG","Creator INTENT is NULL");
            this.finish();
        }

        // Retrieve parameters from intent
        _currentAction = MenuToolsFragment.actionType.UNDEFINED_ACTION;
        _currentAction = (MenuToolsFragment.actionType) launcherintent.getSerializableExtra(MenuToolsFragment.WAIT_FOR_TAP_ACTION);
         // look for string Password
        _password = "";
        _password  = (String) launcherintent.getStringExtra("password");

        // look for string modification password
        _modificationpassword = "";
        _modificationpassword = (String) launcherintent.getStringExtra("rightAccess");
        // need to convert the entered password into a 128 bits Key
        // Strategy HEXA string retrieve is converted in Bytes and then complete with 0xFF until Key is 128 bit longs.
        _password128bitslong = null;
        if (_password != null)
        {
            _password128bitslong = new byte[] {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
                                               (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
            try {
                System.arraycopy(stnfchelper.hexStringToByteArray(_password), 0, _password128bitslong, 0, _password.length() / 2);
            } catch (IllegalArgumentException ex) {
                Log.e(this.getClass().getName(), "Password: " + ex.toString());
            }
        }

        _modificationpassword128bitslong = null;

        if (_modificationpassword != null)
        {
            _modificationpassword128bitslong = new byte[] {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
                                               (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
            try {
                System.arraycopy(stnfchelper.hexStringToByteArray(_modificationpassword), 0, _modificationpassword128bitslong, 0, _modificationpassword.length() / 2);
            } catch(IllegalArgumentException ex) {
                Log.e(this.getClass().getName(), "Password: " + ex.toString());
            }
        }



        if (_currentAction == MenuToolsFragment.actionType.SETUP_COUNTER)
        {
            _msetupCounter = (int) launcherintent.getIntExtra("countersetup", 1);
        }

        if (_currentAction == MenuToolsFragment.actionType.SETSTATE_CFGGPO)
        {
            _gpo_config = (int) launcherintent.getIntExtra("CFGGPO", 1);
        }


        // retrieve HZState
        _HZState = (boolean) (launcherintent.getIntExtra("_HZState", 0) == 1? true:false);


        if (_currentAction == MenuToolsFragment.actionType.UNDEFINED_ACTION)
        {
            // must not happen..
            Log.d("TAG","ACTION is UNDEFINED");
            this.finish();
        }





        // Retrieve payload for M24SR Settings demo
        _msettingsPayload = launcherintent.getByteArrayExtra("serializePayload");
        this._mdefDiscoveryKitCtrlMessage = new NDEFDiscoveryKitCtrlMessage();
        if ((_msettingsPayload!=null) && (_msettingsPayload.length!=0))
        {
            _mdefDiscoveryKitCtrlMessage.parseAndSetNDEFMessage(_msettingsPayload);
        }

         // Check for enabled NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcState = NfcState.STATE_NFC_ENABLED;
            // Create the PendingIntent, Filters and technologies that will be used in onResume, either after NFC_NOT_ENABLED or NFC_ENABLED states
            //nfcPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, TagInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            Intent intent = new Intent(WaitForNFCTapActivity.this, WaitForNFCTapActivity.class);
            intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
            nfcPendingIntent = PendingIntent.getActivity(this, 0, intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

 @Override
 protected void onResume() 
 {
     Log.v(this.getClass().getName(), "OnResume Activity");

     super.onResume();

     // In this activity, no more active tag should be stored
    NFCApplication currentApp = (NFCApplication) getApplication();
    //currentApp.setCurrentTag(null);
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
             setMainText(nfcState);

        }
     }


 }


 @Override
 protected void onPause() 
 {
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
            if ((!NFCApplication.getApplication().getCurrentTag().getModel().contains("24SR")
                    && (!NFCApplication.getApplication().getCurrentTag().getModel().contains("SRTAG")))
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
        case LOCK_WRITE_ACCESS: {
            Log.d("TAG", "Action  LOCK WRITE ACCESS required");
            NDEFLockWrite();
            this.finish();
        }
            break;

        case UNLOCK_WRITE_ACCESS: {
            Log.d("TAG", "Action  UNLOCK WRITE ACCESS required");
            NDEFUnLockWrite();
            this.finish();
        }
            break;
        case LOCK_READ_ACCESS: {
            Log.d("TAG", "Action  LOCK READ ACCESS required");
            NDEFLockRead();
            this.finish();
        }
            break;
        case UNLOCK_READ_ACCESS: {
            Log.d("TAG", "Action  UNLOCK READ ACCESS required");
            NDEFUnLockRead();
            this.finish();
        }
            break;
        case SETSTATE_GPO: {
            Log.d("TAG", "Action  TOGGLE GPO required");
//            _HZState = (boolean) intent.getBooleanExtra("_HZState", false);
//            _HZState = (boolean) (intent.getIntExtra("_HZState", 0) == 1? true:false);
            ToggleGPO();
            this.finish();
        }
            break;
        case ERASE_NDEF: {
            Log.d("TAG", "Action  Erase required");
            EraseNDEF();
            this.finish();
        }
            break;

        case SETUP_COUNTER: {
            Log.d("TAG", "Action  Setup Counter Required");
            SetupCounter(_msetupCounter);
            this.finish();
        }
            break;
        case SETSTATE_CFGGPO: {
            Log.d("TAG", "Action  Setup Config GPO");
            SetupConfigGPO(_gpo_config);
            this.finish();
        }
            break;
            case SETSTATE_DVCFGGPO: {
                Log.d("TAG", "Action  Setup Config GPO");
                SetupConfigGPO(_gpo_config);
                this.finish();
            }
            break;
        default: {
            Log.d("TAG", "Action not yet handle by Wait4Tag Activity");
            this.finish();
        }
            break;
        }
    }
  
@Override
  protected void onNewIntent(Intent intent) 
  {
      Log.v(this.getClass().getName(), "OnNewIntent Activity");

      super.onNewIntent(intent);
      proccessRequiredActivity(intent);
  }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

static byte[] toBytes(int i)
{
      byte[] result = new byte[4];

      result[0] = (byte) (i >> 24);
      result[1] = (byte) (i >> 16);
      result[2] = (byte) (i >> 8);
      result[3] = (byte) (i /*>> 0*/);

      return result;
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

//        Resources res = getResources();
        TextView msgTextView = (TextView) findViewById(R.id.WforTapActMainTxt1Id);
//        msgTextView.setTextSize(res.getInteger(R.integer.wc_act_nfc_not_avail_txt_size));
        msgTextView.setTypeface(font1, style1);
        msgTextView.setTextColor(color1);
        msgTextView.setText(text1);

        msgTextView = (TextView) findViewById(R.id.WforTapActMainTxt2Id);
//        msgTextView.setTextSize(res.getInteger(R.integer.wc_act_nfc_not_avail_txt_size));
        msgTextView.setTypeface(font2, style2);
        msgTextView.setTextColor(color2);
        msgTextView.setText(text2);
    }

    private void onTagChanged (NFCTag newTag) {
        // Empty Method for now
    }
    
    public void toastStatus(String status)
    {

        // Create Toast to inform user on the Tool request process.
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        WFNFCTAPToast = Toast.makeText(context, status, duration);
        WFNFCTAPToast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
        WFNFCTAPToast.show();

    }
    
    private int reportactionstatus(String status, int i)
    {
        Log.d("TAG",status);
        toastStatus(status);
        return i;
    }
    
// Architecture
    
    public int NDEFLockWrite() {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().NDEFLockWrite(currentTag,this._password128bitslong,this.DEFAULT_PASSWORD);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }
 
    public int NDEFUnLockWrite() {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().NDEFUnLockWrite(currentTag,this._password128bitslong,this.DEFAULT_PASSWORD);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }

    public int NDEFLockRead() {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().NDEFLockRead(currentTag,this._password128bitslong,this._modificationpassword128bitslong,this.DEFAULT_PASSWORD);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }

    public int NDEFUnLockRead() {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().NDEFUnLockRead(currentTag,this._modificationpassword128bitslong,this.DEFAULT_PASSWORD);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }


    public int ToggleGPO() {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().ToggleGPO(currentTag,_HZState);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }

    public int EraseNDEF() {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().EraseNDEF(currentTag);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }

    public int SetupCounter(int setupCounter ) {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        GenErrorAppReport err = currentTag.getSTTagHandler().SetupCounter(currentTag,setupCounter);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }
//
    public int SetupConfigGPO(int gpo_mode) {
        NFCApplication currentApp = (NFCApplication) getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        STNfcTagHandler  sttagh = (STNfcTagHandler) currentTag.getSTTagHandler();
        GenErrorAppReport err = sttagh.SetupGPOConfig(currentTag,gpo_mode);
        return reportactionstatus(err.m_err_text,err.m_err_value);
    }

    
 


  
    
 
}
