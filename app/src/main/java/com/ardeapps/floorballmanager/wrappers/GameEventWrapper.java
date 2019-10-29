package com.ardeapps.floorballmanager.wrappers;


import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.eventGoalDialog.GoalWizardDialogFragment;
import com.ardeapps.floorballmanager.eventPenaltyDialog.PenaltyWizardDialogFragment;
import com.ardeapps.floorballmanager.objects.Goal;
import com.ardeapps.floorballmanager.objects.Penalty;
import com.ardeapps.floorballmanager.viewObjects.GameFragmentData;
import com.ardeapps.floorballmanager.viewObjects.GoalWizardDialogData;
import com.ardeapps.floorballmanager.viewObjects.PenaltyWizardDialogData;


public class GameEventWrapper {

    public interface PenaltyDialogListener {
        void onSavePenalty(Penalty penalty);
    }

    public interface GoalDialogListener {
        void onSaveGoal(Goal goal);
    }

    private static GameEventWrapper instance;
    private static GameFragmentData data;

    public static GameEventWrapper getInstance(GameFragmentData gameFragmentData) {
        if (instance == null) {
            instance = new GameEventWrapper();
        }
        data = gameFragmentData;
        return instance;
    }

    public void openPenaltyWizardDialog(final Penalty penalty, boolean isHomePenalty, PenaltyDialogListener listener) {
        final PenaltyWizardDialogFragment dialog = new PenaltyWizardDialogFragment();
        dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa rangaistusta");

        final boolean opponentPenalty = (data.getGame().isHomeGame() && !isHomePenalty) || (!data.getGame().isHomeGame() && isHomePenalty);
        PenaltyWizardDialogData dialogData = new PenaltyWizardDialogData();
        dialogData.setGame(data.getGame());
        dialogData.setLines(data.getLines());
        dialogData.setOpponentPenalty(opponentPenalty);
        dialogData.setPenalty(penalty);
        dialog.setData(dialogData);

        dialog.setListener(penaltyToSave -> {
            dialog.dismiss();
            listener.onSavePenalty(penaltyToSave);
        });
    }

    public void openGoalWizardDialog(final Goal goal, boolean isHomeGoal, GoalDialogListener listener) {
        final GoalWizardDialogFragment dialog = new GoalWizardDialogFragment();
        dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Muokkaa maalia");

        final boolean opponentGoal = (data.getGame().isHomeGame() && !isHomeGoal) || (!data.getGame().isHomeGame() && isHomeGoal);
        GoalWizardDialogData dialogData = new GoalWizardDialogData();
        dialogData.setGoal(goal);
        dialogData.setGame(data.getGame());
        dialogData.setLines(data.getLines());
        dialogData.setPenalties(data.getPenalties());
        dialogData.setGoals(data.getGoals());
        dialogData.setOpponentGoal(opponentGoal);
        dialog.setData(dialogData);

        dialog.setListener(goalToSave -> {
            dialog.dismiss();
            listener.onSaveGoal(goalToSave);
        });
    }
}
