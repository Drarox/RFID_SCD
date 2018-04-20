// THE PRESENT FIRMWARE WHICH IS FOR GUIDANCE ONLY AIMS AT PROVIDING CUSTOMERS 
// WITH CODING INFORMATION REGARDING THEIR PRODUCTS IN ORDER FOR THEM TO SAVE 
// TIME. AS A RESULT, STMICROELECTRONICS SHALL NOT BE HELD LIABLE FOR ANY 
// DIRECT, INDIRECT OR CONSEQUENTIAL DAMAGES WITH RESPECT TO ANY CLAIMS 
// ARISING FROM THE CONTENT OF SUCH FIRMWARE AND/OR THE USE MADE BY CUSTOMERS 
// OF THE CODING INFORMATION CONTAINED HEREIN IN CONNECTION WITH THEIR PRODUCTS.

package com.st.demo; 

import com.st.NFC.NFCAppHeaderFragment;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import com.st.nfcv.Helper;
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcm24LRBasicOperation;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.util.Log;

public class BasicWriteLRActivity extends FragmentActivity
{
    EditText value1; 
    EditText value2;
    EditText value3;
    EditText value4; 
    EditText valueBlock;
    Button buttonWrite;
    Button buttonClear;
    
    private boolean Value1Enable = true;
    private boolean Value2Enable = true;
    private boolean Value3Enable = true;
    private boolean Value4Enable = true;
    
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    private String blockName = null;
    private String blockValue = null;
    private long cpt = 0;
    
    byte[] GetSystemInfoAnswer = null;
    private byte[] WriteSingleBlockAnswer = null;
    private byte [] addressStart = null;
    private byte[] dataToWrite = new byte[4];

    private static String GET_BLOCK_NAME = "blockname";
    private static String GET_BLOCK_VALUE = "blockvalue";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_write);
        String memorySize ;
        
        Bundle objetbunble  = this.getIntent().getExtras();

        //DataDevice dataDevice = (DataDevice)getApplication(); 
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
        memorySize = sysHDL.getMemorySize();
        

        //Get data from bundle
        if (objetbunble != null && objetbunble.containsKey(GET_BLOCK_NAME) && objetbunble.containsKey(GET_BLOCK_VALUE)) 
        {
            blockName = this.getIntent().getStringExtra(GET_BLOCK_NAME);
            blockValue = this.getIntent().getStringExtra(GET_BLOCK_VALUE);
            //Used for DEBUG : Log.i("ERROR == " + blockName, "ERROR == " + blockValue);
        }
        else //Error
        {
            blockName = null;
            blockValue = null;
        }

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mFilters = new IntentFilter[] {ndef,};
        mTechLists = new String[][] { new String[] { android.nfc.tech.NfcV.class.getName() } };

        initListener();
        
        if (blockName != null && blockValue != null)
        {
            String array[] =  blockValue.split(" ");
            value1.setText(Helper.FormatValueByteWrite(array[0]));
            value2.setText(Helper.FormatValueByteWrite(array[2]));
            value3.setText(Helper.FormatValueByteWrite(array[4]));
            value4.setText(Helper.FormatValueByteWrite(array[6]));

            array = blockName.split(" ");
            String tmp = Helper.FormatStringAddressStart(array[2], memorySize);
            valueBlock.setText(tmp.toUpperCase());
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

        
/*        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);     
        DataDevice ma = (DataDevice)getApplication();
        ma.setCurrentTag(tagFromIntent);*/
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

    private void initListener()
    {
        value1 = (EditText) findViewById(R.id.etvalue1);
        value1.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value1.getSelectionStart();
                int aend = value1.getSelectionEnd();
                        
                String FieldValue = s.toString().toUpperCase();                
                
                if (Helper.checkDataHexa(FieldValue) == false) 
                {
                    value1.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value1.setSelection(astart-1, aend-1);
                }
                else
                    value1.setSelection(astart, aend);

                if (value1.getText().length() >0 && value1.getText().length() < 2)
                {
                    value1.setTextColor(0xffff0000); //RED color
                    buttonWrite.setClickable(false);
                    Value1Enable = false;
                }
                else
                {
                    value1.setTextColor(0xff000000); //BLACK color                    
                    Value1Enable = true;
                    if (Value1Enable == true &&
                        Value2Enable == true &&
                        Value3Enable == true &&
                        Value4Enable == true)                            
                            buttonWrite.setClickable(true);
                        
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
        
        value2 = (EditText) findViewById(R.id.etvalue2);
        value2.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value2.getSelectionStart();
                int aend = value2.getSelectionEnd();
                        
                String FieldValue = s.toString().toUpperCase();                                
                
                if (Helper.checkDataHexa(FieldValue) == false) 
                {
                    value2.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value2.setSelection(astart-1, aend-1);
                }
                else
                    value2.setSelection(astart, aend);
                
                if (value2.getText().length() >0 && value2.getText().length() < 2)
                {
                    value2.setTextColor(0xffff0000); //RED color
                    buttonWrite.setClickable(false);
                    Value2Enable = false;
                }
                else
                {
                    value2.setTextColor(0xff000000); //BLACK color
                    Value2Enable = true;
                    if (Value1Enable == true &&
                        Value2Enable == true &&
                        Value3Enable == true &&
                        Value4Enable == true)                            
                            buttonWrite.setClickable(true);
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
        
        value3 = (EditText) findViewById(R.id.etvalue3);
        value3.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value3.getSelectionStart();
                int aend = value3.getSelectionEnd();
                        
                String FieldValue = s.toString().toUpperCase();                                
                
                if (Helper.checkDataHexa(FieldValue) == false) 
                {
                    value3.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value3.setSelection(astart-1, aend-1);
                }
                else
                    value3.setSelection(astart, aend);
                
                if (value3.getText().length() >0 && value3.getText().length() < 2)
                {
                    value3.setTextColor(0xffff0000); //RED color
                    buttonWrite.setClickable(false);
                    Value3Enable = false;
                }
                else
                {
                    value3.setTextColor(0xff000000); //BLACK color
                    Value3Enable = true;
                    if (Value1Enable == true &&
                        Value2Enable == true &&
                        Value3Enable == true &&
                        Value4Enable == true)                            
                            buttonWrite.setClickable(true);
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
        
        value4 = (EditText) findViewById(R.id.etvalue4);
        value4.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        value4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = value4.getSelectionStart();
                int aend = value4.getSelectionEnd();
                        
                String FieldValue = s.toString().toUpperCase();                                
                
                if (Helper.checkDataHexa(FieldValue) == false) 
                {
                    value4.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    value4.setSelection(astart-1, aend-1);
                }
                else
                    value4.setSelection(astart, aend);
                
                if (value4.getText().length() >0 && value4.getText().length() < 2)
                {
                    value4.setTextColor(0xffff0000); //RED color
                    buttonWrite.setClickable(false);
                    Value4Enable = false;
                }
                else
                {
                    value4.setTextColor(0xff000000); //BLACK color
                    Value4Enable = true;
                    if (Value1Enable == true &&
                        Value2Enable == true &&
                        Value3Enable == true &&
                        Value4Enable == true)                            
                            buttonWrite.setClickable(true);
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
        
        valueBlock = (EditText) findViewById(R.id.etBlock);
        valueBlock.setInputType(android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        valueBlock.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                int astart = valueBlock.getSelectionStart();
                int aend = valueBlock.getSelectionEnd();
                        
                String FieldValue = s.toString().toUpperCase();
                if (Helper.checkDataHexa(FieldValue) == false) 
                {
                    valueBlock.setTextKeepState(Helper.checkAndChangeDataHexa(FieldValue));
                    valueBlock.setSelection(astart-1, aend-1);
                }
                else
                    valueBlock.setSelection(astart, aend);
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
        
        buttonWrite = (Button) findViewById(R.id.button_writing);
        buttonClear = (Button) findViewById(R.id.button_clear);

        buttonWrite.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
/*                // TODO Auto-generated method stub
                new StartWriteTask().execute();
                //Used for DEBUG : Log.i("Write", "SUCCESS");
*/                
                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();
                SysFileLRHandler sysHDL = (SysFileLRHandler) (currentTag.getSYSHandler());
                if (sysHDL.getMemorySize() != null) {
                    String tmpmemsize = sysHDL.getMemorySize();
                    processPreExecute(sysHDL.getMemorySize());
                    stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHDL.getMaxTransceiveLength());
                    if (bop.m24LRWriteBasicOp(addressStart, dataToWrite) == 0) {
                        // ok
                    } else {
                        // ko
                        
                    }
                } else {    
                    Toast.makeText(getApplicationContext(), "Invalid parameters, Tag memory size issue", Toast.LENGTH_LONG).show();
                }

            }
        });
        
        buttonClear.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                value1.setText("");
                value2.setText("");
                value3.setText("");
                value4.setText("");
                valueBlock.setText("");
                
                Value1Enable = true;
                Value2Enable = true;
                Value3Enable = true;
                Value4Enable = true;
                buttonWrite.setClickable(true);
            }
        });
    }

    
    private void processPreExecute (String memorySize) {
        String startAddressString = valueBlock.getText().toString(); 
        startAddressString = Helper.castHexKeyboard(startAddressString);
        startAddressString = Helper.FormatStringAddressStart(startAddressString, memorySize);
        valueBlock.setText(startAddressString.toUpperCase());
        addressStart = Helper.ConvertStringToHexBytes(startAddressString);

        String valueBlock1 = value1.getText().toString();
        String valueBlock2 = value2.getText().toString();
        String valueBlock3 = value3.getText().toString();
        String valueBlock4 = value4.getText().toString();
        
        if(valueBlock1.length() == 0)
            valueBlock1 = "00";
        if(valueBlock2.length() == 0)
            valueBlock2 = "00";
        if(valueBlock3.length() == 0)
            valueBlock3 = "00";
        if(valueBlock4.length() == 0)
            valueBlock4 = "00";
        
        value1.setText(Helper.FormatValueByteWrite(valueBlock1));
        value2.setText(Helper.FormatValueByteWrite(valueBlock2));
        value3.setText(Helper.FormatValueByteWrite(valueBlock3));
        value4.setText(Helper.FormatValueByteWrite(valueBlock4));

        String valueBlockTotal = "";
        valueBlockTotal += valueBlock1 + valueBlock2;
        byte[] valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);
        
        dataToWrite[0] = valueBlockWrite[0];
        dataToWrite[1] = valueBlockWrite[1];
        
        valueBlockTotal = "";
        valueBlockTotal += valueBlock3 + valueBlock4;
        valueBlockWrite = Helper.ConvertStringToHexBytes(valueBlockTotal);
        
        dataToWrite[2] = valueBlockWrite[0];
        dataToWrite[3] = valueBlockWrite[1];
    
    }
    

     
}