package de.mpc.pqi.model.protein;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	private Set<PeptideModel> peptides;
	private String description = "";

	/**
	 * Constructor.
	 * @param name
	 */
	public ProteinModel(String name) {
		this.name = name;
		this.peptides = new HashSet<>();
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
		return peptides.stream().collect(Collectors.toList());
	}
	
	/**
	 * Add a peptide to the list of found peptides for this protein.
	 * @param peptide
	 */
	public void addPeptide(PeptideModel peptide) {
		peptides.add(peptide);
	}

	public double getMean(String string, String reference) {
		double mean = 0;
		for (PeptideModel peptide : peptides) {
			mean += peptide.getRatioOfMeans(string, reference);
		}
		return mean / peptides.size();
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

}
