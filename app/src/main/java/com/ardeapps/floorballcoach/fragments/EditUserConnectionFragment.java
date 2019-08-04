package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.handlers.GetUserHandler;
import com.ardeapps.floorballcoach.objects.User;
import com.ardeapps.floorballcoach.objects.UserConnection;
import com.ardeapps.floorballcoach.objects.UserInvitation;
import com.ardeapps.floorballcoach.resources.UserConnectionsResource;
import com.ardeapps.floorballcoach.resources.UserInvitationsResource;
import com.ardeapps.floorballcoach.resources.UsersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.utils.Logger;
import com.ardeapps.floorballcoach.utils.StringUtils;
import com.ardeapps.floorballcoach.viewObjects.DataView;

import java.util.ArrayList;

import static com.ardeapps.floorballcoach.utils.Helper.isValidEmail;
import static com.ardeapps.floorballcoach.utils.Helper.setEditTextValue;
import static com.ardeapps.floorballcoach.utils.Helper.setSpinnerSelection;


public class EditUserConnectionFragment extends Fragment implements DataView {

    EditText emailText;
    Spinner roleSpinner;
    TextView roleInfoText;
    Button saveButton;

    private UserConnection userConnection;

    public Listener mListener = null;

    public void setListener(Listener l) {
        mListener = l;
    }

    public interface Listener {
        void onUserConnectionEdited(UserConnection userConnection);
    }

    @Override
    public void setData(Object viewData) {
        userConnection = (UserConnection)viewData;
    }

    @Override
    public UserConnection getData() {
        return userConnection;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_user_connection, container, false);
        emailText = v.findViewById(R.id.emailText);
        roleSpinner = v.findViewById(R.id.roleSpinner);
        roleInfoText = v.findViewById(R.id.roleInfoText);
        saveButton = v.findViewById(R.id.saveButton);

        ArrayList<String> roleTitles = new ArrayList<>();
        roleTitles.add(getString(R.string.admin));
        roleTitles.add(getString(R.string.player));
        Helper.setSpinnerAdapter(roleSpinner, roleTitles);

        // Initialize
        setEditTextValue(emailText, "");
        setSpinnerSelection(roleSpinner, 0);
        roleInfoText.setText(getString(R.string.add_user_connection_info_admin));
        emailText.setEnabled(true);
        saveButton.setText(getString(R.string.save));

        if(userConnection != null) {
            setEditTextValue(emailText, userConnection.getEmail());
            emailText.setEnabled(false);
            UserConnection.Role role = UserConnection.Role.fromDatabaseName(userConnection.getRole());
            if(role == UserConnection.Role.PLAYER) {
                setSpinnerSelection(roleSpinner, 1);
                roleInfoText.setText(getString(R.string.add_user_connection_info_player));
            }
            UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnection.getStatus());
            if(status == UserConnection.Status.DENY) {
                saveButton.setText(getString(R.string.add_user_connection_invite_again));
            }
        }

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_admin));
                } else if(position == 1) {
                    roleInfoText.setText(getString(R.string.add_user_connection_info_player));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserConnection();
            }
        });
        return v;
    }

    private void saveUserConnection() {
        final String email = emailText.getText().toString();
        int position = roleSpinner.getSelectedItemPosition();
        final UserConnection.Role role = position == 0 ? UserConnection.Role.ADMIN : UserConnection.Role.PLAYER;

        if(StringUtils.isEmptyString(email)) {
            Logger.toast(getString(R.string.error_empty));
            return;
        }

        if(!isValidEmail(email)) {
            Logger.toast(getString(R.string.login_error_email));
            return;
        }

        if(AppRes.getInstance().getUser().getEmail().equalsIgnoreCase(email)) {
            Logger.toast(getString(R.string.add_user_connection_email_self));
            return;
        }

        if(userConnection != null && !email.equalsIgnoreCase(userConnection.getEmail())) {
            Logger.toast(getString(R.string.add_user_connection_email_changed_error));
            return;
        }

        for(UserConnection userConnection : AppRes.getInstance().getUserConnections().values()) {
            if(userConnection.getEmail().equalsIgnoreCase(email)) {
                Logger.toast(getString(R.string.add_user_connection_email_already_added));
                return;
            }
        }

        if(userConnection != null) {
            final UserConnection userConnectionToSave = userConnection.clone();
            userConnectionToSave.setRole(role.toDatabaseName());
            final UserConnection.Status status = UserConnection.Status.fromDatabaseName(userConnectionToSave.getStatus());
            // Set status back to pending
            if(status != UserConnection.Status.CONNECTED) {
                userConnectionToSave.setStatus(UserConnection.Status.PENDING.toDatabaseName());
            }
            UserConnectionsResource.getInstance().editUserConnection(userConnectionToSave, new FirebaseDatabaseService.EditDataSuccessListener() {
                @Override
                public void onEditDataSuccess() {
                    AppRes.getInstance().setUserConnection(userConnectionToSave.getUserConnectionId(), userConnectionToSave);
                    // Send or update invitation
                    if(status != UserConnection.Status.CONNECTED) {
                        final UserInvitation userInvitation = new UserInvitation();
                        userInvitation.setUserConnectionId(userConnectionToSave.getUserConnectionId());
                        userInvitation.setTeamId(AppRes.getInstance().getSelectedTeam().getTeamId());
                        userInvitation.setEmail(userConnectionToSave.getEmail());
                        userInvitation.setRole(userConnectionToSave.getRole());
                        UserInvitationsResource.getInstance().sendUserInvitation(userInvitation, new FirebaseDatabaseService.EditDataSuccessListener() {
                            @Override
                            public void onEditDataSuccess() {
                                mListener.onUserConnectionEdited(userConnectionToSave);
                            }
                        });
                    } else {
                        mListener.onUserConnectionEdited(userConnectionToSave);
                    }
                }
            });
        } else {
            // Check if user exists
            UsersResource.getInstance().getUserByEmail(email, new GetUserHandler() {
                @Override
                public void onUserLoaded(final User user) {
                    final UserConnection userConnectionToSave = new UserConnection();
                    userConnectionToSave.setEmail(email);
                    userConnectionToSave.setUserId(user != null ? user.getUserId() : null);
                    userConnectionToSave.setRole(role.toDatabaseName());
                    userConnectionToSave.setStatus(UserConnection.Status.PENDING.toDatabaseName());

                    UserConnectionsResource.getInstance().addUserConnection(userConnectionToSave, new FirebaseDatabaseService.AddDataSuccessListener() {
                        @Override
                        public void onAddDataSuccess(String id) {
                            userConnectionToSave.setUserConnectionId(id);
                            AppRes.getInstance().setUserConnection(userConnectionToSave.getUserConnectionId(), userConnectionToSave);

                            final UserInvitation userInvitation = new UserInvitation();
                            userInvitation.setUserConnectionId(userConnectionToSave.getUserConnectionId());
                            userInvitation.setTeamId(AppRes.getInstance().getSelectedTeam().getTeamId());
                            userInvitation.setEmail(userConnectionToSave.getEmail());
                            userInvitation.setRole(userConnectionToSave.getRole());
                            UserInvitationsResource.getInstance().sendUserInvitation(userInvitation, new FirebaseDatabaseService.EditDataSuccessListener() {
                                @Override
                                public void onEditDataSuccess() {
                                    mListener.onUserConnectionEdited(userConnectionToSave);
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
