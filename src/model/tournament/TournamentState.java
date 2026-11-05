package model.tournament;

public enum TournamentState {
    NOT_DRAWN, // Groups not drawn yet -> Go to GroupDrawView
    DRAWN_UNCONFIRMED, // Drawn, not confirmed yet
   GROUP_STAGE, // Group stage in progress -> Go to GroupStageView
   KNOCKOUT_STAGE // Knockout stage (quarter/semi/final) -> Go to KnockoutView
}