package de.mpc.pqi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortType;

import de.mpc.pqi.model.properties.PepQuantFileConfiguration;
import de.mpc.pqi.model.properties.PeptideQuantificationFileSettings;
import de.mpc.pqi.model.properties.RunConfiguration;
import de.mpc.pqi.model.properties.StateConfiguration;
import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.PeptideModel.State.Run;
import de.mpc.pqi.model.protein.ProteinModel;


/**
 * This is the model implementation of PQI.
 * 
 *
 * @author 
 */
public class PQINodeModel extends NodeModel {
//	public static class SelectionRow extends DefaultRow {
//
//		public SelectionRow(String rowId, List<DataCell> row) {
//			super(rowId, row);
//			// TODO Auto-generated constructor stub
//		}
//		
//	}
	
    // the logger instance
    private static final NodeLogger logger = NodeLogger.getLogger(PQINodeModel.class);
        
	private static final String CFGKEY_PEPTIDE_QUANTIFICATION_FILE = "Peptide quantification file";
    private final PeptideQuantificationFileSettings peptideQuantificationFile = 
    		new PeptideQuantificationFileSettings(CFGKEY_PEPTIDE_QUANTIFICATION_FILE, new PepQuantFileConfiguration());
    
    private List<ProteinModel> proteins;

    /**
     * Constructor for the node model.
     */
    protected PQINodeModel() {
    	super(new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE, BufferedDataTable.TYPE_OPTIONAL}, 
    			new PortType[] {BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	proteins = new ArrayList<>();
    	PepQuantFileConfiguration pqf = peptideQuantificationFile.getPeptideQuantificationFile();
    	
    	BufferedDataTable quantPeptides = inData[0];
    	BufferedDataTable quantProteins = inData[1];
    	BufferedDataTable fasta = inData[2];
    	
    	Map<String, String> fastaMapping = new HashMap<>();
    	if (fasta != null) {
    		fasta.forEach(row -> {
    			String s = row.getCell(0).toString();
    			if (s.startsWith(">")){
    				int index = s.indexOf(" ");
    				if (index > 0) fastaMapping.put(s.substring(1, index), s.substring(index));
    			}
    		});
    	}	
    	
    	ProteinModel notUsedPeptides = new ProteinModel("Not used for quantification");
    	proteins.add(notUsedPeptides);
    	Map<String, ProteinModel> quantifiedProteins = new HashMap<>();
    	Map<ProteinModel, Set<String>> map = new HashMap<>();
    	quantProteins.forEach(proteinData -> {
    		String proteinGroupName = proteinData.getKey().toString();
    		ProteinModel protein = new ProteinModel(proteinGroupName);
    		boolean first = true;
    		String description = "";
    		for (String proteinName : proteinGroupName.split("/")) {

    			if (fastaMapping.containsKey(proteinName)){
    				description += (first ? "" : "/") + fastaMapping.get(proteinName);
    				if (first) first = false;
    			}
    		}

    		protein.setDescription(description);
    		
    		Set<String> set = new HashSet<>();
    		for (String proteinName : proteinGroupName.split("/")) {
    			quantifiedProteins.put(proteinName, protein);
    			set.add(proteinName);
    		}
    		map.put(protein, set);
    		proteins.add(protein);
    	});
    	
    	quantPeptides.forEach(peptideData -> {
    		List<State> states = new ArrayList<>();
    		for (StateConfiguration stateConfig : pqf.getStateConfigurations()) {
    			List<Run> runs = new ArrayList<>();
    			for (RunConfiguration runConfig : stateConfig.getRuns()) {
    				double abundance = Double.parseDouble(peptideData.getCell(runConfig.getColumn()).toString());
    				runs.add(new Run(runConfig.getName(), abundance));
    			}
    			states.add(new State(stateConfig.getName(), runs));
    		}
    		PeptideModel peptide = new PeptideModel(peptideData.getKey().toString(), states);
    		peptide.setUnique(true);
    		
    		String proteinNames = peptideData.getCell(pqf.getProteinColumn()).toString();
    		Set<ProteinModel> handledProteins = new HashSet<>();
    		boolean neverUsed = true;
    		for (String proteinName : proteinNames.split("/")) {
				ProteinModel protein = quantifiedProteins.get(proteinName);
				boolean incomplete = false;
				if (protein == null || handledProteins.contains(protein)) {
					incomplete = true;
				} else {
					handledProteins.add(protein);
					Set<String> strings = map.get(protein);
					for (String string : strings) {
						boolean found = false;
						for (String name : proteinNames.split("/")) {
							if (name.equals(string)) {
								found = true;
								break;
							}
						}
						if (!found) {
							incomplete = true;
							break;
						}
					}
				}
				if (!incomplete) {
					if (!neverUsed) peptide.setUnique(false);
					neverUsed = false;
					protein.addPeptide(peptide);
				} 
    		}
    		if (neverUsed) {
    			peptide.setUnique(false);
    			peptide.setUsedForQuantification(false);
    			notUsedPeptides.addPeptide(peptide);
    		} else {
    			peptide.setUsedForQuantification(true);
    		}
    	});
    	
        DataColumnSpec[] outputColumns = new DataColumnSpec[quantPeptides.getDataTableSpec().getNumColumns() + 1];
    	for (int i = 0 ; i < quantPeptides.getDataTableSpec().getNumColumns() ; i++) {
    		outputColumns[i] = quantPeptides.getDataTableSpec().getColumnSpec(i);
    	}
    	outputColumns[quantPeptides.getDataTableSpec().getNumColumns()] = new DataColumnSpecCreator("selected",
    		DataType.getType(BooleanCell.class)).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(outputColumns);
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        quantPeptides.forEach(entry -> {
        	DataCell[] cells = new DataCell[entry.getNumCells() + 1];
        	for (int i = 0 ; i < entry.getNumCells() ; i++) {
        		cells[i] = entry.getCell(i);
        	}
        	cells[entry.getNumCells()] = BooleanCellFactory.create(true);
        	container.addRowToTable(new DefaultRow(entry.getKey(), cells));
        });
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    /**
     * Returns the input data.
     * @return
     */
    public List<ProteinModel> getData() {
    	return proteins;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    	if (proteins != null) proteins.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        peptideQuantificationFile.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
    	peptideQuantificationFile.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
    	peptideQuantificationFile.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
    }

}

