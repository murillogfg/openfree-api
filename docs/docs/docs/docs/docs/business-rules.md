# OpenFree — Business Rules v1.0

## Objetivo

Este documento define todas as regras de negócio da OpenFree.

As regras aqui descritas devem ser respeitadas por qualquer implementação do sistema.

---

# Usuários

## BR-001

Todo usuário deve possuir um e-mail único.

---

## BR-002

A senha nunca poderá ser armazenada em texto puro.

Deve ser criptografada utilizando BCrypt.

---

## BR-003

Todo usuário possui um status.

Status possíveis:

- ATIVO
- INATIVO
- SUSPENSO

---

# Empresas

## BR-010

Uma empresa deve possuir CNPJ válido.

No MVP será validado apenas o formato.

No futuro será consultada a Receita Federal.

---

## BR-011

Uma empresa só pode publicar vagas se estiver VERIFICADA.

---

## BR-012

Uma empresa pode possuir vários administradores.

Sempre existirá apenas um OWNER.

---

## BR-013

O OWNER pode promover ou remover administradores.

---

## BR-014

Administradores não podem excluir a empresa.

Apenas o OWNER.

---

# Freelancers

## BR-020

Um freelancer só pode se candidatar se possuir perfil completo.

Perfil completo significa:

- Nome
- CPF
- Cidade
- Telefone

---

## BR-021

Um freelancer não pode se candidatar duas vezes para a mesma vaga.

---

## BR-022

Freelancers suspensos não podem realizar novas candidaturas.

---

# Vagas

## BR-030

Uma vaga pertence obrigatoriamente a uma empresa.

---

## BR-031

A vaga deve possuir:

- título
- descrição
- categoria
- cidade
- data do serviço

---

## BR-032

Somente vagas PUBLICADAS podem receber candidaturas.

---

## BR-033

Uma vaga FINALIZADA não pode ser editada.

---

# Candidaturas

## BR-040

Uma candidatura pertence a:

- uma vaga
- um freelancer

---

## BR-041

Quando uma candidatura for ACEITA:

- todas as demais poderão ser recusadas automaticamente.

---

## BR-042

Ao aceitar uma candidatura deve ser criado um contrato.

---

# Contratos

## BR-050

Todo contrato pertence:

- a uma empresa
- a um freelancer
- a uma vaga

---

## BR-051

Contratos concluídos devem permitir avaliação.

---

# Avaliações

## BR-060

Empresa avalia Freelancer.

---

## BR-061

Freelancer avalia Empresa.

---

## BR-062

Apenas contratos concluídos podem ser avaliados.

---

# Segurança

## BR-070

Toda ação importante deve gerar log.

---

## BR-071

Toda alteração deve registrar:

- usuário
- data
- ação realizada

---

# Permissões

## BR-080

OWNER possui acesso total.

---

## BR-081

ADMIN pode gerenciar vagas.

---

## BR-082

RH pode contratar freelancers.

---

## BR-083

FINANCEIRO pode visualizar pagamentos.

---

# Futuro

Estas regras poderão evoluir através de ADRs.

Nenhuma regra poderá ser alterada sem documentação.
