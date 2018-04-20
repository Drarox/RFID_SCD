/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1708 $
  * Revision of last commit    :  $Rev: 1708 $
  * Date of last commit     :  $Date: 2016-02-28 17:44:48 +0100 (Sun, 28 Feb 2016) $ 
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

import com.st.Fragments.MenuSmartNdefViewFragment.NdefViewFragmentListener;
import com.st.Fragments.MenuToolsFragment.actionType;
import com.st.NFC.CCFileGenHandler;
import com.st.demo.R;
import com.st.demo.MMYDemoWriteNDEFActivity;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler;
import com.st.util.PasswordDialogFragment;
import com.st.NDEF.ndefError;
import com.st.NDEF.stnfcndefhandler;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.stnfchelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class MenuNDEFFilesFragment extends NFCPagerFragment {
    final static int LAYOUT_ALIGN_NONE = -1;
    // TODO: put following defines in global XML IDs file
    final static int LAYOUT_ID_MESSAGE_LAYOUT_PREFIX = 0x10000000;
    final static int LAYOUT_ID_RECORD_LAYOUT_PREFIX = 0x20000000;
    final static int LAYOUT_ID_HEADER_TEXT_PREFIX = 0x01000000;
    final static int LAYOUT_ID_FIELD_TEXT_PREFIX = 0x02000000;

    // Store view corresponding to current fragment
    private View _curFragmentView = null;


    private actionType currentAction;
    public static final int DIALOG_NDEF_PROTECT_UNLOCK = 3;
    public static final int RESULT_OK = 101;


    private int currentTLVBlockID = 0;


    NdefViewFragmentListener _mListener;


    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag NFC Tag to consider
     * @return A new instance of fragment MenuNDEFFilesFragment.
     */
    public static MenuNDEFFilesFragment newInstance(NFCTag mNFCTag) {
        MenuNDEFFilesFragment fragment = new MenuNDEFFilesFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuNDEFFilesFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuNDEFFilesFragment fragment = new MenuNDEFFilesFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuNDEFFilesFragment() {
        // Required empty public constructor
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _mListener = (NdefViewFragmentListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MenuSmartNdefViewFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + " page: " + page + " Name: " + title);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "onCreateView Fragment");
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_ndef_files, container,
                false);

        // Create the nested fragments (tag header one)
        FragmentManager fragMng = getChildFragmentManager();
        // First check if the fragment already exists (in case current fragment has been temporarily destroyed)
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) fragMng.findFragmentById(R.id.MnfTagHeaderFragmentId);
        if (mTagHeadFrag == null) {
            mTagHeadFrag = new TagHeaderFragment();
            FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.add(R.id.MnfTagHeaderFragmentId, mTagHeadFrag);
            transaction.commit();
            fragMng.executePendingTransactions();
        }

        // Configure the buttons
        // "Read" button
        /*
        Button readBtn = (Button) _curFragmentView.findViewById(R.id.MnfReadBtnAreaId).findViewById(R.id.BasicBtnId);
        readBtn.setText(R.string.mnf_frag_read_menu_btn_txt);
        readBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MMYDemoNDEFReadActivity.class);
                startActivity(intent);
            }
        });
        */
        // "Write" button
        Button writeBtn = (Button) _curFragmentView.findViewById(R.id.MnfWriteBtnAreaId).findViewById(R.id.BasicBtnId);
        writeBtn.setText(R.string.mnf_frag_write_menu_btn_txt);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MenuNDEFWriteActivity.class);
                NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(currentTLVBlockID);
                NFCApplication.getApplication().setFileID(currentTLVBlockID);
                Intent intent = new Intent(getActivity(), MMYDemoWriteNDEFActivity.class);
                startActivity(intent);
            }
        });


        Button _unlockButton = (Button) _curFragmentView.findViewById(R.id.unlockReadBtnId);

        //
        _unlockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

                    newFragment.setTargetFragment(MenuNDEFFilesFragment.this, DIALOG_NDEF_PROTECT_UNLOCK);
                    newFragment.show(getFragmentManager(), "dialog");
                    MenuNDEFFilesFragment.this.currentAction = MenuToolsFragment.actionType.UNLOCK_READ_NDEF_FILE;

                } else {

                    PasswordDialogFragment newFragment = (PasswordDialogFragment) PasswordDialogFragment.newInstance(
                            getString(R.string.pw_title),
                            getString(R.string.pw_readmsg),
                            getString(R.string.pw_button_ok),
                            getString(R.string.pw_button_cancel));

                    newFragment.setTargetFragment(MenuNDEFFilesFragment.this, DIALOG_NDEF_PROTECT_UNLOCK);
                    newFragment.show(getFragmentManager(), "dialog");
                    MenuNDEFFilesFragment.this.currentAction = MenuToolsFragment.actionType.UNLOCK_READ_NDEF_FILE;
                }
            }
        });

        _unlockButton.setVisibility(View.INVISIBLE);

        if (NFCApplication.getApplication().getCurrentTag().getType() == NFCTag.NfcTagTypes.NFC_TAG_TYPE_4A) {
            writeBtn.setVisibility(View.VISIBLE);
        } else {
            writeBtn.setVisibility(View.INVISIBLE);
        }

        return _curFragmentView;
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
        RadioGroup radioGroup = (RadioGroup) _curFragmentView.findViewById(R.id.MnfNDEFDescrRadiogroupIDFileLayout);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.MnfNDEFDescrFileID1:
                        currentTLVBlockID = 0;
                        break;
                    case R.id.MnfNDEFDescrFileID2:
                        currentTLVBlockID = 1;
                        break;
                    case R.id.MnfNDEFDescrFileID3:
                        currentTLVBlockID = 2;
                        break;
                    case R.id.MnfNDEFDescrFileID4:
                        currentTLVBlockID = 3;
                        break;
                    case R.id.MnfNDEFDescrFileID5:
                        currentTLVBlockID = 4;
                        break;
                    case R.id.MnfNDEFDescrFileID6:
                        currentTLVBlockID = 5;
                        break;
                    case R.id.MnfNDEFDescrFileID7:
                        currentTLVBlockID = 6;
                        break;
                    case R.id.MnfNDEFDescrFileID8:
                        currentTLVBlockID = 7;
                        break;
                    default:
                        currentTLVBlockID = 0;
                        break;
                }
                // updateTLVInf(currentTLVBlockID);

                NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(currentTLVBlockID);
                NFCApplication.getApplication().setFileID(currentTLVBlockID);
                // updateSmartFragment( NFCApplication.getApplication().getCurrentTag());
                onTagChanged(NFCApplication.getApplication().getCurrentTag());

            }
        });


        // Fill in the layout with the currentTag
        onTagChanged (NFCApplication.getApplication().getCurrentTag());
    }

    @Override
    public void onResume() {
        Log.v(this.getClass().getName(), "onResume Fragment");
        super.onResume();

        // Fill in the layout with the currentTag
        onTagChanged(NFCApplication.getApplication().getCurrentTag());
    }


/*    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getName(), "onDestroyView Fragment");
        super.onDestroyView();
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onTagChanged(NFCTag newTag) {
        // Update instance attribute

        // Set the layout content according to the content of the tag
        // - Tag header
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getChildFragmentManager().findFragmentById(R.id.MnfTagHeaderFragmentId);
        mTagHeadFrag.onTagChanged(NFCApplication.getApplication().getCurrentTag());

        // - Check if NDEF data are present for current tag
        stnfcndefhandler tagNDEFHandler = null;

        // retrieve number of file from CC Files and display radio button selector if necessary
        if (((NFCApplication.getApplication().getCurrentTag().getCCHandler() != null)) &&
                (NFCApplication.getApplication().getCurrentTag().getCCHandler().getNbFile() > 1)) {
            RelativeLayout selectFileIDLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.MnfNDEFDescrFileFragIDFileLayout);
            selectFileIDLayout.setVisibility(View.VISIBLE);
            NFCApplication.getApplication().getFileID();
            // Update Radio button visibility according to the number of TLV blocks from CC File.
            int buttonIDList[] = {R.id.MnfNDEFDescrFileID1, R.id.MnfNDEFDescrFileID2, R.id.MnfNDEFDescrFileID3, R.id.MnfNDEFDescrFileID4, R.id.MnfNDEFDescrFileID5, R.id.MnfNDEFDescrFileID6, R.id.MnfNDEFDescrFileID7, R.id.MnfNDEFDescrFileID8};
            for (int i = 0; i < 8; i++) {
                RadioButton rdButton = (RadioButton) _curFragmentView.findViewById(buttonIDList[i]);
                if (i < NFCApplication.getApplication().getCurrentTag().getCCHandler().getNbFile()) {
                    rdButton.setVisibility(View.VISIBLE);
                    if (i == NFCApplication.getApplication().getFileID()) {
                        rdButton.setChecked(true);
                    }
                } else {
                    rdButton.setVisibility(View.GONE);
                }
            }

        } else {
            //hide Select File ID layout
            RelativeLayout selectFileIDLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.MnfNDEFDescrFileFragIDFileLayout);
            selectFileIDLayout.setVisibility(View.GONE);
            NFCApplication.getApplication().setFileID(0);
        }

        if (NFCApplication.getApplication().getCurrentTag().getCurrentValideTLVBlokID() != -1) {
            tagNDEFHandler = NFCApplication.getApplication().getCurrentTag().getNDEFHandler(NFCApplication.getApplication().getCurrentTag().getCurrentValideTLVBlokID());
        }
        // FAR BEGIN
        // Enable "Write" button only for NFC_TAG_TYPE_4A
        Button writeBtn = (Button) _curFragmentView.findViewById(R.id.MnfWriteBtnAreaId).findViewById(R.id.BasicBtnId);
        if (NFCApplication.getApplication().getCurrentTag().getType() == NFCTag.NfcTagTypes.NFC_TAG_TYPE_4A) {
            writeBtn.setVisibility(View.VISIBLE);
        } else {
            writeBtn.setVisibility(View.INVISIBLE);
        }


        if (tagNDEFHandler == null || tagNDEFHandler.getStatus() != ndefError.ERR_NDEF_OK) {
            int index = NFCApplication.getApplication().getCurrentTag().getCurrentValideTLVBlokID();
            CCFileGenHandler cchdl = NFCApplication.getApplication().getCurrentTag().getCCHandler();
            // Check if NDEF is locked in Read Mode
            if ((cchdl != null) &&
                    ((cchdl.isNDEFPermanentLOCKRead(index)) || (cchdl.isNDEFLOCKRead(index))
                    )
                    ) {
            // Check if NDEF is locked in Read Mode
//            if ((NFCApplication.getApplication().getCurrentTag().getCCHandler() != null) &&
//                    ((NFCApplication.getApplication().getCurrentTag().getCCHandler().isNDEFPermanentLOCKRead(NFCApplication.getApplication().getCurrentTag().getCurrentValideTLVBlokID())) ||
//                            (NFCApplication.getApplication().getCurrentTag().getCCHandler().isNDEFLOCKRead(NFCApplication.getApplication().getCurrentTag().getCurrentValideTLVBlokID()))
//                    )
//                    ) {
                // Unhide "No NDEF data" text view and replace text by lock
                TextView curTxtView = (TextView) _curFragmentView.findViewById(R.id.MnfNoNDEFDataId);
                //curTxtView.setText("NDEF File Read protected by password");
                curTxtView.setVisibility(View.VISIBLE);
                if (cchdl.isAPwdAvailableForNDEFLockRead(index)) {
                    Button _unlockButton = (Button) _curFragmentView.findViewById(R.id.unlockReadBtnId);
                    _unlockButton.setVisibility(View.VISIBLE);
                    ScrollView NDEFDescr = (ScrollView) _curFragmentView.findViewById(R.id.MnfNDEFDescrScrollViewId);
                    NDEFDescr.setVisibility(View.GONE);
                } else {
                    Button _unlockButton = (Button) _curFragmentView.findViewById(R.id.unlockReadBtnId);
                    _unlockButton.setVisibility(View.GONE);
                    ScrollView NDEFDescr = (ScrollView) _curFragmentView.findViewById(R.id.MnfNDEFDescrScrollViewId);
                    NDEFDescr.setVisibility(View.GONE);
                }


            } else {
                Button _unlockButton = (Button) _curFragmentView.findViewById(R.id.unlockReadBtnId);
                _unlockButton.setVisibility(4);
                // Unhide "No NDEF data" text view
                TextView curTxtView = (TextView) _curFragmentView.findViewById(R.id.MnfNoNDEFDataId);
                curTxtView.setVisibility(View.VISIBLE);
                // Hide NDEF description (ScrollView) & Read button
                ScrollView NDEFDescr = (ScrollView) _curFragmentView.findViewById(R.id.MnfNDEFDescrScrollViewId);
                NDEFDescr.setVisibility(View.GONE);
                //Button readBtn = (Button) _curFragmentView.findViewById(R.id.MnfReadBtnAreaId).findViewById(R.id.BasicBtnId);
                //readBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            Button _unlockButton = (Button) _curFragmentView.findViewById(R.id.unlockReadBtnId);
            _unlockButton.setVisibility(4);

            TextView commonTxtView;
            // Hide "No NDEF data" text view
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.MnfNoNDEFDataId);
            commonTxtView.setVisibility(View.GONE);
            // Unhide NDEF description (ScrollView) & Read button
            ScrollView NDEFDescr = (ScrollView) _curFragmentView.findViewById(R.id.MnfNDEFDescrScrollViewId);
            NDEFDescr.setVisibility(View.VISIBLE);
            //Button readBtn = (Button) _curFragmentView.findViewById(R.id.MnfReadBtnAreaId).findViewById(R.id.BasicBtnId);
            //readBtn.setVisibility(View.VISIBLE);

            // Remove all existing views in ScrollView (actually underlying LinearLayout...),
            // to recreate new ones with new info from the tag
            // TODO: to be improved for better performances !!!...
            LinearLayout ndefDescrParentLayout = (LinearLayout) _curFragmentView.findViewById(R.id.MnfNDEFDescrContainerId);
            if (ndefDescrParentLayout.getChildCount() > 0) {
                ndefDescrParentLayout.removeAllViews();
            }

            // Create the structure of the NDEF description
            int prevMsgLayoutId = LAYOUT_ALIGN_NONE;
            // Iterate on all messages
            for (int msgIdx = 0; msgIdx < tagNDEFHandler.getFilesNb(); msgIdx++) {
                // Create NDEF file header
                // - Relative Layout
                int curMsgLayoutId = LAYOUT_ID_MESSAGE_LAYOUT_PREFIX + ((msgIdx + 1) << 16);
                RelativeLayout curMsgLayout = createMsgRelLayout(curMsgLayoutId, prevMsgLayoutId);
                ndefDescrParentLayout.addView(curMsgLayout);
                // - Message header
                int curMsgHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16);
                TextView curMsgHeaderTxt =
                        createHeaderTextView(getString(R.string.mnf_frag_NDEF_msg_header_txt),
                                curMsgHeaderId,
                                LAYOUT_ALIGN_NONE,
                                0, 0, 0, 0);
                curMsgLayout.addView(curMsgHeaderTxt);
                // - Message field
                int curMsgFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16);
                String curMsgFieldStr = String.valueOf(msgIdx + 1) + " / " + String.valueOf(tagNDEFHandler.getFilesNb());
                TextView curMsgFieldTxt =
                        createFieldTextView(curMsgFieldStr,
                                curMsgFieldId,
                                LAYOUT_ALIGN_NONE,
                                LAYOUT_ALIGN_NONE,
                                curMsgHeaderId,
                                curMsgHeaderId,
                                getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                curMsgLayout.addView(curMsgFieldTxt);

                // NLEN desc
                // - Relative Layout
                int curNLENLayoutId = LAYOUT_ID_MESSAGE_LAYOUT_PREFIX + ((msgIdx + 1) << 16) + 1;
                RelativeLayout curNLENLayout = createRecRelLayout(curNLENLayoutId, curMsgHeaderId);
                curMsgLayout.addView(curNLENLayout);
                // - NLEN header
                int curNLENHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + 1;
                TextView curNLENHeaderTxt =
                        createHeaderTextView(getString(R.string.mnf_frag_NDEF_NLEN_header_txt),
                                curNLENHeaderId,
                                LAYOUT_ALIGN_NONE,
                                0, 0, 0, 0);
                curNLENLayout.addView(curNLENHeaderTxt);
                // - NLEN field
                int curNLENFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + 1;
                String curNLENFieldStr = String.valueOf(tagNDEFHandler.getNLEN()) + " " + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt);
                TextView curNLENFieldTxt =
                        createFieldTextView(curNLENFieldStr,
                                curNLENFieldId,
                                LAYOUT_ALIGN_NONE,
                                LAYOUT_ALIGN_NONE,
                                curNLENHeaderId,
                                curNLENHeaderId,
                                getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                curNLENLayout.addView(curNLENFieldTxt);

                // Iterate on all records of the file
                int prevRecLayoutId = curNLENLayoutId;
                for (int recIdx = 0; recIdx < tagNDEFHandler.getRecordNb(msgIdx); recIdx++) {
                    // Create NDEF record header
                    // - Relative Layout
                    int curRecLayoutId = LAYOUT_ID_RECORD_LAYOUT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8);
                    RelativeLayout curRecLayout = createRecRelLayout(curRecLayoutId, prevRecLayoutId);
                    curMsgLayout.addView(curRecLayout);
                    // - Record header
                    int curRecHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8);
                    TextView curRecHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_header_txt),
                                    curRecHeaderId,
                                    LAYOUT_ALIGN_NONE,
                                    0, 0, 0, 0);
                    curRecLayout.addView(curRecHeaderTxt);
                    // - Record field
                    int curRecFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8);
                    String curRecFieldStr = String.valueOf(recIdx + 1) + " / " + String.valueOf(tagNDEFHandler.getRecordNb(msgIdx));
                    TextView curRecFieldTxt =
                            createFieldTextView(curRecFieldStr,
                                    curRecFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecHeaderId,
                                    curRecHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecLayout.addView(curRecFieldTxt);

                    // Record Content desc
                    // - Relative Layout
                    int curRecContentLayoutId = LAYOUT_ID_RECORD_LAYOUT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 1;
                    RelativeLayout curRecContentLayout = createRecRelLayout(curRecContentLayoutId, curRecHeaderId);
                    curRecLayout.addView(curRecContentLayout);
                    // - MB
                    int curRecMBHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 1;
                    TextView curRecMBHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_MB_header_txt),
                                    curRecMBHeaderId,
                                    LAYOUT_ALIGN_NONE,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecMBHeaderTxt);
                    int curRecMBFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 1;
                    String curRecMBFieldStr;
                    if (tagNDEFHandler.isamessagebegin(recIdx)) {
                        curRecMBFieldStr = "1";
                    } else {
                        curRecMBFieldStr = "0";
                    }
                    TextView curRecMBFieldTxt =
                            createFieldTextView(curRecMBFieldStr,
                                    curRecMBFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecMBHeaderId,
                                    curRecMBHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecMBFieldTxt);
                    // - ME
                    int curRecMEHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 2;
                    TextView curRecMEHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_ME_header_txt),
                                    curRecMEHeaderId,
                                    curRecMBHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecMEHeaderTxt);
                    int curRecMEFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 2;
                    String curRecMEFieldStr;
                    if (tagNDEFHandler.isamessageend(recIdx)) {
                        curRecMEFieldStr = "1";
                    } else {
                        curRecMEFieldStr = "0";
                    }
                    TextView curRecMEFieldTxt =
                            createFieldTextView(curRecMEFieldStr,
                                    curRecMEFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecMEHeaderId,
                                    curRecMEHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecMEFieldTxt);
                    // - CF
                    int curRecCFHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 3;
                    TextView curRecCFHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_CF_header_txt),
                                    curRecCFHeaderId,
                                    curRecMEHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecCFHeaderTxt);
                    int curRecCFFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 3;
                    String curRecCFFieldStr;
                    if (tagNDEFHandler.isachunkedmessage(recIdx)) {
                        curRecCFFieldStr = "1";
                    } else {
                        curRecCFFieldStr = "0";
                    }
                    TextView curRecCFFieldTxt =
                            createFieldTextView(curRecCFFieldStr,
                                    curRecCFFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecCFHeaderId,
                                    curRecCFHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecCFFieldTxt);
                    // - SR
                    int curRecSRHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 4;
                    TextView curRecSRHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_SR_header_txt),
                                    curRecSRHeaderId,
                                    curRecCFHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecSRHeaderTxt);
                    int curRecSRFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 4;
                    String curRecSRFieldStr;
                    if (tagNDEFHandler.isashortrecord(recIdx)) {
                        curRecSRFieldStr = "1";
                    } else {
                        curRecSRFieldStr = "0";
                    }
                    TextView curRecSRFieldTxt =
                            createFieldTextView(curRecSRFieldStr,
                                    curRecSRFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecSRHeaderId,
                                    curRecSRHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecSRFieldTxt);
                    // - IL
                    int curRecILHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 5;
                    TextView curRecILHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_IL_header_txt),
                                    curRecILHeaderId,
                                    curRecSRHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecILHeaderTxt);
                    int curRecILFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 5;
                    String curRecILFieldStr;
                    if (tagNDEFHandler.isIDpresent(recIdx)) {
                        curRecILFieldStr = getString(R.string.all_act_present);
                    } else {
                        curRecILFieldStr = getString(R.string.all_act_absent);
                    }
                    TextView curRecILFieldTxt =
                            createFieldTextView(curRecILFieldStr,
                                    curRecILFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecILHeaderId,
                                    curRecILHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecILFieldTxt);
                    // - TNF
                    int curRecTNFHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 6;
                    TextView curRecTNFHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_TNF_header_txt),
                                    curRecTNFHeaderId,
                                    curRecILHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecTNFHeaderTxt);
                    int curRecTNFFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 6;
                    String curRecTNFFieldStr = tagNDEFHandler.getTNFtoString(recIdx)
                            + " (0x" + String.format("%02X", tagNDEFHandler.gettnf(recIdx).ordinal()) + ")";
                    TextView curRecTNFFieldTxt =
                            createFieldTextView(curRecTNFFieldStr,
                                    curRecTNFFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecTNFHeaderId,
                                    curRecTNFHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecTNFFieldTxt);
                    // - Type Length
                    int curRecTLgthHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 7;
                    TextView curRecTLgthHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_TLgth_header_txt),
                                    curRecTLgthHeaderId,
                                    curRecTNFHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecTLgthHeaderTxt);
                    int curRecTLgthFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 7;
                    String curRecTLgthFieldStr = String.valueOf(tagNDEFHandler.gettypelength(recIdx)) + " " + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt);
                    TextView curRecTLgthFieldTxt =
                            createFieldTextView(curRecTLgthFieldStr,
                                    curRecTLgthFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecTLgthHeaderId,
                                    curRecTLgthHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecTLgthFieldTxt);
                    // - Payload Length
                    int curRecPLgthHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 8;
                    TextView curRecPLgthHeaderTxt =
                            createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_PLgth_header_txt),
                                    curRecPLgthHeaderId,
                                    curRecTLgthHeaderId,
                                    0, 0, 0, 0);
                    curRecContentLayout.addView(curRecPLgthHeaderTxt);
                    int curRecPLgthFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 8;
                    String curRecPLgthFieldStr = String.valueOf(tagNDEFHandler.getpayloadlength(recIdx)) + " " + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt);
                    TextView curRecPLgthFieldTxt =
                            createFieldTextView(curRecPLgthFieldStr,
                                    curRecPLgthFieldId,
                                    LAYOUT_ALIGN_NONE,
                                    LAYOUT_ALIGN_NONE,
                                    curRecPLgthHeaderId,
                                    curRecPLgthHeaderId,
                                    getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                    curRecContentLayout.addView(curRecPLgthFieldTxt);
                    // - ID Length (if IL is set)
                    int prevRecFieldId = curRecPLgthHeaderId;
                    int curRecIDLgthHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 9;
                    if (tagNDEFHandler.isIDpresent(recIdx)) {
                        TextView curRecIDLgthHeaderTxt =
                                createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_IDLength_header_txt),
                                        curRecIDLgthHeaderId,
                                        curRecPLgthHeaderId,
                                        0, 0, 0, 0);
                        curRecContentLayout.addView(curRecIDLgthHeaderTxt);
                        int curRecIDLgthFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 9;
                        String curRecIDLgthFieldStr = String.valueOf(tagNDEFHandler.getIDlength(recIdx)) + " " + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt);
                        TextView curRecIDLgthFieldTxt =
                                createFieldTextView(curRecIDLgthFieldStr,
                                        curRecIDLgthFieldId,
                                        LAYOUT_ALIGN_NONE,
                                        LAYOUT_ALIGN_NONE,
                                        curRecIDLgthHeaderId,
                                        curRecIDLgthHeaderId,
                                        getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                        curRecContentLayout.addView(curRecIDLgthFieldTxt);

                        prevRecFieldId = curRecIDLgthHeaderId;
                    }
                    // - Type (if Type Length is not null)
                    int curRecTypeHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 10;
                    if (tagNDEFHandler.gettypelength(recIdx) > 0) {
                        TextView curRecTypeHeaderTxt =
                                createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_Type_header_txt),
                                        curRecTypeHeaderId,
                                        prevRecFieldId,
                                        0, 0, 0, 0);
                        curRecContentLayout.addView(curRecTypeHeaderTxt);
                        int curRecTypeFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 10;
                        byte[] type = tagNDEFHandler.gettype(recIdx);
                        String curRecTypeFieldStr = "";
                        if (type != null) {
                            curRecTypeFieldStr = stnfchelper.bytArrayToHex(tagNDEFHandler.gettype(recIdx));
                        }
                        TextView curRecTypeFieldTxt =
                                createFieldTextView(curRecTypeFieldStr,
                                        curRecTypeFieldId,
                                        LAYOUT_ALIGN_NONE,
                                        LAYOUT_ALIGN_NONE,
                                        curRecTypeHeaderId,
                                        curRecTypeHeaderId,
                                        getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                        curRecContentLayout.addView(curRecTypeFieldTxt);

                        prevRecFieldId = curRecTypeHeaderId;
                    }
                    // - ID (if ID Length is present and not null)
                    int curRecIDHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 11;
                    if (tagNDEFHandler.isIDpresent(recIdx) && (tagNDEFHandler.getIDlength(recIdx) > 0)) {
                        TextView curRecIDHeaderTxt =
                                createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_ID_header_txt),
                                        curRecIDHeaderId,
                                        prevRecFieldId,
                                        0, 0, 0, 0);
                        curRecContentLayout.addView(curRecIDHeaderTxt);
                        int curRecIDFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 11;
                        String curRecIDFieldStr = stnfchelper.bytArrayToHex(tagNDEFHandler.getID(recIdx));
                        TextView curRecIDFieldTxt =
                                createFieldTextView(curRecIDFieldStr,
                                        curRecIDFieldId,
                                        LAYOUT_ALIGN_NONE,
                                        LAYOUT_ALIGN_NONE,
                                        curRecIDHeaderId,
                                        curRecIDHeaderId,
                                        getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                        curRecContentLayout.addView(curRecIDFieldTxt);

                        prevRecFieldId = curRecIDHeaderId;
                    }
                    // - Payload (if Payload Length is not null)
                    int curRecPayloadHeaderId = LAYOUT_ID_HEADER_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 12;
                    if (tagNDEFHandler.getpayloadlength(recIdx) > 0) {
                        TextView curRecPayloadHeaderTxt =
                                createHeaderTextView(getString(R.string.mnf_frag_NDEF_rec_Payload_header_txt),
                                        curRecPayloadHeaderId,
                                        prevRecFieldId,
                                        0, 0, 0, 0);
                        curRecContentLayout.addView(curRecPayloadHeaderTxt);
                        int curRecPayloadFieldId = LAYOUT_ID_FIELD_TEXT_PREFIX + ((msgIdx + 1) << 16) + ((recIdx + 1) << 8) + 12;
                        String curRecPayloadFieldStr = tagNDEFHandler.getPayloadtoHex(recIdx);
                        TextView curRecPayloadFieldTxt =
                                createFieldTextView(curRecPayloadFieldStr,
                                        curRecPayloadFieldId,
                                        LAYOUT_ALIGN_NONE,
                                        curRecPayloadHeaderId,
                                        curRecPayloadHeaderId,
                                        LAYOUT_ALIGN_NONE,
                                        getResources().getDimensionPixelSize(R.dimen.ti_act_add_txt_field_left_margin), 0, 0, 0);
                        curRecContentLayout.addView(curRecPayloadFieldTxt);

                        prevRecFieldId = curRecPayloadHeaderId;
                    }

                    // Prepare next iteration
                    prevRecLayoutId = curRecLayoutId;
                }

                // Prepare next iteration
                prevMsgLayoutId = curMsgLayoutId;
            }
        }
    }

    private RelativeLayout createMsgRelLayout(int id, int alignView) {
        RelativeLayout newRelLayout = new RelativeLayout(getActivity());

        // Set View id
        newRelLayout.setId(id);

        // Set layout parameters
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        //layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.NDEF_message_top_margin), 0, 0);
        if (alignView != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.BELOW, alignView);
        }
        newRelLayout.setLayoutParams(layoutParams);

        return newRelLayout;
    }

    private RelativeLayout createRecRelLayout(int id, int alignView) {
        RelativeLayout newRelLayout = new RelativeLayout(getActivity());

        // Set View id
        newRelLayout.setId(id);

        // Set layout parameters
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.mnf_frag_NDEF_rec_left_margin),
                getResources().getDimensionPixelSize(R.dimen.mnf_frag_NDEF_rec_top_margin),
                getResources().getDimensionPixelSize(R.dimen.mnf_frag_NDEF_rec_right_margin),
                getResources().getDimensionPixelSize(R.dimen.mnf_frag_NDEF_rec_bottom_margin));
        if (alignView != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.BELOW, alignView);
        }
        newRelLayout.setLayoutParams(layoutParams);

        return newRelLayout;
    }

    private TextView createHeaderTextView(String text, int id, int alignView, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        TextView newTxtView = new TextView(getActivity());

        // Set View id
        newTxtView.setId(id);

        // Set layout parameters
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.setMargins(leftMargin,
                topMargin,
                rightMargin,
                bottomMargin);
        if (alignView != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.BELOW, alignView);
        }
        newTxtView.setLayoutParams(layoutParams);

        // Format text
        newTxtView.setText(text);
        newTxtView.setTextSize(getResources().getDimensionPixelSize(R.dimen.mnf_frag_NDEF_msg_header_field_size));
        newTxtView.setTypeface(Typeface.SERIF, Typeface.BOLD);

        return newTxtView;
    }

    // TODO: Rename the parameters
    // Because, actually, this is not really an alignment on Left and Right parameters, but a position to the left and right...
    private TextView createFieldTextView(String text, int id,
                                         int alignLeft, int alignTop, int alignRight, int alignBottom,
                                         int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        TextView newTxtView = new TextView(getActivity());

        // Set View id
        newTxtView.setId(id);

        // Set layout parameters
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(leftMargin,
                topMargin,
                rightMargin,
                bottomMargin);
        if (alignLeft != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.LEFT_OF, alignLeft);
        }
        if (alignTop != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, alignTop);
        }
        if (alignRight != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.RIGHT_OF, alignRight);
        }
        if (alignBottom != LAYOUT_ALIGN_NONE) {
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, alignBottom);
        }
        newTxtView.setLayoutParams(layoutParams);

        // Format text
        newTxtView.setText(text);
        newTxtView.setTextSize(getResources().getDimensionPixelSize(R.dimen.mnf_frag_NDEF_msg_nb_field_size));
        newTxtView.setTypeface(Typeface.SERIF);

        return newTxtView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    NFCApplication.getApplication().getCurrentTag().readLockedNDEFFile(password);
                    // Show lock state

                    // request parent fragment update
                    // send intent to the
                    //this.onTagChanged(NFCApplication.getApplication().getCurrentTag());
                    _mListener.OnlockNdefMessage();
                }
                break;


        }
    }

}
