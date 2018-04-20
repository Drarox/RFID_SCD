/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1257 $
  * Revision of last commit    :  $Rev: 1257 $
  * Date of last commit     :  $Date: 2015-10-22 16:02:56 +0200 (Thu, 22 Oct 2015) $
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

import com.st.demo.R;
import com.st.Fragments.DVRegisters.EditTextWithOneChoiceDVEHBehaviour;
import com.st.Fragments.DVRegisters.EditTextWithOneChoiceDVITBehaviour;
import com.st.Fragments.DVRegisters.EditTextWithXChoiceDVRFMngtBehaviour;
import com.st.Fragments.DVRegisters.EditTextWithXChoiceDVGPOBehaviour;
import com.st.nfcv.Helper;
import com.st.nfcv.NFCCommandVExtended;
import com.st.nfcv.stnfcRegisterHandler;
import com.st.nfcv.stnfcRegisterHandler.ST25DVRegisterTable;
import com.st.nfcv.stnfcm24LRBasicOperation;
import com.st.nfcv.SysFileLRHandler;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.SysFileGenHandler;
import com.st.util.EditTextWithXChoiceCustomBehaviour;
import com.st.Fragments.DVRegisters.EditTextWithXChoiceDVRFiZSSBehaviour;
import com.st.Fragments.DVRegisters.EditTextWithXChoiceDVRFiZSSBehaviourRest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuSYSFileLRFragment extends NFCPagerFragment {
    // Store current tag
    // private NFCTag _curNFCTag = null;
    // Store view corresponding to current fragment
    private View _curFragmentView = null;
    private RadioButton rbOptionCfgIC;
    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuNDEFFilesFragment.
     */
    public static MenuSYSFileLRFragment newInstance(NFCTag mNFCTag) {
        MenuSYSFileLRFragment fragment = new MenuSYSFileLRFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }

    public static MenuSYSFileLRFragment newInstance(NFCTag mNFCTag, int page, String title) {
        MenuSYSFileLRFragment fragment = new MenuSYSFileLRFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }


    public MenuSYSFileLRFragment() {
        // Required empty public constructor
    }

    public void setNFCTag(NFCTag mNFCTag) {
        NFCApplication.getApplication().setCurrentTag(mNFCTag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreateView Fragment");

        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_sys_file_lr, container, false);

        // Create the nested fragments (tag header one)
        FragmentManager fragMng = getChildFragmentManager();
        // First check if the fragment already exists (in case current fragment
        // has been temporarily destroyed)
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) fragMng
                .findFragmentById(R.id.SYSFileFragTagHeaderFragmentId);
        if (mTagHeadFrag == null) {
            mTagHeadFrag = new TagHeaderFragment();
            FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.add(R.id.SYSFileFragTagHeaderFragmentId, mTagHeadFrag);
            transaction.commit();
            fragMng.executePendingTransactions();
        }


        initRegisterListener();
        initRegisterListenerList();

        initICConfigSelection();


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

    @Override
    public void onResume() {
        Log.v(this.getClass().getName(), "onResume Fragment");
        super.onResume();

        // Fill in the layout with the currentTag
        //onTagChanged(NFCApplication.getApplication().getCurrentTag());

    }

    private void showFragmentContentNotAvailable() {
        // Unhide "Not available" text view
        TextView curTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragNotAvailableId);
        curTxtView.setVisibility(View.VISIBLE);
        // Hide all other elements (encapsulated in a single RelativeLayout)
        RelativeLayout availLayout = (RelativeLayout) _curFragmentView
                .findViewById(R.id.SYSFileFragAvailFileRelLayout);
        availLayout.setVisibility(View.GONE);

    }
    private void showFragmentContentAvailable() {
        // Hide "Not available" text view
        TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragNotAvailableId);
        commonTxtView.setVisibility(View.GONE);
        // Unhide all other elements (encapsulated in a single
        // RelativeLayout)
        RelativeLayout availLayout = (RelativeLayout) _curFragmentView
                .findViewById(R.id.SYSFileFragAvailFileRelLayout);
        availLayout.setVisibility(View.VISIBLE);

    }

    public void onTagChanged(NFCTag newTag) {
        // Update instance attribute
        RelativeLayout layout;

        if (newTag == null)
        {
            newTag = NFCApplication.getApplication().getCurrentTag();
        }
        else
        {
            NFCApplication.getApplication().setCurrentTag(newTag);
        }
        // Set the layout content according to the content of the tag
        // - Tag header
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getChildFragmentManager()
                .findFragmentById(R.id.SYSFileFragTagHeaderFragmentId);

        if (mTagHeadFrag != null) {
            mTagHeadFrag.onTagChanged(newTag);
        }
        // - Check if SYS File is available for current tag
        SysFileGenHandler sysfilehandler;
        //SysFileLRHandler tagSYSHandler = (SysFileLRHandler)(newTag.getSYSHandler());
        sysfilehandler = newTag.getSYSHandler();
        if (sysfilehandler == null || !(sysfilehandler instanceof SysFileLRHandler)) {
            showFragmentContentNotAvailable();
        } else {
            SysFileLRHandler tagSYSHandler = (SysFileLRHandler)(sysfilehandler);
            TextView commonTxtView;
            String commonStr;

            showFragmentContentAvailable();

            // Set the fields of SYSTEM File
            // SYS File Length
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragSYSLgthFieldId);
            commonStr = String.valueOf(tagSYSHandler.getSYSLength()) + " "
                    + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt);
            commonTxtView.setText(commonStr);

            // UID
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragUIDFieldId);
            if (tagSYSHandler.getUid()!= null) {
                commonTxtView.setText(tagSYSHandler.getUid().toUpperCase());
            } else {
                commonTxtView.setText("UID not FOUND");
            }

            // Memory Size
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragMemSizeFieldId);
            if (newTag.getModel().contains("ST25DV")) {
                if (newTag.getMemSize() != 0) {
                    commonStr = String.valueOf(newTag.getMemSize()) + " "
                            + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt) + " ("
                            + String.format("0x%04X", newTag.getMemSize()) + ")";


                } else {
                    commonStr = " "
                            + getString(R.string.ti_act_add_txt_memory_size_unknown) + " ("
                            + String.format("0x%04X", newTag.getMemSize()) + ")";

                }
            } else {
                int mem = tagSYSHandler.getMconverterMemSize();
                commonStr = String.valueOf(mem) + " "
                        + getString(R.string.mnf_frag_NDEF_NLEN_suffix_txt) + " ("
                        + String.format("0x%04X", mem) + ")";
            }
            commonTxtView.setText(commonStr);


            // Product Code
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragProdCodeFieldId);
            if (tagSYSHandler.getIcReference() != null) {
                commonStr = tagSYSHandler.getIcReference().toUpperCase() + " (" + newTag.getModel() + ")";
            } else {
                commonStr =  " (" + newTag.getModel() + ")";
            }

            commonTxtView.setText(commonStr);

            // DSFID Code
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragDSFIdFieldId);
            //commonStr = tagSYSHandler.getDsfid().toUpperCase();
            if (tagSYSHandler.getDsfid() != null) {
                commonStr = tagSYSHandler.getDsfid().toUpperCase();
            } else {
                commonStr =  "xx";
            }

            commonTxtView.setText(commonStr);

            // AFI Code
            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragAFIFieldId);
            //commonStr = tagSYSHandler.getAfi().toUpperCase();
            if (tagSYSHandler.getAfi() != null) {
                commonStr = tagSYSHandler.getAfi().toUpperCase();
            } else {
                commonStr =  "xx";
            }
            commonTxtView.setText(commonStr);

            int iTemp = 0;
            String sTemp = null;
            iTemp = tagSYSHandler.getMemoryNbOfBlocks();
            // Memory map NB Blocks
 /*           if (newTag.getModel().contains("ST25DV")) {
                iTemp = tagSYSHandler.getMemoryNbOfBlocks();

            } else {
                sTemp = tagSYSHandler.getMemorySize();
                if (sTemp != null) {
                    sTemp = com.st.nfcv.Helper.StringForceDigit(sTemp, 4);
                    iTemp = Integer.valueOf(sTemp);
                    iTemp++;
                }
            }*/
            commonTxtView = (TextView) _curFragmentView
                    .findViewById(R.id.SYSFileFragMemoryMapNbBlocksFieldActFieldId);
            commonStr = String.valueOf(iTemp);
            commonTxtView.setText(commonStr);

            // Memory map bytes per blocks
            iTemp = 0;
            sTemp = null;
            sTemp = tagSYSHandler.getBlockSize();
            sTemp = com.st.nfcv.Helper.StringForceDigit(sTemp, 4);
            if (sTemp != null) {
                iTemp = com.st.nfcv.Helper.ConvertStringToInt(sTemp);
                iTemp++;
                sTemp = com.st.nfcv.Helper.ConvertHexByteToString((byte) iTemp);
            }

            commonTxtView = (TextView) _curFragmentView.findViewById(R.id.SYSFileFragMemoryMapBytesBlocksFieldId);
            commonStr = sTemp;
            commonTxtView.setText(commonStr);

            // Hide the Registers
            RelativeLayout gLayout;
            if (newTag.getModel().contains("ST25DV")) {
                gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragAvailRegisterRelLayout);
                gLayout.setVisibility(View.VISIBLE);
                SysFileLRHandler sysHDL = (SysFileLRHandler) (newTag.getSYSHandler());
                updateRegisterUIContent(sysHDL.mST25DVRegister);
            } else {
                // Hide the Registers
                gLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.SYSFileFragAvailRegisterRelLayout);
                gLayout.setVisibility(View.GONE);

            }

        }

        // Set footnote
        TextView tmFootNoteTxt = (TextView) _curFragmentView.findViewById(R.id.FootNoteTxtId);
        tmFootNoteTxt.setText(newTag.getFootNote());
    }

    private int ICorFPGA  = 0;
    private void initICConfigSelection(){
        ICorFPGA = 0;
        RadioButton ICRadioButton = (RadioButton) _curFragmentView.findViewById(R.id.cfgV1);
        RadioButton FPGARadioButton = (RadioButton) _curFragmentView.findViewById(R.id.cfgV2);
        if (ICRadioButton.isChecked()) {
            ICorFPGA = 0;
        }
        if (FPGARadioButton.isChecked()) {
            ICorFPGA = 1;
        }
        TextView cmdVersion = (TextView)_curFragmentView.findViewById(R.id.TextViewCmdVersionUsed);
        cmdVersion.setText(NFCCommandVExtended.CMD_VERSION_BUILD);
    }
    public void onRadioButtonICClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.cfgV1:
                if (checked)
                    ICorFPGA = 0;
                break;
            case R.id.cfgV2:
                if (checked)
                    ICorFPGA = 1;
                break;
        }
    }






private Toast WFNFCTAPToast;
private void toastStatus(String status) {

    // Create Toast to inform user on the Tool request process.
    Context context = NFCApplication.getApplication().getApplicationContext();
    int duration = Toast.LENGTH_SHORT;
    WFNFCTAPToast = Toast.makeText(context, status, duration);
    WFNFCTAPToast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
    WFNFCTAPToast.show();

}
    private void initRegisterListenerList() {
        Button button = (Button) _curFragmentView.findViewById(R.id.BTRegisterRefresh);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NFCApplication currentApp = NFCApplication.getApplication();
                NFCTag currentTag = currentApp.getCurrentTag();

                if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                    if (currentTag != null) {
                        SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
                        if (sysHL != null && sysHL.mST25DVRegister != null) {
                            if (sysHL.mST25DVRegister.readAllSystemRegister(currentTag.getTag(), sysHL)) {
                                Log.v(this.getClass().getName(), "cmd readAllSystemRegister succeed .. ");
                                toastStatus("Register values retrieved ...");

                            } else {
                                Log.v(this.getClass().getName(), "cmd readAllSystemRegister failed .. ");
                                toastStatus("Register values retrieval failed ...");

                            }
                            updateRegisterUIContent(sysHL.mST25DVRegister);
                            initRegisterListener();

                        }else {
                            Log.v(this.getClass().getName(), "cmd readAllSystemRegister failed .. ");
                            toastStatus("System file retrieval issue ...");
                        }
                    } else {
                        Log.v(this.getClass().getName(), "cmd readAllSystemRegister failed .. ");
                        toastStatus("Tag not on the field ...");
                    }
                }
            }
        });

    }


    EditTextWithOneChoiceDVITBehaviour editTextWithOneChoiceDVITBehaviour;
    EditTextWithXChoiceDVGPOBehaviour editTextWithDVGPOBehaviour;
    EditTextWithOneChoiceDVEHBehaviour editTextWithOneChoiceDVEHBehaviour;
    EditTextWithXChoiceDVRFMngtBehaviour editTextWithOneChoiceDVRFMngtBehaviour;

    TextView commonTxtViewRF1ZSSValue;
    TextView commonTxtViewRF2ZSSValue;
    TextView commonTxtViewRF3ZSSValue;
    TextView commonTxtViewRF4ZSSValue;

    EditTextWithXChoiceCustomBehaviour textWithRFiZSSBehaviour;

private void initRegisterListener() {
    NFCApplication currentApp = NFCApplication.getApplication();
    NFCTag currentTag = currentApp.getCurrentTag();
    stnfcRegisterHandler reg = null;
    byte value = 0;

    if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
        SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
        if (sysHL.mST25DVRegister != null) {
            reg = sysHL.mST25DVRegister;
        }
    }

    Button button = (Button) _curFragmentView.findViewById(R.id.BTextViewGPOValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_GPO);
    editTextWithDVGPOBehaviour = new EditTextWithXChoiceDVGPOBehaviour(_curFragmentView.getContext(),(TextView) _curFragmentView.findViewById(R.id.EditViewGPOValue),value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewGPOValue);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_GPO);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewitdurationValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_ITime);
    editTextWithOneChoiceDVITBehaviour = new EditTextWithOneChoiceDVITBehaviour(_curFragmentView.getContext(), (TextView) _curFragmentView.findViewById(R.id.EditViewitdurationValue),value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewitdurationValue);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_ITime);
        }
    });
    button = (Button) _curFragmentView.findViewById(R.id.BTextViewEHValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_EH);
    editTextWithOneChoiceDVEHBehaviour = new EditTextWithOneChoiceDVEHBehaviour(_curFragmentView.getContext(),(TextView) _curFragmentView.findViewById(R.id.EditViewEHValue),value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewEHValue);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_EH);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewRFMgtValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_Rfdis);
    editTextWithOneChoiceDVRFMngtBehaviour = new EditTextWithXChoiceDVRFMngtBehaviour(_curFragmentView.getContext(),(TextView) _curFragmentView.findViewById(R.id.EditViewRFMgtValue),value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewRFMgtValue);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_Rfdis);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewRF1ZSSValue);
    commonTxtViewRF1ZSSValue = (TextView) _curFragmentView.findViewById(R.id.EditViewRF1ZSSValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ1SS);
    textWithRFiZSSBehaviour = new EditTextWithXChoiceDVRFiZSSBehaviourRest(_curFragmentView.getContext(),commonTxtViewRF1ZSSValue,value );
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewRF1ZSSValue);
            processUpdateRegisterValue(commonTxtViewRF1ZSSValue, ST25DVRegisterTable.Reg_RFZ1SS);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewEndZ1Value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewEndZ1Value);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_End1);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewRF2ZSSValue);
    commonTxtViewRF2ZSSValue = (TextView) _curFragmentView.findViewById(R.id.EditViewRF2ZSSValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ2SS); else value = 0;
    textWithRFiZSSBehaviour = new EditTextWithXChoiceDVRFiZSSBehaviour(_curFragmentView.getContext(),commonTxtViewRF2ZSSValue,value );
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewRF2ZSSValue);
            processUpdateRegisterValue(commonTxtViewRF2ZSSValue, ST25DVRegisterTable.Reg_RFZ2SS);
        }
    });
    button = (Button) _curFragmentView.findViewById(R.id.BTextViewEndZ2Value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewEndZ2Value);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_End2);
        }
    });
    button = (Button) _curFragmentView.findViewById(R.id.BTextViewRF3ZSSValue);
    commonTxtViewRF3ZSSValue = (TextView) _curFragmentView.findViewById(R.id.EditViewRF3ZSSValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ3SS); else value = 0;
    textWithRFiZSSBehaviour = new EditTextWithXChoiceDVRFiZSSBehaviour(_curFragmentView.getContext(),commonTxtViewRF3ZSSValue,value );
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewRF3ZSSValue);
            processUpdateRegisterValue(commonTxtViewRF3ZSSValue, ST25DVRegisterTable.Reg_RFZ3SS);
        }
    });
    button = (Button) _curFragmentView.findViewById(R.id.BTextViewEndZ3Value);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewEndZ3Value);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_End3);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewRF4ZSSValue);
    commonTxtViewRF4ZSSValue = (TextView) _curFragmentView.findViewById(R.id.EditViewRF4ZSSValue);
    if (reg != null) value = (byte) reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ4SS); else value = 0;
    textWithRFiZSSBehaviour = new EditTextWithXChoiceDVRFiZSSBehaviour(_curFragmentView.getContext(),commonTxtViewRF4ZSSValue,value );

    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewRF4ZSSValue);
            processUpdateRegisterValue(commonTxtViewRF4ZSSValue, ST25DVRegisterTable.Reg_RFZ4SS);
        }
    });

    button = (Button) _curFragmentView.findViewById(R.id.BTextViewLCKCFGValue);
    button.setOnClickListener(new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            TextView commonTxtView = (TextView) _curFragmentView.findViewById(R.id.EditViewLCKCFGValue);
            processUpdateRegisterValue(commonTxtView, ST25DVRegisterTable.Reg_LockCfg);
        }
    });

}

private boolean processUpdateRegisterValue(TextView commonTxtView, ST25DVRegisterTable reg) {
    //Helper.ConvertStringToHexByte(commonTxtView.getText().toString())
    boolean ret = false;
    String mt = commonTxtView.getText().toString();
    if (mt == null) {
        Log.e(this.getClass().getName(), "cmd failed: invalid parameter" );
        return ret;

    }
    byte Value = Helper.ConvertStringToHexByte(Helper.FormatValueByteWrite(mt));
    NFCApplication currentApp = NFCApplication.getApplication();
    NFCTag currentTag = currentApp.getCurrentTag();

        if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
            SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
            stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHL.getMaxTransceiveLength());
            boolean staticRegister = true;
            if (bop.writeRegister(reg, Value, staticRegister) == 0) {
                // ok
                ret = true;
                if (sysHL.mST25DVRegister != null) {
                    sysHL.mST25DVRegister.setRegisterValue(reg,Value);
                    updateRegisterUIContent(sysHL.mST25DVRegister);
                    Log.v(this.getClass().getName(), "cmd succeed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                    toastStatus("Register value updated ...");
                } else {
                    Log.v(this.getClass().getName(), "cmd succeed but impossible to update values " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                    toastStatus("Register value not updated ...");
                }

            } else {
                // ko
                byte[] response = bop.getMBBlockAnswer();
                if (response != null) {
                    Log.e(this.getClass().getName(), "cmd failed: " + Helper.ConvertHexByteArrayToString(response));
                    toastStatus("Register value update failed ..." + Helper.ConvertHexByteArrayToString(response));
                } else {
                    Log.e(this.getClass().getName(), "cmd failed: " );
                    toastStatus("Register value update failed ..." );
                }
            }

        } else {
            Log.e(this.getClass().getName(), "cmd failed: invalid parameter" );
        }

    return ret;
}

private void updateRegisterUIContent(stnfcRegisterHandler reg){

    TextView commonTxtView ;
    short value = -1;
    //GPO
    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewGPOValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_GPO);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewitdurationValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_ITime);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewEHValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_EH);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewRFMgtValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_Rfdis);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewRF1ZSSValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ1SS);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewEndZ1Value);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_End1);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewRF2ZSSValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ2SS);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewEndZ2Value);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_End2);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewRF3ZSSValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ3SS);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewEndZ3Value);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_End3);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewRF4ZSSValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_RFZ4SS);
    commonTxtView.setText(String.format("0x%02X", value));

    commonTxtView = (TextView) _curFragmentView.findViewById(R.id.TextViewLCKCFGValue);
    if (reg != null) value = reg.getKnownRegisterValue(ST25DVRegisterTable.Reg_LockCfg);
    commonTxtView.setText(String.format("0x%02X", value));


}

}
