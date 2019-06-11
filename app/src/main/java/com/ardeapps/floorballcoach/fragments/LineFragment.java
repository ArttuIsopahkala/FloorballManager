package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.SelectPlayerDialogFragment;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.Iterator;
import java.util.Map;


public class LineFragment extends Fragment {

    public interface Listener {
        void onLineChanged(Line line, String playerId);
    }

    Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    public void update() {
        setCardView(card_lw, Player.Position.LW);
        setCardView(card_c, Player.Position.C);
        setCardView(card_rw, Player.Position.RW);
        setCardView(card_ld, Player.Position.LD);
        setCardView(card_rd, Player.Position.RD);
    }

    LinearLayout card_lw;
    LinearLayout card_c;
    LinearLayout card_rw;
    LinearLayout card_ld;
    LinearLayout card_rd;
    Line line;
    int lineNumber;

    public void setLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line, container, false);

        card_lw = v.findViewById(R.id.card_lw);
        card_c = v.findViewById(R.id.card_c);
        card_rw = v.findViewById(R.id.card_rw);
        card_ld = v.findViewById(R.id.card_ld);
        card_rd = v.findViewById(R.id.card_rd);

        update();

        return v;
    }

    private void setCardView(LinearLayout card, final Player.Position position) {
        final String pos = position.toDatabaseName();

        ImageView pictureImage = card.findViewById(R.id.pictureImage);
        IconView addIcon = card.findViewById(R.id.addIcon);
        TextView nameText = card.findViewById(R.id.nameText);

        // Default view
        addIcon.setVisibility(View.VISIBLE);
        pictureImage.setVisibility(View.GONE);
        nameText.setText(getString(R.string.select));

        if(line != null) {
            String playerId = line.getPlayerIdMap().get(pos);
            Player player = AppRes.getInstance().getPlayers().get(playerId);
            nameText.setText(StringUtils.getPlayerName(playerId));

            if (player != null) {
                addIcon.setVisibility(View.GONE);
                pictureImage.setVisibility(View.VISIBLE);
                if (player.getPicture() != null) {
                    pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
                } else {
                    pictureImage.setImageResource(R.drawable.default_picture);
                }
            }
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SelectPlayerDialogFragment dialog = new SelectPlayerDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "Valitse pelaaja");
                dialog.setListener(new SelectPlayerDialogFragment.SelectPlayerDialogListener() {
                    @Override
                    public void onPlayerSelected(Player player) {
                        String playerId = player.getPlayerId();
                        if(line == null) {
                            line = new Line();
                            line.setLineNumber(lineNumber);
                        }
                        // Remove existing player if he is in same line in different position
                        Iterator<Map.Entry<String, String>> it = line.getPlayerIdMap().entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, String> entry = it.next();
                            String position = entry.getKey();
                            String id = entry.getValue();
                            if(id.equals(playerId) && !pos.equals(position)) {
                                it.remove();
                            }
                        }

                        line.getPlayerIdMap().put(pos, playerId);

                        mListener.onLineChanged(line, playerId);
                    }

                    @Override
                    public void onPlayerRemoved() {
                        // No line created or player not selected and empty clicked
                        if(line == null) {
                            dialog.dismiss();
                            return;
                        }

                        String playerId = line.getPlayerIdMap().get(pos);
                        if(playerId == null) {
                            dialog.dismiss();
                            return;
                        }

                        line.getPlayerIdMap().remove(pos);

                        mListener.onLineChanged(line, playerId);
                    }
                });
            }
        });
    }

}
