package de.mpc.pqi.view.protein;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

import org.quinto.swing.table.model.IModelFieldGroup;
import org.quinto.swing.table.model.ModelData;
import org.quinto.swing.table.model.ModelField;
import org.quinto.swing.table.model.ModelFieldGroup;
import org.quinto.swing.table.model.ModelRow;
import org.quinto.swing.table.view.JBroTable;
import org.quinto.swing.table.view.JBroTableModel;

import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.ProteinModel;
import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.PeptideModel.State.Run;

public class PeptideTable {

	private JBroTable table;

	private ProteinModel proteinModel;

	private AbundanceValueType valueType;

	public JScrollPane initTable(ProteinModel proteinModel, AbundanceValueType valueType) {
		this.table = new JBroTable();
		this.table.setModel(new JBroTableModel(null) {

			private static final long serialVersionUID = -8306397569789436774L;
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 17) return Boolean.class;
				else return super.getColumnClass(columnIndex);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 17;
			}
		});

		this.valueType = valueType;

		this.table.setBackground(Color.WHITE);

		if (proteinModel != null) {
			this.proteinModel = proteinModel;
			setData(proteinModel, this.valueType);
		}
		return this.table.getScrollPane();
	}

	public void setData(ProteinModel proteinModel, AbundanceValueType valueType) {

		this.valueType = valueType;

		this.proteinModel = proteinModel;

		ModelField proteinField = new ModelField("protein", "Protein");

		ModelField peptidesField = new ModelField("peptides", "Peptides");

		ModelFieldGroup abundacesField = new ModelFieldGroup("abundances", valueType.getCaption());

		ModelField uniqueField = new ModelField("unique", "Unique");

		for (State state : proteinModel.getPeptides().get(0).getStates()) {
			ModelFieldGroup s = new ModelFieldGroup(UUID.randomUUID().toString(), state.getName());

			for (Run run : state.getRuns()) {

				ModelField r = new ModelField(UUID.randomUUID().toString(), run.getName());

				s.withChild(r);
			}
			abundacesField.withChild(s);
		}

		IModelFieldGroup groups[] = new IModelFieldGroup[] { proteinField, peptidesField, abundacesField, uniqueField };

		ModelField fields[] = ModelFieldGroup.getBottomFields(groups);

		List<ModelRow> rows = new ArrayList<>();

		ModelRow proteinIdentifierRow = new ModelRow(1);
		proteinIdentifierRow.setValue(0, proteinModel.getName());

		rows.add(proteinIdentifierRow);

		for (PeptideModel peptideModel : proteinModel.getPeptides()) {

			List<Double> values = getValuesOfPeptide(peptideModel);

			ModelRow row = new ModelRow(fields.length);
			for (int i = 1; i < fields.length - 1; i++) {
				if (i == 1) {
					row.setValue(i, peptideModel.getName());
				} else {
					row.setValue(i, values.get(i - 2));
				}
			}

			row.setValue(fields.length - 1, peptideModel.isUnique());

			rows.add(row);
		}

		ModelData data = new ModelData(groups);

		ModelRow[] rowsArray = new ModelRow[rows.size()];
		rowsArray = rows.toArray(rowsArray);

		data.setRows(rowsArray);

		this.table.setData(data);

	}

	private List<Double> getValuesOfPeptide(PeptideModel peptideModel) {
		List<Double> values = new ArrayList<>();

		for (State state : peptideModel.getStates()) {
			for (Run run : state.getRuns()) {
				values.add(run.getAbundance());
			}
		}

		return valueType.transformValues(values);
	}

	public void addListSelectionListener(ListSelectionListener listener) {
		this.table.getSelectionModel().addListSelectionListener(listener);
	}

	public PeptideModel getValueAt(int index) {
		return proteinModel.getPeptides().get(index);
	}

	public ProteinModel getProteinModel() {
		return proteinModel;
	}

	public int[] getSelectedRows() {
		return this.table.getSelectedRows();
	}

	public void updateValueType(AbundanceValueType abundanceValueType) {
		if (proteinModel != null) {
			setData(proteinModel, abundanceValueType);
		}
	}
}
