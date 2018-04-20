/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1609 $
  * Revision of last commit    :  $Rev: 1609 $
  * Date of last commit     :  $Date: 2016-02-03 17:49:23 +0100 (Wed, 03 Feb 2016) $ 
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
package com.st.nfc4;

import com.st.NFC.stnfcTag_Err;

public class Iso7816_4Err extends stnfcTag_Err {


    // PROCESS COMPLETE
    static final byte SW1_NORMALPROCESSING =(byte)0x90;
    static final byte SW2_NORMALPROCESSING =(byte)0x00;

    static final byte SW1_NORMALPROCESSINGREMAINDATA =(byte)0x61;

    // Warning DEF - PROCESS COMPLETE
    static final byte SW1_WARNINGPROCESSING_NVMUNCHANGED = (byte) 0x62;
    static final byte SW2_WARNTRIGGEDBYCARDB = (byte) 0x02;
    static final byte SW2_WARNTRIGGEDBYCARDE = (byte) 0x80;
    static final byte SW2_WARNTDATAMAYCORRUPT = (byte) 0x81;
    static final byte SW2_WARNEOFREACHED = (byte) 0x82;
    static final byte SW2_WARNSELFILERELEASED = (byte) 0x83;
    static final byte SW2_WARNFILTECTRLERR = (byte) 0x84;


    static final byte SW1_WARNINGPROCESSING_NVMCHANGED = (byte) 0x63;


    // Execution Error
    static final byte SW1_EXECERROR_NVMUNCHANGED = (byte)0x64;

    static final byte SW1_EXECERROR_NVMCHANGED = (byte)0x65;

    static final byte SW1_EXECERROR_SECURITY = (byte)0x66;


    // Checked Error
    static final byte SW1_CHECKERROR_WRONGLENGTH = (byte)0x67;

    static final byte SW1_CHECKERROR_FUNCLANOTSUPPORTED = (byte)0x68;

    static final byte SW1_CHECKERROR_CMDNOTALLOWED = (byte)0x69;

    static final byte SW1_CHECKERROR_WRONGPARAMETER = (byte)0x6A;

    static final byte SW1_CHECKERROR_WRONGPARAMETER2 = (byte)0x6B;

    static final byte SW1_CHECKERROR_WRONGLEFIELD = (byte)0x6C;

    static final byte SW1_CHECKERROR_INSNOTSUPPORTED = (byte)0x6D;

    static final byte SW1_CHECKERROR_CLANOTSUPPORTED = (byte)0x6E;

    static final byte SW1_CHECKERROR_UNKNOWNERR = (byte)0x6F;


    // constructor

    public Iso7816_4Err()
    {
        _SWField = new byte[2];
        _SWField[0] = (byte)0x00;
        _SWField[1] = (byte)0x00;
        _answer = null;
    }

    public void reset()
    {
        _SWField[0] = (byte)0x00;
        _SWField[1] = (byte)0x00;
        _answer = null;
    }


    public Iso7816_4Err(byte p1, byte p2)
    {
        _SWField = new byte[2];
        _SWField[0] = p1;
        _SWField[1] = p2;
        _answer = null;
    }

    public boolean isSuccess()
    {
        if (( (this._SWField[0] == (byte)SW1_NORMALPROCESSING)&&(this._SWField[1] == (byte)SW2_NORMALPROCESSING))
                  || (this._SWField[0] ==SW1_NORMALPROCESSINGREMAINDATA )
                  )
                  return true;
        else return false;
    }

    public boolean isWarning()
    {
        return false;
    }

    public boolean isError()
    {
        return false;
    }

    public String translate()
    {
        int index=0;
        String errolog="";
        byte lsw1 = getSW1();
        byte lsw2 = getSW2();

        while (index < Iso7816_4ErrLabel._Iso7816_4ErrMeans.length) {
/*            if ((Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW1() != getSW1())
                    && ((Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW2() & 0xF0) == 0xC0)) {
                errolog = "The password transmitted is incorrect, remains "
                        + (int) ((Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW2()) & 0x0F) + "try";
                break;
            }*/
            // Patch FBE on error - password ....
            if (    (Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW1() == getSW1())
                    && (Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW2() == getSW2())
                    ) {
                if ((Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW1() == getSW1() && lsw1 == 0x63)
                        && ((Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW2() & 0xF0) == 0xC0)) {
                    errolog = "The password transmitted is incorrect, remains "
                            + (int) ((Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._err.getSW2()) & 0x0F) + "try";
                    break;
                } else {
                    break;
                }
            }
            index++;
        }

        if (index >= Iso7816_4ErrLabel._Iso7816_4ErrMeans.length) index--;
        if (errolog.isEmpty()) errolog = Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._mean;
        return     Iso7816_4ErrLabel._Iso7816_4ErrMeans[index]._mean;
    }
}
