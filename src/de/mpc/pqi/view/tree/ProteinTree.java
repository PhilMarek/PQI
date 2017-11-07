package de.mpc.pqi.view.tree;

import java.awt.GridBagLayout;
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

import de.mpc.pqi.model.PQIModel;
import de.mpc.pqi.view.GridBagHelper;

/**
 * Basically a JTree working on the ProteinTreeModel. It additionally manages filtering and covers smaller features as collapsing or
 * expanding all nodes at once.
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

	/** Maps a filter function to every JRadioButton choice */
	private static final Map<String, Function<PQIModel, Boolean>> m_filterActionMap = new HashMap<>();
	/** Static final string for "Containts"-Radiobutton */
	private static final String CONTAINS = "Contains";
	/** Static final string for "Prefix"-Radiobutton */
	private static final String PREFIX = "Prefix";
	
	/** Label for the filter textfield */
	private JLabel m_filterLabel;
	/** Textfield for filtering the tree */
	private JTextField m_filterTextField;
	
	/** Radiobutton which activates the "contains"-filter */
	private JRadioButton m_containsRadioButton;
	/** Radiobutton which activates the "prefix"-filter */
	private JRadioButton m_prefixRadioButton;
	/** ButtonGroup which manages selection of the radiobuttons */
	private ButtonGroup m_buttonGroup;
	
	/** Tree for displaying the protein/peptide data */
	private JTree m_tree;
	/** Corresponding model for the tree */
	private ProteinTreeModel m_model;
	/** Corresponding selection model for the tree */
	private TreeSelectionModel m_selectionModel;
	/** Scrollpane for the tree */
	private JScrollPane m_scrollPane;
	
	/** Button which collapses all nodes on action */
	private JButton m_collapse;
	/** Button which expands all nodes on action */
	private JButton m_expand;
	
	/**
	 * Constructor. Initializes the gui, its layout and the controls. Furthermore, it sets the tree model.
	 * @param model
	 */
	public ProteinTree(ProteinTreeModel model) {
		initGUI();

		m_filterActionMap.put(CONTAINS, data -> { 
			return data.getName().trim().toLowerCase().contains(m_filterTextField.getText().trim().toLowerCase()); 
		});
		m_filterActionMap.put(PREFIX, data -> { 
			return data.getName().trim().toLowerCase().startsWith(m_filterTextField.getText().trim().toLowerCase()); 
		});
		setModel(model);		

		initLayout();
		initControl();
	}
	
	/**
	 * Initializes gui components.
	 */
	private void initGUI() {
		m_filterLabel = new JLabel("Filter");
		m_filterTextField = new JTextField();
		
		m_containsRadioButton = new JRadioButton(CONTAINS);
		m_prefixRadioButton = new JRadioButton(PREFIX);
		m_buttonGroup = new ButtonGroup();
		m_buttonGroup.add(m_containsRadioButton);
		m_buttonGroup.add(m_prefixRadioButton);
		m_buttonGroup.setSelected(m_containsRadioButton.getModel(), true);
		
		m_selectionModel = new DefaultTreeSelectionModel();
		m_selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		m_tree = new JTree();
		m_tree.setSelectionModel(m_selectionModel);
		m_scrollPane = new JScrollPane(m_tree);
		
		m_collapse = new JButton("Collapse");
		m_expand = new JButton("Expand");
	}
	
	/**
	 * Initializes layouting of the gui components.
	 */
	private void initLayout() {
		setLayout(new GridBagLayout());

		GridBagHelper constraints = new GridBagHelper(new double[]{0, 0.1, 0, 0, 0, 0}, new double[]{0, 0.1, 0});
		add(m_filterLabel, constraints.getConstraints(0, 0));
		add(m_filterTextField, constraints.getConstraints(1, 0, 3, 1));
		add(m_containsRadioButton, constraints.getConstraints(4, 0));
		add(m_prefixRadioButton, constraints.getConstraints(5, 0));
		add(m_scrollPane, constraints.getConstraints(0, 1, 6, 1));
		add(m_collapse, constraints.getConstraints(0, 2, 2, 1));
		add(m_expand, constraints.getConstraints(2, 2));
	}
	
	/**
	 * Registers controls and listeneres for the gui components.
	 */
	private void initControl() {
		m_filterTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {	onChange(); }
			@Override
			public void insertUpdate(DocumentEvent arg0) { onChange(); }
			@Override
			public void removeUpdate(DocumentEvent arg0) { onChange(); }
			
			private void onChange() {
				updateComponent();
			}
		});
		
		m_containsRadioButton.addActionListener(e -> updateComponent());
		m_prefixRadioButton.addActionListener(e -> updateComponent());
		
		m_selectionModel.addTreeSelectionListener(treePath -> {
			//Selection events are only fired if and only if a leaf (a peptide) is selected.
			TreePath newSelectionPath = treePath.getNewLeadSelectionPath();
			Object selection = newSelectionPath != null && m_model.isLeaf(newSelectionPath.getLastPathComponent()) ?
					newSelectionPath.getLastPathComponent() : null;
			if (selection != null) selectionListeners.forEach(listener -> listener.selectionChanged(selection));
		});

		m_collapse.addActionListener(e -> {
			for (int i = 1 ; i < m_tree.getRowCount() ; i++) m_tree.collapseRow(i);
		});
		
		m_expand.addActionListener(e -> {
			for (int i = 0 ; i < m_tree.getRowCount() ; i++) m_tree.expandRow(i);
		});
	}
	
	/**
	 * Sets the treeModel as new model and updates the components.
	 * @param treeModel
	 */
	public void setModel(ProteinTreeModel treeModel) {
		m_model = treeModel;
		updateComponent();
	}
	
	/**
	 * Updates the gui components.
	 */
	public void updateComponent() {
		String filter = null;
		if (m_containsRadioButton.isSelected()) filter = CONTAINS;
		else if (m_prefixRadioButton.isSelected()) filter = PREFIX;
		m_tree.setModel(m_model.getFilteredModel(m_filterTextField.getText(), m_filterActionMap.get(filter)));
	}

	/**
	 * Adds {@link listener} to the list of selection listeneres
	 * @param listener
	 */
	public void addSelectionListener(ProteinTreeSelectionListener listener) {
		selectionListeners.add(listener);
	}
	

	/**
	 * Removes {@link listener} from the list of selection listeneres
	 * @param listener
	 */
	public void removeSelectionListener(ProteinTreeSelectionListener listener) {
		selectionListeners.remove(listener);
	}
}
