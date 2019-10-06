package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.TeamListAdapter;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.objects.User;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.objects.UserInvitation;
import com.ardeapps.floorballmanager.objects.UserRequest;
import com.ardeapps.floorballmanager.resources.TeamsResource;
import com.ardeapps.floorballmanager.resources.UserConnectionsResource;
import com.ardeapps.floorballmanager.resources.UserInvitationsResource;
import com.ardeapps.floorballmanager.resources.UserRequestsResource;
import com.ardeapps.floorballmanager.resources.UsersResource;
import com.ardeapps.floorballmanager.services.BillingService;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.IconView;

import java.util.ArrayList;
import java.util.Map;


public class MainSelectionFragment extends Fragment implements TeamListAdapter.Listener {

    Button bluetoothButton;
    Button addTeamButton;
    Button searchTeamButton;
    ListView teamList;
    TextView userInvitationInfoText;
    LinearLayout userInvitationsContainer;
    LinearLayout userRequestsContainer;
    TeamListAdapter adapter;

    TextView productIdTextView;
    Button buyButton;
    Button detailsButton;

    BillingClient billingClient;

    public void update() {
        //bluetooth.refreshData();
        adapter.setTeams(new ArrayList<>(AppRes.getInstance().getTeams().values()));
        adapter.notifyDataSetChanged();

        Map<String, UserInvitation> userInvitations = AppRes.getInstance().getUserInvitations();
        if (userInvitations.isEmpty()) {
            userInvitationInfoText.setVisibility(View.GONE);
            userInvitationsContainer.setVisibility(View.GONE);
        } else {
            userInvitationInfoText.setVisibility(View.VISIBLE);
            userInvitationsContainer.setVisibility(View.VISIBLE);

            final UserInvitationHolder holder = new UserInvitationHolder();
            LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
            userInvitationsContainer.removeAllViews();

            for (final UserInvitation userInvitation : userInvitations.values()) {
                View cv = inf.inflate(R.layout.list_item_user_invitation, userInvitationsContainer, false);
                holder.roleText = cv.findViewById(R.id.roleText);
                holder.nameText = cv.findViewById(R.id.nameText);
                holder.removeIcon = cv.findViewById(R.id.removeIcon);
                holder.acceptIcon = cv.findViewById(R.id.acceptIcon);
                holder.logoImage = cv.findViewById(R.id.logoImage);

                final Team team = userInvitation.getTeam();
                if (team.getLogo() != null) {
                    holder.logoImage.setImageBitmap(ImageUtil.getSquarePicture(team.getLogo()));
                } else {
                    holder.logoImage.setImageResource(R.drawable.default_logo);
                }

                String roleText;
                UserConnection.Role role = UserConnection.Role.fromDatabaseName(userInvitation.getRole());
                if (role == UserConnection.Role.ADMIN) {
                    roleText = getString(R.string.main_selection_user_invitation_admin);
                } else if (role == UserConnection.Role.PLAYER) {
                    roleText = getString(R.string.main_selection_user_invitation_player);
                } else {
                    roleText = getString(R.string.main_selection_user_invitation_guest);
                }
                holder.roleText.setText(roleText);
                holder.nameText.setText(userInvitation.getTeam().getName());

                holder.removeIcon.setOnClickListener(v -> UserConnectionsResource.getInstance().editUserConnectionAsInvited(userInvitation, UserConnection.Status.DENY, null, ()
                        -> UserInvitationsResource.getInstance().removeUserInvitation(userInvitation.getUserConnectionId(), () -> {
                    AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                    update();
                })));

                holder.acceptIcon.setOnClickListener(v -> {
                    // Check that team still exists
                    TeamsResource.getInstance().getTeam(userInvitation.getTeamId(), false, team1 -> {
                        if(team1 != null) {
                            // Add team to user
                            final User userToSave = AppRes.getInstance().getUser();
                            userToSave.getTeamIds().add(userInvitation.getTeamId());
                            UsersResource.getInstance().editUser(userToSave, () -> {
                                AppRes.getInstance().setUser(userToSave);
                                // Add team to AppRes. Team is loaded when invitations are shown.
                                AppRes.getInstance().getTeams().put(userInvitation.getTeamId(), userInvitation.getTeam());
                                // Remove invitation
                                UserInvitationsResource.getInstance().removeUserInvitation(userInvitation.getUserConnectionId(), () -> {
                                    AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                                    // Set user connection as connected
                                    UserConnectionsResource.getInstance().editUserConnectionAsInvited(userInvitation, UserConnection.Status.CONNECTED, userToSave.getUserId(), this::update);
                                });
                            });
                        } else {
                            Logger.toast(R.string.main_selection_user_invitation_team_removed);
                            AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                            update();
                        }
                    });
                });

                userInvitationsContainer.addView(cv);
            }
        }

        Map<String, UserRequest> userRequests = AppRes.getInstance().getUserRequests();
        if (userRequests.isEmpty()) {
            userRequestsContainer.setVisibility(View.GONE);
        } else {
            userRequestsContainer.setVisibility(View.VISIBLE);

            final UserRequestHolder holder = new UserRequestHolder();
            LayoutInflater inf = LayoutInflater.from(AppRes.getContext());
            userRequestsContainer.removeAllViews();

            for (final UserRequest userRequest : userRequests.values()) {
                View cv = inf.inflate(R.layout.list_item_user_request, userRequestsContainer, false);
                holder.nameText = cv.findViewById(R.id.nameText);
                holder.removeIcon = cv.findViewById(R.id.removeIcon);
                holder.logoImage = cv.findViewById(R.id.logoImage);

                final Team team = userRequest.getTeam();
                if (team.getLogo() != null) {
                    holder.logoImage.setImageBitmap(ImageUtil.getSquarePicture(team.getLogo()));
                } else {
                    holder.logoImage.setImageResource(R.drawable.default_logo);
                }

                holder.nameText.setText(userRequest.getTeam().getName());

                holder.removeIcon.setOnClickListener(v -> {
                    UserRequestsResource.getInstance().removeUserRequest(userRequest.getUserConnectionId(), () -> {
                        AppRes.getInstance().setUserRequest(userRequest.getUserConnectionId(), null);
                        UserConnectionsResource.getInstance().editUserConnectionAsRequest(userRequest, UserConnection.Status.DENY, this::update);
                    });
                });

                userRequestsContainer.addView(cv);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TeamListAdapter(AppRes.getActivity(), TeamListAdapter.Type.SELECT);
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_selection, container, false);

        bluetoothButton = v.findViewById(R.id.bluetoothButton);
        addTeamButton = v.findViewById(R.id.addTeamButton);
        searchTeamButton = v.findViewById(R.id.searchTeamButton);
        teamList = v.findViewById(R.id.teamList);
        userInvitationsContainer = v.findViewById(R.id.userInvitationsContainer);
        userRequestsContainer = v.findViewById(R.id.userJoinRequestsContainer);
        userInvitationInfoText = v.findViewById(R.id.userInvitationInfoText);

        productIdTextView = v.findViewById(R.id.productIdTextView);
        buyButton = v.findViewById(R.id.buyButton);
        detailsButton = v.findViewById(R.id.detailsButton);

        teamList.setAdapter(adapter);

        update();

        addTeamButton.setOnClickListener(v12 -> FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(null));
        searchTeamButton.setOnClickListener(v13 -> FragmentListeners.getInstance().getFragmentChangeListener().goToSearchTeamFragment());
        //bluetoothButton.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToBluetoothFragment());

        // TODO
        final BillingService billingService = new BillingService();
        //billingService.initialize();
       /* buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billingService.startBillingFlow();
            }
        });
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billingService.loadProduct(new BillingService.LoadProductListener() {
                    @Override
                    public void onProductsLoaded(List<SkuDetails> skuDetailsList) {
                        productIdTextView.setText(skuDetailsList.get(0).getTitle());
                    }
                });
            }
        });*/
        return v;
    }

    @Override
    public void onTeamSelected(Team team) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToTeamDashboardFragment(team);
    }

    private class UserInvitationHolder {
        ImageView logoImage;
        TextView roleText;
        TextView nameText;
        IconView removeIcon;
        IconView acceptIcon;
    }

    private class UserRequestHolder {
        ImageView logoImage;
        TextView nameText;
        IconView removeIcon;
    }
}
