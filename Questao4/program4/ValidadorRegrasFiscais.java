import java.math.BigDecimal;

public class ValidadorRegrasFiscais implements ValidadorDocumento {

    @Override
    public String nome() {
        return "Validador de Regras Fiscais";
    }

    @Override
    public ResultadoValidacaoIndividual validar(DocumentoFiscal documento, ContextoValidacao contexto) {
        
        BigDecimal esperado = documento.calcularImpostoEsperado();
        BigDecimal informado = documento.getImpostoCalculado();

        if (esperado.subtract(informado).abs().compareTo(new BigDecimal("0.05")) > 0) {
            return ResultadoValidacaoIndividual.falha("Imposto calculado difere do esperado. Esperado: "
                    + esperado + ", informado: " + informado);
        }

        return ResultadoValidacaoIndividual.sucesso("Regras fiscais atendidas com imposto " + informado);
    }
}



