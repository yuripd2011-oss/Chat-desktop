# 💬 Chat Desktop

Um aplicativo de chat com Inteligência Artificial desenvolvido em **JavaFX**, integrado à API da **Groq**.

O projeto tem como objetivo criar um assistente de IA em formato desktop, com uma interface moderna, simples e rápida.

---

## 🚀 Funcionalidades

* 💬 Envio de mensagens para a IA
* 🤖 Respostas geradas pela Groq
* ⚡ Comunicação assíncrona para não travar a interface
* 🎨 Interface desktop moderna em JavaFX
* 🌙 Tema escuro
* 📱 Campo de mensagem com envio pelo botão ou tecla Enter
* 🔄 Indicador de status da conexão
* ⚠️ Tratamento de erros da API
* 🧠 Integração com modelos de IA disponíveis na Groq
* 💾 Não utiliza banco de dados nesta versão

---

## 🛠️ Tecnologias utilizadas

* **Java 21**
* **JavaFX 21**
* **Maven**
* **Jackson**
* **Groq API**
* **HTTP Client**
* **IntelliJ IDEA**

---

## 📁 Estrutura do projeto

```text
chat-desktop/
│
├── pom.xml
│
└── src/
    └── main/
        └── java/
            ├── module-info.java
            │
            └── com/
                └── example/
                    └── chatdesktop/
                        └── Main.java
```

---

## ⚙️ Requisitos

Antes de executar o projeto, você precisa ter instalado:

* Java JDK 21 ou superior
* IntelliJ IDEA
* Maven
* Uma chave da API da Groq
* Conexão com a Internet

---

## 🔑 Configuração da API

O projeto utiliza uma chave da **Groq API** para enviar as mensagens para o modelo de inteligência artificial.

No arquivo `Main.java`, localize:

```java
private static final String GROQ_API_KEY =
        "SUA_CHAVE_AQUI";
```

Coloque sua chave da Groq entre as aspas:

```java
private static final String GROQ_API_KEY =
        "gsk_sua_chave_aqui";
```

> ⚠️ **Importante:** nunca publique sua chave da API no GitHub ou compartilhe ela publicamente.

Para um projeto de produção, recomenda-se utilizar uma variável de ambiente ou outro método seguro para armazenar a chave.

---

## 🤖 Modelo utilizado

O projeto utiliza um modelo disponibilizado pela Groq.

Atualmente, o modelo configurado é:

```java
private static final String MODELO =
        "openai/gpt-oss-20b";
```

O modelo pode ser alterado no `Main.java` caso você queira utilizar outro modelo disponível na API da Groq.

---

## ▶️ Como executar

### 1. Clone o projeto

```bash
git clone https://github.com/SEU-USUARIO/chat-desktop.git
```

### 2. Abra o projeto no IntelliJ IDEA

Abra a pasta do projeto e aguarde o Maven baixar as dependências.

### 3. Configure sua chave

No `Main.java`:

```java
private static final String GROQ_API_KEY =
        "SUA_CHAVE_AQUI";
```

### 4. Execute o projeto

Execute:

```text
Main.java
```

ou utilize o Maven:

```bash
mvn javafx:run
```

---

## 💻 Funcionamento

O funcionamento básico do aplicativo é:

```text
Usuário
   │
   ▼
Digita uma mensagem
   │
   ▼
Chat Desktop
   │
   ▼
Java HttpClient
   │
   ▼
Groq API
   │
   ▼
Modelo de IA
   │
   ▼
Resposta
   │
   ▼
Chat Desktop
```

---

## 🖥️ Interface

A aplicação possui:

* Área principal para exibição das mensagens
* Mensagens do usuário alinhadas à direita
* Respostas da IA alinhadas à esquerda
* Campo para escrever mensagens
* Botão de envio
* Indicador de conexão
* Indicador de processamento enquanto a IA responde

---

## 🔮 Próximas melhorias

O projeto ainda está em desenvolvimento. Algumas funcionalidades planejadas são:

* [x] Histórico completo das conversas
* [ ] Memória da conversa
* [x] Barra lateral com conversas
* [x] Criar nova conversa
* [x] Excluir conversas
* [ ] Renomear conversas
* [ ] Persistência das conversas
* [ ] Markdown nas respostas
* [x] Formatação de código
* [x] Copiar resposta
* [x] Botão para regenerar resposta
* [x] Personalização do tema
* [ ] Configurações do aplicativo
* [ ] Seleção de modelos
* [x] Tela de configurações da API
* [ ] Armazenamento seguro da API Key
* [ ] Indicador de conexão com a Groq
* [x] Sistema de mensagens de erro mais detalhado

---

## 📌 Status do projeto

🟡 **Em desenvolvimento**

Esta é uma versão inicial do Chat Desktop. O projeto será expandido gradualmente com novas funcionalidades e melhorias na interface.

---

## 👨‍💻 Desenvolvedor

**Yuri**

Projeto desenvolvido para estudos e prática de desenvolvimento de aplicações desktop utilizando Java, JavaFX e integração com APIs de Inteligência Artificial.

---

## 📄 Licença

Este projeto está atualmente disponível para fins educacionais e de estudo.
