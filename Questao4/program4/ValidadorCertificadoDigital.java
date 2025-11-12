import java.time.Clock;
import java.time.LocalDateTime;

public class ValidadorCertificadoDigital implements ValidadorDocumento {

    @Override
    public String nome() {
        return "Validador de Certificado Digital";
    }

    @Override
    public ResultadoValidacaoIndividual validar(DocumentoFiscal documento, ContextoValidacao contexto) {
        CertificadoDigital certificado = documento.getCertificadoDigital();
        if (certificado == null) {
            return ResultadoValidacaoIndividual.falha("Documento sem certificado digital associado");
        }

        Clock relogio = contexto.getRelogio();
        LocalDateTime agora = LocalDateTime.now(relogio);

        if (certificado.estaExpirado(agora)) {
            return ResultadoValidacaoIndividual.falha("Certificado digital expirado em " + certificado.getDataExpiracao());
        }

        if (certificado.isRevogado()) {
            return ResultadoValidacaoIndividual.falha("Certificado digital revogado para o titular " + certificado.getTitular());
        }

        return ResultadoValidacaoIndividual.sucesso("Certificado em conformidade");
    }
}


