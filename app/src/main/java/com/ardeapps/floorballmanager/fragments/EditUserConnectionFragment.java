package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserInvitation;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UserInvitationsResource;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.utils.StringUtils;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.views.PlayerHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ardeapps.floorballmanager.utils.Helper.isValidEmail;
import static com.ardeapps.floorballmanager.utils.Helper.setEditTextValue;
import static com.ardeapps.floorballmanager.utils.Helper.setSpinnerSelection;


public class EditUserConnectionFragment extends Fragment implements DataView {

    public Listener mListener = null;
    EditText emailText;
    Spinner roleSpinner;
    TextView roleInfoText;
    Button saveButton;
    TextView noPlayersText;
    LinearLayout playersList;
    Map<String, PlayerHolder> holders = new HashMap<>();
    String selectedPlayerId;
    private UserConnection oldUserConnection;

    public void setListener(Listener l) {
        mListener = l;
    }

    @Override
    public UserConnection getData() {
        return oldUserConnection;
    }

    @Override
    public void setData(Object viewData) {
        oldUserConnection = (UserConnection) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_user_connection, container, false);
        emailText = v.findViewById(R.id.emailText);
        roleSpinner = v.findViewById(R.id.roleSpinner);
        roleInfoText = v.findViewById(R.id.roleInfoText);
        saveButton = v.findViewById(R.id.saveButton);
        noPlayersText = v.findViewById(R.id.noPlayersText);
        playersList = v.findViewById(R.id.playersList);

        ArrayList<String> roleTitles = new ArrayList<>();
        roleTitles.add(getString(R.string.admin));
        roleTitles.add(getString(R.string.player));
        roleTitles.add(getString(R.string.guest));
        Helper.setSpinnerAdapter(roleSpinner, roleTitles);

        // Set holder views
        ArrayList<Player> players = AppRes.getInstance().getActivePlayers(true);
        playersList.removeAllViewsInLayout();
        LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
        holders = new HashMap<>();
        for (final Player player : players) {
            View view = inf.inflate(R.layout.list_item_player, playersList, false);
            final PlayerHolder holder = new PlayerHolder(view, false, false);
            holders.put(player.getPlayerId(), holder);
            if (player.getPicture() != null) {
                holder.pictureImage.setImageDrawable(ImageUtil.getRoundedDrawable(player.getPicture()));
            }

            holder.nameNumberShootsText.setText(player.getName());
            holder.positionText.setText(Player.getPositionText(player.getPosition(), false));

            holder.playerContainer.setOnClickListener(v12 -> {
                if (holder.isSelected()) {
                    holder.setSelected(false);
                    selectedPlayerId = null;
                } else {
                    for (Map.Entry<String, PlayerHolder> entry : holders.entrySet()) {
                        PlayerHolder holder1 = entry.getValue();
                        holder1.setSelected(false);
                    }
                    holder.setSelected(true);
                    selectedPlayerId = player.getPlayerId();
                }
            });
            playersList.addView(view);
        }

        // Initialize
        setPlayerListVisibility(false);
        setEditTextValue(emailText, "");
        setSpinnerSelection(roleSpinner, 0);
        roleInfoText.setText(getString(R.string.add_user_connection_info_admin));
        emailText.setEnabled(true);
        saveButton.setText(getString(R.string.save));
        selectedPlayerId = null;

        if (oldUserConnection != null) {
            setEditTextValue(emailText, oldUserConnection.getEmail());
            emailText.setEnabled(false);
            UserConnection.Role role = UserConnection.Role.fromDatabaseName(oldUserConnection.getRole());
            if (role == UserConnection.Role.PLAYER) {
                setSpinnerSelection(roleSpinner, 1);
                roleInfoText.setText(getString(R.string.add_user_connection_info_player));
            } else if (role == UserConnection.Role.GUEST) {
                setSpinnerSelection(roleSpinner, 2);
                roleInfoText.setText(getString(R.string.add_user_connection_info_guest));
            }
            UserConnection.Status status = UserConnection.Status.fromDatabaseName(oldUserConnection.getStatus());
            if (status == UserConnection.Status.DENY) {
                saveButton.setText(getString(R.string.add_user_connection_invite_again));
            }
            String playerId = oldUserConnection.getPlayerId();
            if (playerId != null) {
                PlayerHolder holder = holders.get(playerId);
                if (holder != null) {
                    selectedPlayerId = playerId;
                    holder.setSelected(true);
                }
            }
        }

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_admin));
                    setPlayerListVisibility(false);
                } else if (position == 1) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_player));
                    setPlayerListVisibility(true);
                } else if (position == 2) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_guest));
                    setPlayerListVisibility(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        saveButton.setOnClickListener(v1 -> saveUserConnection());
        return v;
    }

    public void setPlayerListVisibility(boolean visible) {
        if (visible) {
            ArrayList<Player> players = AppRes.getInstance().getActivePlayers(true);
            if (!players.isEmpty()) {
                playersList.setVisibility(View.VISIBLE);
                noPlayersText.setVisibility(View.GONE);
            } else {
                playersList.setVisibility(View.GONE);
                noPlayersText.setVisibility(View.VISIBLE);
            }
        } else {
            playersList.setVisibility(View.GONE);
            noPlayersText.setVisibility(View.GONE);
        }
    }

    private void saveUserConnection() {
        final String email = emailText.getText().toString();
        int position = roleSpinner.getSelectedItemPosition();
        UserConnection.Role role;
        if(position == 0) {
            role = UserConnection.Role.ADMIN;
        } else if(position == 1) {
            role = UserConnection.Role.PLAYER;
        } else {
            role = UserConnection.Role.GUEST;
        }
        if (StringUtils.isEmptyString(email)) {
            Logger.toast(getString(R.string.error_empty));
            return;
        }

        if (!isValidEmail(email)) {
            Logger.toast(getString(R.string.login_error_email));
            return;
        }

        if ((role == UserConnection.Role.PLAYER && selectedPlayerId == null)
                || (role == UserConnection.Role.ADMIN && selectedPlayerId != null)
                || (role == UserConnection.Role.GUEST && selectedPlayerId != null)) {
            Logger.toast(getString(R.string.add_user_connection_player_not_selected));
            return;
        }

        if (AppRes.getInstance().getUser().getEmail().equalsIgnoreCase(email)) {
            Logger.toast(getString(R.string.add_user_connection_email_self));
            return;
        }

        if (oldUserConnection != null && !email.equalsIgnoreCase(oldUserConnection.getEmail())) {
            Logger.toast(getString(R.string.add_user_connection_email_changed_error));
            return;
        }

        if (oldUserConnection == null) {
            for (UserConnection userConnection : AppRes.getInstance().getUserConnections().values()) {
                if (userConnection.getEmail().equalsIgnoreCase(email)) {
                    Logger.toast(getString(R.string.add_user_connection_email_already_added));
                    return;
                }
            }
        }

        // Collect data from view
        if (oldUserConnection != null) {
            final UserConnection userConnectionToSave = oldUserConnection.clone();
            userConnectionToSave.setEmail(email);
            userConnectionToSave.setRole(role.toDatabaseName());
            UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnectionToSave.getStatus());
            // Set status back to pending from deny
            if (status == UserConnection.Status.DENY) {
                userConnectionToSave.setStatus(UserConnection.Status.PENDING.toDatabaseName());
            }
            userConnectionToSave.setPlayerId(selectedPlayerId);

            UserConnectionsResource.getInstance().editUserConnection(userConnectionToSave, () -> {
                AppRes.getInstance().setUserConnection(userConnectionToSave.getUserConnectionId(), userConnectionToSave);
                UserConnection.Status status1 = UserConnection.Status.fromDatabaseName(userConnectionToSave.getStatus());
                // Send user invitation again
                if (status1 != UserConnection.Status.CONNECTED) {
                    sendUserInvitation(userConnectionToSave);
                } else {
                    mListener.onUserConnectionEdited(userConnectionToSave);
                }
            });
        } else {
            final UserConnection userConnectionToSave = new UserConnection();
            userConnectionToSave.setEmail(email);
            userConnectionToSave.setRole(role.toDatabaseName());
            userConnectionToSave.setStatus(UserConnection.Status.PENDING.toDatabaseName());
            userConnectionToSave.setPlayerId(selectedPlayerId);

            UserConnectionsResource.getInstance().addUserConnection(userConnectionToSave, id -> {
                userConnectionToSave.setUserConnectionId(id);
                AppRes.getInstance().setUserConnection(userConnectionToSave.getUserConnectionId(), userConnectionToSave);
                sendUserInvitation(userConnectionToSave);
            });
        }
    }

    private void sendUserInvitation(final UserConnection userConnection) {
        final UserInvitation userInvitation = new UserInvitation();
        userInvitation.setUserConnectionId(userConnection.getUserConnectionId());
        userInvitation.setTeamId(AppRes.getInstance().getSelectedTeam().getTeamId());
        userInvitation.setEmail(userConnection.getEmail());
        userInvitation.setRole(userConnection.getRole());
        UserInvitationsResource.getInstance().sendUserInvitation(userInvitation, () -> mListener.onUserConnectionEdited(userConnection));
    }

    public interface Listener {
        void onUserConnectionEdited(UserConnection userConnection);
    }
}
