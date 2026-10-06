# Sistema de Gestão de Acervo e Biblioteca

Projeto desenvolvido para a disciplina de **Programação Orientada a Objetos** — DCX/CCAE/UFPB (Entrega 1).

---

## 1. Visão Geral do Domínio

O sistema gerencia o acervo bibliográfico e os usuários de uma biblioteca universitária, permitindo o cadastramento e busca de obras (livros, periódicos e mídias), controle de exemplares físicos com tombos globais e gerenciamento de usuários.

As entidades centrais do domínio são:
* **Item**: Representa a obra intelectual cadastrada no acervo (identificada por um código único, título, autoria, categoria e ano de publicação).
* **Exemplar**: Representa a unidade física correspondente a um Item, identificada por um número de tombo único em todo o sistema e acompanhada de seu estado de disponibilidade (`DISPONIVEL`, `EMPRESTADO`, etc.).
* **Usuario**: Representa o membro da comunidade acadêmica cadastrado para utilização dos serviços da biblioteca (identificado por matrícula e nome).

---

## 2. Mapa do Projeto

A estrutura de diretórios do projeto está organizada da seguinte forma:

```text
dev-java/
├── .github/workflows/
│   └── build.yml               # Pipeline de integração contínua (GitHub Actions com Maven e JDK 21)
├── dados/
│   └── itens-exemplo.csv       # Arquivo de dados de exemplo para o acervo
├── docs/
│   ├── modelo.puml             # Diagrama de classes UML do domínio e arquitetura em formato PlantUML
│   └── modelo.png              # Imagem gerada do diagrama UML do sistema
├── src/
│   ├── main/java/br/ufpb/dcx/poo/biblioteca/
│   │   ├── Fabrica.java        # Ponto de entrada oficial para instanciação independente da Biblioteca
│   │   ├── contrato/           # Pacote CONGELADO com interfaces, records de visualização e exceções
│   │   │   ├── Biblioteca.java
│   │   │   ├── AcervoService.java
│   │   │   ├── UsuarioService.java
│   │   │   ├── EmprestimoService.java
│   │   │   ├── RelatorioService.java
│   │   │   ├── *View.java
│   │   │   ├── Status*.java
│   │   │   └── excecoes/
│   │   └── inicial/            # Implementação do modelo de domínio e serviços em memória
│   │       ├── Item.java       # Entidade de domínio Item com proteção de invariantes
│   │       ├── Exemplar.java   # Entidade de domínio Exemplar
│   │       ├── Usuario.java    # Entidade de domínio Usuario
│   │       ├── AcervoEmMemoria.java      # Implementação do AcervoService em memória (Map/List)
│   │       ├── UsuariosEmMemoria.java    # Implementação do UsuarioService em memória (Map)
│   │       └── BibliotecaInicial.java    # Ponto centralizador dos serviços da Biblioteca
│   └── test/java/br/ufpb/dcx/poo/biblioteca/
│       ├── AcervoTest.java     # Suíte de testes do acervo (casos normais, inválidos, limites e encapsulamento)
│       ├── UsuarioTest.java    # Suíte de testes de usuários
│       └── FabricaTest.java    # Suíte de testes de isolamento e integridade da Fábrica
├── pom.xml                     # Configuração do Maven (Java 21, JUnit 5 Surefire)
├── DECLARACAO-DE-USO-DE-IA.md  # Registro transparente do uso de ferramentas de IA
└── README.md                   # Documentação completa do projeto
```

---

## 3. Como Executar

O projeto utiliza **Java 21** e **Maven**.

### Executar a suíte de testes:
```bash
mvn test
```

### Compilar, verificar regras e empacotar:
```bash
mvn verify
```

---

## 4. Justificativa das Escolhas de Coleções

Para garantir clareza, integridade de dados e desempenho adequado a cada requisito do sistema, foram utilizadas as seguintes estruturas de dados:

1. **`Map<String, Item>` (`LinkedHashMap` em `AcervoEmMemoria`)**:
   * *Onde:* Mapeamento interno dos itens cadastrados indexados pelo `codigo`.
   * *Por que:* O código do item é seu identificador único. O `Map` garante que a verificação de duplicidade (`containsKey`) e a recuperação do item (`get`) ocorram em tempo constante $O(1)$, evitando varreduras lineares desnecessárias e impedindo códigos repetidos. A variante `LinkedHashMap` preserva a ordem de inserção original.

2. **`Map<String, Exemplar>` (`HashMap` em `AcervoEmMemoria`)**:
   * *Onde:* Registro global de todos os exemplares físicos da biblioteca indexados pelo `tombo`.
   * *Por que:* A regra do domínio exige que o tombo seja único globalmente em todo o acervo (entre obras iguais ou distintas). Mapear diretamente os tombos em uma tabela hash permite validar a unicidade global em $O(1)$ antes de associar o exemplar ao seu respectivo item.

3. **`List<Exemplar>` (`ArrayList` em `Item`)**:
   * *Onde:* Lista de exemplares físicos pertencentes a um item específico.
   * *Por que:* Uma obra pode possuir múltiplos exemplares ordenados cronologicamente por aquisição/tombamento. A lista indexada permite iteração direta para contagem de totais e disponíveis.

4. **`Map<String, Usuario>` (`LinkedHashMap` em `UsuariosEmMemoria`)**:
   * *Onde:* Armazenamento dos usuários cadastrados indexados pela `matricula`.
   * *Por que:* A matrícula é a chave primária natural do usuário. O uso de `Map` substituiu as listas paralelas anteriores, agrupando os dados no objeto de domínio `Usuario` e permitindo buscas e validações em $O(1)$.

5. **Encapsulamento com Coleções Imutáveis (`Collections.unmodifiableList` / `List.copyOf`)**:
   * *Onde:* Em todos os métodos que retornam listas (`listarItens()`, `buscarPorTitulo()`, `listarExemplares()`, `listarUsuarios()`, `Item.getExemplares()`).
   * *Por que:* Impede que consumidores externos consigam invocar métodos mutáveis como `.clear()` ou `.remove()` na referência recebida, protegendo o estado interno da biblioteca contra corrupção.

---

## 5. Defeito Oculto Identificado e Corrigido

### O Defeito
No código original fornecido em `AcervoEmMemoria.java`, a busca interna de itens no método `localizar(String codigo)` realizava a comparação utilizando o operador de igualdade de referência:
```java
// Código original com defeito:
if (item.getCodigo() == codigo) {
    return item;
}
```
Como em Java o operador `==` compara a identidade de memória dos objetos `String` e não o seu conteúdo, qualquer chamada a `buscarItem` ou `cadastrarItem` que recebesse uma `String` instanciada dinamicamente (como `new String("L1")` ou textos lidos de arquivos/entradas de usuário) falhava ao localizar o item, lançando indevidamente `RecursoNaoEncontradoException` ou permitindo itens duplicados.

### Processo de Correção (TDD / Regressão)
1. **Teste de Regressão (Commit 1):** Foi criado o teste `buscarItemComNovaInstanciaDeString` em `AcervoTest.java`, passando `new String("L1")`. Inicialmente o teste falhou comprovando a existência do defeito (`commit: test: adiciona teste de regressão para defeito no acervo`).
2. **Correção Pontual (Commit 2):** O código foi corrigido para utilizar `.equals()` (`commit: fix: corrige defeito identificado no acervo`).
3. **Refatoração com Map:** Posteriormente, a estrutura foi aprimorada com a utilização de `Map<String, Item>`, garantindo busca segura e de alta performance.

---

## 6. Extensão Autoral da Equipe

### Regra de Negócio: Validação Temporal de Publicação e Unicidade Estrita de Tombamento
* **Motivação:** Em um sistema bibliotecário acadêmico, o catálogo deve garantir a plausibilidade histórica das obras e a integridade do patrimônio físico.
* **Comportamento da Regra:**
  1. *Validação Temporal de Publicação:* Todo item cadastrado deve possuir ano de publicação plausível, definido no intervalo entre o marco histórico da imprensa moderna (ano **1450**) e o ano corrente acrescido de um ano de margem editorial. Obras com anos anteriores a 1450 (como manuscritos antigos que exigem tombamento especial de arquivo) ou datas futuras irreais disparam `DadosInvalidosException`.
  2. *Proteção de Invariantes das Entidades:* As entidades de domínio (`Item`, `Exemplar`, `Usuario`) protegem ativamente suas invariantes já em seus construtores, rejeitando valores nulos, vazios ou em branco.
  3. *Atomicidade e Unicidade Global de Tombo:* A tentativa de cadastrar um tombo duplicado em qualquer obra falha sem corromper ou alterar parcialmente o estado do item ou do acervo.
* **Onde foi implementada:** Em `Item.java` (constante `ANO_MINIMO_PUBLICACAO`, validação no construtor e setters), `Exemplar.java`, `Usuario.java` e `AcervoEmMemoria.java`.
* **Testes:** Coberta pelos testes unitários `anoDePublicacaoInvalidoPassado()`, `anoDePublicacaoInvalidoFuturo()`, `tomboDuplicadoEntreItensDiferentes()`, `tomboDuplicadoNoMesmoItem()`, `encapsulamentoDasListasRetornadas()` e `atomicidadeAdicionarExemplar()` em `AcervoTest.java`.

---

## 7. Modelo UML

O diagrama completo de classes e arquitetura do sistema encontra-se modelado em [`docs/modelo.puml`](docs/modelo.puml) e renderizado na imagem [`docs/modelo.png`](docs/modelo.png).

---

## 8. Equipe

| Nome | Matrícula | GitHub |
|---|---|---|
| Jean Gustavo | 2026001 | @JeanGustavo |
