package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UserRequestsResource;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.viewObjects.DataView;
import com.ardeapps.floorballmanager.views.PlayerHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ardeapps.floorballmanager.utils.Helper.setSpinnerSelection;


public class AcceptUserRequestFragment extends Fragment implements DataView {

    TextView emailText;
    Spinner roleSpinner;
    TextView roleInfoText;
    Button saveButton;
    TextView noPlayersText;
    LinearLayout playersList;
    Map<String, PlayerHolder> holders = new HashMap<>();
    String selectedPlayerId;
    private UserRequest userRequest;

    @Override
    public UserRequest getData() {
        return userRequest;
    }

    @Override
    public void setData(Object viewData) {
        userRequest = (UserRequest) viewData;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accept_user_request, container, false);
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
        emailText.setText(userRequest.getEmail());
        setSpinnerSelection(roleSpinner, 0);
        roleInfoText.setText(getString(R.string.add_user_connection_info_admin));
        selectedPlayerId = null;

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_admin));
                    selectedPlayerId = null;
                    for (Map.Entry<String, PlayerHolder> entry : holders.entrySet()) {
                        PlayerHolder holder = entry.getValue();
                        holder.setSelected(false);
                    }
                    setPlayerListVisibility(false);
                } else if (position == 1) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_player));
                    setPlayerListVisibility(true);
                } else if (position == 2) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_guest));
                    selectedPlayerId = null;
                    setPlayerListVisibility(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        saveButton.setOnClickListener(v1 -> addUserConnection());
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

    private void addUserConnection() {
        int position = roleSpinner.getSelectedItemPosition();
        UserConnection.Role role;
        if(position == 0) {
            role = UserConnection.Role.ADMIN;
        } else if(position == 1) {
            role = UserConnection.Role.PLAYER;
        } else {
            role = UserConnection.Role.GUEST;
        }

        if ((role == UserConnection.Role.PLAYER && selectedPlayerId == null)
                || (role == UserConnection.Role.ADMIN && selectedPlayerId != null)
                || (role == UserConnection.Role.GUEST && selectedPlayerId != null)) {
            Logger.toast(getString(R.string.add_user_connection_player_not_selected));
            return;
        }

        for (UserConnection userConnection : AppRes.getInstance().getUserConnections().values()) {
            if (userConnection.getEmail().equalsIgnoreCase(userRequest.getEmail())) {
                Logger.toast(getString(R.string.add_user_connection_email_already_added));
                return;
            }
        }

        // Check if player is already connected
        for(UserConnection userConnection : AppRes.getInstance().getUserConnections().values()) {
            if(selectedPlayerId != null && selectedPlayerId.equals(userConnection.getPlayerId())) {
                Logger.toast(R.string.add_user_connection_exists);
                return;
            }
        }

        final UserConnection userConnectionToSave = new UserConnection();
        userConnectionToSave.setUserConnectionId(userRequest.getUserConnectionId());
        userConnectionToSave.setEmail(userRequest.getEmail());
        userConnectionToSave.setUserId(userRequest.getUserId());
        userConnectionToSave.setRole(role.toDatabaseName());
        userConnectionToSave.setStatus(UserConnection.Status.CONNECTED.toDatabaseName());
        userConnectionToSave.setPlayerId(selectedPlayerId);

        UserConnectionsResource.getInstance().editUserConnection(userConnectionToSave, () -> {
            AppRes.getInstance().setUserConnection(userConnectionToSave.getUserConnectionId(), userConnectionToSave);

            UserRequest userRequestToSave = userRequest.clone();
            userRequestToSave.setStatus(UserRequest.Status.ACCEPTED.toDatabaseName());
            UserRequestsResource.getInstance().editUserRequest(userRequestToSave, () -> {
                AppRes.getInstance().setUserJoinRequest(userRequest.getUserConnectionId(), userRequestToSave);
                AppRes.getActivity().onBackPressed();
            });
        });
    }
}
