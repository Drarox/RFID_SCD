package com.st.Fragments.DVRegisters;

import android.content.Context;
import android.widget.TextView;

import com.st.util.EditTextWithXChoiceCustomBehaviour;

/**
 * Created by MMY team on 11/9/2016.
 */
public class EditTextWithXChoiceDVRFiZSSBehaviour extends EditTextWithXChoiceCustomBehaviour {

    public EditTextWithXChoiceDVRFiZSSBehaviour(Context context, TextView textView, byte initRefValue) {

        super(context, textView,"[a-dA-D0-9]+", "RFAiSS register Fields (* except A1)?", new String[]{
                    "b0-b1:00 No Pwd",
                    "b0-b1:01 Pwd1",
                    "b0-b1:10 Pwd2",
                    "b0-b1:11 Pwd3",
                    "b2-b3:00  No/Wrong PWD:R&W Present PWD:RorW",
                    "b2-b3:01  No/Wrong PWD:R&NoW Present PWD:RorW",
                    "b2-b3:10* No/Wrong PWD:NoR&NoW Present PWD:RorW",
                    "b2-b3:11* No/Wrong PWD:NoR&NoW Present PWD:RorNoW"
        }, new boolean[]{
                (initRefValue & 0x03) == 0?true:false, //
                (initRefValue & 0x03) == 1?true:false, //
                (initRefValue & 0x03) == 2?true:false, //
                (initRefValue & 0x03) == 3?true:false, //
                (initRefValue & 0x0C) == 0?true:false, //
                (initRefValue & 0x0C) == 4?true:false, //
                (initRefValue & 0x0C) == 8?true:false, //
                (initRefValue & 0x0C) == 12?true:false //
        });
    }

    @Override
    protected byte computeValueFromFields() {
        byte byteEditViewRFZiSSValue = 0;
        int ret = 0;
        int nbFieldCount = checkedFields.length;
        // Manage the PWD
        for (int i = 0; i < nbFieldCount/2; i++) {
            boolean checked = checkedFields[i];
            if (checked) {
                int shiftValue = i;
                ret = ret | i;
            }
        }
        // Manage the Protection
        int protection = 0;
        for (int i = nbFieldCount/2; i <nbFieldCount ; i++) {
            boolean checked = checkedFields[i];
            if (checked) {
                int shiftValue = i-nbFieldCount/2;
                protection = protection | shiftValue;
            }
        }
        byteEditViewRFZiSSValue = (byte) ((byte) ret | (byte)(protection <<2));
        return byteEditViewRFZiSSValue;
    }


    @Override
    protected void manageValueOnChoice(int which) {
/*
        int nbFieldCount = checkedFields.length;
        // Manage the PWD
        if (which < nbFieldCount/2 ) {
            for (int i = 0; i < nbFieldCount/2; i++) {
                boolean checked = checkedFields[i];
                if (checked && which != i ) {
                    checkedFields[i] = false;
                }
            }

        } else {
            for (int i = nbFieldCount/2; i < nbFieldCount; i++) {
                boolean checked = checkedFields[i];
                if (checked && which != i ) {
                    checkedFields[i] = false;
                }
            }

        }
*/

    }
}