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
import com.st.nfcv.SysFileLRHandler;
import com.st.nfcv.stnfcccLRhandler;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuCCFileLRFragment extends NFCPagerFragment  {
    // Store current tag
    // private NFCTag _curNFCTag = null;
    // Store view corresponding to current fragment
    private View _curFragmentView = null;
    private int currentTLVBlockID = 0;
    stnfcccLRhandler _mtagCCHandler = null;
    //STNfcCCDvHandler _mtagCCHandler = null;
    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuNDEFFilesFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static MenuCCFileLRFragment newInstance(NFCTag mNFCTag) {
        MenuCCFileLRFragment fragment = new MenuCCFileLRFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }
    public static MenuCCFileLRFragment newInstance(NFCTag mNFCTag,int page, String title) {
        MenuCCFileLRFragment fragment = new MenuCCFileLRFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }
    public MenuCCFileLRFragment() {
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
        //Log.v(this.getClass().getName(), "OnCreate Fragment");
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        Log.v(this.getClass().getName(), "OnCreate Fragment" + "page: " + page + " Name: " + title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "OnCreateView Fragment");

        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_cc_file_lr, container,
                false);

        // Create the nested fragments (tag header one)
        FragmentManager fragMng = getChildFragmentManager();
        // First check if the fragment already exists (in case current fragment has been temporarily destroyed)
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) fragMng.findFragmentById(R.id.CCFileFragTagHeaderFragmentId);
        if (mTagHeadFrag == null) {
            mTagHeadFrag = new TagHeaderFragment();
             FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.add(R.id.CCFileFragTagHeaderFragmentId, mTagHeadFrag);
            transaction.commit();
            fragMng.executePendingTransactions();
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

        RadioGroup radioGroup = (RadioGroup) _curFragmentView.findViewById(R.id.RadiogroupIDFileLayout);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                      case R.id.FileID1:
                          currentTLVBlockID = 0;
                           break;
                      case R.id.FileID2:
                          currentTLVBlockID = 1;
                          break;
                      case R.id.FileID3:
                          currentTLVBlockID = 2;
                          break;
                      case R.id.FileID4:
                          currentTLVBlockID = 3;
                          break;
                     case R.id.FileID5:
                         currentTLVBlockID = 4;
                         break;
                     case R.id.FileID6:
                         currentTLVBlockID = 5;
                         break;
                     case R.id.FileID7:
                         currentTLVBlockID = 6;
                         break;
                    case R.id.FileID8:
                        currentTLVBlockID = 7;
                        break;
                    default:
                        currentTLVBlockID = 0;
                        break;
                }   
                //updateTLVInf(currentTLVBlockID);
                NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(currentTLVBlockID);
                NFCApplication.getApplication().setFileID(currentTLVBlockID);
                onTagChanged(null);
                
            }
        }); 
        
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

    public void onRadioButtonClicked()
    {

    }
    private void updateTLVInf(int currentFileID)
    {
        TextView commonTxtView;
        String commonStr = null;
        //stnfcccLRhandler nfccchandler;
        //nfccchandler = _mtagCCHandler;

        NFCTag tt = NFCApplication.getApplication().getCurrentTag();
        SysFileLRHandler sysHDL = (SysFileLRHandler) (tt.getSYSHandler());
        NFCApplication.getApplication().getCurrentTag().setCurrentValideTLVBlokID(currentFileID);
        NFCApplication.getApplication().setFileID(currentFileID);
        stnfcccLRhandler nfccchandler = (stnfcccLRhandler) tt.getCCHandler();

        int availableTLV = 0;

        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVTFieldId);
        if (nfccchandler.gettfield(availableTLV) == nfccchandler.TAG_TYPEV_CC_FILE_TLV_TYPE_NDEF) {
            commonStr = String.format("%02X", nfccchandler.gettfield(availableTLV)) + " (" + getString(R.string.all_act_NDEF_File_Ctrl_TLV_txt) + ")";
        } else if (nfccchandler.gettfield(availableTLV) == nfccchandler.TAG_TYPEV_CC_FILE_TLV_TYPE_PROPRIETARY) {
            commonStr = String.format("%02X", nfccchandler.gettfield(availableTLV)) + " (" + getString(R.string.all_act_proprietary_File_Ctrl_TLV_txt) + ")";
        } else {
            commonStr = String.format("%02X", nfccchandler.gettfield(availableTLV));
        }
        commonTxtView.setText(commonStr);

        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVLFieldId);
        commonTxtView.setText(String.valueOf(nfccchandler.getlfield(availableTLV))+ " (" +
                getString(R.string.ti_act_add_txt_memory_size_suffix)+ ")");

        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVFileIdHeaderId);
        if (nfccchandler.gettfield(availableTLV) == nfccchandler.TAG_TYPEV_CC_FILE_TLV_TYPE_NDEF) {
            commonTxtView.setText(getString(R.string.all_act_NDEFID_header_txt));
        } else {
            commonTxtView.setText(getString(R.string.all_act_EFID_header_txt));
        }

        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVFileIdFieldId);
        commonTxtView.setText(String.format("%04X", nfccchandler.getfieldId(availableTLV)+1));
        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVFileLgthFieldId);
        commonTxtView.setText(String.valueOf(nfccchandler.getndeffilelength(availableTLV)));
        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVReadAccHeaderId);
        commonTxtView.setVisibility(View.GONE);
        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVReadAccFieldId);
//        commonTxtView.setText(String.format("%02X", nfccchandler.getreadaccess(availableTLV)));
        commonTxtView.setVisibility(View.GONE);
        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVWriteAccHeaderId);
        commonTxtView.setVisibility(View.GONE);
        commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVWriteAccFieldId);
//        commonTxtView.setText(String.format("%02X", nfccchandler.getwriteaccess(availableTLV)));
        commonTxtView.setVisibility(View.GONE);

    }

    public void onTagChanged (NFCTag newTag) {
        // Use a Tag parser for UI purpose: parsing is done when creating "NFCTag" object
        // This parser should:
        // - identify the tag manufacturer (STM or other)
        //    -> if not STM, no logo, no menu for tag management
        //    -> else (= STM tag), identify the product to determine the suitable logo, tag name, and specific menu
        
        // Update instance attribute
        int currentFileID = 0;

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
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getChildFragmentManager().findFragmentById(R.id.CCFileFragTagHeaderFragmentId);
        mTagHeadFrag.onTagChanged(newTag);

        // - Check if CC File is available for current tag
        //stnfccchandler tagCCHandler = newTag.getCCHandler();
        _mtagCCHandler = (stnfcccLRhandler) newTag.getCCHandler();
        if (_mtagCCHandler == null) {
            // Unhide "Not available" text view
            TextView curTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNotAvailableId);
            curTxtView.setVisibility(View.VISIBLE);
            RelativeLayout availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragAvailFileRelLayoutZ1);
            availLayout.setVisibility(View.VISIBLE);
            // Hide all other elements (encapsulated in a single RelativeLayout)
            availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragAvailFileRelLayout);
            availLayout.setVisibility(View.GONE);
        } else {
            // Check if CC file is correct ..... Check content
            if (_mtagCCHandler.getCCFMemorySize() == 0 ) {
                // Unhide "Not available" text view
                TextView curTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNotAvailableId);
                curTxtView.setVisibility(View.VISIBLE);
                // Hide all other elements (encapsulated in a single RelativeLayout)
                RelativeLayout availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragAvailFileRelLayout);
                availLayout.setVisibility(View.GONE);
                // hide CC info + TLV ==> Hide
                availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragAvailFileRelLayoutZ1);
                availLayout.setVisibility(View.VISIBLE);
                availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVVAreaId);
                availLayout.setVisibility(View.GONE);


            } else {

                // hide CC info + TLV ==> Hide
                RelativeLayout availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragAvailFileRelLayoutZ1);
                availLayout.setVisibility(View.GONE);
                availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragAvailFileRelLayout);
                availLayout.setVisibility(View.VISIBLE);
                availLayout = (RelativeLayout) _curFragmentView.findViewById(R.id.CCFileFragNDEFTLVVAreaId);
                availLayout.setVisibility(View.VISIBLE);

                TextView commonTxtView;
                String commonStr = null;
                // Hide "Not available" text view
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragNotAvailableId);
                commonTxtView.setVisibility(View.GONE);


                // Set the fields of CC File length
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragCCLgthFieldId);
                commonTxtView.setText(String.valueOf(_mtagCCHandler.getCCLength()));

                // Set the fields of CC File Magic Number
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragCCMgicFieldId);
                commonTxtView.setText(String.format("0x%02X", _mtagCCHandler.getMagicNumber()));


                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragMappingVerFieldId);
                commonStr = String.format("%02X", _mtagCCHandler.getMajorVersion()) + "." + String.format("%02X", _mtagCCHandler.getMinorVersion());
                commonTxtView.setText(commonStr);

                // need to add access conditions
                //CC1 = bit7-6 : Major version
                //         bit5-4 : Minor version
                //         bit3-2 : Read access (00:free access)
                //         bit1-0 : Write access (00:free access / 10:write need password / 11:no write access)
                // Set the fields of CC File Magic Number
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragRAFieldId);
                commonTxtView.setText(String.format("0x%02X", _mtagCCHandler.getReadAccess()));
                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragWAFieldId);
                commonTxtView.setText(String.format("0x%02X", _mtagCCHandler.getWriteAccess()));

                // need to add Byte 3 Type 5 of CC file
                // Need to add Memory size


                commonTxtView = (TextView) _curFragmentView.findViewById(R.id.CCFileFragMemSizeFieldId);
                //commonTxtView.setText(String.format("%02X", _mtagCCHandler.getCCFMemorySize()));
                //commonTxtView.setText(String.valueOf(_mtagCCHandler.getCCFMemorySize()));
                SysFileLRHandler sysHDL = (SysFileLRHandler) (newTag.getSYSHandler());
                int memSizeInBytes = sysHDL.getZoneSize(newTag.getCurrentValideTLVBlokID());
                if (memSizeInBytes != 0) {
                    //if (_mtagCCHandler.getCCFMemorySize() != 0) {
                    //commonTxtView.setText(String.valueOf((_mtagCCHandler.getCCFMemorySize() + 8)) + " " + getString(R.string.ti_act_add_txt_memory_size_suffix));
                    commonTxtView.setText(String.valueOf(memSizeInBytes) + " " + getString(R.string.ti_act_add_txt_memory_size_suffix));
                } else {
                    commonTxtView.setText(getString(R.string.ti_act_add_txt_memory_size_unknown) + " " + getString(R.string.ti_act_add_txt_memory_size_unknown_suffix));
                }

            }



            //hide Select File ID layout
            RelativeLayout selectFileIDLayout = (RelativeLayout)_curFragmentView.findViewById(R.id.CCFileFragIDFileLayout);
            selectFileIDLayout.setVisibility(View.GONE);
            //           currentFileID=0;
//               updateTLVInf(currentFileID);

            // TLV control part
            // Check Nb TLV Block ...
            if (((NFCApplication.getApplication().getCurrentTag().getCCHandler() != null)) &&
                    (NFCApplication.getApplication().getCurrentTag().getCCHandler().getNbFile() > 1)) {
                selectFileIDLayout = (RelativeLayout)_curFragmentView.findViewById(R.id.CCFileFragIDFileLayout);
                selectFileIDLayout.setVisibility(View.VISIBLE);
                currentFileID=NFCApplication.getApplication().getFileID();
                // Update Radio button visibility according to the number of TLV blocks from CC File.
                int buttonIDList[] = {R.id.FileID1,R.id.FileID2,R.id.FileID3,R.id.FileID4,R.id.FileID5,R.id.FileID6,R.id.FileID7,R.id.FileID8};
                for (int i=0;i<8;i++)
                {
                    RadioButton rdButton = (RadioButton) _curFragmentView.findViewById(buttonIDList[i]);
                    if (i < NFCApplication.getApplication().getCurrentTag().getCCHandler().getNbFile())
                    {
                        rdButton.setVisibility(View.VISIBLE);
                        // if ( i == newTag.getCurrentValideTLVBlokID())
                        if ( i == NFCApplication.getApplication().getFileID())
                        {
                            rdButton.setChecked(true);
                        }
                    }
                    else
                    {
                        rdButton.setVisibility(View.GONE);
                    }
                }

            }
            else
            {
                //hide Select File ID layout

                selectFileIDLayout = (RelativeLayout)_curFragmentView.findViewById(R.id.CCFileFragIDFileLayout);
                selectFileIDLayout.setVisibility(View.GONE);
                currentFileID=0;
            }
            updateTLVInf(currentFileID);

        }
    }

}
