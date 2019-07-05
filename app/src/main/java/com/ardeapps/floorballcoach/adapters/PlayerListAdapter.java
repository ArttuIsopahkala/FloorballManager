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
    public PlayerListManageListener manageListener = null;
    public PlayerListSelectListener selectListener = null;
    private ArrayList<Player> players = new ArrayList<>();

    PlayerHolder.ViewType type;

    public PlayerListAdapter(Context ctx, PlayerHolder.ViewType type) { // Activity
        this.type = type;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setManageListener(PlayerListManageListener l) {
        manageListener = l;
    }

    public void setSelectListener(PlayerListSelectListener l) {
        selectListener = l;
    }

    public void refreshData() {
        players = new ArrayList<>(AppRes.getInstance().getPlayers().values());
    }

    public void update() {

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
        final PlayerHolder holder = new PlayerHolder(v, type);

        final Player player = players.get(position);

        if(player.getPicture() != null) {
            holder.pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
        }

        Player.Shoots shoots = Player.Shoots.fromDatabaseName(player.getShoots());
        String shootsText = AppRes.getContext().getString(shoots == Player.Shoots.LEFT ? R.string.add_player_shoots_left : R.string.add_player_shoots_right);
        holder.nameNumberShootsText.setText(player.getNameWithNumber(false) + " | " + shootsText);
        holder.positionText.setText(Player.getPositionText(player.getPosition(), false));
        holder.typeText.setText(Player.getTypeText(player.getType()));

        if(type == PlayerHolder.ViewType.MANAGE) {
            holder.statisticsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onStatisticsClick(player);
                }
            });

            holder.playerContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manageListener.onContainerClick(player);
                }
            });
        } else {
            holder.playerContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectListener.onPlayerSelected(player);
                }
            });
        }

        return v;
    }

    public interface PlayerListManageListener {
        void onStatisticsClick(Player player);
        void onContainerClick(Player player);
    }

    public interface PlayerListSelectListener {
        void onPlayerSelected(Player player);
    }
}
