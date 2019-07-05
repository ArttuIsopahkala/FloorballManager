package com.ardeapps.floorballcoach.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.SelectPictureDialogFragment;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.resources.PictureResource;
import com.ardeapps.floorballcoach.resources.PlayersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FirebaseStorageService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static com.ardeapps.floorballcoach.utils.Helper.setEditTextValue;
import static com.ardeapps.floorballcoach.utils.Helper.setRadioButtonChecked;
import static com.ardeapps.floorballcoach.utils.Helper.setSpinnerSelection;


public class EditPlayerFragment extends Fragment implements DataView {

    IconView selectPictureIcon;
    IconView typeInfoIcon;
    Button saveButton;
    ImageView pictureImage;
    EditText nameText;
    EditText numberText;
    RadioButton leftRadioButton;
    RadioButton rightRadioButton;
    Spinner positionSpinner;
    Spinner typeSpinner;

    ArrayAdapter<String> positionSpinnerAdapter;
    ArrayAdapter<String> typeSpinnerAdapter;
    public Listener mListener = null;
    Player player;
    Map<Player.Type, String> typeMap;
    ArrayList<Player.Type> types;
    Map<Player.Position, String> positionMap;
    ArrayList<Player.Position> positionTypes;
    Bitmap selectedPicture;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void setData(Object viewData) {
        player = (Player) viewData;
    }

    @Override
    public Object getData() {
        return player;
    }

    public interface Listener {
        void onPlayerEdited(Player player);
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
        setSpinnerSelection(typeSpinner, 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        positionMap = new TreeMap<>();
        positionMap.put(Player.Position.LW, getString(R.string.position_lw));
        positionMap.put(Player.Position.C, getString(R.string.position_c));
        positionMap.put(Player.Position.RW, getString(R.string.position_rw));
        positionMap.put(Player.Position.LD, getString(R.string.position_ld));
        positionMap.put(Player.Position.RD, getString(R.string.position_rd));
        ArrayList<String> positions = new ArrayList<>(positionMap.values());
        positionTypes = new ArrayList<>(positionMap.keySet());

        positionSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, positions);
        positionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinnerAdapter.notifyDataSetChanged();

        typeMap = new TreeMap<>();
        typeMap.put(Player.Type.GRINDER_FORWARD, getString(R.string.grinder_forward));
        typeMap.put(Player.Type.PLAY_MAKER_FORWARD, getString(R.string.play_maker_forward));
        typeMap.put(Player.Type.POWER_FORWARD, getString(R.string.power_forward));
        typeMap.put(Player.Type.SNIPER_FORWARD, getString(R.string.sniper_forward));
        typeMap.put(Player.Type.TWO_WAY_FORWARD, getString(R.string.two_way_forward));
        typeMap.put(Player.Type.DEFENSIVE_DEFENDER, getString(R.string.defensive_defender));
        typeMap.put(Player.Type.POWER_DEFENDER, getString(R.string.power_defender));
        typeMap.put(Player.Type.OFFENSIVE_DEFENDER, getString(R.string.offensive_defender));
        typeMap.put(Player.Type.TWO_WAY_DEFENDER, getString(R.string.two_way_defender));
        ArrayList<String> typeTexts = new ArrayList<>();
        typeTexts.add(getString(R.string.type_not_set));
        typeTexts.addAll(typeMap.values());
        types = new ArrayList<>(typeMap.keySet());

        typeSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, typeTexts);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_player, container, false);

        selectPictureIcon = v.findViewById(R.id.selectPictureIcon);
        typeInfoIcon = v.findViewById(R.id.typeInfoIcon);
        saveButton = v.findViewById(R.id.saveButton);
        pictureImage = v.findViewById(R.id.pictureImage);
        nameText = v.findViewById(R.id.nameText);
        numberText = v.findViewById(R.id.numberText);
        leftRadioButton = v.findViewById(R.id.leftRadioButton);
        rightRadioButton = v.findViewById(R.id.rightRadioButton);
        positionSpinner = v.findViewById(R.id.positionSpinner);
        typeSpinner = v.findViewById(R.id.typeSpinner);

        positionSpinner.setAdapter(positionSpinnerAdapter);
        typeSpinner.setAdapter(typeSpinnerAdapter);

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
            if(player.getNumber() != null) {
                setEditTextValue(numberText, String.valueOf(player.getNumber()));
            } else {
                setEditTextValue(numberText, "");
            }
            // Shoots
            if (Player.Shoots.fromDatabaseName(player.getShoots()) == Player.Shoots.LEFT) {
                setRadioButtonChecked(leftRadioButton, true);
            } else setRadioButtonChecked(rightRadioButton, true);

            // Position
            Player.Position position = Player.Position.fromDatabaseName(player.getPosition());
            setSpinnerSelection(positionSpinner, positionTypes.indexOf(position));

            // Type
            if(player.getType() != null) {
                Player.Type type = Player.Type.fromDatabaseName(player.getType());
                setSpinnerSelection(typeSpinner, types.indexOf(type) + 1); // +1 because first is empty
            }
        }

        typeInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToTypesInfoFragment();
            }
        });
        selectPictureIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SelectPictureDialogFragment dialog = new SelectPictureDialogFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "Vaihda kuva");
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
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                int positionSpinnerPosition = positionSpinner.getSelectedItemPosition();
                String position = positionTypes.get(positionSpinnerPosition).toDatabaseName();
                int typeSpinnerPosition = typeSpinner.getSelectedItemPosition();
                String type = null;
                if(typeSpinnerPosition > 0) {
                    type = types.get(typeSpinnerPosition - 1).toDatabaseName(); // -1 because first is empty
                }
                String shoots = leftRadioButton.isChecked() ? Player.Shoots.LEFT.toDatabaseName() : Player.Shoots.RIGHT.toDatabaseName();

                final Player playerToSave = player != null ? player.clone() : new Player();
                playerToSave.setTeamId(AppRes.getInstance().getSelectedTeam().getTeamId());
                playerToSave.setName(name);
                playerToSave.setShoots(shoots);
                if(number != null) {
                    playerToSave.setNumber(number);
                }
                playerToSave.setPosition(position);
                playerToSave.setType(type);

                if (player != null) {
                    PlayersResource.getInstance().editPlayer(playerToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                        @Override
                        public void onEditDataSuccess() {
                            handlePictureAndSave(playerToSave);
                        }
                    });
                } else {
                    PlayersResource.getInstance().addPlayer(playerToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                        @Override
                        public void onAddDataSuccess(String id) {
                            playerToSave.setPlayerId(id);
                            handlePictureAndSave(playerToSave);
                        }
                    });
                }

            }
        });

        return v;
    }

    private void handlePictureAndSave(final Player playerToSave) {
        if(player == null) {
            // Is picture added or changed?
            if(selectedPicture != null) {
                PictureResource.getInstance().addPicture(playerToSave.getPlayerId(), selectedPicture, new FirebaseStorageService.AddBitmapListener() {
                    @Override
                    public void onAddBitmapSuccess() {
                        playerToSave.setPictureUploaded(true);
                        PlayersResource.getInstance().editPlayer(playerToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                playerToSave.setPicture(selectedPicture);
                                mListener.onPlayerEdited(playerToSave);
                            }
                        });
                    }
                });
            } else {
                // Picture not changed
                playerToSave.setPictureUploaded(false);
                PlayersResource.getInstance().editPlayer(playerToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                    @Override
                    public void onEditDataSuccess() {
                        playerToSave.setPicture(null);
                        mListener.onPlayerEdited(playerToSave);
                    }
                });
            }
        } else {
            // Is picture added or changed?
            if(selectedPicture != null && !selectedPicture.sameAs(player.getPicture())) {
                PictureResource.getInstance().addPicture(playerToSave.getPlayerId(), selectedPicture, new FirebaseStorageService.AddBitmapListener() {
                    @Override
                    public void onAddBitmapSuccess() {
                        playerToSave.setPictureUploaded(true);
                        PlayersResource.getInstance().editPlayer(playerToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                playerToSave.setPicture(selectedPicture);
                                mListener.onPlayerEdited(playerToSave);
                            }
                        });
                    }
                });
                // Is picture removed?
            } else if(selectedPicture == null && player.getPicture() != null){
                PictureResource.getInstance().removePicture(playerToSave.getPlayerId(), new FirebaseStorageService.DeleteBitmapSuccessListener() {
                    @Override
                    public void onDeleteBitmapSuccess() {
                        playerToSave.setPictureUploaded(false);
                        PlayersResource.getInstance().editPlayer(playerToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                playerToSave.setPicture(null);
                                mListener.onPlayerEdited(playerToSave);
                            }
                        });
                    }
                });
            } else {
                // Picture not changed -> set old image
                playerToSave.setPicture(player.getPicture());
                mListener.onPlayerEdited(playerToSave);
            }
        }
    }

}
