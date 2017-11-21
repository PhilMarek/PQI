package de.mpc.pqi.view.diagram;

import java.util.ArrayList;
import java.util.List;

public class BoxplotData {
	private String category;

	private List<Double> controleValues;
	private List<Double> diseaseValues;

	public BoxplotData(String category) {
		this.category = category;
		this.diseaseValues = new ArrayList<Double>();
		this.controleValues = new ArrayList<Double>();
	}

	public String getCategroy() {
		return this.category;
	}

	public void addDiseaseValue(Double value) {
		this.diseaseValues.add(value);
	}

	public void addControleValue(Double value) {
		this.controleValues.add(value);
	}

	public List<Double> getControleValues() {
		return this.controleValues;
	}

	public List<Double> getDiseaseValues() {
		return this.diseaseValues;
	}

}
