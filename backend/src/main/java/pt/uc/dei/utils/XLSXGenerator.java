package pt.uc.dei.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.enums.Language;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class XLSXGenerator {
    /**
     * Generates an XLSX file from a list of UserDTOs, with translated headers and dynamic columns.
     *
     * @param users   List of UserDTOs to export
     * @param lang    Language for header translation
     * @param isAdmin Whether the export is performed by an admin (affects columns)
     * @return XLSX as byte array
     */
    public static byte[] generateUserXLSX(List<UserDTO> users, Language lang, boolean isAdmin) {
        String[] headers;
        boolean includeAccountState = isAdmin;
        boolean isPortuguese = lang == Language.PORTUGUESE;
        String noManager = isPortuguese ? "Sem gestor" : "No manager";
        if (isPortuguese) {
            headers = includeAccountState
                    ? new String[]{"ID", "Email", "Nome", "Apelido", "Contacto telefónico", "Escritório", "Cargo", "Estado de conta", "Gestor"}
                    : new String[]{"ID", "Email", "Nome", "Apelido", "Contacto telefónico", "Escritório", "Cargo", "Gestor"};
        } else {
            headers = includeAccountState
                    ? new String[]{"ID", "Email", "Name", "Surname", "Phone", "Office", "Role", "Account state", "Manager"}
                    : new String[]{"ID", "Email", "Name", "Surname", "Phone", "Office", "Role", "Manager"};
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Try to use Istok Web and Catamaran fonts if available, else fallback
            String headerFontName = "Istok Web";
            String dataFontName = "Catamaran";

            // PDF color scheme
            short headerBgColor = IndexedColors.RED.getIndex(); // fallback if custom not available
            short altRowBgColor = IndexedColors.GREY_25_PERCENT.getIndex(); // fallback
            // Custom colors (as close as possible)
            XSSFColor titleColor = new XSSFColor(new java.awt.Color(156, 47, 49), null); // #9C2F31
            XSSFColor subtitleColor = new XSSFColor(new java.awt.Color(66, 67, 89), null); // #424359
            XSSFColor accentColor = new XSSFColor(new java.awt.Color(136, 144, 159), null); // #88909F

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setFontName(headerFontName);
            headerStyle.setFont(headerFont);
            if (headerStyle instanceof org.apache.poi.xssf.usermodel.XSSFCellStyle) {
                ((org.apache.poi.xssf.usermodel.XSSFCellStyle) headerStyle).setFillForegroundColor(titleColor);
            } else {
                headerStyle.setFillForegroundColor(headerBgColor);
            }
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 12);
            dataFont.setFontName(dataFontName);
            dataStyle.setFont(dataFont);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Create alternate row style (use subtitle color as background)
            CellStyle altDataStyle = workbook.createCellStyle();
            altDataStyle.cloneStyleFrom(dataStyle);
            if (altDataStyle instanceof org.apache.poi.xssf.usermodel.XSSFCellStyle) {
                ((org.apache.poi.xssf.usermodel.XSSFCellStyle) altDataStyle).setFillForegroundColor(accentColor);
            } else {
                altDataStyle.setFillForegroundColor(altRowBgColor);
            }
            altDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            // Data rows
            int rowIdx = 1;
            for (UserDTO user : users) {
                List<Object> row = new ArrayList<>();
                row.add(user.getId());
                row.add(user.getEmail());
                row.add(user.getName());
                row.add(user.getSurname());
                row.add(user.getPhone());
                row.add(user.getOffice() != null ? user.getOffice().name() : "");
                row.add(user.getRole() != null ? user.getRole().name() : "");
                if (includeAccountState) {
                    row.add(user.getAccountState() != null ? user.getAccountState().name() : "");
                }
                String managerName = (user.getManager() != null && (user.getManager().getName() != null || user.getManager().getSurname() != null))
                        ? (user.getManager().getName() + " " + user.getManager().getSurname()) : noManager;
                row.add(managerName);
                Row dataRow = sheet.createRow(rowIdx);
                CellStyle rowStyle = (rowIdx % 2 == 0) ? altDataStyle : dataStyle;
                for (int i = 0; i < row.size(); i++) {
                    Cell cell = dataRow.createCell(i);
                    Object value = row.get(i);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                    cell.setCellStyle(rowStyle);
                }
                rowIdx++;
            }
            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate user XLSX", e);
        }
    }
}
