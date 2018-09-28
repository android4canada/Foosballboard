package com.tmg.foosballboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tmg.foosballboard.models.GameRecord;
import com.tmg.foosballboard.models.RankingInfo;

import java.util.ArrayList;

public class DBHandler {
    private static final String TAG = "DBHandler";
	public static DBHandler dbHandler = null;
	public static DBHandler getInstance(Context ctx){
		if(dbHandler ==null){
			dbHandler = new DBHandler(ctx);
			dbHandler.initDB();
		}
		return dbHandler;
	}
	public static final String KEY_ROWID = "_id";

	//Property Info table
	private static final String DATABASE_GAMES_TABLE = "GamesTable";
	public static final String KEY_WINNER = "Winner";
	public static final String KEY_SCORE1= "Score1";
	public static final String KEY_LOSER= "Loser";
	public static final String KEY_SCORE2= "Score2";

	private static final String DATABASE_NAME = "database.db";


    public static final int DATABASE_VERSION = 1;
    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

	
	private class DbHelper extends SQLiteOpenHelper {
       
		public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

        @Override
        public void onCreate(SQLiteDatabase db) {


            db.execSQL("CREATE TABLE " + DATABASE_GAMES_TABLE + " (" +
                    KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_WINNER + " TEXT, " +
                    KEY_SCORE1 + " INTEGER, " +
                    KEY_LOSER + " TEXT, " +
                    KEY_SCORE2 + " INTEGER );"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Just incase we need it
            switch(oldVersion){

            }
        }
    }

    public DBHandler(Context c){
        ourContext = c;
    }
    public DBHandler initDB() throws SQLException {

        ourHelper = new DbHelper (ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        //dbHelper.getHelper(ourContext);
        //ourDatabase = DbHelper.getDBConnection();

        return this;
    }
    public void close(){
        ourHelper.close();

    }
    public void clear_refresh_DB(){
        ourDatabase.delete(DATABASE_GAMES_TABLE, null, null);
        ourHelper.close();
        initDB();

    }


	public boolean addGame(GameRecord gameRecord) {
		
		if(gameRecord == null){
		    return false;
        }
		ContentValues cv = new ContentValues();

		cv.put(KEY_WINNER, gameRecord.getWinner());
        cv.put(KEY_SCORE1, gameRecord.getScore1());
        cv.put(KEY_LOSER, gameRecord.getLoser());
        cv.put(KEY_SCORE2, gameRecord.getScore2());

        long ret = ourDatabase.insert(DATABASE_GAMES_TABLE, null, cv);
		if(ret ==-1){
		    return false;
        }
        gameRecord.setId(ret);
        return true;
	}
	



	public ArrayList<GameRecord> getAllGameRecords() {

		Cursor  c = ourDatabase.query(DATABASE_GAMES_TABLE, null, null, null, null, null, null);

		ArrayList<GameRecord> gameRecord_arr = new ArrayList<GameRecord>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            GameRecord gameRecord = new GameRecord();
            gameRecord.setId(c.getInt(c.getColumnIndex(KEY_ROWID)));
            gameRecord.setWinner(c.getString(c.getColumnIndex(KEY_WINNER)));
            gameRecord.setScore1(c.getInt(c.getColumnIndex(KEY_SCORE1)));
            gameRecord.setLoser(c.getString(c.getColumnIndex(KEY_LOSER)));
            gameRecord.setScore2(c.getInt(c.getColumnIndex(KEY_SCORE2)));

            gameRecord_arr.add(gameRecord);
		}

		return gameRecord_arr;
	}

    public ArrayList<RankingInfo> getRankingByWins() {

        String sql =  String.format("select count(%s) as wins, %s from %s Group By %s" +
                        " Union ALL " +
                        "select DISTINCT 0, %s from %s where %s Not in (select %s from %s) " +
                        "ORDER by wins desc",
                KEY_WINNER, KEY_WINNER, DATABASE_GAMES_TABLE, KEY_WINNER, KEY_LOSER,DATABASE_GAMES_TABLE,KEY_LOSER,KEY_WINNER, DATABASE_GAMES_TABLE);

        Log.i(TAG, "getRankingByWins "+sql);
        Cursor  c = ourDatabase.rawQuery(sql ,null);

        ArrayList<RankingInfo> rankingInfo_arr = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            RankingInfo rankingInfo = new RankingInfo();
            rankingInfo.setPlayerName(c.getString(c.getColumnIndex(KEY_WINNER)));
            rankingInfo.setDetails(c.getInt(c.getColumnIndex("wins")));

            rankingInfo_arr.add(rankingInfo);
        }

        return rankingInfo_arr;
    }
    public ArrayList<RankingInfo> getRankingByGamesPlayed() {

        String sql =  String.format("select SUM(played) as total, name from (select count(%s) as played, " +
                        "%s as name from %s Group By %s \n" +
                        " UNION ALL " +
                        "select count(%s) as played, %s as name  from %s Group By %s) group by name ORDER by total desc",
                KEY_WINNER, KEY_WINNER, DATABASE_GAMES_TABLE, KEY_WINNER, KEY_LOSER,KEY_LOSER, DATABASE_GAMES_TABLE, KEY_LOSER);

        Log.i(TAG, "getRankingByGamesPlayed "+sql);
        Cursor  c = ourDatabase.rawQuery(sql ,null);

        ArrayList<RankingInfo> rankingInfo_arr = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            RankingInfo rankingInfo = new RankingInfo();
            rankingInfo.setPlayerName(c.getString(c.getColumnIndex("name")));
            rankingInfo.setDetails(c.getInt(c.getColumnIndex("total")));

            rankingInfo_arr.add(rankingInfo);
        }

        return rankingInfo_arr;
    }
    /*  This will get players, their wins and their total games played
    public ArrayList<RankingInfo> getSummaryByWinsAndGamesPlayed() {

        String sql =  String.format("select D.total, D.player2, A.wins from (select SUM(B.played) as total , B.player as player2 from \n" +
                        "(select count(%s) as played, %s as player from %s Group By %s \n" +
                        " UNION ALL " +
                        "select count(%s) as played, %s as player from %s Group By %s) B group by B.player)D " +
                        "LEFT JOIN (select count(%s) as wins, " +
                        "%s as player from %s Group By %s) A on A.player = D.player2",
                KEY_WINNER, KEY_WINNER, DATABASE_GAMES_TABLE, KEY_WINNER, KEY_LOSER,KEY_LOSER, DATABASE_GAMES_TABLE, KEY_LOSER,
                KEY_WINNER, KEY_WINNER, DATABASE_GAMES_TABLE,KEY_WINNER);
        Cursor  c = ourDatabase.rawQuery(sql ,null);

        ArrayList<RankingInfo> rankingInfo_arr = new ArrayList<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            RankingInfo rankingInfo = new RankingInfo();
            // load data
            rankingInfo_arr.add(rankingInfo);
        }

        return rankingInfo_arr;
    }*/

	public boolean updateGameRecord(GameRecord gameRecord) {

        if(gameRecord == null){
            return false;
        }
		ContentValues cv = new ContentValues();

		cv.put(KEY_ROWID, gameRecord.getId());
        cv.put(KEY_WINNER, gameRecord.getWinner());
        cv.put(KEY_SCORE1, gameRecord.getScore1());
        cv.put(KEY_LOSER, gameRecord.getLoser());
        cv.put(KEY_SCORE2, gameRecord.getScore2());

		long ret = ourDatabase.update(DATABASE_GAMES_TABLE, cv, KEY_ROWID + "=" + gameRecord.getId(), null);
	    if(ret ==0){
	        return false;
        }
        return true;
	}

    public boolean deleteGameRecordById(long Id) {
        String idStr = null;
        try{
           idStr = String.valueOf(Id);
        }catch(NumberFormatException e){
            return false;
        }

        long ret = ourDatabase.delete(DATABASE_GAMES_TABLE,KEY_ROWID + "=" + idStr, null);
        if(ret ==0){
            return false;
        }
        return true;
    }

}
