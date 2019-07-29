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
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.resources.TeamsSeasonsResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
    Spinner periodSpinner;
    Button saveButton;

    final ArrayList<Integer> durations = new ArrayList<>(Arrays.asList(20, 15, 10, 5));
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
        periodSpinner = v.findViewById(R.id.periodSpinner);
        saveButton = v.findViewById(R.id.saveButton);

        ArrayList<String> durationTitles = new ArrayList<>();
        for(Integer duration : durations) {
            durationTitles.add(duration + "min");
        }
        Helper.setSpinnerAdapter(periodSpinner, durationTitles);

        Calendar calendar = GregorianCalendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String hintYears = year + "-" + (year + 1);
        seasonInfoText.setText(getString(R.string.team_settings_season_info, hintYears));
        Helper.setEditTextValue(seasonEditText, "");
        Helper.setSpinnerSelection(periodSpinner, 0);

        if(season != null) {
            Helper.setEditTextValue(seasonEditText, season.getName());
            Helper.setSpinnerSelection(periodSpinner, durations.indexOf(season.getPeriodInMinutes()));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSeason();
            }
        });

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
        seasonToSave.setPeriodInMinutes(durations.get(periodSpinner.getSelectedItemPosition()));

        if(season != null) {
            TeamsSeasonsResource.getInstance().editSeason(seasonToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    AppRes.getInstance().setSeason(seasonToSave.getSeasonId(), seasonToSave);
                    mListener.onSeasonSaved(seasonToSave);
                }
            });
        } else {
            TeamsSeasonsResource.getInstance().addSeason(seasonToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                @Override
                public void onAddDataSuccess(String id) {
                    seasonToSave.setSeasonId(id);
                    AppRes.getInstance().setSeason(seasonToSave.getSeasonId(), seasonToSave);
                    mListener.onSeasonSaved(seasonToSave);
                }
            });
        }
    }
}
