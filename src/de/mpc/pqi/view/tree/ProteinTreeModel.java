package de.mpc.pqi.view.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.mpc.pqi.model.PQIModel;
import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.ProteinModel;

/**
 * 
 * The ProteinTreeModel contains the input data, thus peptides and corresponding proteins, and handles filtering of these.
 * 
 * @author Phil Marek
 *
 */
public class ProteinTreeModel implements TreeModel {
	/**
	 * Specialization of PQIModel for root object.
	 */
	public final static PQIModel ROOT = new PQIModel() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return "Root";
		}
	};
	
	/** Contains all show protein and the underlying shown peptides */
	private List<ProteinModel> proteinData;
	
	/**
	 * Constructor. Initializes data.
	 * @param proteinData
	 */
	public ProteinTreeModel(List<ProteinModel> proteinData) {
		this.proteinData = proteinData == null ? new ArrayList<>() : proteinData;
	}

	/**
	 * Returns a filtered tree model based on filterString and function.
	 * @param filterString
	 * @param function
	 * @param filterForProteins 
	 * @return
	 */
	public ProteinTreeModel getFilteredModel(final String filterString, Function<PQIModel, Boolean> function, boolean filterForProteins) {
		//if root fulfills the filter conditions or if filterString is empty, just return everything
		if (function.apply(getRoot()) || filterString.trim().isEmpty()) return this;

		List<ProteinModel> filteredProteins = new ArrayList<>();
		if (filterForProteins) {
			// Filtering proteins. If a protein fulfills the filter condition, all corresponding peptides are added.
			proteinData.stream().filter(protein -> function.apply(protein)).collect(Collectors.toList()).forEach(protein -> {
				filteredProteins.add(protein);
			});
			return new ProteinTreeModel(filteredProteins);
		} else {
			// Filtering peptides. If any peptide of a protein fulfills the filter condition, the complete protein is added.
			proteinData.forEach(protein -> {
				List<PeptideModel> peptides = protein.getPeptides().stream()
						.filter(peptide -> function.apply(peptide)).collect(Collectors.toList());
				if (!peptides.isEmpty()) filteredProteins.add(protein);
			});
			return new ProteinTreeModel(filteredProteins);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getChild(Object parent, int index) {
		if (parent.equals(ROOT)) return proteinData.get(index);
		else return ((ProteinModel)parent).getPeptides().get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ProteinModel) {
			return ((ProteinModel)parent).getPeptides().size();
		} else if (parent.equals(ROOT)) {
			return proteinData.size();
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.equals(ROOT)) return proteinData.indexOf(child);
		else return ((ProteinModel)parent).getPeptides().indexOf(child);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PQIModel getRoot() {
		return ROOT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLeaf(Object arg0) {
		return getChildCount(arg0) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
}
