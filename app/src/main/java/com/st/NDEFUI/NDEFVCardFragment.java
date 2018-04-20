/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1747 $
  * Revision of last commit    :  $Rev: 1747 $
  * Date of last commit     :  $Date: 2016-03-11 19:30:55 +0100 (Fri, 11 Mar 2016) $ 
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.st.NDEF.NDEFSimplifiedMessage;
import com.st.NDEF.NDEFVCardMessage;
import com.st.NFC.NFCApplication;
import com.st.demo.R;
import com.st.util.ContactsUtilities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class NDEFVCardFragment extends NDEFSimplifiedMessageFragment implements OnItemSelectedListener {

    /*
     * Attributes
     */
    private int defaultPhotoHsize = 256;
    private int defaultPhotoWsize = 256;

    private View _curFragmentView = null;
    NDEFVCardMessage _ndefMsg = null;

    public boolean isExportPhoto() {
        return exportPhoto;
    }

    private boolean exportPhoto;
    public void setExportPhoto(boolean exportPhoto) {
        this.exportPhoto = exportPhoto;
        CheckBox exPhoto = (CheckBox) _curFragmentView.findViewById(R.id.capture_btn_Checkbox);
        if (exportPhoto) {
            exPhoto.setChecked(true);

        } else {
            exPhoto.setChecked(false);

        }

    }

    private SeekBar mseekQPicture = null;
    
    private  float seek_discrete=0; 
    private float seek_start=0; 
    private float seek_end=100; 
    private float seek_start_pos=0; 
    private int seek_start_position=50;
    
    private TextView mseekText = null;
    
    Context fragmentContext = null;
    public Uri uriContact;

    ContactsUtilities m_ContactsUtilities = null;
    
    public ContactsUtilities getM_ContactsUtilities(Uri uriContact) {
        return m_ContactsUtilities;
    }

    public void setM_ContactsUtilities(Uri uriContact, ContentResolver contentResolver) {
        this.m_ContactsUtilities = new ContactsUtilities(uriContact, contentResolver);
    }

    static final String TAG = "VCARD FRAGMENT";





    public static NDEFVCardFragment newInstance(NDEFVCardMessage msg, boolean readOnly) {
        NDEFVCardFragment fragment = new NDEFVCardFragment();
        fragment.setNDEFVCardMsg(msg);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public static NDEFVCardFragment newInstance(boolean readOnly) {
        NDEFVCardFragment fragment = new NDEFVCardFragment();
        fragment.setNDEFVCardMsg(null);
        fragment.setReadOnly(readOnly);
        return fragment;
    }

    public NDEFVCardFragment() {
        // Required empty public constructor
    }

    // Accessors
    public void setNDEFVCardMsg (NDEFVCardMessage msg) { _ndefMsg = msg; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getName(),"onCreate");
        super.onCreate(savedInstanceState);

        // _ndefMsg._VcardHandler = new VcardHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(this.getClass().getName(),"onCreateView Fragment");
        // Inflate the layout for this fragment
        _curFragmentView = inflater.inflate(R.layout.fragment_ndef_vcard, container, false);
        exportPhoto = false;
        Button captureButton = (Button) _curFragmentView.findViewById(R.id.capture_btn);
        // Deactivate the view(s) in case of ReadOnly invocation

        CheckBox exPhoto = (CheckBox) _curFragmentView.findViewById(R.id.capture_btn_Checkbox);
        exPhoto.setChecked(false);

        exPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   exportPhoto = isChecked;
               }
           }
        );

        seek_start=0;      //you need to give starting value of SeekBar
        seek_end=100;         //you need to give end value of SeekBar
        seek_start_pos=80;    //you need to give starting position value of SeekBar

        seek_start_position=(int) (((seek_start_pos-seek_start)/(seek_end-seek_start))*100);
        seek_discrete=seek_start_pos;
        mseekQPicture=(SeekBar) _curFragmentView.findViewById(R.id.photoSizeSlider);
        mseekQPicture.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Toast.makeText(_curFragmentView.getContext(), "discrete = "+String.valueOf(seek_discrete), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                // To convert it as discrete value
                float temp=progress;
                float dis=seek_end-seek_start;
                seek_discrete=(seek_start+((temp/100)*dis));
                int imgsize = computeEstimedVCardSize();
                mseekText.setText("Photo(bytes)[" + (int)seek_discrete + "] :" + imgsize);

            }
        });

        mseekText = (TextView) _curFragmentView.findViewById(R.id.SeekBarLabel);
        mseekQPicture.setProgress((int) seek_start_pos);
        //mseekText.setText("Photo QA [" + (int)seek_discrete + "]");

        if (isReadOnly()) {
            Button buttonGetContact = (Button) _curFragmentView.findViewById(R.id.button1);
            buttonGetContact.setEnabled(false);
            buttonGetContact.setVisibility(View.INVISIBLE);


            // de-activate edittext
            EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
            nameText.setFocusable(false);
            nameText.setRawInputType(InputType.TYPE_NULL);
            EditText numberText = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
            numberText.setRawInputType(InputType.TYPE_NULL);
            numberText.setFocusable(false);
            EditText SPAddrText = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
            SPAddrText.setFocusable(false);
            SPAddrText.setRawInputType(InputType.TYPE_NULL);
            EditText WebSiteText = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
            WebSiteText.setFocusable(false);
            WebSiteText.setRawInputType(InputType.TYPE_NULL);
            EditText EmailText = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
            EmailText.setFocusable(false);
            EmailText.setRawInputType(InputType.TYPE_NULL);
            //Button captureButton = (Button) _curFragmentView.findViewById(R.id.capture_btn);
            captureButton.setVisibility(View.GONE);

            CheckBox exportPhoto = (CheckBox) _curFragmentView.findViewById(R.id.capture_btn_Checkbox);
            exportPhoto.setVisibility(View.GONE);
            exportPhoto.setFocusable(false);

            View PhotoCropsView = (View) _curFragmentView.findViewById(R.id.LayoutPhotoFieldCrops);
            PhotoCropsView.setVisibility(View.GONE);

            View photoVCardlayoutQASeek = (View) _curFragmentView.findViewById(R.id.photoVCardlayoutQASeek);
            photoVCardlayoutQASeek.setVisibility(View.GONE);

            View linearLayoutinfoView = (View) _curFragmentView.findViewById(R.id.linearLayoutinfoView);
            linearLayoutinfoView.setVisibility(View.GONE);


            }
        else
        {
            Button buttonGetContact = (Button) _curFragmentView.findViewById(R.id.button1);
            buttonGetContact.setEnabled(true);
            buttonGetContact.setVisibility(View.VISIBLE);


            // activate edittext
            EditText nameText = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
            nameText.setFocusableInTouchMode(true);
            EditText numberText = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
            numberText.setFocusableInTouchMode(true);
            EditText SPAddrText = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
            SPAddrText.setFocusableInTouchMode(true);
            EditText WebSiteText = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
            WebSiteText.setFocusableInTouchMode(true);

            EditText EmailText = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
            EmailText.setFocusableInTouchMode(true);
            //Button captureButton = (Button) _curFragmentView.findViewById(R.id.capture_btn);
            captureButton.setVisibility(View.VISIBLE);

            CheckBox exportPhoto = (CheckBox) _curFragmentView.findViewById(R.id.capture_btn_Checkbox);
            exportPhoto.setVisibility(View.VISIBLE);
            exportPhoto.setFocusable(true);

            View PhotoCropsView = (View) _curFragmentView.findViewById(R.id.LayoutPhotoFieldCrops);
            PhotoCropsView.setVisibility(View.VISIBLE);

            View photoVCardlayoutQASeek = (View) _curFragmentView.findViewById(R.id.photoVCardlayoutQASeek);
            photoVCardlayoutQASeek.setVisibility(View.VISIBLE);

            View linearLayoutinfoView = (View) _curFragmentView.findViewById(R.id.linearLayoutinfoView);
            linearLayoutinfoView.setVisibility(View.VISIBLE);

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

    private int computeEstimedVCardSize() {
        String encodedImage = null;
        int imagesize = 0;
        // if (exportPhoto)
        // {
        ImageView VCardPhoto = (ImageView) _curFragmentView.findViewById(R.id.photoView);
        Bitmap bitmap = ((BitmapDrawable) VCardPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the
        // bitmap object
        bitmap.compress(Bitmap.CompressFormat.JPEG, (int) this.seek_discrete, baos); // bm
                                                                                        // is
                                                                                        // the
                                                                                        // bitmap
                                                                                        // object
        //ImageView imageView = (ImageView) _curFragmentView.findViewById(R.id.photoView);
        //imageView.setImageBitmap(bitmap);
        
        byte[] b = baos.toByteArray();
        // String encodedImage = Base64.encode(b, Base64.DEFAULT);
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        // }
        imagesize = encodedImage.length();
        return imagesize;
    }

    // Implementation of abstract method(s) from parent
    public NDEFSimplifiedMessage getNDEFSimplifiedMessage() {
        NDEFVCardMessage ndefMessage = null;

        EditText VCardEmail = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
        String email = VCardEmail.getText().toString();

        EditText VCardName = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
        String name = VCardName.getText().toString();

        EditText VCardNumber = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
        String number = VCardNumber.getText().toString();

        EditText VCardSPAddr = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
        String spaddr = VCardSPAddr.getText().toString();

        EditText VCardWebSite = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
        String website = VCardWebSite.getText().toString();

        ImageView VCardPhoto = (ImageView)  _curFragmentView.findViewById(R.id.photoView);

        String encodedImage = null;
        if (exportPhoto)
        {
            Bitmap bitmap = ((BitmapDrawable)VCardPhoto.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
            bitmap.compress(Bitmap.CompressFormat.JPEG, (int)this.seek_discrete, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            //String encodedImage = Base64.encode(b, Base64.DEFAULT);
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        }

        if(
        ( getResources().getString(R.string.name_hint) != name)
        || (getResources().getString(R.string.email_hint) != email)
        || (getResources().getString(R.string.SPAddr_hint) != spaddr)
        || (getResources().getString(R.string.number_hint) != number)
            )
        {
            ndefMessage = new NDEFVCardMessage();
            if (getResources().getString(R.string.name_hint) != name) ndefMessage.getVCardHandler().setName(name);
            if (getResources().getString(R.string.email_hint) != email) ndefMessage.getVCardHandler().setEmail(email);
            if (getResources().getString(R.string.SPAddr_hint) != spaddr) ndefMessage.getVCardHandler().setSPAddr(spaddr);
            if (getResources().getString(R.string.number_hint) != number) ndefMessage.getVCardHandler().setNumber(number);
            if (getResources().getString(R.string.website_hint) != website) ndefMessage.getVCardHandler().setWebSite(website);
            if ((encodedImage != null) && !encodedImage.isEmpty())  ndefMessage.getVCardHandler().setPhoto(encodedImage);
        }

        return ndefMessage;
    }

    public void onMessageChanged(NDEFSimplifiedMessage ndefMsg) {

        // Update VCard in TextView
        EditText VCardEmail = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
        VCardEmail.setText(((NDEFVCardMessage)ndefMsg).getVCardHandler().getEmail());

        EditText VCardName = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
        VCardName.setText(((NDEFVCardMessage)ndefMsg).getVCardHandler().getName());

        EditText VCardNumber = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
        VCardNumber.setText(((NDEFVCardMessage)ndefMsg).getVCardHandler().getNumber());

        EditText VCardSPAddr = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
        VCardSPAddr.setText(((NDEFVCardMessage)ndefMsg).getVCardHandler().getSPAddr());

        EditText VCardWebSite = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
        VCardWebSite.setText(((NDEFVCardMessage)ndefMsg).getVCardHandler().getWebSite());

        ImageView photo = (ImageView)  _curFragmentView.findViewById(R.id.photoView);
        String ImageBase64 = ((NDEFVCardMessage)ndefMsg).getVCardHandler().getPhoto();
        if (ImageBase64 != null)
        {
            // Log.i("IMAGE TO DECODE", ImageBase64);
            byte[] decodedString = Base64.decode(ImageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if ((decodedByte!=null) && (decodedByte.getByteCount() != 0)) {
                photo.setImageBitmap(decodedByte);
                setExportPhoto(true);
            }

        } else {
            setExportPhoto(false);
        }
        // additional info used for CES Salon improvement
        NFCApplication currentApp = NFCApplication.getApplication() ;
        currentApp.setmNFCApp_customername(((NDEFVCardMessage)ndefMsg).getVCardHandler().getName());
        currentApp.setmNFCApp_customermail(((NDEFVCardMessage)ndefMsg).getVCardHandler().getEmail());
        //currentApp.setmNFCApp_customerSociete(((NDEFVCardMessage)ndefMsg).getVCardHandler().getName());


    }

    // Implementation of the AdapterView.OnItemSelectedListener interface, for Spinner change behavior
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // Nothing to do
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    public void setPhotoContact(Bitmap thePic)
    {
        ImageView imageView = (ImageView) _curFragmentView.findViewById(R.id.photoView);
        imageView.setImageBitmap(thePic);
    }

    public void retrieveContactPhoto() {

        Bitmap photo = null;
        Bitmap resized = null;
 
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(fragmentContext.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(this.m_ContactsUtilities.getM_id())));
 
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                // Resize the Photo to 256 * 256 to fit in a 64k
                if (photo.getHeight() > defaultPhotoHsize || photo.getWidth() > defaultPhotoWsize) {
                    // resize to size
                    resized = Bitmap.createScaledBitmap(photo, defaultPhotoHsize, defaultPhotoWsize, true);
                } else
                {
                    resized = photo;
                }
                
                ImageView imageView = (ImageView) _curFragmentView.findViewById(R.id.photoView);
                imageView.setImageBitmap(resized);
                inputStream.close();
            }
 
            //assert inputStream != null;

 
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }



    public void retrieveContactNumberNew() {
        if (this.m_ContactsUtilities != null) {
            String Name = m_ContactsUtilities.retrieveContactNumber(m_ContactsUtilities.getM_id());
            if ((Name!=null) && (!Name.isEmpty() ))
            {
                EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
                editText.setText(Name);
            }
            else
            {
                EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
                editText.setText((getResources().getString(R.string.number_hint)));
            }
            Log.d(TAG, "Contact Phone Number: " + Name);
        }
        else
        {
            EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Number);
            editText.setText((getResources().getString(R.string.number_hint)));
        }

    }

 
    
    public void retrieveContactNameNew() {
        String lname;
        if (this.m_ContactsUtilities != null) {
            String Name = m_ContactsUtilities.getDisplayName(m_ContactsUtilities.getM_id());
            if ((Name!=null) && (!Name.isEmpty() ))
            {
                EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
                editText.setText(Name);
                lname = Name;
            }
            else
            {
                EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
                editText.setText((getResources().getString(R.string.name_hint)));
                lname = getResources().getString(R.string.name_hint);
            }
            Log.d(TAG, "Contact Name: " + Name);
        }
        else
        {
            EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Name);
            editText.setText((getResources().getString(R.string.name_hint)));
            lname = getResources().getString(R.string.name_hint);
        }
        if (NFCApplication.getApplication().isEnableSalonFeature()) {
            NFCApplication.getApplication().setmNFCApp_customername(lname);
        }

    }
    


    public void retrieveContactEmailNew() {
        String lname;
        if (this.m_ContactsUtilities != null) {
            String Name = m_ContactsUtilities.retrieveContactEmail(m_ContactsUtilities.getM_id());
            if ((Name != null) && (!Name.isEmpty())) {
                EditText editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
                editText.setText(Name);
                lname = Name;
            } else {
                EditText editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
                editText.setText((getResources().getString(R.string.email_hint)));
                lname = getResources().getString(R.string.email_hint);
            }
            Log.d(TAG, "Contact Email @: " + Name);

        } else {
            EditText editText = (EditText) _curFragmentView.findViewById(R.id.Contact_Email);
            editText.setText((getResources().getString(R.string.email_hint)));
            lname = getResources().getString(R.string.email_hint);
        }

        if (NFCApplication.getApplication().isEnableSalonFeature()) {
            NFCApplication.getApplication().setmNFCApp_customermail(lname);
        }

    }  

    

  
   public void retrieveContactWebSiteNew() {

               if (this.m_ContactsUtilities != null) {
                String Name = m_ContactsUtilities.retrieveContactWebSite(m_ContactsUtilities.getM_id());
                if ((Name != null) && (!Name.isEmpty())) {
                       EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
                       editText.setText(Name);
                } else {
                       EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
                       editText.setText("");
                }
                Log.d(TAG, "Contact WebSite @: " + Name);

            } else {
                   EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr);
                   editText.setText("");

            }

   }
   

 
    public void retrieveContactStructurePostAddrNew() {

           if (this.m_ContactsUtilities != null) {
            String Name = m_ContactsUtilities.retrieveContactStructurePostAddr(m_ContactsUtilities.getM_id());
            if ((Name != null) && (!Name.isEmpty())) {
                EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
                editText.setText(Name);
            } else {
                EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
                editText.setText((getResources().getString(R.string.SPAddr_hint)));
            }
            Log.d(TAG, "Contact PostAddr @: " + Name);

        } else {
            EditText  editText = (EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr);
            editText.setText((getResources().getString(R.string.SPAddr_hint)));
        }

        }

   
    public void importContactFields()
    {
        _ndefMsg._VCardHandler.setName( ((EditText) _curFragmentView.findViewById(R.id.Contact_Name)).getText().toString());
        _ndefMsg._VCardHandler.setNumber(((EditText) _curFragmentView.findViewById(R.id.Contact_Number)).getText().toString());
        _ndefMsg._VCardHandler.setEmail(((EditText) _curFragmentView.findViewById(R.id.Contact_Email)).getText().toString());
        _ndefMsg._VCardHandler.setSPAddr(((EditText) _curFragmentView.findViewById(R.id.Contact_SPAddr)).getText().toString());
        _ndefMsg._VCardHandler.setWebSite(((EditText) _curFragmentView.findViewById(R.id.Contact_WebSiteAddr)).getText().toString());
        // TBD : add Photo Import
    }



    

}
