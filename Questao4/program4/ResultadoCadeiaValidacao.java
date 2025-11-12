import java.util.Collections;
import java.util.List;

public class ResultadoCadeiaValidacao {
    private final boolean sucessoGlobal;
    private final boolean interrompidoPorCircuitBreaker;
    private final List<RegistroResultadoValidador> registros;

    public ResultadoCadeiaValidacao(boolean sucessoGlobal,
                                    boolean interrompidoPorCircuitBreaker,
                                    List<RegistroResultadoValidador> registros) {
        this.sucessoGlobal = sucessoGlobal;
        this.interrompidoPorCircuitBreaker = interrompidoPorCircuitBreaker;
        this.registros = Collections.unmodifiableList(registros);
    }

    public boolean isSucessoGlobal() {
        return sucessoGlobal;
    }

    public boolean isInterrompidoPorCircuitBreaker() {
        return interrompidoPorCircuitBreaker;
    }

    public List<RegistroResultadoValidador> getRegistros() {
        return registros;
    }
}


