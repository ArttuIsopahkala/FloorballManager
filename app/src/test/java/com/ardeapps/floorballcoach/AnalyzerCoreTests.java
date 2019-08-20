package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.services.AnalyzerCore;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class AnalyzerCoreTests extends JSONService {

    private static final ArrayList<String> attackSkills1 = new ArrayList<>(Arrays.asList(Player.Skill.PASSING.toDatabaseName(), Player.Skill.GAME_SENSE.toDatabaseName()));
    private static final ArrayList<String> attackSkills2 = new ArrayList<>(Arrays.asList(Player.Skill.SHOOTING.toDatabaseName(), Player.Skill.BALL_HANDLING.toDatabaseName()));
    private static final ArrayList<String> attackSkills3 = new ArrayList<>(Arrays.asList(Player.Skill.SPEED.toDatabaseName(), Player.Skill.PHYSICALITY.toDatabaseName(), Player.Skill.BALL_PROTECTION.toDatabaseName()));
    private static final ArrayList<String> defenceSkills1 = new ArrayList<>(Arrays.asList(Player.Skill.SHOOTING.toDatabaseName(), Player.Skill.PHYSICALITY.toDatabaseName()));
    private static final ArrayList<String> defenceSkills2 = new ArrayList<>(Arrays.asList(Player.Skill.GAME_SENSE.toDatabaseName(), Player.Skill.BALL_PROTECTION.toDatabaseName(), Player.Skill.PASSING.toDatabaseName()));
    private static final ArrayList<String> defenceSkills3 = new ArrayList<>(Arrays.asList(Player.Skill.BLOCKING.toDatabaseName(), Player.Skill.INTERCEPTION.toDatabaseName()));

    @Test
    public void testGetGoalsChemistry() {
        String playerId1 = "id1";
        String playerId2 = "id2";
        Goal goal1 = new Goal();

        ArrayList<Goal> goals = new ArrayList<>();
        goals.add(goal1);
        assertEquals(0, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));

        goal1.getPlayerIds().add(playerId1);
        goal1.getPlayerIds().add(playerId2);
        goal1.setOpponentGoal(true);
        assertEquals(-1, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setOpponentGoal(false);
        assertEquals(1, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setScorerId(playerId1);
        assertEquals(2, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setScorerId(playerId2);
        assertEquals(2, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setScorerId(null);
        goal1.setAssistantId(playerId1);
        assertEquals(2, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setAssistantId(playerId2);
        assertEquals(2, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setScorerId(playerId1);
        goal1.setAssistantId(playerId2);
        assertEquals(3, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
        goal1.setScorerId(playerId2);
        goal1.setAssistantId(playerId1);
        assertEquals(3, AnalyzerCore.getGoalsChemistry(playerId1, playerId2, goals));
    }

    @Test
    public void testGetStrengthsChemistry() {
        // VASEN HYÖKKÄÄJÄ
        Player.Position playerPos = Player.Position.LW;
        Player player = new Player();
        player.setShoots(Player.Shoots.LEFT.toDatabaseName());
        player.setStrengths(attackSkills1);
        Player.Position comparePlayerPos = Player.Position.C;
        Player comparePlayer = new Player();
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));

        player.setShoots(Player.Shoots.RIGHT.toDatabaseName());
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills3);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // CENTTERI
        playerPos = Player.Position.C;
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills2);
        comparePlayerPos = Player.Position.LW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills1);
        comparePlayerPos = Player.Position.LW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills2);
        comparePlayerPos = Player.Position.LW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayer.setStrengths(attackSkills1);
        comparePlayerPos = Player.Position.LW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // OIKEA LAITAHYÖKKÄÄJÄ
        playerPos = Player.Position.RW;
        player.setShoots(Player.Shoots.LEFT.toDatabaseName());
        comparePlayerPos = Player.Position.C;
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills3);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));

        player.setShoots(Player.Shoots.RIGHT.toDatabaseName());
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // VASEN PAKKI
        playerPos = Player.Position.LD;
        comparePlayerPos = Player.Position.RD;
        player.setStrengths(defenceSkills1);
        comparePlayer.setStrengths(defenceSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills2);
        comparePlayer.setStrengths(defenceSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills3);
        comparePlayer.setStrengths(defenceSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayer.setStrengths(defenceSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // OIKEA PAKKI
        playerPos = Player.Position.RD;
        comparePlayerPos = Player.Position.LD;
        player.setStrengths(defenceSkills1);
        comparePlayer.setStrengths(defenceSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills2);
        comparePlayer.setStrengths(defenceSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills3);
        comparePlayer.setStrengths(defenceSkills1);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayer.setStrengths(defenceSkills2);
        assertEquals(1, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
    }

    @Test
    public void testGetStrengthsChemistryNull() {
        Player.Position playerPos = Player.Position.LW;
        Player player = new Player();
        player.setShoots(Player.Shoots.LEFT.toDatabaseName());
        Player.Position comparePlayerPos = Player.Position.C;
        Player comparePlayer = new Player();
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));

        comparePlayer.setStrengths(attackSkills1);
        player.setStrengths(null);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(null);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
    }

    @Test
    public void testGetStrengthsChemistryZero() {
        // VASEN HYÖKKÄÄJÄ
        Player.Position playerPos = Player.Position.LW;
        Player player = new Player();
        player.setShoots(Player.Shoots.LEFT.toDatabaseName());
        player.setStrengths(attackSkills1);
        Player.Position comparePlayerPos = Player.Position.C;
        Player comparePlayer = new Player();
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));

        player.setShoots(Player.Shoots.RIGHT.toDatabaseName());
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // CENTTERI
        playerPos = Player.Position.C;
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills3);
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills3);
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills3);
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayer.setStrengths(attackSkills3);
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        comparePlayerPos = Player.Position.RW;
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // OIKEA LAITAHYÖKKÄÄJÄ
        playerPos = Player.Position.RW;
        player.setShoots(Player.Shoots.LEFT.toDatabaseName());
        comparePlayerPos = Player.Position.C;
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills2);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));

        player.setShoots(Player.Shoots.RIGHT.toDatabaseName());
        player.setStrengths(attackSkills1);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills2);
        comparePlayer.setStrengths(attackSkills3);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(attackSkills3);
        comparePlayer.setStrengths(attackSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // VASEN PAKKI
        playerPos = Player.Position.LD;
        comparePlayerPos = Player.Position.RD;
        player.setStrengths(defenceSkills1);
        comparePlayer.setStrengths(defenceSkills1);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills2);
        comparePlayer.setStrengths(defenceSkills3);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills3);
        comparePlayer.setStrengths(defenceSkills3);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        // OIKEA PAKKI
        playerPos = Player.Position.RD;
        comparePlayerPos = Player.Position.LD;
        player.setStrengths(defenceSkills1);
        comparePlayer.setStrengths(defenceSkills3);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills2);
        comparePlayer.setStrengths(defenceSkills2);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
        player.setStrengths(defenceSkills3);
        comparePlayer.setStrengths(defenceSkills3);
        assertEquals(0, AnalyzerCore.getStrengthsChemistry(playerPos, player, comparePlayerPos, comparePlayer));
    }

    @Test
    public void testGetShootsChemistry() {
        // VASEN HYÖKKÄÄJÄ
        Player.Position playerPos = Player.Position.LW;
        Player.Shoots playerShoots = Player.Shoots.LEFT;
        Player.Position comparePlayerPos = Player.Position.C;
        Player.Shoots comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.LD;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.LD;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // CENTTERI
        playerPos = Player.Position.C;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.LW;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.RW;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.LW;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.RW;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // OIKEA LAITAHYÖKKÄÄJÄ
        playerPos = Player.Position.RW;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.RD;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.C;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.RD;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // VASEN PAKKI
        playerPos = Player.Position.LD;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.LW;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.LW;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.RD;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // OIKEA PAKKI
        playerPos = Player.Position.RD;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.RW;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.LD;
        comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.RW;
        comparePlayerShoots = Player.Shoots.LEFT;
        assertEquals(1, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
    }

    @Test
    public void testGetShootsChemistryZero() {
        // VASEN HYÖKKÄÄJÄ
        Player.Position playerPos = Player.Position.LW;
        Player.Shoots playerShoots = Player.Shoots.LEFT;
        Player.Position comparePlayerPos = Player.Position.C;
        Player.Shoots comparePlayerShoots = Player.Shoots.RIGHT;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.RD;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // CENTTERI
        playerPos = Player.Position.C;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.RD;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.LD;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.C;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // OIKEA LAITAHYÖKKÄÄJÄ
        playerPos = Player.Position.RW;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.C;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.LD;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.RW;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // VASEN PAKKI
        playerPos = Player.Position.LD;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.C;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.RW;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.LD;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        // OIKEA PAKKI
        playerPos = Player.Position.RD;
        playerShoots = Player.Shoots.LEFT;
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));

        playerShoots = Player.Shoots.RIGHT;
        comparePlayerPos = Player.Position.LW;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
        comparePlayerPos = Player.Position.RD;
        assertEquals(0, AnalyzerCore.getShootsChemistry(playerPos, playerShoots, comparePlayerPos, comparePlayerShoots));
    }
}

