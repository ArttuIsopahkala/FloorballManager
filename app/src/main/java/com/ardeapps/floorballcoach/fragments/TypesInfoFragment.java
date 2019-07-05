package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.utils.Logger;

public class TypesInfoFragment extends Fragment {

    public class TypesInfoHolder {
        TextView headerText;
        TextView strengthsText;
        TextView weaknessesText;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_types_info, container, false);

        LinearLayout typesContainer = v.findViewById(R.id.typesContainer);

        final TypesInfoHolder holder = new TypesInfoHolder();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        typesContainer.removeAllViews();

        for (Player.Type type : Player.Type.values()) {
            View cv = inf.inflate(R.layout.list_item_type, typesContainer, false);

            holder.headerText = cv.findViewById(R.id.headerText);
            holder.strengthsText = cv.findViewById(R.id.strengthsText);
            holder.weaknessesText = cv.findViewById(R.id.weaknessesText);

            String header = Player.getTypeText(type.toDatabaseName());
            holder.headerText.setText(header);
            Logger.log(header);

            String strengths = "";
            String weaknesses = "";
            if(type == Player.Type.GRINDER_FORWARD) {
                strengths += "- " + getString(R.string.grinder_forward_plus1) + "\n";
                strengths += "- " + getString(R.string.grinder_forward_plus2);
                weaknesses += "- " + getString(R.string.grinder_forward_minus1) + "\n";
                weaknesses += "- " + getString(R.string.grinder_forward_minus2);
            } else if (type == Player.Type.PLAY_MAKER_FORWARD) {
                strengths += "- " + getString(R.string.play_maker_forward_plus1) + "\n";
                strengths += "- " + getString(R.string.play_maker_forward_plus2);
                weaknesses += "- " + getString(R.string.play_maker_forward_minus1) + "\n";
                weaknesses += "- " + getString(R.string.play_maker_forward_minus2) + "\n";
                weaknesses += "- " + getString(R.string.play_maker_forward_minus3);
            } else if (type == Player.Type.POWER_FORWARD) {
                strengths += "- " + getString(R.string.power_forward_plus1) + "\n";
                strengths += "- " + getString(R.string.power_forward_plus2);
                weaknesses += "- " + getString(R.string.power_forward_minus1) + "\n";
                weaknesses += "- " + getString(R.string.power_forward_minus2);
            } else if (type == Player.Type.SNIPER_FORWARD) {
                strengths += "- " + getString(R.string.sniper_forward_plus1);
                weaknesses += "- " + getString(R.string.sniper_forward_minus1) + "\n";
                weaknesses += "- " + getString(R.string.sniper_forward_minus2);
            } else if (type == Player.Type.TWO_WAY_FORWARD) {
                strengths += "- " + getString(R.string.two_way_forward_plus1) + "\n";
                strengths += "- " + getString(R.string.two_way_forward_plus2) + "\n";
                strengths += "- " + getString(R.string.two_way_forward_plus3);
                weaknesses += "- " + getString(R.string.two_way_forward_minus1) + "\n";
                weaknesses += "- " + getString(R.string.two_way_forward_minus2) + "\n";
                weaknesses += "- " + getString(R.string.two_way_forward_minus3);
            } else if (type == Player.Type.DEFENSIVE_DEFENDER) {
                strengths += "- " + getString(R.string.defensive_defender_plus1) + "\n";
                strengths += "- " + getString(R.string.defensive_defender_plus2);
                weaknesses += "- " + getString(R.string.defensive_defender_minus1) + "\n";
                weaknesses += "- " + getString(R.string.defensive_defender_minus2) + "\n";
                weaknesses += "- " + getString(R.string.defensive_defender_minus3);
            } else if (type == Player.Type.POWER_DEFENDER) {
                strengths += "- " + getString(R.string.power_defender_plus1) + "\n";
                strengths += "- " + getString(R.string.power_defender_plus2);
                weaknesses += "- " + getString(R.string.power_defender_minus1) + "\n";
                weaknesses += "- " + getString(R.string.power_defender_minus2) + "\n";
                weaknesses += "- " + getString(R.string.power_defender_minus3);
            } else if (type == Player.Type.OFFENSIVE_DEFENDER) {
                strengths += "- " + getString(R.string.offensive_defender_plus1) + "\n";
                strengths += "- " + getString(R.string.offensive_defender_plus2);
                weaknesses += "- " + getString(R.string.offensive_defender_minus1) + "\n";
                weaknesses += "- " + getString(R.string.offensive_defender_minus2) + "\n";
                weaknesses += "- " + getString(R.string.offensive_defender_minus3);
            } else if (type == Player.Type.TWO_WAY_DEFENDER) {
                strengths += "- " + getString(R.string.two_way_defender_plus1) + "\n";
                strengths += "- " + getString(R.string.two_way_defender_plus2);
                weaknesses += "- " + getString(R.string.two_way_defender_minus1) + "\n";
                weaknesses += "- " + getString(R.string.two_way_defender_minus2);
            }

            holder.strengthsText.setText(strengths);
            holder.weaknessesText.setText(weaknesses);

            typesContainer.addView(cv);
        }

        return v;
    }

}
