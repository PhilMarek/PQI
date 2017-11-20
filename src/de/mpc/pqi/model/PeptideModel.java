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
		public static class Run {
			private String name;
			private long abundance;
			
			public String getName() {
				return name;
			}

			public long getAbundance() {
				return abundance;
			}

			public Run(String name, long abundance) {
				this.name = name;
				this.abundance = abundance;
			}
		}
		
		private List<Run> runs;
		private String name;
		
		public State(String name, List<Run> runs) {
			this.name = name;
			this.runs = runs;
		}
		
		public List<Run> getRuns() {
			return runs;
		}
		
		public String getName() {
			return name;
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
