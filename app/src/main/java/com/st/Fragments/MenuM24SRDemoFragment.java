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
import com.st.demo.PingPongSet;
import com.st.demo.WaitForNFCTapActivity;
import com.st.demo.WaitForNFCTapM24SRDemo;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NDEFUI.NDEFDiscoveryKitCtrlFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.util.PasswordDialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;



public class MenuM24SRDemoFragment extends NFCPagerFragment implements NumberPicker.OnValueChangeListener{


    private View _curFragmentView = null;      // Store view corresponding to current fragment

    public static final int RESULT_OK = 101;
    public static final int TOOL_REQUEST_DONE = 102;
    public static final int TOOL_EXCHANGE_DATA_DONE = 103;

    // Current Activity Handling the fragment
    NdefViewFragmentListener _mListener;

    public Button buttonSettingsDemoDone;

    public static final String WAIT_FOR_TAP_ACTION="WAIT_FOR_TAP_ACTION";

    public enum actionType{
        FORMAT_NDEF,
        SETUP_COUNTER,
        DRIVE_IT,
        TOOL_EXCHANGE_DATA_DONE,
        WRITE_NDEF_FILE,
        START_EXCHANGE,
        START_SETTINGS_DEMO,
        PINGPONGDEMO,
        LOGSDEMO,
        UNDEFINED_ACTION
    }


    PingPongSet _mpingpongset = new PingPongSet();

    // Dialog member to request Nb Ndef Files configuration while formating
    static private Dialog _mNbPickerDialog;

    private actionType currentAction;

    static final String TAG_HEADER_FRAGMENT_PARAM = "tagHeaderFrag";


    public NDEFDiscoveryKitCtrlFragment _msettingsFragment;
    public NDEFDiscoveryKitCtrlMessage  _msettingsNdefMessage;


    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuToolsFragment.
     */

    public static MenuM24SRDemoFragment newInstance(NFCTag mNFCTag) {
        MenuM24SRDemoFragment fragment = new MenuM24SRDemoFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }
    public static MenuM24SRDemoFragment newInstance(NFCTag mNFCTag,int page, String title) {
        MenuM24SRDemoFragment fragment = new MenuM24SRDemoFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuM24SRDemoFragment() {
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
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);

        currentAction = actionType.UNDEFINED_ACTION;
        _msettingsNdefMessage = new NDEFDiscoveryKitCtrlMessage();
        // set default value
        _msettingsNdefMessage.defaultValue();

    }

    private void enableButton(boolean bool)
    {

        Button button = (Button)_curFragmentView.findViewById(R.id.BFormatNdef);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.bEnableIT);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BStartExchange);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BStartSetSettingsDemo);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BSetSettings);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BStartDataLogDemo);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BStartPingPongDemo);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BSettingsPingPongDemo);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BSettingsDone);
        button.setEnabled(bool);
        button = (Button)_curFragmentView.findViewById(R.id.BSettingsDone);

    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("Nb NDEF Files requested while formating: ",""+newVal);

    }
    
    private void showNbPickerNDEFFile()
    {
        final Dialog nbPickerDialog = new Dialog((Activity) _mListener); // OnAttach must be called before
        nbPickerDialog.setTitle("Select NDEF File Number");
        nbPickerDialog.setContentView(R.layout.pickerdialog);
        Button buttonCancel = (Button) nbPickerDialog.findViewById(R.id.buttonCancel);
        Button buttonSelect = (Button) nbPickerDialog.findViewById(R.id.buttonSelect);
        final NumberPicker ndefFileNbPicker = (NumberPicker) nbPickerDialog.findViewById(R.id.numberPicker);
        ndefFileNbPicker.setMaxValue(8);   // max value 8
        ndefFileNbPicker.setMinValue(1);   // min value 1
        ndefFileNbPicker.setWrapSelectorWheel(false);
        ndefFileNbPicker.setOnValueChangedListener(this);
        buttonSelect.setOnClickListener(new OnClickListener()
        {
         public void onClick(View v) {
             MenuM24SRDemoFragment.this.currentAction = MenuM24SRDemoFragment.actionType.FORMAT_NDEF;
            int nbNdefFiles = ndefFileNbPicker.getValue();
            nbPickerDialog.dismiss();
             Intent intent = new Intent(getActivity(), WaitForNFCTapM24SRDemo.class);
            intent.putExtra(WAIT_FOR_TAP_ACTION,currentAction);
            intent.putExtra("NbFiles",nbNdefFiles);
            startActivityForResult(intent, TOOL_REQUEST_DONE);
          }    
         });
        buttonCancel.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
             nbPickerDialog.dismiss(); // dismiss the dialog
          }    
         });
        nbPickerDialog.show();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
     Log.v(this.getClass().getName(), "OnCreateView Fragment");

    // Inflate the layout for this fragment
    _curFragmentView = inflater.inflate(R.layout.fragment_menu_m24srdemo, container,false);


    // Format Management
    // Configure the Erase - Button

    // Setup Dialog NBPicker Dialog

    Button buttonFormat = (Button) _curFragmentView.findViewById(R.id.BFormatNdef);
    buttonFormat.setOnClickListener(new Button.OnClickListener() {
    public void onClick(View v)
    {

        showNbPickerNDEFFile();

    }
    });




    // "Send IT" Management - Beware - Code as example as not implemented on M24SR Demo

    Button buttonIT = (Button) _curFragmentView.findViewById(R.id.bEnableIT);

    buttonIT.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            MenuM24SRDemoFragment.this.currentAction = MenuM24SRDemoFragment.actionType.DRIVE_IT;
            Intent intent = new Intent(getActivity(), WaitForNFCTapM24SRDemo.class);
            intent.putExtra(WAIT_FOR_TAP_ACTION,currentAction);
            startActivityForResult(intent, TOOL_REQUEST_DONE);
        }
      });


    // "Exchange data" Management - FW upload purpose

    Button buttonExchange = (Button) _curFragmentView.findViewById(R.id.BStartExchange);

    buttonExchange.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            MenuM24SRDemoFragment.this.currentAction = MenuM24SRDemoFragment.actionType.START_EXCHANGE;
            Intent intent = new Intent(getActivity(), WaitForNFCTapM24SRDemo.class);
            intent.putExtra(WAIT_FOR_TAP_ACTION,currentAction);
            startActivityForResult(intent, TOOL_EXCHANGE_DATA_DONE);
        }
      });

    // Start Configuration M24SR Discovery kit demo
    // M24SR Discovery kit requests configuration message
    // App sends back the settings.

    Button buttonSettingsDemo = (Button) _curFragmentView.findViewById(R.id.BStartSetSettingsDemo);

    buttonSettingsDemo.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            MenuM24SRDemoFragment.this.currentAction = MenuM24SRDemoFragment.actionType.START_SETTINGS_DEMO;
            Intent intent = new Intent(getActivity(), WaitForNFCTapM24SRDemo.class);
            intent.putExtra(WAIT_FOR_TAP_ACTION,currentAction);
            intent.putExtra("serializePayload",_msettingsNdefMessage.serializeNDEFMessage());
            
            // need to provide here the settings - update could be done by Settings Button
            startActivityForResult(intent, TOOL_EXCHANGE_DATA_DONE);
        }
      });


    buttonSettingsDemoDone = (Button) _curFragmentView.findViewById(R.id.BSettingsDone);
    buttonSettingsDemoDone.setVisibility(View.GONE);

    buttonSettingsDemoDone.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            // store data from Fragment DiscoveryKitCtrl

            View rlLayout = _curFragmentView.findViewById(R.id.settingsLayout);
            rlLayout.setVisibility(View.GONE);

            // hide Fragment DiscoveryKitCtrl
            NDEFDiscoveryKitCtrlFragment tmpFragment = (NDEFDiscoveryKitCtrlFragment) getFragmentManager().findFragmentByTag("settingsFragment");
            // String tmpFragmentTag = "settingsFragment";



            if (tmpFragment != null) {
                _msettingsNdefMessage = (NDEFDiscoveryKitCtrlMessage) tmpFragment.getNDEFSimplifiedMessage();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(tmpFragment);
                transaction.commitAllowingStateLoss();
                }
            getFragmentManager().executePendingTransactions();
            View tempView = _curFragmentView.findViewById(R.id.frSettings);
            tempView.setAlpha(0);
            buttonSettingsDemoDone.setVisibility(View.GONE);

            tmpFragment.onMessageChanged(_msettingsNdefMessage);
        }
      });


    Button buttonStartDataLogDemo = (Button) _curFragmentView.findViewById(R.id.BStartDataLogDemo);
    buttonStartDataLogDemo.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            MenuM24SRDemoFragment.this.currentAction = MenuM24SRDemoFragment.actionType.LOGSDEMO;
            Intent intent = new Intent(getActivity(), WaitForNFCTapM24SRDemo.class);
            intent.putExtra(WAIT_FOR_TAP_ACTION,currentAction);
            // need to provide here the settings - update could be done by Settings Button
            startActivityForResult(intent, TOOL_EXCHANGE_DATA_DONE);
        }
      });


    Button buttonUpdateSettingForDemo = (Button) _curFragmentView.findViewById(R.id.BSetSettings);

    buttonUpdateSettingForDemo.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            // create editable Fragment
            boolean isNewFragment = false;
            _msettingsFragment = NDEFDiscoveryKitCtrlFragment.newInstance(_msettingsNdefMessage,true);

            // - Text simplified NDEF message
            // Check if there's an existing Text fragment in current activity/fragment

            NDEFDiscoveryKitCtrlFragment tmpFragment = (NDEFDiscoveryKitCtrlFragment) getFragmentManager().findFragmentByTag("settingsFragment");
            String tmpFragmentTag = "settingsFragment";

            if (tmpFragment == null) {
                    tmpFragment = NDEFDiscoveryKitCtrlFragment.newInstance(_msettingsNdefMessage,false);
                    isNewFragment = true;
                }

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // If this is a new fragment, add it to the fragment manager; otherwise, just show it
                if (isNewFragment) {
                    //tmpFragment.getView().setBackgroundResource(R.drawable.bckgnd);

                    transaction.add(R.id.frSettings, tmpFragment, tmpFragmentTag);
                    isNewFragment = false;
                }

                //transaction.addToBackStack(null);
                transaction.show(tmpFragment);

                transaction.commitAllowingStateLoss();
                View tempView = _curFragmentView.findViewById(R.id.frSettings);
                //tempView.setBackgroundColor(Color.WHITE);
                tempView.setAlpha(1);

                buttonSettingsDemoDone.setVisibility(View.VISIBLE);

                View rlLayout = _curFragmentView.findViewById(R.id.settingsLayout);
                rlLayout.setVisibility(View.VISIBLE);
                getFragmentManager().executePendingTransactions();
                tmpFragment.onMessageChanged(_msettingsNdefMessage);
        }
      });


    // PingPong Demo Button Setup
    // Setup Settings


    // defautl value
    final TextView etstartValue = (TextView) _curFragmentView.findViewById(R.id.EtsettingsPingPongValue);
    _mpingpongset.start = Integer.parseInt(etstartValue.getText().toString());
    final TextView etoffsetValue = (TextView) _curFragmentView.findViewById(R.id.EtOffsetPingPongValue);
    _mpingpongset.offset = Integer.parseInt(etoffsetValue.getText().toString());
    final TextView tvLoopValue = (TextView) _curFragmentView.findViewById(R.id.EtLoopPingPongValue);
    _mpingpongset.loop = Integer.parseInt(tvLoopValue.getText().toString());


    SeekBar  valueSB = (SeekBar) _curFragmentView.findViewById(R.id.startseekbar1);
    // Set Default value
    valueSB.setProgress(_mpingpongset.start);
    valueSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
       
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
    
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            // TODO Auto-generated method stub
            etstartValue.setText(String.valueOf(progress));

        }
    });
    SeekBar  offsetSB = (SeekBar) _curFragmentView.findViewById(R.id.offsetseekbar1);
    // Set Default value
    offsetSB.setProgress(_mpingpongset.offset);
    offsetSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
       
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
    
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            // TODO Auto-generated method stub
            etoffsetValue.setText(String.valueOf(progress));

        }
    });

    SeekBar  loopSB = (SeekBar) _curFragmentView.findViewById(R.id.loopseekbar1);
    // Set Default value
    loopSB.setProgress(_mpingpongset.loop);
    loopSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
       
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
    
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            // TODO Auto-generated method stub
            tvLoopValue.setText(String.valueOf(progress));

        }
    });

    Button buttonLaunchPingPongDemo = (Button) _curFragmentView.findViewById(R.id.BStartPingPongDemo);

    buttonLaunchPingPongDemo.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            MenuM24SRDemoFragment.this.currentAction = MenuM24SRDemoFragment.actionType.PINGPONGDEMO;
            Intent intent = new Intent(getActivity(), WaitForNFCTapM24SRDemo.class);
            intent.putExtra(WAIT_FOR_TAP_ACTION,currentAction);
            intent.putExtra("loopValue",_mpingpongset.loop);
            intent.putExtra("startValue",_mpingpongset.start);
            intent.putExtra("offsetValue",_mpingpongset.offset);
            // need to provide here the settings - update could be done by Settings Button
            startActivityForResult(intent, TOOL_EXCHANGE_DATA_DONE);
        }
      });

    Button buttonUpdateSettingForPingPongDemo = (Button) _curFragmentView.findViewById(R.id.BSettingsPingPongDemo);

    buttonUpdateSettingForPingPongDemo.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            // create editable Fragment
            // Retrieve Relative layout ID for PingPongSetting Frame
            RelativeLayout PingPongSettingsLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.settingsPingPongLayout);
            PingPongSettingsLayout.setVisibility(View.VISIBLE);
            // need to invalidate other buttons behind the layout
            enableButton(false);

        }
      });

    // Setup PingPong Demo settings
    Button buttonSettingDone = (Button) _curFragmentView.findViewById(R.id.BPingPongSettingsDone);
    buttonSettingDone.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View v)
        {
            // create Editable Fragment
            // Retrieve Relative layout ID for PingPongSetting Frame to hide it
            RelativeLayout PingPongSettingsLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.settingsPingPongLayout);
            TextView et = (TextView) _curFragmentView.findViewById(R.id.EtsettingsPingPongValue);
            _mpingpongset.start =  Integer.parseInt(et.getText().toString());
            et = (TextView) _curFragmentView.findViewById(R.id.EtOffsetPingPongValue);
            _mpingpongset.offset = Integer.parseInt(et.getText().toString());

             et = (TextView) _curFragmentView.findViewById(R.id.EtLoopPingPongValue);
            _mpingpongset.loop = Integer.parseInt(et.getText().toString());
             PingPongSettingsLayout.setVisibility(View.GONE);
            /*
             InputMethodManager inputManager = (InputMethodManager)
                                               NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

             inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                                  InputMethodManager.HIDE_NOT_ALWAYS);
                                                  */
            enableButton(true);
        }
      });


    return _curFragmentView;

    }

    public void onBackPressed() {

        View tempView = _curFragmentView.findViewById(R.id.frSettings);
        tempView.setAlpha(0);
        tempView.setVisibility(View.GONE);
        //super.onBackPressed();
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
        onTagChanged (NFCApplication.getApplication().getCurrentTag());
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

 public void onTagChanged (NFCTag newTag) {
    // Use a Tag parser for UI purpose: parsing is done when creating "NFCTag" object
    // This parser should:
    // - identify the tag manufacturer (STM or other)
    //    -> if not STM, no logo, no menu for tag management
    //    -> else (= STM tag), identify the product to determine the suitable logo, tag name, and specific menu
     
     // Update instance attribute
     LinearLayout ghostLayout;

     // // Lock management layout - M24SR Model
     //ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.LockManagementSection);
     //ghostLayout.setVisibility(View.GONE);

     // Set footnote
        TextView tmFootNoteTxt = (TextView) _curFragmentView.findViewById(R.id.FootNoteTxtId);
        tmFootNoteTxt.setText(newTag.getFootNote());



 }

 public void onActivityResult(int requestCode, int resultCode, Intent data) {
     switch(requestCode) {            
         case TOOL_REQUEST_DONE:
         {
             Log.d("DIALOG DEBUG","Tool request done !");
             // NFCApplication.getApplication().getCurrentTag().decodeTagType4A();
             _mListener.OnlockNdefMessage(); // request update

             break;
         }
         case TOOL_EXCHANGE_DATA_DONE:
         {
             Log.d("DIALOG DEBUG","Tool-Exchange request done !");
             break;
         }
     }
 }
 
}

