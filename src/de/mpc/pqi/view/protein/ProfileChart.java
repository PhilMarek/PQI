package de.mpc.pqi.view.protein;

import java.util.List;

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

import de.mpc.pqi.model.protein.PeptideModel;
import de.mpc.pqi.model.protein.ProteinModel;
import de.mpc.pqi.model.protein.PeptideModel.State;
import de.mpc.pqi.model.protein.PeptideModel.State.Run;

public class ProfileChart {

	private CategoryPlot categoryPlot;
	private NumberAxis xAxis;

	private AbundanceValueType valueType;

	private Object lastData;

	private CategoryDataset createDataset(PeptideModel peptideModel) {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();

		if (peptideModel != null) {
			for (State state : peptideModel.getStates()) {
				for (Run run : state.getRuns()) {
					Double value = valueType.transformValue(run.getAbundance());
					result.addValue(value, peptideModel.getName(),
								state.getName() + " " + run.getName());
				}
			}
		}
		return result;
	}

	private CategoryDataset createDataset(ProteinModel proteinModel) {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();

		if (proteinModel != null) {
			for (PeptideModel peptideModel : proteinModel.getPeptides())
				if (peptideModel != null) {
					for (State state : peptideModel.getStates()) {
						for (Run run : state.getRuns()) {
							Double value = valueType.transformValue(run.getAbundance());
							result.addValue(value, peptideModel.getName(),
										state.getName() + " " + run.getName());
						}
					}
				}
		}
		return result;
	}

	private CategoryDataset createDataset(List<PeptideModel> peptideModels) {
		final DefaultCategoryDataset result = new DefaultCategoryDataset();

		if (peptideModels != null) {
			for (PeptideModel peptideModel : peptideModels)
				if (peptideModel != null) {
					for (State state : peptideModel.getStates()) {
						for (Run run : state.getRuns()) {
							Double value = valueType.transformValue(run.getAbundance());
							result.addValue(value, peptideModel.getName(),
										state.getName() + " " + run.getName());
						}
					}
				}
		}
		return result;
	}

	public void updateChartData(Object data) {
		this.lastData = data;

		if (data instanceof ProteinModel) {
			ProteinModel proteinModel = (ProteinModel) data;
			categoryPlot.setDataset(createDataset(proteinModel));
		} else if (data instanceof PeptideModel) {
			PeptideModel peptideModel = (PeptideModel) data;
			categoryPlot.setDataset(createDataset(peptideModel));
		} else if (data instanceof List) {
			@SuppressWarnings("unchecked")
			List<PeptideModel> peptideModels = (List<PeptideModel>) data;
			categoryPlot.setDataset(createDataset(peptideModels));
		}
	}

	public ChartPanel createChart(PeptideModel peptideModel, AbundanceValueType valueType) {

		this.valueType = valueType;
		this.lastData = peptideModel;
		final CategoryDataset dataset1 = createDataset(peptideModel);
		this.xAxis = new NumberAxis(valueType.getCaption());
		this.xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		final LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
		renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		this.categoryPlot = new CategoryPlot(dataset1, null, xAxis, renderer1);
		this.categoryPlot.setDomainGridlinesVisible(true);

		final CategoryAxis xAxis = new CategoryAxis("Runs");
		xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot(xAxis);

		plot.add(this.categoryPlot);

		ChartPanel chartPanel = new ChartPanel(new JFreeChart(plot));

		return chartPanel;
	}

	public void updateValueType(AbundanceValueType valueType) {
		this.valueType = valueType;
		this.xAxis.setLabel(valueType.getCaption());
		updateChartData(lastData);
	}
}
