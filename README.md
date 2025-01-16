# Gerador de GL

Gerador de gramáticas locais a partir de exemplos. 

A gramática é gerada no padrão da ferramenta [Unitex/GramLab](https://unitexgramlab.org/pt).

A IDE utilizada para o desenvolvimento foi o NetBeans. A distribuição Linux utilizada para realizar a implementação foi a Kali Linux, mas qualquer outra distribuição deve funcionar.

**O código não roda no Windows pela falta de uma JNI (Java Native Interface) compilada para o Windows.**

---
## Funcionamento
A maior parte do processamento do programa é chamado a partir da classe "Grafos Presenter".

A classe "UnitexFunctions" configura os paths e as opções de execução do unitextool e também fornece as funções necessárias para o preprocessamento das frases marcadas.

A classe "EventAdpter" monta os exemplos separando suas caracteristicas, literal, lema, classe gramátical, flexões e para isso utiliza-se várias regras de regex.

A classe "Generalizer" faz o cálculo da frequência das características e gera um map final com a solução generalizada.

A classe "Graph" monta o grafo conforme o padrão do grf.

---
## Interação com Spacy
Para realizar o processo de POS-Tagging foi utilizada a biblioteca do Python, Spacy.

É importante realizar a instalação dos seguintes componentes:

```pip install -U pip setuptools wheel```

```pip install -U spacy```

```python -m spacy download en_core_web_sm```

```pip install -U flask```

---
## Execução
Primeiro vá até a pasta GeradorDeGL\src\httpClient, abra um terminal e digite: ```python main.py```

Para executar o projeto, abra o projeto em alguma IDE e execute a TelaPrincipalPresenter.java.
