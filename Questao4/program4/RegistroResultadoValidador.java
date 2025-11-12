public class RegistroResultadoValidador {
    private final String nomeValidador;
    private final StatusValidacaoEtapa status;
    private final String mensagem;

    public RegistroResultadoValidador(String nomeValidador, StatusValidacaoEtapa status, String mensagem) {
        this.nomeValidador = nomeValidador;
        this.status = status;
        this.mensagem = mensagem;
    }

    public String getNomeValidador() {
        return nomeValidador;
    }

    public StatusValidacaoEtapa getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return "RegistroResultadoValidador{" +
                "nomeValidador='" + nomeValidador + '\'' +
                ", status=" + status +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}


