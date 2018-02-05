package de.mpc.pqi.view.properties;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.mpc.pqi.model.properties.CSVFile;
import de.mpc.pqi.model.properties.PeptideQuantificationFileConfiguration;

public class PropertyDialog extends JDialog {
	private static final long serialVersionUID = 1284951775403174558L;

	private CSVFilePropertyPanel pepQuantFilePropertyPanel;
	private CSVFilePropertyPanel protQuantFilePropertyPanel;
	private DataPropertyPanel dataPropertyPanel;
	
	private CSVFile pepQuantFile;
	private CSVFile protQuantFile;
	private PeptideQuantificationFileConfiguration pepQuantFileConfig;

	private CardLayout cardLayout;
	
	private JPanel propertyPanel;
	private int index = 0;
	
	private JButton nextButton;
	private JButton cancelButton;

	public PropertyDialog() {
		initGUI();
		initLayout();
		initControl();
		
		setModal(true);
	}

	private void initGUI() {
		pepQuantFilePropertyPanel = new CSVFilePropertyPanel();
		pepQuantFile = new CSVFile();
		pepQuantFilePropertyPanel.setModel(pepQuantFile);
		
		protQuantFilePropertyPanel = new CSVFilePropertyPanel();
		protQuantFile = new CSVFile();
		protQuantFilePropertyPanel.setModel(protQuantFile);
		
		dataPropertyPanel = new DataPropertyPanel();
		
		nextButton = new JButton("Next");
		cancelButton = new JButton("Cancel");
	}

	private void initLayout() {
		setLayout(new BorderLayout());
		
		cardLayout = new CardLayout();
		propertyPanel = new JPanel(cardLayout);
		propertyPanel.add(pepQuantFilePropertyPanel, "Peptide Quantification File");
		propertyPanel.add(dataPropertyPanel, "File properties");
		propertyPanel.add(protQuantFilePropertyPanel, "Protein Quantification File");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(nextButton);
		buttonPanel.add(cancelButton);
		
		add(propertyPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void initControl() {
		cancelButton.addActionListener(l -> {
			pepQuantFile = null;
			pepQuantFileConfig = null;
			setVisible(false);
		});
		//TODO on close event
		nextButton.addActionListener(l -> {
			if (index == 0) {
				//TODO validation
				pepQuantFile = pepQuantFilePropertyPanel.getModel();
				if (pepQuantFileConfig == null) pepQuantFileConfig = new PeptideQuantificationFileConfiguration();
//				try {
					//TODO peptideQuantificationFile.setStringColumnNames(csvFile.getStringColumnNames());
					//TODO peptideQuantificationFile.setNumberColumnNames(csvFile.getNumberColumnNames());
					dataPropertyPanel.initSettings(pepQuantFileConfig);
					
					cardLayout.show(propertyPanel, "File properties");
					index++;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			} else if (index == 1) {
				//TODO validation
				pepQuantFileConfig = dataPropertyPanel.getSettings();
				cardLayout.show(propertyPanel, "Protein Quantification File");
				nextButton.setText("Ok");
				index++;
			} else if (index == 2) {
				//TODO validation
				protQuantFile = protQuantFilePropertyPanel.getModel();
				setVisible(false);
			} 
		});
	}

	public PeptideQuantificationFileConfiguration getPeptideQuantificationFile() {
		return pepQuantFileConfig;
	}
	
	public CSVFile getPepQuantFile() {
		return pepQuantFile;
	}
	
	public CSVFile petProtQuantFile() {
		return protQuantFile;
	}
	
	public void setPepQuantFileConfiguration(PeptideQuantificationFileConfiguration peptideQuantificationFile) {
		this.pepQuantFileConfig = peptideQuantificationFile;
		dataPropertyPanel.initSettings(peptideQuantificationFile);
	}
	
	public void setPepQuantFile(CSVFile csvFile) {
		this.pepQuantFile = csvFile;
		pepQuantFilePropertyPanel.setModel(csvFile);
	}

	public void setProtQuantFile(CSVFile protQuantFile) {
		this.protQuantFile = protQuantFile;
		protQuantFilePropertyPanel.setModel(protQuantFile);
	}
}
