package com.tmg.foosballboard.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.tmg.foosballboard.R;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog progressDiag = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public void showProgressDailog(String msg){
        if(progressDiag!=null && !progressDiag.isShowing()){
           return;
        }else{
            progressDiag = new ProgressDialog(this);
            progressDiag.setIndeterminate(true);
            if(msg == null) {
                progressDiag.setMessage("Loading....");
            }else{
                progressDiag.setMessage(msg);
            }
            progressDiag.show();
        }
    }
    public void hideProgressDialog(){
        if(progressDiag!=null && progressDiag.isShowing()){
            progressDiag.dismiss();
        }
    }
    public void displayAlert(String msg){
        AlertDialog diag =  new AlertDialog.Builder(this).create();
        diag.setTitle(getString(R.string.dialog_error));
        diag.setMessage(msg);
        diag.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_ok_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        diag.show();

    }
}
