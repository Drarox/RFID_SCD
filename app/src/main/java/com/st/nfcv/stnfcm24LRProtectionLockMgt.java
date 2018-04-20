package com.st.nfcv;

import com.st.NFC.NFCApplication;
import com.st.NFC.NFCTag;
import com.st.NFC.STNfcTagVHandler;
import com.st.util.GenErrorAppReport;

import android.nfc.tech.Ndef;
import android.util.Log;

public class stnfcm24LRProtectionLockMgt {
    private String TAG = "stnfcm24LRProtectionLockMgt";

    public stnfcm24LRProtectionLockMgt() {
        // TODO Auto-generated constructor stub
    }

    public int presentPassword(Ndef ndefTag, byte[] password, byte pwdnumber) {
        Log.d(TAG, " presentPassword");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt<= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " presentPassword Action");
            // STNfcTagVHandler tgh =
            // (STNfcTagVHandler)(currentTag.getSTTagHandler());
            GenErrorAppReport err = ((STNfcTagVHandler) (currentTag.getSTTagHandler())).presentPassword(pwdnumber,password);
            returncd = currentTag.reportActionStatus(err.m_err_text, err.m_err_value);
            // this.finish();

        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }


    public int writePassword(Ndef ndefTag, byte[] password, byte pwdnumber) {
        Log.d(TAG, " writePassword");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt<= 10) {

            try {
                Thread.sleep(10);
                cpt++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " writePassword Action");
            // STNfcTagVHandler tgh =
            // (STNfcTagVHandler)(currentTag.getSTTagHandler());
            GenErrorAppReport err = ((STNfcTagVHandler) (currentTag.getSTTagHandler())).writePassword(pwdnumber, password);
            returncd = currentTag.reportActionStatus(err.m_err_text + "code: " + err.m_err_value, err.m_err_value);
            // this.finish();

        } else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }


    public int updateProtectionLockSector(Ndef ndefTag, int block, byte lLockConfig, byte lPasswordNumber) {
        Log.d(TAG, " updateProtectionLockSector");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt<= 10) {

            try {
                Thread.sleep(10);
                cpt ++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*
         * try { ndefTag.close(); } catch (IOException e) {
         * Log.v(this.getClass().getName(),
         * "Exchange  Failure - Close exception"); e.printStackTrace(); }
         */
        if (ret) {
            Log.d(TAG, " presentPassword Action");
            // STNfcTagVHandler tgh =
            // (STNfcTagVHandler)(currentTag.getSTTagHandler());
            byte[] SectorNumberAddress = null;
            byte LockSectorByte = (byte)0x00;

            int intSectorAddress = block * 0x20;
            SectorNumberAddress = Helper.ConvertIntTo2bytesHexaFormat(intSectorAddress);
            LockSectorByte = (byte)((byte)(lLockConfig<<1) | (byte)(lPasswordNumber<<3) | (byte)0x01);

            GenErrorAppReport err = ((STNfcTagVHandler) (currentTag.getSTTagHandler())).lockSector(SectorNumberAddress,
                    LockSectorByte);
            returncd = currentTag.reportActionStatus(err.m_err_text + "code: " + err.m_err_value, err.m_err_value);
            // this.finish();

        }else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }


    public int updateProtectionLockZone(int zone, byte lLockConfig, byte lPasswordNumber) {
        Log.d(TAG, " updateProtectionLockZone");
        int returncd = 0;
        boolean ret = true;
        NFCApplication currentApp = NFCApplication.getApplication();
        NFCTag currentTag = currentApp.getCurrentTag();
        long cpt = 0;

        while ((ret = currentTag.pingTag()) != true && cpt<= 10) {

            try {
                Thread.sleep(10);
                cpt ++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        stnfcRegisterHandler.ST25DVRegisterTable reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ1SS;
        switch (zone){
            case 0:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ1SS;
                break;
            case 1:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ2SS;
                break;
            case 2:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ3SS;
                break;
            case 3:
                reg = stnfcRegisterHandler.ST25DVRegisterTable.Reg_RFZ4SS;
                break;
            default:
                break;
        }
        int LockSectorByte = (byte)0x00;
        LockSectorByte = (byte)(((lLockConfig & (byte)0x03))<<2) | (byte)((lPasswordNumber & 0x03));

        if (ret) {
            Log.d(TAG, " updateProtectionLockZone Action");
            if ( currentTag.getSYSHandler() instanceof SysFileLRHandler) {
                SysFileLRHandler sysHL = (SysFileLRHandler) currentTag.getSYSHandler();
                stnfcm24LRBasicOperation bop = new stnfcm24LRBasicOperation(sysHL.getMaxTransceiveLength());
                boolean staticRegister = true;
                if (bop.writeRegister(reg, (byte) LockSectorByte, staticRegister) == 0) {
                    // ok
                    ret = true;
                    sysHL.mST25DVRegister.setRegisterValue(reg,(byte)LockSectorByte);
                    Log.v(this.getClass().getName(), "cmd succeed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                    //toastStatus("Register value updated ..." + Helper.ConvertHexByteArrayToString(bop.getBlockAnswer()));
                    returncd = currentTag.reportActionStatusTransparent("cmd succeed ... ", 0);

                } else {
                    // ko
                    if (bop.getMBBlockAnswer() != null) {
                        Log.v(this.getClass().getName(), "cmd failed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()));
                        returncd = currentTag.reportActionStatus("cmd failed: " + Helper.ConvertHexByteArrayToString(bop.getMBBlockAnswer()), -1);

                    } else {
                        Log.v(this.getClass().getName(), "cmd failed, no tag answer ");
                        returncd = currentTag.reportActionStatus("cmd failed, no tag answer ", -1);
                    }
                }

            } else {
                Log.v(this.getClass().getName(), "cmd failed: invalid parameter" );
                returncd = currentTag.reportActionStatus("cmd failed: invalid parameter", -1);
            }

        }else {
            returncd = currentTag.reportActionStatus("Tag not on the field...", -1);
        }
        return returncd;
    }



}
