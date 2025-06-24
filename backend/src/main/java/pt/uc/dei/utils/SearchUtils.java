package pt.uc.dei.utils;

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

    public static boolean isNotBlank(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isQuoted(String input) {
        return input != null && input.length() >= 2 && input.startsWith("\"") && input.endsWith("\"");
    }

    public static String stripQuotes(String input) {
        return isQuoted(input) ? input.substring(1, input.length() - 1) : input;
    }
}
