package com.shopme.admin.user;

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
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.common.entity.User;

public class UserExporter {

	public static void exportToCSV(List<User> usersList, HttpServletResponse response) throws IOException {
		UserExporter.setResponseHeader(response, "text/csv",".csv"); 
		
		ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE); 
		
		String[] csvHeaders = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"}; 
		String[] rowMapping = {"id", "email", "firstName", "lastName","roles" ,"enabled"}; 
		
		writer.writeHeader(csvHeaders); 
		
		for(User user: usersList) {
			writer.write(user, rowMapping); 
		}
		
		writer.close();
	}

	public static void exportToExcel(List<User> usersList, HttpServletResponse response) throws IOException {
		UserExporter.setResponseHeader(response, "application/octet-stream", ".xlsx"); 
		
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		XSSFSheet sheet = workbook.createSheet("Users"); 
		
		UserExporter.writeHeaderLine(workbook, sheet); 
		UserExporter.writeDataLines(workbook, sheet, usersList); 
		
		ServletOutputStream outputStream = response.getOutputStream(); 
		
		workbook.write(outputStream); 
		
		workbook.close();
		outputStream.close(); 
	} 
	
	private static void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, List<User> usersList) {
		int rowIndex = 1; 
		
		XSSFCellStyle cellStyle = workbook.createCellStyle(); 
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14); 
		cellStyle.setFont(font);
		
		for(User user : usersList) {
			XSSFRow row = sheet.createRow(rowIndex++); 
			
			int columnIndex = 0; 
			
			UserExporter.createCell(sheet, row, columnIndex++, user.getId(), cellStyle);
			UserExporter.createCell(sheet, row, columnIndex++, user.getEmail(), cellStyle);
			UserExporter.createCell(sheet, row, columnIndex++, user.getFirstName(), cellStyle);
			UserExporter.createCell(sheet, row, columnIndex++, user.getLastName(), cellStyle);
			UserExporter.createCell(sheet, row, columnIndex++, user.getRoles().toString(), cellStyle);
			UserExporter.createCell(sheet, row, columnIndex++, user.isEnabled(), cellStyle);
		}
	}

	private static void writeHeaderLine(XSSFWorkbook workbook, XSSFSheet sheet) {
		XSSFRow row = sheet.createRow(0); 
		
		XSSFCellStyle cellStyle = workbook.createCellStyle(); 
		XSSFFont font = workbook.createFont();
		font.setBold(true); 
		font.setFontHeight(16); 
		cellStyle.setFont(font);
		
		UserExporter.createCell(sheet, row, 0, "User ID", cellStyle);
		UserExporter.createCell(sheet, row, 1, "E-mail", cellStyle);
		UserExporter.createCell(sheet, row, 2, "First Name", cellStyle);
		UserExporter.createCell(sheet, row, 3, "Last Name", cellStyle);
		UserExporter.createCell(sheet, row, 4, "Roles", cellStyle);
		UserExporter.createCell(sheet, row, 5, "Enabled", cellStyle);
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
		String fileName = "users_" + timestamp + fileExtension; 
		
		response.setContentType(contentType); 
		
		String headerKey = "Content-Disposition"; 
		String headerValue = "attachment; filename=" + fileName; 
		
		response.setHeader(headerKey, headerValue); 
	}

	public static void exportToPDF(List<User> usersList, HttpServletResponse response) throws IOException {
		UserExporter.setResponseHeader(response, "application/pdf", ".pdf");
		
		Document document = new Document(PageSize.A4); 
		PdfWriter.getInstance(document, response.getOutputStream()); 
		
		document.open(); 
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD); 
		font.setSize(18); 
		font.setColor(Color.BLUE); 
		
		Paragraph paragraph = new Paragraph("List of users", font); 
		paragraph.setAlignment(Paragraph.ALIGN_CENTER); 
		
		document.add(paragraph); 
		
		PdfPTable table = new PdfPTable(6); 
		table.setWidthPercentage(100f); 
		table.setSpacingBefore(10); 
		table.setWidths(new float[] {
				1.2f, 3.5f, 3.0f, 3.0f, 3.0f, 1.7f
		}); 
		
		UserExporter.writeTableHeader(table); 
		UserExporter.writeTableData(table, usersList); 
		
		document.add(table); 
		
		document.close();
	}

	private static void writeTableData(PdfPTable table, List<User> usersList) {
		for(User user : usersList) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(user.getEmail());
			table.addCell(user.getFirstName());
			table.addCell(user.getLastName());
			table.addCell(user.getRoles().toString());
			table.addCell(String.valueOf(user.isEnabled()));
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
		
		cell.setPhrase(new Phrase("E-mail", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("First Name", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Last Name", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Roles", font)); 
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Enabled", font)); 
		table.addCell(cell);
	}
}