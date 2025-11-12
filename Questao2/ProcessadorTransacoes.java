package Questao2;

/**
 * Interface moderna que define o contrato de autorização de transações.
 * Mantemos este contrato enxuto para aplicar SRP/ISP, isolando o domínio atual
 * dos detalhes do legado.
 */
public interface ProcessadorTransacoes {

    Autorizacao autorizar(String cartao, double valor, String moeda);
}

