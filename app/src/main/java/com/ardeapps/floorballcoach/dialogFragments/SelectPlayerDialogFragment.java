package com.ardeapps.floorballcoach.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.PlayerListAdapter;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.views.PlayerHolder;

import java.util.ArrayList;

public class SelectPlayerDialogFragment extends DialogFragment {

    SelectPlayerDialogListener mListener = null;

    public void setListener(SelectPlayerDialogListener l) {
        mListener = l;
    }

    public interface SelectPlayerDialogListener {
        void onPlayerSelected(Player player);
        void onPlayerRemoved();
    }

    ListView playerList;
    Button cancelButton;
    Button clearButton;
    PlayerListAdapter adapter;
    Player.Position position;

    public void setPosition(Player.Position position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(AppRes.getActivity(), PlayerHolder.ViewType.SELECT);
        adapter.setSelectListener(new PlayerListAdapter.PlayerListSelectListener() {
            @Override
            public void onPlayerSelected(Player player) {
                dismiss();
                mListener.onPlayerSelected(player);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_select_player, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        playerList = v.findViewById(R.id.playerList);
        cancelButton = v.findViewById(R.id.cancelButton);
        clearButton = v.findViewById(R.id.clearButton);

        playerList.setAdapter(adapter);

        ArrayList<Player> players = new ArrayList<>();
        for(Player player : AppRes.getInstance().getPlayers().values()) {
            if(player.isActive()) {
                players.add(player);
            }
        }
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mListener.onPlayerRemoved();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }
}
