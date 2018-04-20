package com.st.Fragments.DVRegisters;

import android.content.Context;
import android.widget.TextView;

import com.st.util.EditTextWithXChoiceCustomBehaviour;

/**
 * Created by MMY team on 11/9/2016.
 */
public class EditTextWithXChoiceDVGPOBehaviour extends EditTextWithXChoiceCustomBehaviour {

    public EditTextWithXChoiceDVGPOBehaviour(Context context, TextView textView, byte initRefValue) {

        super(context, textView,"[a-fA-F0-9]+", "GPO register Fields ?", new String[]{
                "GPOEn",
                "RFWriteEn",
                "RFGetMsgEn",
                "RFPutMsgEn",
                "FieldChangeEn",
                "RFInteruptEn",
                "RFBusyEn",
                "RFUserEn"
        }, new boolean[]{
                (initRefValue & 0x80) == 128?true:false, // GPOEn
                (initRefValue & 0x40) == 64?true:false, //  RFWriteEn
                (initRefValue & 0x20) == 32?true:false, // RFGetMsgEn
                (initRefValue & 0x10) == 16?true:false, // RFPutMsgEn
                (initRefValue & 0x08) == 8?true:false, // FieldChangeEn
                (initRefValue & 0x04) == 4?true:false, // RFInteruptEn
                (initRefValue & 0x02) == 2?true:false, // RFBusyEn
                (initRefValue & 0x01) == 1?true:false // RFUserEn
        } );
    }

    @Override
    protected byte computeValueFromFields() {
        byte byteEditViewGPOValue = 0;
        byteEditViewGPOValue = 0;
        int ret = 0;
        int nbFieldCount = checkedFields.length;
        for (int i = 0; i < checkedFields.length; i++) {
            boolean checked = checkedFields[i];
            if (checked) {
                int shiftValue = nbFieldCount-1-i;
                ret = ret | (1 << (shiftValue));
            }
        }
        byteEditViewGPOValue = (byte) ret;
        return byteEditViewGPOValue;
    }


    @Override
    protected void manageValueOnChoice(int which) {
    }
}
