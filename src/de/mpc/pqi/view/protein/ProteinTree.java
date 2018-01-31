package de.mpc.pqi.view.protein;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.mpc.pqi.view.GridBagHelper;

/**
 * Basically a JTree working on the ProteinTreeModel. It additionally manages
 * filtering and covers smaller features as collapsing or expanding all nodes at
 * once.
 * 
 * @author Phil Marek
 *
 */
public class ProteinTree extends JPanel {
	private static final long serialVersionUID = -131916735947791789L;

	/**
	 * Listener which is invoked as soon as any PQIModel is selected.
	 * 
	 * @author Phil Marek
	 *
	 */
	public static interface ProteinTreeSelectionListener {
		public abstract void selectionChanged(Object selection);
	}

	/** List of ProteinTreeSelectionListeners registered for this. */
	private List<ProteinTreeSelectionListener> selectionListeners = new ArrayList<>();

	/** Static final string for "Aundance"-Radiobutton */
	private static final String ABUNDANCE = "Abundance";
	/** Static final string for "Log10"-Radiobutton */
	private static final String LOG10 = "log10(abundance)";
	/** Static final string for "Arcsin"-Radiobutton */
	private static final String ARCSIN = "arcsinh(abundance)";

	private JLabel abundanceValueType;
	private JRadioButton abundanceValueButton;
	private JRadioButton log10ValueButton;
	private JRadioButton arcsinhValueButton;
	private ButtonGroup abundanceValueButtonGroup;

	/** Maps a filter function to every JRadioButton choice */
	private static final Map<String, Function<String, Boolean>> filterActionMap = new HashMap<>();
	/** Static final string for "Contains"-Radiobutton */
	private static final String CONTAINS = "Contains";
	/** Static final string for "Prefix"-Radiobutton */
	private static final String PREFIX = "Prefix";
	/** Static final string for "Protein"-Radiobutton */
	private static final String PROTEIN = "Protein";
	/** Static final string for "Peptide"-Radiobutton */
	private static final String PEPTIDE = "Peptid";

	/** Label for the filter textfield */
	private JLabel filterLabel;
	/** Textfield for filtering the tree */
	private JTextField filterTextField;

	private JLabel filterCategoryLabel;
	private JRadioButton proteinRadioButton;
	private JRadioButton peptideRadioButton;
	private ButtonGroup filterCategoryGroup;

	private JLabel filterMethodLabel;
	/** Radiobutton which activates the "contains"-filter */
	private JRadioButton containsRadioButton;
	/** Radiobutton which activates the "prefix"-filter */
	private JRadioButton prefixRadioButton;
	/**
	 * ButtonGroup which manages selection of the radiobuttons for choosing the
	 * filter method
	 */
	private ButtonGroup filterMethodGroup;

	/** Tree for displaying the protein/peptide data */
	private JTree tree;
	/** Corresponding model for the tree */
	private ProteinTreeModel model;
	/** Corresponding selection model for the tree */
	private TreeSelectionModel selectionModel;
	/** Scrollpane for the tree */
	private JScrollPane scrollPane;

	/** Button which collapses all nodes on action */
	private JButton collapse;
	/** Button which expands all nodes on action */
	private JButton expand;

	/**
	 * Constructor. Initializes the gui, its layout and the controls.
	 * Furthermore, it sets the tree model.
	 * 
	 * @param model
	 */
	public ProteinTree() {
		initGUI();

		filterActionMap.put(CONTAINS, data -> {
			return data.trim().toLowerCase().contains(filterTextField.getText().trim().toLowerCase());
		});
		filterActionMap.put(PREFIX, data -> {
			return data.trim().toLowerCase().startsWith(filterTextField.getText().trim().toLowerCase());
		});

		initLayout();
		initControl();
	}

	/**
	 * Initializes gui components.
	 */
	private void initGUI() {
		filterLabel = new JLabel("Filter");
		filterTextField = new JTextField();

		filterCategoryLabel = new JLabel("Category:");
		proteinRadioButton = new JRadioButton(PROTEIN);
		peptideRadioButton = new JRadioButton(PEPTIDE);
		filterCategoryGroup = new ButtonGroup();
		filterCategoryGroup.add(proteinRadioButton);
		filterCategoryGroup.add(peptideRadioButton);
		filterCategoryGroup.setSelected(proteinRadioButton.getModel(), true);

		filterMethodLabel = new JLabel("Method:");
		containsRadioButton = new JRadioButton(CONTAINS);
		prefixRadioButton = new JRadioButton(PREFIX);
		filterMethodGroup = new ButtonGroup();
		filterMethodGroup.add(containsRadioButton);
		filterMethodGroup.add(prefixRadioButton);
		filterMethodGroup.setSelected(containsRadioButton.getModel(), true);

		abundanceValueType = new JLabel("Show Value as:");
		abundanceValueButton = new JRadioButton(ABUNDANCE);
		log10ValueButton = new JRadioButton(LOG10);
		arcsinhValueButton = new JRadioButton(ARCSIN);
		abundanceValueButtonGroup = new ButtonGroup();
		abundanceValueButtonGroup.add(abundanceValueButton);
		abundanceValueButtonGroup.add(log10ValueButton);
		abundanceValueButtonGroup.add(arcsinhValueButton);
		abundanceValueButtonGroup.setSelected(abundanceValueButton.getModel(), true);

		selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree = new JTree(new ProteinTreeModel(null));
		tree.setSelectionModel(selectionModel);
		scrollPane = new JScrollPane(tree);

		collapse = new JButton("Collapse");
		expand = new JButton("Expand");
	}

	/**
	 * Initializes layouting of the gui components.
	 */
	private void initLayout() {
		setLayout(new GridBagLayout());

		GridBagHelper constraints = new GridBagHelper(new double[] { 0, 0, 0, 0, 0.1, 0 },
				new double[] { 0, 0, 0, 0 });
		add(filterLabel, constraints.getConstraints(0, 0));
		add(filterTextField, constraints.getConstraints(1, 0, 3, 1));

		add(filterCategoryLabel, constraints.getConstraints(0, 1));
		add(proteinRadioButton, constraints.getConstraints(1, 1));
		add(peptideRadioButton, constraints.getConstraints(2, 1));

		add(filterMethodLabel, constraints.getConstraints(0, 2));
		add(containsRadioButton, constraints.getConstraints(1, 2));
		add(prefixRadioButton, constraints.getConstraints(2, 2));

		add(abundanceValueType, constraints.getConstraints(0, 3));
		add(abundanceValueButton, constraints.getConstraints(1, 3));
		add(log10ValueButton, constraints.getConstraints(2, 3));
		add(arcsinhValueButton, constraints.getConstraints(3, 3));

		add(scrollPane, constraints.getConstraints(0, 4, 4, 1));

		add(collapse, constraints.getConstraints(0, 5));
		add(expand, constraints.getConstraints(1, 5));
	}

	/**
	 * Registers controls and listeners for the gui components.
	 */
	private void initControl() {
		filterTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				onChange();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				onChange();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				onChange();
			}

			private void onChange() {
				updateComponent();
			}
		});

		// If any radio button selection is changed, just call updateComponent
		proteinRadioButton.addActionListener(e -> updateComponent());
		peptideRadioButton.addActionListener(e -> updateComponent());
		containsRadioButton.addActionListener(e -> updateComponent());
		prefixRadioButton.addActionListener(e -> updateComponent());

		selectionModel.addTreeSelectionListener(treePath -> {
			TreePath newSelectionPath = treePath.getNewLeadSelectionPath();
			Object selection = newSelectionPath != null
					&& !ProteinTreeModel.ROOT.equals(newSelectionPath.getLastPathComponent())
							? newSelectionPath.getLastPathComponent() : null;
			if (selection != null)
				selectionListeners.forEach(listener -> listener.selectionChanged(selection));
		});

		collapse.addActionListener(e -> {
			for (int i = 1; i < tree.getRowCount(); i++)
				tree.collapseRow(i);
		});

		expand.addActionListener(e -> {
			for (int i = 0; i < tree.getRowCount(); i++)
				tree.expandRow(i);
		});
		
		abundanceValueButton.setActionCommand("abundance");
		log10ValueButton.setActionCommand("log10");
		arcsinhValueButton.setActionCommand("arcsinh");
	}

	/**
	 * Sets the treeModel as new model and updates the components.
	 * 
	 * @param treeModel
	 */
	public void setModel(ProteinTreeModel treeModel) {
		model = treeModel;
		updateComponent();
	}

	/**
	 * Updates the gui components.
	 */
	public void updateComponent() {
		String filter = null;
		if (containsRadioButton.isSelected())
			filter = CONTAINS;
		else if (prefixRadioButton.isSelected())
			filter = PREFIX;
		boolean filterForProteins = proteinRadioButton.isSelected();
		tree.setModel(model.getFilteredModel(filterTextField.getText(), filterActionMap.get(filter),
				filterForProteins));
	}

	/**
	 * Adds {@link listener} to the list of selection listeneres
	 * 
	 * @param listener
	 */
	public void addSelectionListener(ProteinTreeSelectionListener listener) {
		selectionListeners.add(listener);
	}

	/**
	 * Removes {@link listener} from the list of selection listeneres
	 * 
	 * @param listener
	 */
	public void removeSelectionListener(ProteinTreeSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	public void addAbundanceValueButtonListener(ActionListener listener) {
		this.abundanceValueButton.addActionListener(listener);
		this.log10ValueButton.addActionListener(listener);
		this.arcsinhValueButton.addActionListener(listener);
	}
}
