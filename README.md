Simulador de Rede de Filas
Implementação em Java de um sistema de simulação de redes de filas G/G/n/k.
Este projeto evoluiu de um simulador de fila única para um sistema mais robusto, capaz de modelar redes de filas, como filas em tandem. Ele faz parte de um trabalho prático focado na simulação e análise de desempenho de sistemas de enfileiramento.

Visão Geral
Este simulador modela uma rede de filas onde as chegadas e os tempos de serviço seguem distribuições gerais (G/G/n/k). Ele permite que você defina uma topologia de rede e experimente diferentes configurações de fila, ajustando o número de servidores, capacidade e tempos de serviço para cada fila, bem como o processo de chegada para todo o sistema.

A simulação produz resultados estatísticos detalhados para cada fila na rede, tais como:

Configuração da fila (G/G/servidores/capacidade)

Intervalos de tempo de chegada e de serviço

Tabela de distribuição de estados (tempo gasto em cada estado e suas probabilidades)

Número de clientes perdidos devido à capacidade da fila

Tempo total de simulação da rede

Como Executar
Este projeto requer a biblioteca SnakeYAML para analisar o arquivo de configuração.

1. Pré-requisitos
JDK (Java Development Kit) instalado e configurado.

O arquivo snakeyaml-2.2.jar deve estar no mesmo diretório que o Main.java. Você pode baixá-lo aqui.

2. Compile o projeto
Devido à biblioteca externa, você deve incluí-la no classpath durante a compilação.

Bash

# Para Windows
javac -cp ".;snakeyaml-2.2.jar" Main.java

# Para Linux/macOS
javac -cp ".:snakeyaml-2.2.jar" Main.java

3. Execute a simulação
Da mesma forma, o classpath é necessário para a execução.



# Para Windows
java -cp ".;snakeyaml-2.2.jar" Main run modelo.yml

# Para Linux/macOS
java -cp ".:snakeyaml-2.2.jar" Main run modelo.yml
Arquivo de Configuração
O simulador usa um arquivo YAML (modelo.yml) para definir a rede e os parâmetros da simulação.

parameters: Configurações globais da simulação.

rndnumbersPerSeed: O número de aleatórios a serem gerados, o que efetivamente limita a duração da simulação.

seeds: Uma lista de sementes aleatórias para executar as simulações.

queues: Um dicionário que define cada fila na rede.

id: Um ID numérico, único e sequencial, começando em 0. A fila com id: 0 é o ponto de entrada para chegadas externas.

servers, capacity, minService, maxService: Os parâmetros específicos para aquela fila.

minArrival, maxArrival: O intervalo de tempo entre chegadas de clientes que entram na rede (usado apenas para a fila com id: 0).

routing: Define a probabilidade de um cliente se mover de uma fila para outra.

Exemplo de modelo.yml para Filas em Tandem
YAML

parameters:
  # O número de aleatórios a serem gerados
  rndnumbersPerSeed: 100000
  # Lista de sementes para executar as simulações
  seeds:
    - 41

queues: 
  Queue1: 
    id: 0
    servers: 2
    capacity: 3
    minArrival: 1.0
    maxArrival: 4.0
    minService: 3.0
    maxService: 4.0
  Queue2: 
    id: 1
    servers: 1
    capacity: 5
    minArrival: 0 # Ignorado, pois não é a fila de entrada
    maxArrival: 0 # Ignorado
    minService: 2.0
    maxService: 3.0

routing:
  # Da fila de origem...
  Queue1:
    # ...para a fila de destino: probabilidade
    Queue2: 1.0 # 100% dos clientes da Fila 1 vão para a Fila 2
Exemplo de Saída
----------- EXECUTANDO SIMULAÇÃO COM SEED: 41 -----------

Relatório Final da Simulação
=========================================================
**************** Fila Queue1 (G/G/2/3) ****************
Service Time: 3.0 ... 4.0
---------------------------------------------------------
 State                  Time        Probability
     0             2611.3837              11.83%
     1             4837.2831              21.92%
     2             7859.3907              35.61%
     3             6762.3832              30.64%
Perdas de clientes: 7176
*********************************************************

**************** Fila Queue2 (G/G/1/5) ****************
Service Time: 2.0 ... 3.0
---------------------------------------------------------
 State                  Time        Probability
     0              265.5901               1.20%
     1             2356.5511              10.68%
     2             4082.9095              18.50%
     3             5090.8711              23.07%
     4             5446.8839              24.68%
     5             4827.6350              21.87%
Perdas de clientes: 111
*********************************************************

Tempo global da simulação: 22070.4406
=========================================================
Estrutura do Projeto
queue-simulator/
├── Main.java               # Código principal da simulação
├── modelo.yml              # Arquivo de configuração da rede
├── snakeyaml-2.2.jar       # Biblioteca necessária
└── README.md               # Esta documentação
Contexto do Trabalho
Para validação, os resultados da simulação para a seguinte rede de filas em tandem devem ser entregues:

Fila 1: G/G/2/3, com chegadas entre [1, 4] e serviço entre [3, 4].

Fila 2: G/G/1/5, com serviço entre [2, 3].

Roteamento: 100% dos clientes que concluem o serviço na Fila 1 seguem para a Fila 2.

Requisitos da simulação:

Começar com as filas vazias e o primeiro cliente chegando no instante 1.5.

Executar a simulação com 100.000 números aleatórios usando a semente 41.

Relatar para cada fila:

Distribuição de probabilidade dos estados.

Tempo acumulado por estado.

Número de clientes perdidos.

Relatar o tempo total da simulação.