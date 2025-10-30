# 🧾 Sistema de Atendimento P4 Fila

## 📋 Sumário

1. [Visão Geral](#1-visão-geral)
2. [Arquitetura e Padrões de Design](#2-arquitetura-e-padrões-de-design)
3. [Modelagem](#3-modelagem)
4. [Diagrama de Classes](#4-diagrama-de-classes)
5. [Diagrama de Casos de Uso](#5-diagrama-de-casos-de-uso)
6. [Entidades do Sistema](#6-entidades-do-sistema)
7. [Regras de Negócio](#7-regras-de-negócio)
8. [Script da Base de Dados](#8-script-da-base-de-dados)
9. [Tecnologias](#9-tecnologias)
10. [Estrutura de Pacotes](#10-estrutura-de-pacotes)
11. [Interface Web](#11-interface-web)
12. [Como Executar](#12-como-executar)
13. [API REST](#13-api-rest)

---

## 1. Visão Geral

O **Sistema de Atendimento P4 Fila** é uma aplicação web desenvolvida com **Spring Boot** para gerenciar filas de atendimento em departamentos, permitindo a organização de clientes em filas normais ou prioritárias, com controle de atendimentos realizados por colaboradores.

### Objetivo
Gerenciar o fluxo completo de atendimentos, desde a geração de senhas até o registro de atendimentos, incluindo funcionalidades de chamada, confirmação, finalização e transferência de atendimentos entre departamentos.

---

## 2. Arquitetura e Padrões de Design

### 2.1 Padrões de Design Adotados

#### 🎯 **Singleton Pattern**
**Classe:** `H2ConnectionSingleton`

**Justificativa:** 
- Garantir **uma única instância** de conexão com o banco de dados H2
- Reduzir overhead de criação de múltiplas conexões
- Centralizar o controle de acesso ao banco
- Thread-safe para operações concorrentes

**Implementação:**
```java
public static synchronized H2ConnectionSingleton getInstance() {
    if (instance == null) {
        instance = new H2ConnectionSingleton();
    }
    return instance;
}
```

**Benefícios:**
- ✅ Eficiência de recursos
- ✅ Controle centralizado
- ✅ Prevenção de race conditions
- ✅ Facilita manutenção

#### 🏭 **Factory Pattern**
**Classe:** `EntityFactory`

**Justificativa:**
- **Centralizar a lógica de criação** de entidades
- Garantir **criação consistente** de objetos
- Implementar regras de negócio na geração de senhas
- Facilitar extensão e manutenção

**Implementação:**
```java
public Secao createSecao(Long idCliente, String tipoSecao) {
    Secao secao = new Secao();
    secao.setIdCliente(idCliente);
    secao.setTipoSecao(tipoSecao);
    secao.setSenha(generateSenha(tipoSecao)); // Regra de negócio
    secao.setAtivo(true);
    secao.setStatus("AGUARDANDO");
    return secao;
}
```

**Benefícios:**
- ✅ Separação de responsabilidades
- ✅ Criação padronizada
- ✅ Facilita testes
- ✅ Reduz acoplamento

---

## 3. Modelagem

### 3.1 Modelo Conceitual (Diagrama E-R)

```
┌─────────────────┐
│   Departamento  │
│  (id, nome)     │
└────────┬────────┘
         │ 1
         │
         │ N
┌────────▼────────┐         ┌─────────────────┐
│   Colaborador   │         │     Cliente     │
│ (id, nome, cpf, │         │  (id, nome, cpf,│
│ idade, usuario, │         │  email, senha)  │
│ senha,          │         └────────┬────────┘
│ dep_id)         │                  │ 1
└────────┬────────┘                  │
         │ N                         │ N
         │                           │
┌────────▼────────┐         ┌────────▼────────┐
│  Atendimento    │         │     Secao       │
│ (id, id_secao,  │────────1│  (id, senha,    │
│ id_colab,       │         │   tipo, status, │
│ descricao,      │         │   ativo)        │
│ status)         │         └─────────────────┘
└────────┬────────┘
         │
         │
┌────────▼────────┐
│      Fila       │
│ (id, id_dept,   │
│ id_secao,       │
│ espera)         │
└─────────────────┘
```

### 3.2 Modelo Lógico (Tabelas)

#### **departamentos**
| Campo | Tipo      | Constraints |
|-------|-----------|-------------|
| id    | BIGINT    | PK, AUTO_INCREMENT |
| nome  | VARCHAR   | NOT NULL, UNIQUE |

#### **colaboradores**
| Campo           | Tipo      | Constraints |
|-----------------|-----------|-------------|
| id              | UUID      | PK |
| nome            | VARCHAR   | NOT NULL |
| cpf             | VARCHAR   | UNIQUE |
| idade           | INTEGER   | |
| usuario         | VARCHAR   | UNIQUE, NOT NULL |
| senha           | VARCHAR   | NOT NULL |
| departamento_id | BIGINT    | FK → departamentos |

#### **clientes**
| Campo  | Tipo      | Constraints |
|--------|-----------|-------------|
| id     | BIGINT    | PK, AUTO_INCREMENT |
| nome   | VARCHAR   | NOT NULL |
| idade  | INTEGER   | |
| cpf    | VARCHAR   | UNIQUE |
| email  | VARCHAR   | UNIQUE |
| senha  | VARCHAR   | |

#### **secoes**
| Campo        | Tipo      | Constraints |
|--------------|-----------|-------------|
| id           | BIGINT    | PK, AUTO_INCREMENT |
| id_cliente   | BIGINT    | FK → clientes |
| senha        | VARCHAR   | UNIQUE, NOT NULL |
| tipo_secao   | VARCHAR   | NOT NULL (NORMAL/PRIORITARIA) |
| status       | VARCHAR   | (AGUARDANDO/CHAMADA/EM_ATENDIMENTO/FINALIZADA/TRANSFERIDA) |
| ativo        | BOOLEAN   | NOT NULL |

#### **atendimentos**
| Campo                   | Tipo      | Constraints |
|-------------------------|-----------|-------------|
| id                      | BIGINT    | PK, AUTO_INCREMENT |
| id_secao                | BIGINT    | FK → secoes |
| id_colaborador          | UUID      | FK → colaboradores |
| descricao_atendimento   | TEXT      | |
| status                  | VARCHAR   | |
| departamento_origem_id  | BIGINT    | FK → departamentos |
| departamento_destino_id | BIGINT    | FK → departamentos |

#### **filas**
| Campo           | Tipo      | Constraints |
|-----------------|-----------|-------------|
| id              | BIGINT    | PK, AUTO_INCREMENT |
| id_departamento | BIGINT    | FK → departamentos |
| id_secao        | BIGINT    | FK → secoes |
| espera          | BOOLEAN   | NOT NULL |

---

## 4. Diagrama de Classes

```
┌─────────────────────────────────────────────────────────────┐
│                        H2ConnectionSingleton                │
├─────────────────────────────────────────────────────────────┤
│ - instance: H2ConnectionSingleton                           │
│ - dataSource: DataSource                                    │
│ - connection: Connection                                    │
│ + getInstance(): H2ConnectionSingleton                      │
│ + getDataSource(): DataSource                               │
│ + getConnection(): Connection                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                         EntityFactory                        │
├─────────────────────────────────────────────────────────────┤
│ + createCliente(...): Cliente                               │
│ + createDepartamento(...): Departamento                     │
│ + createColaborador(...): Colaborador                       │
│ + createSecao(...): Secao                                   │
│ + createAtendimento(...): Atendimento                       │
│ + createFila(...): Fila                                     │
│ - generateSenha(...): String                                │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                        Secao                                │
├─────────────────────────────────────────────────────────────┤
│ - id: Long                                                  │
│ - idCliente: Long                                           │
│ - senha: String                                             │
│ - tipoSecao: String                                         │
│ - status: String                                            │
│ - ativo: Boolean                                            │
│ - cliente: Cliente                                          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     ViewController                          │
├─────────────────────────────────────────────────────────────┤
│ - clienteRepository: ClienteRepository                      │
│ - secaoRepository: SecaoRepository                          │
│ - colaboradorRepository: ColaboradorRepository              │
│ - colaboradorLogado: Colaborador                            │
│ + index(): String                                           │
│ + clientes(): String                                        │
│ + criarSecao(): String                                      │
│ + visualizarSenhas(): String                                │
│ + login(): String                                           │
│ + atendimento(): String                                     │
│ + chamarSenha(): String                                     │
│ + iniciarAtendimento(): String                              │
│ + finalizarAtendimento(): String                            │
│ + transferirAtendimento(): String                           │
└─────────────────────────────────────────────────────────────┘
```

**Relacionamentos:**
- `H2ConnectionSingleton` → implementa Singleton Pattern
- `EntityFactory` → implementa Factory Pattern
- `ViewController` → usa Repositories → usa Models
- Models → relacionamentos JPA (@ManyToOne, @OneToMany)

---

## 5. Diagrama de Casos de Uso

### 5.1 Ator: Cliente

```
┌─────────────────────────────────────────────────┐
│                    Cliente                      │
└─────────────────────────────────────────────────┘
           │
           ├── Cadastrar Cliente
           │
           ├── Gerar Senha
           │   ├── Normal
           │   └── Prioritária
           │
           └── Visualizar Senhas
```

### 5.2 Ator: Colaborador

```
┌─────────────────────────────────────────────────┐
│                 Colaborador                     │
└─────────────────────────────────────────────────┘
           │
           ├── Login
           │
           ├── Visualizar Senhas Aguardando
           │
           ├── Chamar Senha
           │
           ├── Confirmar Cliente
           │
           ├── Atender Cliente
           │   ├── Finalizar Atendimento
           │   └── Transferir para Outro Departamento
           │
           └── Ver Histórico de Atendimentos
```

### 5.3 Sistema Completo

```
┌──────────────────────────────────────────────────────────┐
│                    Sistema P4 Fila                       │
├──────────────────────────────────────────────────────────┤
│                                                           │
│  UC01: Cadastrar Cliente                                 │
│  UC02: Gerar Senha (Normal/Prioritária)                  │
│  UC03: Visualizar Senhas                                 │
│  UC04: Login Colaborador                                 │
│  UC05: Chamar Senha                                      │
│  UC06: Confirmar Cliente                                 │
│  UC07: Finalizar Atendimento                             │
│  UC08: Transferir Atendimento                            │
│  UC09: Listar Atendimentos                               │
│                                                           │
└──────────────────────────────────────────────────────────┘
```

---

## 6. Entidades do Sistema

### 6.1 Cliente
Representa o usuário que solicita atendimento.

| Campo  | Tipo    | Descrição                      |
|--------|---------|--------------------------------|
| id     | Long    | Identificador único            |
| nome   | String  | Nome completo                  |
| idade  | Integer | Idade                          |
| cpf    | String  | CPF (único)                    |
| email  | String  | E-mail (único)                 |
| senha  | String  | Senha de acesso                |

### 6.2 Colaborador
Funcionário responsável por atendimentos.

| Campo          | Tipo   | Descrição                    |
|----------------|--------|------------------------------|
| id             | UUID   | Identificador único          |
| nome           | String | Nome completo                |
| cpf            | String | CPF (único)                  |
| idade          | Integer| Idade                        |
| usuario        | String | Login (único)                |
| senha          | String | Senha                        |
| departamentoId | Long   | FK para Departamento         |

### 6.3 Secao
Senha de atendimento gerada para cliente.

| Campo      | Tipo    | Descrição                                    |
|------------|---------|----------------------------------------------|
| id         | Long    | Identificador                                |
| idCliente  | Long    | FK para Cliente                              |
| senha      | String  | Código gerado (NA### ou PX###)               |
| tipoSecao  | String  | NORMAL ou PRIORITARIA                        |
| status     | String  | AGUARDANDO/CHAMADA/EM_ATENDIMENTO/FINALIZADA/TRANSFERIDA |
| ativo      | Boolean | Indica se está ativa                         |

### 6.4 Atendimento
Registro de atendimento realizado.

| Campo                  | Tipo   | Descrição                        |
|------------------------|--------|----------------------------------|
| id                     | Long   | Identificador                    |
| idSecao                | Long   | FK para Secao                    |
| idColaborador          | UUID   | FK para Colaborador              |
| descricaoAtendimento   | String | Descrição do atendimento         |
| status                 | String | Status do atendimento            |
| departamentoOrigemId   | Long   | FK para Departamento (origem)    |
| departamentoDestinoId  | Long   | FK para Departamento (destino)   |

---

## 7. Regras de Negócio

### RN01: Geração de Senha
- Normal: prefixo `NA` + número sequencial (ex: NA001, NA002)
- Prioritária: prefixo `PX` + número sequencial (ex: PX001, PX002)
- Implementada via **Factory Pattern**

### RN02: Fluxo de Atendimento
1. **AGUARDANDO** → Senha criada
2. **CHAMADA** → Colaborador chama senha
3. **EM_ATENDIMENTO** → Cliente confirmado pelo colaborador
4. **FINALIZADA** → Atendimento concluído
5. **TRANSFERIDA** → Enviada para outro departamento

### RN03: Priorização
- Senhas prioritárias (`PX`) têm prioridade sobre normais (`NA`)
- Ordenação automática na lista de aguardando

### RN04: Atendimento Simultâneo
- Um colaborador pode atender apenas uma senha por vez
- Necessário finalizar ou transferir para chamar outra

---

## 8. Script da Base de Dados

```sql
-- CREATE DATABASE p4_fila;
-- USE p4_fila;

CREATE TABLE departamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE colaboradores (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    idade INTEGER,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    departamento_id BIGINT,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
);

CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    idade INTEGER,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(255) UNIQUE,
    senha VARCHAR(255)
);

CREATE TABLE secoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    senha VARCHAR(20) UNIQUE NOT NULL,
    tipo_secao VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'AGUARDANDO',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
);

CREATE TABLE atendimentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_secao BIGINT NOT NULL,
    id_colaborador UUID NOT NULL,
    descricao_atendimento TEXT,
    status VARCHAR(20),
    departamento_origem_id BIGINT,
    departamento_destino_id BIGINT,
    FOREIGN KEY (id_secao) REFERENCES secoes(id),
    FOREIGN KEY (id_colaborador) REFERENCES colaboradores(id),
    FOREIGN KEY (departamento_origem_id) REFERENCES departamentos(id),
    FOREIGN KEY (departamento_destino_id) REFERENCES departamentos(id)
);

CREATE TABLE filas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_departamento BIGINT NOT NULL,
    id_secao BIGINT NOT NULL,
    espera BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_departamento) REFERENCES departamentos(id),
    FOREIGN KEY (id_secao) REFERENCES secoes(id)
);

-- Colaboradores padrão
INSERT INTO departamentos (nome) VALUES ('Atendimento ao Cliente');

-- (Os colaboradores serão inseridos automaticamente via DataInitializer)
```

---

## 9. Tecnologias

| Componente           | Tecnologia                  |
|---------------------|----------------------------|
| Backend              | Spring Boot 3.5.7          |
| Banco de Dados       | H2 Database (in-memory)    |
| ORM                  | Spring Data JPA            |
| Linguagem            | Java 21                    |
| Frontend             | Thymeleaf + HTML5 + CSS3   |
| Build                | Maven                      |
| Design Pattern       | Singleton, Factory         |
| Estilo               | Glassmorphism (efeito vidro) |

---

## 10. Estrutura de Pacotes

```
src/main/java/com/example/P4_Fila/
│
├── config/
│   ├── H2ConnectionSingleton.java        # Singleton para conexão
│   └── DataInitializer.java              # Dados iniciais
│
├── factory/
│   └── EntityFactory.java                # Factory Pattern
│
├── model/                                # Entidades JPA
│   ├── Cliente.java
│   ├── Colaborador.java
│   ├── Departamento.java
│   ├── Secao.java
│   ├── Atendimento.java
│   └── Fila.java
│
├── repository/                           # Spring Data JPA
│   ├── ClienteRepository.java
│   ├── ColaboradorRepository.java
│   └── ...
│
├── service/                              # Lógica de negócio
│   ├── ClienteService.java
│   ├── ColaboradorService.java
│   └── ...
│
├── controller/                           # Controllers REST + Views
│   ├── ViewController.java
│   └── ...
│
└── P4FilaApplication.java
```

---

## 11. Interface Web

### Design
- **Tema:** Preto e Branco elegante
- **Efeito:** Glassmorphism (vidro com blur)
- **Responsivo:** Adaptável a todos os dispositivos
- **Animações:** Transições suaves

### Telas
1. **Dashboard** (`/`) - Estatísticas e acesso rápido
2. **Clientes** (`/clientes`) - CRUD de clientes
3. **Criar Senha** (`/criar-secao`) - Geração de senhas
4. **Visualizar Senhas** (`/senhas`) - Lista de senhas
5. **Login** (`/login`) - Autenticação de colaboradores
6. **Atendimento** (`/atendimento`) - Painel de atendimento

---

## 12. Como Executar

### Pré-requisitos
- Java 21
- Maven 3.8+

### Executar
```bash
mvn spring-boot:run
```

### Acessar
- Interface Web: http://localhost:8080
- Console H2: http://localhost:8080/h2-console

### Credenciais Padrão

| Usuário | Senha | Nome          |
|---------|-------|---------------|
| 1       | 123   | João Silva    |
| 2       | 123   | Maria Santos  |
| 3       | 123   | Pedro Oliveira|

---

## 13. API REST

### Exemplos

#### Criar Cliente
```http
POST /api/clientes
Content-Type: application/json

{
  "nome": "João Silva",
  "idade": 35,
  "cpf": "12345678900",
  "email": "joao@email.com",
  "senha": "1234"
}
```

#### Criar Senha
```http
POST /api/secoes
Content-Type: application/json

{
  "idCliente": 1,
  "tipoSecao": "PRIORITARIA"
}
```

**Resposta:**
```json
{
  "id": 1,
  "senha": "PX001",
  "tipoSecao": "PRIORITARIA",
  "status": "AGUARDANDO",
  "ativo": true
}
```

---

## 👨‍💻 Autor

Pedro Henrique - Sistema desenvolvido com Spring Boot, H2 Database e padrões de design Singleton + Factory.

---

## 📄 Licença

Este projeto é um demo educacional desenvolvido para fins de aprendizado.
