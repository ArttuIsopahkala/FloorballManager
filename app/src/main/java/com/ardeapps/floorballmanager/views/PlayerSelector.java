package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arttu on 23.2.2018.
 */

public class PlayerSelector extends LinearLayout {

    final Map<String, PlayerHolder> holders = new HashMap<>();
    final Map<Integer, RadioButton> selectAllRadioButtons = new HashMap<>();
    public Listener mListener = null;
    RelativeLayout lineContainer1;
    RelativeLayout lineContainer2;
    RelativeLayout lineContainer3;
    RelativeLayout lineContainer4;
    boolean multiSelect;
    List<String> selectedPlayerIds = new ArrayList<>();
    List<String> disabledPlayerIds = new ArrayList<>();
    Map<Integer, Line> lines = new HashMap<>();

    public PlayerSelector(Context context) {
        super(context);
    }

    public PlayerSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void createMultiSelectView(Map<Integer, Line> lines) {
        createView(lines, true, null);
    }

    public void createSingleSelectView(Map<Integer, Line> lines, Listener listener) {
        createView(lines, false, listener);
    }

    private void createView(Map<Integer, Line> lines, boolean multiSelect, Listener listener) {
        this.lines = lines;
        this.multiSelect = multiSelect;
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(AppRes.getContext());
        inflater.inflate(R.layout.player_selector, this);
        lineContainer1 = findViewById(R.id.lineContainer1);
        lineContainer2 = findViewById(R.id.lineContainer2);
        lineContainer3 = findViewById(R.id.lineContainer3);
        lineContainer4 = findViewById(R.id.lineContainer4);

        createLineView(lineContainer1, lines.get(1));
        createLineView(lineContainer2, lines.get(2));
        createLineView(lineContainer3, lines.get(3));
        createLineView(lineContainer4, lines.get(4));
    }

    private void createLineView(RelativeLayout layout, final Line line) {
        if (line != null && !line.getPlayerIdMap().isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            RadioButton selectAllRadioButton = layout.findViewById(R.id.selectAllRadioButton);
            TextView lineText = layout.findViewById(R.id.lineText);
            LinearLayout playersList = layout.findViewById(R.id.playersList);

            selectAllRadioButtons.put(line.getLineNumber(), selectAllRadioButton);
            setRadioButtons();

            if (multiSelect) {
                selectAllRadioButton.setVisibility(View.VISIBLE);
                selectAllRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Unselect other radiobuttons
                        for (RadioButton radioButton : selectAllRadioButtons.values()) {
                            if (radioButton != buttonView) radioButton.setChecked(false);
                        }

                        // Add all line and disabled players
                        selectedPlayerIds.clear();
                        selectedPlayerIds.addAll(disabledPlayerIds);
                        for (String playerId : line.getPlayerIdMap().values()) {
                            if (!selectedPlayerIds.contains(playerId)) {
                                selectedPlayerIds.add(playerId);
                            }
                        }

                        setSelections();
                    }
                });
            } else {
                selectAllRadioButton.setVisibility(View.GONE);
            }

            lineText.setVisibility(View.VISIBLE);
            lineText.setText(line.getLineNumber() + ". " + AppRes.getContext().getString(R.string.line));

            playersList.removeAllViewsInLayout();
            LayoutInflater inf = LayoutInflater.from(AppRes.getContext());

            for (Map.Entry<String, String> entry : line.getSortedPlayers().entrySet()) {
                final String playerId = entry.getValue();

                final Player player = AppRes.getInstance().getPlayers().get(playerId);

                View v = inf.inflate(R.layout.list_item_player, playersList, false);
                final PlayerHolder holder = new PlayerHolder(v, false, false);
                holders.put(playerId, holder);

                if (player == null) {
                    // Poistettu pelaaja
                    holder.nameNumberShootsText.setText(AppRes.getContext().getString(R.string.removed_player));
                    holder.nameNumberShootsText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light_secondary));
                    holder.pictureImage.setImageResource(R.drawable.default_picture);
                } else {
                    if (player.getPicture() != null) {
                        holder.pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
                    }
                    holder.nameNumberShootsText.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light));
                    holder.nameNumberShootsText.setText(player.getName());
                }

                holder.playerContainer.setOnClickListener(v1 -> {
                    if (!holder.isDisabled()) {
                        if (multiSelect) {
                            if (holder.isSelected()) {
                                selectedPlayerIds.remove(playerId);
                            } else {
                                selectedPlayerIds.add(playerId);
                            }
                        } else {
                            if (holder.isSelected()) {
                                selectedPlayerIds.remove(playerId);
                                if(mListener != null) {
                                    mListener.onPlayerUnSelected(line.getLineNumber(), playerId);
                                }
                            } else {
                                // Clear other and add new selection
                                selectedPlayerIds.clear();
                                selectedPlayerIds.add(playerId);
                                if(mListener != null) {
                                    mListener.onPlayerSelected(line.getLineNumber(), playerId);
                                }
                            }
                        }
                        setSelections();
                    }
                });

                playersList.addView(v);
            }
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    private void setRadioButtons() {
        for (Map.Entry<Integer, RadioButton> entry : selectAllRadioButtons.entrySet()) {
            Integer lineNumber = entry.getKey();
            RadioButton radioButton = entry.getValue();

            if (lineNumber == null) {
                Helper.setRadioButtonChecked(radioButton, false);
                continue;
            }

            Line line = lines.get(lineNumber);
            if (line == null || line.getPlayerIdMap() == null) {
                Helper.setRadioButtonChecked(radioButton, false);
                continue;
            }

            boolean allSelectedFromLine = selectedPlayerIds.containsAll(line.getPlayerIdMap().values());
            boolean selectedSizeEqualsLineSize = selectedPlayerIds.size() == line.getPlayerIdMap().size();
            if (!allSelectedFromLine || !selectedSizeEqualsLineSize) {
                Helper.setRadioButtonChecked(radioButton, false);
                continue;
            }

            // All fine, select radio button
            Helper.setRadioButtonChecked(radioButton, true);
        }
    }

    public void setSelections() {
        setRadioButtons();
        for (Map.Entry<String, PlayerHolder> entry : holders.entrySet()) {
            String playerId = entry.getKey();
            PlayerHolder holder = entry.getValue();

            if (selectedPlayerIds.contains(playerId)) {
                holder.setSelected(true);
            } else {
                holder.setSelected(false);
            }

            if (disabledPlayerIds.contains(playerId)) {
                holder.setDisabled(true);
            } else {
                holder.setDisabled(false);
            }
        }
    }

    public void setDisabledPlayerIds(List<String> playerIds) {
        this.disabledPlayerIds = new ArrayList<>(playerIds);
        setSelections();
    }

    public List<String> getSelectedPlayerIds() {
        if (selectedPlayerIds == null) {
            selectedPlayerIds = new ArrayList<>();
        }
        return selectedPlayerIds;
    }

    public void setSelectedPlayerIds(List<String> playerIds) {
        this.selectedPlayerIds = new ArrayList<>(playerIds);
        setSelections();
    }

    public interface Listener {
        void onPlayerSelected(int lineNumber, String playerId);

        void onPlayerUnSelected(int lineNumber, String playerId);
    }
}
