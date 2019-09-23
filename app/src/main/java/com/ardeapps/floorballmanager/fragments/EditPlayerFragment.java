package com.ardeapps.floorballmanager.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.SelectPictureDialogFragment;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.PictureResource;
import com.ardeapps.floorballmanager.resources.PlayersResource;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.ardeapps.floorballmanager.utils.Helper.setCheckBoxChecked;
import static com.ardeapps.floorballmanager.utils.Helper.setEditTextValue;
import static com.ardeapps.floorballmanager.utils.Helper.setRadioButtonChecked;
import static com.ardeapps.floorballmanager.utils.Helper.setSpinnerSelection;


public class EditPlayerFragment extends Fragment implements DataView {

    public Listener mListener = null;
    IconView selectPictureIcon;
    Button saveButton;
    ImageView pictureImage;
    EditText nameText;
    EditText numberText;
    RadioButton leftRadioButton;
    RadioButton rightRadioButton;
    Spinner positionSpinner;
    LinearLayout strengthsContainer;
    LinearLayout strengthsContent;
    LinearLayout shootsContent;
    Player player;
    ArrayList<CheckBox> strengthCheckBoxes = new ArrayList<>();
    ArrayList<Player.Position> positionTypes;
    Bitmap selectedPicture;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public Player getData() {
        return player;
    }

    @Override
    public void setData(Object viewData) {
        player = (Player) viewData;
    }

    private void refreshPicture(Bitmap selectedPicture) {
        this.selectedPicture = selectedPicture;
        if (selectedPicture != null) {
            pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(selectedPicture));
        } else {
            pictureImage.setImageResource(R.drawable.default_picture);
        }
        ImageUtil.fadeImageIn(pictureImage);
    }

    private void resetFields() {
        selectedPicture = null;
        pictureImage.setImageResource(R.drawable.default_picture);
        setEditTextValue(nameText, "");
        setEditTextValue(numberText, "");
        setRadioButtonChecked(leftRadioButton, true);
        setSpinnerSelection(positionSpinner, 0);
        for (CheckBox checkBox : strengthCheckBoxes) {
            setCheckBoxChecked(checkBox, false);
        }
    }

    @Override
    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_player, container, false);

        selectPictureIcon = v.findViewById(R.id.selectPictureIcon);
        saveButton = v.findViewById(R.id.saveButton);
        pictureImage = v.findViewById(R.id.pictureImage);
        nameText = v.findViewById(R.id.nameText);
        numberText = v.findViewById(R.id.numberText);
        leftRadioButton = v.findViewById(R.id.leftRadioButton);
        rightRadioButton = v.findViewById(R.id.rightRadioButton);
        positionSpinner = v.findViewById(R.id.positionSpinner);
        strengthsContainer = v.findViewById(R.id.strengthsContainer);
        strengthsContent = v.findViewById(R.id.strengthsContent);
        shootsContent = v.findViewById(R.id.shootsContent);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.ADMIN) {
            strengthsContent.setVisibility(View.VISIBLE);
        } else {
            strengthsContent.setVisibility(View.GONE);
        }

        Map<Player.Position, String> positionMap = new TreeMap<>();
        positionMap.put(Player.Position.LW, getString(R.string.position_lw));
        positionMap.put(Player.Position.C, getString(R.string.position_c));
        positionMap.put(Player.Position.RW, getString(R.string.position_rw));
        positionMap.put(Player.Position.LD, getString(R.string.position_ld));
        positionMap.put(Player.Position.RD, getString(R.string.position_rd));
        positionMap.put(Player.Position.MV, getString(R.string.position_mv));
        ArrayList<String> positionTitles = new ArrayList<>(positionMap.values());
        positionTypes = new ArrayList<>(positionMap.keySet());
        Helper.setSpinnerAdapter(positionSpinner, positionTitles);

        Map<Player.Skill, String> strengthTextsMap = Player.getStrengthTextsMap();
        strengthsContainer.removeAllViewsInLayout();
        strengthCheckBoxes = new ArrayList<>();
        for (Map.Entry<Player.Skill, String> entry : strengthTextsMap.entrySet()) {
            Player.Skill skill = entry.getKey();
            String title = entry.getValue();

            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            AppCompatCheckBox checkBox = new AppCompatCheckBox(AppRes.getActivity());
            checkBox.setText(title);
            checkBox.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light));
            checkBox.setLayoutParams(params);
            checkBox.setTag(skill);

            ColorStateList color = ContextCompat.getColorStateList(AppRes.getContext(), R.color.color_text_light);
            checkBox.setSupportButtonTintList(color);

            strengthCheckBoxes.add(checkBox);
            strengthsContainer.addView(checkBox);
        }

        resetFields();
        if (player != null) {
            // Picture
            if (player.getPicture() != null) {
                selectedPicture = player.getPicture();
                pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(selectedPicture));
                ImageUtil.fadeImageIn(pictureImage);
            }

            // Name
            setEditTextValue(nameText, player.getName());

            // Number
            if (player.getNumber() != null) {
                setEditTextValue(numberText, String.valueOf(player.getNumber()));
            } else {
                setEditTextValue(numberText, "");
            }

            // Position
            Player.Position position = Player.Position.fromDatabaseName(player.getPosition());
            setSpinnerSelection(positionSpinner, positionTypes.indexOf(position));

            if (position != Player.Position.MV) {
                strengthsContent.setVisibility(View.VISIBLE);
                shootsContent.setVisibility(View.VISIBLE);
                // Shoots
                if (Player.Shoots.fromDatabaseName(player.getShoots()) == Player.Shoots.LEFT) {
                    setRadioButtonChecked(leftRadioButton, true);
                } else setRadioButtonChecked(rightRadioButton, true);

                // Strengths
                for (CheckBox checkBox : strengthCheckBoxes) {
                    Player.Skill skill = (Player.Skill) checkBox.getTag();
                    setCheckBoxChecked(checkBox, player.getStrengths() != null && player.getStrengths().contains(skill.toDatabaseName()));
                }
            } else {
                strengthsContent.setVisibility(View.GONE);
                shootsContent.setVisibility(View.GONE);
            }
        }

        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == positionTypes.size() - 1) {
                    strengthsContent.setVisibility(View.GONE);
                    shootsContent.setVisibility(View.GONE);
                } else {
                    strengthsContent.setVisibility(View.VISIBLE);
                    shootsContent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectPictureIcon.setOnClickListener(v12 -> {
            final SelectPictureDialogFragment dialog = new SelectPictureDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Vaihda kuva");
            dialog.setListener(new SelectPictureDialogFragment.SelectPictureDialogCloseListener() {
                @Override
                public void onPictureSelected(Bitmap logo) {
                    refreshPicture(logo);
                    dialog.dismiss();
                }

                @Override
                public void onDefaultSelected() {
                    refreshPicture(null);
                    dialog.dismiss();
                }

                @Override
                public void onCancelClick() {
                    dialog.dismiss();
                }
            });
        });
        saveButton.setOnClickListener(v1 -> {
            String name = nameText.getText().toString();
            String numberString = numberText.getText().toString();

            if (StringUtils.isEmptyString(name)) {
                Logger.toast(getString(R.string.error_empty));
                return;
            }
            Long number = null;
            if (!StringUtils.isEmptyString(numberString)) {
                try {
                    number = Long.parseLong(numberString);
                    if (number < 1 || number > 99) {
                        Logger.toast(getString(R.string.error_invalid_value));
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    Logger.toast(getString(R.string.error_invalid_value));
                    return;
                }
            }

            Helper.hideKeyBoard(nameText);

            int positionSpinnerPosition = positionSpinner.getSelectedItemPosition();
            Player.Position position = positionTypes.get(positionSpinnerPosition);
            String shoots = null;
            if(position != Player.Position.MV) {
                shoots = leftRadioButton.isChecked() ? Player.Shoots.LEFT.toDatabaseName() : Player.Shoots.RIGHT.toDatabaseName();
            }

            // Collect strengths
            ArrayList<String> strengths = null;
            if(position != Player.Position.MV) {
                strengths = new ArrayList<>();
                for (CheckBox checkBox : strengthCheckBoxes) {
                    Player.Skill skill = (Player.Skill) checkBox.getTag();
                    if (checkBox.isChecked()) {
                        strengths.add(skill.toDatabaseName());
                    }
                }
                if (strengths.size() > 3) {
                    Logger.toast(getString(R.string.strengths_too_many_error));
                    return;
                }
            }

            final Player playerToSave = player != null ? player.clone() : new Player();
            playerToSave.setTeamId(AppRes.getInstance().getSelectedTeam().getTeamId());
            playerToSave.setName(name);
            playerToSave.setShoots(shoots);
            if (number != null) {
                playerToSave.setNumber(number);
            }
            playerToSave.setPosition(position.toDatabaseName());
            playerToSave.setStrengths(strengths);

            if (player != null) {
                PlayersResource.getInstance().editPlayer(playerToSave, () -> handlePictureAndSave(playerToSave));
            } else {
                playerToSave.setActive(true);
                PlayersResource.getInstance().addPlayer(playerToSave, id -> {
                    playerToSave.setPlayerId(id);
                    handlePictureAndSave(playerToSave);
                });
            }

        });

        return v;
    }

    private void handlePictureAndSave(final Player playerToSave) {
        if (player == null) {
            // Is picture added or changed?
            if (selectedPicture != null) {
                PictureResource.getInstance().addPicture(playerToSave.getPlayerId(), selectedPicture, () -> {
                    playerToSave.setPictureUploaded(true);
                    PlayersResource.getInstance().editPlayer(playerToSave, () -> {
                        playerToSave.setPicture(selectedPicture);
                        mListener.onPlayerEdited(playerToSave);
                    });
                });
            } else {
                // Picture not changed
                playerToSave.setPictureUploaded(false);
                PlayersResource.getInstance().editPlayer(playerToSave, () -> {
                    playerToSave.setPicture(null);
                    mListener.onPlayerEdited(playerToSave);
                });
            }
        } else {
            // Is picture added or changed?
            if (selectedPicture != null && !selectedPicture.sameAs(player.getPicture())) {
                PictureResource.getInstance().addPicture(playerToSave.getPlayerId(), selectedPicture, () -> {
                    playerToSave.setPictureUploaded(true);
                    PlayersResource.getInstance().editPlayer(playerToSave, () -> {
                        playerToSave.setPicture(selectedPicture);
                        mListener.onPlayerEdited(playerToSave);
                    });
                });
                // Is picture removed?
            } else if (selectedPicture == null && player.getPicture() != null) {
                PictureResource.getInstance().removePicture(playerToSave.getPlayerId(), () -> {
                    playerToSave.setPictureUploaded(false);
                    PlayersResource.getInstance().editPlayer(playerToSave, () -> {
                        playerToSave.setPicture(null);
                        mListener.onPlayerEdited(playerToSave);
                    });
                });
            } else {
                // Picture not changed -> set old image
                playerToSave.setPicture(player.getPicture());
                mListener.onPlayerEdited(playerToSave);
            }
        }
    }

    public interface Listener {
        void onPlayerEdited(Player player);
    }

}
