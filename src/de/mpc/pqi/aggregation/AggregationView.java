package de.mpc.pqi.aggregation;

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.ProteinModel;
import de.mpc.pqi.view.GridBagHelper;

public class AggregationView extends JPanel {
	private static final long serialVersionUID = 2665924781251637075L;

	private AggregationModel model;

	private JLabel proteinNameLabel;
	
	private JLabel referenceLabel;
	private JComboBox<String> referenceComboBox;

	private AggregationTable table;

	public AggregationView() {
		this.model = new AggregationModel();
		initGui();
		initLayout();
		initControl();
	}

	private void initGui() {
		this.proteinNameLabel = new JLabel("Protein: ");
		this.referenceLabel = new JLabel("Reference: ");

		this.referenceComboBox = new JComboBox<String>();

		this.table = new AggregationTable();

	}

	private void initLayout() {
		GridBagHelper constraints = new GridBagHelper(new double[] { 0, 0.2 }, new double[] { 0, 0, 0.1});
		setLayout(new GridBagLayout());

		add(referenceLabel, constraints.getConstraints(0, 0));
		add(referenceComboBox, constraints.getConstraints(1, 0));

		add(proteinNameLabel, constraints.getConstraints(2, 0));
		add(table, constraints.getConstraints(0, 1, 4, 1));
	}
	
	private void initControl() {
		referenceComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (referenceComboBox.getSelectedItem() != null) {
						model.setSelectedState(referenceComboBox.getSelectedItem().toString());
						table.update(model.getTableModel());
					}
				}
			}
		});
	}

	public void setModel(ProteinModel proteinModel) {
		//TODO states are fixed in the names, thus keep them in combo box and keep the selection
		referenceComboBox.removeAllItems();
		for (State state : proteinModel.getPeptides().get(0).getStates()) {
			referenceComboBox.addItem(state.getName());
		}
		if (referenceComboBox.getItemCount() != 0) {
			referenceComboBox.setSelectedIndex(0);
			model.setSelectedState(referenceComboBox.getSelectedItem().toString());
		}
		
		model.setProteinModel(proteinModel);

		table.update(model.getTableModel());
		
		proteinNameLabel.setText("Protein: " + proteinModel.getName() + " " + proteinModel.getDescription());
	}
}
