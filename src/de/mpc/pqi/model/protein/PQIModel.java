package de.mpc.pqi.model.protein;

/**
 * Abstract model for input data and visualization in trees and diagrams.
 * Provides a name, e.g. a protein or peptide name.
 * 
 * @author Phil Marek
 *
 */
public abstract class PQIModel {
	/**
	 * Provides a unique name;
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName();
	}
}
