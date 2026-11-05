package model.tournament;

public enum TournamentState {
    NOT_DRAWN, // Groups not drawn yet -> Go to GroupDrawView
    DRAWN_UNCONFIRMED, //Sorteado, no confirmado
   GROUP_STAGE, // Group stage in progress -> Go to GroupStageView
   KNOCKOUT_STAGE // En playoffs (cuartos/semi/final) -> Ir a KnockoutView
}