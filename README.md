# Sistema de Agendamento via WhatsApp - Barbearia Will

Este é um sistema de agendamento de serviços de barbearia que utiliza o WhatsApp como interface de comunicação com os clientes. O sistema permite que os clientes agendem serviços através de uma conversa interativa no WhatsApp.

## 🚀 Tecnologias Utilizadas

- Java 21
- Spring Boot 3.4.5
- Spring Security
- Spring Data JPA
- PostgreSQL
- H2 Database (para testes)
- Lombok
- JUnit 5
- JaCoCo (para cobertura de testes)
- Gradle
- WhatsApp Business API

## 📋 Pré-requisitos

- Java 21 ou superior
- Gradle
- PostgreSQL (para ambiente de produção)
- Conta no WhatsApp Business API

## 🔧 Configuração

1. Clone o repositório
2. Configure as variáveis de ambiente no arquivo `application.properties`:
   ```properties
   whatsapp.token=seu_token_aqui
   whatsapp.phoneId=seu_phone_id_aqui
   whatsapp.url=url_da_api_whatsapp
   ```

3. Configure o banco de dados PostgreSQL:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/barbearia
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```

## 🏗️ Arquitetura

O projeto segue a arquitetura hexagonal (ports and adapters) com as seguintes camadas:

- **Domain**: Contém as regras de negócio e modelos de domínio
- **Application**: Casos de uso e DTOs
- **Adapter**: Implementações dos adaptadores de entrada e saída
  - **in**: Controllers e interfaces de entrada
  - **out**: Implementações de persistência e integração com WhatsApp

## 🔄 Endpoints

### Webhook WhatsApp
- **URL**: `/webhook`
- **Método**: POST
- **Descrição**: Endpoint que recebe as mensagens do WhatsApp e processa o fluxo de agendamento
- **Fluxo de Agendamento**:
  1. Cliente inicia conversa
  2. Seleção de serviço
  3. Escolha do profissional
  4. Seleção da data
  5. Seleção do horário
  6. Confirmação do agendamento

## 📦 Modelo de Dados

### Appointment (Agendamento)
- ID (UUID)
- Telefone
- Serviço ID
- Profissional
- Data
- Hora
- Status

## 🔒 Segurança

- O sistema utiliza Spring Security para proteção dos endpoints
- O endpoint `/webhook` é público para receber as mensagens do WhatsApp
- Demais endpoints requerem autenticação básica

## 🧪 Testes

O projeto possui uma cobertura de testes abrangente, incluindo:
- Testes unitários
- Testes de integração
- Testes de repositório
- Testes de adaptadores

Para executar os testes:
```bash
./gradlew test
```

Para verificar a cobertura de testes:
```bash
./gradlew jacocoTestReport
```

## 📝 Status do Agendamento

O sistema utiliza os seguintes status para controlar o fluxo de agendamento:
- INICIADO
- SERVICO_SELECIONADO
- PROFISSIONAL_SELECIONADO
- DATA_SELECIONADA
- HORA_SELECIONADA
- CONFIRMADO

## 🤝 Contribuição

1. Faça o fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request