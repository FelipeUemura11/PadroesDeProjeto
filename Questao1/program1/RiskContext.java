package program1;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class RiskContext {
    private final Map<String, Double> parametros = new HashMap<>();

    void setParameter(String nome, double valor) {
        parametros.put(nome, valor);
    }

    double getDouble(String nome, double valorPadrao) {
        return parametros.getOrDefault(nome, valorPadrao);
    }

    Map<String, Double> viewParameters() {
        return Collections.unmodifiableMap(parametros);
    }
}
