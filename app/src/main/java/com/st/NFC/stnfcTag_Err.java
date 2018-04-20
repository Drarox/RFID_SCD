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

package com.st.NFC;

public abstract class stnfcTag_Err {

    protected byte[] _SWField;
    protected byte[] _answer;

    public stnfcTag_Err() {
        // TODO Auto-generated constructor stub
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


    public void initBuff(int size)
    {
        _answer = new byte[size];
    }

    public void resetBuff(int size)
    {
        _answer = null;
    }


    public byte getSW1() { return _SWField[0]; }
    public byte getSW2() { return _SWField[1]; }
    public void setSW1(byte p1) { _SWField[0] = p1;}
    public void setSW2(byte p2) { _SWField[1] = p2;}

    public boolean  hasAnswerData()
    {
        return (_answer.length!=0);
    }
    public byte[] get()
    {
        return _SWField;
    }
    public byte[] getbuffAnswer()
    {
        return _answer;
    }

    public void set(byte [] abyteArray)
    {
        if (abyteArray == null)
        {
            _SWField[0] = 0x00;
            _SWField[1] = 0x00;
        }
        else
        {
            if (abyteArray.length < 2)
                this.reset();
            else
            {
                _SWField[0] = abyteArray[abyteArray.length-2];
                _SWField[1] = abyteArray[abyteArray.length-1];
                if (abyteArray.length-2 >0)
                {
                    if ( (this._answer == null) || (this._answer.length !=(abyteArray.length-2)) )
                    {
                        _answer = new byte[abyteArray.length-2];
                    }
                    System.arraycopy(abyteArray, 0, _answer, 0, abyteArray.length-2);
                }
            }
        }
    }



    public abstract boolean isSuccess();


    public abstract boolean isWarning();

    public abstract boolean isError();


    public abstract String translate();


}
