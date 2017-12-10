package de.mpc.pqi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
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

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.PeptideModel.State;
import de.mpc.pqi.model.PeptideModel.State.Run;
import de.mpc.pqi.model.ProteinModel;
import de.mpc.pqi.model.properties.PeptideQuantificationFile;
import de.mpc.pqi.model.properties.PeptideQuantificationFileSettings;
import de.mpc.pqi.model.properties.RunConfiguration;
import de.mpc.pqi.model.properties.StateConfiguration;

/**
 * This is the model implementation of PQI.
 * 
 *
 * @author
 */
public class PQINodeModel extends NodeModel {
	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(PQINodeModel.class);

	private static final String CFGKEY_PEPTIDE_QUANTIFICATION_FILE = "Peptide quantification file";
	private final PeptideQuantificationFileSettings peptideQuantificationFile = new PeptideQuantificationFileSettings(
			CFGKEY_PEPTIDE_QUANTIFICATION_FILE, new PeptideQuantificationFile());

	private List<ProteinModel> proteins;

	/**
	 * Constructor for the node model.
	 */
	protected PQINodeModel() {
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		proteins = new ArrayList<>();
		PeptideQuantificationFile pqf = peptideQuantificationFile.getPeptideQuantificationFile();
		Map<String, ProteinModel> proteinMap = new HashMap<>();

		BufferedDataTable quantPeptides = inData[0];
		quantPeptides.forEach(peptideData -> {
			String proteinName = peptideData.getCell(pqf.getProteinColumn()).toString();
			ProteinModel protein = proteinMap.get(proteinName);
			if (protein == null) {
				protein = new ProteinModel(proteinName);
				proteins.add(protein);
				proteinMap.put(proteinName, protein);
			}
			List<State> states = new ArrayList<>();
			for (StateConfiguration stateConfig : pqf.getStateConfigurations()) {
				List<Run> runs = new ArrayList<>();
				for (RunConfiguration runConfig : stateConfig.getRuns()) {
					double abundance = Double.parseDouble(peptideData.getCell(runConfig.getColumn()).toString());
					runs.add(new Run(runConfig.getName(), abundance));
				}
				states.add(new State(stateConfig.getName(), runs));
			}
			protein.addPeptide(new PeptideModel(peptideData.getKey().toString(), states));
		});

		// TODO
		DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
		allColSpecs[0] = new DataColumnSpecCreator("Column 0", StringCell.TYPE).createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("Column 1", DoubleCell.TYPE).createSpec();
		allColSpecs[2] = new DataColumnSpecCreator("Column 2", IntCell.TYPE).createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		for (int i = 0; i < 5; i++) {
			RowKey key = new RowKey("Row " + i);
			// the cells of the current row, the types of the cells must match
			// the column spec (see above)
			DataCell[] cells = new DataCell[3];
			cells[0] = new StringCell("String_" + i);
			cells[1] = new DoubleCell(0.5 * i);
			cells[2] = new IntCell(i);
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);

			// check if the execution monitor was canceled
			exec.checkCanceled();
			exec.setProgress(i / 5, "Adding row " + i);
		}

		checkUnique();

		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
	}

	private void checkUnique() {
		for (ProteinModel proteinModel : proteins) {
			for (PeptideModel peptideModel : proteinModel.getPeptides()) {
				checkPeptideUnique(peptideModel);
			}
		}
	}

	private void checkPeptideUnique(PeptideModel peptideModel) {

		boolean foundFirstTime = false;

		for (int i = 0; i < proteins.size(); i++) {
			ProteinModel protein = proteins.get(i);
			for (int j = 0; j < protein.getPeptides().size(); j++) {
				PeptideModel peptide = protein.getPeptides().get(j);

				if (peptideModel.getName().equalsIgnoreCase(peptide.getName())) {
					if (!foundFirstTime) {
						foundFirstTime = true;
					} else {
						return;
					}
				}
			}

			if (i == proteins.size() - 1) {
				peptideModel.setUnique(true);
			}
		}
	}

	/**
	 * Returns the input data.
	 * 
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
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
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

		return new DataTableSpec[] { null };
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
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
	}

}
