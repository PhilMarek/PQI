package de.mpc.pqi.view.protein;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.mpc.pqi.model.protein.PQIModel;
import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.ProteinModel;

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
	
	/** Contains all shown proteins and the underlying shown peptides **/
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
	public ProteinTreeModel getFilteredModel(final String filterString, Function<String, Boolean> function, boolean filterForProteins) {
		//if root fulfills the filter conditions or if filterString is empty, just return everything
		if (function.apply(getRoot().getName()) || filterString.trim().isEmpty()) return this;

		Set<ProteinModel> filteredProteins = new HashSet<>();
		if (filterForProteins) {
			// Filtering proteins. If a protein fulfills the filter condition, all corresponding peptides are added.
			proteinData.stream().filter(protein -> function.apply(protein.getName()) || function.apply(protein.getDescription())).collect(Collectors.toList()).forEach(protein -> {
				filteredProteins.add(protein);
			});
		} else {
			// Filtering peptides. If any peptide of a protein fulfills the filter condition, the complete protein is added.
			proteinData.forEach(protein -> {
				List<PeptideModel> peptides = protein.getPeptides().stream()
						.filter(peptide -> function.apply(peptide.getName())).collect(Collectors.toList());
				if (!peptides.isEmpty()) filteredProteins.add(protein);
			});
		}
		return new ProteinTreeModel(new ArrayList<>(filteredProteins));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		//TODO
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
