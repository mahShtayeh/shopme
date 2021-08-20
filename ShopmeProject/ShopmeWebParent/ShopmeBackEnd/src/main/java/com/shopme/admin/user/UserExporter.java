package com.shopme.admin.user;

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
}