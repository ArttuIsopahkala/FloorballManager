package com.ardeapps.floorballmanager.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.handlers.GetGoalsHandler;
import com.ardeapps.floorballmanager.handlers.GetPenaltiesHandler;
import com.ardeapps.floorballmanager.objects.Game;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.objects.Player;
import com.ardeapps.floorballmanager.objects.Season;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.PenaltiesResource;
import com.ardeapps.floorballmanager.resources.PlayerGamesResource;
import com.ardeapps.floorballmanager.services.StatsHelper;
import com.ardeapps.floorballmanager.utils.Helper;
import com.ardeapps.floorballmanager.viewObjects.PlayerPenaltiesData;
import com.ardeapps.floorballmanager.viewObjects.PlayerStatsData;
import com.ardeapps.floorballmanager.views.AssistsStatList;
import com.ardeapps.floorballmanager.views.GoalsStatList;
import com.ardeapps.floorballmanager.views.PenaltiesStatList;
import com.ardeapps.floorballmanager.views.PointsStatList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class StatsFragment extends Fragment {

    Spinner seasonSpinner;
    TextView noSeasonsText;
    Spinner typeSpinner;
    PointsStatList pointsStatList;
    PenaltiesStatList penaltiesStatList;
    AssistsStatList assistsStatList;
    GoalsStatList goalsStatList;

    Map<String, Player> players = new HashMap<>();
    Map<String, ArrayList<Game>> playerGames;
    ArrayList<Goal> seasonGoals;
    ArrayList<Penalty> seasonPenalties;

    StatsType selectedStatsType;
    String selectedSeasonId;

    public enum StatsType {
        POINTS,
        GOALS,
        ASSISTS,
        PENALTIES
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        players = AppRes.getInstance().getPlayers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        noSeasonsText = v.findViewById(R.id.noSeasonsText);
        typeSpinner = v.findViewById(R.id.typeSpinner);
        pointsStatList = v.findViewById(R.id.pointsStatList);
        penaltiesStatList = v.findViewById(R.id.penaltiesStatList);
        assistsStatList = v.findViewById(R.id.assistsStatList);
        goalsStatList = v.findViewById(R.id.goalsStatList);

        setSeasonSpinner();

        Map<StatsType, String> typeMap = new TreeMap<>();
        typeMap.put(StatsType.POINTS, getString(R.string.stats_type_points));
        typeMap.put(StatsType.GOALS, getString(R.string.stats_type_goals));
        typeMap.put(StatsType.ASSISTS, getString(R.string.stats_type_assists));
        typeMap.put(StatsType.PENALTIES, getString(R.string.stats_type_penalties));
        ArrayList<String> typeTitles = new ArrayList<>(typeMap.values());
        ArrayList<StatsType> statsTypes = new ArrayList<>(typeMap.keySet());
        Helper.setSpinnerAdapter(typeSpinner, typeTitles);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatsType = statsTypes.get(position);
                loadStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return v;
    }

    public void setSeasonSpinner() {
        Map<String, Season> seasons = AppRes.getInstance().getSeasons();

        if (seasons.isEmpty()) {
            noSeasonsText.setVisibility(View.VISIBLE);
            seasonSpinner.setVisibility(View.GONE);
        } else {
            noSeasonsText.setVisibility(View.GONE);
            seasonSpinner.setVisibility(View.VISIBLE);
        }
        final ArrayList<String> seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        for (Season season : seasons.values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);
        int seasonPosition = 0;
        if (!seasons.isEmpty()) {
            Season selectedSeason = AppRes.getInstance().getSelectedSeason();
            if (selectedSeason != null) {
                seasonPosition = seasonIds.indexOf(selectedSeason.getSeasonId());
            }
            Helper.setSpinnerSelection(seasonSpinner, seasonPosition > -1 ? seasonPosition : 0);
        }

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSeasonId = seasonIds.get(position);

                playerGames = null;
                seasonGoals = null;
                seasonPenalties = null;

                loadStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadStats() {
        if (selectedSeasonId == null) {
            return;
        }
        if(playerGames != null && seasonGoals != null && seasonPenalties != null) {
            populateList();
        } else {
            // Get games, goals and penalties
            PlayerGamesResource.getInstance().getSeasonGames(players.keySet(), selectedSeasonId, seasonGames -> {
                StatsFragment.this.playerGames = seasonGames;
                GoalsResource.getInstance().getGoals(selectedSeasonId, (GetGoalsHandler) goals -> {
                    ArrayList<Goal> seasonGoals = new ArrayList<>();
                    for(ArrayList<Goal> gameGoals : goals.values()) {
                        seasonGoals.addAll(gameGoals);
                    }
                    StatsFragment.this.seasonGoals = seasonGoals;
                    PenaltiesResource.getInstance().getPenalties(selectedSeasonId, (GetPenaltiesHandler) penalties -> {
                        ArrayList<Penalty> seasonPenalties = new ArrayList<>();
                        for(ArrayList<Penalty> gamePenalties : penalties.values()) {
                            seasonPenalties.addAll(gamePenalties);
                        }
                        StatsFragment.this.seasonPenalties = seasonPenalties;
                        populateList();
                    });
                });
            });
        }
    }

    public class PlayerStatsItem {
        public Player player;
        public int gameCount;
        public PlayerStatsData statsData;
        public PlayerPenaltiesData penaltiesData;
    }

    private void populateList() {
        ArrayList<PlayerStatsItem> playerStatsItems = new ArrayList<>();
        for (Player player : players.values()) {
            String playerId = player.getPlayerId();
            ArrayList<Game> games = playerGames.get(playerId);
            int gamesCount = games != null ? games.size() : 0;

            PlayerStatsData statsData = StatsHelper.getPlayerStats(playerId, gamesCount, seasonGoals);
            PlayerPenaltiesData penaltiesData = StatsHelper.getPlayerPenaltiesData(playerId, gamesCount, seasonPenalties);

            PlayerStatsItem item = new PlayerStatsItem();
            item.player = player;
            item.gameCount = gamesCount;
            item.statsData = statsData;
            item.penaltiesData = penaltiesData;
            playerStatsItems.add(item);
        }

        pointsStatList.setVisibility(View.GONE);
        goalsStatList.setVisibility(View.GONE);
        assistsStatList.setVisibility(View.GONE);
        penaltiesStatList.setVisibility(View.GONE);

        if (selectedStatsType == StatsType.POINTS) {
            pointsStatList.setVisibility(View.VISIBLE);
            pointsStatList.setItems(playerStatsItems, HeaderType.POINTS);
        } else if (selectedStatsType == StatsType.GOALS) {
            goalsStatList.setVisibility(View.VISIBLE);
            goalsStatList.setItems(playerStatsItems, HeaderType.GOALS);
        } else if (selectedStatsType == StatsType.ASSISTS) {
            assistsStatList.setVisibility(View.VISIBLE);
            assistsStatList.setItems(playerStatsItems, HeaderType.ASSISTS);
        } else if (selectedStatsType == StatsType.PENALTIES) {
            penaltiesStatList.setVisibility(View.VISIBLE);
            penaltiesStatList.setItems(playerStatsItems, HeaderType.PENALTIES);
        }
    }

    public enum HeaderType {
        NAMES,
        GAMES,
        PLUSES,
        MINUSES,
        PLUS_MINUSES,
        GOALS,
        ASSISTS,
        POINTS,
        YV_GOALS,
        AV_GOALS,
        RL_GOALS,
        GOALS_PER_GAME,
        YV_ASSISTS,
        AV_ASSISTS,
        ASSISTS_PER_GAME,
        MIN_2,
        MIN_5,
        MIN_10,
        MIN_20,
        PENALTIES_PER_GAME,
        PENALTIES
    }

    public static ArrayList<PlayerStatsItem> sortStatsList(Map<HeaderType, TextView> headers, HeaderType sort, ArrayList<PlayerStatsItem> playerStatsItems) {
        for(TextView existingHeader : headers.values()) {
            existingHeader.setTypeface(Typeface.DEFAULT);
            existingHeader.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light_secondary));
        }
        TextView header = headers.get(sort);
        if(header != null) {
            header.setTypeface(Typeface.DEFAULT_BOLD);
            header.setTextColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_text_light));
        }

        if(sort == HeaderType.NAMES) {
            Collections.sort(playerStatsItems, (o1, o2) -> o1.player.getName().compareTo(o2.player.getName()));
        } else if(sort == HeaderType.GAMES) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.gameCount, o1.gameCount));
        } else if(sort == HeaderType.PLUSES) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.pluses, o1.statsData.pluses));
        } else if(sort == HeaderType.MINUSES) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.minuses, o1.statsData.minuses));
        } else if(sort == HeaderType.PLUS_MINUSES) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.plusMinus, o1.statsData.plusMinus));
        } else if(sort == HeaderType.GOALS) {
            Collections.sort(playerStatsItems, (o1, o2) -> {
                if(o1.statsData.scores != o2.statsData.scores) {
                    return Integer.compare(o2.statsData.scores, o1.statsData.scores);
                }
                return Integer.compare(o1.gameCount, o2.gameCount);
            });
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.scores, o1.statsData.scores));
        } else if(sort == HeaderType.ASSISTS) {
            Collections.sort(playerStatsItems, (o1, o2) -> {
                if(o1.statsData.assists != o2.statsData.assists) {
                    return Integer.compare(o2.statsData.assists, o1.statsData.assists);
                }
                return Integer.compare(o1.gameCount, o2.gameCount);
            });
        } else if(sort == HeaderType.POINTS) {
            Collections.sort(playerStatsItems, (o1, o2) -> {
                if (o1.statsData.points != o2.statsData.points) {
                    return Integer.compare(o2.statsData.points, o1.statsData.points);
                }
                if (o1.statsData.scores != o2.statsData.scores) {
                    return Integer.compare(o2.statsData.scores, o1.statsData.scores);
                }
                return Integer.compare(o2.gameCount, o1.gameCount);
            });
        } else if(sort == HeaderType.YV_GOALS) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.yvScores, o1.statsData.yvScores));
        } else if(sort == HeaderType.AV_GOALS) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.avScores, o1.statsData.avScores));
        } else if(sort == HeaderType.RL_GOALS) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.rlScores, o1.statsData.rlScores));
        } else if(sort == HeaderType.GOALS_PER_GAME) {
            Collections.sort(playerStatsItems, (o1, o2) -> Double.compare(o2.statsData.scoresPerGame, o1.statsData.scoresPerGame));
        }  else if(sort == HeaderType.YV_ASSISTS) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.yvAssists, o1.statsData.yvAssists));
        } else if(sort == HeaderType.AV_ASSISTS) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.statsData.avAssists, o1.statsData.avAssists));
        } else if(sort == HeaderType.ASSISTS_PER_GAME) {
            Collections.sort(playerStatsItems, (o1, o2) -> Double.compare(o2.statsData.assistsPerGame, o1.statsData.assistsPerGame));
        } else if(sort == HeaderType.MIN_2) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.penaltiesData.penalties2min, o1.penaltiesData.penalties2min));
        } else if(sort == HeaderType.MIN_5) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.penaltiesData.penalties5min, o1.penaltiesData.penalties5min));
        } else if(sort == HeaderType.MIN_10) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.penaltiesData.penalties10min, o1.penaltiesData.penalties10min));
        } else if(sort == HeaderType.MIN_20) {
            Collections.sort(playerStatsItems, (o1, o2) -> Integer.compare(o2.penaltiesData.penalties20min, o1.penaltiesData.penalties20min));
        } else if(sort == HeaderType.PENALTIES_PER_GAME) {
            Collections.sort(playerStatsItems, (o1, o2) -> Double.compare(o2.penaltiesData.penaltiesPerGame, o1.penaltiesData.penaltiesPerGame));
        } else if(sort == HeaderType.PENALTIES) {
            Collections.sort(playerStatsItems, (o1, o2) -> {
                if(o1.penaltiesData.penalties != o2.penaltiesData.penalties) {
                    return Long.compare(o2.penaltiesData.penalties, o1.penaltiesData.penalties);
                }
                return Integer.compare(o1.gameCount, o2.gameCount);
            });
        }
        return playerStatsItems;
    }
}
