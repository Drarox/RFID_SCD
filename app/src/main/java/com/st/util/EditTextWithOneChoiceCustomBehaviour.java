package com.st.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.st.nfcv.Helper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by MMY Team on 11/9/2016.
 */
public abstract class EditTextWithOneChoiceCustomBehaviour {
    TextView commonTxtView;
    Context ctx;
    protected byte value;
    private String mask;

    protected String title;
    protected String[] fields;
    protected boolean[] checkedFields;



    public EditTextWithOneChoiceCustomBehaviour(Context context, TextView textView, String mask, String title, String[] fields, boolean[] checkedFields) {
        ctx = context;
        commonTxtView = textView;
        this.title = title;
        this.fields = fields;
        this.checkedFields = checkedFields;
        this.mask = mask;

        if (title == null) title = "default title";
        if (fields == null) {
            fields = new String[]{
                    "default_Mode = 0",
                    "default__Mode = 1"
            };

        }
        if (checkedFields == null) {
            checkedFields = new boolean[]{
                    true, //
                    false
            };
        }
        if (mask == null) mask = "[a-fA-F0-9]+";


        final String[] finalFields = fields;
        final boolean[] finalCheckedFields = checkedFields;
        final String finalTitle = title;
        commonTxtView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Build an AlertDialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                int defaultValue = 0;
                int nbElements = finalCheckedFields.length;
                int i=0;
                for (i=0;i<nbElements; i++) {
                    if (finalCheckedFields[i] == true) break;
                }
                defaultValue = i;
                // Convert the color array to list
                final List<String> fieldsList = Arrays.asList(finalFields);
                builder.setSingleChoiceItems(finalFields,defaultValue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Get the current focused item
                        String currentItem = fieldsList.get(which);
                        value = (byte) which;
                        manageValueOnChoice(which);
                        // Notify the current action

                    }
                });
                // Specify the dialog is not cancelable
                builder.setCancelable(false);

                // Set a title for alert dialog
                builder.setTitle(finalTitle);

                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        value = computeValueFromFields();
                        commonTxtView.setText(Helper.ConvertHexByteToString(value));
                    }
                });

                // Set the neutral/cancel button click listener
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the neutral button
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
                return false;
            }
        });

        final String finalMask = mask;
        commonTxtView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //Right after the text is changed
            @Override
            public void afterTextChanged(Editable s) {
                //Store the text on a String
                String text = s.toString();

                //Get the length of the String
                int length = s.length();

            /*If the String length is bigger than zero and it's not
            composed only by the following characters: A to F and/or 0 to 9 */
                if (!text.matches(finalMask) && length > 0) {
                    //Delete the last character
                    s.delete(length - 1, length);
                }
            }
        });
    }

    protected EditTextWithOneChoiceCustomBehaviour() {
    }


    // ====================================================================================
    protected abstract  byte  computeValueFromFields() ;
    // ====================================================================================
    protected abstract  void  manageValueOnChoice(int which) ;
}
