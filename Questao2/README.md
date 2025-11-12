# Questão 2

**Problema:** conversar com uma API legada que exige payloads específicos, sem poluir o contrato moderno de autorização de transações.

**Solução adotada:** a aplicação fala sempre com a interface `ProcessadorTransacoes`. Somente o adapter (`ProcessadorTransacoesLegadoAdapter`) conhece o `SistemaBancarioLegado` e as transformaçōes necessárias. Os mapeadores (`SolicitacaoLegadaMapper`, `RespostaLegadaMapper`) tratam de converter dados para/desde o legado e o `ConversorMoeda` guarda a tabela de códigos.

**Padrão selecionado:** Adapter. Ele cria a ponte entre o modelo atual e o legado, preservando o contrato moderno (`ProcessadorTransacoes`) e evitando dependência direta da camada de negócio com campos legados obrigatórios.

**Impacto em SOLID:**
- SRP: cada classe tem responsabilidade única (adapter, mapeadores, conversor, exceptions).
- OCP: ampliar moedas ou regras de tradução ocorre adicionando mapeamentos sem tocar no adapter.
- LSP: qualquer implementação de `ProcessadorTransacoes` pode substituir o adapter sem quebrar o cliente.
- ISP: a interface expõe apenas `autorizar`, mantendo o domínio atual simples.
- DIP: camadas externas recebem `ProcessadorTransacoes`; a dependência concreta do legado fica encapsulada.