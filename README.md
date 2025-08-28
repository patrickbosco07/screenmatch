🎬 Screenmatch

Um projeto full stack desenvolvido com Spring Boot (Java 17, JPA, PostgreSQL) no backend e HTML, CSS e JavaScript no frontend.
O sistema permite exibir e gerenciar informações sobre séries e filmes, consumindo e armazenando dados em banco de dados.

📌 Tecnologias Utilizadas
🔹 Backend

Java 17

- Spring Boot 3.5.3

- Spring Web

- Spring Data JPA

- DevTools

- PostgreSQL (banco de dados relacional)

- Maven (gerenciamento de dependências)

- Jackson (serialização JSON)

🔹 Frontend

- HTML5

- CSS3

- JavaScript (ES6+)

🚀 Funcionalidades

✅ Cadastro e persistência de séries no banco de dados.

✅ Consulta de informações via API REST.

✅ Exibição das séries em uma interface web amigável.

✅ Integração entre backend (API em Spring Boot) e frontend (JS + HTML + CSS).

✅ Estrutura pronta para expansão, como cadastro de usuários ou avaliações.

📂 Estrutura do Projeto
screenmatch/
 ├── backend/                # Código do backend (Spring Boot)
 │   ├── src/main/java/...
 │   ├── src/main/resources/
 │   └── pom.xml
 ├── frontend/               # Código do frontend (HTML, CSS e JS)
 │   ├── index.html
 │   ├── css/
 │   └── js/
 ├── README.md

⚙️ Como Rodar o Projeto
🔸 Pré-requisitos

- Java 17 instalado

- Maven instalado

- PostgreSQL em execução

🔸 Backend

1) Clone o repositório:
- git clone https://github.com/seu-usuario/screenmatch.git
- cd screenmatch/backend

2) Configure o application.properties com as credenciais do PostgreSQL.

3) Rode a aplicação:
- mvn spring-boot:run

4) O backend estará disponível em:
- http://localhost:8080

🔸 Frontend

1) Vá até a pasta frontend/

2) Abra o arquivo index.html no navegador (utilizando a extensão live server do VS Code)

3) O frontend consumirá a API do backend para exibir as séries 🎥

📖 Exemplos de Endpoints

Listar todas as séries:
- GET /series

Buscar série por ID:
- GET /series/{id}




