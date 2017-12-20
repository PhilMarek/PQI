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

	/**
	 * 
	 */
	private static final long serialVersionUID = 2665924781251637075L;

	private AggregationModel model;

	private JLabel proteinNameLabel;
	private JLabel referenceLabel;

	private JComboBox<String> firstComboBox;

	private AggregationTabel table;

	private GridBagHelper constraints = new GridBagHelper(new double[] { 0, 0.2 }, new double[] { 0, 0, 0.1});

	private GridBagLayout layout;

	public AggregationView() {
		this.model = new AggregationModel();
		initGui();
		initLayout();
		initControle();
	}

	private void initGui() {
		this.proteinNameLabel = new JLabel("Protein: ");
		this.referenceLabel = new JLabel("Reference: ");

		this.firstComboBox = new JComboBox<String>();

		this.table = new AggregationTabel();

	}

	private void initLayout() {

		this.layout = new GridBagLayout();
		setLayout(this.layout);

		add(referenceLabel, constraints.getConstraints(0, 0));
		add(firstComboBox, constraints.getConstraints(1, 0));

		add(proteinNameLabel, constraints.getConstraints(2, 0));
		add(table, constraints.getConstraints(0, 1, 3, 1));
	}

	public void update(ProteinModel proteinModel) {

		firstComboBox.removeAllItems();

		for (State state : proteinModel.getPeptides().get(0).getStates()) {
			firstComboBox.addItem(state.getName());
		}
		firstComboBox.setSelectedIndex(0);

		this.model.setSelectedState(firstComboBox.getSelectedItem().toString());

		this.model.setProteinModel(proteinModel);

		this.table.update(model.getTableModel());
		this.proteinNameLabel.setText("Protein: " + proteinModel.getName());

	}

	private void initControle() {

		firstComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {

					if (firstComboBox.getSelectedItem() != null) {
						model.setSelectedState(firstComboBox.getSelectedItem().toString());
						table.update(model.getTableModel());
					}
				}

			}
		});
	}
}
