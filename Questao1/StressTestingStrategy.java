public class StressTestingStrategy implements RiskStrategy {
    @Override
    public String calculate(RiskContext contexto) {
        double valorCarteira = contexto.getDouble("valorCarteira", 0.0);
        double quedaPrecoStress = contexto.getDouble("quedaPrecoStress", 0.3);

        double resultadoSimulado = valorCarteira * quedaPrecoStress;
        return String.format("Stress Testing estimado: %.2f", resultadoSimulado);
    }
}