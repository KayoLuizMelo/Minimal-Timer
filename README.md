# Minimal-Timer
Simple free timer so you can actually focus without any adds.

## Descrição do Projeto

O Minimal Timer é um aplicativo Android desenvolvido para oferecer uma experiência simples, rápida e livre de distrações para usuários que desejam utilizar um temporizador em seus estudos, trabalho, exercícios ou atividades do dia a dia.

Diferentemente de muitos aplicativos de produtividade disponíveis atualmente, o Minimal Timer não possui anúncios, telas desnecessárias ou recursos que desviem a atenção do usuário. Seu objetivo é oferecer apenas o essencial: um timer eficiente, bonito e fácil de usar.

---

## Problema que o aplicativo pretende resolver

Atualmente, muitos aplicativos de timer e técnica Pomodoro possuem propagandas, exigem criação de conta, vendem recursos premium ou apresentam interfaces muito complexas.

Esses fatores acabam prejudicando justamente a produtividade do usuário.

O Minimal Timer pretende resolver esse problema oferecendo:

- Interface limpa e intuitiva;
- Ausência de anúncios;
- Funcionamento totalmente gratuito;
- Inicialização rápida;
- Foco apenas na contagem do tempo.

---

## Plataforma Escolhida

**Android**

O aplicativo será desenvolvido utilizando:

- Android Studio
- Linguagem Kotlin
- Material Design 3
- Jetpack Compose (ou XML, dependendo da evolução do projeto)

---

## Interface do Usuário (UI)

Como o aplicativo será destinado ao usuário final, não haverá uma interface de administrador.

A interface será composta por poucas telas:

### Tela Principal

- Timer em destaque
- Botão Iniciar
- Botão Pausar
- Botão Reiniciar
- Seleção rápida de tempo

### Tela de Configurações

- Tema claro ou escuro
- Som ao finalizar
- Vibração
- Tempo padrão

---

## Principais Funcionalidades

Na versão inicial:

- Timer personalizado
- Iniciar contagem
- Pausar
- Reiniciar
- Escolher duração

Nas próximas versões:

- Presets rápidos (5, 10, 15, 25, 30, 45 e 60 minutos)
- Histórico de sessões
- Estatísticas de uso
- Modo foco
- Notificações
- Funcionamento em segundo plano

---

## Design

O aplicativo seguirá um design minimalista.

### Wireframe da Tela Principal

```
+----------------------------------+

          MINIMAL TIMER

             25:00

        [ Iniciar ]

   [ Pausar ] [ Reiniciar ]

------------------------------------

Tempos rápidos

 5 min   10 min   25 min

 30 min  45 min   60 min

⚙ Configurações

+----------------------------------+
```

### Wireframe da Tela de Configurações

```
+------------------------------+

Configurações

☑ Vibração

☑ Som ao terminar

Tema

( ) Claro

(•) Escuro

Tempo padrão

[25 minutos]

Salvar

+------------------------------+
```

---

## Objetivos do Projeto

- Desenvolver um aplicativo Android funcional.
- Aplicar os conceitos aprendidos durante o curso.
- Criar um aplicativo útil para estudos e produtividade.
- Disponibilizar gratuitamente um timer sem anúncios.

---

## Tecnologias Utilizadas

- Kotlin
- Android Studio
- Git
- GitHub
- Material Design
- Jetpack Compose (planejado)

---

## Cronograma

### Módulo 1
- Planejamento do projeto
- Criação do repositório
- Elaboração do README


### Módulo 2
- Estrutura inicial do aplicativo

Nesta etapa, a arquitetura base do Minimal Timer foi estruturada utilizando componentes fundamentais do ecossistema Android para garantir performance e modularidade:

### 1. Navegação e Passagem de Dados (Activities & Intents)
* **MainActivity:** Tela principal que gerencia o fluxo de execução do timer.
* **SettingsActivity:** Tela de configurações do aplicativo.
* **Intents:** A transição entre telas é feita via `Intent` explícita, enviando parâmetros do estado atual do timer por meio de `extras` (`putExtra`).

### 2. Interface Modular (Fragments Dinâmicos)
* O Timer e os botões de ação rápida foram encapsulados no `TimerFragment`.
* A renderização é realizada de forma **dinâmica** dentro do contêiner da `MainActivity` usando o `supportFragmentManager`, preparando o app para futuras adaptações de layout.

### 3. Interação com o Usuário e Estilos
* **AlertDialog:** Implementado balão informativo com instruções de uso rápidas sem desviar o foco da aplicação.
* **Temas:** Customização aplicada via `themes.xml` herdando de `Theme.Material3.DayNight.NoActionBar` para remover a barra de títulos padrão, mantendo a estética limpa e minimalista proposta.

### 4. Ciclo de Vida do Android
O ciclo de vida foi mapeado através do Logcat na `MainActivity` para garantir a correta persistência do tempo e gerenciamento de memória.


### Módulo 3
Nesta etapa, o Minimal Timer ganhou interatividade direta com o usuário através de capturas de dados, validações e melhorias de usabilidade na interface, preservando a identidade visual minimalista:

### 1. Entrada e Recuperação de Dados (EditText)
* **Componente de Entrada:** Adicionado o campo `etCustomTime` configurado com `android:inputType="number"`, o que restringe a entrada do usuário apenas a caracteres numéricos inteiros.
* **Recuperação de Valores:** Implementada a lógica no Kotlin para extrair o valor string digitado e convertê-lo com segurança para o formato numérico (`toInt()`).

### 2. Tratamento de Eventos de Clique (Listeners)
* Configuração do listener `btnAplicarTempo.setOnClickListener` para interceptar a ação do usuário, realizar validações de consistência de dados (impedindo campos vazios ou intervalos inválidos) e atualizar dinamicamente o contador principal (`tvTimer`).

### 3. Responsividade de Layout (ScrollView)
* Implementação do componente estrutural `ScrollView` envolvendo o contêiner principal do fragment. Isso garante a perfeita rolagem vertical e adaptabilidade do layout caso o teclado virtual do dispositivo se sobreponha aos elementos de tela, eliminando falhas de quebra de interface (viewport clipping).


### Módulo 4
Nesta etapa, o Minimal Timer recebeu melhorias visuais e de navegação, aplicando conceitos avançados de componentes de interface e organização de código:

* **Componentes Visuais Avançados:** 
    * **GridView:** Implementação de uma galeria de temas/ícones para personalização.
    * **ImageSwitcher:** Navegação entre imagens com transições suaves.
    * **ImageView:** Exibição dinâmica de ícones e elementos visuais.
    * **WebView:** Central de ajuda integrada carregando documentação externa.
* **Menus e Interação:**
    * **Options Menu:** Menu global para navegação rápida entre Galeria, Configurações e Ajuda.
    * **Context Menu:** Ações rápidas acionadas por clique longo no Timer (Reiniciar/Copiar).
* **Organização de Código:**
    * **HelperMethods:** Centralização de funções utilitárias (formatação de tempo e Toasts) para evitar repetição e facilitar a manutenção.

### Módulo 5
- Configurações

### Módulo 6
- Notificações e melhorias

### Módulo 7
- Testes

### Módulo 8
- Projeto final
