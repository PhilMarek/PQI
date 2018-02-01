package de.mpc.pqi.aggregation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;

import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class AggregationTable extends JScrollPane {
	private static final long serialVersionUID = -8110151826617663593L;

	private JTable table;
	
	private boolean internalUpdate = false;

	public AggregationTable() {
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

		this.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (table.getModel() instanceof AggregationTableModel) {
					AggregationTableModel model = (AggregationTableModel) table.getModel();
					int row = table.rowAtPoint(evt.getPoint());
					if (row > 0) {
						model.rowSelect(row);
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
		this.table.setDefaultRenderer(Boolean.class, new TableCellRenderer() {
			private DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if (value instanceof Boolean) {
					JCheckBox cb = new JCheckBox();
					if (value != null) cb.setSelected((boolean)value);
					if (row % 2 == 0) {
						cb.setBackground(Color.WHITE);
					} else {
						cb.setBackground(Color.LIGHT_GRAY);
					}
					return cb;
				} else {
					Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
							column);
					if (row % 2 == 0) {
						c.setBackground(Color.WHITE);
					} else {
						c.setBackground(Color.LIGHT_GRAY);
					}
					return c;
				}
			}
		});
		this.table.setAutoCreateRowSorter(true);
		this.table.getRowSorter().addRowSorterListener(e -> {
			// fireTableDataChanged lets the row sorter sort again, thus, internalUpdate prevents endless loops
			if (!internalUpdate) {
				internalUpdate = true;
				((AggregationTableModel)table.getModel()).fireTableDataChanged();
				internalUpdate = false;
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
