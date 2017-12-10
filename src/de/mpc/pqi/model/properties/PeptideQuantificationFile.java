package de.mpc.pqi.model.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeptideQuantificationFile implements Serializable {
	private static final long serialVersionUID = 7556074706355349214L;

	private int proteinColumn = 0;
	private int numberOfStates = 0;
	private String[] stringColumnNames = new String[0];
	private int[] stringColumnIndizes = new int[0];
	private String[] numberColumnNames = new String[0];
	private int[] numberColumnIndizes = new int[0];
	private List<StateConfiguration> stateConfigurations = new ArrayList<>();
	
	public PeptideQuantificationFile() { 
	}

	public int getProteinColumn() {
		return proteinColumn;
	}

	public int getNumberOfStates() {
		return numberOfStates;
	}

	public List<StateConfiguration> getStateConfigurations() {
		return stateConfigurations;
	}

	public void setProteinColumn(int proteinColumn) {
		this.proteinColumn = proteinColumn;
	}

	public void setNumberOfStates(int numberOfStates) {
		this.numberOfStates = numberOfStates;
	}

	public void setStateConfigurations(List<StateConfiguration> stateConfigurations) {
		this.stateConfigurations = stateConfigurations;
	}

	public String[] getStringColumnNames() {
		return stringColumnNames;
	}

	public void setStringColumnNames(String[] stringColumnNames) {
		this.stringColumnNames = stringColumnNames;
	}

	public String[] getNumberColumnNames() {
		return numberColumnNames;
	}

	public void setNumberColumnNames(String[] numberColumnNames) {
		this.numberColumnNames = numberColumnNames;
	}

	public int[] getStringColumnIndizes() {
		return stringColumnIndizes;
	}

	public void setStringColumnIndizes(int[] stringColumnIndizes) {
		this.stringColumnIndizes = stringColumnIndizes;
	}

	public int[] getNumberColumnIndizes() {
		return numberColumnIndizes;
	}

	public void setNumberColumnIndizes(int[] numberColumnIndizes) {
		this.numberColumnIndizes = numberColumnIndizes;
	}
}
