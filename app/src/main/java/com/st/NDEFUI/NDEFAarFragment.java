/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1672 $
  * Revision of last commit    :  $Rev: 1672 $
  * Date of last commit     :  $Date: 2016-02-18 17:11:40 +0100 (Thu, 18 Feb 2016) $ 
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

package com.st.NDEFUI;

import java.util.ArrayList;
import java.util.List;

import com.st.NDEF.NDEFAarMessage;
import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NFC.NFCApplication;
import com.st.demo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class NDEFAarFragment extends NDEFSimplifiedMessageFragment implements OnItemClickListener{

    /*
     * Attributes
     */
    private View _curFragmentView = null;
    NDEFAarMessage _ndefMsg = null;
    private String contactID;     // contacts unique I

    Context fragmentContext = null;
    public Uri uriContact;

    static final String TAG = "VCARD - TEST";

     private ListView lView;
     private ArrayList results = new ArrayList();

     private List<ResolveInfo> _maarlist;





    public static NDEFAarFragment newInstance(NDEFAarMessage msg, boolean readOnly) {
        NDEFAarFragment fragment = new NDEFAarFragment();
        fragment.setAardMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFAarFragment newInstance(boolean readOnly) {
        NDEFAarFragment fragment = new NDEFAarFragment();
        fragment.setAardMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFAarFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setAardMsg (NDEFAarMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // _ndefMsg._VcardHandler = new VcardHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_aar, container, false);

        // "getList button"
        Button getList = (Button) _curFragmentView.findViewById(R.id.aarbutton);

        getList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getaapList(v);
            }
        });


        // Deactivate the view(s) in case of ReadOnly invocation
        if (isReadOnly()) {
            Button buttonGetAar = (Button) _curFragmentView.findViewById(R.id.aarbutton);
            buttonGetAar.setEnabled(false);
            buttonGetAar.setVisibility(View.INVISIBLE);


            // de-activate edittext
            EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Aar_selection);
            nameText.setFocusable(false);
            nameText.setRawInputType(InputType.TYPE_NULL);

            lView = (ListView) _curFragmentView.findViewById(R.id.arrlist);
            lView.setVisibility(View.INVISIBLE);

            }
        else
        {
            Button buttonGetAar = (Button) _curFragmentView.findViewById(R.id.aarbutton);
            buttonGetAar.setEnabled(true);
            buttonGetAar.setVisibility(View.VISIBLE);

            // activate edittext
            EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Aar_selection);
            nameText.setFocusableInTouchMode(true);

            // build app list
            lView = (ListView) _curFragmentView.findViewById(R.id.arrlist);
            PackageManager pm = NFCApplication.getContext().getPackageManager();

            lView.setOnItemClickListener(this);

            /*
            lView.setOnItemClickListener(new OnItemClickListener() {
                   public void onItemSelected(AdapterView <?> parentView, View childView, int position, long id)
                   {
                       getselectedAar( parentView,  childView, position,  id);
                   };
                   public void onNothingSelected(AdapterView <?> parentView)
                   {
                       //nothing to do.
                   }
            });
            */

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            _maarlist = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);

            for (ResolveInfo rInfo : _maarlist)
            {
             results.add(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
             Log.w("Installed Applications", rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            }
            ArrayAdapter arrAdapter = new ArrayAdapter(NFCApplication.getContext(), R.layout.aar_item, results);

            lView.setAdapter(arrAdapter);
            lView.setVisibility(View.INVISIBLE);
        }

        return _curFragmentView;
    }

    @Override
    public void onStart() {
     Log.v(this.getClass().getName(), "onStart Fragment");
     super.onStart();

     if (_ndefMsg != null) {
         // Fill in the layout with the current message
         onMessageChanged (_ndefMsg);
         _ndefMsg = null;
     }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Implementation of abstract method(s) from parent
    public NDEFSimplifiedMessage getNDEFSimplifiedMessage() {
        NDEFAarMessage ndefMessage = null;

    EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Aar_selection);
     String aarSelect = nameText.getText().toString();

     ndefMessage = new NDEFAarMessage(aarSelect);
    return ndefMessage;

    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {

        EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Aar_selection);
        nameText.setText(((NDEFAarMessage)ndefMsg).get_maar());

    }

    // Implementation of the AdapterView.OnItemSelectedListener interface, for Spinner change behavior
 public void onItemSelected(AdapterView<?> parent, View view, 
         int pos, long id) {
     // Nothing to do
 }

 public void onNothingSelected(AdapterView<?> parent) {
     // Another interface callback
 }

public void getaapList(View v) {
    lView = (ListView) _curFragmentView.findViewById(R.id.arrlist);
    lView.setVisibility(View.VISIBLE);
    }

public void  getselectedAar(AdapterView parentView, View childView, int position, long id)
{
     //private List<ResolveInfo> _maarlist;
    final PackageManager pm = NFCApplication.getContext().getPackageManager();
    //ResolveInfo currentAppInfo = _maarlist.get(position);
    EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Aar_selection);
    nameText.setText(_maarlist.get(position).activityInfo.applicationInfo.packageName);

    Button buttonGetAar = (Button) _curFragmentView.findViewById(R.id.aarbutton);
    buttonGetAar.setEnabled(true);
    buttonGetAar.setVisibility(View.VISIBLE);

    lView.setVisibility(View.INVISIBLE);
}

@Override
public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    getselectedAar(arg0, arg1, arg2, arg3);

}


}
