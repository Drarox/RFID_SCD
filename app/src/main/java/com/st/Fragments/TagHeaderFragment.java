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

import com.st.NFC.NFCTag;
import com.st.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TagHeaderFragment extends Fragment {
//public class TagHeaderFragment extends Fragment implements Serializable {
    //private static final long serialVersionUID = 0L;

    // Store the view where the fragment applies
    private View _curView = null;

    public static TagHeaderFragment newInstance(View mView) {
        TagHeaderFragment fragment = new TagHeaderFragment();
        fragment.setView(mView);
        return fragment;
    }

    public TagHeaderFragment() {
        // Required empty public constructor
    }

    public void setView(View mView) {
        _curView = mView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "onCreateView Fragment");
        // Inflate the layout for this fragment
        _curView = inflater.inflate(R.layout.fragment_tag_header, container,
                false);
        return _curView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(this.getClass().getName(), "onActivityCreated Fragment");
        super.onActivityCreated(savedInstanceState);

        // Get the active tag description
        /*NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag= currentApp.getCurrentTag();
        onTagChanged(currentTag);*/
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

    public void onTagChanged (NFCTag mNFCTag) {
        // Get applicable view
        View mView;
        if (_curView != null) {
            mView = _curView;
        } else {
            mView = getView();
        }

        // Update model, additional description and Tag type according to the tag characteristics... if any
        TextView modelText = (TextView) mView.findViewById(R.id.TagHeaderModelTxtId);
        TextView addDescText = (TextView) mView.findViewById(R.id.TagHeaderAddDescTxtId);
        TextView tagTypeText = (TextView) mView.findViewById(R.id.TagHeaderTagTypeTxtId);
        if (mNFCTag != null) {
            // - Model
            modelText.setText(mNFCTag.getModel());
            // - Additional Description, if any
            String addDesc = mNFCTag.getAddDescr();
            if (addDesc == "") {
                addDescText.setVisibility(View.GONE);
            } else {
                addDescText.setText(addDesc);
                addDescText.setVisibility(View.VISIBLE);
            }
            // - Tag Type, if any
            String tagType = mNFCTag.getTypeStr();
            if (tagType == "") {
                tagTypeText.setVisibility(View.GONE);
            } else {
                tagTypeText.setText(tagType);
                tagTypeText.setVisibility(View.VISIBLE);
            }
        } else {
            modelText.setText(getString(R.string.tag_header_default_model_txt));
            addDescText.setVisibility(View.GONE);
            tagTypeText.setVisibility(View.GONE);
        }
    }
}
