package com.tmg.foosballboard.models;

/**
 * Created by A.Rasem on 09 / 2018.
 */
public class GameRecord {

    private String winner, loser;
    private int score1, score2;
    private long id;

    public GameRecord() {
        winner = loser = "";
        score1 = score2 = 0;
        id = 0;
    }

    public GameRecord(String player1, int score1, String player2, int score2) {
        id = 0;
        initValues(player1, score1, player2, score2);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void copyValuesFrom(GameRecord tmp) {

        winner = tmp.getWinner();
        this.score1 = tmp.getScore1();
        loser = tmp.getLoser();
        this.score2 = tmp.getScore2();

    }

    public void initValues(String player1, int score1, String player2, int score2) {
        if (score1 >= score2) {
            winner = player1;
            this.score1 = score1;
            loser = player2;
            this.score2 = score2;
        } else {
            winner = player2;
            this.score1 = score2;
            loser = player1;
            this.score2 = score1;

        }
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "winner='" + winner + '\'' +
                ", loser='" + loser + '\'' +
                ", score1=" + score1 +
                ", score2=" + score2 +
                ", id=" + id +
                '}';
    }
}
