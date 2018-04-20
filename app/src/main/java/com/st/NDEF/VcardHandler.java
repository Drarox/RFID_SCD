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

package com.st.NDEF;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class VcardHandler {    



//    private Uri uriContact;
//    private String contactID;     // contacts unique I
    
    
    public VcardHandler l;

    public String _Number;
    public String _Name;
    public String _NickName;
    public String _FormatedName;

    public String _Email;
    public String _SPAddr;
    public String _WebSiteAddr;
    public String _Vcard;
    public String _Photo;

    NdefRecord nfcRecord = null;
    static final String TAG = "VCARD - TEST";

    public VcardHandler()
    {
        _Number = null;
        _Name = null;
        _NickName = null;
         _FormatedName = null;
        _Email = null;
        _SPAddr = null;
        _WebSiteAddr = null;
        _Vcard = null;
        _Photo = null;
    }

    public String getNumber ()
    {
        return _Number;
    }

    public String getName()
    {
        return _Name;
    }

    public String getFormatedName()
    {
        return _FormatedName;
    }

    public String getNickName()
    {
        return _NickName;
    }

    public String getEmail()
    {
        return _Email;
    }

    public String getSPAddr()
    {
        return _SPAddr;
    }

    public String getWebSite()
    {
        return _WebSiteAddr;
    }


    public String getVcard()
    {
        return _Vcard;
    }

    public String getPhoto()
    {
        return _Photo;
    }

    public void setNumber( String aNumber) { _Number= new String(aNumber);}
    public void setName(String aName) { _Name = new String(aName);}
    public void setFormatedName(String aFormatedName) { _FormatedName = new String(aFormatedName);}
    public void setNickName(String aNickName) { _NickName = new String(aNickName);}
    public void setEmail(String aEmail) {_Email = new String(aEmail);}
    public void setSPAddr(String aSPAddr) {_SPAddr = new String(aSPAddr);}
    public void setWebSite(String aWebSiteAddr) {_WebSiteAddr = new String(aWebSiteAddr);}
    public void setPhoto(String Photo) { _Photo = new String(Photo);}
    public void setVcard(String vcardString) {_Vcard = new String(vcardString);}
    
    
}
