# Simulador de Rede de Filas

## Descrição do Projeto

Este projeto consiste no desenvolvimento de um simulador de redes de filas. A equipe tem a liberdade de escolher a linguagem de programação que julgar mais apropriada para a implementação.

É fundamental que as instruções de uso sejam claras e detalhadas, permitindo que qualquer pessoa possa compilar, executar e testar o simulador sem conhecimento prévio do seu funcionamento interno.

## Instruções de Uso

Para fins de teste e comparação de resultados, utilize o simulador de rede de filas disponibilizado no módulo 3. Em caso de dúvidas sobre a especificação e os requisitos desta etapa, consulte o texto multimodal do módulo de aprendizagem.

## Cenário de Simulação para Validação

Para validar o simulador, você deve entregar, além do código-fonte, o resultado da simulação da seguinte rede de filas:

  * **Fila 1:**

      * **Tipo:** G/G/2/3
      * **Chegadas:** Intervalo entre 1 e 4 unidades de tempo.
      * **Atendimento:** Intervalo entre 3 e 4 unidades de tempo.

  * **Fila 2:**

      * **Tipo:** G/G/1/5
      * **Atendimento:** Intervalo entre 2 e 3 unidades de tempo.
      * **Chegadas:** Esta fila não recebe clientes do exterior. 100% dos clientes que concluem o serviço na Fila 1 são direcionados para a Fila 2 (configuração em tandem).

### Parâmetros da Simulação

  * **Estado Inicial:** As filas iniciam vazias.
  * **Primeira Chegada:** O primeiro cliente chega ao sistema no tempo `1,5`.
  * **Duração da Simulação:** A simulação deve ser executada até que `100.000` números aleatórios tenham sido utilizados e então deve ser encerrada.

### Resultados a Serem Apresentados

Ao final da simulação, os seguintes dados devem ser reportados:

  * A distribuição de probabilidades dos estados de cada fila.
  * Os tempos acumulados para cada estado em cada fila.
  * O número de perdas de clientes (se houver) em cada fila.
  * O tempo global (total) da simulação.

## Como Executar

Este projeto utiliza a biblioteca **SnakeYAML** para processar o arquivo de configuração.

### 1\. Pré-requisitos

  * **JDK (Java Development Kit)** instalado e configurado no sistema.
  * O arquivo `snakeyaml-2.2.jar` deve estar localizado no mesmo diretório que o arquivo `Main.java`. Você pode fazer o download [aqui](https://www.google.com/search?q=https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.2/snakeyaml-2.2.jar).

### 2\. Compile o Projeto

Para compilar, é necessário incluir a biblioteca externa no classpath.

  * **Para Windows:**

    ```bash
    javac -cp ".;snakeyaml-2.2.jar" Main.java
    ```

  * **Para Linux/macOS:**

    ```bash
    javac -cp ".:snakeyaml-2.2.jar" Main.java
    ```

### 3\. Execute a Simulação

Da mesma forma, a execução requer a especificação do classpath.

  * **Para Windows:**

    ```bash
    java -cp ".;snakeyaml-2.2.jar" Main run modelo.yml
    ```

  * **Para Linux/macOS:**

    ```bash
    java -cp ".:snakeyaml-2.2.jar" Main run modelo.yml
    ```

## Arquivo de Configuração

O simulador é configurado através de um arquivo YAML (`modelo.yml`), que define a estrutura da rede de filas e todos os parâmetros necessários para a simulação.