public interface ValidadorDocumento {
    String nome();
    ResultadoValidacaoIndividual validar(DocumentoFiscal documento, ContextoValidacao contexto) throws Exception;
}


