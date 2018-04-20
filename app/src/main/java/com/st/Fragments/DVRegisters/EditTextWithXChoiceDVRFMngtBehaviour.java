package com.st.Fragments.DVRegisters;

import android.content.Context;
import android.widget.TextView;

import com.st.util.EditTextWithOneChoiceCustomBehaviour;
import com.st.util.EditTextWithXChoiceCustomBehaviour;

/**
 * Created by MMY team on 11/9/2016.
 */
public class EditTextWithXChoiceDVRFMngtBehaviour extends EditTextWithXChoiceCustomBehaviour {

    public EditTextWithXChoiceDVRFMngtBehaviour(Context context, TextView textView, byte initRefValue) {

        super(context, textView,"[0-3]+", "RF Management Fields?", new String[]{
                "RF_DISABLE ",
                "RF_SLEEP"
        }, new boolean[]{
                (initRefValue & 0x01) == 1?true:false, //
                (initRefValue & 0x02) == 2?true:false

        } );
    }

    @Override
    protected byte computeValueFromFields() {
        byte byteEditViewRFZiSSValue = 0;
        int ret = 0;
        int nbFieldCount = checkedFields.length;
        for (int i = 0; i < nbFieldCount; i++) {
            boolean checked = checkedFields[i];
            if (checked) {
                int shiftValue = i;
                ret = ret | 1<<i;
            }
        }
        byteEditViewRFZiSSValue = (byte) ret;
        return byteEditViewRFZiSSValue;
    }


    @Override
    protected void manageValueOnChoice(int which) {
    }
}
