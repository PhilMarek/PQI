package de.mpc.pqi.model.properties;

import java.io.Serializable;

public class RunConfiguration implements Serializable {
	private static final long serialVersionUID = -518798792833635271L;

	private String name;
	private int column = 0;
	
	public RunConfiguration(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
}
