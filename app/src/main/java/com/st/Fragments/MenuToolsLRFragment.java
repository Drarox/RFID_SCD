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

package com.st.Fragments;


import java.util.Hashtable;

import com.st.Fragments.MenuSmartNdefViewFragment.NdefViewFragmentListener;
import com.st.NFC.EnergyHarvesting;
import com.st.NFC.STNfcTagHandler;
import com.st.NFC.STNfcTagVHandler;
import com.st.demo.R;
import com.st.demo.WaitForNFCTapActivity;
import com.st.NDEF.NDEFDiscoveryKitCtrlMessage;
import com.st.NDEFUI.NDEFDiscoveryKitCtrlFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.nfcv.DVEnergyHarvesting;
import com.st.nfcv.NFCCommandVExtended;
import com.st.nfcv.SysFileLRHandler;
import com.st.util.GenErrorAppReport;
import com.st.util.PasswordDialogFragment;


import com.st.nfcv.Helper;
import com.st.nfcv.stnfcm24LRHarvesting;
import com.st.nfcv.stnfcm24LRProtectionLockMgt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MenuToolsLRFragment extends NFCPagerFragment {
    private String TAG = "MenuToolsLRFragment";

    private View _curFragmentView = null; // Store view corresponding to current
    // fragment

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
        PRESENT_PASSWORD, WRITE_PASSWORD, CLEAR_PASSWORD, LOCK_SECTOR, SETSTATE_DVGPO, SETSTATE_DVCFGGPO, UNDEFINED_ACTION
    }

    private actionType currentAction;

    static final String TAG_HEADER_FRAGMENT_PARAM = "tagHeaderFrag";

    public NDEFDiscoveryKitCtrlFragment _msettingsFragment;
    public NDEFDiscoveryKitCtrlMessage _msettingsNdefMessage;


    private EditText value1;
    private EditText value2;
    private EditText value3;
    private EditText value4;
    private EditText PW2value1;
    private EditText PW2value2;
    private EditText PW2value3;
    private EditText PW2value4;

    public static enum PasswordByteList {
        PWD_BYTE_1,
        PWD_BYTE_2,
        PWD_BYTE_3,
        PWD_BYTE_4,
        PWD_BYTE_5,
        PWD_BYTE_6,
        PWD_BYTE_7,
        PWD_BYTE_8
    }

    private final Hashtable<PasswordByteList, EditText> m_pwd_FctDescr = new Hashtable<PasswordByteList, EditText>();
    private byte nbpwdvalues = 4;
    private byte current_pwd_FctDescr;


    private boolean Value1Enable = true;
    private boolean Value2Enable = true;
    private boolean Value3Enable = true;
    private boolean Value4Enable = true;
    private boolean PW2Value1Enable = true;
    private boolean PW2Value2Enable = true;
    private boolean PW2Value3Enable = true;
    private boolean PW2Value4Enable = true;

    private Button buttonPresentPassword;
    private Button buttonWritePassword;
    private Button buttonClear;

    private RadioButton rbOptionCfgPwd1;
    private RadioButton rbOptionPwd1;
    private RadioButton rbOptionPwd2;
    private RadioButton rbOptionPwd3;

    private byte PasswordNumber = (byte) 0x01;
    private byte[] PasswordData = new byte[4];

    private EditText m_sectorvalue;

    private Button buttonLockSector;
    private RadioButton rbOptionLockConfig00;
    private RadioButton rbOptionLockConfig01;
    private RadioButton rbOptionLockConfig10;
    private RadioButton rbOptionLockConfig11;

    private byte LockConfig = (byte) 0x00;
    private byte[] SectorNumberAddress = null;
    private byte LockSectorByte = (byte) 0x00;
    int valueBlock1bis = 0;


    private stnfcm24LRProtectionLockMgt m24LRProtectionLockMgt;
    //private stnfcm24LRHarvesting mEnergyHarvesting;
    private EnergyHarvesting mEnergyHarvesting;

    private EditText valueEHconfigByte;

    private Button buttonReadEHconfig;
    private Button buttonWriteEHconfig;
    private Button buttonWriteD0config;

    private EditText valueEHenableByte;

    private Button buttonCheckEHenable;
    private Button buttonResetEHenable;
    private Button buttonSetEHenable;

    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag NFC Tag to consider
     * @return A new instance of fragment MenuToolsFragment.
     */

    public static MenuToolsLRFragment newInstance(NFCTag mNFCTag) {
        MenuToolsLRFragment fragment = new MenuToolsLRFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuToolsLRFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuToolsLRFragment fragment = new MenuToolsLRFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);

        return fragment;
    }

    public MenuToolsLRFragment() {
        // Required empty public constructor
        m24LRProtectionLockMgt = new stnfcm24LRProtectionLockMgt();
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
        currentAction = actionType.UNDEFINED_ACTION;
        _msettingsNdefMessage = new NDEFDiscoveryKitCtrlMessage();
        // set default value
        _msettingsNdefMessage.defaultValue();

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);
        this.setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreateView Fragment");

        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_lr_tools, container, false);

        // initialise PWD
        value1 = (EditText) _curFragmentView.findViewById(R.id.etvalue1);
        value1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                current_pwd_FctDescr = (byte) 0x01;
            }
        });
        value2 = (EditText) _curFragmentView.findViewById(R.id.etvalue2);
        value2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                current_pwd_FctDescr = (byte) 0x02;
            }
        });

        value3 = (EditText) _curFragmentView.findViewById(R.id.etvalue3);
        value3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                current_pwd_FctDescr = (byte) 0x03;
            }
        });

        value4 = (EditText) _curFragmentView.findViewById(R.id.etvalue4);
        value4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                current_pwd_FctDescr = (byte) 0x04;
            }
        });

        m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_1, value1);
        m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_2, value2);
        m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_3, value3);
        m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_4, value4);

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag.getModel().contains("DV")) {
            mEnergyHarvesting = new DVEnergyHarvesting();
            // Additional PWD values - 64 bits = 2 X LR one
            // initialise PWD
            this.nbpwdvalues = 8;
            PW2value1 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue1);
            PW2value1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    current_pwd_FctDescr = (byte) 0x05;
                }
            });

            PW2value2 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue2);
            PW2value2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    current_pwd_FctDescr = (byte) 0x06;
                }
            });

            PW2value3 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue3);
            PW2value3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    current_pwd_FctDescr = (byte) 0x07;
                }
            });

            PW2value4 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue4);
            PW2value4.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    current_pwd_FctDescr = (byte) 0x08;
                }
            });

            m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_5, PW2value1);
            m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_6, PW2value2);
            m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_7, PW2value3);
            m_pwd_FctDescr.put(PasswordByteList.PWD_BYTE_8, PW2value4);

            // show registers

        } else {
            // hide Register
            mEnergyHarvesting = new stnfcm24LRHarvesting();


        }


        initListenerPWD();
        initListenerPWDNumbers();
        initListenerZoneSector();
        initListenerEH();
        initListenerGPO();

        return _curFragmentView;

    }

    public void onBackPressed() {

/*        View tempView = _curFragmentView.findViewById(R.id.frSettings);
        tempView.setAlpha(0);
        tempView.setVisibility(View.GONE);*/
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
        if ((newTag != null) && ((newTag.getModel().contains("M24LR")) || (newTag.getModel().contains("LRI"))
                || (newTag.getModel().contains("DV")))) {
            gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.ToolRelLayout);
            gLayout.setVisibility(View.VISIBLE);

            // LR and LRI need to be checked differently of DV
            if (newTag.getModel().contains("DV")) {
                gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.RLM24SRSpecificSectionEH);
                //gLayout.setVisibility(View.GONE);
                gLayout.setVisibility(View.VISIBLE);
                // for the 64bits PWD
                ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.lyWritePW2);
                ghostLayout.setVisibility(View.VISIBLE);
                rbOptionCfgPwd1.setVisibility(View.VISIBLE);
                this.nbpwdvalues = 8;

            } else {
                gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.RLM24SRSpecificSectionEH);
                gLayout.setVisibility(View.VISIBLE);
                // for the 32bits PWD - reduce the 64 bits PWD used for DV - LR = 32bits
                this.nbpwdvalues = 4;
                ghostLayout = (LinearLayout) _curFragmentView.findViewById(R.id.lyWritePW2);
                ghostLayout.setVisibility(View.GONE);
                rbOptionCfgPwd1.setVisibility(View.GONE);
                this.nbpwdvalues = 4;

            }


        } else {
            gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.ToolRelLayout);
            gLayout.setVisibility(View.GONE);
        }

        TextView tmFootNoteTxt = (TextView) _curFragmentView.findViewById(R.id.FootNoteTxtId);
        tmFootNoteTxt.setText(newTag.getFootNote());


    }


    private void initListenerZoneSector() {
        // by default, M24LR resources enable
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag.getModel().contains("DV")) {
            TextView sectorLabel = (TextView) _curFragmentView.findViewById(R.id.textViewConfigValue);
            sectorLabel.setVisibility(View.GONE);
            m_sectorvalue = (EditText) _curFragmentView.findViewById(R.id.etvalueSector);
            m_sectorvalue.setVisibility(View.GONE);
            buttonLockSector = (Button) _curFragmentView.findViewById(R.id.button_LockSector);
            buttonLockSector.setVisibility(View.GONE);

            sectorLabel = (TextView) _curFragmentView.findViewById(R.id.textViewConfigZoneValue);
            sectorLabel.setText("Area: ");
            sectorLabel.setVisibility(View.VISIBLE);
            m_sectorvalue = (EditText) _curFragmentView.findViewById(R.id.etvalueZone);
            m_sectorvalue.setVisibility(View.VISIBLE);
            buttonLockSector = (Button) _curFragmentView.findViewById(R.id.button_LockZone);
            buttonLockSector.setVisibility(View.VISIBLE);
            buttonLockSector.setText(" Lock Area ");
            buttonLockSector.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO Auto-generated method stub
                    getLockZone();
                    int ret = m24LRProtectionLockMgt.updateProtectionLockZone( valueBlock1bis, LockConfig, PasswordNumber);
                    //Used for DEBUG : Log.i("Write", "SUCCESS");
                    if (ret == 0) {
                        Toast.makeText(NFCApplication.getContext(), "Successfull setting", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NFCApplication.getContext(), "setting error ... " , Toast.LENGTH_LONG).show();
                    }
                }
            });


        } else {
            m_sectorvalue = (EditText) _curFragmentView.findViewById(R.id.etvalueSector);
            m_sectorvalue.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

            buttonLockSector = (Button) _curFragmentView.findViewById(R.id.button_LockSector);
            buttonLockSector.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO Auto-generated method stub
                    getLockSector();
                    int ret = m24LRProtectionLockMgt.updateProtectionLockSector(null, valueBlock1bis, LockConfig, PasswordNumber);
                    if (ret == 0) {
                        Toast.makeText(NFCApplication.getContext(), "Successfull setting", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NFCApplication.getContext(), "setting error ... " , Toast.LENGTH_LONG).show();
                    }
                    //Used for DEBUG : Log.i("Write", "SUCCESS");
                }
            });

        }
        rbOptionLockConfig00 = (RadioButton) _curFragmentView.findViewById(R.id.LockConfig00);
        rbOptionLockConfig01 = (RadioButton) _curFragmentView.findViewById(R.id.LockConfig01);
        rbOptionLockConfig10 = (RadioButton) _curFragmentView.findViewById(R.id.LockConfig10);
        rbOptionLockConfig11 = (RadioButton) _curFragmentView.findViewById(R.id.LockConfig11);
        rbOptionLockConfig00.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LockConfig = (byte) 0x00;
            }
        });

        rbOptionLockConfig01.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LockConfig = (byte) 0x01;
            }
        });

        rbOptionLockConfig10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LockConfig = (byte) 0x02;
            }
        });

        rbOptionLockConfig11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LockConfig = (byte) 0x03;
            }
        });



    }

    private boolean checkpwdUIValidity() {
        boolean ret = false;

        if (this.nbpwdvalues == 4) {
            if (Helper.checkDataHexa(value1.getText().toString()) == true &&
                    Helper.checkDataHexa(value2.getText().toString()) == true &&
                    Helper.checkDataHexa(value3.getText().toString()) == true &&
                    Helper.checkDataHexa(value4.getText().toString()) == true) {

                ret = true;
            }

        } else if (this.nbpwdvalues == 8) {
            if (Helper.checkDataHexa(value1.getText().toString()) == true &&
                    Helper.checkDataHexa(value2.getText().toString()) == true &&
                    Helper.checkDataHexa(value3.getText().toString()) == true &&
                    Helper.checkDataHexa(value4.getText().toString()) == true &&
                    Helper.checkDataHexa(PW2value1.getText().toString()) == true &&
                    Helper.checkDataHexa(PW2value2.getText().toString()) == true &&
                    Helper.checkDataHexa(PW2value3.getText().toString()) == true &&
                    Helper.checkDataHexa(PW2value4.getText().toString()) == true) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            ret = false;
        }
        return ret;
    }

    private boolean checkPwdvalueEnableState() {
        boolean ret = false;
        if (this.nbpwdvalues == 4) {
            if (Value1Enable == true &&
                    Value2Enable == true &&
                    Value3Enable == true &&
                    Value4Enable == true) {
                ret = true;
            }
        } else if (this.nbpwdvalues == 8) {
            if (Value1Enable == true &&
                    Value2Enable == true &&
                    Value3Enable == true &&
                    Value4Enable == true &&
                    PW2Value1Enable == true &&
                    PW2Value2Enable == true &&
                    PW2Value3Enable == true &&
                    PW2Value4Enable == true) {
                ret = true;
            }
        } else {
            ret = false;

        }

        return ret;
    }


    private void initListenerPWDNumbers() {
        rbOptionCfgPwd1 = (RadioButton) _curFragmentView.findViewById(R.id.cfgpwd1);
        rbOptionPwd1 = (RadioButton) _curFragmentView.findViewById(R.id.pwd1);
        rbOptionPwd2 = (RadioButton) _curFragmentView.findViewById(R.id.pwd2);
        rbOptionPwd3 = (RadioButton) _curFragmentView.findViewById(R.id.pwd3);

        buttonPresentPassword = (Button) _curFragmentView.findViewById(R.id.button_presentpassword);
        buttonWritePassword = (Button) _curFragmentView.findViewById(R.id.button_writepassword);
        buttonClear = (Button) _curFragmentView.findViewById(R.id.button_clear);

        rbOptionCfgPwd1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PasswordNumber = (byte) 0x00;
            }
        });

        rbOptionPwd1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PasswordNumber = (byte) 0x01;
            }
        });

        rbOptionPwd2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PasswordNumber = (byte) 0x02;
            }
        });

        rbOptionPwd3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PasswordNumber = (byte) 0x03;
            }
        });

        buttonPresentPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                if (checkpwdUIValidity()) {
//                if (Helper.checkDataHexa(value1.getText().toString()) == true &&
//                    Helper.checkDataHexa(value2.getText().toString()) == true &&
//                    Helper.checkDataHexa(value3.getText().toString()) == true &&
//                    Helper.checkDataHexa(value4.getText().toString()) == true) {
                    //new StartPresentPasswordTask().execute();
                    preExecutePresentPwd();
                    m24LRProtectionLockMgt.presentPassword(null, PasswordData, PasswordNumber);

                } else {
                    Toast.makeText(NFCApplication.getContext(), "Invalid password data, please modify", Toast.LENGTH_LONG).show();
                }
                //Used for DEBUG : Log.i("Write", "SUCCESS");
            }
        });

        buttonWritePassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                if (checkpwdUIValidity()) {
//                    if (Helper.checkDataHexa(value1.getText().toString()) == true &&
//                    Helper.checkDataHexa(value2.getText().toString()) == true &&
//                    Helper.checkDataHexa(value3.getText().toString()) == true &&
//                    Helper.checkDataHexa(value4.getText().toString()) == true) {
                    //new StartWritePasswordTask().execute();
                    preExecuteWritePwd();
                    int ret = m24LRProtectionLockMgt.writePassword(null, PasswordData, PasswordNumber);
                    if (ret == 0) {
                        Toast.makeText(NFCApplication.getContext(), "Successfull setting", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NFCApplication.getContext(), "setting error ... " , Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(NFCApplication.getContext(), "Invalid password data, please modify", Toast.LENGTH_LONG).show();
                }
                //Used for DEBUG : Log.i("Write", "SUCCESS");
            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                clearpwdUIValues();
                buttonPresentPassword.setClickable(true);
                buttonWritePassword.setClickable(true);

            }
        });
    }

    private void clearpwdUIValues() {
        if (this.nbpwdvalues == 4) {
            clearpwdUIValues32();
        } else if (this.nbpwdvalues == 8) {
            clearpwdUIValues32();
            clearpwdUIValues32bis();
        }
    }


    private void clearpwdUIValues32() {
        value1.setText("");
        value2.setText("");
        value3.setText("");
        value4.setText("");
        Value1Enable = true;
        Value2Enable = true;
        Value3Enable = true;
        Value4Enable = true;

    }

    private void clearpwdUIValues32bis() {
        PW2value1.setText("");
        PW2value2.setText("");
        PW2value3.setText("");
        PW2value4.setText("");
        PW2Value1Enable = true;
        PW2Value2Enable = true;
        PW2Value3Enable = true;
        PW2Value4Enable = true;

    }

    private void initListenerPWD() {
        value1 = (EditText) _curFragmentView.findViewById(R.id.etvalue1);
        value1.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value1.getSelectionStart();
                int aend = value1.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    value1.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value1.setSelection(astart - 1, aend - 1);
                } else
                    value1.setSelection(astart, aend);

                if (value1.getText().length() > 0 && value1.getText().length() < 2) {
                    value1.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    Value1Enable = false;
                } else {
                    value1.setTextColor(0xff000000); //BLACK color
                    Value1Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        value2 = (EditText) _curFragmentView.findViewById(R.id.etvalue2);
        value2.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value2.getSelectionStart();
                int aend = value2.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    value2.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value2.setSelection(astart - 1, aend - 1);
                } else
                    value2.setSelection(astart, aend);

                if (value2.getText().length() > 0 && value2.getText().length() < 2) {
                    value2.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    Value2Enable = false;
                } else {
                    value2.setTextColor(0xff000000); //BLACK color
                    Value2Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        value3 = (EditText) _curFragmentView.findViewById(R.id.etvalue3);
        value3.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value3.getSelectionStart();
                int aend = value3.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    value3.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value3.setSelection(astart - 1, aend - 1);
                } else
                    value3.setSelection(astart, aend);

                if (value3.getText().length() > 0 && value3.getText().length() < 2) {
                    value3.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    Value3Enable = false;
                } else {
                    value3.setTextColor(0xff000000); //BLACK color
                    Value3Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        value4 = (EditText) _curFragmentView.findViewById(R.id.etvalue4);
        value4.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value4.getSelectionStart();
                int aend = value4.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    value4.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value4.setSelection(astart - 1, aend - 1);
                } else
                    value4.setSelection(astart, aend);

                if (value4.getText().length() > 0 && value4.getText().length() < 2) {
                    value4.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    Value4Enable = false;
                } else {
                    value4.setTextColor(0xff000000); //BLACK color
                    Value4Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });


    }

    private void initListenerPWD_64() {
        PW2value1 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue1);
        PW2value1.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        PW2value1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = PW2value1.getSelectionStart();
                int aend = PW2value1.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    PW2value1.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    PW2value1.setSelection(astart - 1, aend - 1);
                } else
                    PW2value1.setSelection(astart, aend);

                if (PW2value1.getText().length() > 0 && PW2value1.getText().length() < 2) {
                    PW2value1.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    PW2Value1Enable = false;
                } else {
                    PW2value1.setTextColor(0xff000000); //BLACK color
                    PW2Value1Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        PW2value2 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue2);
        PW2value2.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        PW2value2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = PW2value2.getSelectionStart();
                int aend = PW2value2.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    PW2value2.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    PW2value2.setSelection(astart - 1, aend - 1);
                } else
                    PW2value2.setSelection(astart, aend);

                if (PW2value2.getText().length() > 0 && PW2value2.getText().length() < 2) {
                    PW2value2.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    PW2Value2Enable = false;
                } else {
                    PW2value2.setTextColor(0xff000000); //BLACK color
                    PW2Value2Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        PW2value3 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue3);
        PW2value3.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        PW2value3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = PW2value3.getSelectionStart();
                int aend = PW2value3.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    PW2value3.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    PW2value3.setSelection(astart - 1, aend - 1);
                } else
                    PW2value3.setSelection(astart, aend);

                if (PW2value3.getText().length() > 0 && PW2value3.getText().length() < 2) {
                    PW2value3.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    PW2Value3Enable = false;
                } else {
                    PW2value3.setTextColor(0xff000000); //BLACK color
                    PW2Value3Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        PW2value4 = (EditText) _curFragmentView.findViewById(R.id.PW2etvalue4);
        PW2value4.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        PW2value4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = PW2value4.getSelectionStart();
                int aend = PW2value4.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (Helper.checkDataHexa(FieldValue) == false) {
                    PW2value4.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    PW2value4.setSelection(astart - 1, aend - 1);
                } else
                    PW2value4.setSelection(astart, aend);

                if (PW2value4.getText().length() > 0 && PW2value4.getText().length() < 2) {
                    PW2value4.setTextColor(0xffff0000); //RED color
                    buttonPresentPassword.setClickable(false);
                    buttonWritePassword.setClickable(false);
                    PW2Value4Enable = false;
                } else {
                    PW2value4.setTextColor(0xff000000); //BLACK color
                    PW2Value4Enable = true;
                    if (checkPwdvalueEnableState()) {
                        buttonPresentPassword.setClickable(true);
                        buttonWritePassword.setClickable(true);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });


    }

    private boolean _GPOTogglecheck;
    private void initListenerGPO() {
        boolean disableDVFeatures = false;
        NFCApplication currentApp = NFCApplication.getApplication();
        final NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag.getModel().contains("DV")) {
            disableDVFeatures = true;
        }
        if (disableDVFeatures) {
            LinearLayout GPOConfButtonLayout = (LinearLayout) _curFragmentView.findViewById(R.id.GPOConfButtonLayout);
            GPOConfButtonLayout.setVisibility(View.VISIBLE);
            LinearLayout lyGPOField = (LinearLayout) _curFragmentView.findViewById(R.id.lyGPOField);
            lyGPOField.setVisibility(View.VISIBLE);
            LinearLayout stategpoRelLayout = (LinearLayout) _curFragmentView.findViewById(R.id.stategpoRelLayout);
            stategpoRelLayout.setVisibility(View.VISIBLE);


            // GPO Toogle Management
            // Configure the GPOToggle - Button + Switch

            Button buttonGPO = (Button) _curFragmentView.findViewById(R.id.bEnablestategpo);

            buttonGPO.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    MenuToolsLRFragment.this.currentAction = MenuToolsLRFragment.actionType.SETSTATE_DVGPO;

                    //_GPOTogglecheck = ((Switch) _curFragmentView.findViewById(R.id.tbEnablestategpo)).isChecked();
                    // int value = _GPOTogglecheck == true ? 1:0;
                    byte CFGGPO = (byte) 0x80;
                    if (manageConfigGPO(CFGGPO) != 0) {
                        currentTag.reportActionStatus("Check that GPO register is set to User mode...", -1);
                    }
                }
            });

            Button buttonCFGGPO = (Button) _curFragmentView.findViewById(R.id.ConfGPOButton);

            buttonCFGGPO.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    MenuToolsLRFragment.this.currentAction = MenuToolsLRFragment.actionType.SETSTATE_DVCFGGPO;
                    byte CFGGPO = 0;
                    RadioButton rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_GPOSet);
                    if (rb.isChecked()){
                        CFGGPO = 0x00;
                    }
                    rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_GPOReset);
                    if (rb.isChecked()){
                        CFGGPO = 0x01;
                    }
                    rb = (RadioButton) _curFragmentView.findViewById(R.id.RB_GPOInterrupt);
                    if (rb.isChecked()){
                        CFGGPO = (byte) 0x80;
                    }
                    if (manageConfigGPO(CFGGPO) != 0) {
                        currentTag.reportActionStatus("Check that GPO register is set to User mode...", -1);
                    }

                }
            });


        }
    }
    private int manageConfigGPO(byte gpo_mode) {
        byte[] MBBlockAnswer;
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;
        returncd = currentTag.reportActionStatusTransparent("SetupConfigGPO ERROR  (No tag answer) ", -1);
        while ((ret = currentTag.pingTag()) != true && cpt <= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (ret) {
            Log.d(TAG, " SetupConfigGPO Action");
            //
            SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
            STNfcTagVHandler mtagHDL;
            mtagHDL = (STNfcTagVHandler) (currentTag.getSTTagHandler());

            NFCCommandVExtended LRcmdExtended = new NFCCommandVExtended(currentTag.getModel());
            MBBlockAnswer = null;
            cpt = 0;
            while ((MBBlockAnswer == null || MBBlockAnswer[0] == 1) && cpt <= 10) {
                MBBlockAnswer = LRcmdExtended.setGPOConfig(currentTag.getTag(),sysHDL,gpo_mode);
                cpt++;
            }

            if (MBBlockAnswer == null || MBBlockAnswer[0] == -1) {
                returncd = currentTag.reportActionStatus("SetupConfigGPO ERROR write (No tag answer) ", -1);
            } else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x01) {
                returncd = currentTag.reportActionStatus("Command is not supported. ", 0x01);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x02) {
                returncd = currentTag.reportActionStatus("Command is not recognized (format error). ", 0x02);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x03) {
                returncd = currentTag.reportActionStatus("SetupConfigGPO ERROR write:Invalid command ", 0x03);
            } else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x0F) {
                returncd = currentTag.reportActionStatus("Error with no information given. ", 0x0F);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x10) {
                returncd = currentTag.reportActionStatus("The specified block is not available. ", 0x10);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x11) {
                returncd = currentTag.reportActionStatus("The specified block is already locked and thus cannot be locked again. ", 0x11);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x12) {
                returncd = currentTag.reportActionStatus("The specified block is locked and its contents cannot be changed. ", 0x12);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x13) {
                returncd = currentTag.reportActionStatus("The specified block was not successfully programmed. ", 0x13);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x14) {
                returncd = currentTag.reportActionStatus("The specified block was not successfully locked. ", 0x14);
            }else if ((MBBlockAnswer[0] & 0x01) == 1 && MBBlockAnswer[1] == (byte) 0x15) {
                returncd = currentTag.reportActionStatus("The specified block is protected. ", 0x15);
            }else if ((MBBlockAnswer[0] & 0x01) == 0) {
                returncd = currentTag.reportActionStatus("SetupConfigGPO command succeeded", 0);
            } else {
                returncd = currentTag.reportActionStatus("SetupConfigGPO Error ... ", -1);
            }


            //
        } else {
            returncd = currentTag.reportActionStatus("SetupConfigGPO Tag not on the field...", -1);
        }
        return returncd;
    }

    private void initListenerEH() {
        boolean disableDVFeatures = false;
        NFCApplication currentApp = NFCApplication.getApplication();
        final NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag.getModel().contains("DV")) {
            disableDVFeatures = true;
        }

        valueEHconfigByte = (EditText) _curFragmentView.findViewById(R.id.EHconfigByte);

        valueEHconfigByte.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        valueEHconfigByte.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = valueEHconfigByte.getSelectionStart();
                int aend = valueEHconfigByte.getSelectionEnd();

                String FieldValue = s.toString().toUpperCase();

                if (FieldValue.length() < 2) {
                    valueEHconfigByte.setTextColor(0xffff0000); //RED color
                    buttonWriteEHconfig.setClickable(false);
                    buttonWriteD0config.setClickable(false);
                } else {
                    valueEHconfigByte.setTextColor(0xff000000); //BLACK color
                    buttonWriteEHconfig.setClickable(true);
                    buttonWriteD0config.setClickable(true);
                }

                if (Helper.checkDataHexa(FieldValue) == false) {
                    valueEHconfigByte.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    valueEHconfigByte.setSelection(astart - 1 > 0? (astart - 1):0, aend - 1> 0? (aend - 1):0);
                } else
                    valueEHconfigByte.setSelection(astart, aend);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }
        });

        buttonReadEHconfig = (Button) _curFragmentView.findViewById(R.id.button_ReadEHconfig);
        buttonWriteEHconfig = (Button) _curFragmentView.findViewById(R.id.button_WriteEHconfig);
        buttonWriteD0config = (Button) _curFragmentView.findViewById(R.id.button_WriteD0config);
        if (disableDVFeatures) buttonWriteD0config.setVisibility(View.GONE);

        valueEHenableByte = (EditText) _curFragmentView.findViewById(R.id.EHenableByte);
        valueEHenableByte.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        buttonCheckEHenable = (Button) _curFragmentView.findViewById(R.id.button_CheckEHenable);
        buttonResetEHenable = (Button) _curFragmentView.findViewById(R.id.button_ResetEHenable);
        buttonSetEHenable = (Button) _curFragmentView.findViewById(R.id.button_SetEHenable);

        buttonReadEHconfig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valueEHconfigByte.getApplicationWindowToken(), 0);

                // ==> FBE                new StartReadEHconfigTask().execute();
                if (mEnergyHarvesting.readEHConfig() == 0) {
                    // ok
                    currentTag.reportActionStatus("EH config read succeed", 0);

                } else {
                    currentTag.reportActionStatus("EH config read failed", -1);

                }
                String valueByte = mEnergyHarvesting.getValueEHConfigByte();
                if (valueByte != null) valueEHconfigByte.setText(valueByte);
                else {
                    valueByte = "X";
                    valueEHconfigByte.setText(valueByte);
                }
            }
        });

        buttonWriteEHconfig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valueEHconfigByte.getApplicationWindowToken(), 0);

                String value = valueEHconfigByte.getText().toString();

                if (value.length() == 0)
                    value = "00";
                byte dataToWrite = Helper.ConvertStringToHexByte(value);
                if (mEnergyHarvesting.writeEHConfig(dataToWrite) == 0) {
                    currentTag.reportActionStatus("EH config write succeed",0);
                } else {
                    currentTag.reportActionStatus("EH config write failed",-1);
                }
                String valueByte = mEnergyHarvesting.getValueEHConfigByte();
                valueEHconfigByte.setText(valueByte);
            }

        });

        buttonWriteD0config.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valueEHconfigByte.getApplicationWindowToken(), 0);

                String value = valueEHconfigByte.getText().toString();

                if (value.length() == 0)
                    value = "00";
                byte dataToWrite = Helper.ConvertStringToHexByte(value);
                if (mEnergyHarvesting.writeD0Config(dataToWrite) == 0) {
                    // ok
                } else {
                    // ko
                }
            }
        });

        buttonCheckEHenable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valueEHenableByte.getApplicationWindowToken(), 0);

                if (mEnergyHarvesting.checkEHConfig() == 0) {
                    // ok
                    currentTag.reportActionStatus("EH check succeed",0);

                } else {
                     currentTag.reportActionStatus("EH check  failed",-1);
                }
                String valueByte = mEnergyHarvesting.getValueEHEnableByte();
                valueEHenableByte.setText(valueByte);           }
        });

        buttonResetEHenable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valueEHenableByte.getApplicationWindowToken(), 0);

                if (mEnergyHarvesting.resetEHConfig() == 0) {
                    // ok
                    currentTag.reportActionStatus("EH Reset succeed",0);
                } else {
                    currentTag.reportActionStatus("EH Reset failed",-1);

                }
                String valueByte = mEnergyHarvesting.getValueEHEnableByte();
                valueEHenableByte.setText(valueByte);
            }
        });

        buttonSetEHenable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity

                InputMethodManager imm = (InputMethodManager) NFCApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(valueEHenableByte.getApplicationWindowToken(), 0);
                if (mEnergyHarvesting.setEHConfig() == 0) {
                    // ok
                    currentTag.reportActionStatus("EH set succeed",0);
                } else {
                    currentTag.reportActionStatus("EH set failed",-1);
                }
                String valueByte = mEnergyHarvesting.getValueEHEnableByte();
                valueEHenableByte.setText(valueByte);
            }
        });

    }


    private void preExecutePresentPwd32() {
        String valueBlock1 = value1.getText().toString();
        String valueBlock2 = value2.getText().toString();
        String valueBlock3 = value3.getText().toString();
        String valueBlock4 = value4.getText().toString();

        if (valueBlock1.length() == 0)
            valueBlock1 = "00";
        if (valueBlock2.length() == 0)
            valueBlock2 = "00";
        if (valueBlock3.length() == 0)
            valueBlock3 = "00";
        if (valueBlock4.length() == 0)
            valueBlock4 = "00";

        value1.setText(Helper.FormatValueByteWrite(valueBlock1));
        value2.setText(Helper.FormatValueByteWrite(valueBlock2));
        value3.setText(Helper.FormatValueByteWrite(valueBlock3));
        value4.setText(Helper.FormatValueByteWrite(valueBlock4));

        valueBlock1 = value1.getText().toString();
        valueBlock2 = value2.getText().toString();
        valueBlock3 = value3.getText().toString();
        valueBlock4 = value4.getText().toString();

        String valueBlockTotal = "";
        valueBlockTotal += valueBlock1 + valueBlock2;
        byte[] valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[0] = valueBlockWrite[0];
        PasswordData[1] = valueBlockWrite[1];

        valueBlockTotal = "";
        valueBlockTotal += valueBlock3 + valueBlock4;
        valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[2] = valueBlockWrite[0];
        PasswordData[3] = valueBlockWrite[1];
    }

    private void preExecutePresentPwd32_2() {
        String valueBlock1 = PW2value1.getText().toString();
        String valueBlock2 = PW2value2.getText().toString();
        String valueBlock3 = PW2value3.getText().toString();
        String valueBlock4 = PW2value4.getText().toString();

        if (valueBlock1.length() == 0)
            valueBlock1 = "00";
        if (valueBlock2.length() == 0)
            valueBlock2 = "00";
        if (valueBlock3.length() == 0)
            valueBlock3 = "00";
        if (valueBlock4.length() == 0)
            valueBlock4 = "00";

        PW2value1.setText(Helper.FormatValueByteWrite(valueBlock1));
        PW2value2.setText(Helper.FormatValueByteWrite(valueBlock2));
        PW2value3.setText(Helper.FormatValueByteWrite(valueBlock3));
        PW2value4.setText(Helper.FormatValueByteWrite(valueBlock4));

        valueBlock1 = PW2value1.getText().toString();
        valueBlock2 = PW2value2.getText().toString();
        valueBlock3 = PW2value3.getText().toString();
        valueBlock4 = PW2value4.getText().toString();

        String valueBlockTotal = "";
        valueBlockTotal += valueBlock1 + valueBlock2;
        byte[] valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[4] = valueBlockWrite[0];
        PasswordData[5] = valueBlockWrite[1];

        valueBlockTotal = "";
        valueBlockTotal += valueBlock3 + valueBlock4;
        valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[6] = valueBlockWrite[0];
        PasswordData[7] = valueBlockWrite[1];
    }

    private void preExecutePresentPwd() {
        if (this.nbpwdvalues == 4) {
            preExecutePresentPwd32();

        } else if (this.nbpwdvalues == 8) {
            PasswordData = null;
            PasswordData = new byte[this.nbpwdvalues];
            preExecutePresentPwd32();
            preExecutePresentPwd32_2();

        }
    }

    private void preExecuteWritePwd() {
        if (this.nbpwdvalues == 4) {
            preExecuteWritePwd32();

        } else if (this.nbpwdvalues == 8) {
            PasswordData = null;
            PasswordData = new byte[this.nbpwdvalues];
            preExecuteWritePwd32();
            preExecuteWritePwd32_2();

        }

    }

    private void preExecuteWritePwd32() {
        String valueBlock1 = value1.getText().toString();
        String valueBlock2 = value2.getText().toString();
        String valueBlock3 = value3.getText().toString();
        String valueBlock4 = value4.getText().toString();

        if (valueBlock1.length() == 0)
            valueBlock1 = "00";
        if (valueBlock2.length() == 0)
            valueBlock2 = "00";
        if (valueBlock3.length() == 0)
            valueBlock3 = "00";
        if (valueBlock4.length() == 0)
            valueBlock4 = "00";

        value1.setText(Helper.FormatValueByteWrite(valueBlock1));
        value2.setText(Helper.FormatValueByteWrite(valueBlock2));
        value3.setText(Helper.FormatValueByteWrite(valueBlock3));
        value4.setText(Helper.FormatValueByteWrite(valueBlock4));

        valueBlock1 = value1.getText().toString();
        valueBlock2 = value2.getText().toString();
        valueBlock3 = value3.getText().toString();
        valueBlock4 = value4.getText().toString();

        String valueBlockTotal = "";
        valueBlockTotal += valueBlock1 + valueBlock2;
        byte[] valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[0] = valueBlockWrite[0];
        PasswordData[1] = valueBlockWrite[1];

        valueBlockTotal = "";
        valueBlockTotal += valueBlock3 + valueBlock4;
        valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[2] = valueBlockWrite[0];
        PasswordData[3] = valueBlockWrite[1];
    }

    private void preExecuteWritePwd32_2() {
        String valueBlock1 = PW2value1.getText().toString();
        String valueBlock2 = PW2value2.getText().toString();
        String valueBlock3 = PW2value3.getText().toString();
        String valueBlock4 = PW2value4.getText().toString();

        if (valueBlock1.length() == 0)
            valueBlock1 = "00";
        if (valueBlock2.length() == 0)
            valueBlock2 = "00";
        if (valueBlock3.length() == 0)
            valueBlock3 = "00";
        if (valueBlock4.length() == 0)
            valueBlock4 = "00";

        PW2value1.setText(Helper.FormatValueByteWrite(valueBlock1));
        PW2value2.setText(Helper.FormatValueByteWrite(valueBlock2));
        PW2value3.setText(Helper.FormatValueByteWrite(valueBlock3));
        PW2value4.setText(Helper.FormatValueByteWrite(valueBlock4));

        valueBlock1 = PW2value1.getText().toString();
        valueBlock2 = PW2value2.getText().toString();
        valueBlock3 = PW2value3.getText().toString();
        valueBlock4 = PW2value4.getText().toString();

        String valueBlockTotal = "";
        valueBlockTotal += valueBlock1 + valueBlock2;
        byte[] valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[4] = valueBlockWrite[0];
        PasswordData[5] = valueBlockWrite[1];

        valueBlockTotal = "";
        valueBlockTotal += valueBlock3 + valueBlock4;
        valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);

        PasswordData[6] = valueBlockWrite[0];
        PasswordData[7] = valueBlockWrite[1];
    }

    /**
     *
     */
    private void getLockSector() {
        String valueBlock1 = m_sectorvalue.getText().toString();
        if (valueBlock1.length() == 0)
            valueBlock1 = "00";
        m_sectorvalue.setText(Helper.FormatValueByteWrite(valueBlock1));
        valueBlock1bis = Integer.parseInt(valueBlock1);

    }

    private void getLockZone() {
        String valueBlock1 = m_sectorvalue.getText().toString();
        if (valueBlock1.length() == 0)
            valueBlock1 = "0";
        m_sectorvalue.setText(Helper.FormatValueByteWrite(valueBlock1));
        valueBlock1bis = Integer.parseInt(valueBlock1);

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
                        String pwd;
                        if (data.getStringExtra("password") != null) {
                            pwd = data.getStringExtra("password").toString();
                        } else {
                            pwd = "No pwd";
                        }
                        intent.putExtra("password", pwd);
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
                        tempPasswordRead = data.getStringExtra("password").toString();
                        // Start New Fragment Activity to handle user Request.
                        // Now request Modification password
                        PasswordDialogFragment newFragment = (PasswordDialogFragment) PasswordDialogFragment.newInstance(
                                getString(R.string.pw_title), getString(R.string.pw_rightmsg),
                                getString(R.string.pw_button_ok), getString(R.string.pw_button_cancel));
                        newFragment.setTargetFragment(MenuToolsLRFragment.this, DIALOG_FRAGMENT_RIGHT);
                        newFragment.show(getFragmentManager(), "dialog");
                    } else {
                        Log.d("DIALOG DEBUG", "No New Password -  ");
                    }
                }
                break;

            case TOOL_REQUEST_DONE: {
                Log.d("DIALOG DEBUG", "Tool request done !");

                break;
            }
            case TOOL_EXCHANGE_DATA_DONE: {
                Log.d("DIALOG DEBUG", "Tool-Exchange request done !");
                break;
            }
        }
    }

}
