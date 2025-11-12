import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DocumentoFiscal {

    private final String numero;
    private final String xmlConteudo;

    private final BigDecimal valorProdutos;
    private final BigDecimal aliquotaImposto;
    private final BigDecimal impostoCalculado;
    
    private final LocalDateTime dataEmissao;
    private final CertificadoDigital certificadoDigital;

    public DocumentoFiscal(String numero,
                           String xmlConteudo,
                           BigDecimal valorProdutos,
                           BigDecimal aliquotaImposto,
                           BigDecimal impostoCalculado,
                           LocalDateTime dataEmissao,
                           CertificadoDigital certificadoDigital) {
        this.numero = numero;
        this.xmlConteudo = xmlConteudo;
        this.valorProdutos = valorProdutos;
        this.aliquotaImposto = aliquotaImposto;
        this.impostoCalculado = impostoCalculado;
        this.dataEmissao = dataEmissao;
        this.certificadoDigital = certificadoDigital;
    }

    public String getNumero() {
        return numero;
    }

    public String getXmlConteudo() {
        return xmlConteudo;
    }

    public BigDecimal getValorProdutos() {
        return valorProdutos;
    }

    public BigDecimal getAliquotaImposto() {
        return aliquotaImposto;
    }

    public BigDecimal getImpostoCalculado() {
        return impostoCalculado;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public CertificadoDigital getCertificadoDigital() {
        return certificadoDigital;
    }

    public BigDecimal calcularImpostoEsperado() {
        return valorProdutos.multiply(aliquotaImposto)
                .setScale(2, java.math.RoundingMode.HALF_EVEN);
    }
}


