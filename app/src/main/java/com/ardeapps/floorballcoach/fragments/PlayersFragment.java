package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.PlayerListAdapter;
import com.ardeapps.floorballcoach.dialogFragments.ActionMenuDialogFragment;
import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.resources.PlayersGamesStatsResource;
import com.ardeapps.floorballcoach.resources.TeamsPlayersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.views.PlayerHolder;


public class PlayersFragment extends Fragment implements PlayerListAdapter.PlayerListManageListener {

    Button createPlayerButton;
    ListView playerList;

    PlayerListAdapter adapter;

    public void update() {
        adapter.refreshData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(AppRes.getActivity(), PlayerHolder.ViewType.MANAGE);
        adapter.setManageListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_players, container, false);

        createPlayerButton = v.findViewById(R.id.createPlayerButton);
        playerList = v.findViewById(R.id.playerList);

        playerList.setAdapter(adapter);

        adapter.refreshData();
        adapter.notifyDataSetChanged();

        createPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(null);
            }
        });

        return v;
    }

    @Override
    public void onStatisticsClick(Player player) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToPlayerStatsFragment(player);
    }

    @Override
    public void onContainerClick(final Player player) {
        final ActionMenuDialogFragment dialog = new ActionMenuDialogFragment();
        dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
        dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
            @Override
            public void onEditItem() {
                dialog.dismiss();
                FragmentListeners.getInstance().getFragmentChangeListener().goToEditPlayerFragment(player);
            }

            @Override
            public void onRemoveItem() {
                dialog.dismiss();
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.add_player_remove_confirmation));
                dialogFragment.show(getChildFragmentManager(), "Poistetaanko pelaaja ja tilastot?");
                dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        PlayersGamesStatsResource.getInstance().removeAllStats(player.getPlayerId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                            @Override
                            public void onDeleteDataSuccess() {
                                TeamsPlayersResource.getInstance().removePlayer(player, new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                    @Override
                                    public void onDeleteDataSuccess() {
                                        AppRes.getInstance().setPlayer(player.getPlayerId(), null);
                                        update();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });


    }
}
