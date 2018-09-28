package com.tmg.foosballboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tmg.foosballboard.R;
import com.tmg.foosballboard.models.GameRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Rasem on 09 / 2018.
 */
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
    public interface ItemLongClickListener  {
        void onItemLongClick(View view, int position);
    }

    private List<GameRecord> gameRecords_arr = new ArrayList<>();
    private Context context;
    private ItemLongClickListener clickListener;



    public HistoryRecyclerViewAdapter(List<GameRecord> list, Context context) {
        this.gameRecords_arr = list;
        this.context = context;
    }
    public void setList(List<GameRecord> list) {
        this.gameRecords_arr = list;
    }
    public List<GameRecord> getList() {
        return gameRecords_arr;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_score_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvPlayer1.setText(gameRecords_arr.get(position).getWinner());
        holder.tvPlayer2.setText(gameRecords_arr.get(position).getLoser());
        holder.tvScore1.setText(String.valueOf(gameRecords_arr.get(position).getScore1()));
        holder.tvScore2.setText(String.valueOf(gameRecords_arr.get(position).getScore2()));


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (clickListener != null) clickListener.onItemLongClick(view, position);
                return true;
            }


        });

    }

    @Override
    public int getItemCount() {
        return gameRecords_arr.size();
    }


    public void setItemLongClickListener(ItemLongClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPlayer1, tvPlayer2, tvScore1, tvScore2;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlayer1 = itemView.findViewById(R.id.tvPlayer1);
            tvPlayer2 = itemView.findViewById(R.id.tvPlayer2);
            tvScore1 = itemView.findViewById(R.id.tvScore1);
            tvScore2 = itemView.findViewById(R.id.tvScore2);

        }
    }
}

