# Sistema de Gestão de Encomendas Personalizadas (SGEP)

## Descrição do projeto

O Sistema de Gestão de Encomendas Personalizadas (SGEP) é um projeto acadêmico desenvolvido em Java, utilizando Spring Boot e JPA.

Seu objetivo é centralizar o gerenciamento de clientes, funcionários, pedidos, itens personalizados, pagamentos e materiais, permitindo acompanhar as encomendas desde o recebimento até a entrega.

O sistema contempla regras de negócio relacionadas à aprovação de artes, confirmação de pedidos, controle de produção, pagamentos parciais, movimentação de estoque e registro de alterações.

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Jakarta Validation
- Lombok
- Flyway Migration
- Maven
- IntelliJ IDEA
- Git e GitHub

## Arquitetura escolhida

O projeto adota a **arquitetura em camadas (Layered Architecture)**.

Essa arquitetura foi escolhida por permitir a separação de responsabilidades, facilitando a organização, manutenção, evolução e testes do sistema.
A arquitetura prevê as camadas Model, Repository, Service e Controller. Nesta etapa, o desenvolvimento está concentrado na camada Model, responsável pelas entidades JPA, seus relacionamentos e regras de negócio. 
As demais camadas serão implementadas futuramente.

## Instruções para compilação e execução

### Pré-requisitos

- JDK 21 instalado.
- IntelliJ IDEA ou outra IDE compatível com Java.
- Maven, utilizando preferencialmente o Maven Wrapper incluído no projeto.

### Como abrir e compilar

1. Clone o repositório:

   ```bash
   git clone https://github.com/pedrocosta09/sistema-gestao-de-encomendas.git
   ```

2. Abra o IntelliJ IDEA.

3. Selecione **File → Open** e abra a pasta que contém o arquivo `pom.xml`.

4. Aguarde o carregamento das dependências Maven.

5. Confira se o SDK está configurado como Java 21.

6. Utilize **Build → Build Project** para compilar o código.

### Execução da aplicação

Nesta fase, o projeto tem como objetivo a implementação e modelagem das entidades de domínio com JPA.

A aplicação ainda não possui banco de dados configurado nem funcionalidades completas de execução.
