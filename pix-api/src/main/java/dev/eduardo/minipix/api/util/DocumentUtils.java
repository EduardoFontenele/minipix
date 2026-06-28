package dev.eduardo.minipix.api.util;

public final class DocumentUtils {

    private DocumentUtils() {}

    public static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return "***.***.***-**";
        return "***.***.%s-%s".formatted(cpf.substring(6, 9), cpf.substring(9));
    }
}
