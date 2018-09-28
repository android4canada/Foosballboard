package com.tmg.foosballboard;

/**
 * Created by A.Rasem on 09 / 2018.
 */
public class ResultStatus {
    public static enum ResultCode{
        SUCCESS, ERROR, WARNING
    }
    public ResultCode code;
    public String msg="";
}
