package de.mpc.pqi.view.diagram;

import java.awt.Font;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

public class BoxAndWhiskerChart {

	private CategoryPlot categoryPlot;

	private BoxAndWhiskerCategoryDataset createDataSet(List<BoxplotData> data) {
		// final int seriesCount = 3;
		// final int categoryCount = 4;
		// final int entityCount = 22;

		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		// for (int i = 0; i < seriesCount; i++) {
		// for (int j = 0; j < categoryCount; j++) {
		// final List list = new ArrayList();
		// // add some values...
		// for (int k = 0; k < entityCount; k++) {
		// final double value1 = 10.0 + Math.random() * 3;
		// list.add(new Double(value1));
		// final double value2 = 11.25 + Math.random(); // concentrate
		// // values in
		// // the
		// // middle
		// list.add(new Double(value2));
		// }
		// dataset.add(list, "Series " + i, " Type " + j);
		// }
		//
		// }

		for (BoxplotData boxplotData : data) {
			dataset.add(boxplotData.getControleValues(), "Controle", boxplotData.getCategroy());
			dataset.add(boxplotData.getDiseaseValues(), "Disease", boxplotData.getCategroy());
		}

		return dataset;
	}

	public ChartPanel getView(List<BoxplotData> data) {

		BoxAndWhiskerCategoryDataset dataset = createDataSet(data);

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

	public void update(List<BoxplotData> data) {
		this.categoryPlot.setDataset(createDataSet(data));
	}
}
