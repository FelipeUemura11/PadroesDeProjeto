public class ResultadoValidacaoIndividual {
    private final boolean valido;
    private final String mensagem;

    private ResultadoValidacaoIndividual(boolean valido, String mensagem) {
        this.valido = valido;
        this.mensagem = mensagem;
    }

    public static ResultadoValidacaoIndividual sucesso(String mensagem) {
        return new ResultadoValidacaoIndividual(true, mensagem);
    }

    public static ResultadoValidacaoIndividual falha(String mensagem) {
        return new ResultadoValidacaoIndividual(false, mensagem);
    }

    public boolean isValido() {
        return valido;
    }

    public String getMensagem() {
        return mensagem;
    }
}


