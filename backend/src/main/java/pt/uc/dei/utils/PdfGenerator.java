package pt.uc.dei.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import pt.uc.dei.dtos.AppraisalResponseDTO;
import pt.uc.dei.enums.Language;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PdfGenerator {

    // Color scheme
    private static final Color TITLE_COLOR = new Color(156, 47, 49);    // #9C2F31
    private static final Color SUBTITLE_COLOR = new Color(66, 67, 89);  // #424359
    private static final Color ACCENT_COLOR = new Color(136, 144, 159); // #88909F
    private static final Color TEXT_COLOR = Color.BLACK;

    // Layout constants
    private static final float MARGIN = 50;
    private static final float LOGO_SCALE = 0.08f;
    private static final int LINE_HEIGHT = 15;
    private static final int FEEDBACK_INDENT = 10;

    // Table column widths (Date, Appraised, Manager, Score)
    private static final float[] COLUMN_WIDTHS = { 100, 120, 120, 210 }; // Feedback not a column

    static {
        // Configure PDFBox to avoid memory mapping issues
        System.setProperty("org.apache.pdfbox.baseParser.pushBackSize", "1000000");
        System.setProperty("org.apache.pdfbox.rendering.UsePureJavaCMYKConversion", "true");
        System.setProperty("org.apache.pdfbox.usePureJavaCMYK", "true");
        System.setProperty("org.apache.pdfbox.forcePureJavaIO", "true");
    }

    // Helper for label translation
    private static String t(String key, Language lang) {
        switch (key) {
            case "date": return lang == Language.PORTUGUESE ? "Data" : "Date";
            case "appraised": return lang == Language.PORTUGUESE ? "Avaliado" : "Appraised";
            case "manager": return "Manager";
            case "score": return "Score";
            case "feedback": return "Feedback";
            case "summary": return lang == Language.PORTUGUESE ? "Resumo" : "Summary";
            case "total": return lang == Language.PORTUGUESE ? "Avaliações totais" : "Total Appraisals";
            case "average": return lang == Language.PORTUGUESE ? "Média" : "Average Score";
            case "report": return lang == Language.PORTUGUESE ? "Relatório gerado" : "Report generated";
            default: return key;
        }
    }

    public static void generateAppraisalsPdf(List<AppraisalResponseDTO> appraisals,
                                             OutputStream outputStream,
                                             Language lang) throws IOException {
        PDDocument document = new PDDocument();
        PDFont[] fonts = loadFonts(document);
        PDImageXObject logo = loadLogo(document);

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = null;
        int yPosition = (int) (PDRectangle.A4.getHeight() - MARGIN);

        try {
            // Draw logo if available
            if (logo != null) {
                try (PDPageContentStream logoStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    float logoHeight = logo.getHeight() * LOGO_SCALE;
                    float logoWidth = logo.getWidth() * LOGO_SCALE;
                    float xLogo = (float) PDRectangle.A4.getWidth() - MARGIN - logoWidth;
                    float yLogo = yPosition - logoHeight;
                    logoStream.drawImage(logo, xLogo, yLogo, logoWidth, logoHeight);
                    // Only move yPosition down if you want to avoid overlap with header
                    yPosition -= logoHeight + 20;
                }
            }

            contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

            int pageNum = 1;
            yPosition = renderHeader(contentStream, fonts, yPosition, lang);
            yPosition = renderSummary(contentStream, fonts, appraisals, yPosition, lang);
            yPosition = renderTableHeader(contentStream, fonts, yPosition, lang);

            for (AppraisalResponseDTO appraisal : appraisals) {
                if (yPosition < 100) { // New page needed
                    renderFooter(contentStream, fonts[1], pageNum); // Render footer before closing
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
                    pageNum++;
                    yPosition = (int) (PDRectangle.A4.getHeight() - MARGIN);

                    yPosition = renderTableHeader(contentStream, fonts, yPosition, lang);
                }

                String date = appraisal.getSubmissionDate() != null ? appraisal.getSubmissionDate().toString() : "N/A";
                String employee = appraisal.getAppraisedUser().getName() + " " + appraisal.getAppraisedUser().getSurname();
                String manager = appraisal.getAppraisingUser().getName() + " " + appraisal.getAppraisingUser().getSurname();
                String scoreDisplay = getScoreDisplay(appraisal, lang);
                String feedback = appraisal.getFeedback() != null ? appraisal.getFeedback() : "No feedback provided";

                float x = MARGIN;
                String[] values = { date, employee, manager, scoreDisplay };
                for (int i = 0; i < values.length; i++) {
                    drawText(contentStream, values[i], fonts[1], 10, TEXT_COLOR, x, yPosition);
                    x += COLUMN_WIDTHS[i];
                }
                yPosition -= LINE_HEIGHT;

                // Feedback (multi-line, indented, with label)
                String[] feedbackLines = splitStringIntoLines(feedback, 90); // wider lines
                float feedbackX = MARGIN + FEEDBACK_INDENT;
                int feedbackY = yPosition;
                drawText(contentStream, t("feedback", lang) + ":", fonts[2], 10, ACCENT_COLOR, feedbackX, feedbackY);
                feedbackY -= LINE_HEIGHT;
                for (String line : feedbackLines) {
                    drawText(contentStream, line, fonts[1], 10, TEXT_COLOR, feedbackX + 20, feedbackY);
                    feedbackY -= LINE_HEIGHT;
                }
                yPosition = feedbackY;

                // Draw a separator line under each row+feedback
                float rowLineLength = (float) PDRectangle.A4.getWidth() - 2 * MARGIN;
                drawLine(contentStream, ACCENT_COLOR, MARGIN, yPosition + 5, rowLineLength, 0.2f);
                yPosition -= 5;
            }

            // Render footer for the last page
            renderFooter(contentStream, fonts[1], pageNum);

            contentStream.close();
            document.save(outputStream);

        } finally {
            if (contentStream != null) {
                try { contentStream.close(); } catch (IOException e) { /* Ignore */ }
            }
            document.close();
        }
    }

    private static PDFont[] loadFonts(PDDocument document) throws IOException {
        PDFont[] fonts = new PDFont[3];
        try {
            try (InputStream boldFontStream = PdfGenerator.class.getResourceAsStream("/fonts/IstokWeb-Bold.ttf");
                 InputStream regularFontStream = PdfGenerator.class.getResourceAsStream("/fonts/Catamaran-Regular.ttf");
                 InputStream semiBoldFontStream = PdfGenerator.class.getResourceAsStream("/fonts/Catamaran-SemiBold.ttf")) {

                if (boldFontStream != null) fonts[0] = PDType0Font.load(document, boldFontStream);
                if (regularFontStream != null) fonts[1] = PDType0Font.load(document, regularFontStream);
                if (semiBoldFontStream != null) fonts[2] = PDType0Font.load(document, semiBoldFontStream);
            }
        } catch (IOException e) {
            fonts[0] = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            fonts[1] = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            fonts[2] = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        }
        return fonts;
    }

    private static PDImageXObject loadLogo(PDDocument document) throws IOException {
        try (InputStream logoStream = PdfGenerator.class.getResourceAsStream("/logo/logo.png")) {
            if (logoStream != null) {
                return PDImageXObject.createFromByteArray(document,
                        logoStream.readAllBytes(), "logo");
            }
        }
        return null;
    }

    private static int renderHeader(PDPageContentStream cs, PDFont[] fonts, int yPosition, Language lang) throws IOException {
        drawText(cs, "EMPLOYEE PERFORMANCE APPRAISALS", fonts[0], 22,
                TITLE_COLOR, MARGIN, yPosition);
        yPosition -= 30;

        String formattedDate = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        drawText(cs, t("report", lang) + ": " + formattedDate, fonts[1], 10,
                ACCENT_COLOR, MARGIN, yPosition);
        yPosition -= 40;

        return yPosition;
    }

    private static int renderSummary(PDPageContentStream cs, PDFont[] fonts,
                                     List<AppraisalResponseDTO> appraisals,
                                     int yPosition, Language lang) throws IOException {
        drawText(cs, t("summary", lang).toUpperCase(), fonts[2], 12, SUBTITLE_COLOR, MARGIN, yPosition);
        drawLine(cs, SUBTITLE_COLOR, MARGIN, yPosition - 5, 100, 0.5f);
        yPosition -= 25;

        drawText(cs, t("total", lang) + ": " + appraisals.size(), fonts[1], 10,
                TEXT_COLOR, MARGIN, yPosition);
        yPosition -= LINE_HEIGHT;

        if (!appraisals.isEmpty()) {
            double avgScore = appraisals.stream()
                    .filter(a -> a.getScore() != null && a.getScore() > 0)
                    .mapToInt(AppraisalResponseDTO::getScore)
                    .average()
                    .orElse(0.0);

            drawText(cs, String.format("%s: %.1f", t("average", lang), avgScore),
                    fonts[1], 10, TEXT_COLOR, MARGIN, yPosition);
            yPosition -= LINE_HEIGHT;
        }

        return yPosition - 20;
    }

    private static int renderTableHeader(PDPageContentStream cs, PDFont[] fonts, int yPosition, Language lang) throws IOException {
        float x = MARGIN;
        String[] headers = { t("date", lang), t("appraised", lang), t("manager", lang), t("score", lang) };
        for (int i = 0; i < headers.length; i++) {
            drawText(cs, headers[i], fonts[2], 11, SUBTITLE_COLOR, x, yPosition);
            x += COLUMN_WIDTHS[i];
        }
        yPosition -= LINE_HEIGHT;
        // Draw line from left margin to right margin
        float lineLength = (float) PDRectangle.A4.getWidth() - 2 * MARGIN;
        drawLine(cs, ACCENT_COLOR, MARGIN, yPosition + 5, lineLength, 0.5f);
        yPosition -= 5;
        return yPosition;
    }

    private static String getScoreDisplay(AppraisalResponseDTO appraisal, Language lang) {
        Integer scoreNum = appraisal.getScore();
        String scoreDesc = pt.uc.dei.enums.ScoreDescription.fromScore(scoreNum)
                .getDescription(lang);
        return (scoreNum != null && scoreNum > 0) ?
                scoreNum + " - " + scoreDesc : scoreDesc;
    }

    private static void renderFooter(PDPageContentStream cs, PDFont font,
                                     int pageNum) throws IOException {
        // Only the number, no label
        drawText(cs, Integer.toString(pageNum), font, 8,
                ACCENT_COLOR, PDRectangle.A4.getWidth() - MARGIN - 10, 30);
    }

    private static String[] splitStringIntoLines(String text, int maxLineLength) {
        if (text == null || text.isEmpty()) {
            return new String[]{"No feedback provided"};
        }

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : text.split("\\s+")) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }
        lines.add(currentLine.toString());

        return lines.toArray(new String[0]);
    }

    private static void drawText(PDPageContentStream cs, String text,
                                 PDFont font, float fontSize,
                                 Color color, float x, float y) throws IOException {
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static void drawLine(PDPageContentStream cs, Color color,
                                 float startX, float startY,
                                 float length, float width) throws IOException {
        cs.setLineWidth(width);
        cs.setStrokingColor(color);
        cs.moveTo(startX, startY);
        cs.lineTo(startX + length, startY);
        cs.stroke();
    }

    private static float sum(float[] arr) {
        float s = 0;
        for (float v : arr) s += v;
        return s;
    }
}