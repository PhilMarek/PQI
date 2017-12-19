package de.mpc.pqi.view.diagram;

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

import de.mpc.pqi.model.PeptideModel;
import de.mpc.pqi.model.PeptideModel.State;
import de.mpc.pqi.model.PeptideModel.State.Run;
import de.mpc.pqi.model.ProteinModel;
import de.mpc.pqi.view.transform.AbundanceValueType;

public class ProfileChart {

	private CategoryPlot categoryPlot;
	private NumberAxis rangeAxis1;

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
		this.rangeAxis1 = new NumberAxis("Abundance");
		this.rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		if (valueType == AbundanceValueType.ABUNDANCE) {
			this.rangeAxis1.setLabel("Abundance");
		} else if (valueType == AbundanceValueType.LOG10) {
			this.rangeAxis1.setLabel("log10(abundance)");
		} else if (valueType == AbundanceValueType.ARCSIN) {
			this.rangeAxis1.setLabel("arcsin(abundance)");
		}

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

	public void updateValueType(AbundanceValueType valueType) {
		this.valueType = valueType;

		if (valueType == AbundanceValueType.ABUNDANCE) {
			this.rangeAxis1.setLabel("Abundance");
		} else if (valueType == AbundanceValueType.LOG10) {
			this.rangeAxis1.setLabel("log10(abundance)");
		} else if (valueType == AbundanceValueType.ARCSIN) {
			this.rangeAxis1.setLabel("arcsin(abundance)");
		}
		
		updateChartData(lastData);
	}
}
