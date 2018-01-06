package de.mpc.pqi.aggregation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.ProteinModel;

public class AggregationTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635322485110185452L;

	private List<String> columns = new ArrayList<>();
	private List<List<Object>> data = new ArrayList<>();

	private ProteinModel proteinModel;

	public AggregationTableModel(ProteinModel model, String reference) {

		this.proteinModel = model;

		initColumns(model);

		for (PeptideModel peptideModel : model.getPeptides()) {
			List<Object> rowData = new ArrayList<>();

			rowData.add(peptideModel.getName());
			for (State state : peptideModel.getStates()) {
				rowData.add(Math.round(state.getMean()*100.0)/100.0);
				rowData.add(Math.round(peptideModel.getRatioOfMeans(state.getName(), reference) * 100.0) / 100.0);
			}
			rowData.add(peptideModel.isUnique());
			rowData.add(peptideModel.isSelected());

			data.add(rowData);
		}

	}

	private void initColumns(ProteinModel model) {
		columns.add("Peptides");
		PeptideModel peptideModel = model.getPeptides().get(0);
		for (State state : peptideModel.getStates()) {
			columns.add("Mean of " + state.getName());
			columns.add("Ratio of means");
		}
		columns.add("Unique");
		columns.add("Select");
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return data.get(row).get(column);
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		data.get(row).set(col, value);
	}

	@Override
	public String getColumnName(int col) {
		return columns.get(col);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == (data.get(0).size() - 1) || columnIndex == (data.get(0).size() - 2))
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void rowSelect(int rowIndex) {
		Boolean b = (Boolean) data.get(rowIndex).get(getColumnCount() - 1);

		if (b) {
			b = new Boolean(false);
			proteinModel.getPeptides().get(rowIndex).setSelected(b);
		} else {
			b = new Boolean(true);
			proteinModel.getPeptides().get(rowIndex).setSelected(b);
		}
		data.get(rowIndex).set(getColumnCount() - 1, b);
	}

	public ProteinModel getProteinModel() {
		return this.proteinModel;
	}
}
