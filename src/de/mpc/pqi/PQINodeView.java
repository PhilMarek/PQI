package de.mpc.pqi;

import org.knime.core.node.NodeView;

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
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // TODO things to do when opening the view
    }

}

