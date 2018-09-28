package com.tmg.foosballboard.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
import com.kcode.permissionslib.main.PermissionCompat;
import com.tmg.foosballboard.DBHandler;
import com.tmg.foosballboard.R;
import com.tmg.foosballboard.ResultStatus;
import com.tmg.foosballboard.models.GameRecord;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVars();
    }

    private void initVars() {

        Button bUseCurrent = findViewById(R.id.bUseCurrent);
        bUseCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileLoaded();
            }
        });


        Button bLoadFile = findViewById(R.id.bLoadFile);
        bLoadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionCompat.Builder builder = new PermissionCompat.Builder(MainActivity.this);
                builder.addPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
                builder.addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        showFilePicker();
                    }

                    @Override
                    public void onDenied(String permission) {
                        Log.e(TAG, permission + "Denied");
                    }
                });
                builder.build().request();


            }
        });
    }


    private void showFilePicker() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);

        FilePickerDialog dialog = new FilePickerDialog(this, properties);
        dialog.setTitle(getString(R.string.main_file_picker_title));
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (files != null && files.length > 0) {
                    handleFilePicked(files[0]);
                }
            }
        });
        dialog.show();
    }

    private void handleFilePicked(String path) {
        new importFile(this, path).execute();
    }


    private void fileLoaded() {
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }


    public class importFile extends AsyncTask<Void, Object, ResultStatus> {

        private ProgressDialog pDiag;
        Context mContext;
        String path;
        DBHandler dbHandler;

        public importFile(Context con, String path) {
            mContext = con;
            this.path = path;
            dbHandler = DBHandler.getInstance(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {

            showProgressDailog(getString(R.string.progress_reading_file));
        }

        @Override
        protected ResultStatus doInBackground(Void... params) {
            // let's start by clearing the database
            dbHandler.clear_refresh_DB();

            ResultStatus res = new ResultStatus();
            List<GameRecord> gameRecords_arr = new ArrayList<GameRecord>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                String str = null;
                boolean firstLinePassed = false;
                boolean warning = false;
                while ((str = br.readLine()) != null) {
                    if (!firstLinePassed) {
                        firstLinePassed = true;
                        continue;
                    }
                    String strTokens[] = str.split(",", -1);
                    //check if we have the expected number of parameters
                    if (strTokens == null || strTokens.length != 4) {
                        warning = true; // ignore the line and move on to the next line
                        Log.w(TAG, "strTokens is null");
                        continue;
                    }
                    //check if the scores are numbers
                    if (!StringUtils.isNumeric(strTokens[1]) || !StringUtils.isNumeric(strTokens[3])) {
                        warning = true; // ignore the line and move on to the next line
                        Log.w(TAG, String.format("Scores not numeric %s %s %s %s", strTokens[0], strTokens[1], strTokens[2], strTokens[3]));
                        continue;
                    }
                    String player1 = strTokens[0];
                    int score1 = Integer.parseInt(strTokens[1]);
                    String player2 = strTokens[2];
                    int score2 = Integer.parseInt(strTokens[3]);
                    //check if the scores are positive
                    if (score1 < 0 || score2 < 0) {
                        warning = true; // ignore the line and move on to the next line
                        Log.w(TAG, String.format("Scores less than 0 %s %s %s %s", strTokens[0], strTokens[1], strTokens[2], strTokens[3]));
                        continue;
                    }
                    GameRecord rec = new GameRecord(player1, score1, player2, score2);
                    gameRecords_arr.add(rec);

                }
                if (!firstLinePassed || gameRecords_arr.isEmpty()) {
                    res.code = ResultStatus.ResultCode.ERROR;
                    res.msg = getString(R.string.main_reading_file_empty_err);
                    Log.e(TAG, "Empty file");

                    return res;
                }
                for (GameRecord rec : gameRecords_arr) {
                    boolean success = dbHandler.addGame(rec);
                    if (!success) {
                        res.code = ResultStatus.ResultCode.ERROR;
                        res.msg = getString(R.string.main_reading_file_db_err);
                        Log.e(TAG, "DB add error for " + rec.toString());
                        return res;
                    }
                }

                if (warning) {
                    res.code = ResultStatus.ResultCode.WARNING;
                    res.msg = getString(R.string.main_reading_file_warning);
                    return res;
                }
            } catch (IOException e) {
                res.code = ResultStatus.ResultCode.ERROR;
                res.msg = getString(R.string.main_reading_file_err);
                Log.e(TAG, "File read IO exception", e);

                return res;
            }

            res.code = ResultStatus.ResultCode.SUCCESS;

            return res;
        }


        @Override
        protected void onPostExecute(ResultStatus result) {
            hideProgressDialog();
            switch (result.code) {
                case SUCCESS:
                    fileLoaded();
                    break;
                case ERROR:
                case WARNING:
                    Toast.makeText(MainActivity.this, result.msg, Toast.LENGTH_LONG).show();
                    break;
            }

        }

    }


}
