package com.shopme.admin.category;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.common.entity.Category;

public class CategoryExporter {

	public static void exportToCSV(List<Category> categoriesList, HttpServletResponse response) throws IOException {
		CategoryExporter.setResponseHeader(response, "text/csv",".csv"); 
		
		ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE); 
		
		String[] csvHeaders = {"ID", "Name"}; 
		String[] rowMapping = {"id", "name"}; 
		
		writer.writeHeader(csvHeaders); 
		
		for(Category category: categoriesList) {
			writer.write(category, rowMapping); 
		}
		
		writer.close();
	}

	public static void exportToExcel(List<Category> categoriesList, HttpServletResponse response) throws IOException {
		CategoryExporter.setResponseHeader(response, "application/octet-stream", ".xlsx"); 
		
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		XSSFSheet sheet = workbook.createSheet("Categories"); 
		
		CategoryExporter.writeHeaderLine(workbook, sheet); 
		CategoryExporter.writeDataLines(workbook, sheet, categoriesList); 
		
		ServletOutputStream outputStream = response.getOutputStream(); 
		
		workbook.write(outputStream); 
		
		workbook.close();
		outputStream.close(); 
	} 
	
	private static void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, List<Category> categoriesList) {
		int rowIndex = 1; 
		
		XSSFCellStyle cellStyle = workbook.createCellStyle(); 
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14); 
		cellStyle.setFont(font);
		
		for(Category category : categoriesList) {
			XSSFRow row = sheet.createRow(rowIndex++); 
			
			int columnIndex = 0; 
			
			CategoryExporter.createCell(sheet, row, columnIndex++, category.getId(), cellStyle);
			CategoryExporter.createCell(sheet, row, columnIndex++, category.getName(), cellStyle);
			CategoryExporter.createCell(sheet, row, columnIndex++, category.getAlias(), cellStyle);
			CategoryExporter.createCell(sheet, row, columnIndex++, 
					category.getParent() == null ? "" : category.getParent().getName(), cellStyle);
			CategoryExporter.createCell(sheet, row, columnIndex++, category.isEnabled(), cellStyle);
		}
	}

	private static void writeHeaderLine(XSSFWorkbook workbook, XSSFSheet sheet) {
		XSSFRow row = sheet.createRow(0); 
		
		XSSFCellStyle cellStyle = workbook.createCellStyle(); 
		XSSFFont font = workbook.createFont();
		font.setBold(true); 
		font.setFontHeight(16); 
		cellStyle.setFont(font);
		
		CategoryExporter.createCell(sheet, row, 0, "ID", cellStyle);
		CategoryExporter.createCell(sheet, row, 1, "Name", cellStyle);
		CategoryExporter.createCell(sheet, row, 2, "Alias", cellStyle);
		CategoryExporter.createCell(sheet, row, 3, "Parent", cellStyle);
		CategoryExporter.createCell(sheet, row, 4, "Enabled", cellStyle);
	}
	
	private static void createCell(XSSFSheet sheet, XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle) {
		XSSFCell cell = row.createCell(columnIndex); 
		sheet.autoSizeColumn(columnIndex); 
		
		if (value instanceof Integer) 
			cell.setCellValue((Integer) value);
		else if (value instanceof Boolean) 
			cell.setCellValue((boolean) value);
		else if (value instanceof String) 
			cell.setCellValue((String) value);
		
		cell.setCellStyle(cellStyle); 
	}
	
	private static void setResponseHeader(HttpServletResponse response, String contentType, String fileExtension) {
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss"); 
		String timestamp = formater.format(new Date()); 
		String fileName = "categories_" + timestamp + fileExtension; 
		
		response.setContentType(contentType); 
		
		String headerKey = "Content-Disposition"; 
		String headerValue = "attachment; filename=" + fileName; 
		
		response.setHeader(headerKey, headerValue); 
	}

	public static void exportToPDF(List<Category> categoriesList, HttpServletResponse response) throws IOException {
		CategoryExporter.setResponseHeader(response, "application/pdf", ".pdf");
		
		Document document = new Document(PageSize.A4); 
		PdfWriter.getInstance(document, response.getOutputStream()); 
		
		document.open(); 
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD); 
		font.setSize(18); 
		font.setColor(Color.BLUE); 
		
		Paragraph paragraph = new Paragraph("List of Categories", font); 
		paragraph.setAlignment(Paragraph.ALIGN_CENTER); 
		
		document.add(paragraph); 
		
		PdfPTable table = new PdfPTable(5); 
		table.setWidthPercentage(100f); 
		table.setSpacingBefore(10); 
		table.setWidths(new float[] {
				1.2f, 4.5f, 4.0f, 4.0f, 1.7f
		}); 
		
		CategoryExporter.writeTableHeader(table); 
		CategoryExporter.writeTableData(table, categoriesList); 
		
		document.add(table); 
		
		document.close();
	}

	private static void writeTableData(PdfPTable table, List<Category> categoriesList) {
		for(Category category : categoriesList) {
			table.addCell(String.valueOf(category.getId()));
			table.addCell(category.getName());
			table.addCell(category.getAlias());
			table.addCell(category.getParent() == null ? "" : category.getParent().getName());
			table.addCell(String.valueOf(category.isEnabled()));
		}
	}

	private static void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell(); 
		
		cell.setBackgroundColor(Color.BLUE);
		cell.setPadding(5);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA); 
		font.setColor(Color.WHITE); 
		
		cell.setPhrase(new Phrase("ID", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Name", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Alias", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Parent", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Enabled", font)); 
		table.addCell(cell);
	}
}