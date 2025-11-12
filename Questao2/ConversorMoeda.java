package Questao2;

import java.util.Map;
import java.util.Optional;

/**
 * Conversão isolada de moedas entre o modelo moderno e o legado.
 * Mantido separado para seguir SRP e facilitar extensões (OCP).
 */
final class ConversorMoeda {

    private static final Map<String, Integer> MAPA_MOEDAS = Map.of(
            "USD", 1,
            "EUR", 2,
            "BRL", 3
    );

    private static final Map<Integer, String> MAPA_REVERSO = Map.of(
            1, "USD",
            2, "EUR",
            3, "BRL"
    );

    private ConversorMoeda() {
    }

    static int paraCodigoLegado(String moeda) {
        return Optional.ofNullable(MAPA_MOEDAS.get(moeda))
                .orElseThrow(() -> new MoedaNaoSuportadaException(moeda));
    }

    static String paraMoedaModerna(int codigo) {
        return Optional.ofNullable(MAPA_REVERSO.get(codigo))
                .orElseThrow(() -> new MoedaNaoSuportadaException("Código legado " + codigo));
    }
}

