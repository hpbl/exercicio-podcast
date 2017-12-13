Análise do uso de bateria
=============

Battery Stats
-----------------
Para realizar a extração de dados relativos ao uso de bateria, seguimos os passos descritos no link https://developer.android.com/studio/profile/battery-historian.html para o uso do Batterystats. Ele é capaz de gerar um log em formato txt, contendo informações a respeito do do uso de bateria dos processos do dispositivo móvel.

Para tal teste, realizamos algumas atividades com a aplicação, similares aos testes relativos a uso de rede, memória e performance; realizamos o download de podcasts, executamos um podcast por cerca de 5 minutos, além de acessar detalhes dos podcasts, bem como intensos scrollings na tela inicial da aplicação.

Todas as informações geradas podem ser visualizadas no arquivo "batterystats.txt", o qual encontra-se na raiz desse branch (Segunda-Entrega).

Battery Historian
-----------------
Infelizmente foram enfrentados problemas com o uso do Battery Historian. Primeiramanete, tanto o README do próprio projeto, quanto o tutorial do android se encontram desatualizados.
Neles o próprio comando de `run` no docker está no modelo antigo. Após perceber isso, e conseguir adaptar o comando para a versão atual, ficamos presos em um erro que persistiu:

![DockerError](Images/DockerError.png)

O erro persistiu mesmo fazendo todas as recomendações encontradas online nos issues do repo, nos fórums de docker, e no google (que não são muitas). E o Docker parece ter sido instalado corretamente:

![DockerWorks](Images/DockerWorks.png)
