package com.tmg.foosballboard.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmg.foosballboard.activities.BaseActivity;

/**
 * Created by A.Rasem on 05 / 2017.
 */

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void showProgressDialog(String msg){
        if(getActivity()!=null) {
            ((BaseActivity)getActivity()).showProgressDailog(msg);
        }
    }
    protected void hideProgressDialog(){
        if(getActivity()!=null) {
            ((BaseActivity) getActivity()).hideProgressDialog();
        }
    }



}
