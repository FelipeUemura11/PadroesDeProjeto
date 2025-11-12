package program1;
public class Main {
    public static void main(String[] args) {
        RiskContext contexto = new RiskContext();
        contexto.setParameter("valorCarteira", 10_000_000.00);
        contexto.setParameter("nivelConfianca", 0.99);
        contexto.setParameter("volatilidadeMercado", 0.25);
        contexto.setParameter("fatorPerdaCauda", 1.7);
        contexto.setParameter("quedaPrecoStress", 0.35);

        RiskCalculator calculadora = new RiskCalculator(new ValueAtRiskStrategy(), contexto);

        System.out.println(">>> Processamento de Risco Dinamico <<<");
        System.out.println("Parametros atuais: " + calculadora.getContext().viewParameters());

        System.out.println(calculadora.execute());

        calculadora.setStrategy(new ExpectedShortfallStrategy());
        System.out.println(calculadora.execute());

        calculadora.setStrategy(new StressTestingStrategy());
        System.out.println(calculadora.execute());
    }
}