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
package com.st.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.st.Fragments.MenuToolsFragment.actionType;
import com.st.NDEF.NDEFBTLeMessage;
import com.st.NDEF.NDEFGenCtrlTranspMessage;
import com.st.NDEF.NDEFMultipleRecordMessage;
import com.st.NDEFUI.NDEFBTLeFragment;
import com.st.NDEFUI.NDEFGenCtrlTranspFragment;
import com.st.NDEFUI.NDEFMultipleRecordFragment;
import com.st.demo.R;
import com.st.demo.MMYDemoWriteNDEFActivity;
import com.st.demo.TagMenuDetailsActivity;
import com.st.NDEF.NDEFAarMessage;
import com.st.NDEF.NDEFBTHandoverMessage;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NDEF.NDEFMailMessage;
import com.st.NDEF.NDEFSPMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFSimplifiedMessageHandler;
import com.st.NDEF.NDEFSimplifiedMessageType;
import com.st.NDEF.NDEFSmsMessage;
import com.st.NDEF.NDEFTextMessage;
import com.st.NDEF.NDEFURIMessage;
import com.st.NDEF.NDEFVCardMessage;
import com.st.NDEF.NDEFWifiHandoverMessage;
import com.st.NDEF.ndefError;
import com.st.NDEF.stnfcndefhandler;
import com.st.NDEFUI.NDEFAarFragment;
import com.st.NDEFUI.NDEFBTHandoverFragment;
import com.st.NDEFUI.NDEFDiscoveryKitCtrlFragment;
import com.st.NDEFUI.NDEFMailFragment;
import com.st.NDEFUI.NDEFSPFragment;
import com.st.NDEFUI.NDEFSimplifiedMessageFragment;
import com.st.NDEFUI.NDEFSmsFragment;
import com.st.NDEFUI.NDEFTextFragment;
import com.st.NDEFUI.NDEFURIFragment;
import com.st.NDEFUI.NDEFVCardFragment;
import com.st.NDEFUI.NDEFWifiHandoverFragment;
import com.st.NFC.CCFileGenHandler;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.NfcMenus;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler;
import com.st.util.PasswordDialogFragment;

import java.lang.reflect.Method;
import java.util.Set;


public class MenuSmartNdefViewFragment extends NFCPagerFragment {
    // Store current tag
    //private NFCTag _curNFCTag = null;
    // Store view corresponding to current fragment
    private View _mcurFragmentView = null;
    private NDEFSimplifiedMessageFragment _mSmartfragment = null;

    static final String TAG_HEADER_FRAGMENT_PARAM = "tagHeaderFrag";

    private actionType currentAction;
    public static final int DIALOG_NDEF_PROTECT_UNLOCK = 3;
    public static final int RESULT_OK = 101;

    private int currentTLVBlockID = 0;

    NdefViewFragmentListener _mListener;

    private FragmentManager _mChildfm;

    // Interface Declaration
    public interface NdefViewFragmentListener {
        public void OnlockNdefMessage();
    }


    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuNDEFFilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuSmartNdefViewFragment newInstance(NFCTag mNFCTag) {
        MenuSmartNdefViewFragment fragment = new MenuSmartNdefViewFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }
    public static MenuSmartNdefViewFragment newInstance(NFCTag mNFCTag,int page, String title) {
        MenuSmartNdefViewFragment fragment = new MenuSmartNdefViewFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuSmartNdefViewFragment() {
        // Required empty public constructor
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            _mListener = (NdefViewFragmentListener) activity;

        }catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement MenuSmartNdefViewFragmentListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        _mChildfm = getChildFragmentManager();

        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);
        //this.setRetainInstance(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
     Log.v(this.getClass().getName(), "OnCreateView Fragment");

     // Inflate the layout for this fragment
     _mcurFragmentView = inflater.inflate(R.layout.fragment_smart_ndef_view, container,false);

     // Create the nested fragments (tag header one)
     //FragmentManager fragMng = getChildFragmentManager();
     //FragmentManager fragMng = getFragmentManager();
     // First check if the fragment already exists (in case current fragment has been temporarily destroyed)
        if (_mChildfm == null)  _mChildfm = getChildFragmentManager();
     TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) _mChildfm.findFragmentById(R.id.RLSmartNDEFViewTagHeaderFragmentId);
     if (mTagHeadFrag == null) {
         mTagHeadFrag = new TagHeaderFragment();
         try {
             FragmentTransaction transaction = _mChildfm.beginTransaction();
             transaction.add(R.id.RLSmartNDEFViewTagHeaderFragmentId, mTagHeadFrag);
             transaction.commit();
             _mChildfm.executePendingTransactions();
         } catch(Exception ex){
             Log.e(this.getClass().getName(), ex.toString());
         }
     }

     Button _unlockButton = (Button) _mcurFragmentView.findViewById(R.id.unlockSmartReadBtnId);
    //
    _unlockButton.setOnClickListener(new View.OnClickListener() {
    public void onClick(View v)
    {
        NFCTag currentTag = NFCApplication.getApplication().getCurrentTag();

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
            String pwdInfoString = getString(R.string.pw_rotection_title)+ " [Area: " + (currentTag.getCurrentValideTLVBlokID()+1) + " Pwd: " + pwdNumber + "]";
            PasswordDialogFragment newFragment = (PasswordDialogFragment) PasswordDialogFragment.newInstance(
                    pwdInfoString,
                    getString(R.string.pw_readmsg64),
                    getString(R.string.pw_button_ok),
                    getString(R.string.pw_button_cancel),16);

            newFragment.setTargetFragment(MenuSmartNdefViewFragment.this, DIALOG_NDEF_PROTECT_UNLOCK);
            newFragment.show(getFragmentManager(), "dialog");
            MenuSmartNdefViewFragment.this.currentAction = MenuToolsFragment.actionType.UNLOCK_READ_NDEF_FILE;

         } else {

            PasswordDialogFragment newFragment = (PasswordDialogFragment) PasswordDialogFragment.newInstance(
                    getString(R.string.pw_title),
                    getString(R.string.pw_readmsg),
                    getString(R.string.pw_button_ok),
                    getString(R.string.pw_button_cancel));

            newFragment.setTargetFragment(MenuSmartNdefViewFragment.this, DIALOG_NDEF_PROTECT_UNLOCK);
            newFragment.show(getFragmentManager(), "dialog");
            MenuSmartNdefViewFragment.this.currentAction = MenuToolsFragment.actionType.UNLOCK_READ_NDEF_FILE;
        }

    }
  });

    _unlockButton.setVisibility(View.INVISIBLE);

     NFCTag newTag = NFCApplication.getApplication().getCurrentTag();
     updateSmartFragment(newTag);

    // "Write" button
    Button writeBtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartWriteBtnAreaId).findViewById(R.id.BasicBtnId);
    writeBtn.setText(R.string.mnf_frag_write_menu_btn_txt);
    writeBtn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(currentTLVBlockID);
            NFCApplication.getApplication().setFileID(currentTLVBlockID);
            Intent intent = new Intent(getActivity(), MMYDemoWriteNDEFActivity.class);
            startActivity(intent);
        }
    });
    // "tools" button
    Button toolsBtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartToolsBtnAreaId).findViewById(R.id.BasicBtnId);
    toolsBtn.setText("Tools");
        if (isToolsMenuAvailable(NFCApplication.getApplication().getCurrentTag().getMenusList()) == 0) toolsBtn.setVisibility(View.GONE);

    toolsBtn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            int tabIndex =  isToolsMenuAvailable(NFCApplication.getApplication().getCurrentTag().getMenusList());
            if (tabIndex != 0) {
                Intent intent = new Intent(getActivity(), TagMenuDetailsActivity.class);
                intent.putExtra(TagMenuDetailsActivity.ARG_TAB_NUMBER, tabIndex);
                startActivity(intent);
            }

        }
    });

    // "tools" dump
    Button dumpBtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartDumpBtnAreaId).findViewById(R.id.BasicBtnId);
    dumpBtn.setText("Store");
    dumpBtn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MMYDemoWriteNDEFActivity.class);
            intent.putExtra("fromsmartndefScreen", true);
            intent.putExtra("ndefclass", _mSmartfragment.getNDEFSimplifiedMessage().getType().toString());

/*            stnfcndefhandler _mndefCandyHandler = new stnfcndefhandler();
            _mndefCandyHandler.setNdefProprietaryGenTransCtrlMsg(_mSmartfragment.getNDEFSimplifiedMessage().getNDEFMessage().toByteArray());
            NDEFGenCtrlTranspMessage ndefMessage = new NDEFGenCtrlTranspMessage();
            ndefMessage.setNDEFMessage(_mndefCandyHandler.gettnf(0), _mndefCandyHandler.gettype(0), _mndefCandyHandler);
            intent.putExtra("ndefbyteArray", ndefMessage.serializeNDEFMessage());
            intent.putExtra("ndefclass", ndefMessage.getType().toString());
*/

            NDEFSimplifiedMessage mess = _mSmartfragment.getNDEFSimplifiedMessage();
            mess.getNDEFMessage().toByteArray();
            switch (mess.getType()) {
                case NDEF_SIMPLE_MSG_TYPE_EMPTY:
                    // Just hide current fragment, if not already hidden
                    break;
                case NDEF_SIMPLE_MSG_TYPE_TEXT:
                    NDEFTextMessage txtmess = (NDEFTextMessage) _mSmartfragment.getNDEFSimplifiedMessage();
                    intent.putExtra("Text", txtmess.getText());
                    break;
                case NDEF_SIMPLE_MSG_TYPE_VCARD:
                    if (((NDEFVCardFragment) _mSmartfragment).isExportPhoto()) ((NDEFVCardFragment) _mSmartfragment).setExportPhoto(true);
                    //break;
                case NDEF_SIMPLE_MSG_TYPE_URI:
                case NDEF_SIMPLE_MSG_TYPE_MAIL:
                case NDEF_SIMPLE_MSG_TYPE_SMS:
                case NDEF_SIMPLE_MSG_TYPE_BTHANDOVER:
                case NDEF_SIMPLE_MSG_TYPE_BTLE:
                case NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER:
                    // case NDEF_SIMPLE_MSG_TYPE_SMART_POSTER:
                    // case NDEF_SIMPLE_MSG_TYPE_TEL_NB:
                    // case NDEF_SIMPLE_MSG_TYPE_SMS,
                    // case NDEF_SIMPLE_MSG_TYPE_MAIL,
                    // case NDEF_SIMPLE_MSG_TYPE_VCARD,
                    // case NDEF_SIMPLE_MSG_TYPE_BT_PAIR,
                    // case NDEF_SIMPLE_MSG_TYPE_WIFI_PAIR,
                    // case NDEF_SIMPLE_MSG_TYPE_PROPRIETARY,
                case NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL:
                case NDEF_SIMPLE_MSG_TYPE_AAR:
                default:
                    byte[] ndeftowrite = _mSmartfragment.getNDEFSimplifiedMessage().getNDEFMessage().toByteArray();
                    intent.putExtra("ndefbyteArray", ndeftowrite);
                    break;
                // Nothing to do... for the moment
            }

//            byte[] ndeftowrite = _mSmartfragment.getNDEFSimplifiedMessage().getNDEFMessage().toByteArray();
//            intent.putExtra("ndefbyteArray", ndeftowrite);

            startActivity(intent);
        }
    });
    // Launch Button
    // "Launch Action" button
    Button launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
    launchbtn.setText(R.string.mnf_frag_launch_action_btn_txt);
    launchbtn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            //connectWifi();
            connect();
        }
    });

        return _mcurFragmentView;
    }

    private int isToolsMenuAvailable(NfcMenus[]  mMenu) {
        int ret = 0;
        int index = 0;
        for (int i =0; i<mMenu.length;i++) {
            if (mMenu[i] == NfcMenus.NFC_MENU_LR_TOOLS ||
                    mMenu[i] == NfcMenus.NFC_MENU_TOOLS    ||
                    mMenu[i] == NfcMenus.NFC_MENU_ST25TV_TOOLS) {
                index = i;
                break;
            }

        }
        if (index != 0) ret = index;
        return ret;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
     Log.v(this.getClass().getName(), "OnActivityCreated Fragment");
     super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
         Log.v(this.getClass().getName(), "onStart Fragment");
         super.onStart();

        RadioGroup radioGroup = (RadioGroup) _mcurFragmentView.findViewById(R.id.SNRadiogroupIDFileLayout);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                      case R.id.SNFileID1:
                          currentTLVBlockID = 0;
                           break;
                      case R.id.SNFileID2:
                          currentTLVBlockID = 1;
                          break;
                      case R.id.SNFileID3:
                          currentTLVBlockID = 2;
                          break;
                      case R.id.SNFileID4:
                          currentTLVBlockID = 3;
                          break;
                     case R.id.SNFileID5:
                         currentTLVBlockID = 4;
                         break;
                     case R.id.SNFileID6:
                         currentTLVBlockID = 5;
                         break;
                     case R.id.SNFileID7:
                         currentTLVBlockID = 6;
                         break;
                    case R.id.SNFileID8:
                        currentTLVBlockID = 7;
                        break;
                    default:
                        currentTLVBlockID = 0;
                        break;
                }
                // updateTLVInf(currentTLVBlockID);

                NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(currentTLVBlockID);
                NFCApplication.getApplication().setFileID(currentTLVBlockID);
                updateSmartFragment( NFCApplication.getApplication().getCurrentTag());
                onTagChanged(NFCApplication.getApplication().getCurrentTag());

            }
        });


         // Fill in the layout with the currentTag
        // onTagChanged (NFCApplication.getApplication().getCurrentTag());
    }

    @Override
    public void onDestroyView() {
         Log.v(this.getClass().getName(), "onDestroyView Fragment");
         super.onDestroyView();
         // Create the nested fragments (tag header one)

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
         Log.v(this.getClass().getName(), "onDestroy Fragment");
        super.onDestroy();
        _mChildfm = null;

    }


    @Override
    public void onResume() {
        Log.v(this.getClass().getName(), "onResume Fragment");
        super.onResume();

        // Fill in the layout with the currentTag
        onTagChanged (NFCApplication.getApplication().getCurrentTag());
    }



    public void updateSmartFragment(NFCTag newTag)
    {
        Button launchbtn;
        final int SUCCESS_RES = 1;
        int retRes = SUCCESS_RES;
        NDEFSimplifiedMessageFragment tmpFragment = null;
        FragmentManager fragMng = null;
        String tmpFragmentTag = null;
        NDEFSimplifiedMessage newMsg = null;

         Log.v(this.getClass().getName(), "updateSmartFragment entry ..");
         //if (newTag.getM_ModelChanged() == 1) return;
        // return new instance according to current tag - if not null
        // - Check if NDEF data are present for current tag
         if (newTag.getCurrentValideTLVBlokID() == -1) {
             // FBE newTag.decodeTag();
             retRes = newTag.decodeTag();
         }
         if ( retRes!= SUCCESS_RES) {
                 Log.e(this.getClass().getName(), "updateSmartFragment decodeTag failed ..");

         } else
         {
                stnfcndefhandler tagNDEFHandler = newTag.getNDEFHandler(newTag.getCurrentValideTLVBlokID());
                NDEFSimplifiedMessageHandler ndefSimpleMsgHandler = newTag.getNDEFSimplifiedHandler(newTag.getCurrentValideTLVBlokID());
                if (ndefSimpleMsgHandler != null) {
                    newMsg = newTag.getNDEFSimplifiedHandler(newTag.getCurrentValideTLVBlokID()).getNDEFSimplifiedMessage();
                }
                NDEFSimplifiedMessageType newMsgType = NDEFSimplifiedMessageType.NDEF_SIMPLE_MSG_TYPE_EMPTY;
                if (newMsg != null) {
                    newMsgType = newMsg.getType();
                    NDEFSimplifiedMessageHandler.getStrFromMsgType(newMsgType);
                }

                tmpFragmentTag = null;
//                    FragmentManager fragMng = getSupportFragmentManager();
                fragMng = _mChildfm; //getChildFragmentManager();
                //fragMng = getActivity().getSupportFragmentManager();
                //fragMng = getFragmentManager();

                // Check the type of the selected item
                switch (newMsgType) {
                    case  NDEF_SIMPLE_MSG_TYPE_EMPTY:
                        // Just hide current fragment, if not already hidden
                         Log.v(this.getClass().getName(), "updateSmartFragment NDEF_SIMPLE_MSG_TYPE_EMPTY entry ..");
                        if (_mSmartfragment != null) {
                            FragmentTransaction transaction = fragMng.beginTransaction();
                            transaction.hide(_mSmartfragment);
                            // transaction.commit();
                            transaction.commitAllowingStateLoss();
                            //_mSmartfragment = null;
                        }
                        // Hide and disable launchBtn

                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_TEXT:
                        // - Text simplified NDEF message
                        // Check if there's an existing Text fragment in current activity/fragment
                        tmpFragmentTag = "NDEFTextFragment";
                        tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag(tmpFragmentTag);
                        //tmpFragment = (NDEFTextFragment) fragMng.findFragmentByTag("NDEFSimpleMsgText");
                        //tmpFragmentTag = "NDEFSimpleMsgText";

                        if (tmpFragment == null) {
                            if (newMsg != null)  {
                                tmpFragment = NDEFTextFragment.newInstance((NDEFTextMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFTextFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_URI:
                        // - URI simplified NDEF message
                        // Check if there's an existing VCard fragment in current activity/fragment
                        tmpFragment = (NDEFURIFragment) fragMng.findFragmentByTag("NDEFSimpleMsgURI");
                        tmpFragmentTag = "NDEFSimpleMsgURI";
                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFURIFragment.newInstance((NDEFURIMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFURIFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_MAIL:
                        // - URI simplified NDEF message
                        // Check if there's an existing VCard fragment in current activity/fragment
                        tmpFragment = (NDEFMailFragment) fragMng.findFragmentByTag("NDEFSimpleMsgMail");
                        tmpFragmentTag = "NDEFSimpleMsgMail";
                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFMailFragment.newInstance((NDEFMailMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFMailFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_SMS:
                        // - URI simplified NDEF message
                        // Check if there's an existing VCard fragment in current activity/fragment
                        tmpFragment = (NDEFSmsFragment) fragMng.findFragmentByTag("NDEFSimpleMsgSms");
                        tmpFragmentTag = "NDEFSimpleMsgSms";
                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFSmsFragment.newInstance((NDEFSmsMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFSmsFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_VCARD:
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFVCardFragment) fragMng.findFragmentByTag("NDEFVCardFragment");
                        tmpFragmentTag = "NDEFVCardFragment";
                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFVCardFragment.newInstance((NDEFVCardMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFVCardFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;

                    case NDEF_SIMPLE_MSG_TYPE_EXT_M24SRDISCOCTRL:
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFDiscoveryKitCtrlFragment) fragMng.findFragmentByTag("NDEFDiscoveryKitCtrlFragment");
                        tmpFragmentTag = "NDEFDiscoveryKitCtrlFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFDiscoveryKitCtrlFragment.newInstance((NDEFDiscoveryKitCtrlMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFDiscoveryKitCtrlFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_BTHANDOVER:
                        // - BT Handover  NDEF message
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFBTHandoverFragment) fragMng.findFragmentByTag("NDEFBTHandoverFragment");
                        tmpFragmentTag = "NDEFBTHandoverFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFBTHandoverFragment.newInstance((NDEFBTHandoverMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFBTHandoverFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        //launchbtn.setFocusable(true);
                        //launchbtn.setVisibility(View.VISIBLE);
                        //launchbtn.setText(R.string.mnf_frag_connect_action_btn_txt);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_BTLE:
                        // - BT Handover  NDEF message
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFBTLeFragment) fragMng.findFragmentByTag("NDEFBTLeFragment");
                        tmpFragmentTag = "NDEFBTLeFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFBTLeFragment.newInstance((NDEFBTLeMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFBTLeFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_AAR:
                        // - BT Handover  NDEF message
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFAarFragment) fragMng.findFragmentByTag("NDEFAarFragment");
                        tmpFragmentTag = "NDEFAarFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFAarFragment.newInstance((NDEFAarMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFAarFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_WIFIHANDOVER:
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFWifiHandoverFragment) fragMng.findFragmentByTag("NDEFWifiHandoverFragment");
                        tmpFragmentTag = "NDEFWifiHandoverFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFWifiHandoverFragment.newInstance((NDEFWifiHandoverMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFWifiHandoverFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(true);
                        launchbtn.setVisibility(View.VISIBLE);
                        launchbtn.setText(R.string.mnf_frag_connect_action_btn_txt);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_SP:
                        // Check if there's an existing URI fragment in current activity/fragment
                        tmpFragment = (NDEFSPFragment) fragMng.findFragmentByTag("NDEFSPFragment");
                        tmpFragmentTag = "NDEFSPFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFSPFragment.newInstance((NDEFSPMessage)newMsg, true);
                            } else {
                                tmpFragment = NDEFSPFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MULTIPLE_RECORD:
                        tmpFragment = (NDEFMultipleRecordFragment) fragMng.findFragmentByTag("NDEFMultipleRecordFragment");
                        tmpFragmentTag = "NDEFMultipleRecordFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFMultipleRecordFragment.newInstance((NDEFMultipleRecordMessage) newMsg, true);
                            } else {
                                tmpFragment = NDEFMultipleRecordFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    case NDEF_SIMPLE_MSG_TYPE_EXT_TRANSP_GENCTRL:
                        tmpFragment = (NDEFGenCtrlTranspFragment) fragMng.findFragmentByTag("NDEFGenCtrlTranspFragment");
                        tmpFragmentTag = "NDEFGenCtrlTranspFragment";

                        if (tmpFragment == null) {
                            if (newMsg != null) {
                                tmpFragment = NDEFGenCtrlTranspFragment.newInstance((NDEFGenCtrlTranspMessage) newMsg, true);
                            } else {
                                tmpFragment = NDEFGenCtrlTranspFragment.newInstance(true);
                            }
                        }
                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                        break;
                    //case NDEF_SIMPLE_MSG_TYPE_SMART_POSTER:
                    //case NDEF_SIMPLE_MSG_TYPE_TEL_NB:
                    //case NDEF_SIMPLE_MSG_TYPE_WIFI_PAIR,
                    //case NDEF_SIMPLE_MSG_TYPE_PROPRIETARY,
                    default:
                        // Nothing to do... for the moment
                        // Just hide current fragment, if not already hidden
                        Log.v(this.getClass().getName(), "updateSmartFragment NDEF others/not known entry ..");
                        if (_mSmartfragment != null) {
                            FragmentTransaction transaction = fragMng.beginTransaction();
                            transaction.hide(_mSmartfragment);
                            // transaction.commit();
                            transaction.commitAllowingStateLoss();
                            //_mSmartfragment = null;
                        }
                        // Hide and disable launchBtn

                        launchbtn = (Button) _mcurFragmentView.findViewById(R.id.MnfSmartLaunchBtnAreaId).findViewById(R.id.BasicBtnId);
                        launchbtn.setFocusable(false);
                        launchbtn.setVisibility(View.GONE);
                    }
             }

             // FBE

             if (tmpFragment != null) {
                 if (fragMng == null) {
                     fragMng = _mChildfm; // getChildFragmentManager();
                    //    fragMng = getActivity().getSupportFragmentManager();

                 }
                 FragmentTransaction transaction = fragMng.beginTransaction();
                 //NDEFSimplifiedMessageFragment frag = (NDEFSimplifiedMessageFragment)fragMng.findFragmentById(R.id.frSmartViewId);
                 //NDEFSimplifiedMessageFragment frag = (NDEFSimplifiedMessageFragment)fragMng.findFragmentByTag(tmpFragmentTag);
                 NDEFSimplifiedMessageFragment frag = null;
                 if (_mSmartfragment != null) frag=(NDEFSimplifiedMessageFragment)_mChildfm.findFragmentById(_mSmartfragment.getId());

                 if (frag == null) {
                      Log.v(this.getClass().getName(), tmpFragmentTag + "added to MenuSmartNdefViewFragment....");
                      transaction.add(R.id.frSmartViewId, tmpFragment, tmpFragmentTag);
                      //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                      transaction.addToBackStack(tmpFragmentTag);
                      transaction.show(tmpFragment);
                     try {
                         transaction.commit();
                     } catch(Exception ex) {
                         Log.e(this.getClass().getName(), tmpFragment.getTag() + "commit...." + ex.toString());
                     }
                      _mSmartfragment = tmpFragment;
                 } else {
                     if (frag.getTag() == tmpFragmentTag) {
                         Log.v(this.getClass().getName(), tmpFragmentTag + "already exist, updated ....");
                         //transaction.addToBackStack(tmpFragmentTag);
                         //transaction.show(tmpFragment);
                         //frag.onMessageChanged(newMsg);
                         tmpFragment.onMessageChanged(newMsg);
                         transaction.show(tmpFragment);
                         transaction.commitAllowingStateLoss();
                         //transaction.commit();
                         //_mSmartfragment = frag;
                         _mSmartfragment = tmpFragment;

                     } else {
                         Log.v(this.getClass().getName(), frag.getTag() + "replaced by...." + tmpFragmentTag);
                         //if (_mSmartfragment != null) transaction.hide(_mSmartfragment);
                         transaction.replace(R.id.frSmartViewId, tmpFragment, tmpFragmentTag);
                        //FBE                         tmpFragment.onMessageChanged(newMsg);
                         //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                         transaction.addToBackStack(tmpFragmentTag);
                         transaction.show(tmpFragment);
                         try {
                             transaction.commitAllowingStateLoss();
                         } catch(Exception ex) {
                             Log.e(this.getClass().getName(), frag.getTag() + "commitAllowingStateLoss...." + ex.toString());
                         }
                         //transaction.commit();
                         _mSmartfragment = tmpFragment;

                     }
                     //_mSmartfragment = tmpFragment;
                 }

                 try {
                 fragMng.executePendingTransactions();
                 } catch (Exception e) {
                        // popup a message saying that we have an issue ... getting the tag....message and fragments update
                        // SHOW UNLOCK FAILURE MSG
                        String textlog = "Issue ... Updating smart fragments update ...";
                        Toast toast = Toast.makeText(NFCApplication.getContext(),textlog, Toast.LENGTH_SHORT);
                        toast.show();

                 }
             } else {
                 Log.v(this.getClass().getName(), "Smart fragment is null ??? ....");
             }
    }


 public void onTagChanged (NFCTag newTag) {
        // Use a Tag parser for UI purpose: parsing is done when creating "NFCTag" object
     // This parser should:
     // - identify the tag manufacturer (STM or other)
     //    -> if not STM, no logo, no menu for tag management
     //    -> else (= STM tag), identify the product to determine the suitable logo, tag name, and specific menu

     // Update instance attribute

     // Set the layout content according to the content of the tag
     // - Tag header
     TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) _mChildfm.findFragmentById(R.id.RLSmartNDEFViewTagHeaderFragmentId);
     if (mTagHeadFrag != null)
         mTagHeadFrag.onTagChanged(newTag);

    // - Check if NDEF data are present for current tag
     stnfcndefhandler tagNDEFHandler = null;

     NFCTag currentTag;
     currentTag = NFCApplication.getApplication().getCurrentTag();
     // retrieve number of file from CC Files and display radio button selector if necessary
     if (currentTag != null){
         CCFileGenHandler ccFileHdl = currentTag.getCCHandler();
         // According to nbfile display radio buttons
         if (ccFileHdl != null) {
             int nbFiles = ccFileHdl.getNbFile();
             if (nbFiles > 1) {
                 RelativeLayout selectFileIDLayout = (RelativeLayout)_mcurFragmentView.findViewById(R.id.SNFileFragIDFileLayout);
                 selectFileIDLayout.setVisibility(View.VISIBLE);
                 NFCApplication.getApplication().getFileID();
                 // Update Radio button visibility according to the number of TLV blocks from CC File.
                 int buttonIDList[] = {R.id.SNFileID1,R.id.SNFileID2,R.id.SNFileID3,R.id.SNFileID4,R.id.SNFileID5,R.id.SNFileID6,R.id.SNFileID7,R.id.SNFileID8};
                 for (int i=0;i<8;i++)
                 {
                     RadioButton rdButton = (RadioButton) _mcurFragmentView.findViewById(buttonIDList[i]);
                     if (i<NFCApplication.getApplication().getCurrentTag().getCCHandler().getNbFile())
                     {
                         rdButton.setVisibility(View.VISIBLE);
                         if ( i == NFCApplication.getApplication().getFileID())
                         {
                             rdButton.setChecked(true);
                         }
                     }
                     else
                     {
                         rdButton.setVisibility(View.GONE);
                     }
                 }
             } else {
                 //hide Select File ID layout
                 RelativeLayout selectFileIDLayout = (RelativeLayout)_mcurFragmentView.findViewById(R.id.SNFileFragIDFileLayout);
                 selectFileIDLayout.setVisibility(View.GONE);
                 NFCApplication.getApplication().setFileID(0);
                 // FBE Done to avoid possible issues in next block due to TLVID = -1 for the current Tag .... Force
                 NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(0);
             }
         } else {
             //hide Select File ID layout
             RelativeLayout selectFileIDLayout = (RelativeLayout)_mcurFragmentView.findViewById(R.id.SNFileFragIDFileLayout);
             selectFileIDLayout.setVisibility(View.GONE);
             NFCApplication.getApplication().setFileID(0);
             // FBE Done to avoid possible issues in next block due to TLVID = -1 for the current Tag .... Force
             NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(0);
         }

         int currentFile = currentTag.getCurrentValideTLVBlokID();
         if (currentFile != -1) {
             tagNDEFHandler = currentTag.getNDEFHandler(currentFile);
         }

         if (tagNDEFHandler == null || tagNDEFHandler.getStatus() != ndefError.ERR_NDEF_OK) {

             int index = NFCApplication.getApplication().getCurrentTag().getCurrentValideTLVBlokID();
             CCFileGenHandler cchdl = NFCApplication.getApplication().getCurrentTag().getCCHandler();
             // Check if NDEF is locked in Read Mode
             if ((cchdl != null) &&
                     ((cchdl.isNDEFPermanentLOCKRead(index)) || (cchdl.isNDEFLOCKRead(index))
                     )
                     ) {
                 // Unhide "No NDEF data" text view and replace text by lock
                 TextView curTxtView = (TextView) _mcurFragmentView.findViewById(R.id.MnfNoSmartNDEFDataId);
                 //curTxtView.setText("NDEF File Read protected by password");
                 curTxtView.setVisibility(View.VISIBLE);
                 if (cchdl.isAPwdAvailableForNDEFLockRead(index)) {
                     Button _unlockButton = (Button) _mcurFragmentView.findViewById(R.id.unlockSmartReadBtnId);
                     _unlockButton.setVisibility(View.VISIBLE);
                     updateSmartFragment(newTag);
                 } else {
                     Button _unlockButton = (Button) _mcurFragmentView.findViewById(R.id.unlockSmartReadBtnId);
                     _unlockButton.setVisibility(View.GONE);
                     //updateSmartFragment(newTag);
                 }
             } else {
                 Button _unlockButton = (Button) _mcurFragmentView.findViewById(R.id.unlockSmartReadBtnId);
                 _unlockButton.setVisibility(4);
                 // Unhide "No NDEF data" text view
                 TextView curTxtView = (TextView) _mcurFragmentView.findViewById(R.id.MnfNoSmartNDEFDataId);
                 curTxtView.setVisibility(View.VISIBLE);

             }
         } else {
             Button _unlockButton = (Button) _mcurFragmentView.findViewById(R.id.unlockSmartReadBtnId);
             _unlockButton.setVisibility(View.GONE);

             TextView commonTxtView;
             // Hide "No NDEF data" text view
             commonTxtView = (TextView) _mcurFragmentView.findViewById(R.id.MnfNoSmartNDEFDataId);
             commonTxtView.setVisibility(View.GONE);

             updateSmartFragment(newTag);
         }
     }

 }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int RES_SUCCESS = 1; // Need to add a global status enum : 0- SUCCESS / 1-FAIL
        switch (requestCode) {

            case DIALOG_NDEF_PROTECT_UNLOCK:
                if (resultCode == RESULT_OK) {
                    Log.d("DIALOG DEBUG", "Get Password is :" + data.getStringExtra("password"));
                    String password = data.getStringExtra("password");
                    if (password != null) {
                        password = data.getStringExtra("password").toString();
                    } else {
                        password = "Wrong one";
                    }
                    if (RES_SUCCESS == NFCApplication.getApplication().getCurrentTag().readLockedNDEFFile(password)) {
                        _mListener.OnlockNdefMessage();
                    } else {
                        // SHOW UNLOCK FAILURE MSG
                        Toast toast = Toast.makeText(NFCApplication.getContext(), getString(R.string.err_tag_not_unlocked), Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
                break;


        }
    }

    public void connect() {
        if (_mSmartfragment instanceof NDEFWifiHandoverFragment)  {
            connectWifi();
        }
        if (_mSmartfragment instanceof NDEFBTHandoverFragment) {
            connectBT();
        }
        if (_mSmartfragment instanceof NDEFBTLeFragment)  {

        }
    }

    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 30;
    private BluetoothDevice discoverBTDevice(String macAddress) {
        BluetoothDevice deviceFound = null;
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
        if (macAddress == null) return deviceFound;
/*        if (mBluetoothAdapter == null)
            showToast("No Bluetooth available");
        else
            showToast("Bluetooth available");

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
*/
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (pairedDevices.isEmpty()) {
            showToast("No devices paired...");
        } else {
            for (BluetoothDevice device : pairedDevices) {
                showToast("Found Device : address : " + device.getAddress() + " name :"
                        + device.getName());
                if (macAddress.equals(device.getAddress())) {
                    deviceFound = device;
                    break;
                }
            }
        }
        return deviceFound;
    }

    private void connectBT() {
        NDEFBTHandoverMessage ndefMessage;
        if (_mSmartfragment instanceof NDEFBTHandoverFragment) {
            ndefMessage = (NDEFBTHandoverMessage) _mSmartfragment.getNDEFSimplifiedMessage();
            BluetoothDevice deviceFound = null;
            deviceFound = discoverBTDevice(ndefMessage.get_string2macAddr());
            if (deviceFound != null) pairDevice(deviceFound);
        }
    }

 private void connectWifi()
 {
     NDEFWifiHandoverMessage ndefMessage;
     Toast toast;
     if (_mSmartfragment instanceof NDEFWifiHandoverFragment)
     {
         ndefMessage = (NDEFWifiHandoverMessage) _mSmartfragment.getNDEFSimplifiedMessage();

        String SSID =         ndefMessage.getSSID();
        String password =     ndefMessage.getEncrKey();
        int authType =        ndefMessage.getAuthType();
        int encrType =        ndefMessage.getEncrType();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID="\""+SSID+"\"";
        //conf.SSID=SSID;
        // if authType = 0 -> Open Network
        if (authType == 0)
        {
            //conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedAuthAlgorithms.clear();
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        }
        // else if authType = 1 (WPA/WPA2 PSK authType)
        else if (authType == 1)
        {
            conf.preSharedKey = "\""+password+"\"";
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            if (encrType == 0)
            {
                conf.allowedKeyManagement.set(WifiConfiguration.GroupCipher.WEP40); // to confirm
            }
            else if (encrType == 1)
            {
                conf.allowedKeyManagement.set(WifiConfiguration.GroupCipher.TKIP);
            }
            else if (encrType == 2)
            {
                conf.allowedKeyManagement.set(WifiConfiguration.GroupCipher.CCMP);
            }
        }
        else
        {
            toast = Toast.makeText(NFCApplication.getContext(), "Unsupported yet Authentification type", Toast.LENGTH_LONG);
            toast.show();
        }


        WifiManager wifiManager = (WifiManager)NFCApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled())
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifiManager.setWifiEnabled(true);

//        conf.status=WifiConfiguration.Status.ENABLED;
//        int netId=wifiManager.addNetwork(conf);
//        wifiManager.saveConfiguration();
//        wifiManager.reconnect();


        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

     }
 }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            showToast("Device paired : " + device.getName());

        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showToast(String message) {
        Toast.makeText(NFCApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
