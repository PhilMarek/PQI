package de.mpc.pqi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.PeptideModel.State;
import de.mpc.pqi.model.PeptideModel.State.Run;
import de.mpc.pqi.model.ProteinModel;
import de.mpc.pqi.view.diagram.ChartDataWrapper;
import de.mpc.pqi.view.diagram.ColumnValuePair;
import de.mpc.pqi.view.diagram.PQICategoryChart;
import de.mpc.pqi.view.tree.ProteinTree;
import de.mpc.pqi.view.tree.ProteinTree.ProteinTreeSelectionListener;
import de.mpc.pqi.view.tree.ProteinTreeModel;

public class ProteinTreeTestPanel extends JPanel {
	private static final long serialVersionUID = -2631147602208704811L;

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Missing program argument: Filepath quants_peptides.csv");
		} else {
			JFrame frame = new JFrame("");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(new ProteinTreeTestPanel(readData(args[0], "#", "\"", "\t", true)),
					BorderLayout.WEST);
			// frame.getContentPane().add(new CSVFilePropertyPanel(), BorderLayout.CENTER);
			//frame.getContentPane().add(new DataPropertyPanel(), BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
		}
	}

	public ProteinTreeTestPanel(Object[][] objects) {
		setLayout(new FlowLayout());
		ProteinTree tree = new ProteinTree(new ProteinTreeModel(parseData(objects)));
		PQICategoryChart chart = new PQICategoryChart();
		add(tree);
		add(chart.createChart(new ArrayList<>()));
		
		tree.addSelectionListener(new ProteinTreeSelectionListener() {
			@Override
			public void selectionChanged(Object selection) {
				PeptideModel peptideModel = (PeptideModel) selection;
				List<ChartDataWrapper> list = new ArrayList<ChartDataWrapper>();
				ChartDataWrapper data = new ChartDataWrapper(peptideModel.getName());
				for (int i = 0; i < peptideModel.getStates().size(); i++) {
					State state = peptideModel.getStates().get(i);
					for (int j = 0; j < state.getRuns().size(); j++) {
						Run run = state.getRuns().get(j);
						Long value = run.getAbundance();
						String column = (i + 1) + " R" + (j + 1);
						data.addColumnValuePair(new ColumnValuePair(column, value.doubleValue()));
					}
				}
				list.add(data);
				chart.updateChart(list);
			}
		});
	}

	private List<ProteinModel> parseData(Object[][] objects) {
		List<ProteinModel> proteins = new ArrayList<>();
		Map<String, ProteinModel> proteinMap = new HashMap<>();
		for (int i = 0; i < objects.length; i++) {
			Object[] peptideData = objects[i];
			ProteinModel protein = proteinMap.get((String)peptideData[1]);
			if (protein == null) {
				protein = new ProteinModel((String) peptideData[1]);
				proteins.add(protein);
				proteinMap.put((String)peptideData[1], protein);
			}
			List<State> states = new ArrayList<>();
			for (int k = 0; k < 5; k++) {
				List<Run> runs = new ArrayList<>();
				for (int l = 0; l < 3; l++) {
					runs.add(new Run("Run" + l, Long.parseLong((String) peptideData[3 + k * 3 + l])));
				}
				states.add(new State("State" + k, runs));
			}
			PeptideModel peptide = new PeptideModel(peptideData[0].toString(), states);
			protein.addPeptide(peptide);
		}
		return proteins;
	}

	private static String[][] readData(String file, String comment, String quote, String colDelimiter,
			boolean hasHeader) throws IOException {
		List<String[]> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			stream.forEach(line -> {
				if (!line.startsWith(comment) && !line.trim().equals("")) {
					String[] split = line.split(colDelimiter);
					for (int i = 0; i < split.length; i++) {
						while (split[i].startsWith(quote))
							split[i] = split[i].substring(1);
						while (split[i].endsWith(quote))
							split[i] = split[i].substring(0, split[i].length() - 1);
					}
					lines.add(split);
				}
			});
		}
		if (hasHeader)
			lines.remove(0);
		return lines.toArray(new String[0][]);
	}

}
