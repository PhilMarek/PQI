package de.mpc.pqi.model.properties;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.PortObjectSpec;

public class PeptideQuantificationFileSettings extends SettingsModel {
	private static final String CFGKEY_STRING_COLUMN_NAMES = "STRING_COLUMN_NAMES";
	private static final String CFGKEY_STRING_COLUMN_INDIZES = "STRING_COLUMN_INDIZES";
	private static final String CFGKEY_NUMBER_COLUMN_NAMES = "NUMBER_COLUMN_NAMES";
	private static final String CFGKEY_NUMBER_COLUMN_INDIZES = "NUMBER_COLUMN_INDIZES";
	private static final String CFGKEY_NUMBER_OF_STATES = "NUMBER_OF_STATES";
	private static final String CFGKEY_PROTEIN_COLUMN = "PROTEIN_COLUMN";
	private static final String CFGKEY_STATE_NAME = "STATE_NAME_";
	private static final String CFGKEY_STATE_RUN_COUNT = "STATE_RUN_COUNT_";
	private static final String CFGKEY_RUN_NAME = "RUN_NAME_";
	private static final String CFGKEY_RUN_COLUMN = "RUN_COLUMN_";
	private String configName;
	private PeptideQuantificationFile peptideQuantificationFile;

	public PeptideQuantificationFileSettings(String configName, PeptideQuantificationFile peptideQuantificationFile) {
		this.configName = configName;
		this.peptideQuantificationFile = peptideQuantificationFile;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected PeptideQuantificationFileSettings createClone() {
		return new PeptideQuantificationFileSettings(configName, peptideQuantificationFile);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModelTypeID() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getConfigName() {
		return configName;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadSettingsForDialog(NodeSettingsRO settings, PortObjectSpec[] specs) throws NotConfigurableException {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsForDialog(NodeSettingsWO settings) throws InvalidSettingsException {

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettingsForModel(NodeSettingsRO settings) throws InvalidSettingsException {
//TODO
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadSettingsForModel(NodeSettingsRO settings) throws InvalidSettingsException {
		peptideQuantificationFile = read(settings);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsForModel(NodeSettingsWO settings) {
		write(settings, peptideQuantificationFile);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public static PeptideQuantificationFile read(NodeSettingsRO settings) {
		PeptideQuantificationFile pqf = new PeptideQuantificationFile();
		
		try {
			pqf.setStringColumnNames(settings.getStringArray(CFGKEY_STRING_COLUMN_NAMES));
			pqf.setStringColumnIndizes(settings.getIntArray(CFGKEY_STRING_COLUMN_INDIZES));
			pqf.setNumberColumnNames(settings.getStringArray(CFGKEY_NUMBER_COLUMN_NAMES));
			pqf.setNumberColumnIndizes(settings.getIntArray(CFGKEY_NUMBER_COLUMN_INDIZES));
			int numberOfStates = settings.getInt(CFGKEY_NUMBER_OF_STATES);
			pqf.setNumberOfStates(numberOfStates);
			pqf.setProteinColumn(settings.getInt(CFGKEY_PROTEIN_COLUMN));
			
			List<StateConfiguration> stateConfigs = new ArrayList<>();
			for (int state = 0 ; state < numberOfStates ; state++) {
				StateConfiguration stateConfig = new StateConfiguration(settings.getString(CFGKEY_STATE_NAME + state));
				int numberOfRuns = settings.getInt(CFGKEY_STATE_RUN_COUNT + state);
				
				for (int run = 0 ; run < numberOfRuns ; run++) {
					RunConfiguration runConfig = new RunConfiguration(settings.getString(CFGKEY_RUN_NAME + state + "_" + run));
					runConfig.setColumn(settings.getInt(CFGKEY_RUN_COLUMN + state + "_" + run));
					stateConfig.addRun(runConfig);
				}
				stateConfigs.add(stateConfig);
			}
			pqf.setStateConfigurations(stateConfigs);
		} catch (InvalidSettingsException e) {
			//e.printStackTrace();
		}
		
		return pqf;
	}
	
	public static void write(NodeSettingsWO settings, PeptideQuantificationFile pqf) {
		settings.addStringArray(CFGKEY_STRING_COLUMN_NAMES, pqf.getStringColumnNames());
		settings.addIntArray(CFGKEY_STRING_COLUMN_INDIZES, pqf.getStringColumnIndizes());
		settings.addStringArray(CFGKEY_NUMBER_COLUMN_NAMES, pqf.getNumberColumnNames());
		settings.addIntArray(CFGKEY_NUMBER_COLUMN_INDIZES, pqf.getNumberColumnIndizes());
		settings.addInt(CFGKEY_NUMBER_OF_STATES, pqf.getNumberOfStates());
		settings.addInt(CFGKEY_PROTEIN_COLUMN, pqf.getProteinColumn());
		
		List<StateConfiguration> stateConfigurations = pqf.getStateConfigurations();
		for (int state = 0 ; state < pqf.getNumberOfStates() ; state++) {
			StateConfiguration stateConfig = stateConfigurations.get(state);
			settings.addString(CFGKEY_STATE_NAME + state, stateConfig.getName());
			settings.addInt(CFGKEY_STATE_RUN_COUNT + state, stateConfig.getNumberOfRuns());
			
			for (int run = 0 ; run < stateConfig.getNumberOfRuns() ; run++) {
				RunConfiguration runConfig = stateConfig.getRuns().get(run);
				settings.addString(CFGKEY_RUN_NAME + state + "_" + run, runConfig.getName());		
				settings.addInt(CFGKEY_RUN_COLUMN + state + "_" + run, runConfig.getColumn());
			}
		}
	}

	public PeptideQuantificationFile getPeptideQuantificationFile() {
		return peptideQuantificationFile;
	}

}
