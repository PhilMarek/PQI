package de.mpc.pqi.view.protein;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public enum AbundanceValueType {
	
	ABUNDANCE(value -> {
		return value;
	}, "Abundance"), 
	LOG10(value -> {
		Double v = new Double(value);
		return v.isInfinite() || v.isNaN() || value == 0.0 ? -1. : Math.log10(value);
	}, "log10(abundance)"), 
	ARCSINH(value -> {
		return Math.log(value + Math.sqrt(value*value + 1.0));
	}, "arcsinh(abundance)");
	
	private DoubleUnaryOperator operator;
	private String caption;

	private AbundanceValueType(DoubleUnaryOperator operator, String caption) {
		this.operator = operator;
		this.caption = caption;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public Double transformValue(Double value){
		return operator.applyAsDouble(value);
	}
	
	public List<Double> transformValues(List<Double> values) {
		List<Double> result = new ArrayList<>();
		for (double value : values) {
			result.add(transformValue(value));
		}
		return result;
	}
}
