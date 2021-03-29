package com.example.igordev.financeiro.util;

import android.app.ProgressDialog;

/**
 * Created by igordev on 09/07/17.
 */

public class ProgressDialogClose {
    public static void closeProgress(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
