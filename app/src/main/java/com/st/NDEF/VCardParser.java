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


import java.util.Arrays;
import java.util.Scanner;

import android.util.Log;


public class VCardParser {

    // byte [] _buffer2parse;
    String _string2parse;
    String [] _StringArray;
    Scanner _scanner;


    //constructor
    public VCardParser(byte [] abuffer2parse)
    {
        _string2parse = abuffer2parse.toString();
        if (!_string2parse.isEmpty())
        {
            _scanner = new Scanner(_string2parse);
        }
    }
    public VCardParser(String astring2parse)
    {
        _string2parse = astring2parse;
        _StringArray = astring2parse.split("\\n");
        if (!_string2parse.isEmpty())
        {
            _scanner = new Scanner(astring2parse);
        }
    }

/*
     public VCardParser(byte [] abuffer2parse)

    {
        _buffer2parse = abuffer2parse.clone();
        if (_buffer2parse.length != 0)
        {
            _scanner = new Scanner(abuffer2parse.toString());
        }
    }
*/

    // populate VCard Handler from the
    public int parse(VcardHandler aVcardHandler)
    {
        _scanner.useDelimiter("\n");
        while (_scanner.hasNext())
        {
            String item = _scanner.next();
            //Log.d("Parser",item);
            String property[] =  item.split(":");
            if (property.length<2)
            {
                continue;
            }
            String propertyName[] =  property[0].split(";");
            String propertyValue[] =  property[1].split(";");
            if (propertyName[0].equals("BEGIN"))
            {
                Log.d("Parser","BEGIN Flag detected!");
            }
            else if (propertyName[0].equals("END"))
            {
                Log.d("Parser","END Flag detected!");
            }
            else if (propertyName[0].equals("VERSION"))
            {
                Log.d("Parser","VERSION Flag detected!");
                Log.d("Parser","Current Version is  " + propertyValue[0]);
            }
            else if (propertyName[0].equals("N"))
            {
                Log.d("Parser","NAME Flag detected!");
                Log.d("Parser","Current NAME is  " + Arrays.toString(propertyValue));
                aVcardHandler.setName(appendStringFromArrayString(propertyValue));

            }
            else if (propertyName[0].equals("FN"))
            {
                Log.d("Parser","FULL NAME Flag detected!");
                Log.d("Parser","Current FULL NAME is" + Arrays.toString(propertyValue));
                aVcardHandler.setFormatedName(appendStringFromArrayString(propertyValue));
            }
            else if (propertyName[0].equals("EMAIL"))
            {
                Log.d("Parser","EMAIL Flag detected!");
                Log.d("Parser","Current EMAIL is" + Arrays.toString(propertyValue));
                aVcardHandler.setEmail(appendStringFromArrayString(propertyValue));
            }
            else if (propertyName[0].equals("NICKNAME"))
            {
                Log.d("Parser","NICKNAME Flag detected!");
                Log.d("Parser","Current EMAIL is" + Arrays.toString(propertyValue));
                aVcardHandler.setNickName(appendStringFromArrayString(propertyValue));
            }
            else if (propertyName[0].equals("TEL"))
            {
                Log.d("Parser","TEL Flag detected!");
                Log.d("Parser","Current TEL is" + Arrays.toString(propertyValue));
                aVcardHandler.setNumber(appendStringFromArrayString(propertyValue));
            }
            else if (propertyName[0].equals("ADR"))
            {
                Log.d("Parser","ADR Flag detected!");
                Log.d("Parser","Current ADR is" + Arrays.toString(propertyValue));
                aVcardHandler.setSPAddr(appendStringFromArrayString(propertyValue));
            }
            else if (propertyName[0].equals("URL"))
            {
                Log.d("Parser","WebSite URI Flag detected!");
                Log.d("Parser","Current URI is" + Arrays.toString(propertyValue));
                aVcardHandler.setWebSite(appendStringFromArrayString(propertyValue));
            }
            else if (propertyName[0].equals("PHOTO"))
            {
                String imageString = propertyValue[0];
                boolean _break = false;
                while ((_scanner.hasNext()) && (_break == false))
                {
                    item = _scanner.next();
                    //Log.d("Parser",item);
                    property =  item.split(":");
                    if (property.length==1)
                    {
                        imageString = imageString +property[0];
                    }
                    else
                    {
                        _break = true;
                    }
                }


                Log.d("Parser","PHOTO Flag detected!");
                Log.d("Parser","Current Photo is" + Arrays.toString(propertyValue));
                aVcardHandler.setPhoto(imageString /*appendStringFromArrayString(propertyValue)*/);
            }
            else
            {
                Log.d("Parser","Token "+propertyName[0]+" not yet handled!");
            }

        }
        return 0;
    }

    private String appendStringFromArrayString(String [] arrayString)
    {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i<arrayString.length;i++)
        {
            result.append(arrayString[i]);
            result.append(" ");
        }
        return result.toString();
    }

}

