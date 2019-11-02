package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.adapters.TeamListAdapter;
import com.ardeapps.floorballmanager.objects.Team;
import com.ardeapps.floorballmanager.services.BillingService;
import com.ardeapps.floorballmanager.services.FragmentListeners;
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.ArrayList;


public class TeamSelectionFragment extends Fragment implements TeamListAdapter.Listener {

    Button bluetoothButton;
    Button addTeamButton;
    Button searchTeamButton;
    ListView teamList;
    TextView infoText;
    TeamListAdapter adapter;

    TextView productIdTextView;
    Button buyButton;
    Button detailsButton;

    BillingClient billingClient;

    public void update() {
        //bluetooth.refreshData();
        adapter.setTeams(new ArrayList<>(AppRes.getInstance().getTeams().values()));
        adapter.notifyDataSetChanged();
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
        View v = inflater.inflate(R.layout.fragment_team_selection, container, false);

        infoText = v.findViewById(R.id.infoText);
        bluetoothButton = v.findViewById(R.id.bluetoothButton);
        addTeamButton = v.findViewById(R.id.addTeamButton);
        searchTeamButton = v.findViewById(R.id.searchTeamButton);
        teamList = v.findViewById(R.id.teamList);

        productIdTextView = v.findViewById(R.id.productIdTextView);
        buyButton = v.findViewById(R.id.buyButton);
        detailsButton = v.findViewById(R.id.detailsButton);

        // Show 'new invitations' title, if team is not selected
        if(AppRes.getInstance().getSelectedTeam() == null && !AppRes.getInstance().getUserInvitations().isEmpty()) {
            infoText.setText(R.string.team_selection_user_invitation_info);
        } else {
            infoText.setText(R.string.team_selection_desc);
        }

        teamList.setAdapter(adapter);

        update();

        addTeamButton.setOnClickListener(v12 -> FragmentListeners.getInstance().getFragmentChangeListener().goToEditTeamFragment(null));
        searchTeamButton.setOnClickListener(v13 -> FragmentListeners.getInstance().getFragmentChangeListener().goToSearchTeamFragment());
        //bluetoothButton.setOnClickListener(v1 -> FragmentListeners.getInstance().getFragmentChangeListener().goToBluetoothFragment());

        // TODO billing service
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
        Team currentTeam = AppRes.getInstance().getSelectedTeam();
        if(currentTeam != null) {
            if(currentTeam.getTeamId().equals(team.getTeamId())) {
                Logger.toast(R.string.team_selection_already_selected);
                return;
            }
        }

        FragmentListeners.getInstance().getFragmentChangeListener().goToTeamDashboardFragment(team, this::update);
    }
}
