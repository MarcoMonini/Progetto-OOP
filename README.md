Introduzione

Il progetto presente in questa repository si occupa di modellare un data set ottenuto da un link contenente diversi contenuti. Di tale link si andrà a selezionare il file TSV che contiene il data-set da elaborare. L' elaborazione del dataset è effettuata sviluppando un software Java sfruttando i vantaggi della programmazione ad aggetti per modellare mediante opportune classi e metodi la struttura dati e per definire le funzioni richieste in fase di specifiche del progetto. L’applicazione avvia un web-server in locale sulla porta 8080 che rimane in attesa di richieste effettuate da client. All'avvio il software effettua il parsing del TSV e ci permette di effettuare richieste GET e POST che restituiscono dati in formato JSON ai quali è possibile applicare una serie di filtri, specificandoli direttamente tramite il metodo GET, sui dati di partenza. 

Richieste gestite

Il software gestisce tre tipi di richieste:

•	Restituisce i metadata del dataset

•	Restituisce i dati (eventualmente filtrati)

•	Restituisce statistiche sui dati (eventualmente filtrati)

Metadati

Rotta: /getMetadati Tipo richiesta: GET Restituisce la lista di metadati in formato JSON.

Dati

Rotta: /getRecord Tipo richiesta: GET o POST

•	GET restituisce l'intero dataset in formato JSON; è possibile inoltre richiedere un singolo record del dataset aggiungendo l'id alla rotta tramite path variable: /getRecord/{id}.

•	POST restituisce il dataset filtrato sulla base dei parametri inseriti nel body della richiesta; per la sintassi del filtro leggere più avanti.

Statistiche

Rotta: /getStatistiche Tipo richiesta: GET o POST

•	GET restituisce le statistiche in formato JSON per il campo passato come parametro "field" alla richiesta; è possibile omettere il parametro per ottenere la lista delle statistiche di tutti i campi.

•	POST restituisce le statistiche considerando solo i record che soddisfano il filtro specificato nel body della richiesta; per la sintassi del filtro leggere più avanti. Anche in questo caso si può specificare un campo oppure omettere il parametro come nel caso della GET.

Sintassi filtro

Il filtro va inserito nel body della richiesta POST come stringa RAW e deve avere il seguente formato:

{"field" : {"operator" : refvalue}}

Non è possibile applicare più filtri contemporanemente.

Field

Specifica il campo sul quale deve essere applicato il filtro. I campi validi sono specificati nei metadati e il tipo di dato da usare come riferimento deve essere lo stesso del campo.

Operator

L'operatore che specifica il tipo di filtro richiesto. 

| Operatore | Descrizione

| $not | disuguaglianza 
| $in | appartenenza a insieme (whitelist) 
| $nin | non appartenenza a insieme (blacklist 
| $gt | maggiore (solo per campi numerici)
| $gte | maggiore o uguale (solo per campi num.)
| $lt | minore (solo per campi numerici) 
| $lte | minore o uguale (solo per campi num) 
| $bt | compreso (solo per campi num.)

Struttura progetto Java

Il progetto presenta un package principale com.esameOOP.Progetto che contiene tutte le classi Java. Le classi sono divise in tre package:

•	Model: contiene la classe CasiLegali modella il singolo record del dataset;

•	Service: contiene la classe Download, Filtri e Statistiche che carica e gestisce gli accessi al dataset;

•	Controller: contiene la classe Controller gestisce le richieste del client e converte le risposte da oggetti Java a stringhe in formato JSON.

