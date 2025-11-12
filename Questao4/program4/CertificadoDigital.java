import java.time.LocalDateTime;

public class CertificadoDigital {
    
    private final String titular;
    private final LocalDateTime dataExpiracao;
    private final boolean revogado;

    public CertificadoDigital(String titular, LocalDateTime dataExpiracao, boolean revogado) {
        this.titular = titular;
        this.dataExpiracao = dataExpiracao;
        this.revogado = revogado;
    }

    public String getTitular() {
        return titular;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public boolean isRevogado() {
        return revogado;
    }

    public boolean estaExpirado(LocalDateTime referencia) {
        return referencia.isAfter(dataExpiracao);
    }
}


