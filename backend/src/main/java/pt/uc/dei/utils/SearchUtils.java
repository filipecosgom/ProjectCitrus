package pt.uc.dei.utils;

/**
 * Utility class for string normalization and search-related helpers.
 * Provides methods to normalize strings, check for blank or quoted strings, and strip quotes.
 */
public class SearchUtils {
    /**
     * Remove caracteres especiais de uma string, substituindo letras acentuadas e outros caracteres especiais
     * com os seus equivalentes não acentuados.
     *
     * @param string a string de entrada que contém caracteres especiais
     * @return uma string com caracteres especiais substituídos pelos seus equivalentes não acentuados
     */
    public static String normalizeString (String string){
        String result = string.replaceAll("[áàãâÁÀÃÃåÅåÄä]","a");
        result = result.replaceAll("[éèêÉÈÊËëĔĕ]","e");
        result = result.replaceAll("[íìîÍÌÎÏïÎîÌì]","i");
        result = result.replaceAll("[óòõôÒÓÕÔÖöÔôŌōØø]","o");
        result = result.replaceAll("[úùûÙÚÛÜüŪūÛûÙùÚú]","u");
        result = result.replaceAll("[çÇČč]","c");
        result = result.replaceAll("[ĞğĢģ]","g");
        result = result.replaceAll("[Ññ]","c");
        result = result.replaceAll("[ŜŝŞş]","s");
        result = result.replaceAll("[Ÿÿ]","y");
        return result;
    }

    /**
     * Checks if the input string is not blank (not null and not empty after trimming).
     *
     * @param input the string to check
     * @return true if the string is not blank, false otherwise
     */
    public static boolean isNotBlank(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Determines if the input string is surrounded by double quotes.
     *
     * @param input the string to check
     * @return true if the string starts and ends with double quotes, false otherwise
     */
    public static boolean isQuoted(String input) {
        return input != null && input.length() >= 2 && input.startsWith("\"") && input.endsWith("\"");
    }

    /**
     * Removes surrounding double quotes from the input string if present.
     *
     * @param input the string to strip quotes from
     * @return the string without surrounding double quotes, or the original string if not quoted
     */
    public static String stripQuotes(String input) {
        return isQuoted(input) ? input.substring(1, input.length() - 1) : input;
    }
}
