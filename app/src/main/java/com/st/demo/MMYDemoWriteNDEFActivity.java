/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1747 $
  * Revision of last commit    :  $Rev: 1747 $
  * Date of last commit     :  $Date: 2016-03-11 19:30:55 +0100 (Fri, 11 Mar 2016) $ 
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


import com.st.NDEF.NDEFGenCtrlTranspMessage;
import com.st.NDEFUI.NDEFBTLeFragment;
import com.st.NDEFUI.NDEFGenCtrlTranspFragment;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler;
import com.st.util.*;
import com.st.NDEF.NDEFAarMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFSimplifiedMessageHandler;
import com.st.NDEF.NDEFSimplifiedMessageType;
import com.st.NDEF.NDEFTextMessage;
import com.st.NDEF.NDEFURIMessage;
import com.st.NDEF.NDEFVCardMessage;
import com.st.NDEF.stndefwritestatus;
import com.st.NDEF.stnfcndefhandler;
import com.st.NDEFUI.NDEFAarFragment;
import com.st.NDEFUI.NDEFBTHandoverFragment;
import com.st.NDEFUI.NDEFDiscoveryKitCtrlFragment;
import com.st.NDEFUI.NDEFMailFragment;
import com.st.NDEFUI.NDEFSimplifiedMessageFragment;
import com.st.NDEFUI.NDEFSmsFragment;
import com.st.NDEFUI.NDEFTextFragment;
import com.st.NDEFUI.NDEFURIFragment;
import com.st.NDEFUI.NDEFVCardFragment;
import com.st.NDEFUI.NDEFWifiHandoverFragment;
import com.st.NFC.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MMYDemoWriteNDEFActivity extends NFCActivity
        implements OnClickListener, OnItemSelectedListener, PasswordDialogFragment.NoticeDialogListener,
        InformDialogFragment.InformDialogListener, AlertDialogFragment.NoticeDialogListener {
    /*
     * Attributes
     */
    static final int PICK_CONTACT_REQUEST = 1; // The request code
    public static final int DIALOG_FRAGMENT_WRITE = 2;
    public static final int RESULT_OK = 101;
    public static final int EMAIL_SEND_ACTION = 9002;

    // Capture feature attributes
    // keep track of the camera capture Intent
    final int CAMERA_CAPTURE = 3;
    final int PIC_CROP = 4;

    // Captured picture uri
    private Uri picUri = null;
    private Bitmap thePic = null;

    static final String TAG = "Write NDEF";

    boolean _resumeFromNewIntent = false;
    // Store current displayed Fragment
    private Fragment _curFragment = null;

    // dirty code to prevent lost of spinner pos when other activity is launch
    private int _keepcurrentSpinerState;

    boolean _selectNDEFListDisplaystate;

    boolean _writeState = false;
    stndefwritestatus _writeStatus = stndefwritestatus.WRITE_STATUS_ERR_IO;
    boolean _sendEmailState = false;

    boolean _mfromWelcomScreen = false;
    boolean _mfromSmartNdefScreen = false;
    private boolean _writepending = false;

    // Cosmetics
    private AnimationDrawable nfcWavesAnim;
    ProgressDialog _dialog = null;


    stnfcndefhandler _mNDEFHandlertoWrite;

    String _mvCardExport;
    private NDEFSimplifiedMessage _curVcardMessage = null;
    private NDEFSimplifiedMessage _curMessage = null;
    private stnfcndefhandler _mndefMessageHandler;
    boolean _mexportVCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreate Activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ndef_write_list);

        findViewById(R.id.RlBActionEditText).setOnClickListener(this);
        findViewById(R.id.RlBActionEditURL).setOnClickListener(this);
        findViewById(R.id.RlBActionEditContact).setOnClickListener(this);
        findViewById(R.id.RlBActionEditm24srDiscoCtrl).setOnClickListener(this);
        findViewById(R.id.RlBActionEditwifi).setOnClickListener(this);
        findViewById(R.id.RlBActionEditbt).setOnClickListener(this);
        findViewById(R.id.RlBActionEditbtle).setOnClickListener(this);
        findViewById(R.id.RlBActionEditsms).setOnClickListener(this);
        findViewById(R.id.RlBActionEditmail).setOnClickListener(this);
        findViewById(R.id.RlBActionEditaar).setOnClickListener(this);
        findViewById(R.id.BasicBtnIdAddRecord).setOnClickListener(this);
        findViewById(R.id.BasicBtnIdSendEmail).setClickable(true);
        findViewById(R.id.BasicBtnIdSendEmail).setOnClickListener(this);

        findViewById(R.id.RlBasicBtnMultiRcds).setOnClickListener(this);

        _selectNDEFListDisplaystate = true;
        _writeState = false;

        _dialog = new ProgressDialog(MMYDemoWriteNDEFActivity.this);

        Intent intent = getIntent();
        _mfromWelcomScreen = (boolean) intent.getBooleanExtra("fromWelcomScreen", false);
        _mfromSmartNdefScreen =  (boolean) intent.getBooleanExtra("fromsmartndefScreen", false);

        // Setup "Write" button
        Button writeButton = (Button) findViewById(R.id.MnwActWriteBtnAreaId).findViewById(R.id.BasicBtnId);
        writeButton.setText(R.string.mnf_frag_write_tag_btn_txt);
        writeButton.setOnClickListener(this);

        Button addRecButton = (Button) findViewById(R.id.BasicBtnIdAddRecord).findViewById(R.id.BasicBtnId);
        addRecButton.setText(R.string.mnf_frag_add_rec_txt);
        addRecButton.setClickable(false);
        addRecButton.setVisibility(View.GONE);

        // Setup "Send Email" button
        Button sendemailButton = (Button) findViewById(R.id.BasicBtnIdSendEmail).findViewById(R.id.BasicBtnIdMail);
        sendemailButton.setText(R.string.mnf_frag_send_email_btn_txt);
        sendemailButton.setOnClickListener(this);
        //findViewById(R.id.BasicBtnIdSendEmail).setOnClickListener(this);
        //sendemailButton.setVisibility(View.GONE);

        // Animation in case of TAP tag request
        ImageView nfcWavesImg = (ImageView) findViewById(R.id.NfcWavesImgId);
        nfcWavesImg.setBackgroundResource(R.drawable.nfc_waves_anim);
        nfcWavesAnim = (AnimationDrawable) nfcWavesImg.getBackground();

        View tapScreenLayout = findViewById(R.id.WForTapActivityId);
        tapScreenLayout.setVisibility(View.GONE);

        String type = intent.getType();
        if ((Intent.ACTION_SEND.equals(intent.getAction())) && ("text/x-vcard".equals(intent.getType()))) {
            Uri uri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
            ContentResolver cr = getContentResolver();
            InputStream stream = null;
            try {
                stream = cr.openInputStream(uri);
            } catch (FileNotFoundException e) {
                // TODO Autogenerated catch block
                e.printStackTrace();
            }
            StringBuffer fileContent = new StringBuffer("");
            int ch;
            try {
                while ((ch = stream.read()) != -1)
                    fileContent.append((char) ch);
            } catch (IOException e) {
                // TODO Autogenerated catch block
                e.printStackTrace();
            }
            _mvCardExport = new String(fileContent);
            findViewById(R.id.rllistndefmsg).setVisibility(View.GONE);
            findViewById(R.id.MnwActWriteBtnAreaId).setVisibility(View.GONE);
            findViewById(R.id.BasicBtnIdAddRecord).setVisibility(View.GONE);

            stnfcndefhandler _mndefVcardHandler = new stnfcndefhandler();
            _mndefVcardHandler.setNdefVCard(_mvCardExport);
            _curVcardMessage = new NDEFVCardMessage();
            _curVcardMessage.setNDEFMessage(_mndefVcardHandler.gettnf(0), _mndefVcardHandler.gettype(0),
                    _mndefVcardHandler);
            _mexportVCard = true;

        }

        findViewById(R.id.MenuNDEFWriteActivityId).invalidate();
        if (_mexportVCard == true)
            writeTag();
        if (_mfromSmartNdefScreen == true) {
            manageSmartNdefArrayData(intent);
            writeTag();

        }

    }

    private void manageSmartNdefArrayData(Intent intent) {
        byte[] barray = intent.getByteArrayExtra("ndefbyteArray");
        String ndefclassfactory = intent.getStringExtra("ndefclass");
//        if (barray ==null || ndefclassfactory == null) {
//            _mndefMessageHandler = null;
        // issue with data to write
//        } else {
            if (ndefclassfactory.matches("NDEF_SIMPLE_MSG_TYPE_TEXT" )) {
                String messagetext = intent.getStringExtra("Text");
                _mndefMessageHandler = new stnfcndefhandler();
                _mndefMessageHandler.setNdefRTDText(messagetext);
                _curMessage = new NDEFTextMessage();
                _curMessage.setNDEFMessage(_mndefMessageHandler.gettnf(0), _mndefMessageHandler.gettype(0),
                        _mndefMessageHandler);
            } else {
                _mndefMessageHandler = new stnfcndefhandler();
                _mndefMessageHandler.setNdefProprietaryGenTransCtrlMsg(barray);
                _curMessage = new NDEFGenCtrlTranspMessage();
                _curMessage.setNDEFMessage(_mndefMessageHandler.gettnf(0), _mndefMessageHandler.gettype(0),
                        _mndefMessageHandler);

            }

            _mfromSmartNdefScreen = true;
        }



    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(this.getClass().getName(), "OnNewIntent Activity");
        super.onNewIntent(intent);
        if (_writepending == true) {
            writeTag();
        }

    }

    public void onBackPressed() {
        _writepending = false;
        if (_selectNDEFListDisplaystate == false ) {
            CheckBox cb;
            if (_curFragment instanceof NDEFTextFragment) {
                cb = (CheckBox) findViewById(R.id.checkbox_meat_text);
                if (cb.isChecked()) storeNDEFMessageRecord(((NDEFTextFragment) _curFragment).getNDEFSimplifiedMessage());
            }
            if (_curFragment instanceof NDEFURIFragment) {
                cb = (CheckBox) findViewById(R.id.checkbox_meat_url);
                if (cb.isChecked()) storeNDEFMessageRecord(((NDEFURIFragment) _curFragment).getNDEFSimplifiedMessage());
            }
            if (_curFragment instanceof NDEFVCardFragment) {
                cb = (CheckBox) findViewById(R.id.checkbox_meat_contact);
                if (cb.isChecked() || ((NDEFVCardFragment) _curFragment).is_storeRcs()) {
                    storeNDEFMessageRecord(((NDEFVCardFragment) _curFragment).getNDEFSimplifiedMessage());
                }
            }
            if (_curFragment instanceof NDEFAarFragment) {
                cb = (CheckBox) findViewById(R.id.checkbox_meat_aar);
                if (cb.isChecked()) storeNDEFMessageRecord(((NDEFAarFragment) _curFragment).getNDEFSimplifiedMessage());
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        Log.v(this.getClass().getName(), "OnResume Activity");

        super.onResume();

        if (NFCApplication.getApplication().isEnableSalonFeature() && _selectNDEFListDisplaystate == false &&
                _curFragment instanceof NDEFMailFragment) {
            findViewById(R.id.BasicBtnIdSendEmail).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.BasicBtnIdSendEmail).setVisibility(View.GONE);
        }

        //enableRcsParameter
        CheckBox cb = (CheckBox)findViewById(R.id.checkbox_meat_text);
        NDEFSimplifiedMessageType messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEXT;
        enableRcsUIState(cb,messtype);
        cb = (CheckBox)findViewById(R.id.checkbox_meat_url);
        messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_URI;
        enableRcsUIState(cb,messtype);
        cb = (CheckBox)findViewById(R.id.checkbox_meat_contact);
        messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_VCARD;
        enableRcsUIState(cb,messtype);
        cb = (CheckBox)findViewById(R.id.checkbox_meat_aar);
        messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_AAR;
        enableRcsUIState(cb,messtype);


        onTagChanged(NFCApplication.getApplication().getCurrentTag());
    }

    private void enableRcsUIState(CheckBox cb,NDEFSimplifiedMessageType messtype){
        if (NFCApplication.getApplication().hashtableNDEFRecords != null) {
            boolean alreadymessagebuilded =  NFCApplication.getApplication().hashtableNDEFRecords.containsKey(messtype);
            if (alreadymessagebuilded == true) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }

    }

    @Override
    protected void onPause() {
        Log.v(this.getClass().getName(), "OnPause Activity");

        super.onPause();

        return;
    }

    private void updateActivityHeader(NFCTag newTag) {
        // Set the layout content according to the content of the tag
        // - Application header
        NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MnwActNFCAppHeaderFragmentId);
        mHeadFrag.onTagChanged(newTag);
    }

    private void onTagChanged(NFCTag newTag) {
        // Set the layout content according to the content of the tag
        // - Activity header
        updateActivityHeader(newTag);
        // - Set the right item in spinner: fragment should follow...
        // Spinner curSpinner = (Spinner) findViewById(R.id.MnwActRecTypeId);
        // FAR BEGIN
        // Short Fix to avoid Null addressing provided by
        // currentTag.getNDEFSimplifiedHandler()
        // Must be managed by Try catch encapsulation
        if (_keepcurrentSpinerState == Spinner.INVALID_POSITION) {
            int spinnerItemIdx = NDEFSimplifiedMessageHandler
                    .getMsgPositionInList(NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EMPTY);

            NDEFSimplifiedMessageHandler _aNDEFSimplifiedMessageHandler = null;

            if (newTag != null) {
                _aNDEFSimplifiedMessageHandler = newTag.getNDEFSimplifiedHandler(newTag.getCurrentValideTLVBlokID());
            }
            if (_aNDEFSimplifiedMessageHandler != null) {
                NDEFSimplifiedMessage newMsg = _aNDEFSimplifiedMessageHandler.getNDEFSimplifiedMessage();
                if (newMsg != null) {
                    NDEFSimplifiedMessageType newMsgType = newMsg.getType();
                    spinnerItemIdx = NDEFSimplifiedMessageHandler.getMsgPositionInList(newMsgType);
                }
            }
            // curSpinner.setSelection(spinnerItemIdx);
        } else {
            // curSpinner.setSelection(this._keepcurrentSpinerState);
            _keepcurrentSpinerState = Spinner.INVALID_POSITION;
        }
        // FAR END
    }


    private void storeNDEFMessageRecord(NDEFSimplifiedMessage msgToWrite) {
        NFCApplication.getApplication().storeNDEFMessageRecord(msgToWrite);
    }


    // Pure write tag function
    private void writeTag() {
        String resultMsg = getString(R.string.ndef_simple_msg_write_no_fragment);
        String titleMsg = getString(R.string.ndef_write_error);

        if ((_curFragment != null) || (_mexportVCard == true) || (_mfromSmartNdefScreen == true)) {
            NDEFSimplifiedMessage msgToWrite;
            if (_mexportVCard == true) {
                msgToWrite = _curVcardMessage;
                Log.v(this.getClass().getName(), "replaced by...._mexportVCard ");
            } else {

                if (_mfromSmartNdefScreen == true) {
                    //msgToWrite = new NDEFSimplifiedMessageHandler(_mndefMessageHandler).getNDEFSimplifiedMessage();
                    msgToWrite = _curMessage;
                    Log.v(this.getClass().getName(), "replaced by...._mfromSmartNdefScreen ");

                } else {
                    if (false) { // for multiple records Rcds NDEF
                        Log.v(this.getClass().getName(), "replaced by....records Rcds NDEF ");

                    } else {
                        msgToWrite = ((NDEFSimplifiedMessageFragment) _curFragment).getNDEFSimplifiedMessage();
                        Log.v(this.getClass().getName(), "replaced by....from WriteList ");

                    }
                }
            }

            if (msgToWrite != null) {

                //  === Register the message
                if (_curFragment != null) {
                    if (((NDEFSimplifiedMessageFragment) _curFragment).is_storeRcs()) {
                        if (NFCApplication.getApplication().hashtableNDEFRecords!=null) {
                            storeNDEFMessageRecord(msgToWrite);
                        }
                    }
                }
                //  ===
                // Get the current tag
                NFCApplication currentApp = (NFCApplication) getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                // need to know if the tag is still in the field.
                // if Yes then try to write on this TAG
                // if no wait for a tag in a field

                // Ping Tag to know if it still present in NFC field.

                if ((currentTag == null) || (!currentTag.pingTag())) {

                    View tapScreenLayout = findViewById(R.id.WForTapActivityId);
                    tapScreenLayout.setVisibility(View.VISIBLE);
                    nfcWavesAnim.start();
                    // Route the NFC events to the next activity (Tag Info ?)
                    /// nfcAdapter.enableForegroundDispatch(this,
                    // nfcPendingIntent, null /*nfcFiltersArray*/, null
                    // /*nfcTechLists*/);
                    // setMainText(nfcState);
                    _writepending = true;
                    // And write NDEF message to current tag
                    _dialog.setMessage(getString(R.string.nfc_act_tag_wait_for_Tapping));
                    _dialog.show();
                    return;
                }

                if (currentTag != null) {

                    // And write NDEF message to current tag

                    _dialog.setMessage(getString(R.string.nfc_act_tag_writing));
                    _dialog.show();

                    _writeStatus = currentTag.writeNDEFMessage(msgToWrite);
                    if (_writeStatus == stndefwritestatus.WRITE_STATUS_ERR_PASSWORD_REQUIRED) {
                        if (_curFragment != null) {
                            // need to request write password
                            if (currentTag.getModel().contains("DV") ) // ST
                            // product - request password and write Tag
                            {
                                // Get info concerning Zone/Area + pwdx in order to put info in title
                                SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
                                int pwdNumber = 0;
                                if (sysHDL.mST25DVRegister != null) {
                                    stnfcRegisterHandler.ST25DVRegisterTable reg = sysHDL.mST25DVRegister.getZSSentry(currentTag.getCurrentValideTLVBlokID());
                                    pwdNumber = sysHDL.mST25DVRegister.getPasswordNumber(reg);
                                }
                                String pwdInfoString = getString(R.string.pw_rotection_title)+ " [Area: " + (currentTag.getCurrentValideTLVBlokID()+1) + " Pwd: " + ((pwdNumber!=0)?pwdNumber:"None") + "]";
                                // check if pwd = 0 ==> no present pwd ......
                                if (pwdNumber != 0) {
                                    PasswordDialogFragment newFragment = PasswordDialogFragment.newInstance(
                                            pwdInfoString, getString(R.string.pw_writemsg64),
                                            getString(R.string.pw_button_ok), getString(R.string.pw_button_cancel), 16);

                                    newFragment.setTargetFragment(_curFragment, DIALOG_FRAGMENT_WRITE);
                                    // getSupportFragmentManager
                                    newFragment.show(getSupportFragmentManager(), "dialog");
                                    //newFragment.show(_curFragment.getFragmentManager(), "dialog");
                                } else {
                                    Toast.makeText(NFCApplication.getContext(), pwdInfoString,Toast.LENGTH_SHORT).show();
                                    _writeStatus = stndefwritestatus.WRITE_STATUS_ERR_IO;
                                }
                            } else {
                                PasswordDialogFragment newFragment = PasswordDialogFragment.newInstance(
                                        getString(R.string.pw_title), getString(R.string.pw_writemsg),
                                        getString(R.string.pw_button_ok), getString(R.string.pw_button_cancel));

                                newFragment.setTargetFragment(_curFragment, DIALOG_FRAGMENT_WRITE);
                                //newFragment.show(_curFragment.getFragmentManager(), "dialog");
                                newFragment.show(getSupportFragmentManager(), "dialog");

                            }

                        } else {
                            Toast.makeText(NFCApplication.getContext(), "Use case not implemented when protection enabled",Toast.LENGTH_LONG).show();
                            _writeStatus = stndefwritestatus.WRITE_STATUS_ERR_IO;
                        }

                    }
                }

                if (_writepending == true) {
                    View tapScreenLayout = findViewById(R.id.WForTapActivityId);
                    tapScreenLayout.setVisibility(View.GONE);
                    _mexportVCard = false;
                    _mfromSmartNdefScreen = false;
                    _writepending = false;
                }
                // FBE
                try {
                    if (_dialog != null)
                        _dialog.dismiss();
                } catch (Exception e) {

                }

                switch (_writeStatus) {
                case WRITE_STATUS_OK:
                    titleMsg = getString(R.string.ndef_write_success);
                    resultMsg = getString(R.string.ndef_simple_msg_write_ok);
                    break;
                case WRITE_STATUS_ERR_PASSWORD_REQUIRED:
                    titleMsg = getString(R.string.ndef_write_LockReq);
                    resultMsg = getString(R.string.ndef_simple_msg_write_locked);
                    break;
                case WRITE_STATUS_ONGOING:
                    titleMsg = getString(R.string.ndef_write_success);
                    resultMsg = getString(R.string.ndef_simple_msg_write_ongoing);
                    break;
                case WRITE_STATUS_ERR_TAG_LOST:
                    resultMsg = getString(R.string.ndef_simple_msg_write_err_tag_lost);
                    break;
                case WRITE_STATUS_ERR_IO:
                    resultMsg = getString(R.string.ndef_simple_msg_write_err_io);
                    break;
                case WRITE_STATUS_ERR_MALFORMED_STRUCTURE:
                    resultMsg = getString(R.string.ndef_simple_msg_write_err_bad_msg);
                    break;
                case WRITE_STATUS_ERR_READ_ONLY_TAG:
                    resultMsg = getString(R.string.ndef_simple_msg_write_err_ro_tag);
                    break;
                case WRITE_STATUS_ERR_NOT_SUPPORTED:
                    resultMsg = getString(R.string.ndef_simple_msg_write_err_not_supported);
                    break;
                default:
                    resultMsg = getString(R.string.ndef_simple_msg_write_cancel);
                    break;
                }
            }
        }

        // Display the result in an information dialog box
        InformDialogFragment informFragment = InformDialogFragment.newInstance(titleMsg, resultMsg);
        informFragment.show(getSupportFragmentManager(), "write_status_dialog");
    }



    // Implementation of the View.onClickListener interface
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Overwrite action confirmed
        // Update activity header
        updateActivityHeader(NFCApplication.getApplication().getCurrentTag());
        // Write data to the tag
        writeTag();
    }

    @Override
    public void onInformDialogPositiveClick(DialogFragment dialog) {

        if ((_writeStatus == stndefwritestatus.WRITE_STATUS_OK)) {
            if ((_mfromWelcomScreen == true) || _mfromSmartNdefScreen == true) {
                _mfromWelcomScreen = false;
                _mfromSmartNdefScreen = false;
                _writeStatus = stndefwritestatus.WRITE_STATUS_ERR_IO;
                Intent intent = new Intent(this, TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, 0);
                startActivity(intent);
            }
            this.finish();
        }

    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        // "Cancel" button pressed, nothing to do
    }

    public void onDialogNeutralClick(DialogFragment dialog) {
        // Update activity header
        onTagChanged(NFCApplication.getApplication().getCurrentTag());
    }

    public void getContact(View v) {
        // Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
        // Uri.parse("content://contacts"));
        Spinner curSpinner = (Spinner) findViewById(R.id.MnwActRecTypeId);
        // _keepcurrentSpinerState = curSpinner.getSelectedItemPosition();
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),
                PICK_CONTACT_REQUEST);
    }

    public void captureFrame(View v) {
        try {
//            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(captureIntent, CAMERA_CAPTURE);
            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = " You're device doesn't support Capturing";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        }
    }



    public void ImportContact(View v) {
        // Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
        // Uri.parse("content://contacts"));
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),
                PICK_CONTACT_REQUEST);
    }

    public void onDiagPasswordPositiveClick(DialogFragment dialog) {
        Intent aIntent = new Intent();
        aIntent.putExtra("PasswordState", true);
        aIntent.putExtra("password", ((PasswordDialogFragment) dialog)._mComputedPassword);

        this.onActivityResult(DIALOG_FRAGMENT_WRITE, RESULT_OK, aIntent);
    }

    public void onDiagPasswordNegativeClick(DialogFragment dialog) {
        Intent aIntent = new Intent();
        aIntent.putExtra("PasswordState", false);
        this.onActivityResult(DIALOG_FRAGMENT_WRITE, RESULT_OK, aIntent);
    }

    // Pure Send Email tag function
    private void sendEmailTag() {
        if (_curFragment != null && NFCApplication.getApplication().isEnableSalonFeature()) {
            NDEFSimplifiedMessage msgToWrite;
            if (_curFragment instanceof NDEFMailFragment) {
                msgToWrite = ((NDEFMailFragment) _curFragment).getNDEFSimplifiedMessage();

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                String tmpMail = ((NDEFMailFragment) _curFragment).get_mailrecipients();
                tmpMail = tmpMail.replace("\r", "");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {
                        tmpMail});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        ((NDEFMailFragment) _curFragment).get_mailsubject());
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        ((NDEFMailFragment) _curFragment).get_mailtext());
                emailIntent.setType("text/plain");

                //
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd hhmm");
                String format = s.format(new Date());
                String boothvisitors = NFCApplication.getApplication().getmNFCApp_customername() + ";" +
                        NFCApplication.getApplication().getmNFCApp_customermail() + ";" +
                        NFCApplication.getApplication().getmNFCApp_customertextinformation()+ ";" +
                        format;
                boothvisitors = boothvisitors.replace("\r", "");
                writeToFile(boothvisitors);
                //

                //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."),EMAIL_SEND_ACTION);
                try {
                    //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."),EMAIL_SEND_ACTION);


                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(NFCApplication.getContext(), "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(NFCApplication.getContext(), "Use case to send Email not in place ...", Toast.LENGTH_SHORT)
                    .show();

        }

    }


    // getByteArrayLength
    /**
     * @param data
     */
    private void writeToFile(String data) {
                try{

                    File root = new File(Environment.getExternalStoragePublicDirectory(STORAGE_SERVICE), "Salon");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    String name = "boothVisitors.txt";
                    File file =new File(root,name);

                    //if file doesnt exists, then create it
                    if(!file.exists()){
                        file.createNewFile();
                    }
                    //Environment.getExternalStoragePublicDirectory(STORAGE_SERVICE);
                    //true = append file
                    FileWriter fileWritter = new FileWriter(file,true);
                    fileWritter.append(data);
                    fileWritter.append(System.getProperty ("line.separator"));
                    fileWritter.flush();
                    fileWritter.close();

                }catch(IOException e){
                    e.printStackTrace();
                }

    }


    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("boothVisitors.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String resultMsg = getString(R.string.ndef_simple_msg_write_no_fragment);
        String titleMsg = getString(R.string.ndef_write_error);

        switch (requestCode) {
        case EMAIL_SEND_ACTION: {
            //if (resultCode == RESULT_OK) {
                // Display the result in an information dialog box
/*                titleMsg = "Sent mail action";
                resultMsg = "Done";
                InformDialogFragment informFragment = InformDialogFragment.newInstance(titleMsg, resultMsg);
                informFragment.show(getSupportFragmentManager(), "write_status_dialog");*/
            //}
            break;

        }

        case PICK_CONTACT_REQUEST: {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Response: " + data.toString());
                ((NDEFVCardFragment) _curFragment).uriContact = data.getData();
                ((NDEFVCardFragment) _curFragment).setM_ContactsUtilities(data.getData(),
                        _curFragment.getActivity().getContentResolver());
                ((NDEFVCardFragment) _curFragment).retrieveContactNameNew();
                ((NDEFVCardFragment) _curFragment).retrieveContactNumberNew();
                ((NDEFVCardFragment) _curFragment).retrieveContactEmailNew();
                ((NDEFVCardFragment) _curFragment).retrieveContactStructurePostAddrNew();
                ((NDEFVCardFragment) _curFragment).retrieveContactPhoto();
                ((NDEFVCardFragment) _curFragment).retrieveContactWebSiteNew();
            }
            CheckBox cb = (CheckBox) findViewById(R.id.checkbox_meat_contact);
            if (cb.isChecked()) {
                ((NDEFVCardFragment)_curFragment).set_storeRcs(true);
            }

            // Work around implementation for SSG S4
            NDEFVCardFragment tmpFragment;
            FragmentManager fragMng = getSupportFragmentManager();
            tmpFragment = (NDEFVCardFragment) fragMng.findFragmentByTag("NDEFVCardFragment");
            if (tmpFragment != null) {
                ((NDEFVCardFragment) tmpFragment).uriContact = data.getData();
                ((NDEFVCardFragment) tmpFragment).setM_ContactsUtilities(data.getData(),
                        _curFragment.getActivity().getContentResolver());
                ((NDEFVCardFragment) tmpFragment).retrieveContactNameNew();
                ((NDEFVCardFragment) tmpFragment).retrieveContactNumberNew();
                ((NDEFVCardFragment) tmpFragment).retrieveContactEmailNew();
                ((NDEFVCardFragment) tmpFragment).retrieveContactStructurePostAddrNew();
                ((NDEFVCardFragment) tmpFragment).retrieveContactPhoto();
                ((NDEFVCardFragment) tmpFragment).retrieveContactWebSiteNew();
                if (cb.isChecked()) {
                    ((NDEFVCardFragment)tmpFragment).set_storeRcs(true);
                }
            }


            break;

        }
        case DIALOG_FRAGMENT_WRITE: {
            if (resultCode == RESULT_OK) {
                boolean check = data.getBooleanExtra("PasswordState", false);
                if (check) {
                    Log.d("DIALOG DEBUG", "Get Password is :" + data.getStringExtra("password"));
                    // Start New Fragment Activity to handle user Request.
                    if (_curFragment != null) {
                        NDEFSimplifiedMessage msgToWrite = ((NDEFSimplifiedMessageFragment) _curFragment)
                                .getNDEFSimplifiedMessage();
                        _writeStatus = NFCApplication.getApplication().getCurrentTag().writeLockedNDEFMessage(msgToWrite,
                                data.getStringExtra("password"));
                        switch (_writeStatus) {
                            case WRITE_STATUS_OK:
                                titleMsg = getString(R.string.ndef_write_success);
                                resultMsg = getString(R.string.ndef_simple_msg_write_ok);
                                break;
                            case WRITE_STATUS_ERR_PASSWORD_REQUIRED:
                                titleMsg = getString(R.string.ndef_write_LockReq);
                                resultMsg = getString(R.string.ndef_simple_msg_write_locked);
                                break;
                            case WRITE_STATUS_ERR_WRONG_PASSWORD:
                                titleMsg = getString(R.string.ndef_write_LockReq);
                                resultMsg = getString(R.string.ndef_simple_msg_write_wrong_pwd);
                                break;
                            case WRITE_STATUS_ONGOING:
                                titleMsg = getString(R.string.ndef_write_success);
                                resultMsg = getString(R.string.ndef_simple_msg_write_ongoing);
                                break;
                            case WRITE_STATUS_ERR_TAG_LOST:
                                resultMsg = getString(R.string.ndef_simple_msg_write_err_tag_lost);
                                break;
                            case WRITE_STATUS_ERR_IO:
                                resultMsg = getString(R.string.ndef_simple_msg_write_err_io);
                                break;
                            case WRITE_STATUS_ERR_MALFORMED_STRUCTURE:
                                resultMsg = getString(R.string.ndef_simple_msg_write_err_bad_msg);
                                break;
                            case WRITE_STATUS_ERR_READ_ONLY_TAG:
                                resultMsg = getString(R.string.ndef_simple_msg_write_err_ro_tag);
                                break;
                            case WRITE_STATUS_ERR_NOT_SUPPORTED:
                                resultMsg = getString(R.string.ndef_simple_msg_write_err_not_supported);
                                break;
                            default:
                                resultMsg = getString(R.string.ndef_simple_msg_write_cancel);
                                break;
                        }
                    } else {
                        titleMsg = getString(R.string.ndef_write_use_case_not_hamdled);
                        resultMsg = getString(R.string.ndef_simple_msg_write_locked);

                    }

                }
            }

            // Display the result in an information dialog box
            InformDialogFragment informFragment = InformDialogFragment.newInstance(titleMsg, resultMsg);
            informFragment.show(getSupportFragmentManager(), "write_status_dialog");

            break;
        }
        case CAMERA_CAPTURE:
            if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                picUri = data.getData();
                performCrop();
            }else {
                String errorMessage = "Your device doesn't support the image capturing feature";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);

            }
                //Bundle extras = data.getExtras();
                //thePic = extras.getParcelable("data");
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                //if (_curFragment != null ) {
                //    ((NDEFVCardFragment) _curFragment).setPhotoContact(photo);
                //}

            }
            break;
        case PIC_CROP:
            if (data != null) {
                Bundle extras = data.getExtras();
                thePic = extras.getParcelable("data");
                if (_curFragment != null ) {
                    ((NDEFVCardFragment) _curFragment).setPhotoContact(thePic);
                } else {

                    //Retrieve content
                    NDEFVCardFragment tmpFragment;
                    FragmentManager fragMng = this.getSupportFragmentManager();
                    tmpFragment = (NDEFVCardFragment) fragMng.findFragmentByTag("NDEFVCardFragment");
                    if (tmpFragment == null) {
                        String errorMessage = " You're device doesn't support Capturing";
                        Toast  toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    } else {
                        _selectNDEFListDisplaystate = false;
                        //_writeState = false;
                        if (_selectNDEFListDisplaystate == false) {
                            findViewById(R.id.rllistndefmsg).setVisibility(View.GONE);
                            findViewById(R.id.MnwActWriteBtnAreaId).setVisibility(View.VISIBLE);
                            findViewById(R.id.BasicBtnIdAddRecord).setVisibility(View.VISIBLE);
                        }


                        tmpFragment.setPhotoContact(thePic);
                        tmpFragment.setExportPhoto(true);
                        _curFragment = tmpFragment;
                        String errorMessage = "Your device lost VCard fragment during the image capturing feature";
                        Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);

                    }

                }
            }
            break;

        default:
        }
    }

    public void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Your device doesn't support the crop feature";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        }
    }

    // For NDEFs Records and NDEFs saving mechanisum
    public void onCheckboxRcdsStorageClicked(View view) {
        switch (view.getId()) {
        case R.id.checkbox_meat_text:
            break;
            default:
                ;
        }
    }

    private void processMultiNDEFMessageList (NDEFSimplifiedMessageType messtype, CheckBox cb) {
        if (NFCApplication.getApplication().hashtableNDEFRecords != null) {
            boolean initialized =  NFCApplication.getApplication().hashtableNDEFRecords.containsKey(messtype);
            if (initialized) {
                if (cb.isChecked()){
                    // add NDEF asRecordto the list/hashtable for Multi record writing
                    NFCApplication.getApplication().hashtableMultiNDEFRecords.remove(messtype);
                    NFCApplication.getApplication().hashtableMultiNDEFRecords.put(messtype,
                            NFCApplication.getApplication().hashtableNDEFRecords.get(messtype));
                } else {
                    // remove the NDEF asRecordto the list/hashtable for Multi record writing if any
                    NFCApplication.getApplication().hashtableMultiNDEFRecords.remove(messtype);
                }
            } else {
                if (!cb.isChecked()){
                    //cb.setChecked(false);
                    NFCApplication.getApplication().hashtableMultiNDEFRecords.remove(messtype);
                }

            }
        }

    }

    public void onCheckboxRcdsNDEF(View view) {
        NDEFSimplifiedMessageType messtype;
        CheckBox cb;
        switch (view.getId()) {
            case R.id.checkbox_NdefRcds_text:
                messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEXT;
                cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_text);
                processMultiNDEFMessageList(messtype, cb);
                break;
            case R.id.checkbox_NdefRcds_url:
                messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_URI;
                cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_url);
                processMultiNDEFMessageList(messtype, cb);
                break;
            case R.id.checkbox_NdefRcds_contact:
                messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_VCARD;
                cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_contact);
                processMultiNDEFMessageList(messtype, cb);
                break;
            case R.id.checkbox_NdefRcds_aar:
                messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_AAR;
                cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_aar);
                processMultiNDEFMessageList(messtype, cb);
                break;
            default:
                ;
        }
    }

    private boolean manageMultiRecordNdefArrayData( NDEFGenCtrlTranspFragment fragment) {
        boolean ret = false;
        NdefMessage message;
        NdefRecord[] records;
        CheckBox cb;
        NFCApplication.getApplication().clearMultiNDEFRecords();
        for (NDEFSimplifiedMessageType messtype : NFCApplication.getApplication().hashtableNDEFRecords
                .keySet()) {
            switch (messtype) {
                case NDEF_SIMPLE_MSG_TYPE_TEXT:
                    cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_text);
                    if (cb.isChecked()){
                        NFCApplication.getApplication().addMultiNDEFRecords(NFCApplication.getApplication().hashtableNDEFRecords.get(messtype));
                    }
                    break;
                case NDEF_SIMPLE_MSG_TYPE_URI:
                    cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_url);
                    if (cb.isChecked()){
                        NFCApplication.getApplication().addMultiNDEFRecords(NFCApplication.getApplication().hashtableNDEFRecords.get(messtype));
                    }
                    break;
                case NDEF_SIMPLE_MSG_TYPE_VCARD:
                    cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_contact);
                    if (cb.isChecked()){
                        NFCApplication.getApplication().addMultiNDEFRecords(NFCApplication.getApplication().hashtableNDEFRecords.get(messtype));
                    }
                    break;
                case NDEF_SIMPLE_MSG_TYPE_AAR:
                    cb = (CheckBox) findViewById(R.id.checkbox_NdefRcds_aar);
                    if (cb.isChecked()){
                        NFCApplication.getApplication().addMultiNDEFRecords(NFCApplication.getApplication().hashtableNDEFRecords.get(messtype));
                    }
                    break;
                case NDEF_SIMPLE_MSG_TYPE_BTHANDOVER:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_EXT_GENCTRL:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_EXT_TRANSP_GENCTRL:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_MAIL:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_SMS:
                    break;
                case NDEF_SIMPLE_MSG_TYPE_SP:
                    break;
                case NDEF_SIMPLE_MULTIPLE_RECORD:
                    break;
                default:
                    ;
            }
        }


        int objectsize = NFCApplication.getApplication().hashtableMultiNDEFRecords.size();
        records = new NdefRecord[objectsize];
        int iterator = 0;
        if (objectsize > 0) {
            for (NDEFSimplifiedMessageType messtype : NFCApplication.getApplication().hashtableMultiNDEFRecords
                    .keySet()) {
                NDEFSimplifiedMessage mess = NFCApplication.getApplication().hashtableMultiNDEFRecords.get(messtype);
                String infomessage = messtype + "\t" + mess.toString();
                Log.v(this.getClass().getName(), "Multi Rcds :" + messtype);
                records[iterator] = mess.getNDEFMessage().getRecords()[0];
                iterator++;
            }
            message = new NdefMessage(records);


            _mndefMessageHandler = new stnfcndefhandler();
            _mndefMessageHandler.setNdefProprietaryGenTransCtrlMsg(message.toByteArray());
            NDEFSimplifiedMessage spMessage;
//            spMessage = new NDEFMultipleRecordMessage(_mndefMessageHandler);

            spMessage = new NDEFGenCtrlTranspMessage();
            spMessage.setNDEFMessage(_mndefMessageHandler.gettnf(0), _mndefMessageHandler.gettype(0),
                    _mndefMessageHandler);
            fragment.setNDEFMsg((NDEFGenCtrlTranspMessage) spMessage);

            ret = true;

        } else {
            //_mfromSmartNdefScreen = false;

        }

        return ret;
    }


    private NDEFSimplifiedMessageFragment createOrRetrieveTextFragment(boolean enableRcsParameter,
            FragmentManager fragMng) {
        NDEFSimplifiedMessageFragment tmpFragment = null;
        NDEFSimplifiedMessageType messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_TEXT;
        if (enableRcsParameter) {
            if (NFCApplication.getApplication().hashtableNDEFRecords != null) {
                boolean alreadymessagebuilded = NFCApplication.getApplication().hashtableNDEFRecords
                        .containsKey(messtype);
                if (alreadymessagebuilded) {
                    tmpFragment = NDEFTextFragment.newInstance(
                            (NDEFTextMessage) NFCApplication.getApplication().hashtableNDEFRecords.get(messtype),
                            false);
                }
            }
        } else {
            //tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFTextFragment");
        }
        return tmpFragment;
    }

    private NDEFSimplifiedMessageFragment createOrRetrieveURIFragment(boolean enableRcsParameter,
            FragmentManager fragMng) {
        NDEFSimplifiedMessageFragment tmpFragment = null;
        NDEFSimplifiedMessageType messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_URI;
        if (enableRcsParameter) {
            if (NFCApplication.getApplication().hashtableNDEFRecords != null) {
                boolean alreadymessagebuilded = NFCApplication.getApplication().hashtableNDEFRecords
                        .containsKey(messtype);
                if (alreadymessagebuilded) {
                    tmpFragment = NDEFURIFragment.newInstance(
                            (NDEFURIMessage) NFCApplication.getApplication().hashtableNDEFRecords.get(messtype), false);
                }
            }
        } else {
            //tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFURIFragment");
        }
        return tmpFragment;
    }
    private NDEFSimplifiedMessageFragment createOrRetrieveContactFragment(boolean enableRcsParameter,
            FragmentManager fragMng) {
        NDEFSimplifiedMessageFragment tmpFragment = null;
        NDEFSimplifiedMessageType messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_VCARD;
        if (enableRcsParameter) {
            if (NFCApplication.getApplication().hashtableNDEFRecords != null) {
                boolean alreadymessagebuilded = NFCApplication.getApplication().hashtableNDEFRecords
                        .containsKey(messtype);
                if (alreadymessagebuilded) {
                    tmpFragment = NDEFVCardFragment.newInstance(
                            (NDEFVCardMessage) NFCApplication.getApplication().hashtableNDEFRecords.get(messtype), false);
                }
            }
        } else {
            //tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFURIFragment");
        }
        return tmpFragment;
    }
    private NDEFSimplifiedMessageFragment createOrRetrieveAarFragment(boolean enableRcsParameter,
                                                                          FragmentManager fragMng) {
        NDEFSimplifiedMessageFragment tmpFragment = null;
        NDEFSimplifiedMessageType messtype = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_AAR;
        if (enableRcsParameter) {
            if (NFCApplication.getApplication().hashtableNDEFRecords != null) {
                boolean alreadymessagebuilded = NFCApplication.getApplication().hashtableNDEFRecords
                        .containsKey(messtype);
                if (alreadymessagebuilded) {
                    tmpFragment = NDEFAarFragment.newInstance(
                            (NDEFAarMessage) NFCApplication.getApplication().hashtableNDEFRecords.get(messtype), false);
                }
            }
        } else {
            //tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFURIFragment");
        }
        return tmpFragment;
    }
    public void onClick(View view) {
        NDEFSimplifiedMessageFragment tmpFragment = null;
        String tmpFragmentTag = null;
        boolean isNewFragment = false;
        boolean enableMail = false;
        boolean enableRcsParameter = false;
        CheckBox cb;
        FragmentManager fragMng = getSupportFragmentManager();

        NDEFSimplifiedMessageHandler _aNDEFSimplifiedMessageHandler = null;

        NDEFSimplifiedMessage curMsg = null;

        this._mfromSmartNdefScreen = false;
        this._mexportVCard = false;


        if (_aNDEFSimplifiedMessageHandler != null) {
            curMsg = _aNDEFSimplifiedMessageHandler.getNDEFSimplifiedMessage();
        }

        switch (view.getId()) {
            case R.id.RlBActionEditText:
                tmpFragmentTag = "NDEFTextFragment";

                // - Text simplified NDEF message
                // Check if there's an existing Text fragment in current
                // activity/fragment
                //enableRcsParameter
                cb = (CheckBox) findViewById(R.id.checkbox_meat_text);
                if (cb.isChecked()) {
                    enableRcsParameter = true;
                    tmpFragment = createOrRetrieveTextFragment(enableRcsParameter, fragMng);
                    if (tmpFragment != null) isNewFragment = true;
                }
                if (tmpFragment == null)
                    tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFTextFragment");

                if (tmpFragment == null) {
                    tmpFragment = NDEFTextFragment.newInstance(false);
                    isNewFragment = true;

                }
                if (enableRcsParameter && tmpFragment != null)
                    tmpFragment.set_storeRcs(enableRcsParameter);
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditURL:
                // - URI simplified NDEF message
                // Check if there's an existing VCard fragment in current
                // activity/fragment
                tmpFragmentTag = "NDEFURIFragment";
                //tmpFragment = (NDEFURIFragment) fragMng.findFragmentByTag("NDEFSimpleMsgURI");
                cb = (CheckBox) findViewById(R.id.checkbox_meat_url);
                if (cb.isChecked()) {
                    enableRcsParameter = true;
                    tmpFragment = createOrRetrieveURIFragment(enableRcsParameter, fragMng);
                    if (tmpFragment != null) isNewFragment = true;
                }
                if (tmpFragment == null)
                    tmpFragment = (NDEFURIFragment) fragMng.findFragmentByTag("NDEFURIFragment");

                if (tmpFragment == null) {
                    tmpFragment = NDEFURIFragment.newInstance(false);
                    isNewFragment = true;
                }
                if (enableRcsParameter && tmpFragment != null)
                    tmpFragment.set_storeRcs(enableRcsParameter);
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditContact:
                // - VCard NDEF message
                // Check if there's an existing URI fragment in current
                // activity/fragment
                tmpFragmentTag = "NDEFVCardFragment";
                //tmpFragment = (NDEFVCardFragment) fragMng.findFragmentByTag("NDEFVCardFragment");

                cb = (CheckBox) findViewById(R.id.checkbox_meat_contact);
                if (cb.isChecked()) {
                    enableRcsParameter = true;
                    tmpFragment = createOrRetrieveContactFragment(enableRcsParameter, fragMng);
                    if (tmpFragment != null) isNewFragment = true;
                }
                if (tmpFragment == null)
                    tmpFragment = (NDEFVCardFragment) fragMng.findFragmentByTag("NDEFVCardFragment");


                if (tmpFragment == null) {
                    tmpFragment = NDEFVCardFragment.newInstance(false);
                    isNewFragment = true;
                }
                if (enableRcsParameter && tmpFragment != null)
                    tmpFragment.set_storeRcs(enableRcsParameter);
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditaar:
                // Launch
                // - AAR NDEF message
                // Check if there's an existing VCard fragment in current
                // activity/fragment
                //tmpFragment = (NDEFAarFragment) fragMng.findFragmentByTag("NDEFAarMsgMail");
                //tmpFragmentTag = "NDEFAarMsgMail";
                tmpFragmentTag = "NDEFAarFragment";

                cb = (CheckBox) findViewById(R.id.checkbox_meat_aar);
                if (cb.isChecked()) {
                    enableRcsParameter = true;
                    tmpFragment = createOrRetrieveAarFragment(enableRcsParameter, fragMng);
                    if (tmpFragment != null) isNewFragment = true;
                }
                if (tmpFragment == null)
                    tmpFragment = (NDEFAarFragment) fragMng.findFragmentByTag("NDEFAarFragment");

                if (tmpFragment == null) {
                    tmpFragment = NDEFAarFragment.newInstance(false);
                    isNewFragment = true;
                }
                if (enableRcsParameter && tmpFragment != null)
                    tmpFragment.set_storeRcs(enableRcsParameter);
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditm24srDiscoCtrl:
                // - ST proprietary NDEF message to control M24SRDiscovery board

                // Check if there's an existing URI fragment in current
                // activity/fragment
                tmpFragment = (NDEFDiscoveryKitCtrlFragment) fragMng.findFragmentByTag("NDEFDiscoveryKitCtrlFragment");
                tmpFragmentTag = "NDEFDiscoveryKitCtrlFragment";

                if (tmpFragment == null) {
                    tmpFragment = NDEFDiscoveryKitCtrlFragment.newInstance(false);
                    isNewFragment = true;
                }
                _selectNDEFListDisplaystate = false;
                break;

            case R.id.RlBActionEditwifi:
                // Launch
                // - ST proprietary NDEF message to control M24SRDiscovery board

                // Check first if Wifi Adapter is enabled otherwise notify customer
                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (!wifi.isWifiEnabled()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enable Wifi fisrt", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                // Check if there's an existing URI fragment in current
                // activity/fragment
                tmpFragment = (NDEFWifiHandoverFragment) fragMng.findFragmentByTag("NDEFSimpleMsgWifiHandover");
                tmpFragmentTag = "NDEFSimpleMsgWifiHandover";

                if (tmpFragment == null) {
                    tmpFragment = NDEFWifiHandoverFragment.newInstance(false);
                    isNewFragment = true;
                }
                _selectNDEFListDisplaystate = false;
                break;

            case R.id.RlBActionEditbt:
                // - URI simplified NDEF message
                // Check if there's an existing VCard fragment in current
                // activity/fragment
                tmpFragment = (NDEFBTHandoverFragment) fragMng.findFragmentByTag("NDEFSimpleMsgBTHandover");
                tmpFragmentTag = "NDEFSimpleMsgBTHandover";

                if (tmpFragment == null) {
                    tmpFragment = NDEFBTHandoverFragment.newInstance(false);
                    isNewFragment = true;
                }
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditbtle:
                // - URI simplified NDEF message
                // Check if there's an existing VCard fragment in current
                // activity/fragment
                tmpFragment = (NDEFBTLeFragment) fragMng.findFragmentByTag("NDEFSimpleMsgBTLe");
                tmpFragmentTag = "NDEFSimpleMsgBTLe";

                if (tmpFragment == null) {
                    tmpFragment = NDEFBTLeFragment.newInstance(false);
                    isNewFragment = true;
                }
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditsms:
                // Launch
                // - SMS simplified NDEF message
                // Check if there's an existing fragment in current
                // activity/fragment
                tmpFragment = (NDEFSmsFragment) fragMng.findFragmentByTag("NDEFSimpleMsgSms");
                tmpFragmentTag = "NDEFSimpleMsgSms";

                if (tmpFragment == null) {
                    tmpFragment = NDEFSmsFragment.newInstance(false);
                    isNewFragment = true;
                }
                _selectNDEFListDisplaystate = false;
                break;
            case R.id.RlBActionEditmail:
                // Launch
                // - Mail simplified NDEF message
                // Check if there's an existing VCard fragment in current
                // activity/fragment
                tmpFragment = (NDEFMailFragment) fragMng.findFragmentByTag("NDEFSimpleMsgMail");
                tmpFragmentTag = "NDEFSimpleMsgMail";

                if (tmpFragment == null) {
                    tmpFragment = NDEFMailFragment.newInstance(false);
                    isNewFragment = true;
                }
                _selectNDEFListDisplaystate = false;
                enableMail = true;
                break;

            case R.id.BasicBtnIdAddRecord:
                _selectNDEFListDisplaystate = true;
                _writeState = false;
                break;
            case R.id.BasicBtnId:
                _selectNDEFListDisplaystate = false;
                _writeState = true;
                break;
            case R.id.RlBasicBtnMultiRcds:
                // Added to comply with others
                tmpFragment = (NDEFGenCtrlTranspFragment) fragMng.findFragmentByTag("NDEFGenCtrlTranspFragment");
                tmpFragmentTag = "NDEFGenCtrlTranspFragment";

                if (tmpFragment == null) {
                    tmpFragment = NDEFGenCtrlTranspFragment.newInstance(false);
                    isNewFragment = true;
                }

                _selectNDEFListDisplaystate = false;
                if (manageMultiRecordNdefArrayData((NDEFGenCtrlTranspFragment) tmpFragment) == true) {
                 }
                else {
                    _mfromSmartNdefScreen = false;
                    _writeState = false;
                }
                break;

            case R.id.BasicBtnIdMail:
                _selectNDEFListDisplaystate = false;
                _writeState = false;
                _sendEmailState = true;
                break;
            default:
                _selectNDEFListDisplaystate = true;
        }


        if (!_writeState) {
            if (_selectNDEFListDisplaystate == false) {
                findViewById(R.id.rllistndefmsg).setVisibility(View.GONE);
                findViewById(R.id.MnwActWriteBtnAreaId).setVisibility(View.VISIBLE);
                findViewById(R.id.BasicBtnIdAddRecord).setVisibility(View.VISIBLE);

                if (NFCApplication.getApplication().isEnableSalonFeature() && enableMail) {
                    findViewById(R.id.BasicBtnIdSendEmail).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.BasicBtnIdSendEmail).setVisibility(View.GONE);
                }
            } else {
                findViewById(R.id.rllistndefmsg).setVisibility(View.VISIBLE);
                findViewById(R.id.MnwActWriteBtnAreaId).setVisibility(View.GONE);
                findViewById(R.id.BasicBtnIdAddRecord).setVisibility(View.GONE);
                findViewById(R.id.BasicBtnIdSendEmail).setVisibility(View.GONE);
            }


            // Perform the transactions, if any detected
            if (tmpFragment != null) {
                // If fragment is the current one, no need to change anything
                if (tmpFragment != _curFragment) {
                    FragmentTransaction transaction = fragMng.beginTransaction();
                    // Hide current fragment, if there's one
                    if (_curFragment != null) {
                        transaction.hide(_curFragment);
                        _curFragment = null;
                    }
                    // If this is a new fragment, add it to the fragment
                    // manager; otherwise, just show it
                    if (isNewFragment) {
                        transaction.add(R.id.MnwActRecContentId, tmpFragment, tmpFragmentTag);
                        isNewFragment = false;
                    } else {
                        transaction.show(tmpFragment);
                    }
                    transaction.commit();
                    // Set current fragment
                    _curFragment = tmpFragment;
                    if (enableRcsParameter) {
                        tmpFragment.set_storeRcs(enableRcsParameter);
                    }
                }
            }

            if (!_sendEmailState) {
                // nothing to do .....
            } else {
                _sendEmailState = false;
                sendEmailTag();

            }


        } else {
            _writeState = false;
            writeTag();
        }

        return;
    }
    public void onClickSendEmail(View v) {
        sendEmailTag();
    }


// Implementation of the AdapterView.OnItemSelectedListener interface, for
// Spinner change behavior
public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

}

public void onNothingSelected(AdapterView<?> parent) {
    // Another interface callback
}

}
