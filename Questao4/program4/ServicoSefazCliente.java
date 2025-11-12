import java.time.Duration;

public class ServicoSefazCliente {
    private final Duration tempoRespostaSimulado;
    private final boolean disponibilidade;

    public ServicoSefazCliente(Duration tempoRespostaSimulado, boolean disponibilidade) {
        this.tempoRespostaSimulado = tempoRespostaSimulado;
        this.disponibilidade = disponibilidade;
    }

    public boolean consultarAutorizacao(String numeroDocumento) throws InterruptedException {
        Thread.sleep(tempoRespostaSimulado.toMillis());
        return disponibilidade && numeroDocumento.endsWith("A");
    }
}


