package com.tmg.foosballboard.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tmg.foosballboard.DBHandler;
import com.tmg.foosballboard.R;
import com.tmg.foosballboard.activities.BaseActivity;
import com.tmg.foosballboard.models.RankingInfo;

import java.util.ArrayList;
import java.util.List;


public class RankingFragment extends BaseFragment {
    private static final String TAG = "RankingFragment";
    Spinner sSortingType;
    TableLayout tlRanking;


    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();// Intentionally not doing singleton pattern so I go through the full intialization
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        initVars(view);
        return view;
    }


    private void initVars(View view){
        sSortingType = view.findViewById(R.id.sSortingType);
        ArrayAdapter<String> sortingAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sorting_type));
        sSortingType.setAdapter(sortingAdapter);
        sSortingType.setSelection(0);
        sSortingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                new populateRanking().execute(new Integer(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tlRanking = view.findViewById(R.id.tlRanking);
    }
    // this is so we ensure activity is not null
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new populateRanking().execute(new Integer(0));
    }


    private void loadTable(List<RankingInfo> rankingInfo_arr){
        tlRanking.removeViews(1,tlRanking.getChildCount()-1);
        int rank = 0;
        for(RankingInfo ranking: rankingInfo_arr){
            rank++;
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundResource(R.drawable.trans_table_selector);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            TextView tvRank =  new TextView(getContext());
            tvRank.setLayoutParams(lp);
            tvRank.setGravity(Gravity.CENTER);
            tvRank.setText(String.valueOf(rank));

            TextView tvPlayer =  new TextView(getContext());
            tvPlayer.setLayoutParams(lp);
            tvPlayer.setGravity(Gravity.CENTER);
            tvPlayer.setText(ranking.getPlayerName());


            TextView tvDetails =  new TextView(getContext());
            tvDetails.setLayoutParams(lp);
            tvDetails.setGravity(Gravity.CENTER);
            tvDetails.setText(String.valueOf(ranking.getDetails()));

            tr.addView(tvRank);
            tr.addView(tvPlayer);
            tr.addView(tvDetails);

            /* Add row to TableLayout. */
            tlRanking.addView(tr, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private class populateRanking extends AsyncTask<Integer, Void, List<RankingInfo>> {


        DBHandler dbHandler;
        BaseActivity mAct;
        @Override
        protected void onPreExecute() {
            mAct = (BaseActivity) getActivity();
            dbHandler = DBHandler.getInstance(mAct);
            mAct.showProgressDailog(getString(R.string.ranking_diag_loading));


        }

        @Override
        protected List<RankingInfo>  doInBackground(Integer... params) {
            ArrayList<RankingInfo> rankingInfo_arr = null;
            if(params[0].intValue() == 0) { // get ranking by wins
                rankingInfo_arr = dbHandler.getRankingByWins();
            }else{ // get ranking by games played
                rankingInfo_arr = dbHandler.getRankingByGamesPlayed();
            }
            return rankingInfo_arr;
        }

        @Override
        protected void onPostExecute(List<RankingInfo> rankingInfo_arr) {
            loadTable(rankingInfo_arr);
            mAct.hideProgressDialog();
        }



    }
}
