package pt.uc.dei.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.enums.Language;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVGenerator {
    /**
     * Generates a CSV file from a list of UserDTOs, with translated headers and dynamic columns.
     *
     * @param users   List of UserDTOs to export
     * @param lang    Language for header translation
     * @param isAdmin Whether the export is performed by an admin (affects columns)
     * @return CSV as byte array
     */
    public static byte[] generateUserCSV(List<UserDTO> users, Language lang, boolean isAdmin) {
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
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
             baos.write(0xEF);
             baos.write(0xBB);
             baos.write(0xBF);
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(baos, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.withHeader(headers));
            for (UserDTO user : users) {
                // Build row according to language and admin
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
                // Manager column: show manager's name if available, else blank
                String managerName = (user.getManager() != null && (user.getManager().getName() != null || user.getManager().getSurname() != null))
                        ? (user.getManager().getName() + " " + user.getManager().getSurname()) : noManager;
                row.add(managerName);
                csvPrinter.printRecord(row);
            }
            csvPrinter.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate user CSV", e);
        }
    }
}
