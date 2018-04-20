// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.st.MB.FTMUseCaseChronometer;
import com.st.MB.FTMUseCaseGen;
import com.st.MB.MBFWUListenerDataSent;
import com.st.MB.MBTransferListenerFWU;
import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.demo.FastTransferActivity.ActionMBDemo;
import com.st.nfcv.BasicOperation;
import com.st.nfcv.Helper;
import com.st.nfcv.MBCommandV;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcRegisterHandler.ST25DVRegisterTable;
//import android.util.Log;

public class FTMChronometer extends FragmentActivity implements AdapterView.OnItemSelectedListener {
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


    FTMUseCaseGen mFTMUseCaseChronometer;


    private ActionMBDemo mMBAction = ActionMBDemo.MB_HR_FW_EXCHANGE;
    private int mFWArraySize = 0;


    final Context context = this;

    public TextView counterFrameTV;
    public long counterFrame ;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronometer_xfers);

        sendLog = (TextView) findViewById(R.id.Text_log_send_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());
        receiveLog = (TextView) findViewById(R.id.Text_log_receive_ID);
        sendLog.setMovementMethod(new ScrollingMovementMethod());

        // Logs

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


        initListenerActionCfg();

        initListener();

        mHandler = new Handler();

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

            default:
        }
    }

    public void onBackPressed() {

        super.onBackPressed();
        stop();
    }


    private Chronometer mChronometer;
    private long mTimeElapsed;
    private long mLastTimeUpdate;
    EventLogInfoCallBack log;

    private void initListener() {
        mChronometer = (Chronometer) findViewById(R.id.st25DvChronometer);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer chronometer) {
                mTimeElapsed = chronometer.getTimeElapsed();
            }
        });

        log = new EventLogInfoCallBack(receiveLog, sendLog);

        Button startButton = (Button) findViewById(R.id.startChronoButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        Button stopButton = (Button) findViewById(R.id.stopChronoButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        Button resumeButton = (Button) findViewById(R.id.resumeChronoButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resume();
            }
        });

        Button pauseButton = (Button) findViewById(R.id.pauseChronoButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        counterFrameTV = (TextView) findViewById(R.id.TVbininfoFrame);

    }

    void updateCounterOfFrames(long cpt) {
        final String str = String.valueOf(cpt);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                TextView tv = (TextView) findViewById(R.id.TVbininfoFrame);
                tv.setText(str);
            }
        });


    }


    private XfersChrono xFersChrono = null;

    public enum ChronoTransferEvent {
        START,
        STOP,
        PAUSE,
        RESUME
    }

    private final int SLEEP_TIME = 20; //ms

    public class XfersChrono implements Runnable {

        ChronoTransferEvent mEvent = ChronoTransferEvent.START;

        public void stop() {
            mEvent = ChronoTransferEvent.STOP;
        }

        public void resume() {
            mEvent = ChronoTransferEvent.RESUME;
        }

        public void start() {
            mEvent = ChronoTransferEvent.START;
        }

        public void pause() {
            mEvent = ChronoTransferEvent.PAUSE;
        }

        public void run() {

            while (mEvent == ChronoTransferEvent.START || mEvent == ChronoTransferEvent.RESUME ||
                    mEvent == ChronoTransferEvent.PAUSE) {
                if (mEvent != ChronoTransferEvent.PAUSE) {
                    synchronized (this) {
                        byte[] payload = getDataToWrite();
                        if (payload != null && payload.length == 3) {
                            FTMUseCaseChronometer chrono = new FTMUseCaseChronometer(payload);
                            // for the end of transfrt
                            chrono.addListener(log);
                            chrono.execute();
                            SystemClock.sleep(SLEEP_TIME);
                            updateCounterOfFrames(counterFrame);
                        }

                    }
                } else {
                    SystemClock.sleep(SLEEP_TIME * 5);
                }
            }
        }
    }


    public void start() {
        mChronometer.start();
        this.counterFrame = 0;
        updateCounterOfFrames(this.counterFrame);
        xFersChrono = new XfersChrono();
        new Thread(xFersChrono).start();

    }


    public void resume() {
        mChronometer.resume();
        if (xFersChrono != null) xFersChrono.resume();
        updateCounterOfFrames(this.counterFrame);

     }

    public void pause() {
        mChronometer.pause();
        if (xFersChrono != null) xFersChrono.pause();
        updateCounterOfFrames(this.counterFrame);
    }

    public void stop() {
        mChronometer.stop();
        if (xFersChrono != null) xFersChrono.stop();
        updateCounterOfFrames(this.counterFrame);

    }

    public void incrementCounterFrame() {
        counterFrame++;
    }

    public byte[] getDataToWrite() {
        byte[] data = new byte[3];
        long time = mChronometer.getTimeElapsed();

        int remaining = (int) (time % (3600 * 1000));

        int minutes = (int) (remaining / (60 * 1000));
        remaining = (int) (remaining % (60 * 1000));

        int seconds = (int) (remaining / 1000);
        remaining = (int) (remaining % (1000));

        int hundredsseconds = (int) (((int) time % 1000) / 10);

        data[0] = (byte) (minutes & 0xFF);
        data[1] = (byte) (seconds  & 0xFF);
        data[2] = (byte) (hundredsseconds & 0xFF);

        mLastTimeUpdate = time;
        return data;
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
            if (error > 0) {
                //xFersChrono.stop();
                //stop();
                //logGenericEventInformation("Error detected : " + error +  " - Chrono ended " , false, Color.RED);
            } else {
                incrementCounterFrame();
            }

        }

        @Override
        public void FWUdataSent(long size) {
            // TODO Auto-generated method stub

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


    // ============================================ Chronometer



    Button buttonWriteFromFile;
    Button buttonPresentPassword;

    Button buttonPasswordFWU;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        clearLogInformation(sendLog);
        clearLogInformation(receiveLog);



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }


 }