package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.PlayerListAdapter;
import com.ardeapps.floorballcoach.dialogFragments.ActionMenuDialogFragment;
import com.ardeapps.floorballcoach.dialogFragments.ConfirmDialogFragment;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.resources.PlayerGamesResource;
import com.ardeapps.floorballcoach.resources.PlayerStatsResource;
import com.ardeapps.floorballcoach.resources.PlayersResource;
import com.ardeapps.floorballcoach.services.FirebaseDatabaseService;
import com.ardeapps.floorballcoach.views.PlayerHolder;

import java.util.ArrayList;


public class InactivePlayersFragment extends Fragment implements PlayerListAdapter.PlayerListSelectListener {

    ListView playerList;
    TextView noPlayersText;

    PlayerListAdapter adapter;

    public void update() {
        ArrayList<Player> players = new ArrayList<>();
        for(Player player : AppRes.getInstance().getPlayers().values()) {
            if(!player.isActive()) {
                players.add(player);
            }
        }
        adapter.setPlayers(players);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayerListAdapter(AppRes.getActivity(), PlayerHolder.ViewType.SELECT);
        adapter.setSelectListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_inactive_players, container, false);

        playerList = v.findViewById(R.id.playerList);
        noPlayersText = v.findViewById(R.id.noPlayersText);

        playerList.setEmptyView(noPlayersText);
        playerList.setAdapter(adapter);

        update();

        return v;
    }

    @Override
    public void onPlayerSelected(final Player player) {
        final ActionMenuDialogFragment dialog = ActionMenuDialogFragment.newInstance(getString(R.string.inactive_players_move_player_active));
        dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa tai poista");
        dialog.setListener(new ActionMenuDialogFragment.GoalMenuDialogCloseListener() {
            @Override
            public void onEditItem() {
                dialog.dismiss();
                // Palauta pelaajalistalle
                player.setActive(true);
                PlayersResource.getInstance().editPlayer(player, new FirebaseDatabaseService.EditDataSuccessListener() {
                    @Override
                    public void onEditDataSuccess() {
                        AppRes.getInstance().setPlayer(player.getPlayerId(), player);
                        update();
                    }
                });
            }

            @Override
            public void onRemoveItem() {
                dialog.dismiss();
                ConfirmDialogFragment dialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.inactive_players_remove_player_confirmation));
                dialogFragment.show(getChildFragmentManager(), "Poistetaanko pelaaja ja tilastot?");
                dialogFragment.setListener(new ConfirmDialogFragment.ConfirmationDialogCloseListener() {
                    @Override
                    public void onDialogYesButtonClick() {
                        PlayerStatsResource.getInstance().removeAllStats(player.getPlayerId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                            @Override
                            public void onDeleteDataSuccess() {
                                PlayerGamesResource.getInstance().removeAllGames(player.getPlayerId(), new FirebaseDatabaseService.DeleteDataSuccessListener() {
                                    @Override
                                    public void onDeleteDataSuccess() {
                                        PlayersResource.getInstance().removePlayer(player, new FirebaseDatabaseService.DeleteDataSuccessListener() {
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
                });
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });
    }
}