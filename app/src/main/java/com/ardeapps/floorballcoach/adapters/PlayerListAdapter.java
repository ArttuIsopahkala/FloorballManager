package com.ardeapps.floorballcoach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.views.PlayerHolder;

import java.util.ArrayList;

public class PlayerListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public PlayerListSelectListener selectListener = null;
    private ArrayList<Player> players = new ArrayList<>();

    public PlayerListAdapter(Context ctx) { // Activity
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSelectListener(PlayerListSelectListener l) {
        selectListener = l;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    @Override
    public int getCount() {
        return players.size();
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
        if (v == null) {
            v = inflater.inflate(R.layout.list_item_player, null);
        }
        final PlayerHolder holder = new PlayerHolder(v);

        final Player player = players.get(position);

        if(player.getPicture() != null) {
            holder.pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
        } else {
            holder.pictureImage.setImageResource(R.drawable.default_picture);
        }

        Player.Shoots shoots = Player.Shoots.fromDatabaseName(player.getShoots());
        String shootsText = AppRes.getContext().getString(shoots == Player.Shoots.LEFT ? R.string.add_player_shoots_left : R.string.add_player_shoots_right);
        holder.nameNumberShootsText.setText(player.getNameWithNumber(false) + " | " + shootsText);
        holder.positionText.setText(Player.getPositionText(player.getPosition(), false));

        holder.playerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.onPlayerSelected(player);
            }
        });

        return v;
    }

    public interface PlayerListSelectListener {
        void onPlayerSelected(Player player);
    }
}
