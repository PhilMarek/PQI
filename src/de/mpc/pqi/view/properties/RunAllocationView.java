package de.mpc.pqi.view.properties;

import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.mpc.pqi.view.GridBagHelper;

public class RunAllocationView extends JPanel {
	public static interface RunAllocationListener {
		public abstract void onRemove(List<String> obj);
		public abstract void onAdd(List<String> obj);
	}
	
	private static final long serialVersionUID = -1825331995991784561L;
	
	private JList<String> assignedList;
	private JLabel assignedLabel;
	private JList<String> unusedList;
	private JLabel unusedLabel;
	
	private JButton addButton;
	private JButton removeButton;
	
	private List<RunAllocationListener> listeners = new ArrayList<>();
	
	public RunAllocationView() {
		initGUI();
		initLayout();
		initControl();
	}

	private void initGUI() {
		assignedList = new JList<>();
		assignedLabel = new JLabel("Assigned columns:");
		unusedList = new JList<>();
		unusedLabel = new JLabel("Unused columns:");
		
		addButton = new JButton(">>");
		removeButton = new JButton("<<");
	}

	private void initLayout() {
		setLayout(new GridBagLayout());
		
		GridBagHelper constraints = new GridBagHelper(new double[] {0, 0.2, 0.1, 0.1, 0.2}, new double[] {0.1, 0, 0.1});
		
		add(unusedLabel, constraints.getConstraints(0, 0));
		add(new JScrollPane(unusedList), constraints.getConstraints(0, 1, 1, 4));
		add(addButton, constraints.getConstraints(1, 2));
		add(removeButton, constraints.getConstraints(1, 3));
		add(assignedLabel, constraints.getConstraints(2, 0));
		add(new JScrollPane(assignedList), constraints.getConstraints(2, 1, 1, 4));
	}

	private void initControl() {
		addButton.addActionListener(e -> {
			List<String> selection = unusedList.getSelectedValuesList();
			selection.forEach(element -> ((DefaultListModel<String>)assignedList.getModel()).addElement(element));
			selection.forEach(element -> ((DefaultListModel<String>)unusedList.getModel()).removeElement(element));
			listeners.forEach(listener -> listener.onAdd(selection));
		});
		
		removeButton.addActionListener(e -> {
			List<String> selection = assignedList.getSelectedValuesList();
			selection.forEach(element -> ((DefaultListModel<String>)unusedList.getModel()).addElement(element));
			selection.forEach(element -> ((DefaultListModel<String>)assignedList.getModel()).removeElement(element));
			listeners.forEach(listener -> listener.onRemove(selection));
		});
	}
	
	public void setUnusedColumns(Collection<String> unusedColumns) {
		DefaultListModel<String> model = new DefaultListModel<>();
		unusedColumns.forEach(element -> model.addElement(element));
		unusedList.setModel(model);
	}

	public void setAssignedColumns(Collection<String> assignedColumns) {
		DefaultListModel<String> model = new DefaultListModel<>();
		assignedColumns.forEach(element -> model.addElement(element));
		assignedList.setModel(model);
	}
	
	public void addListener(RunAllocationListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(RunAllocationListener listener) {
		listeners.remove(listener);
	}
}
