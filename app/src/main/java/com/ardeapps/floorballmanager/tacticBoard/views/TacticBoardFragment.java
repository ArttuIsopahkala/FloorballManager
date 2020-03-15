package com.ardeapps.floorballmanager.tacticBoard.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.EditTagDialogFragment;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.tacticBoard.media.AnimationRecorder;
import com.ardeapps.floorballmanager.tacticBoard.media.ScreenshotRecorder;
import com.ardeapps.floorballmanager.tacticBoard.objects.ExportField;
import com.ardeapps.floorballmanager.tacticBoard.utils.JsonDatabase;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.IconView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_RECORD_SCREEN;

public class TacticBoardFragment extends Fragment {

    TacticBoardDraw tacticBoardDraw;
    TacticBoardAnimation tacticBoardAnimation;
    IconView settingsIcon;
    IconView saveIcon;
    IconView galleryIcon;
    Spinner modeSpinner;
    TextView fieldNameText;
    ImageView fieldImage;

    TacticSettingsDialogFragment.Field selectedField;

    public static final int TAKE_SCREENSHOT = 3;
    public static final int RECORD_VIDEO = 2;
    private boolean isAnimationMode;
    private int fieldHeight = 0;
    private int fieldWidth = 0;
    private final static int YELLOW = 1650;
    private final static int RED = 1070;
    private final static int BLUE = 290;
    private int paintColorProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentListeners.getInstance().setPermissionHandledListener(new FragmentListeners.PermissionHandledListener() {
            @Override
            public void onPermissionGranted(int MY_PERMISSION) {
                if(MY_PERMISSION == MY_PERMISSION_ACCESS_RECORD_SCREEN) {
                    Logger.log("RECORD_SCREEN_ACCESS GIVEN");
                    convertAnimationToVideo();
                }
            }

            @Override
            public void onPermissionDenied(int MY_PERMISSION) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RECORD_VIDEO) {
            Logger.log("Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Logger.log("Screen Cast Permission Denied");
            return;
        }
        Logger.log("ACTIVIRY RESULT GIVEN: " + resultCode + " data: " + data);
        AnimationRecorder.getInstance().setPermissionGranted(resultCode, data);
        convertAnimationToVideo();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tactic_board, container, false);

        fieldImage = v.findViewById(R.id.fieldImage);
        fieldNameText = v.findViewById(R.id.fieldNameText);
        modeSpinner = v.findViewById(R.id.modeSpinner);
        settingsIcon = v.findViewById(R.id.settingsIcon);
        saveIcon = v.findViewById(R.id.saveIcon);
        galleryIcon = v.findViewById(R.id.galleryIcon);
        tacticBoardDraw = v.findViewById(R.id.tacticBoardTools);
        tacticBoardAnimation = v.findViewById(R.id.tacticBoardAnimation);

        // Defaults
        fieldNameText.setVisibility(View.GONE);
        isAnimationMode = true;
        ArrayList<String> modeTitles = new ArrayList<>();
        modeTitles.add(getString(R.string.tactic_board_draw_mode));
        modeTitles.add(getString(R.string.tactic_board_animation_mode));
        Helper.setSpinnerAdapter(modeSpinner, modeTitles);
        Helper.setSpinnerSelection(modeSpinner, 1);
        selectedField = TacticSettingsDialogFragment.Field.FULL;
        paintColorProgress = RED;
        tacticBoardDraw.setPaintColorProgress(paintColorProgress);

        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isAnimationMode = position == 1;
                changeMode(isAnimationMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        saveIcon.setOnClickListener(v12 -> {
            if(isAnimationMode) {
                int frameCount = tacticBoardAnimation.getFrameCount();
                if (frameCount > 0 && !tacticBoardAnimation.isAnimationRunning()) {
                    EditTagDialogFragment dialog = new EditTagDialogFragment();
                    dialog.show(getChildFragmentManager(), "Valitse tunniste tallennukselle.");
                    dialog.setListener(tag -> {
                        Logger.toast(tag);
                        tacticBoardAnimation.saveField(tag);
                    });
                }
            } else {

            }
        });

        settingsIcon.setOnClickListener(v1 -> {
            TacticSettingsDialogFragment dialog = new TacticSettingsDialogFragment();
            dialog.setSelectedField(selectedField);
            dialog.setPaintColorProgress(paintColorProgress);
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Piirtoalustan asetukset");
            dialog.setListener((field, colorProgress) -> {
                if (selectedField != field) {
                    if (field == TacticSettingsDialogFragment.Field.FULL) {
                        fieldImage.setImageResource(R.drawable.floorball_field);
                        fieldImage.setRotation(0f);
                    } else if (field == TacticSettingsDialogFragment.Field.HALF_LEFT){
                        fieldImage.setImageResource(R.drawable.floorball_field_rotated);
                        fieldImage.setRotation(0f);
                    } else if (field == TacticSettingsDialogFragment.Field.HALF_RIGHT) {
                        fieldImage.setImageResource(R.drawable.floorball_field_rotated);
                        fieldImage.setRotation(180f);
                    }
                }
                selectedField = field;
                paintColorProgress = colorProgress;
                tacticBoardDraw.setPaintColorProgress(colorProgress);
            });
        });

        galleryIcon.setOnClickListener(v1 -> {
            ArrayList<ExportField> items2 = JsonDatabase.getSavedFields();
            for(ExportField item : items2) {
                Logger.log(item.name);
                Logger.log(item.image);
            }
           /* TacticGalleryDialogFragment dialog = new TacticGalleryDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Galleria");*/
        });

        fieldImage.post(() -> {
            fieldHeight = fieldImage.getHeight();
            fieldWidth  = fieldImage.getWidth();
        });
        return v;
    }

    private void changeMode(boolean animationMode) {
        // Changes view and height of field
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fieldImage.getLayoutParams();
        if(animationMode) {
            int newFieldHeight = fieldHeight - tacticBoardAnimation.getToolBarHeight();
            params.height = newFieldHeight;
            tacticBoardAnimation.setFieldHeight(newFieldHeight, fieldWidth);
            tacticBoardDraw.setVisibility(View.GONE);
            tacticBoardAnimation.setVisibility(View.VISIBLE);
        } else {
            int newFieldHeight = fieldHeight - tacticBoardDraw.getToolBarHeight();
            params.height = newFieldHeight;
            tacticBoardDraw.setFieldHeight(newFieldHeight, fieldWidth);
            tacticBoardDraw.setVisibility(View.VISIBLE);
            tacticBoardAnimation.setVisibility(View.GONE);
        }
        fieldImage.setLayoutParams(params);
    }

    private void takeScreenShot(ScreenshotRecorder.OnScreenshotHandlerCallback handler) {
        if(ScreenshotRecorder.getInstance().isPermissionGiven(intent -> startActivityForResult(intent, RECORD_VIDEO))) {
            ScreenshotRecorder.getInstance().takeScreenshot(handler);
        }
    }

    private void convertAnimationToVideo() {
        if(AnimationRecorder.getInstance().isPermissionGiven(intent -> startActivityForResult(intent, RECORD_VIDEO))) {
            tacticBoardAnimation.showDisableToolsOverlay(false);
            tacticBoardAnimation.setSelectedFrame(0);
            tacticBoardAnimation.runAnimation();
            // TODO tallenna oikea tiedostonimi
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            AnimationRecorder.getInstance().prepareRecorder("MOI_" + timeStamp);
            AnimationRecorder.getInstance().startRecording();
        }
    }

    @TargetApi(21)
    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimationRecorder.getInstance().stopRecording();
    }
}
