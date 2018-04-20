// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import com.st.MB.FTMUseCaseFWUPassword;
import com.st.MB.MBTransferListenerFWU;
import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMUseCaseFWUpdate;
import com.st.MB.FTMUseCaseGen;
import com.st.MB.MBFWUListenerDataSent;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.stnfchelper;
import com.st.demo.FastTransferActivity.ActionMBDemo;
import com.st.nfcv.BasicOperation;
import com.st.nfcv.Helper;
import com.st.nfcv.MBCommandV;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler.ST25DVRegisterTable;

import com.st.nfcv.stnfcm24LRProtectionLockMgt;
import com.st.util.crc;

import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.app.AlertDialog;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Log;

public class FirmwareUpdate extends FragmentActivity implements AdapterView.OnItemSelectedListener {
    TextView sendLog;
    TextView receiveLog;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private long mCpt = 0;

    private int mCurrentCfgSelection = 0; // static by default = 0
    private boolean mCurrentCfgMBEN = true; // On

    int mAndroidLoopThreadCpt = 50;


    FTMUseCaseGen mFWUpdateUseCase;
    EventLogInfoCallBack mMBUseCaseLog;


    private ActionMBDemo mMBAction = ActionMBDemo.MB_HR_FW_EXCHANGE;
    private int mFWArraySize = 0;

    private boolean mFileError = false;

    private stnfcm24LRProtectionLockMgt mProtectionLockMgt;
    private byte[] mModificationPassword64BitsLong;
    final Context context = this;
    public String mComputedPassword;
    //Editable password_prompt ;

    // Password for the FWU use case
    public String mComputedPasswordFWU;
    private byte[] mModificationPassword32BitsLong;
    FTMUseCaseFWUPassword mFWUpdatePassword;
    boolean mFWUEnable = false;
    pwdEventLogInfoCallBack mFWUPwdLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);

        sendLog = (TextView) findViewById(R.id.Text_log_send_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());
        receiveLog = (TextView) findViewById(R.id.Text_log_receive_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());

        // Logs
        mMBUseCaseLog = new EventLogInfoCallBack(receiveLog, sendLog);
        mFWUPwdLog = new pwdEventLogInfoCallBack(receiveLog, sendLog);
        // Config Static Dynamic
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.RadioCFGStaticDyn);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.RB_cfg_static:
                        mCurrentCfgSelection = 0;
                        break;
                    case R.id.RB_cfg_Dyn:
                        mCurrentCfgSelection = 1;
                        break;

                    default:
                        mCurrentCfgSelection = 0;
                        break;
                }

            }
        });

        // Config MB Enable
        RadioGroup radioGroupMBEN = (RadioGroup) findViewById(R.id.RadioCFGMBEN);

        radioGroupMBEN.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.RB_cfg_MBOff:
                        mCurrentCfgMBEN = false;
                        break;
                    case R.id.RB_cfg_MBOn:
                        mCurrentCfgMBEN = true;
                        break;

                    default:
                        mCurrentCfgMBEN = true;
                        break;
                }

            }
        });

        try {
            installBinaryFromApk(); // Request to install apk embedded assets in AppData if not yet copied.
        } catch (NameNotFoundException e) {
            Log.e("ST25DV", " Exception : Data directory not found !!");
            e.printStackTrace();
        }

// ========================
        mFileError = false;

        // Retrieve file from NFCappsActivity_Menu.FirmwareApplicationDirPath
        firmwareRepo = new File(FirmwareApplicationDirPath);
        currentFw2UploadId = 0;
        if (firmwareRepo != null) {
            firmwarelist = firmwareRepo.listFiles();
            nbFWinAppDataDir = firmwarelist.length;
        }

        // Retrieve files from SDCard.
        File extSDStore = Environment.getExternalStorageDirectory();
        String SDFWPath = extSDStore.getAbsolutePath() + "/Download/" + fwextMemDir;
        firmwareSDRepo = new File(SDFWPath);
        if (!firmwareSDRepo.exists()) {
            firmwareSDRepo.mkdir();  // build directory in user wants to store within his own firmwares.
        }

        firmwareSDlist = firmwareSDRepo.listFiles();

        // build list file Name
        listFWFileName = new String[firmwarelist.length + firmwareSDlist.length + 1];
        for (int i = 0; i < firmwarelist.length; i++) {
            listFWFileName[i] = firmwarelist[i].getName();
        }

        for (int i = firmwarelist.length; i < firmwareSDlist.length + firmwarelist.length; i++) {
            listFWFileName[i] = firmwareSDlist[i - firmwarelist.length].getName();
        }
        listFWFileName[firmwarelist.length + firmwareSDlist.length] = m_AutoGeneratedFile;

        // ========================================

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listFWFileName);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);


        m_binsize = 20 * 1024;

        sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
        sizeETView.setText(String.valueOf(m_binsize));
//        sizeETView.setVisibility(View.GONE);

//        findViewById(R.id.TVbinSizeLabel).setVisibility(View.GONE);


        sizeSBView = (SeekBar) findViewById(R.id.valueseekbar1);
//        sizeSBView.setVisibility(View.GONE);
        // Set Default value
        sizeSBView.setProgress(m_binsize);
        sizeSBView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int download = ((int) Math.round(progress / 4)) * 4;
                //m_binsize = ((int)Math.round(progress/4))*4;

                sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
                sizeETView.setVisibility(View.VISIBLE);
                sizeETView.setText(String.valueOf(download));
            }
        });


        initListenerActionCfg();
        initListener();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[]{ndef,};
        mTechLists = new String[][]{new String[]{android.nfc.tech.NfcV.class.getName()}};

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:

                break;
            default:
        }
    }

    public void onBackPressed() {
        if (mFWUpdateUseCase != null)  mFWUpdateUseCase.stopUCThreadingLoop(0);
        if (mFWUpdatePassword != null)  mFWUpdatePassword.stopUCThreadingLoop(0);

        super.onBackPressed();
    }

    private static final int FILE_SELECT_CODE = 0;


    private void initListener() {

        buttonWriteFromFile = (Button) findViewById(R.id.LoadFromFileButton);
        buttonWriteFromFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
// start FWU function .....
// check if files are available
                mMBUseCaseLog.clearGenericEventInformation(false);
                mMBUseCaseLog.clearGenericEventInformation(true);

                if (listFWFileName.length > 1) {
                    if (listFWFileName[currentFw2UploadId].contains(m_AutoGeneratedFile)) {
                        // No file auto generated
                        mMBUseCaseLog.logGenericEventInformation("Starting FW transfer ... ", true, Color.BLACK);
                        mMBUseCaseLog.logGenericEventInformation("No File available ... ", false, Color.RED);
                        mMBUseCaseLog.logGenericEventInformation("Please, populate " + FirmwareApplicationDir, false, Color.RED);

                    } else {
                        if (mFWUEnable) {
                            // file ok - process
                            mFWUpdateUseCase = new FTMUseCaseFWUpdate(bufferFile, m_CRC, MBFct.MB_FCT_FW_UPLOAD);
                            // Display info
                            sizeSBView.setMax(m_binsize);
                            sizeSBView.setProgress(0);
                            sizeETView.setText(String.valueOf(m_binsize));
                            mMBUseCaseLog.logGenericEventInformation("Starting FW transfer of : " + bufferFile.length + " bytes", true, Color.BLACK);

                            // for the end of transfrt
                            mFWUpdateUseCase.addListener(mMBUseCaseLog);
                            // for data size update
                            ((FTMUseCaseFWUpdate) mFWUpdateUseCase).addListenerFWUDataSent(mMBUseCaseLog);

                            mFWUpdateUseCase.setUCThreadingIterator(mAndroidLoopThreadCpt);
                            mFWUpdateUseCase.setUCThreadingSleepTime(10);
                            mFWUpdateUseCase.execute();
                        } else {
                            mMBUseCaseLog.logGenericEventInformation("Starting FW transfer ... ", true, Color.BLACK);
                            mMBUseCaseLog.logGenericEventInformation("Please provide pwd first ... ", false, Color.RED);
                        }
                    }

                } else {
                    // No file available
                    mMBUseCaseLog.logGenericEventInformation("Starting FW transfer ... ", true, Color.BLACK);
                    mMBUseCaseLog.logGenericEventInformation("No File available ... ", false, Color.RED);
                    mMBUseCaseLog.logGenericEventInformation("Please, populate " + FirmwareApplicationDirPath, false, Color.RED);
                }


            }
        });

        buttonPresentPassword = (Button) findViewById(R.id.PWDLoadFromFileButton);
        buttonPresentPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.pwd_prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptView);
                final EditText _passwordEditText = (EditText) promptView.findViewById(R.id.userInput);
                // _passwordEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                _passwordEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputFilter[] filters = new InputFilter[2];
                filters[1] = new InputFilter.LengthFilter(16);
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                        String TempString = "";
                        if (end > start) {
                            char[] acceptedChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                            for (int index = start; index < end; index++) {
                                if (new String(acceptedChars).indexOf(source.charAt(index)) != -1) {
                                    TempString = TempString + source.charAt(index);

                                }
                            }
                        }
                        return TempString;
                    }
                };
                _passwordEditText.setFilters(filters);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        //password_prompt = _passwordEditText.getText();
                        clearLogInformation(sendLog);
                        clearLogInformation(receiveLog);

                        String currentPassword = _passwordEditText.getText().toString();
                        if ((currentPassword.length() % 2) == 1) {
                            currentPassword = currentPassword + "F"; // Password must be even. Add systematically a F in case of Odd password.
                        }
                        mComputedPassword = currentPassword;
                        if (mComputedPassword != null) {
                            mModificationPassword64BitsLong = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                                    (byte) 0xFF};
                            try {
                                System.arraycopy(stnfchelper.hexStringToByteArray(mComputedPassword), 0, mModificationPassword64BitsLong, 0, mComputedPassword.length() / 2);
                            } catch (IllegalArgumentException ex) {
                                Log.e(this.getClass().getName(), "Password: " + ex.toString());
                            }
                        }

                        mProtectionLockMgt = new stnfcm24LRProtectionLockMgt();
                        int ret = 0;
                        ret = mProtectionLockMgt.presentPassword(null, mModificationPassword64BitsLong, (byte) 0);
                        if (ret != 0) {
                            updateLogInformation("Present password ", sendLog);
                            updateLogInformation("Failed, error :" + ret, receiveLog);
                            updateLogInformation("Pwd (even) :" + Helper.ConvertHexByteArrayToString(mModificationPassword64BitsLong), receiveLog);
                        } else {
                            updateLogInformation("Present password ", sendLog);
                            updateLogInformation("Succeed ...", receiveLog);
                        }
                        Log.v(this.getClass().getName(), "Password: " + Helper.ConvertHexByteArrayToString(mModificationPassword64BitsLong));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mComputedPassword = "";
                    }
                });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
        });

        buttonPasswordFWU = (Button) findViewById(R.id.PWDForFWUpdate);
        buttonPasswordFWU.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View promptView = layoutInflater.inflate(R.layout.pwd_prompts, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptView);
                final EditText _passwordEditText = (EditText) promptView.findViewById(R.id.userInput);
                // _passwordEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                _passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputFilter[] filters = new InputFilter[2];
                filters[1] = new InputFilter.LengthFilter(9);
                filters[0] = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                        String TempString = "";
                        if (end > start) {
                            char[] acceptedChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                            for (int index = start; index < end; index++) {
                                if (new String(acceptedChars).indexOf(source.charAt(index)) != -1) {
                                    TempString = TempString + source.charAt(index);

                                }
                            }
                        }
                        return TempString;
                    }
                };
                _passwordEditText.setFilters(filters);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        //password_prompt = _passwordEditText.getText();
                        clearLogInformation(sendLog);
                        clearLogInformation(receiveLog);
                        mModificationPassword32BitsLong = null;
                        String currentPassword = _passwordEditText.getText().toString();

                        //if ((currentPassword.length() % 2) == 1) {
                        //    currentPassword = "0" + currentPassword; // Password must be even. Add systematically a F in case of Odd password.
                        //}
                        int pwd = 1;
                        mComputedPasswordFWU = currentPassword;
                        //mModificationPassword32BitsLong = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                        try {
                            mModificationPassword32BitsLong = stnfchelper.hexStringToByteArray(mComputedPasswordFWU);
                        } catch (IllegalArgumentException ex) {
                            Log.e(this.getClass().getName(), "Password: " + ex.toString());
                            pwd = 0;
                        }
                        /*
                        // old implementation for pwd
                        try {
                            pwd = Integer.parseInt(currentPassword);
                        }catch(NumberFormatException e) {
                            pwd = 0;
                        }
                        mModificationPassword32BitsLong = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                        mModificationPassword32BitsLong[0] = (byte) ((pwd >> 24) & 0xFF);
                        mModificationPassword32BitsLong[1] = (byte) ((pwd >> 16) & 0xFF);
                        mModificationPassword32BitsLong[2] = (byte) ((pwd >> 8) & 0xFF);
                        mModificationPassword32BitsLong[3] = (byte) ((pwd >> 0) & 0xFF);
                        */
                        // Clear MB
/*
                        NFCApplication currentApp = NFCApplication.getApplication();
                        NFCTag currentTag = currentApp.getCurrentTag();

                        SysFileLRHandler sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = false;
                        MBCommandV bop = new MBCommandV(sysHDL.getMaxTransceiveLength());
                        bop.configureMB(staticregister, false);
                        bop.configureMB(staticregister, true);
                        Toast.makeText(getApplicationContext(), "Mail box cleared ....", Toast.LENGTH_LONG).show();
*/
                        // End Clear MB

                        if (mComputedPasswordFWU != null && pwd ==1) {
                            //mModificationPassword32BitsLong = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                            //System.arraycopy(stnfchelper.hexStringToByteArray(mComputedPasswordFWU), 0, mModificationPassword32BitsLong, 0, mComputedPasswordFWU.length() / 2);
                            //mFWUpdatePassword = new FTMUseCaseFWUPassword(stnfchelper.hexStringToByteArray(mComputedPasswordFWU));
                            mFWUpdatePassword = new FTMUseCaseFWUPassword(mModificationPassword32BitsLong);
                            mFWUPwdLog.logGenericEventInformation("Starting FWU Pwd ...", true, Color.BLACK);
                            // for the end of transfrt
                            mFWUpdatePassword.addListener(mFWUPwdLog);
                            mFWUpdatePassword.execute();


                        } else {
                            // No file available
                            mFWUPwdLog.logGenericEventInformation("Starting FWU Pwd ...", true, Color.BLACK);
                            mFWUPwdLog.logGenericEventInformation("No PWD available ... ", false, Color.RED);
                            mFWUPwdLog.logGenericEventInformation("Please, provide a pwd (even)... " + mComputedPasswordFWU , false, Color.RED);
                        }
                  }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mModificationPassword32BitsLong = null;
                        mComputedPasswordFWU = "";
                    }
                });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
        });

    }



    private void initListenerActionCfg() {
        Button launchCfg = (Button) findViewById(R.id.BMailBoxRLReadCfg);
        launchCfg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                clearLogInformation(sendLog);
                clearLogInformation(receiveLog);

                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                if (mCurrentCfgSelection == 0 || mCurrentCfgSelection == 1) {
                    if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = mCurrentCfgSelection == 0 ? true : false;
                        MBCommandV MBCmd = new MBCommandV(sysHDL.getMaxTransceiveLength());
                        BasicOperation bop = new BasicOperation(sysHDL.getMaxTransceiveLength());
                        updateLogInformation("Read MB Config", sendLog);
                        if (MBCmd.MBReadCfgBasicOp(staticregister) == 0) {
                            // ok
                            updateMBRegistersInformation(MBCmd.getBlockAnswer());
                            // Get Watch Dog register value
                            //SysFileLRHandler sfh = (SysFileLRHandler) currentTag.getSYSHandler();
                            if (bop.readRegister(ST25DVRegisterTable.Reg_MB_WDG, true) == 0) {
                                updateMBWDGConfiguration(bop.getMBBlockAnswer());
                            } else {
                                updateMBWDGConfiguration(null);
                            }
                        } else {
                            // ko
                            updateMBRegistersInformation(null);

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid parameters, please select Static/Dynamic Cfg", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button writeCfg = (Button) findViewById(R.id.BMailBoxEnableDisable);
        writeCfg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                clearLogInformation(sendLog);
                clearLogInformation(receiveLog);

                if (mCurrentCfgSelection == 0 || mCurrentCfgSelection == 1) {
                    NFCApplication currentApp = NFCApplication.getApplication();
                    NFCTag currentTag = currentApp.getCurrentTag();
                    if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysLRH = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = mCurrentCfgSelection == 0 ? true : false;
                        MBCommandV bop = new MBCommandV(sysLRH.getMaxTransceiveLength());
                        updateLogInformation("configureMB MBEN", sendLog);
                        boolean mb = true; // otherwise EH
                        boolean enable = mCurrentCfgMBEN;
                        if (bop.configureMB(staticregister, enable) == 0) {
                            // ok
                            Toast.makeText(getApplicationContext(), "configureMB succeed ....", Toast.LENGTH_LONG).show();
                            //updateLogInformation("configureMB succeed  :" + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                            updateLogInformation("configureMB succeed Max TCV :" + sysLRH.getMaxTransceiveLength(), receiveLog);

                        } else {
                            // ko
                            Toast.makeText(getApplicationContext(), "configureMB failed ....", Toast.LENGTH_LONG).show();
                            if (bop.getBlockAnswer() != null)
                                updateLogInformation("configureMB failed : " + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "configureMB Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "configureMB Invalid parameters, please select Static/Dynamic Cfg", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button writeWDG = (Button) findViewById(R.id.BMailBoxWWatchDog);
        writeWDG.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                clearLogInformation(sendLog);
                clearLogInformation(receiveLog);

                TextView textWDG = (TextView) findViewById(R.id.myNumberWatchDog);
                byte ValueWDG = (byte) Byte.valueOf(textWDG.getText().toString());
                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                    SysFileLRHandler sysLRH = (SysFileLRHandler) currentTag.getSYSHandler();
                    boolean staticregister = mCurrentCfgSelection == 0 ? true : false;
                    BasicOperation bop = new BasicOperation(sysLRH.getMaxTransceiveLength());
                    updateLogInformation("Write WDG ", sendLog);
                    if (bop.writeRegister(ST25DVRegisterTable.Reg_MB_WDG, ValueWDG, true) == 0) {
                        // ok
                        Toast.makeText(getApplicationContext(), "Write WDG succeed ....", Toast.LENGTH_LONG).show();
                        updateLogInformation("Write WDG succeed :" + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()), receiveLog);

                    } else {
                        // ko
                        Toast.makeText(getApplicationContext(), "Write WDG failed ....", Toast.LENGTH_LONG).show();
                        byte[] answer = bop.getMBBlockAnswer();
                        if (answer != null)
                            updateLogInformation("Write WDG failed : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Write WDG Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                }


            }
        });

        Button writeAndroidLoopThreadCpt = (Button) findViewById(R.id.BMailBoxThreadLoop);
        TextView textThreadLoop = (TextView) findViewById(R.id.myNumberThreadLoop);
        textThreadLoop.setText(new Integer(mAndroidLoopThreadCpt).toString());

        writeAndroidLoopThreadCpt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView textThreadLoop = (TextView) findViewById(R.id.myNumberThreadLoop);
                mAndroidLoopThreadCpt = Integer.valueOf(textThreadLoop.getText().toString());

            }
        });

    }


    private abstract class EventLogInfoCallBackGeneric {
        public EventLogInfoCallBackGeneric(TextView tv_in, TextView tv_out) {
            super();
            this.tv_in = tv_in;
            this.tv_out = tv_out;
        }

        TextView tv_in;
        TextView tv_out;

        protected void logGenericEventInformation(String text, boolean in_out, int col) {
//            SpannableString ss1=  new SpannableString(tv.getText());
//            tv.append(ss1);
            Spannable WordtoSpan = new SpannableString(text);
            WordtoSpan.setSpan(new ForegroundColorSpan(col), 0, text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //textview.setText(WordtoSpan);

            if (in_out == true) {
                this.tv_out.append(WordtoSpan);
                this.tv_out.append("\n");

            } else {
                this.tv_in.append(WordtoSpan);
                this.tv_in.append("\n");
            }

        }

        protected void clearGenericEventInformation(boolean in_out) {
            if (in_out == true) {
                this.tv_out.setText("");
            } else {
                this.tv_in.setText("");

            }

        }

    }

    private class EventLogInfoCallBack extends EventLogInfoCallBackGeneric implements MBTransferListenerFWU, MBFWUListenerDataSent {
        public EventLogInfoCallBack(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);

        }

        private void display_result(int error, FTMUseCaseGen uc) {
            int col = tv_in.getCurrentTextColor();
            int colforwritting;
            if (error !=0) {
                colforwritting = Color.RED;
                mFWUEnable = false;
                //this.tv_in.setTextColor(Color.RED);
            } else {
                colforwritting = Color.BLUE;
                //this.tv_in.setTextColor(Color.GREEN);

            }
            logGenericEventInformation("Error status : " + (error != 0 ? "Error" : "No Error"), false, colforwritting);
            logGenericEventInformation("Error code :" + Helper.ConvertIntToHexFormatString(uc.getErrorCode()), false, colforwritting);
            logGenericEventInformation("Duration : " + uc.getEllapsedTime()+" ms", false, colforwritting);
            logGenericEventInformation("Full data size : " + uc.getTotalBytesProcessed(), false, colforwritting);

            // get and display Payload received
            byte[] payload_received = uc.getPayloadReceived();
            if (payload_received != null) {
                logGenericEventInformation("data PL size : " + payload_received.length, false, col);
                byte[] spld;
                byte[] epld;
                if (payload_received.length > 20) {
                    int start_pp = 10;
                    int end_pp = 10;
                    spld = new byte[start_pp];
                    epld = new byte[end_pp];
                    System.arraycopy(payload_received, 0, spld, 0, start_pp);
                    System.arraycopy(payload_received, payload_received.length - end_pp, epld, 0, end_pp);
                    logGenericEventInformation("data PL Start ...: " + Helper.ConvertHexByteArrayToString(spld), false, col);
                    logGenericEventInformation("data PL end .....: " + Helper.ConvertHexByteArrayToString(epld), false, col);

                } else {
                    logGenericEventInformation("data PL overall: " + Helper.ConvertHexByteArrayToString(payload_received), false, col);
                }
            } else {
                //updateLogInformation("data PL overall: No Data", false);

            }
            //tv_in.setTextColor(col);

        }

        @Override
        public void endOfTransfer(int error) {
            // TODO Auto-generated method stub
            display_result(error, mFWUpdateUseCase);

        }

        @Override
        public void FWUdataSent(long size) {
            // TODO Auto-generated method stub
            sizeSBView.setProgress((int) size);
            //sizeETView.setText(String.valueOf(size));
            Log.v(this.getClass().getName(), "Data ... " + size);

        }


    }
    private class pwdEventLogInfoCallBack extends EventLogInfoCallBackGeneric implements MBTransferListenerFWU, MBFWUListenerDataSent {
        public pwdEventLogInfoCallBack(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);

        }

        private void display_result(int error, FTMUseCaseGen uc) {
            int col = tv_in.getCurrentTextColor();
            int colforwritting;
            if (error != 0) {
                colforwritting = Color.RED;
                mFWUEnable = false;
                mModificationPassword64BitsLong = null;
                mModificationPassword32BitsLong = null;
                //this.tv_in.setTextColor(Color.RED);
            } else {
                colforwritting = Color.BLUE;
                mFWUEnable = true;
                mModificationPassword64BitsLong = null;
                mModificationPassword32BitsLong = null;
                //this.tv_in.setTextColor(Color.GREEN);

            }
            logGenericEventInformation("Password status : " + (error != 0 ? "Wrong pwd" : "pwd OK ..."), false, colforwritting);
         }

        @Override
        public void endOfTransfer(int error) {
            // TODO Auto-generated method stub
            display_result(error, mFWUpdatePassword);

        }

        @Override
        public void FWUdataSent(long size) {

        }
    }

    private void updateLogInformation(String text, TextView tv) {
//        SpannableString ss1=  new SpannableString(tv.getText());
        tv.append(text);
        tv.append("\n");
//        tv.append(ss1);
    }

    private void clearLogInformation(TextView tv) {
        tv.setText("");
    }

    private void updateMBRegistersInformation(byte[] answer) {
        if (answer != null) {
            Toast.makeText(getApplicationContext(),
                    "Read MB Cfg : " + Helper.ConvertHexByteArrayToString(answer), Toast.LENGTH_LONG).show();
            updateLogInformation("Read MB Cfg : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
            if (answer.length == 1) {
                updateLogInformation("Read MB Cfg : " + "No data", receiveLog);

            } else {
                // update the register text values .....MBEN
                int mcolorzero = Color.RED;
                int id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView06 : R.id.DynCCfgRow1TextView06;
                if ((answer[1] & 0x01) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....HostPutMsg
                id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView05 : R.id.DynCCfgRow1TextView05;
                if ((answer[1] & 0x02) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....RFPutMsg
                id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView04 : R.id.DynCCfgRow1TextView04;
                if ((answer[1] & 0x04) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....HostMissMsg
                id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView03 : R.id.DynCCfgRow1TextView03;
                if ((answer[1] & 0x10) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....RFMissMsg
                id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView02 : R.id.DynCCfgRow1TextView02;
                if ((answer[1] & 0x20) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....CurrentMsg
                id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView01 : R.id.DynCCfgRow1TextView01;
                if ((answer[1] & 0xC0) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                    if ((answer[1] & 0xC0) == 1) {
                        // I2C
                    } else if ((answer[1] & 0xC0) == 2) {
                        // RF
                    } else {
                        // issue .....??
                    }
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Read MB Cfg : " + "Error cmd answer", Toast.LENGTH_LONG)
                    .show();
            updateLogInformation("Read MB Cfg : " + "Error cmd answer", receiveLog);
            // Upodate text in TAB
            // update the register text values .....MBEN
            int id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView06 : R.id.DynCCfgRow1TextView06;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....HostPutMsg
            id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView05 : R.id.DynCCfgRow1TextView05;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....RFPutMsg
            id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView04 : R.id.DynCCfgRow1TextView04;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....HostMissMsg
            id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView03 : R.id.DynCCfgRow1TextView03;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....RFMissMsg
            id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView02 : R.id.DynCCfgRow1TextView02;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....CurrentMsg
            id_tt = this.mCurrentCfgSelection == 0 ? R.id.StaticCfgRow1TextView01 : R.id.DynCCfgRow1TextView01;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);

        }
    }

    private void updateMBWDGConfiguration(byte[] answer) {
        int id_tt = R.id.myNumberWatchDog;
        if (answer != null) {
            Toast.makeText(getApplicationContext(),
                    "WDG Cfg : " + Helper.ConvertHexByteArrayToString(answer), Toast.LENGTH_LONG).show();
            updateLogInformation("WDG Cfg : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
            if (answer.length == 1) {
                updateLogInformation("WDG Cfg : " + "No data", receiveLog);
                ((TextView) this.findViewById(id_tt)).setText("X");
                ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);

            } else {
                // update the register text values .....MBEN

                //((TextView) this.findViewById(id_tt)).setText(Helper.ConvertHexByteToString(answer[1]));
                ((TextView) this.findViewById(id_tt)).setText(new Integer(answer[1]).toString());
                ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
            }

        } else {
            ((TextView) this.findViewById(id_tt)).setText("X");
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), "WDG Cfg : " + "Error cmd answer", Toast.LENGTH_LONG)
                    .show();
            updateLogInformation("WDG Cfg : " + "Error cmd answer", receiveLog);
            // Upodate text in TAB
            // update the register text values .....MBEN

        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        String action = intent.getAction();
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
                || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
                || (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))) {
            Tag rawTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NFCTag tmpTag = null;

            if (rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                tmpTag = new NFCTag(rawTag, msgs);
            } else {
                tmpTag = new NFCTag(rawTag);
            }

            NFCApplication.getApplication().setCurrentTag(tmpTag);
            NFCAppHeaderFragment mHeadFrag = (NFCAppHeaderFragment) this.getSupportFragmentManager().findFragmentById(R.id.WcActNFCAppHeaderFragmentId);
            mHeadFrag.onTagChanged(tmpTag);

        }

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    protected void onPause() {
        mCpt = 500;
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    // ============================================ File mgt

    /**
     * Copy embedded FW from APK to application directory
     */
    /**
     * appData folder path
     */
    private String dataApplicationDir;
    /**
     * firmware appData folder path
     */
    private File FirmwareApplicationDir;
    private String m_AutoGeneratedFile = "auto generated";

    /**
     * Simple copy buffer method helper
     *
     * @param in  : input stream
     * @param out : output stream target
     * @throws IOException
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[in.available()];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void copyFirmwares() {
        AssetManager assetManager = getResources().getAssets();
        String[] files = null;
        try {
            files = assetManager.list("binaries");
        } catch (Exception e) {
            Log.e("read binaries ERROR", e.toString());
            e.printStackTrace();
        }
        for (int i = 0; i < files.length; i++) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("binaries/" + files[i]);
                out = new FileOutputStream(FirmwareApplicationDirPath + files[i]);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("copy Firmwares ERROR", e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * @throws NameNotFoundException Create AppData folder associated to ST95HF demo application
     *                               Extract and install FW provided within application's APK package
     */
    private void installBinaryFromApk() throws NameNotFoundException {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        Log.i(this.getClass().getName(), "Get External Storage Directory" + extStorageDirectory);

        dataApplicationDir = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.dataDir;
        Log.i(this.getClass().getName(), "Get Data Application directory :" + dataApplicationDir);


        FirmwareApplicationDirPath = dataApplicationDir + "/bintouploadforstSt25FWU/";

        FirmwareApplicationDir = new File(FirmwareApplicationDirPath);
        if (!FirmwareApplicationDir.exists()) {
            FirmwareApplicationDir.mkdirs();
        }

        // Launch Copy Firmware.
        copyFirmwares();

    }

    /**
     * number of identified firmwares provided natively with rx95hf demo application
     * Used to display and select the whole list of firmware with the listview widget
     */
    public static int nbFWinAppDataDir = 0;
    /**
     * firmware appData folder path
     */
    public static String FirmwareApplicationDirPath;

    /**
     * directory to stored Binaries (usually located on SD/MMC card)
     */
    private String fwextMemDir = "bintouploadforstSt25FWU";

    /**
     * Array to store native rx95hf firmware provided with the application
     * Those files are stored in xx demo appData folder
     */
    public static File[] firmwarelist = null;
    /**
     * Array to store user xx firmware.
     * Those files can be copied from PC through USB connection on ./Download/fwrx95hf folder
     */
    public static File[] firmwareSDlist = null;
    /**
     * Folder  of the FW natively provided with the xx demo application
     */
    public static File firmwareRepo = null;
    /**
     * Folder of the FW added by the user (usually MMCard/Download/TBD
     */
    public static File firmwareSDRepo = null;


    /**
     * Array to store the complete list of Firmware file name
     * Used to display Firmware file names in the listview widget
     */
    String[] listFWFileName = null;
    /**
     * ID of the current selected firmware to upload
     */
    public static int currentFw2UploadId;


    /**
     * directory to stored Binaries (usually located on SD/MMC card)
     */
    public int m_binsize = 0;
    /**
     * Byte array to store the read buffer from Firmware file to upload
     */
    public byte[] bufferFile = null;
    /**
     * store the computed CRC file to check upload coherence
     */
    public long m_CRC = 0;


    public TextView sizeETView;
    public TextView sizeETinfoView;
    public SeekBar sizeSBView;

    Button buttonWriteFromFile;
    Button buttonPresentPassword;

    Button buttonPasswordFWU;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        clearLogInformation(sendLog);
        clearLogInformation(receiveLog);


        currentFw2UploadId = position;
        if (currentFw2UploadId <= (firmwarelist.length + firmwareSDlist.length)) { // Display Stuff to determine Size of the Stream to generate
            updateRetrievalSelectedFileInfoParameters();
            sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
            sizeETView.setVisibility(View.VISIBLE);
            sizeSBView = (SeekBar) findViewById(R.id.valueseekbar1);
            sizeSBView.setVisibility(View.VISIBLE);
            findViewById(R.id.TVbinSizeLabel).setVisibility(View.VISIBLE);

        } else { // ensure the binary autogenerated option are hidden
            sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
            sizeETView.setVisibility(View.GONE);
            sizeSBView = (SeekBar) findViewById(R.id.valueseekbar1);
            sizeSBView.setVisibility(View.GONE);
            findViewById(R.id.TVbinSizeLabel).setVisibility(View.GONE);
        }
        sizeSBView.setMax(m_binsize);
        sizeSBView.setProgress(0);
        sizeETView.setText(String.valueOf(m_binsize));
        sizeETinfoView = (TextView) findViewById(R.id.TVbininfoSizeValue);
        sizeETinfoView.setText(String.valueOf(m_binsize));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }


    private void updateRetrievalSelectedFileInfoParameters() {
        File f = null;
        int fileSize = 0;
        if (currentFw2UploadId < nbFWinAppDataDir) {
            f = new File(firmwareRepo + "/" + firmwarelist[currentFw2UploadId].getName());
        } else {
            if (firmwareSDlist.length != 0) {
                if (firmwareSDlist.length > (currentFw2UploadId - nbFWinAppDataDir)) {
                    f = new File(firmwareSDRepo + "/" + firmwareSDlist[currentFw2UploadId - nbFWinAppDataDir].getName());
                    fileSize = (int) f.length();
                    m_binsize = fileSize;
                    updateLogInformation("File selected : " + firmwareSDRepo + "/" + firmwareSDlist[currentFw2UploadId - nbFWinAppDataDir].getName(), sendLog);

                } else {
                    // auto generated --- no process
                }
            } else {

            }
        }

//    sizeSBView.setProgress(m_binsize);
//    sizeETView.setText(String.valueOf(m_binsize));

        if (fileSize == 0) {
            mFileError = true;
            updateLogInformation(getResources().getString(R.string.tstate_filenotfound), sendLog);
        } else {

            try {
                m_CRC = crc.CRC(f);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            FileInputStream fileIS = null;
            try {
                fileIS = new FileInputStream(f);
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            bufferFile = new byte[fileSize];
            if (fileIS != null) {
                try {
                    fileIS.read(bufferFile);
                    fileIS.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }


    }

}