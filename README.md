## 💻 Sobre o projeto

MedFema é uma clínica médica fictícia que precisa de um aplicativo para gestão de consultas. O aplicativo deve possuir funcionalidades que permitam o cadastro de médicos e de pacientes, e também o agendamento e cancelamento de consultas.

Enquanto um time de desenvolvimento será responsável pelo aplicativo mobile, o nosso será responsável pelo desenvolvimento da API Rest desse projeto.

---

## ⚙️ Funcionalidades

- [x] CRUD de médicos;
- [x] CRUD de pacientes;
- [x] Agendamento de consultas;
- [x] Cancelamento de consultas;

A exclusão de médicos e pacientes é lógica: o registro não é apagado do banco, apenas marcado como inativo, e as listagens retornam somente os ativos.

---

## 📋 Regras de negócio

### Agendamento de consultas

- O horário de funcionamento da clínica é de segunda a sábado, das 07:00 às 19:00 (o último horário de início é às 18:00);
- A consulta deve ser agendada com antecedência mínima de 30 minutos;
- Não é permitido agendar consultas com médicos ou pacientes inativos;
- O médico não pode ter outra consulta no mesmo horário;
- O paciente não pode ter mais de uma consulta no mesmo dia;
- O médico é opcional: se não for informado, a especialidade passa a ser obrigatória e a API escolhe aleatoriamente um médico livre dessa especialidade.

### Cancelamento de consultas

- É obrigatório informar o motivo do cancelamento: `PACIENTE_DESISTIU`, `MEDICO_CANCELOU` ou `OUTROS`;
- A consulta só pode ser cancelada com antecedência mínima de 24 horas.

### Respostas de erro

- Regra de negócio violada: `400 Bad Request` com a mensagem do erro;
- Campos inválidos no corpo da requisição: `400 Bad Request` com a lista de erros por campo, no formato `[{ "campo": "crm", "mensagem": "..." }]`;
- E-mail, CRM ou CPF já cadastrado: `400 Bad Request` no mesmo formato, indicando o campo repetido (ex.: `[{ "campo": "crm", "mensagem": "já está cadastrado" }]`);
- Registro não encontrado (id inexistente): `404 Not Found`.

---

## 🔗 Endpoints

| Método | Rota              | Descrição                                   |
|--------|-------------------|---------------------------------------------|
| POST   | `/medicos`        | Cadastra um médico                          |
| GET    | `/medicos`        | Lista os médicos ativos (paginado, 10 por página, ordenado por nome) |
| GET    | `/medicos/{id}`   | Detalha um médico, incluindo telefone e endereço |
| PUT    | `/medicos`        | Atualiza os dados de um médico              |
| DELETE | `/medicos/{id}`   | Inativa um médico                           |
| POST   | `/pacientes`      | Cadastra um paciente                        |
| GET    | `/pacientes`      | Lista os pacientes ativos (paginado, 10 por página, ordenado por nome) |
| GET    | `/pacientes/{id}` | Detalha um paciente, incluindo telefone e endereço |
| PUT    | `/pacientes`      | Atualiza os dados de um paciente            |
| DELETE | `/pacientes/{id}` | Inativa um paciente                         |
| GET    | `/consultas`      | Lista as consultas não canceladas (paginado, 10 por página, ordenado por data) |
| POST   | `/consultas`      | Agenda uma consulta                         |
| DELETE | `/consultas`      | Cancela uma consulta (id e motivo no corpo da requisição) |

Especialidades disponíveis: `ORTOPEDIA`, `CARDIOLOGIA`, `GINECOLOGIA` e `DERMATOLOGIA`.

---

## 🚀 Como executar

### Pré-requisitos

- Java 17
- MySQL

### Passo a passo

1. Crie o banco de dados no MySQL:

```sql
CREATE DATABASE medfema_api;
```

2. Ajuste o usuário e a senha do banco no arquivo `src/main/resources/application.properties`.

3. Na raiz do projeto, dê permissão de execução ao Maven Wrapper (necessário apenas na primeira vez):

```bash
chmod +x mvnw
```

4. Inicie a aplicação:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`. As tabelas são criadas automaticamente pelo Flyway na primeira execução.

---

## 🧪 Testes

### Testes automatizados

```bash
./mvnw test
```

Alguns testes sobem o contexto completo da aplicação (`@SpringBootTest`), por isso o MySQL precisa estar rodando.

### Testes manuais das requisições

- `requests.http`: requisições prontas para usar no IntelliJ (HTTP Client) ou no VS Code (extensão REST Client);
- `MedFema_Postman_Collection.json`: coleção para importar no Postman.

---

## 🎨 Layout

O layout da aplicação mobile está disponível neste link: <a href="https://www.figma.com/file/N4CgpJqsg7gjbKuDmra3EV/Voll.med">Figma</a>

---

## 📄 Documentação

A documentação das funcionalidades da aplicação pode ser acessada neste link: <a href="https://trello.com/b/O0lGCsKb/api-voll-med">Trello</a>

---

## 🛠 Tecnologias

As seguintes tecnologias foram utilizadas no desenvolvimento da API Rest do projeto:

- **[Java 17](https://www.oracle.com/java)**
- **[Spring Boot 3](https://spring.io/projects/spring-boot)**
- **[Maven](https://maven.apache.org)**
- **[MySQL](https://www.mysql.com)**
- **[Hibernate](https://hibernate.org)**
- **[Flyway](https://flywaydb.org)**
- **[Lombok](https://projectlombok.org)**

---

## 📝 Licença

Projeto desenvolvido por [Alura](https://www.alura.com.br) e utilizado nos cursos de Spring Boot.

Instrutor: [Rodrigo Ferreira](https://cursos.alura.com.br/user/rodrigo-ferreira) 

---
