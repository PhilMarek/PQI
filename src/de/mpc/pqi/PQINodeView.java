package de.mpc.pqi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.knime.core.node.NodeView;

import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.ProteinModel;
import de.mpc.pqi.view.ProteinView;

/**
 * <code>NodeView</code> for the "PQI" Node.
 * 
 *
 * @author 
 */
public class PQINodeView extends NodeView<PQINodeModel> {
	private ProteinView view;
	
    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link PQINodeModel})
     */
    protected PQINodeView(final PQINodeModel nodeModel) {
        super(nodeModel);
        view = new ProteinView();
        setComponent(view);
        view.setModel(nodeModel.getData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        PQINodeModel nodeModel = 
            (PQINodeModel)getNodeModel();
        assert nodeModel != null;
        
        view.setModel(nodeModel.getData());
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    	JFileChooser fileChooser = new JFileChooser();
    	fileChooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
    	if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
    		String path = fileChooser.getSelectedFile().getAbsolutePath();
    		if (!path.endsWith(".csv")) path += ".csv";
    		File output = new File(path);
    		
    		try (FileWriter writer = new FileWriter(output)) {
    			PQINodeModel model = getNodeModel();

    			writer.append("\"peptide\"\t\"proteins\"\t");
    			model.getData().get(0).getPeptides().get(0).getStates().forEach(state -> {
    				state.getRuns().forEach(run -> {
						try {
							writer.append("\"" + run.getName() + "\"\t");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
    			});
    			writer.append("\"unique\"\t\"selected\"\n");
    			
	    		Set<String> writtenPeptides = new HashSet<>();
	    		for (ProteinModel protein : model.getData()) {
	    			for (PeptideModel peptide : protein.getPeptides()) {
	    				if (!writtenPeptides.contains(peptide.getName())) {
	    					writtenPeptides.add(peptide.getName());
	    					writer.append(peptide.getName() + "\t");
	    					writer.append(protein.getName() + "\t");
	    					peptide.getStates().forEach(state -> state.getRuns().forEach(run -> {
	    						try {
									writer.append(run.getAbundance() + "\t");
								} catch (IOException e) {
									e.printStackTrace();
								}
	    					}));
	    					writer.append(peptide.isUnique() + "\t");
	    					writer.append(peptide.isSelected() + "\t\n");
	    				}
	    			}
	    		}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // TODO things to do when opening the view
    }

}

