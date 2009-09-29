package org.mctsgammon.players;

import org.mctsgammon.Games;
import org.mctsgammon.MoveState;
import org.mctsgammon.Player;
import org.mctsgammon.ThrowState;

public class RolloutPlayer implements Player {
	
	private final int time;

	public RolloutPlayer(int time) {
		this.time = time;
	}
	
	@Override
	public ThrowState chooseMove(MoveState state) {
		ThrowState[] states = state.getChildren();
		if(states.length==1) return states[0];
		long endTime = System.currentTimeMillis()+time;
		double[] ev = new double[states.length];
		int[] nb = new int[states.length];
		int nbIterations = 0;
		int j=0;
		do{
			ev[j] += getEV(state.isBlackTurn, states[j], 1);
			nb[j]++;
			++nbIterations;
			j++;
			if(j==states.length) j=0;
		}while(System.currentTimeMillis()<endTime);

		System.out.println(this+" performed "+nbIterations+" iterations.");
		
		double maxEv = -1;
		int maxEvI = 0;
		for(int i=0;i<states.length;i++){
			if(ev[i]/nb[i]>maxEv){
				maxEv = ev[i]/nb[i];
				maxEvI = i;
			}
		}
		System.out.println(this+" predicts "+maxEv);
		return states[maxEvI];
	}

	private double getEV(boolean isBlackTurn, ThrowState throwState,
			int nbRollOuts) {
		Games games = new Games(throwState, nbRollOuts, new RandomPlayer(), new RandomPlayer());
		games.play();
		// if this turn is for black, then we are black, 
		return isBlackTurn ? games.getBlackPoints()-games.getRedPoints() : games.getRedPoints()-games.getBlackPoints();
		
	}
	
	@Override
	public String toString() {
		return "RolloutPlayer";
	}

	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}

}