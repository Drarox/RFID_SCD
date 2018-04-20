// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.st.MB.FTMUseCaseExchangeRRAChaining;
import com.st.MB.FTMUseCaseExchangeRRANoChaining;
import com.st.MB.MBFWUListenerDataSent;
import com.st.MB.MBTransferListenerDataReceived;
import com.st.MB.MBTransferListenerFWU;
import com.st.MB.MBTransferListenerHostAcknowledge;
import com.st.MB.MBTransferListenerHostRequest;
import com.st.MB.FTMUseCaseFWUpdate;
import com.st.MB.FTMUseCaseHostAnswerAcknowledge;
import com.st.MB.FTMUseCaseHostRequest;
import com.st.MB.FTMPTColRtoH;
import com.st.MB.FTMPTColGeneric.ProtocolStateMachine;
import com.st.MB.FTMUseCaseGen;
import com.st.MB.FTMHeaderBuilder.MBFct;
import com.st.MB.FTMHeaderBuilder.MBcmd;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import com.st.nfcv.BasicOperation;
import com.st.nfcv.Helper;
import com.st.nfcv.MBCommandV;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler.ST25DVRegisterTable;
import com.st.nfcv.NFCCommandVExtended;
import com.st.util.DebugUtility;
import com.st.util.IOUtil;
import com.st.util.crc;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import android.os.Parcelable;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Log;

public class FastTransferActivity extends FragmentActivity
{
    static final String TAG = "FastTransferActivity";

    TextView sendLog;
    TextView receiveLog;
    Button launchCmdAction;
    Button launchClear;
    Button launchSend;
    Button launchReceive;

    public TextView sizeETView;
    public SeekBar  sizeSBView;


    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private long cpt = 0;

    private int m_maxTranceiveSize = 0;

    private  Spinner spinner1;
    private  Spinner spinnerFileSize;
    private int currentCfgSelection = 0; // static by default = 0
    private boolean currentCfgMBEN = true; // On

    private boolean m_background_Taskloop = false;
    Thread threadTaskLoop;
    int m_AndroidLoopThreadCpt = 50;

    FTMUseCaseGen FWuC;
    FTMUseCaseExchangeRRAChaining SimpleChainedByteExchange;
    FTMUseCaseExchangeRRANoChaining SimpleNoChainedByteExchange;

    eventLogInfoCallBack MBuCLog; // For 3 previous object display

    FTMUseCaseHostRequest simpleCmdReceivedFromHost;
    FTMUseCaseHostAnswerAcknowledge mMBGenericUseCaseHostAnswerAcknowledge;

    eventLogInfoCallBackHostCmd MBuCHostCmdLog; // Call back for a Host message cmd received
    eventLogInfoCallBackHostAcknowledge MBuCHostAckCmdLog;

    public enum ActionMBDemo {
        MB_UNKNOWN,
        MB_SIMPLE_EXCHANGE,
        MB_CHAINED_EXCHANGE,
        MB_HR_CHAINED_EXCHANGE,
        MB_HR_SIMPLE_EXCHANGE,
        MB_HR_FW_EXCHANGE,
        MB_RH_FW_EXCHANGE,
        MB_RH_XFERS_UPLOAD
    }
    private ActionMBDemo m_mb_action;
    private long THreadWaitingTime = 100;
    private int FWArraySize = 0;

    private Handler mHandler = new Handler();

    private int SEEKBAR_RED;
    private static int SEEKBAR_GREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);

        SEEKBAR_RED = 0xFFffd300; //getResources().getColor(R.color.st_dark_orange);
        SEEKBAR_GREEN = 0xFFbbcc00; //getResources().getColor(R.color.st_dark_green);

        sendLog = (TextView) findViewById(R.id.Text_log_send_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());
        receiveLog = (TextView) findViewById(R.id.Text_log_receive_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());

        // Logs
        MBuCLog = new eventLogInfoCallBack(receiveLog,sendLog);
        MBuCHostCmdLog = new eventLogInfoCallBackHostCmd(receiveLog,sendLog);
        MBuCHostAckCmdLog = new eventLogInfoCallBackHostAcknowledge(receiveLog,sendLog);

        // Action
        spinner1 = (Spinner) findViewById(R.id.listMBUseCasesSend);
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                MBuCLog.llogClearinfo(false);
                MBuCLog.llogClearinfo(true);
                resetProgressColorSBUiUpdates();

                spinnerFileSize = (Spinner) findViewById(R.id.listFiletoUpload);
                spinnerFileSize.setVisibility(View.GONE);
                if (String.valueOf(spinner1.getSelectedItem()).contains("R/H - Simple Transfer")) {
                    m_mb_action = ActionMBDemo.MB_SIMPLE_EXCHANGE;
                }  else if (String.valueOf(spinner1.getSelectedItem()).contains("R/H - Chained Transfer")) {
                    m_mb_action = ActionMBDemo.MB_CHAINED_EXCHANGE;
                } else if (String.valueOf(spinner1.getSelectedItem()).contains("H/R - Simple Transfer")) {
                    m_mb_action = ActionMBDemo.MB_HR_CHAINED_EXCHANGE;
                } else if (String.valueOf(spinner1.getSelectedItem()).contains("H/R - Chained Transfer")) {
                    m_mb_action = ActionMBDemo.MB_HR_CHAINED_EXCHANGE;
                } else if (String.valueOf(spinner1.getSelectedItem()).contains("R/H - Data Transfer")) {
                    m_mb_action = ActionMBDemo.MB_RH_FW_EXCHANGE;
                    spinnerFileSize.setVisibility(View.VISIBLE);
                }else if (String.valueOf(spinner1.getSelectedItem()).contains("H/R - Data Transfer")) {
                        m_mb_action = ActionMBDemo.MB_RH_XFERS_UPLOAD;
                }else {
                    m_mb_action = ActionMBDemo.MB_UNKNOWN;
                    MBuCLog.lloginfo("Please select first an action to perform ... ", true);
                }

                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        spinnerFileSize = (Spinner) findViewById(R.id.listFiletoUpload);
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("200b");
        spinnerArray.add("1KB");
        spinnerArray.add("5KB");
        spinnerArray.add("10KB");
        spinnerArray.add("16KB");
        spinnerArray.add("20kB");
        spinnerArray.add("25kB");
        spinnerArray.add("32kB");
        spinnerArray.add("50KB");
        spinnerArray.add("100KB");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerFileSize.setAdapter(adapter);
        spinnerFileSize.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        FWArraySize = 200;
                        break;
                    case 1:
                        FWArraySize = 1000;
                        break;
                    case 2:
                        FWArraySize = 5000;
                        break;
                    case 3:
                        FWArraySize = 10000;
                        break;
                    case 4:
                        FWArraySize = 16000;
                        break;
                    case 5:
                        FWArraySize = 20000;
                        break;
                    case 6:
                        FWArraySize = 25000;
                        break;
                    case 7:
                        FWArraySize = 32000;
                        break;
                    case 8:
                        FWArraySize = 50000;
                        break;
                    case 9:
                        FWArraySize = 100000;
                        break;
                    default:
                        FWArraySize = 10000;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });


        spinnerFileSize.setSelection(0);
        // Config Static Dynamic
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.RadioCFGStaticDyn);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                      case R.id.RB_cfg_static:
                          currentCfgSelection = 0;
                           break;
                      case R.id.RB_cfg_Dyn:
                          currentCfgSelection = 1;
                          break;

                    default:
                        currentCfgSelection = 0;
                        break;
                }   
                 
            }
        }); 

        // Config MB Enable
        RadioGroup radioGroupMBEN = (RadioGroup) findViewById(R.id.RadioCFGMBEN);

        radioGroupMBEN.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                      case R.id.RB_cfg_MBOff:
                          currentCfgMBEN = false;
                           break;
                      case R.id.RB_cfg_MBOn:
                          currentCfgMBEN = true;
                          break;

                    default:
                        currentCfgMBEN = true;
                        break;
                }   
                 
            }
        });


        sizeETView = (TextView)  findViewById(R.id.TVbinSizeValue);
        sizeETView.setText(String.valueOf(this.FWArraySize));
        sizeETView.setVisibility(View.GONE);

//        findViewById(R.id.TVbinSizeLabel).setVisibility(View.GONE);


        sizeSBView = (SeekBar)  findViewById(R.id.valueseekbar1);
        sizeSBView.setVisibility(View.GONE);
        sizeSBView.setClickable(false);

        // Set Default value
        sizeSBView.setProgress(FWArraySize);
        sizeSBView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                int download = ((int) Math.round(progress / 4)) * 4;

                sizeETView = (TextView) findViewById(R.id.TVbinSizeValue);
                sizeETView.setVisibility(View.VISIBLE);
                sizeETView.setText(String.valueOf(download));
            }
        });



        initListenerActionCfg();
        initListener();
        
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[] {ndef,};
        mTechLists = new String[][] { new String[] { android.nfc.tech.NfcV.class.getName() } };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case FILE_SELECT_CODE:
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                Uri uri = data.getData();
                Log.d(this.getClass().getName(), "File Uri: " + uri.toString());
                // Get the path
                String path = "";
                try {
                    path = FastTransferActivity.getPath(this.getApplicationContext(),uri);
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d(this.getClass().getName(), "File Path: " + path);
                // Get the file instance
                // File file = new File(path);
                // Initiate the upload
            }

            break;
        default:
        }
    }

    public void onBackPressed() {
        if (SimpleNoChainedByteExchange != null)  SimpleNoChainedByteExchange.stopUCThreadingLoop(0);
        if (SimpleChainedByteExchange != null)  SimpleChainedByteExchange.stopUCThreadingLoop(0);
        if (FWuC != null)  FWuC.stopUCThreadingLoop(0);
        if (simpleCmdReceivedFromHost != null) simpleCmdReceivedFromHost.stopUCThreadingLoop(0);
        super.onBackPressed();
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initListener()
    {


        launchCmdAction = (Button) findViewById(R.id.BMailBoxRLCmdAction);
        launchSend = (Button) findViewById(R.id.BMailBoxRLWrite);
        launchReceive = (Button) findViewById(R.id.BMailBoxRLReceive);

        launchClear = (Button) findViewById(R.id.BMailBoxRLClear);
        launchClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        m_background_Taskloop = false;
                        if (m_mb_action == ActionMBDemo.MB_SIMPLE_EXCHANGE) {
                            if (SimpleNoChainedByteExchange != null)  SimpleNoChainedByteExchange.stopUCThreadingLoop(0);
                        }  else if (m_mb_action == ActionMBDemo.MB_CHAINED_EXCHANGE) {
                            if (SimpleChainedByteExchange != null)  SimpleChainedByteExchange.stopUCThreadingLoop(0);

                        } else if (m_mb_action == ActionMBDemo.MB_RH_FW_EXCHANGE) {
                            if (FWuC != null)  FWuC.stopUCThreadingLoop(0);

                        }  else {

                    }



            }
        });

        launchCmdAction.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sizeETView = (TextView)  findViewById(R.id.TVbinSizeValue);
                sizeETView.setVisibility(View.GONE);
                sizeSBView = (SeekBar)  findViewById(R.id.valueseekbar1);
                sizeSBView.setVisibility(View.GONE);
                resetProgressColorSBUiUpdates();
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                if (m_mb_action == ActionMBDemo.MB_SIMPLE_EXCHANGE) {
                    SimpleNoChainedByteExchange = new FTMUseCaseExchangeRRAChaining(MBFct.MB_FCT_SIMPLE);
                    MBuCLog.llogClearinfo(false);
                    MBuCLog.llogClearinfo(true);
                    MBuCLog.lloginfo("Starting RHR transfert ..16B received ", true);
                    SimpleNoChainedByteExchange.addListener(MBuCLog);
                    SimpleNoChainedByteExchange.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    SimpleNoChainedByteExchange.setUCThreadingSleepTime(100);
                    SimpleNoChainedByteExchange.execute();
                }  else if (m_mb_action == ActionMBDemo.MB_CHAINED_EXCHANGE) {
                    SimpleChainedByteExchange = new FTMUseCaseExchangeRRAChaining(MBFct.MB_FCT_SIMPLE_CHAINED);
                    MBuCLog.llogClearinfo(false);
                    MBuCLog.llogClearinfo(true);
                    MBuCLog.lloginfo("Starting RHR transfert ..512B received ", true);
                    SimpleChainedByteExchange.addListener(MBuCLog);
                    SimpleChainedByteExchange.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    SimpleChainedByteExchange.setUCThreadingSleepTime(100);
                    SimpleChainedByteExchange.execute();

                } else if (m_mb_action == ActionMBDemo.MB_RH_FW_EXCHANGE) {
                    // other test implementation
                    FWuC = new FTMUseCaseFWUpdate(FWArraySize);
                    MBuCLog.llogClearinfo(false);
                    MBuCLog.llogClearinfo(true);
                    MBuCLog.lloginfo("Starting Data transfert of : " + FWArraySize, true);
                    FWuC.addListener(MBuCLog);
                    // Display info
                    sizeETView = (TextView)  findViewById(R.id.TVbinSizeValue);
                    sizeETView.setVisibility(View.VISIBLE);
                    sizeSBView = (SeekBar)  findViewById(R.id.valueseekbar1);
                    sizeSBView.setVisibility(View.VISIBLE);

                    sizeSBView.setMax(FWArraySize);
                    sizeSBView.setProgress(0);
                    sizeETView.setText(String.valueOf(FWArraySize));
                    ((FTMUseCaseFWUpdate)FWuC).addListenerFWUDataSent(MBuCLog);

                    FWuC.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    FWuC.setUCThreadingSleepTime(10);
                    FWuC.execute();
                    // end test implementation

                }  else if (m_mb_action == ActionMBDemo.MB_HR_SIMPLE_EXCHANGE) {
                    llogClearinfo(sendLog);
                    llogClearinfo(receiveLog);
                    // New version
                    simpleCmdReceivedFromHost = new FTMUseCaseHostRequest();
                    MBuCHostCmdLog.llogClearinfo(false);
                    MBuCHostCmdLog.llogClearinfo(true);
                    MBuCHostCmdLog.lloginfo("Starting waiting Host Cmd ... " , true);
                    simpleCmdReceivedFromHost.addListener((MBTransferListenerHostRequest) MBuCHostCmdLog);
                    simpleCmdReceivedFromHost.addListener((MBTransferListenerDataReceived) MBuCHostCmdLog);
                    simpleCmdReceivedFromHost.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    simpleCmdReceivedFromHost.setUCThreadingSleepTime(300);
                    simpleCmdReceivedFromHost.execute();

                } else if (m_mb_action == ActionMBDemo.MB_HR_CHAINED_EXCHANGE) {
                    llogClearinfo(sendLog);
                    llogClearinfo(receiveLog);
                    // New version
                    simpleCmdReceivedFromHost = new FTMUseCaseHostRequest();
                    MBuCHostCmdLog.llogClearinfo(false);
                    MBuCHostCmdLog.llogClearinfo(true);
                    MBuCHostCmdLog.lloginfo("Starting waiting Host Cmd ... " , true);
                    simpleCmdReceivedFromHost.addListener((MBTransferListenerHostRequest) MBuCHostCmdLog);
                    simpleCmdReceivedFromHost.addListener((MBTransferListenerDataReceived) MBuCHostCmdLog);
                    simpleCmdReceivedFromHost.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    simpleCmdReceivedFromHost.setUCThreadingSleepTime(300);
                    simpleCmdReceivedFromHost.execute();

                } else if (m_mb_action == ActionMBDemo.MB_RH_XFERS_UPLOAD) {
                    sizeETView = (TextView)  findViewById(R.id.TVbinSizeValue);
                    sizeETView.setVisibility(View.VISIBLE);
                    sizeSBView = (SeekBar)  findViewById(R.id.valueseekbar1);
                    sizeSBView.setVisibility(View.VISIBLE);
                    sizeETView.setText(String.valueOf("n/a"));

                    llogClearinfo(sendLog);
                    llogClearinfo(receiveLog);
                    simpleCmdReceivedFromHost = new FTMUseCaseHostRequest();
                    MBuCHostCmdLog.llogClearinfo(false);
                    MBuCHostCmdLog.llogClearinfo(true);
                    MBuCHostCmdLog.lloginfo("Starting waiting Host Cmd ... " , true);
                    simpleCmdReceivedFromHost.addListener((MBTransferListenerHostRequest) MBuCHostCmdLog);
                    simpleCmdReceivedFromHost.addListener((MBTransferListenerDataReceived) MBuCHostCmdLog);
                    simpleCmdReceivedFromHost.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                    simpleCmdReceivedFromHost.setUCThreadingSleepTime(300);
                    simpleCmdReceivedFromHost.execute();
                    beginProgressColorSBUiUpdates();

                } else
                    m_mb_action = ActionMBDemo.MB_UNKNOWN;

            }
        });

        launchReceive.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);
                eventLogInfoCallBackHostCmdWithoutAction eventLogWithoutAction = new eventLogInfoCallBackHostCmdWithoutAction(receiveLog,sendLog);
                simpleCmdReceivedFromHost = new FTMUseCaseHostRequest();
                MBuCHostCmdLog.llogClearinfo(false);
                MBuCHostCmdLog.llogClearinfo(true);
                MBuCHostCmdLog.lloginfo("Starting waiting Host Cmd ... " , true);
                simpleCmdReceivedFromHost.addListener((MBTransferListenerHostRequest) eventLogWithoutAction);
                simpleCmdReceivedFromHost.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                simpleCmdReceivedFromHost.setUCThreadingSleepTime(300);
                simpleCmdReceivedFromHost.execute();

            }
        });

        launchSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                boolean forward = false;
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();
                SysFileLRHandler sysHDL = null;
                if (currentTag != null) {
                    if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
                        forward = true;
                    }

                    byte[] cmd = null;


                    // cmd
                    if (m_mb_action == ActionMBDemo.MB_SIMPLE_EXCHANGE && forward) {
                        FTMPTColRtoH m_MBPTL = new FTMPTColRtoH(MBFct.MB_FCT_SIMPLE);
                        m_MBPTL.setCmdPayload(null);
                        m_MBPTL.initProtocol(sysHDL.getMaxTransceiveLength());
                        NFCCommandVExtended LRcmdExtended = new NFCCommandVExtended(currentTag.getModel());
                        cmd = m_MBPTL.processRequest(sysHDL,LRcmdExtended,ProtocolStateMachine.MB_PTL_SM_Current);

                    } else if (m_mb_action == ActionMBDemo.MB_CHAINED_EXCHANGE) {
                        FTMPTColRtoH m_MBPTL = new FTMPTColRtoH(MBFct.MB_FCT_SIMPLE_CHAINED);
                        m_MBPTL.initProtocol(sysHDL.getMaxTransceiveLength());
                        m_MBPTL.setCmdPayload(null);
                        NFCCommandVExtended LRcmdExtended = new NFCCommandVExtended(currentTag.getModel());
                        cmd = m_MBPTL.processRequest(sysHDL,LRcmdExtended,ProtocolStateMachine.MB_PTL_SM_Current);

                    } else if (m_mb_action == ActionMBDemo.MB_RH_FW_EXCHANGE) {
                        Toast.makeText(getApplicationContext(), "Cmd Action not yet handled", Toast.LENGTH_LONG).show();
                        forward = false;


                    } else {
                        Toast.makeText(getApplicationContext(), "Cmd Action not yet handled", Toast.LENGTH_LONG).show();
                        forward = false;
                    }

                    // Process
                    if (forward) {
                        MBCommandV bop = new MBCommandV(
                                sysHDL.getMaxTransceiveLength());
                        lloginfo("Write MB simple cmd ", sendLog);
                        if (bop.writeMBMsg(cmd) == 0) {
                            // ok
                            Toast.makeText(getApplicationContext(), "Write MB simple cmd succeed ....",
                                    Toast.LENGTH_LONG).show();
                            lloginfo("Write MB simple cmd  succeed :"
                                    + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);

                        } else {
                            // ko
                            Toast.makeText(getApplicationContext(), "Write MB simple cmd failed ....",
                                    Toast.LENGTH_LONG).show();
                            lloginfo("Write MB simple cmd  failed : "
                                    + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Write MB simple cmd Invalid parameters, Tag has changed - no compatibility",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    private void initListenerActionCfg()
    {
        Button launchCfg = (Button) findViewById(R.id.BMailBoxRLReadCfg);
        launchCfg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                if (currentCfgSelection == 0 || currentCfgSelection == 1 ) {
                    if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysHDL = (SysFileLRHandler)currentTag.getSYSHandler();
                        boolean staticregister = currentCfgSelection==0?true:false;
                        MBCommandV MBcmd = new MBCommandV(sysHDL.getMaxTransceiveLength());
                        BasicOperation bop = new BasicOperation(sysHDL.getMaxTransceiveLength());
                        lloginfo("Read MB Config" , sendLog);
                        if (MBcmd.MBReadCfgBasicOp(staticregister) == 0) {
                            // ok
                            MBcfgUpdate(MBcmd.getBlockAnswer());
                            // Get Watch Dog register value
                            //SysFileLRHandler sfh = (SysFileLRHandler) currentTag.getSYSHandler();
                            if (bop.readRegister(ST25DVRegisterTable.Reg_MB_WDG, true) == 0) {
                                MBWDcfgUpdate(bop.getMBBlockAnswer());
                            } else {
                                MBWDcfgUpdate(null);
                            }
                        } else {
                            // ko
                            MBcfgUpdate(null);

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Invalid parameters, please select Static/Dynamic Cfg", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button writeCfg = (Button) findViewById(R.id.BMailBoxEnableDisable);
        writeCfg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                if (currentCfgSelection == 0 || currentCfgSelection == 1 ) {
                    NFCApplication currentApp = NFCApplication.getApplication();
                    NFCTag currentTag = currentApp.getCurrentTag();
                    if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysLRH = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = currentCfgSelection==0?true:false;
                        MBCommandV bop = new MBCommandV(sysLRH.getMaxTransceiveLength());
                        lloginfo("configureMB MBEN", sendLog);
                        boolean mb = true; // otherwise EH
                        boolean enable = currentCfgMBEN;
                        if (bop.configureMB(staticregister, enable) == 0) {
                            // ok
                            Toast.makeText(getApplicationContext(), "configureMB succeed ....", Toast.LENGTH_LONG).show();
                            //logGenericEventInformation("configureMB succeed  :" + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                            lloginfo("configureMB succeed Max TCV :" + sysLRH.getMaxTransceiveLength(), receiveLog);

                        } else {
                            // ko
                            Toast.makeText(getApplicationContext(), "configureMB failed ....", Toast.LENGTH_LONG).show();
                            if (bop.getBlockAnswer() != null)
                            lloginfo("configureMB failed : " + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()), receiveLog);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "configureMB Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "configureMB Invalid parameters, please select Static/Dynamic Cfg", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button writeWDG = (Button) findViewById(R.id.BMailBoxWWatchDog);
        writeWDG.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub
                llogClearinfo(sendLog);
                llogClearinfo(receiveLog);

                TextView textWDG = (TextView) findViewById(R.id.myNumberWatchDog);
                byte ValueWDG = (byte)Byte.valueOf(textWDG.getText().toString());
                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                    if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                        SysFileLRHandler sysLRH = (SysFileLRHandler) currentTag.getSYSHandler();
                        boolean staticregister = currentCfgSelection==0?true:false;
                        BasicOperation bop = new BasicOperation(sysLRH.getMaxTransceiveLength());
                        lloginfo("Write WDG ", sendLog);
                        boolean staticRegister = true;
                        if (bop.writeRegister(ST25DVRegisterTable.Reg_MB_WDG, ValueWDG, staticRegister) == 0) {
                            // ok
                            byte[] answer = bop.getMBBlockAnswer();
                            if (answer !=null){
                                Toast.makeText(getApplicationContext(), "Write WDG succeed ...", Toast.LENGTH_LONG).show();
                                lloginfo("Write WDG succeed :" + Helper.ConvertHexByteArrayToString(answer), receiveLog);
                            } else {
                                Toast.makeText(getApplicationContext(), "Write WDG succeed ...", Toast.LENGTH_LONG).show();
                                lloginfo("Write WDG succeed ..." , receiveLog);
                            }

                        } else {
                            // ko
                            Toast.makeText(getApplicationContext(), "Write WDG failed ....", Toast.LENGTH_LONG).show();
                            byte[] answer = bop.getMBBlockAnswer();
                            if (answer !=null)
                            lloginfo("Write WDG failed : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
                            else lloginfo("Write WDG failed ... " , receiveLog);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Write WDG Invalid parameters, Tag has changed - no compatibility", Toast.LENGTH_LONG).show();
                    }


            }
        });

        Button writeAndroidLoopThreadCpt = (Button) findViewById(R.id.BMailBoxThreadLoop);
        TextView textThreadLoop = (TextView) findViewById(R.id.myNumberThreadLoop);
        textThreadLoop.setText(new Integer(m_AndroidLoopThreadCpt).toString());

        writeAndroidLoopThreadCpt.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView textThreadLoop = (TextView) findViewById(R.id.myNumberThreadLoop);
                m_AndroidLoopThreadCpt = Integer.valueOf(textThreadLoop.getText().toString());

            }
        });

    }


    private abstract class eventLogInfoCallBackGeneric {
        public eventLogInfoCallBackGeneric(TextView tv_in, TextView tv_out) {
            super();
            this.tv_in = tv_in;
            this.tv_out = tv_out;
        }
        TextView tv_in;
        TextView tv_out;
        protected void lloginfo(String text,boolean in_out) {
//            SpannableString ss1=  new SpannableString(tv.getText());
//            tv.append(ss1);
            if (in_out == true) {
                this.tv_out.append(text);
                this.tv_out.append("\n");

            } else {
                this.tv_in.append(text);
                this.tv_in.append("\n");
            }

        }
        protected void llogClearinfo(boolean in_out) {
            if (in_out == true) {
                this.tv_out.setText("");
            } else {
                this.tv_in.setText("");

            }

        }

    }
    private class eventLogInfoCallBack extends eventLogInfoCallBackGeneric implements MBTransferListenerFWU, MBFWUListenerDataSent {
        public eventLogInfoCallBack(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);
//            this.tv_in = tv_in;
//            this.tv_out = tv_out;
        }
//        TextView tv_in;
//        TextView tv_out;


        private void display_result(int error, FTMUseCaseGen uc) {
            lloginfo("Error status " + error, false);
            lloginfo("Error code " + Helper.ConvertIntToHexFormatString(uc.getErrorCode()), false);
            lloginfo("Transfer time " + uc.getEllapsedTime() + " ms", false);
            lloginfo("Sent data " + uc.getTotalBytesProcessed(), false);

            // get and display Payload received
            byte[] payload_received = uc.getPayloadReceived();
            if (payload_received != null) {
                lloginfo("Rcv data " + payload_received.length, false);
                byte[] spld;
                byte[] epld;
                if (payload_received.length > 20) {
                    int start_pp = 10;
                    int end_pp = 10;
                    spld = new byte[start_pp];
                    epld = new byte[end_pp];
                    System.arraycopy(payload_received, 0, spld, 0, start_pp);
                    System.arraycopy(payload_received, payload_received.length - end_pp, epld, 0, end_pp);
                    lloginfo("Rcv data start ...: " + Helper.ConvertHexByteArrayToString(spld), false);
                    lloginfo("Rcv data end .....: " + Helper.ConvertHexByteArrayToString(epld), false);

                } else {
                    lloginfo("Rcv data overall: " + Helper.ConvertHexByteArrayToString(payload_received), false);
                }
            } else {
                lloginfo("Rcv data : No Data", false);

            }

        }

        @Override
        public void endOfTransfer(int error) {
            // TODO Auto-generated method stub
            if (m_mb_action == ActionMBDemo.MB_SIMPLE_EXCHANGE) {
                display_result(error,SimpleNoChainedByteExchange);
            }  else if (m_mb_action == ActionMBDemo.MB_CHAINED_EXCHANGE) {
                display_result(error,SimpleChainedByteExchange);
            } else if (m_mb_action == ActionMBDemo.MB_RH_FW_EXCHANGE) {
                display_result(error,FWuC);


            }  else
                lloginfo("Transfer xxxx status " , false);

        }

        @Override
        public void FWUdataSent(long size) {
            //Log.v(this.getClass().getName(), "Data ... " + size);
            sizeSBView.setProgress((int) size);
            //logGenericEventInformation(" " + size + ",", false);

        }
    }

    private Handler progressHandler; // init with new Handler(getMainLooper())
    private static AtomicInteger progressColor = new AtomicInteger(SEEKBAR_GREEN);

    private static AtomicInteger progressLostChunck = new AtomicInteger(0);
    private static AtomicInteger progressCurrentChunck = new AtomicInteger(0);

    private static AtomicInteger progressFullSize = new AtomicInteger(0);
    private static AtomicInteger progressCurrentSize = new AtomicInteger(0);

    private int PROGRESS_POLL_PERIOD_MILLIS = 2000;
    private void beginProgressColorSBUiUpdates() {
        progressColor.set(SEEKBAR_GREEN);
        progressLostChunck.set(0);
        progressCurrentChunck.set(0);
        progressFullSize.set(0);
        progressCurrentSize.set(0);
        displayColorProgress();
        progressHandler.postDelayed(pollProgress, PROGRESS_POLL_PERIOD_MILLIS);
    }
    private void resetProgressColorSBUiUpdates() {
        progressColor.set(SEEKBAR_GREEN);
        progressLostChunck.set(0);
        progressCurrentChunck.set(0);
        progressFullSize.set(0);
        progressCurrentSize.set(0);
        displayColorProgress();
        sizeSBView.setProgress(0);
        sizeSBView.setMax(0);

    }

    private Runnable pollProgress = new Runnable() {
        public void run() {
            if (sizeSBView.getVisibility() == View.VISIBLE) {
                displayColorProgress();
                progressHandler.postDelayed(pollProgress, PROGRESS_POLL_PERIOD_MILLIS);
            }
        }
    };
    private void displayColorProgress() {
        sizeSBView.setBackgroundColor(progressColor.get());
    }

    private class eventLogInfoCallBackHostCmd extends eventLogInfoCallBackGeneric implements MBTransferListenerHostRequest, MBTransferListenerDataReceived {
        public  eventLogInfoCallBackHostCmd(TextView tv_in, TextView tv_out) {
            super(tv_in,tv_out);
            progressHandler= new Handler(getMainLooper());
        }


        protected void display_result(int error, byte[] data, MBFct fct) {
            lloginfo("Reader receive cmd " + fct, false);
            lloginfo("Reader receive error code " + error, false);
            if (data != null) {
                lloginfo("Reader receive full data size " + data.length, false);

                // get and display Payload received
                byte[] payload_received = data;
                lloginfo("data PL size : " + payload_received.length, false);
                byte[] spld;
                byte[] epld;
                if (payload_received.length > 20) {
                    int start_pp = 10;
                    int end_pp = 10;
                    spld = new byte[start_pp];
                    epld = new byte[end_pp];
                    System.arraycopy(payload_received, 0, spld, 0, start_pp);
                    System.arraycopy(payload_received, payload_received.length - end_pp, epld, 0, end_pp);
                    lloginfo("data PL Start ...: " + Helper.ConvertHexByteArrayToString(spld), false);
                    lloginfo("data PL end .....: " + Helper.ConvertHexByteArrayToString(epld), false);

                } else {
                    lloginfo("data PL overall: " + Helper.ConvertHexByteArrayToString(payload_received), false);
                }
            } else {
                lloginfo("data PL overall: No data", false);
            }

        }

        @Override
        public void hostRequestAvailable(int error, byte[] data, MBFct fct, MBcmd cmd) {
            // TODO Auto-generated method stub
            display_result(error,data,fct);

            if (fct == MBFct.MB_FCT_SIMPLE_From_HOST) {
                 mMBGenericUseCaseHostAnswerAcknowledge = new FTMUseCaseHostAnswerAcknowledge(16);
                 lloginfo("starting cmd H2R transfer ..16B received ", true);
                 mMBGenericUseCaseHostAnswerAcknowledge.addListener(MBuCHostAckCmdLog);
                 mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingSleepTime(300);
                 mMBGenericUseCaseHostAnswerAcknowledge.execute();

            } else if (fct == MBFct.MB_FCT_SIMPLE_CHAINED_From_HOST) {
                lloginfo("Duration : " + simpleCmdReceivedFromHost.getEllapsedTime() + " ms", true);
                lloginfo("Full data size : " + simpleCmdReceivedFromHost.getTotalBytesProcessed(), true);
                 mMBGenericUseCaseHostAnswerAcknowledge = new FTMUseCaseHostAnswerAcknowledge(512);
                 lloginfo("starting cmd H2R transfer ..512B received ", true);
                 mMBGenericUseCaseHostAnswerAcknowledge.addListener(MBuCHostAckCmdLog);
                 mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                 mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingSleepTime(300);
                 mMBGenericUseCaseHostAnswerAcknowledge.execute();

            } else if (fct == MBFct.MB_FCT_IMAGE_UPLOAD) {
                lloginfo("Duration : " + simpleCmdReceivedFromHost.getEllapsedTime() + " ms", true);
                lloginfo("Full data size : " + simpleCmdReceivedFromHost.getTotalBytesProcessed(), true);

                //new SavePhotoTask().execute(data);
                long crc = calculateCRCofByteArray(data);
                lloginfo("CRC " + String.format("0x%08X", crc), true);
                //MBuCHostCmdLog.llogClearinfo(false);
                mMBGenericUseCaseHostAnswerAcknowledge = new FTMUseCaseHostAnswerAcknowledge(MBFct.MB_FCT_IMAGE_UPLOAD, IOUtil.longToBytes(crc));
                mMBGenericUseCaseHostAnswerAcknowledge.addListener(MBuCHostAckCmdLog);
                mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingSleepTime(300);
                mMBGenericUseCaseHostAnswerAcknowledge.execute();
            } else if (fct == MBFct.MB_FCT_Xfers_UPLOAD) {
                lloginfo("Duration : " + simpleCmdReceivedFromHost.getEllapsedTime() + " ms", true);
                lloginfo("Full data size : " + simpleCmdReceivedFromHost.getTotalBytesProcessed(), true);

                //new SavePhotoTask().execute(data);
                long crc = calculateCRCofByteArray(data);
                lloginfo("CRC " + String.format("0x%08X", crc), true);
                mMBGenericUseCaseHostAnswerAcknowledge = new FTMUseCaseHostAnswerAcknowledge(MBFct.MB_FCT_Xfers_UPLOAD, IOUtil.longToBytes(crc));
                mMBGenericUseCaseHostAnswerAcknowledge.addListener(MBuCHostAckCmdLog);
                mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingIterator(m_AndroidLoopThreadCpt);
                mMBGenericUseCaseHostAnswerAcknowledge.setUCThreadingSleepTime(300);
                mMBGenericUseCaseHostAnswerAcknowledge.execute();
            } else {
                sizeSBView.setBackgroundColor(SEEKBAR_RED);
                lloginfo("No cmd received or Cmd not yet implemented " + fct.toString(), true);
            }
        }


        @Override
        public void dataReceived(long size, long expectedSize, long currentChunck, long expectedChunck) {
            progressFullSize.set((int) expectedSize);
            progressCurrentSize.set((int) size);

            sizeSBView.setMax((int) expectedSize);
            sizeSBView.setProgress((int) size);

            // do not manipulate log on display
            if (currentChunck > expectedChunck) {
                // Update the progress bar
                progressColor.set(SEEKBAR_RED);
                if (DebugUtility.printMBExecutionInformation)
                    Log.v(TAG, "Lost Data Received chunck : " + currentChunck +
                            " size : " + size +
                            " Tsize : " + expectedSize +
                            " exp. chunck : " + (expectedChunck)
                    );
            } else {
                progressColor.set(SEEKBAR_GREEN);
                if (DebugUtility.printMBExecutionInformation)
                    Log.v(TAG, "Data Received chunck : " + currentChunck +
                            " size : " +  size +
                            " Tsize : " +  expectedSize +
                            " exp. chunck : " +  (expectedChunck)
                    );

            }
        }
    }

    private long calculateCRCofByteArray(byte[] data) {
        long mcrc = 0;
        try {
            mcrc = crc.CRC(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mcrc = -1;
            e.printStackTrace();
        }
        return mcrc;
    }
    private class eventLogInfoCallBackHostCmdWithoutAction extends eventLogInfoCallBackHostCmd {
        public  eventLogInfoCallBackHostCmdWithoutAction(TextView tv_in, TextView tv_out) {
            super(tv_in,tv_out);
        }

        @Override
        public void hostRequestAvailable(int error, byte[] data, MBFct fct, MBcmd cmd) {
            // TODO Auto-generated method stub
            display_result(error,data,fct);
        }

    }

    private class eventLogInfoCallBackHostAcknowledge extends eventLogInfoCallBackGeneric implements MBTransferListenerHostAcknowledge {
        public eventLogInfoCallBackHostAcknowledge(TextView tv_in, TextView tv_out) {
            super(tv_in, tv_out);
            // TODO Auto-generated constructor stub
        }

        private void display_result(int error, byte[] data, MBFct fct, MBcmd cmd) {
            lloginfo("Reader receive Ack fct " + fct, false);
            lloginfo("Reader receive Ack cmd " + cmd, false);
            lloginfo("Reader receive Ack err " + error, false);
            if (data != null) {
                lloginfo("Reader receive full data size" + data.length, false);

                // get and display Payload received
                byte[] payload_received = data;
                lloginfo("data PL size : " + payload_received.length, false);
                byte[] spld;
                byte[] epld;
                if (payload_received.length > 20) {
                    int start_pp = 10;
                    int end_pp = 10;
                    spld = new byte[start_pp];
                    epld = new byte[end_pp];
                    System.arraycopy(payload_received, 0, spld, 0, start_pp);
                    System.arraycopy(payload_received, payload_received.length - end_pp, epld, 0, end_pp);
                    lloginfo("data PL Start ...: " + Helper.ConvertHexByteArrayToString(spld), false);
                    lloginfo("data PL end .....: " + Helper.ConvertHexByteArrayToString(epld), false);

                } else {
                    lloginfo("data PL overall: " + Helper.ConvertHexByteArrayToString(payload_received), false);
                }
            } else {
                lloginfo("data PL overall: No data", false);
            }

        }

        @Override
        public void hostAcknowledgeAvailable(int error, byte[] data, MBFct fct, MBcmd cmd) {
            // TODO Auto-generated method stub
            display_result(error,data,fct,cmd);
        }

    }


    private void lloginfo(String text,TextView tv) {
//        SpannableString ss1=  new SpannableString(tv.getText());
        tv.append(text);
        tv.append("\n");
//        tv.append(ss1);
    }
    private void llogClearinfo(TextView tv) {
        tv.setText("");
    }

    private void MBcfgUpdate(byte[] answer) {
        if (answer != null) {
            Toast.makeText(getApplicationContext(),
                    "Read MB Cfg : "  + Helper.ConvertHexByteArrayToString(answer), Toast.LENGTH_LONG).show();
            lloginfo("Read MB Cfg : "  + Helper.ConvertHexByteArrayToString(answer), receiveLog);
            if (answer.length == 1) {
                lloginfo("Read MB Cfg : " + "No data", receiveLog);

            } else {
                // update the register text values .....MBEN
                int mcolorzero = Color.RED;
                int id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView06:R.id.DynCCfgRow1TextView06;
                if ((answer[1] & 0x01) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....HostPutMsg
                id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView05:R.id.DynCCfgRow1TextView05;
                if ((answer[1] & 0x02) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....RFPutMsg
                id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView04:R.id.DynCCfgRow1TextView04;
                if ((answer[1] & 0x04) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....HostMissMsg
                id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView03:R.id.DynCCfgRow1TextView03;
                if ((answer[1] & 0x10) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....RFMissMsg
                id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView02:R.id.DynCCfgRow1TextView02;
                if ((answer[1] & 0x20) == 0) {
                    ((TextView) this.findViewById(id_tt)).setTextColor(mcolorzero);
                } else {
                    ((TextView) this.findViewById(id_tt)).setTextColor(Color.GREEN);
                }
                // update the register text values .....CurrentMsg
                id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView01:R.id.DynCCfgRow1TextView01;
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
            lloginfo("Read MB Cfg : " +  "Error cmd answer", receiveLog);
            // Upodate text in TAB
            // update the register text values .....MBEN
            int id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView06:R.id.DynCCfgRow1TextView06;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....HostPutMsg
            id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView05:R.id.DynCCfgRow1TextView05;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....RFPutMsg
            id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView04:R.id.DynCCfgRow1TextView04;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....HostMissMsg
            id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView03:R.id.DynCCfgRow1TextView03;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....RFMissMsg
            id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView02:R.id.DynCCfgRow1TextView02;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);
            // update the register text values .....CurrentMsg
            id_tt = this.currentCfgSelection == 0? R.id.StaticCfgRow1TextView01:R.id.DynCCfgRow1TextView01;
            ((TextView) this.findViewById(id_tt)).setTextColor(Color.YELLOW);

        }
    }

    private void MBWDcfgUpdate(byte[] answer) {
        int id_tt = R.id.myNumberWatchDog;
        if (answer != null) {
            Toast.makeText(getApplicationContext(),
                    "WD Cfg : " + Helper.ConvertHexByteArrayToString(answer), Toast.LENGTH_LONG).show();
            lloginfo("WD Cfg : " + Helper.ConvertHexByteArrayToString(answer), receiveLog);
            if (answer.length == 1) {
                lloginfo("WD Cfg : " + "No data", receiveLog);
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
            Toast.makeText(getApplicationContext(), "WD Cfg : " + "Error cmd answer", Toast.LENGTH_LONG)
                    .show();
            lloginfo("WD Cfg : " + "Error cmd answer", receiveLog);
            // Upodate text in TAB
            // update the register text values .....MBEN

        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        String action = intent.getAction();
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
            || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
            || (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)))
        {
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
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    protected void onPause() {
        cpt = 500;
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }



}