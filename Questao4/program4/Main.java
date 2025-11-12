import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        CertificadoDigital certificadoValido = new CertificadoDigital(
                "Empresa Exemplo LTDA",
                LocalDateTime.now(Clock.systemUTC()).plusDays(30),
                false
        );

        DocumentoFiscal documentoFiscal = new DocumentoFiscal(
                "NFE12345A",
                gerarXmlSimples(),
                new BigDecimal("1000.00"),
                new BigDecimal("0.18"),
                new BigDecimal("180.00"),
                LocalDateTime.now(Clock.systemUTC()),
                certificadoValido
        );

        ContextoValidacao contexto = new ContextoValidacao(
                Clock.systemUTC(),
                new RepositorioDocumentosFiscais(),
                new ServicoSefazCliente(Duration.ofMillis(300), true)
        );

        CadeiaValidacaoDocumentos cadeia = CadeiaValidacaoDocumentos.builder()
                .adicionarEtapa(new ValidadorSchemaXml(gerarXsdSimples()), Duration.ofSeconds(2))
                .adicionarEtapa(new ValidadorCertificadoDigital(), Duration.ofSeconds(1))
                .adicionarEtapa(new ValidadorRegrasFiscais(), Duration.ofSeconds(1), true)
                .adicionarEtapa(new ValidadorBancoDados(), Duration.ofSeconds(1))
                .adicionarEtapa(new ValidadorServicoSefaz(), Duration.ofSeconds(2), true)
                .pularSeFalhar("Validador de Schema XML", "Validador de Regras Fiscais")
                .pularSeFalhar("Validador de Certificado Digital", "Validador de Regras Fiscais")
                .limiteFalhasCircuitBreaker(3)
                .construir();

        ResultadoCadeiaValidacao resultado = cadeia.executar(documentoFiscal, contexto);
        imprimirResultado("Cenario 1 - Documento valido", resultado);

        DocumentoFiscal documentoComErroSefaz = new DocumentoFiscal(
                "NFE12345B",
                gerarXmlSimples(),
                new BigDecimal("1000.00"),
                new BigDecimal("0.18"),
                new BigDecimal("180.00"),
                LocalDateTime.now(Clock.systemUTC()),
                certificadoValido
        );

        ResultadoCadeiaValidacao resultadoErroSefaz = cadeia.executar(documentoComErroSefaz, contexto);

        imprimirResultado("Cenario 2 - Falha na SEFAZ com rollback", resultadoErroSefaz);

        CertificadoDigital certificadoRevogado = new CertificadoDigital(
                "Empresa Exemplo LTDA",
                LocalDateTime.now(Clock.systemUTC()).minusDays(1),
                true
        );

        DocumentoFiscal documentoCritico = new DocumentoFiscal(
                "NFE12345A",
                gerarXmlInvalido(),
                new BigDecimal("1000.00"),
                new BigDecimal("0.18"),
                new BigDecimal("200.00"),
                LocalDateTime.now(Clock.systemUTC()),
                certificadoRevogado
        );

        ResultadoCadeiaValidacao resultadoCritico = cadeia.executar(documentoCritico, contexto);

        imprimirResultado("Cenario 3 - Circuit breaker acionado e pulos condicionais", resultadoCritico);
    }

    private static void imprimirResultado(String titulo, ResultadoCadeiaValidacao resultado) {

        System.out.println("==== " + titulo + " ====");
        resultado.getRegistros().forEach(r ->
                System.out.println(r.getNomeValidador() + " -> " + r.getStatus() + " (" + r.getMensagem() + ")"));
        System.out.println("Sucesso global: " + resultado.isSucessoGlobal());
        System.out.println("Circuit breaker acionado: " + resultado.isInterrompidoPorCircuitBreaker());
        System.out.println();

    }

    private static String gerarXmlSimples() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<nfe:NFe xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe\">"
                + "<infNFe>"
                + "<ide><cUF>35</cUF></ide>"
                + "<emit><CNPJ>12345678000100</CNPJ></emit>"
                + "<det><imposto><ICMS><ICMS00><vICMS>180.00</vICMS></ICMS00></ICMS></imposto></det>"
                + "</infNFe>"
                + "</nfe:NFe>";
    }

    private static String gerarXsdSimples() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
                + "targetNamespace=\"http://www.portalfiscal.inf.br/nfe\" "
                + "xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe\" "
                + "elementFormDefault=\"qualified\">"
                + "<xs:element name=\"NFe\">"
                + "<xs:complexType>"
                + "<xs:sequence>"
                + "<xs:element name=\"infNFe\" minOccurs=\"1\">"
                + "<xs:complexType>"
                + "<xs:sequence>"
                + "<xs:element name=\"ide\" minOccurs=\"1\" maxOccurs=\"1\"/>"
                + "<xs:element name=\"emit\" minOccurs=\"1\" maxOccurs=\"1\"/>"
                + "<xs:element name=\"det\" minOccurs=\"1\" maxOccurs=\"unbounded\"/>"
                + "</xs:sequence>"
                + "</xs:complexType>"
                + "</xs:element>"
                + "</xs:sequence>"
                + "</xs:complexType>"
                + "</xs:element>"
                + "</xs:schema>";
    }

    private static String gerarXmlInvalido() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<nfe:NFe xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe\">"
                + "<infNFe>"
                + "<ide></ide>"
                + "</infNFe>"
                + "</nfe:NFe>";
    }
}