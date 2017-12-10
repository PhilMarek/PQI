package de.mpc.pqi.view.table;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JScrollPane;

import org.quinto.swing.table.model.IModelFieldGroup;
import org.quinto.swing.table.model.ModelData;
import org.quinto.swing.table.model.ModelField;
import org.quinto.swing.table.model.ModelFieldGroup;
import org.quinto.swing.table.model.ModelRow;
import org.quinto.swing.table.view.JBroTable;

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.PeptideModel.State;
import de.mpc.pqi.model.PeptideModel.State.Run;
import de.mpc.pqi.model.ProteinModel;

public class Table {

	private JBroTable table;
	private JScrollPane pane;

	public JScrollPane initTable(ProteinModel proteinModel) {
		this.table = new JBroTable();

		this.table.setBackground(Color.WHITE);

		this.pane = this.table.getScrollPane();

		if (proteinModel != null) {
			setData(proteinModel);
		}
		return this.pane;
	}

	public void setData(ProteinModel proteinModel) {

		ModelField proteinField = new ModelField("protein", "Protein");

		ModelField peptidesField = new ModelField("peptides", "Peptides");

		ModelFieldGroup abundacesField = new ModelFieldGroup("abundances", "Abundance");

		for (State state : proteinModel.getPeptides().get(0).getStates()) {
			ModelFieldGroup s = new ModelFieldGroup(UUID.randomUUID().toString(), state.getName());

			for (Run run : state.getRuns()) {

				ModelField r = new ModelField(UUID.randomUUID().toString(), run.getName());

				s.withChild(r);
			}
			abundacesField.withChild(s);
		}

		IModelFieldGroup groups[] = new IModelFieldGroup[] { proteinField, peptidesField, abundacesField };

		ModelField fields[] = ModelFieldGroup.getBottomFields(groups);

		List<ModelRow> rows = new ArrayList<>();

		ModelRow proteinIdentifierRow = new ModelRow(1);
		proteinIdentifierRow.setValue(0, proteinModel.getName());

		rows.add(proteinIdentifierRow);

		for (PeptideModel peptideModel : proteinModel.getPeptides()) {

			List<Double> values = getValuesOfPeptide(peptideModel);

			ModelRow row = new ModelRow(fields.length);

			for (int i = 1; i < fields.length; i++) {
				if (i == 1) {
					row.setValue(i, peptideModel.getName());
				} else {
					row.setValue(i, values.get(i - 2));
				}
			}
			rows.add(row);
		}

		ModelData data = new ModelData(groups);

		ModelRow[] rowsArray = new ModelRow[rows.size()];
		rowsArray = rows.toArray(rowsArray);

		data.setRows(rowsArray);

		this.table.setData(data);

	}

	// private void initData(PeptideModel peptideModel) {
	// List<Double> values = new ArrayList<>();
	//
	// ModelField peptide = new ModelField(peptideModel.getName(),
	// peptideModel.getName());
	//
	// ModelFieldGroup abundance = new ModelFieldGroup("Abundance",
	// "Abundance");
	//
	// for (State state : peptideModel.getStates()) {
	//
	// ModelFieldGroup s = new ModelFieldGroup(UUID.randomUUID().toString(),
	// state.getName());
	//
	// for (Run run : state.getRuns()) {
	//
	// values.add(run.getAbundance());
	//
	// ModelField r = new ModelField(UUID.randomUUID().toString(),
	// run.getName());
	//
	// s.withChild(r);
	// }
	// abundance.withChild(s);
	// }
	//
	// IModelFieldGroup groups[] = new IModelFieldGroup[] { peptide, abundance
	// };
	//
	// ModelField fields[] = ModelFieldGroup.getBottomFields(groups);
	//
	// ModelRow rows[] = new ModelRow[1];
	//
	// rows[0] = new ModelRow(fields.length);
	//
	// for (int i = 0; i < fields.length; i++) {
	// if (i == 0) {
	// rows[0].setValue(i, peptideModel.getName());
	// } else {
	// rows[0].setValue(i, values.get(i - 1));
	// }
	// }
	//
	// ModelData data = new ModelData(groups);
	// data.setRows(rows);
	//
	// this.table.setData(data);
	// }
	//
	// public void updateTable(PeptideModel peptideModel) {
	//
	// List<Double> values = new ArrayList<>();
	//
	// for (State state : peptideModel.getStates()) {
	// for (Run run : state.getRuns()) {
	// values.add(run.getAbundance());
	// }
	// }
	//
	// if (this.table.getData() == null)
	// initData(peptideModel);
	// else {
	//
	// ModelRow[] r = this.table.getData().getRows();
	//
	// List<ModelRow> rowList = new ArrayList<ModelRow>(Arrays.asList(r));
	//
	// ModelRow row = new ModelRow(this.table.getData().getFieldsCount());
	//
	// for (int i = 0; i < this.table.getData().getFieldsCount(); i++) {
	// if (i == 0) {
	// row.setValue(i, peptideModel.getName());
	// } else {
	// row.setValue(i, values.get(i - 1));
	// }
	// }
	//
	// rowList.add(row);
	//
	// ModelRow[] rows = new ModelRow[rowList.size()];
	// rows = rowList.toArray(rows);
	//
	// this.table.getData().setRows(rows);
	// }
	// this.table.revalidate();
	// }

	private List<Double> getValuesOfPeptide(PeptideModel peptideModel) {
		List<Double> values = new ArrayList<>();

		for (State state : peptideModel.getStates()) {
			for (Run run : state.getRuns()) {
				values.add(run.getAbundance());
			}
		}

		return values;
	}
}
