package de.mpc.pqi.view.properties;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.mpc.pqi.model.properties.CSVFile;
import de.mpc.pqi.model.properties.PeptideQuantificationFile;

public class PropertyDialog extends JDialog {
	private static final long serialVersionUID = 1284951775403174558L;

	private CSVFilePropertyPanel csvFilePropertyPanel;
	private DataPropertyPanel dataPropertyPanel;
	
	private CSVFile csvFile;
	private PeptideQuantificationFile peptideQuantificationFile;

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
		csvFilePropertyPanel = new CSVFilePropertyPanel();
		csvFile = new CSVFile();
		csvFilePropertyPanel.setModel(csvFile);
		
		dataPropertyPanel = new DataPropertyPanel();
		
		nextButton = new JButton("Next");
		cancelButton = new JButton("Cancel");
	}

	private void initLayout() {
		setLayout(new BorderLayout());
		
		cardLayout = new CardLayout();
		propertyPanel = new JPanel(cardLayout);
		propertyPanel.add(csvFilePropertyPanel, "CSV File");
		propertyPanel.add(dataPropertyPanel, "File properties");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(nextButton);
		buttonPanel.add(cancelButton);
		
		add(propertyPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void initControl() {
		cancelButton.addActionListener(l -> {
			csvFile = null;
			peptideQuantificationFile = null;
			setVisible(false);
		});
		//TODO on close event
		nextButton.addActionListener(l -> {
			if (index == 0) {
				//TODO validation
				csvFile = csvFilePropertyPanel.getModel();
				if (peptideQuantificationFile == null) peptideQuantificationFile = new PeptideQuantificationFile();
//				try {
					//TODO peptideQuantificationFile.setStringColumnNames(csvFile.getStringColumnNames());
					//TODO peptideQuantificationFile.setNumberColumnNames(csvFile.getNumberColumnNames());
					dataPropertyPanel.initSettings(peptideQuantificationFile);
					
					cardLayout.show(propertyPanel, "File properties");
					nextButton.setText("Ok");
					index++;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			} else if (index == 1) {
				//TODO validation
				peptideQuantificationFile = dataPropertyPanel.getSettings();
				setVisible(false);
			} 
		});
	}

	public PeptideQuantificationFile getPeptideQuantificationFile() {
		return peptideQuantificationFile;
	}
	
	public CSVFile getCSVFile() {
		return csvFile;
	}
	
	public void setPeptideQuantificationFile(PeptideQuantificationFile peptideQuantificationFile) {
		this.peptideQuantificationFile = peptideQuantificationFile;
		dataPropertyPanel.initSettings(peptideQuantificationFile);
	}
	
	public void setCSVFile(CSVFile csvFile) {
		this.csvFile = csvFile;
		csvFilePropertyPanel.setModel(csvFile);
	}
}
