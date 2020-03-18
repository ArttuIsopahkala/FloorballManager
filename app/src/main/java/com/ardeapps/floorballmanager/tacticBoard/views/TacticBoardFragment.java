package com.ardeapps.floorballmanager.tacticBoard.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.EditTagDialogFragment;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.tacticBoard.media.AnimationRecorder;
import com.ardeapps.floorballmanager.tacticBoard.media.ScreenshotRecorder;
import com.ardeapps.floorballmanager.tacticBoard.objects.ExportField;
import com.ardeapps.floorballmanager.tacticBoard.objects.ExportItem;
import com.ardeapps.floorballmanager.tacticBoard.utils.JsonDatabase;
import com.ardeapps.floorballmanager.tacticBoard.utils.TacticBoardHelper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.ardeapps.floorballmanager.services.FragmentListeners.MY_PERMISSION_ACCESS_RECORD_SCREEN;

public class TacticBoardFragment extends Fragment {

    TacticBoardMenu tacticBoardMenu;
    ImageView fieldImage;
    DrawingBoard drawingBoard;
    TacticBoardAnimation tacticBoardAnimation;

    TacticSettingsDialogFragment.Field selectedField;

    public static final int TAKE_SCREENSHOT = 3;
    public static final int RECORD_VIDEO = 2;
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
        drawingBoard = v.findViewById(R.id.drawingBoard);
        tacticBoardAnimation = v.findViewById(R.id.tacticBoardAnimation);
        tacticBoardMenu = v.findViewById(R.id.tacticBoardMenu);

        /// Set listener
        tacticBoardMenu.setAnimationToolListener(tacticBoardAnimation.getAnimationToolListener());

        // Defaults
        selectedField = TacticSettingsDialogFragment.Field.FULL;
        paintColorProgress = RED;
        int color = TacticBoardHelper.getColorFromProgress(paintColorProgress);
        drawingBoard.setPaintColor(color);

        tacticBoardMenu.setListener(new TacticBoardMenu.MenuListener() {
            @Override
            public void onToolChanged(TacticBoardMenu.Tool tool) {
                switch (tool) {
                    case SAVE:
                        int frameCount = tacticBoardMenu.animationTool.getFrameCount();
                        if (frameCount > 0) {
                            EditTagDialogFragment dialog = new EditTagDialogFragment();
                            dialog.show(getChildFragmentManager(), "Valitse tunniste tallennukselle.");
                            dialog.setListener(tag -> {
                                Logger.toast(tag);
                                saveField(tag);
                            });
                        }
                        break;
                    case SETTINGS:
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
                            int color = TacticBoardHelper.getColorFromProgress(paintColorProgress);
                            drawingBoard.setPaintColor(color);
                        });
                        break;
                    case GALLERY:
                        ArrayList<ExportField> items2 = JsonDatabase.getSavedFields();
                        for(ExportField item : items2) {
                            Logger.log(item.name);
                            Logger.log(item.image);
                        }
                       /* TacticGalleryDialogFragment dialog = new TacticGalleryDialogFragment();
                        dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Galleria");*/
                       break;
                    case USERS:
                        tacticBoardAnimation.setNotAddedPlayersVisible(true);
                        break;
                }
            }

            @Override
            public void onActionButtonClick(TacticBoardMenu.Tool tool) {

            }
        });
        return v;
    }

    /*private void changeMode(boolean animationMode) {
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
    }*/


    public void saveField(String tag) {
        String base64 = ImageUtil.getBitmapAsBase64(ImageUtil.getDrawableAsBitmap(ContextCompat.getDrawable(AppRes.getContext(), R.drawable.default_logo)));
        ExportField exportFile1 = new ExportField();
        exportFile1.name = tag;
        exportFile1.image = base64;

        JsonDatabase.saveField(exportFile1);

        /*takeScreenShot(new ScreenshotRecorder.OnScreenshotHandlerCallback() {
            @Override
            public void onScreenshotFinished(Bitmap screenshot) {
                String base64 = ImageUtil.getBitmapAsBase64(screenshot);
                ExportField exportFile = new ExportField();
                exportFile.name = tag;
                exportFile.image = base64;

                JsonDatabase.saveField(exportFile);

                ArrayList<ExportField> items = JsonDatabase.getSavedFields();
                for(ExportField item : items) {
                    Logger.log(item.name);
                    Logger.log(item.image);
                }

                // TODO save screenshot to memory?
                *//*if(StorageHelper.isExternalStorageWritable()) {
                    recordingsPath = StorageHelper.getExternalDirectory();
                } else {
                    recordingsPath = StorageHelper.getInternalDirectory();
                }
                Logger.log("Saving debug screenshot to " + fileName);
                try (FileOutputStream out = new FileOutputStream(fileName)) {
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*
            }

            @Override
            public void onScreenshotFailed(@Nullable Throwable e) {

            }
        });*/
        ArrayList<ExportItem> exportItems = new ArrayList<>();
       /* for (MovableView item : movableViews.values()) {
            exportItems.add(new ExportItem(item));
        }*/
        Gson gson = new Gson();
        String json = gson.toJson(exportItems);
        Logger.log(json);
        ArrayList<ExportItem> items = gson.fromJson(json, new TypeToken<List<ExportItem>>(){}.getType());
        for(ExportItem item : items) {
            Logger.log(item.id);
            Logger.log(item.type);
        }
        ExportField exportFile = new ExportField();
        exportFile.name = tag;
        exportFile.items = items;

        /*if(Build.VERSION.SDK_INT >= 21) {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.tactic_board_record_info));
            dialogFragment.show(getChildFragmentManager(), "Tallennetaanko video?");
            dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                @Override
                public void onDialogYesButtonClick() {
                    // TODO kysy luvat, toista animaatio, näytä "muunnetaan.." ja tallenna video
                    convertAnimationToVideo();
                }
            });
        } else {
            // TODO jos vanha versio -> tallenna vain kuvio

        }*/
    }

    private void takeScreenShot(ScreenshotRecorder.OnScreenshotHandlerCallback handler) {
        if(ScreenshotRecorder.getInstance().isPermissionGiven(intent -> startActivityForResult(intent, RECORD_VIDEO))) {
            ScreenshotRecorder.getInstance().takeScreenshot(handler);
        }
    }

    private void convertAnimationToVideo() {
        if(AnimationRecorder.getInstance().isPermissionGiven(intent -> startActivityForResult(intent, RECORD_VIDEO))) {
            //tacticBoardAnimation.showDisableToolsOverlay(false);
            tacticBoardAnimation.setFrame(0);
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
