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

public class BoxPlot {

	private CategoryPlot categoryPlot;

	private BoxAndWhiskerCategoryDataset createDataSet(PeptideModel peptideModel) {

		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		if(peptideModel != null){
			for (State state : peptideModel.getStates()) {
				List<Double> values = new ArrayList<>();

				for (Run run : state.getRuns()) {
					values.add(run.getAbundance());
				}
				
				dataset.add(values, peptideModel.getName(), state.getName());
			}
		}

		return dataset;
	}

	@SuppressWarnings("deprecation")
	public ChartPanel getView(PeptideModel peptideModel) {

		BoxAndWhiskerCategoryDataset dataset = createDataSet(peptideModel);

		CategoryAxis xAxis = new CategoryAxis("State");
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		NumberAxis yAxis = new NumberAxis("Abundance");
		yAxis.setAutoRangeIncludesZero(false);
		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
		renderer.setOutlinePaint(null);
		

		this.categoryPlot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

		ChartPanel chartPanel = new ChartPanel(
				new JFreeChart(this.categoryPlot));
		chartPanel.setSize(50, 100);

		return chartPanel;
	}

	public void update(PeptideModel peptideModel) {
		this.categoryPlot.setDataset(createDataSet(peptideModel));
	}
}