# 📚 Bookshelf App - Do Codelab ao Nível Sênior

O **Bookshelf** é um aplicativo Android moderno para exploração e busca de livros, utilizando a **Google Books API**. Embora tenha nascido de um guia de estudos do Google, este projeto foi evoluído para aplicar as melhores práticas de arquitetura, performance e UX do mercado.

---

## 🏗️ Arquitetura e Decisões Técnicas

O app foi construído sobre o pilar da **Clean Architecture (MVVM)** com Injeção de Dependência Manual:

### 1. Camada de Dados (Network & Repository)
*   **Retrofit & KotlinX Serialization**: Consumo da API REST com conversão segura de tipos.
*   **Pattern DTO (Data Transfer Object)**: Mapeamento rigoroso do JSON complexo do Google para objetos Kotlin, tratando cada campo como opcional para evitar quebras por dados corrompidos.
*   **Repository Pattern**: O `AppRepository` atua como um filtro, limpando URLs (upgrade de HTTP para HTTPS), injetando lógica de alta resolução (truque do `zoom=2`) e sanitizando as descrições em HTML.

### 2. Camada de UI (Jetpack Compose Avançado)
*   **MVI-like State Management**: Uso de `StateFlow` para emitir estados de `Loading`, `Success`, `Error` e `Empty`.
*   **Navegação Type-Safe**: Utilização da nova API de navegação do Compose (v2.10+) com objetos serializáveis em vez de Strings puras.
*   **Theme Pro**: Implementação de um tema customizado usando `CompositionLocalProvider` e `WindowSizeClass`, garantindo que o app seja 100% responsivo (celulares e tablets).

---

## ⚡ Funcionalidades Implementadas

*   **Scroll Infinito (Pagination)**: Sistema de detecção de fim de lista inteligente que carrega novas páginas conforme o usuário rola, sem interromper a experiência.
*   **Busca com Debounce**: Otimização de rede que espera o usuário parar de digitar (500ms) antes de disparar a requisição, economizando bateria e cota de API.
*   **Shimmer Effect Otimizado**: Efeito de carregamento animado que roda diretamente na fase de **Desenho (GPU)** através do `drawBehind`, evitando recomposições custosas na CPU.
*   **Sanitização de Strings**: Tratamento de caracteres especiais e remoção de tags HTML para uma leitura limpa dos resumos.
*   **Blindagem de UI**: Tratamento elegante para nulos e strings brancas ("Sem título", "Sem autor").

---

## 🛡️ Segurança e Performance

*   **Secrets Gradle Plugin**: A chave da API do Google está protegida no `local.properties` e nunca é exposta no GitHub.
*   **Imutabilidade**: Modelos marcados com `@Immutable` para permitir o *Skipping* do Compose, tornando o scroll da lista extremamente fluido.
*   **State Hoisting**: Separação clara entre o que é estado de negócio (ViewModel) e o que é estado visual (UI).

---

## 🧪 Testes Automatizados

O app possui uma suíte de testes que garante a estabilidade:
*   **Unit Tests**: Verificação da lógica do `HomeViewModel` com simulação de tempo (`advanceUntilIdle`) para validar o comportamento do Debounce e troca de estados.
*   **UI Tests**: Testes instrumentados que validam a renderização correta dos cards e a presença de elementos críticos na tela.

---

## 🚀 Como Executar
1.  Obtenha uma API Key no [Google Cloud Console](https://console.cloud.google.com/).
2.  No seu arquivo `local.properties`, adicione:
    ```properties
    GOOGLE_BOOKS_API_KEY=SUA_CHAVE_AQUI
    ```
3.  Faça o Sync do Gradle e rode no seu emulador ou dispositivo físico.
