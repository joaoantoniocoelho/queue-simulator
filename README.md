# Simulador de Filas

Sistema de simulação de filas G/G/n/k implementado em Java.

## Como executar

### 1. Compilar o projeto

```bash
javac Main.java
```

### 2. Executar a simulação

```bash
java Main run modelo.yml
```

## Arquivo de configuração

O programa utiliza um arquivo YAML (`modelo.yml`) com os seguintes parâmetros:

- `servers`: Número de servidores na fila
- `capacity`: Capacidade máxima da fila
- `minArrival` / `maxArrival`: Tempo mínimo e máximo entre chegadas
- `minService` / `maxService`: Tempo mínimo e máximo de atendimento

## Exemplo de saída

O programa exibe:
- Configuração da fila (G/G/servers/capacity)
- Parâmetros de chegada e serviço
- Tabela com estados, tempos e probabilidades
- Número total de perdas

## Estrutura do projeto

```
M4_SMA/
├── Main.java      # Código principal da simulação
├── modelo.yml     # Arquivo de configuração
└── README.md      # Este arquivo
```
