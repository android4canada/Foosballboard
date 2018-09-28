package com.tmg.foosballboard.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.tmg.foosballboard.DBHandler;
import com.tmg.foosballboard.R;
import com.tmg.foosballboard.ResultStatus;
import com.tmg.foosballboard.activities.BaseActivity;
import com.tmg.foosballboard.adapters.HistoryRecyclerViewAdapter;
import com.tmg.foosballboard.models.GameRecord;

import java.util.ArrayList;
import java.util.List;

import thebat.lib.validutil.ValidUtils;


public class HistoryFragment extends BaseFragment {
    private static final String TAG = "HistoryFragment";
    RecyclerView rvHistory;
    FloatingActionButton fabAdd;
    HistoryRecyclerViewAdapter mAdapter;
    private List<GameRecord> gameRecords_arr = new ArrayList<>();

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();// Intentionally not doing singleton pattern so I go through the full intialization
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        initVars(view);
        return view;
    }


    private void initVars(View view) {
        rvHistory = view.findViewById(R.id.rvHistory);
        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAddDialog();
            }
        });

        mAdapter = new HistoryRecyclerViewAdapter(gameRecords_arr, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.setAdapter(mAdapter);
        //swipe lef to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                new deleteDataAsyncTask(position).execute();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvHistory);

        mAdapter.setItemLongClickListener(new HistoryRecyclerViewAdapter.ItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                editScore(position);
            }
        });

    }

    // this is so we ensure activity is not null
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new populateDataAsyncTask().execute();
    }

    private void displayAddDialog() {
        final AlertDialog diag = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_gamescore_diag, null);
        diag.setView(dialogView);

        final EditText etPlayer1 = dialogView.findViewById(R.id.etPlayer1);
        final EditText etScore1 = dialogView.findViewById(R.id.etScore1);
        final EditText etPlayer2 = dialogView.findViewById(R.id.etPlayer2);
        final EditText etScore2 = dialogView.findViewById(R.id.etScore2);

        diag.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.diag_add_score_positive_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!ValidUtils.validateEditTexts(etPlayer1, etScore1, etPlayer2, etScore2)) {
                    Toast.makeText(getActivity(), R.string.diag_add_score_err_emptyfields, Toast.LENGTH_SHORT).show();
                    return;
                }

                String player1 = etPlayer1.getText().toString().trim();
                String score1 = etScore1.getText().toString().trim();
                String player2 = etPlayer2.getText().toString().trim();
                String score2 = etScore2.getText().toString().trim();
                GameRecord gameRecord = new GameRecord(player1, Integer.parseInt(score1), player2, Integer.parseInt(score2));
                new addDataAsyncTask(gameRecord).execute();

            }
        });
        diag.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.diag_add_score_negative_btn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        diag.show();
    }

    private void displayEditDialog(final int gameRecIdx) {
        final AlertDialog diag = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_gamescore_diag, null);
        diag.setView(dialogView);
        final GameRecord gameRec;
        final EditText etPlayer1 = dialogView.findViewById(R.id.etPlayer1);
        final EditText etScore1 = dialogView.findViewById(R.id.etScore1);
        final EditText etPlayer2 = dialogView.findViewById(R.id.etPlayer2);
        final EditText etScore2 = dialogView.findViewById(R.id.etScore2);
        gameRec = gameRecords_arr.get(gameRecIdx);
        etPlayer1.setText(gameRec.getWinner());
        etPlayer2.setText(gameRec.getLoser());
        etScore1.setText(String.valueOf(gameRec.getScore1()));
        etScore2.setText(String.valueOf(gameRec.getScore2()));

        diag.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.diag_edit_score_positive_btn), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!ValidUtils.validateEditTexts(etPlayer1, etScore1, etPlayer2, etScore2)) {
                    Toast.makeText(getActivity(), R.string.diag_add_score_err_emptyfields, Toast.LENGTH_SHORT).show();
                    return;
                }

                String player1 = etPlayer1.getText().toString().trim();
                String score1 = etScore1.getText().toString().trim();
                String player2 = etPlayer2.getText().toString().trim();
                String score2 = etScore2.getText().toString().trim();
                GameRecord gameRecordClone = new GameRecord(player1, Integer.parseInt(score1), player2, Integer.parseInt(score2));
                gameRecordClone.setId(gameRec.getId());
                new editDataAsyncTask(gameRecIdx, gameRecordClone).execute();

            }
        });
        diag.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.diag_add_score_negative_btn),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        diag.show();
    }

    private void editScore(int position) {
        displayEditDialog(position);
    }


    private class populateDataAsyncTask extends AsyncTask<String, Void, List<GameRecord>> {


        DBHandler dbHandler;
        BaseActivity mAct;

        @Override
        protected void onPreExecute() {
            mAct = (BaseActivity) getActivity();
            dbHandler = DBHandler.getInstance(mAct);
            mAct.showProgressDailog(getString(R.string.history_diag_loading));


        }

        @Override
        protected List<GameRecord> doInBackground(String... params) {

            ArrayList<GameRecord> tempGameRecords_arr = dbHandler.getAllGameRecords();

            return tempGameRecords_arr;
        }

        @Override
        protected void onPostExecute(List<GameRecord> result) {
            gameRecords_arr = result;
            mAdapter.setList(gameRecords_arr);
            mAdapter.notifyDataSetChanged();
            mAct.hideProgressDialog();
        }

    }

    private class addDataAsyncTask extends AsyncTask<Void, Void, ResultStatus> {


        DBHandler dbHandler;
        BaseActivity mAct;
        GameRecord recToAdd;

        public addDataAsyncTask(GameRecord rec) {
            recToAdd = rec;
        }

        @Override
        protected void onPreExecute() {
            mAct = (BaseActivity) getActivity();
            dbHandler = DBHandler.getInstance(mAct);
            mAct.showProgressDailog(mAct.getString(R.string.history_diag_adding));


        }

        @Override
        protected ResultStatus doInBackground(Void... params) {

            boolean success = dbHandler.addGame(recToAdd);

            ResultStatus res = new ResultStatus();
            if (success) {
                res.code = ResultStatus.ResultCode.SUCCESS;
            } else {
                res.code = ResultStatus.ResultCode.ERROR;
                res.msg = mAct.getString(R.string.history_add_score_err);
            }
            return res;
        }

        @Override
        protected void onPostExecute(ResultStatus result) {
            switch (result.code) {
                case SUCCESS:
                    gameRecords_arr.add(recToAdd);
                    mAdapter.notifyDataSetChanged();
                    break;
                case ERROR:
                case WARNING:
                    Toast.makeText(mAct, result.msg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Adding data error " + recToAdd.toString());
                    break;
            }
            mAct.hideProgressDialog();
        }
    }


    private class deleteDataAsyncTask extends AsyncTask<Void, Void, ResultStatus> {


        DBHandler dbHandler;
        BaseActivity mAct;
        int indxToDelete;
        long gameRecordId = -1;

        public deleteDataAsyncTask(int indxToDelete) {
            this.indxToDelete = indxToDelete;
            GameRecord gameRec = gameRecords_arr.get(indxToDelete);
            if (gameRec != null) {
                gameRecordId = gameRecords_arr.get(indxToDelete).getId();
            } else {
                Log.e(TAG, "Error getting game record for  index " + indxToDelete);
            }
        }

        @Override
        protected void onPreExecute() {
            mAct = (BaseActivity) getActivity();
            dbHandler = DBHandler.getInstance(mAct);
            mAct.showProgressDailog(mAct.getString(R.string.history_diag_adding));


        }

        @Override
        protected ResultStatus doInBackground(Void... params) {
            boolean success = false;
            if (gameRecordId != -1) {
                success = dbHandler.deleteGameRecordById(gameRecordId);
            }

            ResultStatus res = new ResultStatus();
            if (success) {
                res.code = ResultStatus.ResultCode.SUCCESS;
            } else {
                res.code = ResultStatus.ResultCode.ERROR;
                res.msg = getString(R.string.history_delete_score_err);
            }
            return res;
        }

        @Override
        protected void onPostExecute(ResultStatus result) {
            switch (result.code) {
                case SUCCESS:
                    gameRecords_arr.remove(indxToDelete);
                    mAdapter.notifyDataSetChanged();
                    break;
                case ERROR:
                case WARNING:
                    Toast.makeText(mAct, result.msg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Adding data error " + gameRecordId);
                    break;
            }
            mAct.hideProgressDialog();
        }
    }




    private class editDataAsyncTask extends AsyncTask<Void, Void, ResultStatus> {


        DBHandler dbHandler;
        BaseActivity mAct;
        int indxToEdit;
        GameRecord clonedRec;

        public editDataAsyncTask(int indxToEdit, GameRecord cloned) {
            this.indxToEdit = indxToEdit;
            clonedRec = cloned;

        }

        @Override
        protected void onPreExecute() {
            mAct = (BaseActivity) getActivity();
            dbHandler = DBHandler.getInstance(mAct);
            mAct.showProgressDailog(getString(R.string.history_diag_saving));


        }

        @Override
        protected ResultStatus doInBackground(Void... params) {
            boolean success = false;
            success = dbHandler.updateGameRecord(clonedRec);


            ResultStatus res = new ResultStatus();
            if (success) {
                res.code = ResultStatus.ResultCode.SUCCESS;
            } else {
                res.code = ResultStatus.ResultCode.ERROR;
                res.msg = getString(R.string.history_edit_score_err);
            }
            return res;
        }

        @Override
        protected void onPostExecute(ResultStatus result) {
            switch (result.code) {
                case SUCCESS:
                    GameRecord rec = gameRecords_arr.get(indxToEdit);
                    rec.copyValuesFrom(clonedRec);
                    mAdapter.notifyDataSetChanged();
                    break;
                case ERROR:
                case WARNING:
                    Toast.makeText(mAct, result.msg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Saving data error " + clonedRec.toString());
                    break;
            }
            mAct.hideProgressDialog();
        }
    }
}



