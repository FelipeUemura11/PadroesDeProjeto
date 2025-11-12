public class ExpectedShortfallStrategy implements RiskStrategy {
    @Override
    public String calculate(RiskContext contexto) {
        double valorCarteira = contexto.getDouble("valorCarteira", 0.0);
        double nivelConfianca = contexto.getDouble("nivelConfianca", 0.95);
        double fatorPerdaCauda = contexto.getDouble("fatorPerdaCauda", 1.5);

        double resultadoSimulado = valorCarteira * (1 - nivelConfianca) * fatorPerdaCauda;
        return String.format("Expected Shortfall estimado: %.2f", resultadoSimulado);
    }
}