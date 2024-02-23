# Gruppo AM34

Andrea Caravano - Biagio Cancelliere - Alessandro Cavallo - Allegra Chiavacci

## Prova Finale (Progetto) di Ingegneria del Software - A.A. 2022/23

Valutato positivamente: 30L/30L

### Note di licenza e publicazione

Vedi [file di licenza](LICENSE)

### Fasi dello sviluppo

| Fase                                                                                                                       | Realizzazione |
|----------------------------------------------------------------------------------------------------------------------------|---------------|
| [Diagramma UML iniziale del model](deliverables/initial-uml-model-controller/Initial-UML-Model-Controller.pdf)             | 🟢            |
| [Diagramma UML iniziale di model e controller](deliverables/initial-uml-model-controller/Initial-UML-Model-Controller.pdf) | 🟢            |
| <a href="deliverables/peer-review/1/Per gruppo revisionato/peer-review-1.pdf">Peer review n. 1</a>                         | 🟢            |
| Implementazione e testing di model e controller                                                                            | 🟢            |
| [Documentazione del protocollo di comunicazione](deliverables/protocollo-comunicazione/protocollo-comunicazione.pdf)       | 🟢            |
| <a href="deliverables/peer-review/2/Per gruppo revisionato/peer-review-2.pdf">Peer review n. 2</a>                         | 🟢            |
| Implementazione del protocollo di comunicazione (RMI + Socket)                                                             | 🟢            |
| GUI in RMI                                                                                                                 | 🟢            |
| GUI in Socket                                                                                                              | 🟢            |
| [JavaDoc](http://myshelfie.andreacaravano.net/javadoc/)                                                                    | 🟢            |
| [Diagrammi UML finali](deliverables/final-uml/pdf)                                                                         | 🟢            |
| [Rapporto sui test di unità](http://myshelfie.andreacaravano.net/tests-report/)                                            | 🟢            |
| [Funzionalità avanzate](#funzionalità-avanzate)                                                                            | 🟢            |
| [Produzione pacchetto eseguibile](deliverables/artifact)                                                                   | 🟢            |
| [Diapositive di accompagnamento alla presentazione finale](deliverables/diapositive-finali/diapositive-am34.pdf)           | 🟢            |

### Funzionalità avanzate

| Fase             | Realizzazione |
|------------------|---------------|
| Partite multiple | 🟢            |
| Persistenza      | 🟢            |

**Si veda anche la sezione [Extra](#extra)**

🔴: Non ancora completata

🟡: Da completare

🟢: Completa (eventualmente in attesa di presentazione a laboratorio)

---

### Extra

È disponibile un Server reale a cui collegarsi tramite il client **Socket** via TCP.

Esso è ospitato presso `myshelfie.andreacaravano.net` alla `porta 3435`.

Viene offerta una [**piattaforma di logging**](http://myshelfie.andreacaravano.net/log/) e
di [**persistenza delle partite**](http://myshelfie.andreacaravano.net/games-persistency/) in corso.

---

### Pacchetto eseguibile (artifact/JAR)

Il pacchetto eseguibile è pubblicato come release sulla repository e come artifact nella
cartella [deliverables](deliverables/artifact).

Per l'avvio della componente Client testuale, è necessaria un'implementazione JavaSE della JVM (JRE/JDK),
con versione almeno pari a 17 (LTS), senza limitazioni superiori.

Per strutturalità, le dipendenze JavaFX sono compatibili con le implementazioni della JVM dalla versione 17 alla
versione 20.0.1 (come da [dichiarazione delle dipendenze](pom.xml)).

Si noti, per tutti gli eseguibili, che macOS dispone di un meccanismo di "quarantena" automatica per gli eseguibili
scaricati dal web (e non firmati digitalmente attraverso un certificato sviluppatore autorizzato), al fine di migliorare
la sicurezza.

Gli esegubili prodotti nell'ambito del progetto sono naturalmente privi di una firma digitale e necessitano di
esplicitarne l'esclusione:

Ad esempio:

`xattr -c eseguibile.jar` rimuove l'attributo di quarantena collegato ad _eseguibile.jar_.

Si noti inoltre che anche il lanciatore automatizzato necessita dell'autorizzazione all'esecuzione:

`chmod +x launch-client-macos.command` rimuove l'attributo di quarantena collegato a _launch-client-macos.command_.

Alternativamente, è possibile autorizzare l'eseguibile con il menù contestuale di sistema.

#### Avvio eseguibile Server

Attraverso un emulatore di terminale che possiede nel proprio PATH un'implementazione della JVM compatibile con le
specifiche descritte, si esegua:

`java -jar Server-AM34.jar PORTATCPSOCKET PORTARMI RMIHOSTNAME`

Ad esempio, nel caso tipico (locale):

`java -jar Server-AM34.jar 3435 3434 127.0.0.1`

(l'esecuzione ricade in questa casistica quando non è fornito alcun parametro)

Si noti che in una rete reale, l'hostname RMI viene sostituito dal proprio indirizzo IP (pubblico, privato o simbolico,
purché risolvibile dall'altro estremo, anche attraverso DNS).

RMI, di default, presenta diversi ostacoli alla interconnessione, anche in una medesima rete locale.
Per una configurazione correttamente funzionante in ambiente Linux, [si veda come
riferimento](https://stackoverflow.com/a/39358386).

Si noti che, sebbene tali variazioni siano indifferenti in una rete locale, non risultano ottimali in una rete
pubblica (si consiglia dunque di mantenerla solo per gli scopi limitati di testing).

In ambiente macOS e Windows, risulta tipicamente sufficiente rimuovere eventuali regole di blocco sul firewall e/o
filtri applicativi.

**Attenzione**: all'atto dell'esecuzione verranno create automaticamente le cartelle `log` e `games-persistency` (quando
viene avviata una partita) nella directory montata al momento dell'avvio dall'emulatore del terminale.

#### Avvio eseguibile Client

Gli eseguibili Client raggruppano entrambe le tipologie di interfacce (testuale/grafica).
Necessitano dunque di essere avviate da un emulatore di terminale, come illustrato nel seguito.

Viene fornito inoltre un lanciatore automatizzato, che seleziona l'eseguibile corretto
in funzione dell'architettura del processore e dei parametri forniti.

Attraverso un emulatore di terminale che possiede nel proprio PATH un'implementazione della JVM compatibile con le
specifiche descritte, si esegua:

Per il client RMI:

`java -jar ClientSelector-AM34-<architettura>.jar PORTARMI SERVERNAME RMIHOSTNAME`

Selezionare dunque le opzioni 1 o 2 (CLI o GUI).

Ad esempio, nel caso tipico (locale):

`java -jar ClientSelector-AM34-<architettura>.jar 3434 localhost 127.0.0.1`

(l'esecuzione ricade in questa casistica quando non è fornito alcun parametro)

Si noti che in una rete reale, l'hostname RMI viene sostituito dal proprio indirizzo IP (pubblico, privato o simbolico,
purché risolvibile dall'altro estremo, anche attraverso DNS).

Per il client Socket TCP:

`java -jar ClientSelector-AM34-<architettura>.jar PORTASOCKET SERVERNAME`

Selezionare dunque le opzioni 3 o 4 (CLI o GUI).

Ad esempio, nel caso tipico (locale):

`java -jar ClientSelector-AM34-<architettura>.jar 3435 localhost`

(l'esecuzione ricade in questa casistica quando non è fornito alcun parametro)

Come da [extra](#extra), è a disposizione un server pubblico reale, per accedervi è sufficiente eseguire:

`java -jar ClientSelector-AM34-<architettura>.jar 3435 myshelfie.andreacaravano.net`

Si noti che `<architettura>` va sostituito con l'architettura del proprio processore, a causa della dipendenza
strutturale dei binari JavaFX nel pacchetto eseguibile (il client testuale è funzionante in maniera indipendente
dall'architettura, naturalmente).

Tipicamente, i processori Intel o AMD fanno uso dell'architettura x64, i processori Apple Silicon serie M, Microsoft ARM
o Qualcom hanno architettura ARM.

Su Windows 11 on ARM, al momento, non è disponibile un'implementazione nativa della JVM, quindi nemmeno le dipendenze
JavaFX sono native.

In tale situazione è dunque adeguato l'eseguibile x64, che viene emulato dal sistema operativo.

---

## My Shelfie

Avete appena portato a casa la vostra nuova libreria ed è giunto il momento di riempirla con i vostri oggetti preferiti:
collezionate libri, piante, giochi da tavolo, ritratti, premi... e non dimenticate di lasciare un po’ di spazio per i
vostri gatti!

Chi riuscirà ad avere la libreria meglio organizzata?

![My Shelfie publisher](src/main/res/README/game.jpg)