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
import de.mpc.pqi.view.tree.ProteinTree;
import de.mpc.pqi.view.tree.ProteinTreeModel;
import de.mpc.pqi.model.ProteinModel;

public class ProteinTreeTestPanel extends JPanel {
	private static final long serialVersionUID = -2631147602208704811L;

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Missing program argument: Filepath quants_peptides.csv");
		} else {
			ProteinTreeTestPanel panel = new ProteinTreeTestPanel(readData(args[0], "#", "\"", "\t", true));
			
			JFrame frame = new JFrame("FrameDemo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(panel, BorderLayout.WEST);
			//TODO(Ayoub): frame.getContentPane().add(diagram, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	public ProteinTreeTestPanel(Object[][] objects) {
        setLayout(new FlowLayout());
        ProteinTree tree = new ProteinTree(new ProteinTreeModel(parseData(objects)));
        
        //TODO(Ayoub): tree.addSelectionListener((selection) -> System.out.println(selection));
        add(tree);
	}
	
	private List<ProteinModel> parseData(Object[][] objects) {
		List<ProteinModel> proteins = new ArrayList<>();
		Map<String, ProteinModel> proteinMap = new HashMap<>();
    	for (int i = 0 ; i < objects.length ; i++) {
    		Object[] peptideData = objects[i];
    		String[] proteinsForPeptide = peptideData[1].toString().split("/");
    		for (int j = 0 ; j < proteinsForPeptide.length ; j++) {
    			ProteinModel protein = proteinMap.get(proteinsForPeptide[j]);
    			if (protein == null) {
    				protein = new ProteinModel(proteinsForPeptide[j]);
    				proteins.add(protein);
    				proteinMap.put(proteinsForPeptide[j], protein);
    			}
    			List<State> states = new ArrayList<>();
    			for (int k = 0 ; k < 5 ; k++) {
    				List<Long> abundances = new ArrayList<>();
    				for (int l = 0 ; l < 3 ; l++) {
    					abundances.add(Long.parseLong((String) peptideData[3 + k * 3 + l]));
    				}
    				states.add(new State(abundances));
    			}
    			PeptideModel peptide = new PeptideModel(peptideData[0].toString(), states); 
    			protein.addPeptide(peptide);
    		}
    	}
    	return proteins;
	}
	
	private static String[][] readData(String file, String comment, String quote, String colDelimiter, boolean hasHeader) throws IOException {
		List<String[]> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
	        stream.forEach(line -> {
	        	if (!line.startsWith(comment) && !line.trim().equals("")) {
	        		String[] split = line.split(colDelimiter);
	        		for (int i = 0 ; i < split.length ; i++) {
	        			while (split[i].startsWith(quote)) split[i] = split[i].substring(1);
	        			while (split[i].endsWith(quote)) split[i] = split[i].substring(0, split[i].length() - 1);
	        		}
	        		lines.add(split);
	        	}
	        });
		}
		if (hasHeader) lines.remove(0);
		return lines.toArray(new String[0][]);
	}
	
}
