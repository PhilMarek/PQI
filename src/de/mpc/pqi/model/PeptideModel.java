package de.mpc.pqi.model;

import java.util.List;

/**
 * Specialization of PQIModel. Store information for peptides, e.g. the peptide name, abundances.
 * Furthermore, a peptide can be deactivated for output.
 * 
 * @author Phil Marek
 *
 */
public class PeptideModel extends PQIModel {
	public static class State {
		private List<Long> abundances;
		
		public State(List<Long> abundances) {
			this.abundances = abundances;
		}
		
		public List<Long> getAbundances() {
			return abundances;
		}
	}
	
	private String name;
	private boolean activated = true;
	private List<State> states;
	
	public PeptideModel(String name, List<State> states) {
		this.name = name;
		this.states  = states;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	@Override
	public String getName() {
		return name;
	}

	public List<State> getStates() {
		return states;
	}

}
