package com.tmg.foosballboard.models;

/**
 * Created by A.Rasem on 09 / 2018.
 */
public class RankingInfo {
    private String playerName;
    private int details;
    public RankingInfo(){
        playerName="";
        details=0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getDetails() {
        return details;
    }

    public void setDetails(int details) {
        this.details = details;
    }
}
