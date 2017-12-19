package de.mpc.pqi.view.properties;

import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import de.mpc.pqi.model.properties.CSVFile;
import de.mpc.pqi.view.GridBagHelper;

public class CSVFilePropertyPanel extends JPanel {
	private static final long serialVersionUID = -151633086412088253L;

	private JTextField fileTextField;
	private JButton fileButton;
	private JLabel columnDelimiterLabel;
	private JTextField columnDelimiterTextField;
	private JLabel rowDelimiterLabel;
	private JTextField rowDelimiterTextField;
	private JLabel quoteLabel;
	private JTextField quoteTextField;
	private JLabel commentLabel;
	private JTextField commentTextField;
	private JLabel hasColumnHeaderLabel;
	private JCheckBox hasColumnHeaderCheckBox;
	private JLabel hasRowHeaderLabel;
	private JCheckBox hasRowHeaderCheckBox;
	
	public CSVFilePropertyPanel() {
		initGUI();
		initLayout();
		initControl();
	}

	private void initGUI() {
		fileTextField = new JTextField();
		fileButton = new JButton("Choose File");

		columnDelimiterLabel = new JLabel("Column Delimiter");
		columnDelimiterTextField = new JTextField();
		
		rowDelimiterLabel = new JLabel("Row Delimiter");
		rowDelimiterTextField = new JTextField();
		
		quoteLabel = new JLabel("Quote");
		quoteTextField = new JTextField();
		
		commentLabel = new JLabel("Comment");
		commentTextField = new JTextField();
		
		hasColumnHeaderLabel = new JLabel("Has Column Header");
		hasColumnHeaderCheckBox = new JCheckBox();
		hasColumnHeaderCheckBox.setSelected(true);
		
		hasRowHeaderLabel = new JLabel("Has Row Header");
		hasRowHeaderCheckBox = new JCheckBox();
	}

	private void initLayout() {
		setLayout(new GridBagLayout());
		
		GridBagHelper constraints = new GridBagHelper(new double[] {0, 0, 0, 0}, new double[] {0, 0.1, 0, 0.1});
		
		add(fileTextField, constraints.getConstraints(0, 0, 3, 1));
		add(fileButton, constraints.getConstraints(3, 0));
		
		add(columnDelimiterLabel, constraints.getConstraints(0, 1));
		add(columnDelimiterTextField, constraints.getConstraints(1, 1));
		add(rowDelimiterLabel, constraints.getConstraints(2, 1));
		add(rowDelimiterTextField, constraints.getConstraints(3, 1));
		
		add(quoteLabel, constraints.getConstraints(0, 2));
		add(quoteTextField, constraints.getConstraints(1, 2));
		add(commentLabel, constraints.getConstraints(2, 2));
		add(commentTextField, constraints.getConstraints(3, 2));

		add(hasColumnHeaderLabel, constraints.getConstraints(0, 3));
		add(hasColumnHeaderCheckBox, constraints.getConstraints(1, 3));
		add(hasRowHeaderLabel, constraints.getConstraints(2, 3));
		add(hasRowHeaderCheckBox, constraints.getConstraints(3, 3));
	}

	private void initControl() {
		fileButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File file) {
					return file.getName().endsWith(".csv") || file.getName().endsWith(".tsv") || file.isDirectory();
				}
				@Override
				public String getDescription() {
					return "CSV/TSV";
				}
			});
			int choice = chooser.showOpenDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION) {
				fileTextField.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
	}
	
	public CSVFile getModel() {
		CSVFile file = new CSVFile();
		file.setFile(fileTextField.getText());
		file.setColumnDelimiter(columnDelimiterTextField.getText());
		file.setRowDelimiter(rowDelimiterTextField.getText());
		file.setQuote(quoteTextField.getText());
		file.setComment(commentTextField.getText());
		file.setHasColumnHeader(hasColumnHeaderCheckBox.isSelected());
		file.setHasRowHeader(hasRowHeaderCheckBox.isSelected());
		return file;
	}

	public void setModel(CSVFile csvFile) {
		fileTextField.setText(csvFile.getFile());
		columnDelimiterTextField.setText(csvFile.getColumnDelimiter());
		rowDelimiterTextField.setText(csvFile.getRowDelimiter());
		quoteTextField.setText(csvFile.getQuote());
		commentTextField.setText(csvFile.getComment());
		hasColumnHeaderCheckBox.setSelected(csvFile.getHasColumnHeader());
		hasRowHeaderCheckBox.setSelected(csvFile.getHasRowHeader());
	}
}
