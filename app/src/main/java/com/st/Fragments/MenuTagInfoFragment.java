/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1616 $
  * Revision of last commit    :  $Rev: 1616 $
  * Date of last commit     :  $Date: 2016-02-03 19:03:03 +0100 (Wed, 03 Feb 2016) $ 
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
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

import android.app.Activity;
import android.app.Fragment;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenuTagInfoFragment extends NFCPagerFragment {
    // Store current tag
    //private NFCTag _curNFCTag = null;
    // Store view corresponding to current fragment
    private View _curFragmentView = null;

    static final String TAG_HEADER_FRAGMENT_PARAM = "tagHeaderFrag";


    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param mNFCTag
     *            NFC Tag to consider
     * @return A new instance of fragment MenuNDEFFilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuTagInfoFragment newInstance(NFCTag mNFCTag) {
        MenuTagInfoFragment fragment = new MenuTagInfoFragment();
        fragment.setNFCTag(mNFCTag);
        return fragment;
    }
    public static MenuTagInfoFragment newInstance(NFCTag mNFCTag,int page, String title) {
        MenuTagInfoFragment fragment = new MenuTagInfoFragment();
        fragment.setNFCTag(mNFCTag);
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuTagInfoFragment() {
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
        _curFragmentView = inflater.inflate(R.layout.fragment_menu_tag_info, container,
                false);

        // Create the nested fragments (tag header one)
        FragmentManager fragMng = getChildFragmentManager();
        // First check if the fragment already exists (in case current fragment has been temporarily destroyed)
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) fragMng.findFragmentById(R.id.TiActTagHeaderFragmentId);
        if (mTagHeadFrag == null) {
            mTagHeadFrag = new TagHeaderFragment();
             FragmentTransaction transaction = fragMng.beginTransaction();
            transaction.add(R.id.TiActTagHeaderFragmentId, mTagHeadFrag);
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

        // Set the layout content according to the content of the tag
        // - Tag header
        TagHeaderFragment mTagHeadFrag = (TagHeaderFragment) getChildFragmentManager().findFragmentById(R.id.TiActTagHeaderFragmentId);
        mTagHeadFrag.onTagChanged(newTag);
        // - Manufacturer
        TextView tagManufacturerText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtManufacturerFieldId);
        tagManufacturerText.setText(newTag.getManufacturer());
        // - UID
        TextView tagUIDText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtUIDFieldId);
        tagUIDText.setText(newTag.getUIDStr());
        // - DSFID
        TextView tagDSFIDText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtDSFIDFieldId);
        tagDSFIDText.setText("0x--");
        tagDSFIDText.setVisibility(View.GONE);
        tagDSFIDText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtDSFIDHeaderId);
        tagDSFIDText.setVisibility(View.GONE);
        // - AFI
        TextView tagAFIText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtAFIFieldId);
        tagAFIText.setText("0x--");
        tagAFIText.setVisibility(View.GONE);
        tagAFIText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtAFIHeaderId);
        tagAFIText.setVisibility(View.GONE);
        // - Memory size
        // Mem size info should always be available: display "unknown" in case of 0
        TextView memSizeText1 = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtMemSizeId);
        TextView memSizeText2 = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtMemSizeSuffixId);
        if (newTag.getMemSize() != 0) {
            memSizeText1.setText(String.valueOf(newTag.getMemSize()));
            memSizeText2.setText(getString(R.string.ti_act_add_txt_memory_size_suffix));
        } else {
            memSizeText1.setText(getString(R.string.ti_act_add_txt_memory_size_unknown));
            memSizeText2.setText(getString(R.string.ti_act_add_txt_memory_size_unknown_suffix));
        }
        // Mem blocks number and size per block are considered as optional: if they are null, discard the display
        memSizeText1 = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtMemBlcksId);
        memSizeText2 = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtMemBlcksSuffixId);
        TextView memSizeText3 = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtMemBytesPerBlckId);
        TextView memSizeText4 = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtMemBytesPerBlckSuffixId);
        if ((newTag.getBlckNb() == 0)
                || (newTag.getBytesPerBlck() == 0)) {
            // At least 1 of the 2 values is unknown, discard the 2 items
            memSizeText1.setVisibility(View.GONE);
            memSizeText2.setVisibility(View.GONE);
            memSizeText3.setVisibility(View.GONE);
            memSizeText4.setVisibility(View.GONE);
        } else {
            // Both values are available, display these
            memSizeText1.setVisibility(View.VISIBLE);
            memSizeText2.setVisibility(View.VISIBLE);
            memSizeText3.setVisibility(View.VISIBLE);
            memSizeText4.setVisibility(View.VISIBLE);

            memSizeText1.setText(String.valueOf(newTag.getBlckNb()));
            memSizeText2.setText(getString(R.string.ti_act_add_txt_memory_blcks_suffix));
            memSizeText3.setText(String.valueOf(newTag.getBytesPerBlck()));
            memSizeText4.setText(getString(R.string.ti_act_add_txt_memory_bytes_per_blck_suffix));
        }
        // - Techno List
        TextView tagTechListText = (TextView) _curFragmentView.findViewById(R.id.TiActAddTxtTechListFieldId);
        tagTechListText.setText(TextUtils.join("\n", newTag.getTechList()));
        // - NDEF content
        //ExpandableListView tagNdefList = (ExpandableListView) _curFragmentView.findViewById(R.id.NdefMsgListId);
        TextView tagNdefText = (TextView) _curFragmentView.findViewById(R.id.TiActNdefContentId);
        // NdefMessage[] msgArray = newTag.getNdefMessages();
        // FAR Remove - no sens to get this inf here.
        //if (msgArray == null) {
        //    tagNdefText.setText(getString(R.string.ti_act_ndef_content_no_ndef));
            //String[] mDispText = new String[]{getString(R.string.ti_act_ndef_content_no_ndef)};
            //tagNdefList.setAdapter(new ArrayAdapter<String>(this, R.layout.view_ti_layout_ndef_item, mDispText));
            //tagNdefList.setAdapter(new BaseExpandableListAdapter();
        //  } else {
            // Binding resources Array to ListAdapter
 //           tagNdefList.setAdapter(new ArrayAdapter<NdefMessage>(this, R.layout.view_ndef_item_text, msgArray));
            //NdefMessage curMsg;
            //int curMsgIdx;
            //for (curMsgIdx = 0; curMsgIdx < msgArray.length; curMsgIdx++) {
            //    curMsg = msgArray[curMsgIdx];
            //}//
            //tagNdefText.setText(msgArray[0].getRecords()[0].toString());
            //}
    }

}
