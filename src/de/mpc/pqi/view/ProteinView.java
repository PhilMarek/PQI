package de.mpc.pqi.view;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.ProteinModel;
import de.mpc.pqi.view.diagram.PQICategoryChart;
import de.mpc.pqi.view.table.Table;
import de.mpc.pqi.view.tree.ProteinTree;
import de.mpc.pqi.view.tree.ProteinTree.ProteinTreeSelectionListener;
import de.mpc.pqi.view.tree.ProteinTreeModel;

public class ProteinView extends JPanel {
	private static final long serialVersionUID = -4267390339147137516L;
	private ProteinTree tree;
	private PQICategoryChart chart;
	private Table table;

	public ProteinView() {
		initGUI();
		initLayout();
		initControl();
	}

	private void initGUI() {
		tree = new ProteinTree();
		chart = new PQICategoryChart();
		table = new Table();
	}

	private void initLayout() {
//		setLayout(new BorderLayout());
//		
//		add(tree, BorderLayout.WEST);
//		add(chart.createChart(null), BorderLayout.CENTER);
//		add(table.initTable(null), BorderLayout.SOUTH);
		
		setLayout(new GridBagLayout());
		GridBagHelper constraints = new GridBagHelper(new double[] {0.1, 0.1}, new double[] {0.1, 0.4});
		add(tree, constraints.getConstraints(0, 0));
		add(chart.createChart(null), constraints.getConstraints(1, 0));
		add(table.initTable(null), constraints.getConstraints(0, 1, 2, 1));
	}

	private void initControl() {
		tree.addSelectionListener(new ProteinTreeSelectionListener() {
			@Override
			public void selectionChanged(Object selection) {
				if (selection instanceof PeptideModel) {
					PeptideModel peptideModel = (PeptideModel) selection;
					chart.updateChart(peptideModel);
					table.updateTable(peptideModel);
				} else if (selection instanceof ProteinModel) {
					ProteinModel proteinModel = (ProteinModel) selection;
					chart.updateChart(proteinModel);
				}
			}
		});
	}

	public void setModel(List<ProteinModel> list) {
		tree.setModel(new ProteinTreeModel(list));
	}
}
