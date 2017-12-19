package de.mpc.pqi.view.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public enum AbundanceValueType {
	
	ABUNDANCE(value -> {
		return value;
	}), 
	LOG10(value -> {
		Double v = new Double(value);
		return v.isInfinite() || v.isNaN() || value == 0.0 ? -1. : Math.log10(value);
	}), 
	ARCSIN(value -> {
		Double result = Math.asin(value);
		return result.isNaN() ? -1. : result;
	});
	
	private DoubleUnaryOperator operator;

	private AbundanceValueType(DoubleUnaryOperator operator) {
		this.operator = operator;
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
