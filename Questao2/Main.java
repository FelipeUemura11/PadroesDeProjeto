package Questao2;

/**
 * Exemplo de uso do adapter, seguindo o estilo da Questao1.
 */
public class Main {

    public static void main(String[] args) {
        SistemaBancarioLegado legado = new SistemaBancarioLegado();
        ProcessadorTransacoes processador = new ProcessadorTransacoesLegadoAdapter(legado);

        Autorizacao autorizacao = processador.autorizar("4111111111111111", 150.0, "BRL");

        System.out.println(">>> Integracao com sistema legado bancario <<<");
        System.out.println("Autorizada: " + autorizacao.autorizada());
        System.out.println("Mensagem: " + autorizacao.mensagem());
        System.out.println("Valor: " + autorizacao.valor());
        System.out.println("Moeda: " + autorizacao.moeda());
    }
}

