package com.ardeapps.floorballcoach.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.ardeapps.floorballcoach.objects.Chemistry.ChemistryConnection;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.objects.Player.Position;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.viewObjects.LineFragmentData;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LineFragment extends Fragment implements DataView {

    public interface Listener {
        void onLineChanged(Line line, String playerId);
    }

    Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
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
    ImageView chemistryLinesImageView;

    private LineFragmentData data;
    Map<Position, Integer> closestChemistries = new HashMap<>();
    Map<ChemistryConnection, Integer> chemistryConnections = new HashMap<>();
    int canvasTop = 0;

    @Override
    public void setData(Object viewData) {
        data = (LineFragmentData) viewData;
    }

    @Override
    public LineFragmentData getData() {
        return data;
    }

    public void update() {
        if(data.isShowChemistry()) {
            // Get data
            Line line = data.getLine();
            closestChemistries = AnalyzerService.getInstance().getClosestChemistryPercentsForPosition(line);
            chemistryConnections = AnalyzerService.getInstance().getChemistryConnections(line);

            // Set chemistry texts
            setChemistryText(c_lw_text, chemistryConnections.get(ChemistryConnection.C_LW));
            setChemistryText(c_rw_text, chemistryConnections.get(ChemistryConnection.C_RW));
            setChemistryText(c_ld_text, chemistryConnections.get(ChemistryConnection.C_LD));
            setChemistryText(c_rd_text, chemistryConnections.get(ChemistryConnection.C_RD));
            setChemistryText(ld_rd_text, chemistryConnections.get(ChemistryConnection.LD_RD));
            setChemistryText(ld_lw_text, chemistryConnections.get(ChemistryConnection.LD_LW));
            setChemistryText(rd_rw_text, chemistryConnections.get(ChemistryConnection.RD_RW));

            // Set chemistry lines
            chemistryLinesImageView.post(new Runnable() {
                @Override
                public void run() {
                    int[] location = new int[2];
                    chemistryLinesImageView.getLocationOnScreen(location);
                    canvasTop = location[1] / 2 + 20;

                    int width = chemistryLinesImageView.getWidth();
                    int height = chemistryLinesImageView.getHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);

                    drawChemistryLine(canvas, card_c, card_lw, ChemistryConnection.C_LW);
                    drawChemistryLine(canvas, card_c, card_rw, ChemistryConnection.C_RW);
                    drawChemistryLine(canvas, card_c, card_ld, ChemistryConnection.C_LD);
                    drawChemistryLine(canvas, card_c, card_rd, ChemistryConnection.C_RD);
                    drawChemistryLine(canvas, card_ld, card_rd, ChemistryConnection.LD_RD);
                    drawChemistryLine(canvas, card_ld, card_lw, ChemistryConnection.LD_LW);
                    drawChemistryLine(canvas, card_rd, card_rw, ChemistryConnection.RD_RW);

                    chemistryLinesImageView.setImageBitmap(bitmap);
                }
            });
        }

        // Set cards
        setCardView(card_lw, Position.LW);
        setCardView(card_c, Position.C);
        setCardView(card_rw, Position.RW);
        setCardView(card_ld, Position.LD);
        setCardView(card_rd, Position.RD);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line, container, false);

        chemistryLinesImageView = v.findViewById(R.id.chemistryLinesImageView);
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

        chemistryLinesImageView.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        c_lw_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        c_rw_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        c_ld_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        c_rd_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        ld_rd_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        ld_lw_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);
        rd_rw_text.setVisibility(data.isShowChemistry() ? View.VISIBLE : View.GONE);

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
        TextView avgPercentText = card.findViewById(R.id.avgPercentText);

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

                setChemistryColorBorder(avgPercentText, chemistryBorder, position);
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
                if(AppRes.getInstance().getPlayers().isEmpty()) {
                    Logger.toast(R.string.lineup_no_players);
                    return;
                }

                final SelectPlayerDialogFragment dialog = new SelectPlayerDialogFragment();
                dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Valitse pelaaja");
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
                        data.setLine(line);

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
                        data.setLine(line);

                        mListener.onLineChanged(line, null);
                    }
                });
            }
        });
    }

    private void drawChemistryLine(Canvas canvas, RelativeLayout fromView, RelativeLayout toView, ChemistryConnection connection) {
        Integer percent = chemistryConnections.get(connection);
        if(percent != null) {
            Point from = getPosition(fromView);
            Point to = getPosition(toView);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            int color = getChemistryColor(percent);
            paint.setColor(color);
            paint.setStrokeWidth(10f);
            canvas.drawLine(from.x, from.y, to.x, to.y, paint);
        }
    }

    private void setChemistryText(TextView textView, Integer points) {
        textView.setText(points != null ? points + "%" : "");
    }

    /**
     * NOTE: Drawable must be set as 'background' in xml to this take effect
     * @param view border ImageView
     */
    private void setChemistryColorBorder(TextView avgPercentText, ImageView view, Position position) {
        int color = R.color.color_background; // Default color
        String percentText = "";

        if (data.isShowChemistry()) {
            Integer percent = closestChemistries.get(position);
            if (percent != null) {
                percentText = String.valueOf(percent);
                color = getChemistryColor(percent);
            }
        }

        view.setColorFilter(color);
        avgPercentText.setText(percentText);
    }

    private Point getPosition(final RelativeLayout card) {
        int[] location = new int[2];
        card.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        double positionX = x + card.getWidth()  / 2.0;
        double positionY = y - canvasTop - card.getHeight() / 2.0;
        return new Point((int)positionX, (int)positionY);
    }

    private int getChemistryColor(int percent) {
        int color = R.color.color_background_third; // Default color
        if (percent > 0 && percent <= 33) {
            color = R.color.color_red_light;
        } else if (percent > 33 && percent <= 66) {
            color = R.color.color_orange_light;
        } else if (percent > 66 && percent <= 100) {
            color = R.color.color_green_light;
        }
        return ContextCompat.getColor(AppRes.getContext(), color);
    }
}
