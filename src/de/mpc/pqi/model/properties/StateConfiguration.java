package de.mpc.pqi.model.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StateConfiguration implements Serializable {
	private static final long serialVersionUID = -2398434245871239403L;

	private String name;
	private List<RunConfiguration> runs;
	
	public StateConfiguration(String name) {
		this.name = name;
		runs = new ArrayList<>();
	}

	public void addRun(RunConfiguration run) {
		runs.add(run);
	}
	
	public void removeRun(RunConfiguration run) {
		runs.remove(run);
	}
	
	public int getNumberOfRuns() {
		return runs.size();
	}
	
	public List<RunConfiguration> getRuns() {
		return runs;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
