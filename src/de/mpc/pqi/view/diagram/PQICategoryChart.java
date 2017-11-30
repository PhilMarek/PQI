package de.mpc.pqi.view.diagram;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.PeptideModel.State;
import de.mpc.pqi.model.PeptideModel.State.Run;
import de.mpc.pqi.model.ProteinModel;

public class PQICategoryChart {

	private CategoryPlot categoryPlot;

	private CategoryDataset createDataset(PeptideModel peptideModel) {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();

		if (peptideModel != null) {
			for (State state : peptideModel.getStates()) {
				for (Run run : state.getRuns()) {

					Long value = run.getAbundance();
					result.addValue(value.doubleValue(), peptideModel.getName(), state.getName() + " " + run.getName());
				}
			}
		}
		return result;
	}

	public ChartPanel createChart(PeptideModel peptideModel) {
		final CategoryDataset dataset1 = createDataset(peptideModel);
		final NumberAxis rangeAxis1 = new NumberAxis("Abundance");
		rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		final LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
		renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		this.categoryPlot = new CategoryPlot(dataset1, null, rangeAxis1, renderer1);
		this.categoryPlot.setDomainGridlinesVisible(true);

		final CategoryAxis domainAxis = new CategoryAxis("Runs");
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(domainAxis);

		plot.add(this.categoryPlot);

		ChartPanel chartPanel = new ChartPanel(new JFreeChart(plot));

		return chartPanel;
	}

	public void updateChart(PeptideModel peptideModel) {

		categoryPlot.setDataset(createDataset(peptideModel));

	}

	private CategoryDataset createDataset(ProteinModel proteinModel) {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();

		if (proteinModel != null) {
			for (PeptideModel peptideModel : proteinModel.getPeptides())
				if (peptideModel != null) {
					for (State state : peptideModel.getStates()) {
						for (Run run : state.getRuns()) {

							Long value = run.getAbundance();
							result.addValue(value.doubleValue(), peptideModel.getName(),
									state.getName() + " " + run.getName());
						}
					}
				}
		}
		return result;
	}

	public void updateChart(ProteinModel proteinModel) {
		categoryPlot.setDataset(createDataset(proteinModel));
	}
}
