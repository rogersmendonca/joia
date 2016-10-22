# joia
JOIA (Job Operation - Interpretador Avançado)

* Programa que interpreta os logs do Control-M e identifica:
	- Número de execuções com sucesso (ENDED OK) por Job; 
	- Número de execuções com falha (ENDED NOTOK) por Job;
	- Registros das execuções com falha.

* Seu objetivo é obter a população de Jobs que terminaram com falha, para testar do controle de Monitoramento de Jobs (DSS01.01 Realizar procedimentos operacionais).

* Versões
	- Multithread (joia-multithread);
	- Hadoop MapReduce (joia-hadoop).

* Módulos
	- joia-common: Utilitários comuns utilizados pelos outros módulos do JOIA.
	- joia-multithread: Versão multithread do JOIA.
	- joia-hadoop: Versão Hadoop MapReduce do JOIA.
