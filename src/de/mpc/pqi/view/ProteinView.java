package de.mpc.pqi.view;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.mpc.pqi.aggregation.AggregationView;
import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.ProteinModel;
import de.mpc.pqi.view.protein.AbundanceValueType;
import de.mpc.pqi.view.protein.BoxPlot;
import de.mpc.pqi.view.protein.PeptideTable;
import de.mpc.pqi.view.protein.ProfileChart;
import de.mpc.pqi.view.protein.ProteinTree;
import de.mpc.pqi.view.protein.ProteinTree.ProteinTreeSelectionListener;
import de.mpc.pqi.view.protein.ProteinTreeModel;

public class ProteinView extends JPanel {
	private static final long serialVersionUID = -4267390339147137516L;
	private ProteinTree tree;
	private ProfileChart chart;
	private PeptideTable table;
	private BoxPlot boxPlot;
	private AggregationView aggregationView;


	private JLabel proteinNameLabel;
	private JLabel proteinNameValue;
	private JLabel proteinDescriptionLabel;
	private JLabel proteinDescriptionValue;
	
	private AbundanceValueType abundanceValueType = AbundanceValueType.ABUNDANCE;

	public ProteinView() {
		initGUI();
		initLayout();
		initControl();
	}

	private void initGUI() {
		tree = new ProteinTree();
		chart = new ProfileChart();
		table = new PeptideTable();
		boxPlot = new BoxPlot();
		aggregationView = new AggregationView();
		

		proteinNameLabel = new JLabel("Protein: ");
		proteinDescriptionLabel = new JLabel("Description: ");
		proteinNameValue = new JLabel("");
		proteinDescriptionValue = new JLabel("");
	}

	private void initLayout() {
		setLayout(new GridBagLayout());
		GridBagHelper constraints = new GridBagHelper(new double[] { 0.1, 0.1 }, new double[] { 0, 0.2, 0.1 });
		add(tree, constraints.getConstraints(0, 0));
		add(chart.createChart(null, abundanceValueType), constraints.getConstraints(1, 0));
		add(boxPlot, constraints.getConstraints(2, 0));

		JPanel proteinPane = new JPanel();
		proteinPane.setLayout(new GridBagLayout());
		GridBagHelper proteinConstraints = new GridBagHelper(new double[] { 0, 0, 0.1 }, new double[] { 0, 0.1 });
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Details", table.initTable(null, abundanceValueType));
		tabPane.add("Aggregation", aggregationView);
		proteinPane.add(proteinNameLabel, proteinConstraints.getConstraints(0, 0));
		proteinPane.add(proteinNameValue, proteinConstraints.getConstraints(1, 0));
		proteinPane.add(proteinDescriptionLabel, proteinConstraints.getConstraints(0, 1));
		proteinPane.add(proteinDescriptionValue, proteinConstraints.getConstraints(1, 1));
		proteinPane.add(tabPane, proteinConstraints.getConstraints(0, 2, 2, 1));
		
		add(proteinPane, constraints.getConstraints(0, 1, 3, 1));
	}

	private void initControl() {
		tree.addSelectionListener(new ProteinTreeSelectionListener() {
			@Override
			public void selectionChanged(Object selection) {
				if (selection instanceof PeptideModel) {
				//	PeptideModel peptideModel = (PeptideModel) selection;
				//	chart.updateChartData(peptideModel);
				} else if (selection instanceof ProteinModel) {
					ProteinModel proteinModel = (ProteinModel) selection;
					chart.updateChartData(proteinModel);
					table.setData(proteinModel, abundanceValueType);
					aggregationView.setModel(proteinModel);
					proteinNameValue.setText(proteinModel.getName());
					proteinDescriptionValue.setText(proteinModel.getDescription());
				}
			}
		});

		table.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					List<PeptideModel> peptideModels = new ArrayList<>();
					if (table.getSelectedRows().length == 0) {
						chart.updateChartData(table.getProteinModel());
					} else {
						for (int i : table.getSelectedRows()) {
							peptideModels.add(table.getValueAt(i));
						}
						if (!peptideModels.isEmpty()) {
							chart.updateChartData(peptideModels);
							boxPlot.setModel(peptideModels.get(0));
						} else {
							//TODO deselect charts
						}
					}
				}
			}
		});

		tree.addAbundanceValueButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equalsIgnoreCase("abundance")) {
					abundanceValueType = AbundanceValueType.ABUNDANCE;
				} else if (e.getActionCommand().equalsIgnoreCase("log10")) {
					abundanceValueType = AbundanceValueType.LOG10;
				} else if (e.getActionCommand().equalsIgnoreCase("arcsinh")) {
					abundanceValueType = AbundanceValueType.ARCSINH;
				}

				table.updateValueType(abundanceValueType);
				chart.updateValueType(abundanceValueType);
				boxPlot.setValueType(abundanceValueType);
			}
		});
	}

	public void setModel(List<ProteinModel> list) {
		tree.setModel(new ProteinTreeModel(list));
	}
}
