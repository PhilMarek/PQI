package de.mpc.pqi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Specialization of PQIModel. Stores information for protein, e.g. the protein name and the found peptides for this protein.
 * 
 * @author Phil Marek
 *
 */
public class ProteinModel extends PQIModel {
	/** The protein name */
	private String name;
	/** Found peptides for this protein */
	private List<PeptideModel> peptides;

	/**
	 * Constructor.
	 * @param name
	 */
	public ProteinModel(String name) {
		this.name = name;
		this.peptides = new ArrayList<>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the found peptides for this protein.
	 * @return
	 */
	public List<PeptideModel> getPeptides() {
		return peptides;
	}
	
	/**
	 * Add a peptide to the list of found peptides for this protein.
	 * @param peptide
	 */
	public void addPeptide(PeptideModel peptide) {
		peptides.add(peptide);
	}

}
