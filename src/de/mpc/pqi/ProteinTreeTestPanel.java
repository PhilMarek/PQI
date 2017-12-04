package de.mpc.pqi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.PeptideModel.State;
import de.mpc.pqi.model.PeptideModel.State.Run;
import de.mpc.pqi.model.ProteinModel;
import de.mpc.pqi.model.properties.CSVFile;
import de.mpc.pqi.model.properties.PeptideQuantificationFile;
import de.mpc.pqi.model.properties.RunConfiguration;
import de.mpc.pqi.model.properties.StateConfiguration;
import de.mpc.pqi.view.ProteinView;
import de.mpc.pqi.view.properties.PropertyDialog;
import de.mpc.pqi.view.diagram.PQICategoryChart;
import de.mpc.pqi.view.table.Table;
import de.mpc.pqi.view.tree.ProteinTree;
import de.mpc.pqi.view.tree.ProteinTree.ProteinTreeSelectionListener;
import de.mpc.pqi.view.tree.ProteinTreeModel;

public class ProteinTreeTestPanel extends JPanel {
	private static final long serialVersionUID = -2631147602208704811L;

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Missing program argument: Filepath quants_peptides.csv");
		} else {
			new ProteinTreeTestPanel(args[0]);
			JFrame frame = new JFrame("");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(new ProteinTreeTestPanel(readData(args[0], "#", "\"", "\t", true)),
					BorderLayout.WEST);
			// frame.getContentPane().add(new CSVFilePropertyPanel(),
			// BorderLayout.CENTER);
			// frame.getContentPane().add(new DataPropertyPanel(),
			// BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
		}
	}

	private JFrame frame;
	private ProteinView proteinView;
	
	private ProteinTreeTestPanel(String filePath) {
		frame = new JFrame("");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		proteinView = new ProteinView(null);
		frame.getContentPane().add(proteinView);
		frame.setJMenuBar(getMenuBar());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem configure = new JMenuItem("Configure");
		
	public ProteinTreeTestPanel(Object[][] objects) {
		setLayout(new VerticalLayout());
		ProteinTree tree = new ProteinTree(new ProteinTreeModel(parseData(objects)));
		PQICategoryChart chart = new PQICategoryChart();
		Table table = new Table();

		add(tree);
		add(chart.createChart(null));

		configure.addActionListener(l -> {
			PropertyDialog dialog = new PropertyDialog();
			dialog.pack();
			dialog.setVisible(true);
			while (dialog.isVisible());
			PeptideQuantificationFile pqf = dialog.getPeptideQuantificationFile();
			CSVFile csvFile = dialog.getCSVFile();

			if (pqf != null && csvFile != null) {
				try {
					String[][] data = csvFile.readData();
					proteinView.setModel(parseData(data, pqf));
				} catch (IOException e) {
					e.printStackTrace();
		tree.addSelectionListener(new ProteinTreeSelectionListener() {
			@Override
			public void selectionChanged(Object selection) {
				if (selection instanceof PeptideModel) {
					PeptideModel peptideModel = (PeptideModel) selection;
					chart.updateChart(peptideModel);

					if (table.getTable() == null) {
						add(table.initTable(peptideModel));
					} else {
						table.updateTable(peptideModel);
					}
				}
			}
		});
		
		file.add(configure);
		menuBar.add(file);
		return menuBar;

	}

	private static List<ProteinModel> parseData(String[][] objects, PeptideQuantificationFile pqf) {
		List<ProteinModel> proteins = new ArrayList<>();
    	Map<String, ProteinModel> proteinMap = new HashMap<>();

    	for (String[] peptideData : objects) {
    		String proteinName = peptideData[pqf.getProteinColumn() + 1].toString();
			ProteinModel protein = proteinMap.get(proteinName);
		Map<String, ProteinModel> proteinMap = new HashMap<>();
		for (int i = 0; i < objects.length; i++) {
			Object[] peptideData = objects[i];
			ProteinModel protein = proteinMap.get((String) peptideData[1]);
			if (protein == null) {
				protein = new ProteinModel("Protein"); //TODO proteinName);
				proteins.add(protein);
				proteinMap.put(proteinName, protein);
				proteinMap.put((String) peptideData[1], protein);
			}
			List<State> states = new ArrayList<>();
			for (StateConfiguration stateConfig : pqf.getStateConfigurations()) {
				List<Run> runs = new ArrayList<>();
				for (RunConfiguration runConfig : stateConfig.getRuns()) {
					double abundance = Double.parseDouble(peptideData[runConfig.getColumn() + 1].toString());
					runs.add(new Run(runConfig.getName(), abundance));
				}
				states.add(new State(stateConfig.getName(), runs, stateConfig.isControlGroup()));
			}
			protein.addPeptide(new PeptideModel(peptideData[0].toString(), states));
		}
		return proteins;
	}
}
