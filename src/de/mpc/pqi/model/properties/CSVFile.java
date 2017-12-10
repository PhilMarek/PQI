package de.mpc.pqi.model.properties;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CSVFile implements Serializable {
	private static final long serialVersionUID = -3891633855254737228L;

	private String file;
	private String columnDelimiter = "\\t";
	private String rowDelimiter = "\\n";
	private String quote = "\"";
	private String comment = "#";
	private boolean hasColumnHeader = true;
	private boolean hasRowHeader = true;
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getColumnDelimiter() {
		return columnDelimiter;
	}
	public void setColumnDelimiter(String columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}
	public String getRowDelimiter() {
		return rowDelimiter;
	}
	public void setRowDelimiter(String rowDelimiter) {
		this.rowDelimiter = rowDelimiter;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean getHasColumnHeader() {
		return hasColumnHeader;
	}
	public void setHasColumnHeader(boolean hasColumnHeader) {
		this.hasColumnHeader = hasColumnHeader;
	}
	public boolean getHasRowHeader() {
		return hasRowHeader;
	}
	public void setHasRowHeader(boolean hasRowHeader) {
		this.hasRowHeader = hasRowHeader;
	}
	
	public String[] getColumnNames() throws IOException {
		String[] header = null;
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			String headerLine = stream.filter(line -> !line.startsWith(comment) && !line.trim().equals("")).findFirst().get();
			
			String[] split = headerLine.split(columnDelimiter);
			header = new String[split.length - 1];
			for (int i = 1; i < split.length; i++) {
				while (split[i].startsWith(quote))
					split[i] = split[i].substring(1);
				while (split[i].endsWith(quote))
					split[i] = split[i].substring(0, split[i].length() - 1);
				header[i - 1] = split[i];
			}
		}
		return header;
	}
	
	public String[][] readData() throws IOException {
		List<String[]> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			stream.forEach(line -> {
				if (!line.startsWith(comment) && !line.trim().equals("")) {
					String[] split = line.split(columnDelimiter);
					for (int i = 0; i < split.length; i++) {
						while (split[i].startsWith(quote))
							split[i] = split[i].substring(1);
						while (split[i].endsWith(quote))
							split[i] = split[i].substring(0, split[i].length() - 1);
					}
					lines.add(split);
				}
			});
		}
		if (hasColumnHeader)
			lines.remove(0);
		return lines.toArray(new String[0][]);
	}
}
