# OpenFree — Product Backlog v1.0

## Objetivo

Organizar as funcionalidades da OpenFree em épicos, histórias de usuário, prioridades e critérios de aceite.

---

## Épico 1 — Identity

### História 1 — Cadastro de usuário

Como visitante, quero criar uma conta para acessar a plataforma.

Prioridade: Alta  
Sprint: Identity

Critérios de aceite:
- Deve informar e-mail e senha.
- E-mail deve ser único.
- Senha deve ser criptografada.
- Usuário inicia com status ATIVO.

---

### História 2 — Login

Como usuário, quero fazer login para acessar minha conta.

Prioridade: Alta  
Sprint: Identity

Critérios de aceite:
- Login deve validar e-mail e senha.
- Deve retornar token JWT.
- Usuários suspensos não podem acessar.

---

## Épico 2 — Companies

### História 3 — Criar empresa

Como usuário, quero criar uma empresa para publicar vagas.

Prioridade: Alta  
Sprint: Company

Critérios de aceite:
- Deve informar CNPJ.
- CNPJ deve ser único.
- Empresa inicia como PENDENTE.
- Usuário criador vira OWNER.

---

### História 4 — Gerenciar funcionários

Como owner, quero adicionar funcionários à empresa.

Prioridade: Média  
Sprint: Company

Critérios de aceite:
- Apenas OWNER pode adicionar funcionários.
- Funcionário deve receber uma role.
- Funcionário pode ser removido.

---

## Épico 3 — Freelancers

### História 5 — Criar perfil freelancer

Como usuário, quero criar um perfil freelancer para me candidatar a vagas.

Prioridade: Alta  
Sprint: Freelancer

Critérios de aceite:
- Deve informar nome, CPF, telefone e cidade.
- CPF deve ser único.
- Perfil inicia como ATIVO.

---

## Épico 4 — Jobs

### História 6 — Publicar vaga

Como empresa, quero publicar uma vaga para encontrar freelancers.

Prioridade: Alta  
Sprint: Marketplace

Critérios de aceite:
- Apenas empresas verificadas podem publicar.
- Vaga deve ter título, descrição, categoria, cidade e data.
- Vaga inicia como PUBLICADA.

---

### História 7 — Buscar vagas

Como freelancer, quero buscar vagas disponíveis.

Prioridade: Alta  
Sprint: Marketplace

Critérios de aceite:
- Deve listar apenas vagas PUBLICADAS.
- Deve permitir filtro por cidade.
- Deve permitir filtro por categoria.

---

## Épico 5 — Applications

### História 8 — Candidatar-se

Como freelancer, quero me candidatar a uma vaga.

Prioridade: Alta  
Sprint: Recruitment

Critérios de aceite:
- Freelancer deve ter perfil completo.
- Não pode se candidatar duas vezes à mesma vaga.
- Candidatura inicia como PENDENTE.

---

### História 9 — Aceitar candidatura

Como empresa, quero aceitar uma candidatura.

Prioridade: Alta  
Sprint: Recruitment

Critérios de aceite:
- Apenas empresa dona da vaga pode aceitar.
- Ao aceitar, deve criar contrato.
- Outras candidaturas podem ser recusadas automaticamente.

---

## Épico 6 — Contracts

### História 10 — Finalizar contrato

Como empresa, quero finalizar um contrato após o serviço.

Prioridade: Média  
Sprint: Contract

Critérios de aceite:
- Contrato deve estar EM_EXECUCAO.
- Após finalização, status vira CONCLUIDO.
- Deve liberar avaliação.

---

## Épico 7 — Reviews

### História 11 — Avaliar após contrato

Como usuário, quero avaliar a outra parte após um contrato.

Prioridade: Média  
Sprint: Trust

Critérios de aceite:
- Apenas contratos CONCLUIDOS podem ser avaliados.
- Nota deve ser entre 1 e 5.
- Avaliação atualiza nota média.

---

## Fora do MVP

- Chat
- Pagamento interno
- OpenTrust completo
- IA
- Mobile
- Programa de indicação
