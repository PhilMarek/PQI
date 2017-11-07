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

	public GridBagConstraints getConstraints(int gridX, int gridY, int spanX, int spanY) {
		GridBagConstraints constraints = getConstraints(gridX, gridY);
		constraints.gridwidth = spanX;
		constraints.gridheight = spanY;
		return constraints;
	}
	
	public GridBagConstraints getConstraints(int gridX, int gridY) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = gridX;
		constraints.gridy = gridY;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = m_rowWeights[gridX];
		constraints.weighty = m_columnWeights[gridY];
		return constraints;
	}
}
