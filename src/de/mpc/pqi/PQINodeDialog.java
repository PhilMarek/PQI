package de.mpc.pqi;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import de.mpc.pqi.model.properties.PeptideQuantificationFile;
import de.mpc.pqi.model.properties.PeptideQuantificationFileSettings;
import de.mpc.pqi.view.properties.DataPropertyPanel;

/**
 * <code>NodeDialog</code> for the "PQI" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class PQINodeDialog extends NodeDialogPane {
	private DataPropertyPanel dataPropertyPanel;
	
    /**
     * New pane for configuring PQI node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected PQINodeDialog() {
        super();
        dataPropertyPanel = new DataPropertyPanel();
        addTabAt(0, "Data properties", dataPropertyPanel);
    }
    
    @Override
    protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
    	PeptideQuantificationFile pqf = PeptideQuantificationFileSettings.read(settings);
    	if (pqf.getStringColumnNames() == null || pqf.getStringColumnNames().length == 0 ||
    		pqf.getNumberColumnNames() == null || pqf.getNumberColumnNames().length == 0) {
    		
    		List<String> stringColumnNames = new ArrayList<>();
    		List<Integer> stringColumnIndizes = new ArrayList<>();
    		List<String> numberColumnNames = new ArrayList<>();
    		List<Integer> numberColumnIndizes = new ArrayList<>();
    		for (int i = 0 ; i < specs[0].getNumColumns() ; i++) {
    			DataColumnSpec cur = specs[0].getColumnSpec(i);
    			if (cur.getType().toString().equals("String")) {
    				stringColumnNames.add(cur.getName());
    				stringColumnIndizes.add(i);
    			} else if (cur.getType().toString().startsWith("Number")) {
    				numberColumnNames.add(cur.getName());
    				numberColumnIndizes.add(i);
    			}
    		}
    		pqf.setStringColumnNames(stringColumnNames.toArray(new String[0]));
    		int[] stringColumnIndizesArray = new int[stringColumnIndizes.size()];
    		for (int i = 0 ; i < stringColumnIndizes.size() ; i++) stringColumnIndizesArray[i] = stringColumnIndizes.get(i);
    		pqf.setStringColumnIndizes(stringColumnIndizesArray);
    		int[] numberColumnIndizesArray = new int[numberColumnIndizes.size()];
    		for (int i = 0 ; i < numberColumnIndizes.size() ; i++) numberColumnIndizesArray[i] = numberColumnIndizes.get(i);
    		pqf.setNumberColumnNames(numberColumnNames.toArray(new String[0]));
    		pqf.setNumberColumnIndizes(numberColumnIndizesArray);
    	}
		dataPropertyPanel.initSettings(pqf);
    }

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		PeptideQuantificationFileSettings.write(settings, dataPropertyPanel.getSettings());
	}
    
}

