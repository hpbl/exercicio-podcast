Verificando o uso de memória na aplicação
=============

Para realizar o teste de uso de memória durante a execução da aplicação, selecionamos as principais ações as quais um usuário pode realizar (ou não realizar), a fim de analisar quais situações são mais críticas nesse contexto. Foram selecionadas cinco situações, as quais são:

- **Situação 1:** Momento de ausência de interação, bem como a ausência de ações ocorrendo;
- **Situação 2:** Momento no qual um podcast está sendo ouvido pelo usuário;
- **Situação 3:** Momento no qual o download de um podcast está sendo realizado;
- **Situação 4:** Momento onde o usuário realiza um clique na tela;
- **Situação 5:** Momento no qual o usuário realiza diversos scrollings na aplicação;

Android Profiler
-----------------
Primeiramente, utilizamos o *Android Profiler* para obter informações a respeito do uso de memória durante essas ações. O *Android Profiler* é capaz de retornar essas informações em tempo real, dando uma representação visual do desempenho da aplicação ao longo do tempo.

Nas situações 1 e 2, o uso de memória manteve-se completamente regular durante o tempo.

![AndroidProfiler](Images/AndroidProfiler/Memoria/memoria_sem_interacao.png)

![AndroidProfiler](Images/AndroidProfiler/Memoria/memoria_play_podcast.png)

Na situação 3, há um leve aumento no consumo de memória no momento de encerramento do download do podcast.

![AndroidProfiler](Images/AndroidProfiler/Memoria/memoria_download_podcast.png)

Na situação 4, há pequenos aumentos no consumo de memória ao serem realizados os cliques. Na imagem abaixo, pode-se observar dois desses aumentos, quando três cliques foram realizados.

![AndroidProfiler](Images/AndroidProfiler/Memoria/memoria_clique_tela.png)

Na situação 5, pode-se observar pequenos aumentos constantes no uso de memória ao serem realizados os scrollings.

![AndroidProfiler](Images/AndroidProfiler/Memoria/memoria_scrolling_tela.png)

LeakCanary
-----------------

O *LeakCanary* é uma biblioteca para *Android* e *Java*, a qual é responsável por detectar vazamentos no uso de memória, os quais podem ser evitados.

Pudemos realizar essa avaliação com o LeakCanary para as situações 2 e 3, e não houve a detecção de *memory leaks* pela interface do *LeakCanary*, como mostra a imagem. Para a situação 2, o teste da execução do podcast durou mais de 9 minutos.

![LeakCanary](Images/LeakCanary/no_leaks_detected.png)
