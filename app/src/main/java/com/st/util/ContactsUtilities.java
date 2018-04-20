/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1170 $
  * Revision of last commit    :  $Rev: 1170 $
  * Date of last commit     :  $Date: 2015-09-23 16:35:26 +0200 (Wed, 23 Sep 2015) $
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

package com.st.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;



import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Entity;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.widget.EditText;

public class ContactsUtilities {
    final static String TAG = "ContactsUtilities";
    public Uri m_uriContact;
    private ContentResolver m_ContentResolver;
    private String m_id;
    private String m_VCardVersion = null;
    private Hashtable m_balance = new Hashtable();


    /**
     *
     */
    public ContactsUtilities() {
        // TODO Auto-generated constructor stub
        m_uriContact = null;
        m_ContentResolver = null;
        m_id = null;
        m_VCardVersion = null;
    }

    public ContactsUtilities(Uri uriContact, ContentResolver contentResolver) {
        // TODO Auto-generated constructor stub
        m_uriContact = uriContact;
        m_ContentResolver = contentResolver;
        m_id = getContactIdFromAddressBook(m_uriContact);
        // used only for test
        //getVCF();
        //m_VCardVersion = getVCardVersion(m_id);
        getVCFHashtable();
    }

    public String getM_id() {
        return m_id;
    }

    private String retrieveContactInfo(Uri uri,String[] projection,String whereName,String[] whereNameParams)
    {
        String ret = null;
        Cursor c = null;
        try {
            c = m_ContentResolver.query(uri, projection, whereName, whereNameParams, null);
            if (c != null && c.moveToFirst()) {
                int indexDisplayName = c.getColumnIndexOrThrow(projection[0]);
                ret = c.getString(indexDisplayName);
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
        return ret;
    }

    public String getContactIdFromAddressBook(Uri uriContact) {
        String ret = null;
        Uri uri = uriContact;
        String[] projection = new String[]{ContactsContract.Contacts._ID};
        String whereName = null;
        String[] whereNameParams = null;

        ret = retrieveContactInfo    (uri, projection, null, null);
        Log.d(TAG, "getContactIdFromAddressBook :" + ret);

        return ret;
    }

    public String getVCardVersion(String id)
    {
        String ret = null;

        Uri uri = ContactsContract.Data.CONTENT_URI;
//        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredName.DATA_VERSION};
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DATA_VERSION};
        String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.Phone.DATA_VERSION };

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);
        Log.d(TAG, "getVCardVersion :" + ret);

        return ret;
    }

    public String getDisplayName(String id)
    {
        String ret = null;
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME};
        String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);

        return ret;
    }


    public  String retrieveContactEmail(String id) {
        String ret = null;
        Uri uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
        String whereName = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
        String[] whereNameParams = new String[]{id};

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);


        return ret;


    }

    public  String retrieveContactWebSite(String id) {
        String ret = null;
/*        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[] {Website.URL};
        String whereName = ContactsContract.Data.CONTACT_ID + " = " + id + " AND ContactsContract.Data.MIMETYPE = '"
                + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                + "'";
        String[] whereNameParams = null;*/

        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Website.URL};
        //String whereName = ContactsContract.Data.CONTACT_ID + " = " + id ;
        String whereName = ContactsContract.Data.CONTACT_ID + " = " + id + " AND ContactsContract.Data.MIMETYPE = '"
                + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                + "'";
        String[] whereNameParams = null;

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);
        if (ret == null) {
            ret = (String)this.m_balance.get("vnd.android.cursor.item/website");
        }

        return ret;


    }


    public  String retrieveContactStructurePostAddr(String id) {
        String ret = null;
        Uri uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};
        String whereName = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ? ";
        String[] whereNameParams = new String[]{id};

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);


        return ret;


    }


    public  String retrieveContactNumber(String id) {
        String ret = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        String whereName = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        String[] whereNameParams = new String[]{id};

        ret = retrieveContactInfo    (uri, projection, whereName, whereNameParams);


        return ret;


    }

    public HashMap<String, String> getFullName(String id)
    {
        HashMap<String, String> ret = new HashMap<String, String>();

        String whereName = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
        Cursor c = null;
        try {
            c = m_ContentResolver.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, null);
            if (c != null && c.moveToFirst()) {
                int indexGivenName = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
                int indexFamilyName = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                int indexDisplayName = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);

                ret.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, c.getString(indexGivenName));
                ret.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, c.getString(indexFamilyName));
                ret.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, c.getString(indexDisplayName));
            }
        }
        catch(Exception e) {
            Log.e("getFullName", e.getMessage());
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
        return ret;
    }


    private  void getVCFHashtable() {
     //String ret = new String();
     Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, Long.parseLong(getM_id()));
     Uri entityUri = Uri.withAppendedPath(this.m_uriContact, android.provider.ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);
     Cursor c = m_ContentResolver.query(entityUri,
              new String[]{RawContacts.SOURCE_ID, android.provider.ContactsContract.Contacts.Entity.DATA_ID, android.provider.ContactsContract.Contacts.Entity.MIMETYPE, android.provider.ContactsContract.Contacts.Entity.DATA1},
              null, null, null);
     m_balance.clear();
     try {
         while (c.moveToNext()) {
             String sourceId = c.getString(0);
             //ret = ret + sourceId;
             if (!c.isNull(1)) {
                 String mimeType = c.getString(2);
                 //ret = ret + "  " + mimeType;
                 String data = c.getString(3);
                 //ret = ret + "  " + data;
                 if (mimeType != null &&  data != null) m_balance.put(mimeType, data);
             }
             //ret = ret + "\n";
         }
     }
         catch(Exception e) {
                Log.e("getVCFHashtable", e.getMessage());
            }
     finally {
         c.close();
     }
    //return ret;
}

    public  void getVCF() {
        final String vfile = "Contacts.csv";
        Cursor phones = m_ContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        phones.moveToFirst();

        do {
            String lookupKey = phones.getString(phones
                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            AssetFileDescriptor fd;
            try {
                fd = m_ContentResolver.openAssetFileDescriptor(uri,
                        "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = new byte[(int) fd.getDeclaredLength()];
                fis.read(buf);
                String VCard = new String(buf);
                String path = Environment.getExternalStorageDirectory()
                        .toString() + File.separator + vfile;
                FileOutputStream mFileOutputStream = new FileOutputStream(path,
                        true);
                mFileOutputStream.write(VCard.toString().getBytes());
                phones.moveToNext();
                Log.d("Vcard", VCard);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } while (phones.moveToNext());

    }

}
