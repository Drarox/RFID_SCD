/*
  * Author                    :  MMY Application Team
  * Last committed            :  $Revision: 1708 $
  * Revision of last commit    :  $Rev: 1708 $
  * Date of last commit     :  $Date: 2016-02-28 17:44:48 +0100 (Sun, 28 Feb 2016) $
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
package com.st.nfcv;


import android.util.Log;

import com.st.NFC.EnergyHarvesting;
import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;

public class DVEnergyHarvesting extends EnergyHarvesting {
    private String TAG = "DVEnergyHarvesting";

    public DVEnergyHarvesting() {
        // TODO Auto-generated constructor stub
    }

    // Static register
    @Override
    public int readEHConfig() {
        return DVReadEHconfig(0);
    }

    @Override
    public int writeEHConfig(byte EHconfig) {
        return DVWriteEHconfig(EHconfig);
    }


    // Dynamic Register
    @Override
    public int checkEHConfig() {
        return DVCheckEHconfig();
    }

    @Override
    public int resetEHConfig() {
        return DVResetEHconfig();
    }

    @Override
    public int setEHConfig() {
        return DVSetEHconfig();
    }



    public int writeD0Config(byte EHconfig) {
        int returncd = 0;
        returncd = -1;
        return returncd;
    }

    private int DVReadEHconfig(int currentCfgSelection) {
        Log.d(TAG, " DVReadEHconfig");
        int returncd = 0;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
            SysFileLRHandler sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
            boolean staticregister = currentCfgSelection == 0 ? true : false;
            BasicOperation bop = new BasicOperation(sysHDL.getMaxTransceiveLength());
            if (bop.readRegister(stnfcRegisterHandler.ST25DVRegisterTable.Reg_EH,staticregister) == 0) {
                //bop.getMBBlockAnswer();
                setValueEHConfigByte(Helper.ConvertHexByteToString(bop.getMBBlockAnswer()[1]).toUpperCase());
            } else {
                returncd = -1;
                setValueEHConfigByte("x");
                byte[] res = bop.getMBBlockAnswer();
                if (res != null) {
                    Log.v(TAG, "cmd failed: " + Helper.ConvertHexByteArrayToString(res));
                    returncd = currentTag.reportActionStatus("Cmd failed: " + Helper.ConvertHexByteArrayToString(res),-1);
                }
            }

        } else {
            returncd = -1;
        }
        return returncd;
    }

    private int DVWriteEHconfig(byte EHconfig) {
        int returncd = 0;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
            SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
            BasicOperation bop = new BasicOperation(sysHL.getMaxTransceiveLength());
            if (bop.writeRegister(stnfcRegisterHandler.ST25DVRegisterTable.Reg_EH, (byte) EHconfig,true) == 0) {
                // ok
                setValueEHConfigByte(Helper.ConvertHexByteToString((byte) EHconfig));
                Log.v(TAG, "cmd succeed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                //toastStatus("Register value updated ..." + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()));

            } else {
                // ko
                returncd = -1;
                setValueEHConfigByte("x");
                Log.v(TAG, "cmd failed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                byte[] res = bop.getMBBlockAnswer();
                if (res != null) {
                    Log.v(TAG, "cmd failed: " + Helper.ConvertHexByteArrayToString(res));
                    returncd = currentTag.reportActionStatus("Cmd failed: " + Helper.ConvertHexByteArrayToString(res),-1);
                }
            }

        } else {
            Log.v(TAG, "cmd failed: invalid parameter" );
        }
        return returncd;    }

    public int DVWriteD0config(byte EHconfig) {
        int returncd = 0;
        returncd = -1;
        return returncd;
    }

    private int DVCheckEHconfig() {
        Log.d(TAG, " DVReadEHconfig");
        int returncd = 0;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if (currentTag.getSYSHandler() instanceof SysFileLRHandler) {
            SysFileLRHandler sysHDL = (SysFileLRHandler) currentTag.getSYSHandler();
            boolean staticregister = false;
            BasicOperation bop = new BasicOperation(sysHDL.getMaxTransceiveLength());
            if (bop.readRegister(stnfcRegisterHandler.ST25DVRegisterTable.Reg_EH,staticregister) == 0) {
                //bop.getMBBlockAnswer();
                setValueEHEnableByte(Helper.ConvertHexByteToString(bop.getMBBlockAnswer()[1]).toUpperCase());
            } else {
                returncd = -1;
                byte[] res = bop.getMBBlockAnswer();
                if (res != null) {
                    Log.v(TAG, "cmd failed: " + Helper.ConvertHexByteArrayToString(res));
                    returncd = currentTag.reportActionStatus("Cmd failed: " + Helper.ConvertHexByteArrayToString(res),-1);
                }
                setValueEHEnableByte("x");
            }

        } else {
            returncd = -1;
        }
        return returncd;
    }

    private int DVResetEHconfig() {
        int returncd = 0;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
            SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
            BasicOperation bop = new BasicOperation(sysHL.getMaxTransceiveLength());
            String value = getValueEHEnableByte();
            byte registerValue = 0x00;
            if (value != null && value.length()>0) {
                registerValue = Helper.ConvertStringToHexByte(value);
                registerValue = (byte) (registerValue & 0x0E);

            } else {
                registerValue = 0x00;
            }

            if (bop.writeRegister(stnfcRegisterHandler.ST25DVRegisterTable.Reg_EH, registerValue,false) == 0) {
                // ok
                setValueEHEnableByte(Helper.ConvertHexByteToString((byte) 0x00));
                byte[] res = bop.getMBBlockAnswer();
                if (res != null) Log.v(TAG, "cmd succeed: " + Helper.ConvertHexByteArrayToString(res));
                //toastStatus("Register value updated ..." + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()));

            } else {
                // ko
                returncd = -1;
                setValueEHEnableByte("x");
                byte[] res = bop.getMBBlockAnswer();
                if (res != null) {
                    Log.v(TAG, "cmd failed: " + Helper.ConvertHexByteArrayToString(res));
                    returncd = currentTag.reportActionStatus("Cmd failed: " + Helper.ConvertHexByteArrayToString(res),-1);
                }
            }

        } else {
            Log.v(TAG, "cmd failed: invalid parameter" );
        }
        return returncd;
    }

    private int DVSetEHconfig() {

        int returncd = 0;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();

        if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
            SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
            BasicOperation bop = new BasicOperation(sysHL.getMaxTransceiveLength());
            String value = getValueEHEnableByte();
            byte registerValue = 0x01;
            if (value != null && value.length()>0) {
                registerValue = Helper.ConvertStringToHexByte(value);
                registerValue = (byte) (registerValue | 0x01);

            } else {
                registerValue = 0x01;
            }


            if (bop.writeRegister(stnfcRegisterHandler.ST25DVRegisterTable.Reg_EH, registerValue,false) == 0) {
                // ok
                setValueEHEnableByte(Helper.ConvertHexByteToString(registerValue));
                Log.v(TAG, "cmd succeed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                //toastStatus("Register value updated ..." + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()));

            } else {
                // ko
                returncd = -1;
                setValueEHEnableByte("x");
                byte[] res = bop.getMBBlockAnswer();
                if (res != null) {
                    Log.v(TAG, "cmd failed: " + Helper.ConvertHexByteArrayToString(res));
                    returncd = currentTag.reportActionStatus("Cmd failed: " + Helper.ConvertHexByteArrayToString(res),-1);
                }
            }

        } else {
            Log.v(TAG, "cmd failed: invalid parameter" );
        }
        return returncd;
    }

}
