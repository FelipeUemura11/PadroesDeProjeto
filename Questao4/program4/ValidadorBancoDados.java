public class ValidadorBancoDados implements ValidadorDocumento, ValidadorComRollback {

    @Override
    public String nome() {
        return "Validador de Banco de Dados";
    }

    @Override
    public ResultadoValidacaoIndividual validar(DocumentoFiscal documento, ContextoValidacao contexto) {
        RepositorioDocumentosFiscais repositorio = contexto.getRepositorio();
        if (repositorio.existeNumero(documento.getNumero())) {
            return ResultadoValidacaoIndividual.falha("Numero de documento ja registrado no banco");
        }

        repositorio.registrarNumero(documento.getNumero());
        return ResultadoValidacaoIndividual.sucesso("Documento registrado para verificacao de duplicidade");
    }

    @Override
    public void desfazer(DocumentoFiscal documento, ContextoValidacao contexto) {
        contexto.getRepositorio().removerNumero(documento.getNumero());
    }
}


