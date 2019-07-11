package com.ardeapps.floorballcoach.fragments;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.SelectPlayerDialogFragment;
import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Player.Position;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.LineFragmentData;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class LineFragment extends Fragment implements DataView {

    public interface Listener {
        void onLineChanged(Line line, String playerId);
    }

    Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    public void update() {
        Line line = data.getLine();
        setChemistryText(c_lw_text, null);
        setChemistryText(c_rw_text, null);
        setChemistryText(c_ld_text, null);
        setChemistryText(c_rd_text, null);
        setChemistryText(ld_rd_text, null);
        setChemistryText(ld_lw_text, null);
        setChemistryText(rd_rw_text, null);

        if(line != null) {
            Map<String, ArrayList<Goal>> goals = AppRes.getInstance().getGoalsByGame();
            Map<String, ArrayList<Line>> lines = AppRes.getInstance().linesByGame();
            chemistriesMap = AnalyzerService.getLineChemistry(data.getLine(), goals, lines);
            // TODO remove this test print
            for (Map.Entry<Player.Position, ArrayList<Chemistry>> chemistry : chemistriesMap.entrySet()) {
                Player.Position position = chemistry.getKey();
                ArrayList<Chemistry> chemistries = chemistry.getValue();
                System.out.println(position.toDatabaseName());
                for(Chemistry chem : chemistries) {
                    System.out.println(chem.getComparePosition() + ": " + chem.getChemistryPoints());
                }
            }

            Map<Position, Integer> cp = getCompareChemistries(Position.C);
            setChemistryText(c_lw_text, cp.get(Position.LW));
            setChemistryText(c_rw_text, cp.get(Position.RW));
            setChemistryText(c_ld_text, cp.get(Position.LD));
            setChemistryText(c_rd_text, cp.get(Position.RD));
            cp = getCompareChemistries(Position.LD);
            setChemistryText(ld_rd_text, cp.get(Position.RD));
            setChemistryText(ld_lw_text, cp.get(Position.LW));
            cp = getCompareChemistries(Position.RD);
            setChemistryText(rd_rw_text, cp.get(Position.RW));
        }

        setCardView(card_lw, Position.LW);
        setCardView(card_c, Position.C);
        setCardView(card_rw, Position.RW);
        setCardView(card_ld, Position.LD);
        setCardView(card_rd, Position.RD);
    }

    TextView c_lw_text;
    TextView c_rw_text;
    TextView c_ld_text;
    TextView c_rd_text;
    TextView ld_rd_text;
    TextView ld_lw_text;
    TextView rd_rw_text;
    RelativeLayout card_lw;
    RelativeLayout card_c;
    RelativeLayout card_rw;
    RelativeLayout card_ld;
    RelativeLayout card_rd;

    private LineFragmentData data;
    private Map<Player.Position, ArrayList<Chemistry>> chemistriesMap = new HashMap<>();

    private void setChemistryText(TextView textView, Integer points) {
        textView.setText(points != null ? points + "%" : "");
    }

    @Override
    public void setData(Object viewData) {
        data = (LineFragmentData) viewData;
    }

    @Override
    public LineFragmentData getData() {
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line, container, false);

        c_lw_text = v.findViewById(R.id.c_lw_text);
        c_rw_text = v.findViewById(R.id.c_rw_text);
        c_ld_text = v.findViewById(R.id.c_ld_text);
        c_rd_text = v.findViewById(R.id.c_rd_text);
        ld_rd_text = v.findViewById(R.id.ld_rd_text);
        ld_lw_text = v.findViewById(R.id.ld_lw_text);
        rd_rw_text = v.findViewById(R.id.rd_rw_text);

        card_lw = v.findViewById(R.id.card_lw);
        card_c = v.findViewById(R.id.card_c);
        card_rw = v.findViewById(R.id.card_rw);
        card_ld = v.findViewById(R.id.card_ld);
        card_rd = v.findViewById(R.id.card_rd);

        update();

        return v;
    }

    private void setCardView(RelativeLayout card, final Position position) {
        final String pos = position.toDatabaseName();

        RelativeLayout pictureContainer = card.findViewById(R.id.pictureContainer);
        ImageView chemistryBorder = card.findViewById(R.id.chemistryBorder);
        ImageView pictureImage = card.findViewById(R.id.pictureImage);
        IconView addIcon = card.findViewById(R.id.addIcon);
        TextView nameText = card.findViewById(R.id.nameText);

        // Default view
        addIcon.setClickable(false);
        addIcon.setFocusable(false);
        addIcon.setVisibility(View.VISIBLE);
        pictureContainer.setVisibility(View.GONE);
        nameText.setText(getString(R.string.select));

        Line line = data.getLine();
        if(line != null) {
            String playerId = line.getPlayerIdMap().get(pos);
            if (playerId != null) {
                addIcon.setVisibility(View.GONE);
                pictureContainer.setVisibility(View.VISIBLE);

                Player player = AppRes.getInstance().getPlayers().get(playerId);

                setChemistryColorBorder(chemistryBorder, player);
                if(player == null) {
                    // Poistettu pelaaja
                    nameText.setText(getString(R.string.removed_player));
                    pictureImage.setImageResource(R.drawable.default_picture);
                } else {
                    nameText.setText(player.getName());
                    if (player.getPicture() != null) {
                        pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
                    } else {
                        pictureImage.setImageResource(R.drawable.default_picture);
                    }
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
                        Line line = data.getLine();
                        if(line == null) {
                            line = new Line();
                            line.setLineNumber(data.getLineNumber());
                        }

                        String playerId = player.getPlayerId();
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
                        Line line = data.getLine();
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

    /**
     * NOTE: Drawable must be set as 'background' in xml to this take effect
     * @param view border ImageView
     */
    private void setChemistryColorBorder(ImageView view, Player player) {
        // TODO tee loppuun
        int color = R.color.color_background; // Default color
        if(player != null) {
            //AnalyzerService.getPlayerChemistries(player, )
            List<Position> positionsToCompare = new ArrayList<>();
            Position position = Position.fromDatabaseName(player.getPosition());
            if(position == Position.LW) {
                positionsToCompare = Arrays.asList(Position.C, Position.LD);
            } else if(position == Position.C) {
                positionsToCompare = Arrays.asList(Position.LW, Position.RW, Position.LD, Position.RD);
            } else if(position == Position.RW) {
                positionsToCompare = Arrays.asList(Position.C, Position.RD);
            } else if(position == Position.LD) {
                positionsToCompare = Arrays.asList(Position.C, Position.LW);
            } else if(position == Position.RD) {
                positionsToCompare = Arrays.asList(Position.C, Position.RW);
            }

            double chemistryCount = 0;
            double compareCount = 0;
            Map<Position, Integer> chemistryPoints = getCompareChemistries(position);
            for(Position comparePos : positionsToCompare) {
                Integer points = chemistryPoints.get(comparePos);
                if(points != null) {
                    Logger.log(position.toDatabaseName() + ": comp: " + comparePos.toDatabaseName() + " " +points);
                    compareCount++;
                    chemistryCount += points;
                }
            }
            int percent = (int)Math.round(chemistryCount / compareCount);

            if(percent > 0 && percent <= 33) {
                color = R.color.color_red_light;
            } else if(percent > 33 && percent <= 66) {
                color = R.color.color_orange_light;
            } else if(percent > 66 && percent <= 100) {
                color = R.color.color_green_light;
            }

            Logger.log(position.toDatabaseName() + ": " + percent + "%");
            // TODO JATKA
           /* ArrayList<Chemistry> chemistries = chemistriesMap.get(player.getPlayerId());
            if(chemistries != null) {
                ArrayList<Chemistry> filtered = getFilteredChemistries(positionsToCompare, chemistries);
                for(Chemistry chemistry : filtered) {
                    chemistryCount += chemistry.getChemistryPoints();
                }

                int percent = (int)Math.round(chemistryCount / filtered.size());

                if(percent > 0 && percent <= 33) {
                    color = R.color.color_red_light;
                } else if(percent > 33 && percent <= 66) {
                    color = R.color.color_orange_light;
                } else if(percent > 66 && percent <= 100) {
                    color = R.color.color_green_light;
                }
            }*/
        }

        Drawable background = view.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(ContextCompat.getColor(AppRes.getContext(), color));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(ContextCompat.getColor(AppRes.getContext(), color));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(ContextCompat.getColor(AppRes.getContext(), color));
        }
    }

    /**
     * @param position
     * @return chemistry points indexed by compared positions
     */
    private Map<Position, Integer> getCompareChemistries(Position position) {
        Map<Position, Integer> chemistryPoints = new HashMap<>();
        ArrayList<Chemistry> chemistries = chemistriesMap.get(position);
        if(chemistries != null) {
            for(Chemistry chemistry : chemistries) {
                Position comparePosition = Position.fromDatabaseName(chemistry.getComparePosition());
                chemistryPoints.put(comparePosition, chemistry.getChemistryPoints());
            }
        }

        return chemistryPoints;
    }

    private ArrayList<Chemistry> getFilteredChemistries(List<Position> positions, ArrayList<Chemistry> chemistries) {
        ArrayList<Chemistry> filtered = new ArrayList<>();
        for(Chemistry chemistry : chemistries) {
            Position position = Position.fromDatabaseName(chemistry.getComparePosition());
            if(positions.contains(position)) {
                filtered.add(chemistry);
            }
        }
        return filtered;
    }

}
