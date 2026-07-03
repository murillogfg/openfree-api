# OpenFree — State Machines v1.0

## Objetivo

Definir os estados principais das entidades da OpenFree e quais transições são permitidas.

---

## Vaga

### Estados

- RASCUNHO
- PUBLICADA
- EM_ANDAMENTO
- FINALIZADA
- CANCELADA
- ARQUIVADA

### Fluxo

```mermaid
stateDiagram-v2
    [*] --> RASCUNHO
    RASCUNHO --> PUBLICADA
    PUBLICADA --> EM_ANDAMENTO
    PUBLICADA --> CANCELADA
    EM_ANDAMENTO --> FINALIZADA
    FINALIZADA --> ARQUIVADA
    CANCELADA --> ARQUIVADA
```

---

## Candidatura

### Estados

- PENDENTE
- VISUALIZADA
- ACEITA
- RECUSADA
- CANCELADA

### Fluxo

```mermaid
stateDiagram-v2
    [*] --> PENDENTE
    PENDENTE --> VISUALIZADA
    VISUALIZADA --> ACEITA
    VISUALIZADA --> RECUSADA
    PENDENTE --> CANCELADA
    VISUALIZADA --> CANCELADA
```

---

## Empresa

### Estados

- PENDENTE
- EM_ANALISE
- VERIFICADA
- REPROVADA
- SUSPENSA
- INATIVA

### Fluxo

```mermaid
stateDiagram-v2
    [*] --> PENDENTE
    PENDENTE --> EM_ANALISE
    EM_ANALISE --> VERIFICADA
    EM_ANALISE --> REPROVADA
    VERIFICADA --> SUSPENSA
    VERIFICADA --> INATIVA
    SUSPENSA --> VERIFICADA
```

---

## Contrato

### Estados

- CRIADO
- EM_EXECUCAO
- CONCLUIDO
- CANCELADO
- AVALIADO

### Fluxo

```mermaid
stateDiagram-v2
    [*] --> CRIADO
    CRIADO --> EM_EXECUCAO
    CRIADO --> CANCELADO
    EM_EXECUCAO --> CONCLUIDO
    EM_EXECUCAO --> CANCELADO
    CONCLUIDO --> AVALIADO
```

---

## Regra geral

Nenhum status deve ser alterado diretamente sem passar por uma regra de negócio.

Exemplo errado:

```java
vaga.setStatus("FINALIZADA");
```

Exemplo correto:

```java
vagaService.finalizarVaga(id);
```
