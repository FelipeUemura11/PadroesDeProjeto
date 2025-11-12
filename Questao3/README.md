# Questão 3

**Contexto resumido:** um controlador precisa validar mudanças de estado da usina com base em leituras de sensores, gatilhos (manual/automático) e modo de manutenção.

**Como o código foi organizado?**
1. `NuclearPlantController` registra todas as transições possíveis em uma estrutura `transitionMap` e mantém o histórico recente de estados para detectar loops.
2. Cada transição é modelada como `TransitionRule`, que conhece o destino, o gatilho aceito e a validação necessária (`TransitionValidator`).
3. Leituras de sensores são agrupadas em `SensorSnapshot`, criado via Builder para montar somente os campos relevantes em cada cenário.

**Padrões aplicados e justificativa:**
- State (abordagem baseada em regras): dá flexibilidade para anexar lógicas diferentes a cada par estado/gatilho. Fica prático estender novos estados sem tocar no fluxo principal.
- Builder em `SensorSnapshot`: evita construtores com muitos parâmetros opcionais e deixa explícito quais leituras foram informadas para determinada transição.

**Relação com SOLID:**
- SRP: controlador orquestra, regras validam, snapshot só agrupa dados sensores.
- OCP: adicionar um estado ou transição envolve registrar uma nova `TransitionRule`; nada existente precisa ser alterado.
- LSP/ISP: `TransitionValidator` é um contrato único (`isAllowed`) com implementações intercambiáveis.
- DIP: o controlador depende das interfaces (`TransitionValidator`) em vez de regras concretas, permitindo injeção de novos comportamentos.