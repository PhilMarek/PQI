package de.mpc.pqi.view.protein;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.PeptideModel.State.Run;

public class BoxPlot extends ChartPanel {
	private static final long serialVersionUID = -5485704018928241705L;
	
	private PeptideModel model;
	private AbundanceValueType abundanceValueType = AbundanceValueType.ABUNDANCE;

	private CategoryPlot categoryPlot;
	private NumberAxis yAxis;
	private CategoryAxis xAxis;
	
	public BoxPlot() {
		super(null);
		initGUI();
		setSize(50, 100);
	}
	
	private void initGUI() {
		BoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		
		xAxis = new CategoryAxis("State");
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		
		yAxis = new NumberAxis(abundanceValueType.getCaption());
		yAxis.setAutoRangeIncludesZero(false);
		
		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		renderer.setOutlinePaint(null);

		categoryPlot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
		setChart(new JFreeChart(this.categoryPlot));
	}

	private void update() {
		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		if(model != null){
			for (State state : model.getStates()) {
				List<Double> values = new ArrayList<>();

				for (Run run : state.getRuns()) {
					values.add(abundanceValueType.transformValue(run.getAbundance()));
				}
				
				dataset.add(values, model.getName(), state.getName());
			}
		}
		categoryPlot.setDataset(dataset);

		yAxis.setLabel(abundanceValueType.getCaption());
	}

	public void setModel(PeptideModel peptideModel) {
		this.model = peptideModel;
		update();
	}

	public void setValueType(AbundanceValueType abundanceValueType) {
		this.abundanceValueType = abundanceValueType;
		update();
	}
}
