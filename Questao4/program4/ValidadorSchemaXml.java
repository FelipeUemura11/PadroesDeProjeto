import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.xml.sax.SAXException;

public class ValidadorSchemaXml implements ValidadorDocumento {

    private final String xsdConteudo;

    public ValidadorSchemaXml(String xsdConteudo) {
        this.xsdConteudo = xsdConteudo;
    }

    @Override
    public String nome() {
        return "Validador de Schema XML";
    }

    @Override
    public ResultadoValidacaoIndividual validar(DocumentoFiscal documento, ContextoValidacao contexto) {

        try {
            validarEstruturaXml(documento.getXmlConteudo());
            return ResultadoValidacaoIndividual.sucesso("XML valido conforme XSD fornecido");
        } catch (SAXException ex) {
            return ResultadoValidacaoIndividual.falha("Estrutura XML invalida: " + ex.getMessage());
        } catch (Exception ex) {
            return ResultadoValidacaoIndividual.falha("Falha ao validar XML: " + ex.getMessage());
        }
    }

    private void validarEstruturaXml(String xmlConteudo) throws Exception {

        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setNamespaceAware(true);
        fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        fabrica.setExpandEntityReferences(false);

        DocumentBuilder construtor = fabrica.newDocumentBuilder();
        construtor.parse(new ByteArrayInputStream(xmlConteudo.getBytes(StandardCharsets.UTF_8)));

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new javax.xml.transform.stream.StreamSource(
                new ByteArrayInputStream(xsdConteudo.getBytes(StandardCharsets.UTF_8))));
                
        Validator validator = schema.newValidator();
        validator.validate(new javax.xml.transform.stream.StreamSource(
                new ByteArrayInputStream(xmlConteudo.getBytes(StandardCharsets.UTF_8))));
    }
}


