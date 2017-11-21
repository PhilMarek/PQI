package de.mpc.pqi.view.diagram;

public class ColumnValuePair {
	private String column;
	private Double value;

	public ColumnValuePair(String column, Double value) {
		this.column = column;
		this.value = value;
	}

	public double getValue() {
		return logBase10(this.value);
	}

	public String getColumnKey() {
		return this.column;
	}

	private double logBase10(Double v) {

		if (v.isNaN() || v < 0.0 || v == 0.0) {
			return 0.0;
		} else if (v.isInfinite()) {
			return 9999.0;
		} else {
			return Math.log10(v);
		}
	}
}
