package de.mpc.pqi.view.diagram;

import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class PQICategoryChart {

	private CategoryPlot categoryPlot;

	private CategoryDataset createDataset(List<ChartDataWrapper> data) {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();

		for (ChartDataWrapper chartDataWrapper : data) {
			for (ColumnValuePair columnValuePair : chartDataWrapper.getSeriesValuePairs()) {
				result.addValue(columnValuePair.getValue(), chartDataWrapper.getRowKey(),
						columnValuePair.getColumnKey());
			}
		}

		return result;
	}

	public ChartPanel createChart(List<ChartDataWrapper> data) {
		final CategoryDataset dataset1 = createDataset(data);
		final NumberAxis rangeAxis1 = new NumberAxis("log10 (Sum Quantity)");
		rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		final LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
		renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		this.categoryPlot = new CategoryPlot(dataset1, null, rangeAxis1, renderer1);
		this.categoryPlot.setDomainGridlinesVisible(true);

		final CategoryAxis domainAxis = new CategoryAxis("Runs");
		CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);

		plot.add(this.categoryPlot);

		return new ChartPanel(new JFreeChart(plot));
	}

	public void updateChart(List<ChartDataWrapper> data) {

		categoryPlot.setDataset(createDataset(data));

	}

}
