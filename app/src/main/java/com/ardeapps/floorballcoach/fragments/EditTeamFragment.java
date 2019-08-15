package com.ardeapps.floorballcoach.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.dialogFragments.SelectPictureDialogFragment;
import com.ardeapps.floorballcoach.objects.Team;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.resources.LogoResource;
import com.ardeapps.floorballcoach.resources.TeamsResource;
import com.ardeapps.floorballcoach.resources.UserConnectionsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FirebaseStorageService;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.DataView;
import com.ardeapps.floorballcoach.views.IconView;

import static com.ardeapps.floorballcoach.utils.Helper.setEditTextValue;


public class EditTeamFragment extends Fragment implements DataView {

    IconView selectLogoIcon;
    Button saveButton;
    ImageView logoImage;
    EditText nameText;

    Team team;
    private Bitmap selectedLogo;
    public Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public void setData(Object viewData) {
        team = (Team) viewData;
    }

    @Override
    public Team getData() {
        return team;
    }

    public interface Listener {
        void onTeamEdited(Team team);
    }

    public void refreshLogo(Bitmap selectedLogo) {
        this.selectedLogo = selectedLogo;
        if(selectedLogo != null) {
            logoImage.setImageDrawable(ImageUtil.getRoundedDrawable(selectedLogo));
        } else {
            logoImage.setImageResource(R.drawable.default_logo);
        }
        ImageUtil.fadeImageIn(logoImage);
    }

    private void resetFields() {
        selectedLogo = null;
        logoImage.setImageResource(R.drawable.default_logo);
        setEditTextValue(nameText, "");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_team, container, false);

        selectLogoIcon = v.findViewById(R.id.selectLogoIcon);
        saveButton = v.findViewById(R.id.saveButton);
        logoImage = v.findViewById(R.id.logoImage);
        nameText = v.findViewById(R.id.nameText);

        resetFields();
        if(team != null) {
            // Picture
            if (team.getLogo() != null) {
                selectedLogo = team.getLogo();
                logoImage.setImageBitmap(ImageUtil.getSquarePicture(selectedLogo));
                ImageUtil.fadeImageIn(logoImage);
            }

            // Name
            setEditTextValue(nameText, team.getName());
        }

        selectLogoIcon.setOnClickListener(v12 -> {
            final SelectPictureDialogFragment dialog = new SelectPictureDialogFragment();
            dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Vaihda logo");
            dialog.setListener(new SelectPictureDialogFragment.SelectPictureDialogCloseListener() {
                @Override
                public void onPictureSelected(Bitmap logo) {
                    refreshLogo(logo);
                    dialog.dismiss();
                }

                @Override
                public void onDefaultSelected() {
                    refreshLogo(null);
                    dialog.dismiss();
                }

                @Override
                public void onCancelClick() {
                    dialog.dismiss();
                }
            });
        });
        saveButton.setOnClickListener(v1 -> {
            final String name = nameText.getText().toString();

            if(StringUtils.isEmptyString(name)) {
                Logger.toast(getString(R.string.error_empty));
                return;
            }

            // Edit or create
            final Team teamToSave;
            if(team != null) {
                teamToSave = team.clone();
                teamToSave.setName(name);
                TeamsResource.getInstance().editTeam(teamToSave, () -> {
                    AppRes.getInstance().setSelectedTeam(teamToSave);
                    saveTeamToUser(teamToSave);
                });
            } else {
                teamToSave = new Team();
                teamToSave.setName(name);
                TeamsResource.getInstance().addTeam(teamToSave, id -> {
                    teamToSave.setTeamId(id);
                    AppRes.getInstance().setSelectedTeam(teamToSave);

                    addUserConnection(teamToSave);
                });
            }
        });

        return v;
    }

    private void addUserConnection(final Team teamToSave) {
        final UserConnection userConnection = new UserConnection();
        User user = AppRes.getInstance().getUser();
        userConnection.setEmail(user.getEmail());
        userConnection.setUserId(user.getUserId());
        userConnection.setRole(UserConnection.Role.ADMIN.toDatabaseName());
        userConnection.setStatus(UserConnection.Status.CONNECTED.toDatabaseName());
        UserConnectionsResource.getInstance().addUserConnection(userConnection, id -> {
            userConnection.setUserConnectionId(id);
            AppRes.getInstance().setUserConnection(userConnection.getUserConnectionId(), userConnection);
            saveTeamToUser(teamToSave);
        });
    }

    private void saveTeamToUser(final Team teamToSave) {
        // Set user to team author
        User user = AppRes.getInstance().getUser();
        if(user.getTeamIds().contains(teamToSave.getTeamId())) {
            handleLogoAndSave(teamToSave);
        } else {
            user.getTeamIds().add(teamToSave.getTeamId());
            UsersResource.getInstance().editUser(user, () -> handleLogoAndSave(teamToSave));
        }
    }

    private void handleLogoAndSave(final Team teamToSave) {
        if(team == null) {
            // Is logo added or changed?
            if(selectedLogo != null) {
                LogoResource.getInstance().addLogo(teamToSave.getTeamId(), selectedLogo, () -> {
                    teamToSave.setLogoUploaded(true);
                    TeamsResource.getInstance().editTeam(teamToSave, () -> {
                        teamToSave.setLogo(selectedLogo);
                        mListener.onTeamEdited(teamToSave);
                    });
                });
            } else {
                // Logo not changed
                teamToSave.setLogoUploaded(false);
                TeamsResource.getInstance().editTeam(teamToSave, () -> {
                    teamToSave.setLogo(null);
                    mListener.onTeamEdited(teamToSave);
                });
            }
        } else {
            // Is logo added or changed?
            if(selectedLogo != null && !selectedLogo.sameAs(team.getLogo())) {
                LogoResource.getInstance().addLogo(teamToSave.getTeamId(), selectedLogo, () -> {
                    teamToSave.setLogoUploaded(true);
                    TeamsResource.getInstance().editTeam(teamToSave, () -> {
                        teamToSave.setLogo(selectedLogo);
                        mListener.onTeamEdited(teamToSave);
                    });
                });
                // Is logo removed?
            } else if(selectedLogo == null && team.getLogo() != null) {
                LogoResource.getInstance().removeLogo(teamToSave.getTeamId(), () -> {
                    teamToSave.setLogoUploaded(false);
                    TeamsResource.getInstance().editTeam(teamToSave, () -> {
                        teamToSave.setLogo(null);
                        mListener.onTeamEdited(teamToSave);
                    });
                });

            } else {
                // Logo not changed -> set old image
                teamToSave.setLogo(team.getLogo());
                mListener.onTeamEdited(teamToSave);
            }
        }
    }

}
