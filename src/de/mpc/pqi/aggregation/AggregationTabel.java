package de.mpc.pqi.aggregation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.InputMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class AggregationTabel extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8110151826617663593L;

	private JTable table;

	public AggregationTabel() {

		this.table = new JTable();
		this.table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		this.table.setFillsViewportHeight(true);

		this.table.setRowSelectionAllowed(false);
		this.table.setColumnSelectionAllowed(false);
		this.table.setCellSelectionEnabled(false);
		InputMap im = getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke("DOWN"), "none");
		im.put(KeyStroke.getKeyStroke("UP"), "none");
		im.put(KeyStroke.getKeyStroke("LEFT"), "none");
		im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					return;
				}
				if (table.getModel() instanceof AggregationTableModel) {
					AggregationTableModel model = (AggregationTableModel) table.getModel();
					int index = table.getSelectionModel().getLeadSelectionIndex();
					if (index != -1) {
						model.rowSelect(table.getSelectionModel().getLeadSelectionIndex());

						table.repaint();
					}

				}

			}
		});

		this.table.getTableHeader().setReorderingAllowed(false);

		this.table.setDefaultRenderer(Object.class, new TableCellRenderer() {
			private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				if (row % 2 == 0) {
					c.setBackground(Color.WHITE);
				} else {
					c.setBackground(Color.LIGHT_GRAY);
				}
				return c;
			}

		});

		getViewport().add(this.table);
	}

	public void update(AggregationTableModel tableModel) {
		if (tableModel != null) {
			this.table.setModel(tableModel);
			this.table.repaint();
		}

	}
}
