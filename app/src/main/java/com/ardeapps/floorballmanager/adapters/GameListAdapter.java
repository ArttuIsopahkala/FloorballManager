package com.ardeapps.floorballmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

public class GameListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public Listener mListener = null;
    private ArrayList<Game> games = new ArrayList<>();

    public GameListAdapter(Context ctx) { // Activity
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
        Collections.sort(games, (o1, o2) -> Long.compare(o2.getDate(), o1.getDate()));
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final Holder holder = new Holder();
        if (v == null) {
            v = inflater.inflate(R.layout.list_item_game, null);
        }

        holder.gameContainer = v.findViewById(R.id.gameContainer);
        holder.dateText = v.findViewById(R.id.dateText);
        holder.nameText = v.findViewById(R.id.nameText);
        holder.resultText = v.findViewById(R.id.resultText);

        final Game game = games.get(position);

        holder.dateText.setText(StringUtils.getDateText(game.getDate(), true));

        String nameText;
        if (game.isHomeGame()) {
            nameText = AppRes.getInstance().getSelectedTeam().getName() + " - " + game.getOpponentName();
        } else {
            nameText = game.getOpponentName() + " - " + AppRes.getInstance().getSelectedTeam().getName();
        }
        holder.nameText.setText(nameText);

        String result = "";
        if (game.getHomeGoals() != null) {
            result += game.getHomeGoals() + " - ";
        } else {
            result += "X - ";
        }
        if (game.getAwayGoals() != null) {
            result += game.getAwayGoals();
        } else {
            result += "X";
        }
        holder.resultText.setText(result);

        holder.gameContainer.setOnClickListener(v1 -> mListener.onGameSelected(game));

        return v;
    }

    public interface Listener {
        void onGameSelected(Game game);
    }

    public class Holder {
        TextView dateText;
        TextView nameText;
        TextView resultText;
        RelativeLayout gameContainer;
    }
}
