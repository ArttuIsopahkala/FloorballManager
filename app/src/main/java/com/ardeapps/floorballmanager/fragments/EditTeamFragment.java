package com.ardeapps.floorballmanager.fragments;


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

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.SelectPictureDialogFragment;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.LogoResource;
import com.ardeapps.floorballmanager.resources.TeamsResource;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UsersResource;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.views.IconView;

import static com.ardeapps.floorballmanager.utils.Helper.setEditTextValue;


public class EditTeamFragment extends Fragment implements DataView {

    public Listener mListener = null;
    IconView selectLogoIcon;
    Button saveButton;
    ImageView logoImage;
    EditText nameText;
    Team team;
    private Bitmap selectedLogo;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public Team getData() {
        return team;
    }

    @Override
    public void setData(Object viewData) {
        team = (Team) viewData;
    }

    public void refreshLogo(Bitmap selectedLogo) {
        this.selectedLogo = selectedLogo;
        if (selectedLogo != null) {
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
        if (team != null) {
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

            if (StringUtils.isEmptyString(name)) {
                Logger.toast(getString(R.string.error_empty));
                return;
            }

            Helper.hideKeyBoard(nameText);

            TeamsResource.getInstance().getTeamByName(name, teamExists -> {
                if(teamExists != null) {
                    Logger.toast(R.string.add_team_name_exists);
                    return;
                }

                // Edit or create
                final Team teamToSave;
                if (team != null) {
                    teamToSave = team.clone();
                    teamToSave.setName(name);
                    TeamsResource.getInstance().editTeam(teamToSave, () -> {
                        saveTeamToUser(teamToSave);
                    });
                } else {
                    teamToSave = new Team();
                    teamToSave.setName(name);
                    teamToSave.setCreationTime(System.currentTimeMillis());
                    teamToSave.setFounder(AppRes.getInstance().getUser().getUserId());
                    TeamsResource.getInstance().addTeam(teamToSave, id -> {
                        teamToSave.setTeamId(id);
                        addUserConnection(teamToSave);
                    });
                }
            });
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
        UserConnectionsResource.getInstance().addUserConnection(teamToSave.getTeamId(), userConnection, id -> {
            userConnection.setUserConnectionId(id);
            AppRes.getInstance().setUserConnection(userConnection.getUserConnectionId(), userConnection);
            saveTeamToUser(teamToSave);
        });
    }

    private void saveTeamToUser(final Team teamToSave) {
        // Set user to team author
        User user = AppRes.getInstance().getUser();
        if (user.getTeamIds().contains(teamToSave.getTeamId())) {
            handleLogoAndSave(teamToSave);
        } else {
            user.getTeamIds().add(teamToSave.getTeamId());
            UsersResource.getInstance().editUser(user, () -> handleLogoAndSave(teamToSave));
        }
    }

    private void handleLogoAndSave(final Team teamToSave) {
        if (team == null) {
            // Is logo added or changed?
            if (selectedLogo != null) {
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
            if (selectedLogo != null && !selectedLogo.sameAs(team.getLogo())) {
                LogoResource.getInstance().addLogo(teamToSave.getTeamId(), selectedLogo, () -> {
                    teamToSave.setLogoUploaded(true);
                    TeamsResource.getInstance().editTeam(teamToSave, () -> {
                        teamToSave.setLogo(selectedLogo);
                        mListener.onTeamEdited(teamToSave);
                    });
                });
                // Is logo removed?
            } else if (selectedLogo == null && team.getLogo() != null) {
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

    public interface Listener {
        void onTeamEdited(Team team);
    }

}
