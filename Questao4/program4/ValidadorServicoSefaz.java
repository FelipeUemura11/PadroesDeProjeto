public class ValidadorServicoSefaz implements ValidadorDocumento {

    @Override
    public String nome() {
        return "Validador de Serviço SEFAZ";
    }

    @Override
    public ResultadoValidacaoIndividual validar(DocumentoFiscal documento, ContextoValidacao contexto) throws Exception {

        boolean autorizado = contexto.getServicoSefazCliente().consultarAutorizacao(documento.getNumero());
        
        if (!autorizado) {
            return ResultadoValidacaoIndividual.falha("SEFAZ nao autorizou o documento " + documento.getNumero());
        }
        return ResultadoValidacaoIndividual.sucesso("SEFAZ confirma autorizacao do documento");
    }
}


