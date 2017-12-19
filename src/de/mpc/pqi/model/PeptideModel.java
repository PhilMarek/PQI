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
			private double abundance;
			
			public Run(String name, double abundance) {
				this.name = name;
				this.abundance = abundance;
			}
			
			public String getName() {
				return name;
			}

			public double getAbundance() {
				return abundance;
			}
		}
		
		private String name;
		private List<Run> runs;
		
		public State(String name, List<Run> runs) {
			this.name = name;
			this.runs = runs;
		}
		
		public String getName() {
			return name;
		}
		
		public List<Run> getRuns() {
			return runs;
		}
	}
	
	private String name;
	/** The boolean indicating if this protein is used for quantification **/
	private boolean usedForQuantification;
	private boolean activated = true;
	private List<State> states;
	private boolean unique;
	
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
	
	public boolean isUsedForQuantification() {
		return usedForQuantification;
	}

	public void setUsedForQuantification(boolean usedForQuantification) {
		this.usedForQuantification = usedForQuantification;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
}
