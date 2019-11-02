package com.ardeapps.floorballmanager.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.BuildConfig;
import com.ardeapps.floorballmanager.MainActivity;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballmanager.dialogFragments.InfoDialogFragment;
import com.ardeapps.floorballmanager.objects.AppData;
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
import com.ardeapps.floorballmanager.services.AppInviteService;
import com.ardeapps.floorballmanager.services.FirebaseAuthService;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.ImageUtil;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.IconView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class SettingsFragment extends Fragment {

    View teamsDivider;
    Button teamsButton;
    Button inviteButton;
    Button changePasswordButton;
    Button logOutButton;
    TextView rateText;
    TextView moreText;
    TextView versionText;
    TextView privacyPolicyText;
    TextView emailText;
    LinearLayout userRequestsContainer;
    LinearLayout userInvitationsContainer;
    TextView userRequestsTitle;
    TextView userInvitationsTitle;

    private class UserRequestHolder {
        ImageView logoImage;
        TextView nameText;
        IconView removeIcon;
    }

    private class UserInvitationHolder {
        ImageView logoImage;
        TextView roleText;
        TextView nameText;
        IconView removeIcon;
        IconView acceptIcon;
    }

    private void update() {
        Map<String, UserInvitation> userInvitations = AppRes.getInstance().getUserInvitations();
        if (userInvitations.isEmpty()) {
            userInvitationsContainer.setVisibility(View.GONE);
            userInvitationsTitle.setVisibility(View.GONE);
        } else {
            userInvitationsContainer.setVisibility(View.VISIBLE);
            userInvitationsTitle.setVisibility(View.VISIBLE);

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
                    roleText = AppRes.getContext().getString(R.string.team_selection_user_invitation_admin);
                } else if (role == UserConnection.Role.PLAYER) {
                    roleText = AppRes.getContext().getString(R.string.team_selection_user_invitation_player);
                } else {
                    roleText = AppRes.getContext().getString(R.string.team_selection_user_invitation_guest);
                }
                holder.roleText.setText(roleText);
                holder.nameText.setText(userInvitation.getTeam().getName());

                holder.removeIcon.setOnClickListener(v -> UserConnectionsResource.getInstance().editUserConnectionAsInvited(userInvitation, UserConnection.Status.DENY, null, ()
                        -> UserInvitationsResource.getInstance().removeUserInvitation(userInvitation.getUserConnectionId(), () -> {
                    AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                    update();
                    FragmentListeners.getInstance().getApplicationListener().onUserInvitationHandled();
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
                                    UserConnectionsResource.getInstance().editUserConnectionAsInvited(userInvitation, UserConnection.Status.CONNECTED, userToSave.getUserId(), () -> {
                                        update();
                                        FragmentListeners.getInstance().getApplicationListener().onUserInvitationHandled();
                                    });
                                });
                            });
                        } else {
                            Logger.toast(R.string.team_selection_user_invitation_team_removed);
                            AppRes.getInstance().setUserInvitation(userInvitation.getUserConnectionId(), null);
                            update();
                            FragmentListeners.getInstance().getApplicationListener().onUserInvitationHandled();
                        }
                    });
                });

                userInvitationsContainer.addView(cv);
            }
        }

        Map<String, UserRequest> userRequests = AppRes.getInstance().getUserRequests();
        if (userRequests.isEmpty()) {
            userRequestsContainer.setVisibility(View.GONE);
            userRequestsTitle.setVisibility(View.GONE);
        } else {
            userRequestsContainer.setVisibility(View.VISIBLE);
            userRequestsTitle.setVisibility(View.VISIBLE);

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        userRequestsContainer = v.findViewById(R.id.userJoinRequestsContainer);
        userInvitationsContainer = v.findViewById(R.id.userInvitationsContainer);
        teamsButton = v.findViewById(R.id.teamsButton);
        teamsDivider = v.findViewById(R.id.teamsDivider);
        inviteButton = v.findViewById(R.id.inviteButton);
        changePasswordButton = v.findViewById(R.id.changePasswordButton);
        logOutButton = v.findViewById(R.id.logOutButton);
        rateText = v.findViewById(R.id.rateText);
        moreText = v.findViewById(R.id.moreText);
        versionText = v.findViewById(R.id.versionText);
        privacyPolicyText = v.findViewById(R.id.privacyPolicyText);
        emailText = v.findViewById(R.id.emailText);
        userRequestsTitle = v.findViewById(R.id.userRequestsTitle);
        userInvitationsTitle = v.findViewById(R.id.userInvitationsTitle);

        emailText.setText(AppRes.getInstance().getUser().getEmail());
        versionText.setText(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        rateText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_rate) + "</u>"));
        moreText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_more) + "</u>"));
        privacyPolicyText.setText(Html.fromHtml("<u>" + getString(R.string.settings_link_privacy) + "</u>"));

        update();

        Team currentTeam = AppRes.getInstance().getSelectedTeam();
        if(currentTeam != null) {
            teamsButton.setVisibility(View.VISIBLE);
            teamsDivider.setVisibility(View.VISIBLE);
            teamsButton.setOnClickListener(changeButton -> FragmentListeners.getInstance().getFragmentChangeListener().goToTeamSelectionFragment());
        } else {
            teamsButton.setVisibility(View.GONE);
            teamsDivider.setVisibility(View.GONE);
        }

        inviteButton.setOnClickListener(v16 -> AppInviteService.openChooser());

        changePasswordButton.setOnClickListener(v15 -> {
            ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.settings_change_password_confirm));
            dialogFragment.show(getChildFragmentManager(), "Lähetetäänkö salasananvaihtolinkki?");
            dialogFragment.setListener(() -> FirebaseAuthService.getInstance().sendPasswordResetEmail(AppRes.getInstance().getUser().getEmail(), () -> {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.login_forgot_password_sent));
                dialog.show(getChildFragmentManager(), "Salasanan vaihtolinkki lähetettiin");
            }));
        });

        logOutButton.setOnClickListener(v14 -> {
            FirebaseAuth.getInstance().signOut();
            FragmentActivity activity = AppRes.getActivity();
            Intent i = new Intent(activity, MainActivity.class);
            activity.finish();
            activity.overridePendingTransition(0, 0);
            activity.startActivity(i);
            activity.overridePendingTransition(0, 0);
        });

        rateText.setOnClickListener(v13 -> openUrl(AppData.GOOGLE_PLAY_APP_URL));

        moreText.setOnClickListener(v12 -> openUrl(getString(R.string.google_play_developer_url)));

        privacyPolicyText.setOnClickListener(v1 -> openUrl(AppData.PRIVACY_POLICY_URL));

        return v;
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
