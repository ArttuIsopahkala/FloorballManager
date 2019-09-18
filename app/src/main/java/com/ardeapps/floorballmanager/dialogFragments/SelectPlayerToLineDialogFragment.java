package com.ardeapps.floorballmanager.dialogFragments;

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
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.PlayerListAdapter;
import com.ardeapps.floorballmanager.objects.Player;

import java.util.ArrayList;
import java.util.Collections;

public class SelectPlayerToLineDialogFragment extends DialogFragment {

    TextView linePositionText;
    int lineNumber;

    SelectPlayerDialogListener mListener = null;

    public void setListener(SelectPlayerDialogListener l) {
        mListener = l;
    }
    ListView playerList;
    Button cancelButton;
    Button clearButton;
    PlayerListAdapter adapter;
    Player.Position position;

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setPosition(Player.Position position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(AppRes.getActivity(), false);
        adapter.setSelectListener(player -> {
            dismiss();
            mListener.onPlayerSelected(player);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_select_player_to_line, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        linePositionText = v.findViewById(R.id.linePositionText);
        playerList = v.findViewById(R.id.playerList);
        cancelButton = v.findViewById(R.id.cancelButton);
        clearButton = v.findViewById(R.id.clearButton);

        linePositionText.setText(getString(R.string.line_number, String.valueOf(lineNumber)) + ": " + Player.getPositionText(position.toDatabaseName(), false));

        playerList.setAdapter(adapter);

        ArrayList<Player> players = AppRes.getInstance().getActivePlayers();
        // Sort by position
        Collections.sort(players, (o1, o2) -> {
            Player.Position position1 = Player.Position.fromDatabaseName(o1.getPosition());
            Player.Position position2 = Player.Position.fromDatabaseName(o2.getPosition());
            if (position1 == position && position2 != position) {
                return -1;
            } else if (position2 == position && position1 != position) {
                return 1;
            }
            return 0;
        });

        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();

        clearButton.setOnClickListener(v12 -> {
            dismiss();
            mListener.onPlayerRemoved();
        });

        cancelButton.setOnClickListener(v1 -> dismiss());
        return v;
    }

    // This sets dialog full screen
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setLayout(width, height);
        }
    }

    public interface SelectPlayerDialogListener {
        void onPlayerSelected(Player player);

        void onPlayerRemoved();
    }
}
