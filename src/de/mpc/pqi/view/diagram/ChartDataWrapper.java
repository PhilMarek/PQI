package de.mpc.pqi.view.diagram;

import java.util.ArrayList;
import java.util.List;

public class ChartDataWrapper {

	private String rowKey;
	public List<ColumnValuePair> columnValuePairs;

	public ChartDataWrapper(String rowKey) {
		this.rowKey = rowKey;
		this.columnValuePairs = new ArrayList<ColumnValuePair>();
	}

	public List<ColumnValuePair> getSeriesValuePairs() {
		return this.columnValuePairs;
	}
	
	public void addColumnValuePair(ColumnValuePair columnValuePair){
		this.columnValuePairs.add(columnValuePair);
	}

	public String getRowKey() {
		return this.rowKey;
	}

}
