package model;

public enum TournamentState {
    NOT_DRAWN, // Aún no se sortearon los grupos -> Ir a GroupDrawView
   GROUP_STAGE, // En plena fase de grupos -> Ir a GroupStageView
   KNOCKOUT_STAGE // En playoffs (cuartos/semi/final) -> Ir a KnockoutView
}