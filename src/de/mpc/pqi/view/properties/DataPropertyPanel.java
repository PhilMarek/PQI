package de.mpc.pqi.view.properties;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import de.mpc.pqi.model.properties.PeptideQuantificationFileConfiguration;
import de.mpc.pqi.model.properties.RunConfiguration;
import de.mpc.pqi.model.properties.StateConfiguration;
import de.mpc.pqi.view.GridBagHelper;
import de.mpc.pqi.view.properties.RunAllocationView.RunAllocationListener;

public class DataPropertyPanel extends JPanel {
	public static class StateTableModel extends DefaultTableModel {
		private static final long serialVersionUID = -4439201681251553047L;

		public StateTableModel(Object[][] objects, String[] strings) {
			super(objects, strings);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			StateConfiguration stateConfig = (StateConfiguration) getValueAt(row, column);
			stateConfig.setName((String) aValue);
		}
	}
	
	private static final long serialVersionUID = 464625449624189774L;
	
	private JLabel proteinColumnLabel;
	private JComboBox<String> proteinColumnComboBox;
	
	private JLabel numberOfStatesLabel;
	private JTextField numberOfStatesTextField;
	
	private JTable stateTable;
	private StateTableModel stateModel;
	
	private RunAllocationView runAllocationView;
	
	private Map<Integer, String> stringColumns;
	private Map<Integer, String> numberColumns;
	private Map<Integer, String> unusedNumberColumns;
	
	
	public DataPropertyPanel() {
		initGUI();
		initLayout();
		initControl();
	}

	private void initGUI() {
		proteinColumnLabel = new JLabel("Column for proteins");
		proteinColumnComboBox = new JComboBox<>();
		
		numberOfStatesLabel = new JLabel("Number of States");
		numberOfStatesTextField = new JTextField();

		stateTable = new JTable();
		stateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stateModel = new StateTableModel(new Object[][]{ }, new String[]{ "States" });
		stateTable.setModel(stateModel);
		
		runAllocationView = new RunAllocationView();
	}

	private void initLayout() {
		setLayout(new GridBagLayout());

		GridBagHelper constraints = new GridBagHelper(new double[]{0, 0, 0, 0.1}, new double[]{0, 0.1, 0.1});

		add(proteinColumnLabel, constraints.getConstraints(0, 0));
		add(proteinColumnComboBox, constraints.getConstraints(1, 0));

		add(numberOfStatesLabel, constraints.getConstraints(0, 1));
		add(numberOfStatesTextField, constraints.getConstraints(1, 1));

		add(new JScrollPane(stateTable), constraints.getConstraints(0, 2, 2, 1));
		
		add(runAllocationView, constraints.getConstraints(2, 2));
	}

	private void initControl() {
		stateTable.getSelectionModel().addListSelectionListener(e -> {
			int selectedRow = stateTable.getSelectedRow();
			if (selectedRow < 0) {
				runAllocationView.setUnusedColumns(new ArrayList<>());
				runAllocationView.setAssignedColumns(new ArrayList<>());
			} else {
				StateConfiguration stateConfig = (StateConfiguration) stateModel.getValueAt(selectedRow, 0);
				List<String> selectedColumns = new ArrayList<>();
				stateConfig.getRuns().forEach(run -> selectedColumns.add(numberColumns.get(run.getColumn())));
				runAllocationView.setAssignedColumns(selectedColumns);
				runAllocationView.setUnusedColumns(unusedNumberColumns.values());
			}
		});
		
		runAllocationView.addListener(new RunAllocationListener() {
			@Override
			public void onRemove(List<String> elements) {
				int selectedRow = stateTable.getSelectedRow();
				if (selectedRow >= 0) {
					StateConfiguration stateConfig = (StateConfiguration) stateModel.getValueAt(selectedRow, 0);
					Set<Integer> indices = new HashSet<>();
					elements.forEach(element -> numberColumns.forEach((index, string) -> {
						if (element.equals(string)) {
							indices.add(index);
							unusedNumberColumns.put(index, numberColumns.get(index));
						}
					}));
					List<RunConfiguration> toRemove = new ArrayList<>();
					stateConfig.getRuns().forEach(run -> {
						if (indices.contains(run.getColumn())) toRemove.add(run);
					});
					toRemove.forEach(run -> stateConfig.removeRun(run));
				}
			}

			@Override
			public void onAdd(List<String> elements) {
				int selectedRow = stateTable.getSelectedRow();
				if (selectedRow >= 0) {
					StateConfiguration stateConfig = (StateConfiguration) stateModel.getValueAt(selectedRow, 0);
					elements.forEach(element -> numberColumns.forEach((index, string) -> {
						if (element.equals(string)) {
							unusedNumberColumns.remove(index);
							RunConfiguration runConfiguration = new RunConfiguration(string);
							runConfiguration.setColumn(index);
							stateConfig.addRun(runConfiguration);
						}
					}));
				}
			}
			
		});
		
		numberOfStatesTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {	onChange(); }
			@Override
			public void insertUpdate(DocumentEvent arg0) { onChange(); }
			@Override
			public void removeUpdate(DocumentEvent arg0) { onChange(); }
			
			private void onChange() {
				if (isValidNumberInput(numberOfStatesTextField.getText())) {
					int numberOfStates = Integer.parseInt(numberOfStatesTextField.getText());
					int currentNumberOfStates = stateModel.getRowCount();
					if (numberOfStates < currentNumberOfStates) {
						for (int stateIndex = currentNumberOfStates - 1 ; stateIndex >= numberOfStates ; stateIndex--) {
							StateConfiguration stateConfig = (StateConfiguration) stateModel.getValueAt(stateIndex, 0);
							stateConfig.getRuns().forEach(run -> {
								numberColumns.forEach((index, string) -> {
									if (run.getName().equals(string)) {
										unusedNumberColumns.put(index, numberColumns.get(index));
									}
								});
							});
							stateModel.removeRow(stateIndex);
						}
					} else {
						for (int stateIndex = currentNumberOfStates ; stateIndex < numberOfStates ; stateIndex++) {
							StateConfiguration stateConfig = new StateConfiguration((stateIndex + 1) + "");
							stateModel.addRow(new Object[]{stateConfig});
						}
					}
					stateTable.clearSelection();
				} else {
					numberOfStatesTextField.setText("");
				}
			}
		});
		
	}

	private boolean isValidNumberInput(String text) {
		return text.matches("^\\d+$");
	}

	public int getProteinColumn() {
		return proteinColumnComboBox.getSelectedIndex();
	}

	public void initSettings(PeptideQuantificationFileConfiguration pqf) {
		stringColumns = new HashMap<>();
		for (int i = 0 ; i < pqf.getStringColumnIndizes().length ; i++) {
			stringColumns.put(pqf.getStringColumnIndizes()[i], pqf.getStringColumnNames()[i]);
		}
		numberColumns = new HashMap<>();
		unusedNumberColumns = new HashMap<>();
		for (int i = 0 ; i < pqf.getNumberColumnIndizes().length ; i++) {
			numberColumns.put(pqf.getNumberColumnIndizes()[i], pqf.getNumberColumnNames()[i]);
			unusedNumberColumns.put(pqf.getNumberColumnIndizes()[i], pqf.getNumberColumnNames()[i]);
		}
		
		proteinColumnComboBox.setModel(new DefaultComboBoxModel<String>(stringColumns.values().toArray(new String[0])));
		proteinColumnComboBox.setSelectedItem(stringColumns.get(pqf.getProteinColumn()));
		
		numberOfStatesTextField.setText(pqf.getNumberOfStates() + "");
		
		stateModel.setRowCount(0);
		for (StateConfiguration stateConfiguration : pqf.getStateConfigurations()) {
			stateModel.addRow(new Object[]{stateConfiguration});
			stateConfiguration.getRuns().forEach(run -> unusedNumberColumns.remove(run.getColumn()));
		}
	}

	public PeptideQuantificationFileConfiguration getSettings() {
		PeptideQuantificationFileConfiguration pqf = new PeptideQuantificationFileConfiguration();
		pqf.setStringColumnIndizes(stringColumns.keySet().stream().mapToInt(i -> i).toArray());
		pqf.setStringColumnNames(stringColumns.values().toArray(new String[0]));
		pqf.setNumberColumnIndizes(numberColumns.keySet().stream().mapToInt(i -> i).toArray());
		pqf.setNumberColumnNames(numberColumns.values().toArray(new String[0]));
		pqf.setNumberOfStates(Integer.parseInt(numberOfStatesTextField.getText()));
		stringColumns.forEach((number, string) -> {
			if (string.equals(proteinColumnComboBox.getSelectedItem())) pqf.setProteinColumn(number);
		});
		
		List<StateConfiguration> stateConfigurations = new ArrayList<>();
		for (int row = 0 ; row < stateModel.getRowCount() ; row++) {
			stateConfigurations.add((StateConfiguration) stateModel.getValueAt(row, 0));
		}
		pqf.setStateConfigurations(stateConfigurations);
		return pqf;
	}
}
