/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1673 $
  * Revision of last commit    :  $Rev: 1673 $
  * Date of last commit     :  $Date: 2016-02-18 17:11:58 +0100 (Thu, 18 Feb 2016) $ 
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
import com.st.demo.R;
import com.st.demo.WaitForNFCTapActivity;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NDEFUI.NDEFDiscoveryKitCtrlFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.util.PasswordDialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

public class MenuToolsFragment extends NFCPagerFragment {

    private View _curFragmentView = null; // Store view corresponding to current
                                            // fragment

    private boolean _lockWritecheck;

    private boolean _lockReadcheck;

    private boolean _GPOTogglecheck;

    private String tempPasswordRead; // used to store the password while we
                                        // retrieve the Modification Password

    // Used To manage Password from Dialog
    public static final int DIALOG_FRAGMENT_WRITE = 1;
    public static final int DIALOG_FRAGMENT_READ = 2;
    public static final int DIALOG_FRAGMENT_RIGHT = 3;

    public static final int RESULT_OK = 101;
    public static final int TOOL_REQUEST_DONE = 102;
    public static final int TOOL_EXCHANGE_DATA_DONE = 103;

    // Current Activity Handling the fragment
    NdefViewFragmentListener _mListener;

    public Button buttonSettingsDemoDone;

    public static final String WAIT_FOR_TAP_ACTION = "WAIT_FOR_TAP_ACTION";

    public enum actionType {
        LOCK_WRITE_ACCESS, UNLOCK_WRITE_ACCESS, LOCK_READ_ACCESS, UNLOCK_READ_ACCESS, UNLOCK_READ_NDEF_FILE, SETSTATE_GPO, ERASE_NDEF,
        SETUP_COUNTER, SETSTATE_CFGGPO,SETSTATE_DVCFGGPO, PRESENT_PASSWORD, WRITE_PASSWORD, CLEAR_PASSWORD, LOCK_SECTOR,UNDEFINED_ACTION
    }

    // Dialog member to request Nb Ndef Files configuration while formating
    static private Dialog _mNbPickerDialog;

    private actionType currentAction;

    static final String TAG_HEADER_FRAGMENT_PARAM = "tagHeaderFrag";

    public NDEFDiscoveryKitCtrlFragment _msettingsFragment;
    public NDEFDiscoveryKitCtrlMessage _msettingsNdefMessage;

    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuToolsFragment.
     */

    public static MenuToolsFragment newInstance(NFCTag mNFCTag) {
        MenuToolsFragment fragment = new MenuToolsFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuToolsFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuToolsFragment fragment = new MenuToolsFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);

        return fragment;
    }

    public MenuToolsFragment() {
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
        Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        _lockWritecheck = false;
        currentAction = actionType.UNDEFINED_ACTION;
        _msettingsNdefMessage = new NDEFDiscoveryKitCtrlMessage();
        // set default value
        _msettingsNdefMessage.defaultValue();

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);
        this.setRetainInstance(true);

    }

    private void enableButton(boolean bool) {

        Button button = (Button) _curFragmentView.findViewById(R.id.bEnableLockWAccess);
        button.setEnabled(bool);
        Switch switchB = (Switch) _curFragmentView.findViewById(R.id.tbEnableLockWAccess);
        switchB.setEnabled(bool);
        button = (Button) _curFragmentView.findViewById(R.id.bEnableLockRAccess);
        button.setEnabled(bool);
        switchB = (Switch) _curFragmentView.findViewById(R.id.tbEnableLockRAccess);
        switchB.setEnabled(bool);
        button = (Button) _curFragmentView.findViewById(R.id.bEnablestategpo);
        button.setEnabled(bool);
        switchB = (Switch) _curFragmentView.findViewById(R.id.tbEnablestategpo);
        switchB.setEnabled(bool);
        button = (Button) _curFragmentView.findViewById(R.id.BEraseNdef);
        button.setEnabled(bool);

        button = (Button) _curFragmentView.findViewById(R.id.Counter);
        button.setEnabled(bool);

    }

    private void showCounterSetup() {
        final Dialog nbPickerDialog = new Dialog((Activity) _mListener); // OnAttach
                                                                            // must
                                                                            // be
                                                                            // called
                                                                            // before
        nbPickerDialog.setTitle("SETUP CONTER");
        nbPickerDialog.setContentView(R.layout.counterdialog);
        Button buttonCancel = (Button) nbPickerDialog.findViewById(R.id.buttonCancel);
        Button buttonSelect = (Button) nbPickerDialog.findViewById(R.id.buttonSelect);
        RadioButton rb1a = (RadioButton) nbPickerDialog.findViewById(R.id.writeEnable);
        rb1a.setChecked(true);
        buttonSelect.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RadioButton rb1a;
                int counterValue = 0;
                MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.SETUP_COUNTER;
                if (((RadioButton) nbPickerDialog.findViewById(R.id.writeEnable)).isChecked() == true) {
                    counterValue = 0x01;
                } else if (((RadioButton) nbPickerDialog.findViewById(R.id.readEnable)).isChecked()) {
                    counterValue = 0x02;
                } else {
                    counterValue = 0x00;
                }
                nbPickerDialog.dismiss();
                Intent intent = new Intent(getActivity(), WaitForNFCTapActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                intent.putExtra("countersetup", counterValue);
                startActivityForResult(intent, TOOL_REQUEST_DONE);
            }
        });
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nbPickerDialog.dismiss(); // dismiss the dialog
            }
        });
        nbPickerDialog.show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreateView Fragment");

        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_tools, container, false);

        // Configure the LockWriteAccessToggle

        Button buttonWA = (Button) _curFragmentView.findViewById(R.id.bEnableLockWAccess);
        buttonWA.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PasswordDialogFragment newFragment = PasswordDialogFragment.newInstance(getString(R.string.pw_title),
                        getString(R.string.pw_writemsg), getString(R.string.pw_button_ok),
                        getString(R.string.pw_button_cancel));

                newFragment.setTargetFragment(MenuToolsFragment.this, DIALOG_FRAGMENT_WRITE);
                newFragment.show(getFragmentManager(), "dialog");

                // Request the password from user here
                if (_lockWritecheck) {
                    MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.LOCK_WRITE_ACCESS;
                } else {
                    MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.UNLOCK_WRITE_ACCESS;
                }
            }
        });

        // Switch toggleLWA = (Switch)
        // _curFragmentView.findViewById(R.id.ToolRelLayout).findViewById(R.id.LockWAccessRelLayout).findViewById(R.id.tbEnableLockWAccess);
        Switch toggleLWA = (Switch) _curFragmentView.findViewById(R.id.tbEnableLockWAccess);
        toggleLWA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Request the password from user here
                if (isChecked) {
                    _lockWritecheck = true;
                    // MenuToolsFragment.this.currentAction =
                    // MenuToolsFragment.actionType.LOCK_WRITE_ACCESS;
                } else {
                    _lockWritecheck = false;
                    // MenuToolsFragment.this.currentAction =
                    // MenuToolsFragment.actionType.UNLOCK_WRITE_ACCESS;
                }
            }
        });

        // Configure the LockReadAccessToggle
        Button buttonRA = (Button) _curFragmentView.findViewById(R.id.bEnableLockRAccess);
        buttonRA.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PasswordDialogFragment newFragment = (PasswordDialogFragment) PasswordDialogFragment.newInstance(
                        getString(R.string.pw_title), getString(R.string.pw_readmsg), getString(R.string.pw_button_ok),
                        getString(R.string.pw_button_cancel));
                newFragment.setTargetFragment(MenuToolsFragment.this, DIALOG_FRAGMENT_READ);
                newFragment.show(getFragmentManager(), "dialog");

                // Request the password from user here
                if (_lockReadcheck) {
                    MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.LOCK_READ_ACCESS;
                } else {
                    MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.UNLOCK_READ_ACCESS;
                }
            }
        });

        // Switch toggleLWA = (Switch)
        // _curFragmentView.findViewById(R.id.ToolRelLayout).findViewById(R.id.LockWAccessRelLayout).findViewById(R.id.tbEnableLockWAccess);
        Switch toggleLRA = (Switch) _curFragmentView.findViewById(R.id.tbEnableLockRAccess);
        toggleLRA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Request the password from user here
                if (isChecked) {
                    _lockReadcheck = true;
                    // MenuToolsFragment.this.currentAction =
                    // MenuToolsFragment.actionType.LOCK_WRITE_ACCESS;
                } else {
                    _lockReadcheck = false;
                    // MenuToolsFragment.this.currentAction =
                    // MenuToolsFragment.actionType.UNLOCK_WRITE_ACCESS;
                }

            }
        });

        // GPO Toogle Management
        // Configure the GPOToggle - Button + Switch

        Button buttonGPO = (Button) _curFragmentView.findViewById(R.id.bEnablestategpo);

        buttonGPO.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.SETSTATE_GPO;

                _GPOTogglecheck = ((Switch) _curFragmentView.findViewById(R.id.tbEnablestategpo)).isChecked();

                Intent intent = new Intent(getActivity(), WaitForNFCTapActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                intent.putExtra("_HZState", _GPOTogglecheck == true ? 1:0);
                startActivityForResult(intent, TOOL_REQUEST_DONE);
            }
        });
        // GPO Config Management - Only ST25TA
        // Configure the GPOToggle - Button + Switch

        Button buttonCFGGPO = (Button) _curFragmentView.findViewById(R.id.ConfGPOButton);

        buttonCFGGPO.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.SETSTATE_CFGGPO;
                int CFGGPO = 0;
                RadioButton rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_SO);
                if (rb.isChecked()){
                    CFGGPO = 1;
                }
                rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_WIP);
                if (rb.isChecked()){
                    CFGGPO = 2;
                }
                rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_MIP);
                if (rb.isChecked()){
                    CFGGPO = 3;
                }
                rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_IT);
                if (rb.isChecked()){
                    CFGGPO = 4;
                }
                rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_SC);
                if (rb.isChecked()){
                    CFGGPO = 5;
                }
                rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_RFB);
                if (rb.isChecked()){
                    CFGGPO = 6;
                }
                rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_FD);
                if (rb.isChecked()){
                    CFGGPO = 7;
                }

                Intent intent = new Intent(getActivity(), WaitForNFCTapActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                intent.putExtra("CFGGPO", CFGGPO);
                startActivityForResult(intent, TOOL_REQUEST_DONE);
            }
        });
        // Erase Management
        // Configure the Erase - Button

        Button buttonErase = (Button) _curFragmentView.findViewById(R.id.BEraseNdef);

        buttonErase.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                MenuToolsFragment.this.currentAction = MenuToolsFragment.actionType.ERASE_NDEF;

                Intent intent = new Intent(getActivity(), WaitForNFCTapActivity.class);
                intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                startActivityForResult(intent, TOOL_REQUEST_DONE);
            }
        });

        // Setup Dialog Counter Dialog

        Button buttonCounter = (Button) _curFragmentView.findViewById(R.id.Counter);
        buttonCounter.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                showCounterSetup();

            }
        });

        return _curFragmentView;

    }

    public void onBackPressed() {

        View tempView = _curFragmentView.findViewById(R.id.frSettings);
        tempView.setAlpha(0);
        tempView.setVisibility(View.GONE);
        // super.onBackPressed();
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

        // Fill in the layout with the currentTag
        onTagChanged(NFCApplication.getApplication().getCurrentTag());
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getName(), "onDestroyView Fragment");

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onTagChanged(NFCTag newTag) {
        // Use a Tag parser for UI purpose: parsing is done when creating
        // "NFCTag" object
        // This parser should:
        // - identify the tag manufacturer (STM or other)
        // -> if not STM, no logo, no menu for tag management
        // -> else (= STM tag), identify the product to determine the suitable
        // logo, tag name, and specific menu

        // Update instance attribute
        LinearLayout ghostLayout;
        RelativeLayout gLayout;

        // // Lock management layout - M24SR Model
        // ghostLayout = (LinearLayout)
        // _curFragmentView.findViewById(R.id.LockManagementSection);
        // ghostLayout.setVisibility(View.GONE);

        // Tool no visibility if not SRTAG or M24SR
        if ((newTag != null) && ((newTag.getModel().contains("SRTAG2KL")) || (newTag.getModel().contains("M24SR"))
                || (newTag.getModel().contains("ST25TA")))) {
            gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.ToolRelLayout);
            gLayout.setVisibility(View.VISIBLE);
        } else {
            gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.ToolRelLayout);
            gLayout.setVisibility(View.GONE);
        }

        // Manage the difference between SRTAG/M24SR - Counter function
        if ((newTag != null) && ((newTag.getModel().contains("SRTAG2KL")) || (newTag.getModel().contains("ST25TA")))) {
            ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.CounterLayout);
            ghostLayout.setVisibility(View.VISIBLE);
            ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.GPOConfButtonLayout);
            ghostLayout.setVisibility(View.VISIBLE);
            ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.lyGPOField);
            ghostLayout.setVisibility(View.VISIBLE);

        } else // M24SR Model
        {
            ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.CounterLayout);
            ghostLayout.setVisibility(View.GONE);
            ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.GPOConfButtonLayout);
            ghostLayout.setVisibility(View.GONE);
            ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.lyGPOField);
            ghostLayout.setVisibility(View.GONE);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        case DIALOG_FRAGMENT_WRITE:
            if (resultCode == RESULT_OK) {
                boolean check = data.getBooleanExtra("PasswordState", false);
                if (check) {
                    Log.d("DIALOG DEBUG", "Get Password is :" + data.getStringExtra("password"));
                    // Start New Fragment Activity to handle user Request.
                    Intent intent = new Intent(getActivity(), WaitForNFCTapActivity.class);
                    intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                    intent.putExtra("password", data.getStringExtra("password").toString());
                    startActivityForResult(intent, TOOL_REQUEST_DONE);
                } else {
                    Log.d("DIALOG DEBUG", "No New Password -  ");
                }
            }
            break;

        case DIALOG_FRAGMENT_READ:
            if (resultCode == RESULT_OK) {
                boolean check = data.getBooleanExtra("PasswordState", false);
                if (check) {
                    Log.d("DIALOG DEBUG", "Get Password is :" + data.getStringExtra("password"));

                    tempPasswordRead = data.getStringExtra("password");
                    if (tempPasswordRead != null) {
                        tempPasswordRead = data.getStringExtra("password").toString();
                    } else {
                        tempPasswordRead = "Wrong pwd";
                    }
                    // Start New Fragment Activity to handle user Request.
                    // Now request Modification password
                    PasswordDialogFragment newFragment = (PasswordDialogFragment) PasswordDialogFragment.newInstance(
                            getString(R.string.pw_title), getString(R.string.pw_rightmsg),
                            getString(R.string.pw_button_ok), getString(R.string.pw_button_cancel));
                    newFragment.setTargetFragment(MenuToolsFragment.this, DIALOG_FRAGMENT_RIGHT);
                    newFragment.show(getFragmentManager(), "dialog");
                } else {
                    Log.d("DIALOG DEBUG", "No New Password -  ");
                }
            }
            break;

        case DIALOG_FRAGMENT_RIGHT: // Use to retrieve modification rights
            if (resultCode == RESULT_OK) {
                boolean check = data.getBooleanExtra("PasswordState", false);
                if (check) {
                    Log.d("DIALOG DEBUG", "Get modification rights :" + data.getStringExtra("password"));
                    // Start New Fragment Activity to handle user Request.
                    // tempPasswordRead =
                    // data.getStringExtra("password").toString();

                    Intent intent = new Intent(getActivity(), WaitForNFCTapActivity.class);
                    intent.putExtra(WAIT_FOR_TAP_ACTION, currentAction);
                    //
                    intent.putExtra("password", tempPasswordRead);
                    intent.putExtra("rightAccess", data.getStringExtra("password").toString());
                    startActivityForResult(intent, TOOL_REQUEST_DONE);
                } else {
                    Log.d("DIALOG DEBUG", "No New Password -  ");
                    tempPasswordRead = "";
                }
            }
            break;

        case TOOL_REQUEST_DONE: {
            Log.d("DIALOG DEBUG", "Tool request done !");
            // NFCApplication.getApplication().getCurrentTag().decodeTagType4A();
//            _mListener.OnlockNdefMessage(); // request update

            break;
        }
        case TOOL_EXCHANGE_DATA_DONE: {
            Log.d("DIALOG DEBUG", "Tool-Exchange request done !");
            break;
        }
        }
    }

}
