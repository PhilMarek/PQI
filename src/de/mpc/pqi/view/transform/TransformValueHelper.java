package de.mpc.pqi.view.transform;

import java.util.List;

public class TransformValueHelper {

	public static List<Double> transformValues(List<Double> values, AbundanceValueType type) {

		if (type == AbundanceValueType.ABUNDANCE) {
			return values;
		} else if (type == AbundanceValueType.ARCSIN) {
			for (int i = 0; i < values.size(); i++) {
				Double value = values.get(i);
				values.set(i, getARCSIN(value));
			}
			return values;
		} else if (type == AbundanceValueType.LOG10) {
			for (int i = 0; i < values.size(); i++) {
				Double value = values.get(i);
				values.set(i, getLog10(value));
			}
			return values;
		}

		return null;
	}

	public static Double transformValue(Double value,AbundanceValueType valueType ){
		if (valueType == AbundanceValueType.ABUNDANCE) {
			return value;
		} else if (valueType == AbundanceValueType.LOG10) {
			return getLog10(value);
		} else if (valueType == AbundanceValueType.ARCSIN) {
			return getARCSIN(value);
		}

		return null;
	}

	private static Double getLog10(Double value) {
		if (value.isInfinite() || value.isNaN() || value == 0.0) {
			return -1.0;
		} else {
			return Math.log10(value);
		}
	}

	private static double getARCSIN(double value) {
		Double result = Math.asin(value);
		
		if(result.isNaN()){
			return -1.0;
		}
		
		return result;
	}

}
