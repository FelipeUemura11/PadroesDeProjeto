package program1;
public class RiskCalculator {
    
    private RiskStrategy estrategia;
    private final RiskContext contexto;

    RiskCalculator(RiskStrategy estrategia, RiskContext contexto) {
        this.estrategia = estrategia;
        this.contexto = contexto;
    }

    void setStrategy(RiskStrategy estrategia) {
        this.estrategia = estrategia;
    }

    String execute() {
        if (estrategia == null) {
            throw new IllegalStateException("Nenhuma estrategia configurada");
        }
        return estrategia.calculate(contexto);
    }

    RiskContext getContext() {
        return contexto;
    }
}