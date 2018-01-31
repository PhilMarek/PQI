package de.mpc.pqi.model.protein;

import java.util.List;

/**
 * Specialization of PQIModel. Store information for peptides, e.g. the peptide
 * name, abundances. Furthermore, a peptide can be deactivated for output.
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

		public Double getMean() {

			Double sum = 0.0;

			for (Run run : getRuns()) {
				sum += run.getAbundance();
			}

			return sum / getRuns().size();
		}
	}

	private String name;
	/** The boolean indicating if this protein is used for quantification **/
	private boolean usedForQuantification;
	private boolean activated = true;
	private List<State> states;
	private boolean unique;
	private boolean selected = true;

	public PeptideModel(String name, List<State> states) {
		this.name = name;
		this.states = states;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Double getRatioOfMeans(String firstState, String secondState) {
		State fState = null;
		State sState = null;
		for (State s : getStates()) {
			if (s.getName().equalsIgnoreCase(firstState)) {
				fState = s;
			}
			if (s.getName().equalsIgnoreCase(secondState)) {
				sState = s;
			}
		}
		if (fState != null && sState != null) {
			return calculateRatio(fState.getMean(), sState.getMean());
		}

		return 0.0;
	}

	private Double calculateRatio(Double a, Double b) {
		if (a != 0.0 && b != 0.0) {
			if (a == b) {
				return 1.0;
			} else {
				return Math.round((a / b) * 100.0) / 100.0;
			}
		}
		return 0.0;
	}
}
