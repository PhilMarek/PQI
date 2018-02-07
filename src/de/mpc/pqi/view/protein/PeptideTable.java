package de.mpc.pqi.view.protein;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.quinto.swing.table.model.IModelFieldGroup;
import org.quinto.swing.table.model.ModelData;
import org.quinto.swing.table.model.ModelField;
import org.quinto.swing.table.model.ModelFieldGroup;
import org.quinto.swing.table.model.ModelRow;
import org.quinto.swing.table.view.JBroTable;
import org.quinto.swing.table.view.JBroTableModel;

import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.PeptideModel.State.Run;
import de.mpc.pqi.model.protein.ProteinModel;

public class PeptideTable {

	private JBroTable table;

	private ProteinModel proteinModel;

	private AbundanceValueType valueType;
	
	private boolean internalUpdate = false;

	public JScrollPane initTable(ProteinModel proteinModel, AbundanceValueType valueType) {
		this.table = new JBroTable();
		this.table.setModel(new JBroTableModel(null) {

			private static final long serialVersionUID = -8306397569789436774L;
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == getColumnCount() - 1) return Boolean.class;
				else return super.getColumnClass(columnIndex);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		});

		this.valueType = valueType;

		this.table.setBackground(Color.WHITE);

		if (proteinModel != null) {
			this.proteinModel = proteinModel;
			setData(proteinModel, this.valueType);
		}
		
		this.table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (!isSelected) {
					if (row % 2 == 0) {
						c.setBackground(Color.WHITE);
					} else {
						c.setBackground(Color.LIGHT_GRAY);
					}
				}
				return c;
			}

		});
		this.table.setDefaultRenderer(Boolean.class, new TableCellRenderer() {
			private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof Boolean) {
					JCheckBox cb = new JCheckBox();
					if (value != null) cb.setSelected((boolean)value);
					if (!isSelected) {
						if (row % 2 == 0) {
							cb.setBackground(Color.WHITE);
						} else {
							cb.setBackground(Color.LIGHT_GRAY);
						}
					} else {
						cb.setBackground(new Color(51, 153, 255));
					}
					return cb;
				} else {
					Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
							column);
					if (!isSelected) {
						if (row % 2 == 0) {
							c.setBackground(Color.WHITE);
						} else {
							c.setBackground(Color.LIGHT_GRAY);
						}
					}
					return c;
				}
				
				
			}
		});
		
		this.table.setAutoCreateRowSorter(true);
		this.table.getRowSorter().addRowSorterListener(e -> {
			// fireTableDataChanged lets the row sorter sort again, thus, internalUpdate prevents endless loops
			if (!internalUpdate) {
				internalUpdate = true;
				table.getModel().fireTableDataChanged();
				internalUpdate = false;
			}
		});
		
		return this.table.getScrollPane();
	}

	public void setData(ProteinModel proteinModel, AbundanceValueType valueType) {

		this.valueType = valueType;

		this.proteinModel = proteinModel;


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

		IModelFieldGroup groups[] = new IModelFieldGroup[] { peptidesField, abundacesField, uniqueField };

		ModelField fields[] = ModelFieldGroup.getBottomFields(groups);

		List<ModelRow> rows = new ArrayList<>();

		for (PeptideModel peptideModel : proteinModel.getPeptides()) {

			List<Double> values = getValuesOfPeptide(peptideModel);

			ModelRow row = new ModelRow(fields.length);
			row.setValue(0, peptideModel.getName());
			for (int i = 1; i < fields.length - 1; i++) {
				row.setValue(i, values.get(i - 1));
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
		int[] selectedRows = this.table.getSelectedRows();
		int[] selectedIndizes = new int[selectedRows.length];
		for (int i = 0 ; i < selectedRows.length ; i++) {
			selectedIndizes[i] = this.table.convertRowIndexToModel(selectedRows[i]);
		}
		return selectedIndizes;
	}

	public void updateValueType(AbundanceValueType abundanceValueType) {
		if (proteinModel != null) {
			setData(proteinModel, abundanceValueType);
		}
	}
}
