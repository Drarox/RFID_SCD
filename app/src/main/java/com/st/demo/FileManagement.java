// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import com.st.nfcv.Helper;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcm24LRBasicOperation;

import java.io.FileOutputStream;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.util.Log;

public class FileManagement extends FragmentActivity {

    Button buttonLoadFromFile;
    Button buttonSaveInFile;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    private String blockName = null;
    private String blockValue = null;
    private long cpt = 0;

    byte[] GetSystemInfoAnswer = null;

    private byte[] WriteSingleBlockAnswer = null;
    private byte[] ReadMultipleBlockAnswer = null;

    byte[] numberOfBlockToRead = null;

    private byte[] addressStart = null;
    private byte[] dataToWrite = new byte[4];

    private byte[] bufferFile = null;;
    private int blocksToWrite = 0;

    private boolean FileError = false;

    private EditText textLoadFileName;
    private EditText textSaveFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_management_menu);

        Bundle objetbunble = this.getIntent().getExtras();
        // DataDevice dataDevice = (DataDevice)getApplication();
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[] { ndef, };
        mTechLists = new String[][] { new String[] { android.nfc.tech.NfcV.class.getName() } };

        FileError = false;

        initListener();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        String action = intent.getAction();
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
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
        cpt = 500;
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    private void initListener()
    {

        textSaveFileName = (EditText) findViewById(R.id.editTextSaveFileName);
        textSaveFileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = textSaveFileName.getSelectionStart();
                int aend = textSaveFileName.getSelectionEnd();

                String FieldValue = s.toString();//.toUpperCase();

                if (FieldValue.length() == 0)
                {
                    textSaveFileName.setTextColor(0xffff0000); //RED color
                    buttonSaveInFile.setClickable(false);
                }
                else
                {
                    textSaveFileName.setTextColor(0xff000000); //BLACK color
                    buttonSaveInFile.setClickable(true);

                }

                if (Helper.checkFileName(FieldValue) == false)
                {
                    textSaveFileName.setTextKeepState(Helper.checkAndChangeFileName(FieldValue));
                    textSaveFileName.setSelection(astart-1, aend-1);
                }
                else
                    textSaveFileName.setSelection(astart, aend);
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

        textLoadFileName = (EditText) findViewById(R.id.editTextLoadFileName);
        //value1.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        textLoadFileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = textLoadFileName.getSelectionStart();
                int aend = textLoadFileName.getSelectionEnd();

                String FieldValue = s.toString();//.toUpperCase();

                if (FieldValue.length() == 0)
                {
                    textLoadFileName.setTextColor(0xffff0000); //RED color
                    buttonLoadFromFile.setClickable(false);
                }
                else
                {
                    textLoadFileName.setTextColor(0xff000000); //BLACK color
                    buttonLoadFromFile.setClickable(true);

                }

                if (Helper.checkFileName(FieldValue) == false)
                {
                    textLoadFileName.setTextKeepState(Helper.checkAndChangeFileName(FieldValue));
                    textLoadFileName.setSelection(astart-1, aend-1);
                }
                else
                    textLoadFileName.setSelection(astart, aend);
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

        buttonSaveInFile = (Button) findViewById(R.id.SaveInFileButton);
        buttonLoadFromFile = (Button) findViewById(R.id.LoadFromFileButton);

        buttonSaveInFile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textSaveFileName.getApplicationWindowToken(), 0);

                //new StartSaveInFileTask().execute();
                //Used for DEBUG : Log.i("Write", "SUCCESS");
                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();
                SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
                if (sysHDL.getMemorySize() != null) {
                    String tmpmemsize = sysHDL.getMemorySize();
                    processSaveInFilePreExecute(sysHDL.getMemorySize());
                    stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength());
                    if (bop.m24LRReadBasicOp(addressStart, numberOfBlockToRead,tmpmemsize) == 0) {
                        // ok
                        processSaveInFilePostExecute(bop.getReadMultipleBlockAnswer(),tmpmemsize);
                    } else {
                        // ko
                        processSaveInFilePostExecute(null,tmpmemsize);

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid parameters, File or Tag memory size issue", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonLoadFromFile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub

                //Close Keyboard before stating activity
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textLoadFileName.getApplicationWindowToken(), 0);

                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();
                SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
                if (sysHDL.getMemorySize() != null) {
                    String tmpmemsize = sysHDL.getMemorySize();
                    if (processLoadFromFilePreExecute(sysHDL.getMemorySize()) == false) {
                        stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength());
                        addressStart = Helper.ConvertIntTo2bytesHexaFormat(0x00);
                        if (bop.m24LRWriteMemoryBasicOp(addressStart, bufferFile, blocksToWrite) == 0) {
                            // ok
                        } else {
                            // ko

                        }
                    } else {

                        Toast.makeText(getApplicationContext(), "Invalid parameters, File size issue", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid parameters, Tag memory size issue", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void processSaveInFilePreExecute(String memorySize) {

        String memsTemp = memorySize;
        if (memsTemp != null) {
            memsTemp = com.st.nfcv.Helper.StringForceDigit(memorySize, 4);
         }

        int MemorySizeBlocksNumber = (Helper.ConvertStringToInt((memsTemp.replace(" ", ""))));

        addressStart = Helper.ConvertIntTo2bytesHexaFormat(0x00);
        //numberOfBlockToRead = Helper.ConvertIntTo2bytesHexaFormatBis(MemorySizeBlocksNumber);
        numberOfBlockToRead = Helper.ConvertIntTo2bytesHexaFormat(Integer.parseInt(memsTemp));

        // String strSaveFileNameTest = textSaveFileName.getText().toString();

    }

    private void processSaveInFilePostExecute(byte[] ReadMultipleBlockAnswer, String mMemorySize) {
        String memsTemp = mMemorySize;
        if (memsTemp != null) {
            memsTemp = com.st.nfcv.Helper.StringForceDigit(mMemorySize, 4);
        }
        //int MemorySizeBlocksNumber = (Helper.ConvertStringToInt((memsTemp.replace(" ", ""))) + 1);
        int MemorySizeBlocksNumber = (Integer.parseInt(memsTemp));

        if (ReadMultipleBlockAnswer != null && ReadMultipleBlockAnswer.length - 1 > 0) {
            int MemorySizeBytes = MemorySizeBlocksNumber * 4;

            if (ReadMultipleBlockAnswer[0] == (byte) 0x00 && ReadMultipleBlockAnswer.length > MemorySizeBytes) {
                byte DataRead[] = new byte[MemorySizeBytes];
                int i = 1;
                while (i <= MemorySizeBytes) {
                    DataRead[i - 1] = ReadMultipleBlockAnswer[i];
                    i++;
                }

                if (textSaveFileName.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "File name error, data not saved ", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    try {

                        File folder = new File(Environment.getExternalStorageDirectory(), "ST25DemoApp");
                        if (!folder.exists())
                            folder.mkdir();

                        String strSaveFileName = textSaveFileName.getText().toString();

                        FileOutputStream fileIS;
                        fileIS = new FileOutputStream(Environment.getExternalStorageDirectory() + "/ST25DemoApp/"
                                + strSaveFileName.replace(" ", ""));

                        fileIS.write(DataRead);

                    } catch (IOException e) {
                        FileError = true;
                        Toast toast = Toast.makeText(getApplicationContext(), "File cannot be created, data not saved ",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        // finish();
                    }
                }

            }
        } else // read fail
        {

            Toast.makeText(getApplicationContext(), "ERROR Tag Read - file not saved (no Tag answer) ",
                    Toast.LENGTH_LONG).show();
        }

    }

    private boolean processLoadFromFilePreExecute(String memorySize) {
        String formattedMemorySize = Helper.StringForceDigit(memorySize, 4);
        int nbBlocks = Integer.valueOf(formattedMemorySize.replace(" ", ""));
        int MemorySizeBytes = (nbBlocks + 1) * 4;

        FileError = false;

        // read binary file
        try {
            File folder = new File(Environment.getExternalStorageDirectory(), "ST25DemoApp");
            if (!folder.exists())
                folder.mkdir();

            String strFileName = textLoadFileName.getText().toString();

            File f = new File(
                    Environment.getExternalStorageDirectory() + "/ST25DemoApp/" + strFileName.replace(" ", ""));
            FileInputStream fileIS = new FileInputStream(f);
            BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
            int fileSize = (int) f.length();

            if ((fileSize % 4) > 0) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "File format error : multiple of 4 bytes need (4 bytes per block)", Toast.LENGTH_SHORT);
                toast.show();
                FileError = true;
                // finish();
            } else if (fileSize > MemorySizeBytes) {
                Toast toast = Toast.makeText(getApplicationContext(), "File too big for your memory Tag",
                        Toast.LENGTH_SHORT);
                toast.show();
                FileError = true;
                // finish();
            } else if (fileSize == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "File empty", Toast.LENGTH_SHORT);
                toast.show();
                FileError = true;
                // finish();
            } else {
                bufferFile = new byte[MemorySizeBytes];
                fileIS.read(bufferFile);
                blocksToWrite = (int) fileSize / 4;
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "File not found or not formated as binary file",
                    Toast.LENGTH_SHORT);
            toast.show();
            FileError = true;
            // finish();
        }
        return FileError;

    }



}