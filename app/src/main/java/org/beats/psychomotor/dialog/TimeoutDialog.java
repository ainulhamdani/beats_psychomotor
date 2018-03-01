package org.beats.psychomotor.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.beats.psychomotor.R;

/**
 * Created by Johan on 21/02/2018.
 */

public class TimeoutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.timeout).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(TimeoutDialog.this);
            }
        });
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public interface TimeoutDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    TimeoutDialogListener mListener;

    public void setListener(TimeoutDialogListener listener) {
        this.mListener = listener;
    }
}
