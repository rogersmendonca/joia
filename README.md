# joia
JOIA (Job Operation - Interpretador Avançado)

* Funcionalidade
Interpreta os logs do Control-M e identifica:
1. Número de execuções com sucesso (ENDED OK) por Job; 
2. Número de execuções com falha (ENDED NOTOK) por Job;
3. Registros das execuções com falha.

* Objetivo: 
Obter a população de Jobs que terminaram com falha, para testar do controle de Monitoramento de Jobs (DSS01.01 Realizar procedimentos operacionais).

* Versões:
1. Multithread (joia-multithread);
2. Hadoop MapReduce (joia-hadoop).

* Módulos:
1. joia-common: Utilitários comuns utilizados pelos outros módulos do JOIA.
2. joia-multithread: Versão multithread do JOIA.
3. joia-hadoop: Versão Hadoop MapReduce do JOIA.
