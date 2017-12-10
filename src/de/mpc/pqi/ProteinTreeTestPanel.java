package de.mpc.pqi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class ProteinTreeTestPanel extends JPanel {
	private static final long serialVersionUID = -2631147602208704811L;

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Missing program argument: Filepath quants_peptides.csv");
		} else {
			new ProteinTreeTestPanel(args[0]);
		}
	}

	private JFrame frame;
	private ProteinView proteinView;
	
	private ProteinTreeTestPanel(String filePath) {
		frame = new JFrame("");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		proteinView = new ProteinView();
		frame.getContentPane().add(proteinView);
		frame.setJMenuBar(getMenuBar());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem configure = new JMenuItem("Configure");
		
		configure.addActionListener(l -> {
			PeptideQuantificationFile pqf = null;
			CSVFile csvFile = null;
			pqf = (PeptideQuantificationFile) readObject("pqf");
			csvFile = (CSVFile) readObject("csv");


			PropertyDialog dialog = new PropertyDialog();
			if (pqf != null) dialog.setPeptideQuantificationFile(pqf);
			if (csvFile != null) dialog.setCSVFile(csvFile);
			
			dialog.pack();
			dialog.setVisible(true);
			while (dialog.isVisible());
			pqf = dialog.getPeptideQuantificationFile();
			csvFile = dialog.getCSVFile();

			if (pqf != null && csvFile != null) {
				try {
					String[][] data = csvFile.readData();
					proteinView.setModel(parseData(data, pqf));
					writeObject(csvFile, "csv");
					writeObject(pqf, "pqf");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		file.add(configure);
		menuBar.add(file);
		return menuBar;
	}
	
	private void writeObject(Object object, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object readObject(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object object = ois.readObject();
			ois.close();
			fis.close();
			return object;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		} 
		
	}

	private static List<ProteinModel> parseData(String[][] objects, PeptideQuantificationFile pqf) {
		List<ProteinModel> proteins = new ArrayList<>();
    	Map<String, ProteinModel> proteinMap = new HashMap<>();

    	for (String[] peptideData : objects) {
    		String proteinName = peptideData[pqf.getProteinColumn() + 1].toString();
			ProteinModel protein = proteinMap.get(proteinName);
			if (protein == null) {
				protein = new ProteinModel(proteinName);
				proteins.add(protein);
				proteinMap.put(proteinName, protein);
			}
			List<State> states = new ArrayList<>();
			for (StateConfiguration stateConfig : pqf.getStateConfigurations()) {
				List<Run> runs = new ArrayList<>();
				for (RunConfiguration runConfig : stateConfig.getRuns()) {
					double abundance = Double.parseDouble(peptideData[runConfig.getColumn() + 1].toString());
					runs.add(new Run(runConfig.getName(), abundance));
				}
				states.add(new State(stateConfig.getName(), runs));
			}
			protein.addPeptide(new PeptideModel(peptideData[0].toString(), states));
		}
		return proteins;
	}
}
