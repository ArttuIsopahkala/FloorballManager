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
import android.widget.EditText;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.resources.SeasonsResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditSeasonDialogFragment extends DialogFragment {

    public interface EditSeasonDialogCloseListener {
        void onSeasonSaved(Season season);
    }

    EditSeasonDialogCloseListener mListener = null;

    public void setListener(EditSeasonDialogCloseListener l) {
        mListener = l;
    }

    EditText seasonEditText;
    TextView seasonInfoText;
    Button saveButton;

    Season season;

    public void setSeason(Season season) {
        this.season = season;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_edit_season, container);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        seasonEditText = v.findViewById(R.id.seasonEditText);
        seasonInfoText = v.findViewById(R.id.seasonInfoText);
        saveButton = v.findViewById(R.id.saveButton);

        Calendar calendar = GregorianCalendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String hintYears = year + "-" + (year + 1);
        seasonInfoText.setText(getString(R.string.team_settings_season_info, hintYears));
        Helper.setEditTextValue(seasonEditText, "");

        if(season != null) {
            Helper.setEditTextValue(seasonEditText, season.getName());
        }

        saveButton.setOnClickListener(v1 -> saveSeason());

        return v;
    }

    private void saveSeason() {
        String seasonName = seasonEditText.getText().toString();

        if(StringUtils.isEmptyString(seasonName)) {
            Logger.toast(R.string.error_empty);
            return;
        }

        for(Season season : AppRes.getInstance().getSeasons().values()) {
            if(season.getName().equals(seasonName)) {
                Logger.toast(R.string.team_settings_exists);
                return;
            }
        }
        Helper.hideKeyBoard(seasonEditText);
        // Close dialog
        dismiss();

        final Season seasonToSave = season != null ? season.clone() : new Season();
        seasonToSave.setName(seasonName);

        if(season != null) {
            SeasonsResource.getInstance().editSeason(seasonToSave, () -> {
                AppRes.getInstance().setSeason(seasonToSave.getSeasonId(), seasonToSave);
                mListener.onSeasonSaved(seasonToSave);
            });
        } else {
            SeasonsResource.getInstance().addSeason(seasonToSave, id -> {
                seasonToSave.setSeasonId(id);
                AppRes.getInstance().setSeason(seasonToSave.getSeasonId(), seasonToSave);
                mListener.onSeasonSaved(seasonToSave);
            });
        }
    }
}
