package de.mpc.pqi.view.diagram;

import java.awt.Font;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import de.mpc.pqi.model.PeptideModel;

public class BoxPlotChart {

	private CategoryPlot categoryPlot;

	private BoxAndWhiskerCategoryDataset createDataSet(PeptideModel peptideModel) {

		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		return dataset;
	}

	@SuppressWarnings("deprecation")
	public ChartPanel getView(PeptideModel peptideModel) {

		BoxAndWhiskerCategoryDataset dataset = createDataSet(peptideModel);

		CategoryAxis xAxis = new CategoryAxis("Type");
		NumberAxis yAxis = new NumberAxis("Value");
		yAxis.setAutoRangeIncludesZero(false);
		BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(true);
		renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

		this.categoryPlot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

		ChartPanel chartPanel = new ChartPanel(
				new JFreeChart("Box-and-Whisker Demo", new Font("SansSerif", Font.BOLD, 14), this.categoryPlot, true));

		chartPanel.setSize(50, 100);

		return chartPanel;
	}

	public void update(PeptideModel peptideModel) {
		this.categoryPlot.setDataset(createDataSet(peptideModel));
	}
}
