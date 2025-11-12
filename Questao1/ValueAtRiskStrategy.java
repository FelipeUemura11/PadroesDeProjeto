public class ValueAtRiskStrategy implements RiskStrategy {
    @Override
    public String calculate(RiskContext contexto) {
        double valorCarteira = contexto.getDouble("valorCarteira", 0.0);
        double nivelConfianca = contexto.getDouble("nivelConfianca", 0.95);
        double volatilidadeMercado = contexto.getDouble("volatilidadeMercado", 0.2);

        double resultadoSimulado = valorCarteira * volatilidadeMercado * (1 - nivelConfianca);
        return String.format("Value at Risk estimado: %.2f", resultadoSimulado);
    }
}