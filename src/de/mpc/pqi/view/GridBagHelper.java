package de.mpc.pqi.view;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridBagHelper {
	private double[] m_rowWeights;
	private double[] m_columnWeights;
	
	public GridBagHelper(double[] rowWeights, double[] columnWeights) {
		m_rowWeights = rowWeights;
		m_columnWeights = columnWeights;
	}

	public GridBagConstraints getConstraints(int column, int row, int columnSpan, int rowSpan) {
		GridBagConstraints constraints = getConstraints(column, row);
		constraints.gridwidth = columnSpan;
		constraints.gridheight = rowSpan;
		return constraints;
	}
	
	public GridBagConstraints getConstraints(int column, int row) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = m_columnWeights[column];
		constraints.weighty = m_rowWeights[row];
		return constraints;
	}
}
