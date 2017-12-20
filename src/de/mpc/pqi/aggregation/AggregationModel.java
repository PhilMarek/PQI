package de.mpc.pqi.aggregation;

import de.mpc.pqi.model.protein.ProteinModel;

public class AggregationModel {

	private ProteinModel proteinModel;
	private String state;
	private AggregationTableModel tableModel;

	public void setProteinModel(ProteinModel proteinModel) {
		this.proteinModel = proteinModel;
		this.tableModel = new AggregationTableModel(proteinModel, state);
	}

	public void setSelectedState(String value) {
		this.state = value;

		if (this.proteinModel != null) {
			this.tableModel = new AggregationTableModel(proteinModel, value);
		}
	}

	public AggregationTableModel getTableModel() {
		return this.tableModel;
	}

}
