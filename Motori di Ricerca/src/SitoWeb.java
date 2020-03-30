import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Questa classe rappresenta un sito web, nonche' un insieme di pagine web aventi lo stesso host. 
 * Estende MotoreDiRicerca poiche' contiene una collezione di pagine web sulle quali e' possibile
 * eseguire tutti i metodi di ricerca e sorting, ad eccezione di alcuni metodi di setting di cui
 * e' fatto opportunamente override. In altre parole, ogni sito web e' un motore di ricerca ristretto
 * alle pagine in esso contenute, che hanno la proprieta' di condividere lo stesso hostname.
 * Il dynamic binding per istanze di SitoWeb e' sconsigliato, anche se e' opportunamente gestito nei metodi.
 * 
 * @author Fabio Caironi
 * @author Manuel Trezzi
 * @author Giacomo Bianchi
 *
 */
public class SitoWeb extends MotoreDiRicerca {

	/** L'URL della homepage del sito web.  */
	private URL urlBase;
	
	
	/** Costruisce un nuovo sito web vuoto e senza un URL di base. */
	public SitoWeb() {
		super();
		urlBase = null;
	}
	
	/** Costruisce un nuovo sito web a partire da un URL, che sara' l'URL della homepage. 
	 * 
	 * @param u L'URL che servira' da URL-base del sito web. Qualsiasi URL valido e' ammesso,
	 * tuttavia il costruttore lo tronchera' salvandone solo il protocollo e l'hostname.
	 * @throws UrlNonValidoException se l'argomento e' null.
	 */
	public SitoWeb(URL u) throws UrlNonValidoException {
		super();
		if(u == null)
			throw new UrlNonValidoException("URL in input non valido.");
		this.urlBase = new URL(u.getProtocollo(), u.getHostname(), "/");		
	}
	
	
	public URL getUrlBase() {
		return this.urlBase;
	}
	
	
	@Override
	/** Aggiunge una pagina web a questo sito web.
	 * 
	 * @param p La pagina web da aggiungere a questo sito web. Condizione necessaria affinche' la pagina
	 * venga aggiunta e' che abbia lo stesso hostname del sito web.
	 * @return true se e solo se la pagina web p e' stata aggiunta.
	 */
	public boolean aggiungiPagina(PaginaWeb p) {
		if( !(p.getURL().stessoHost(urlBase)) )
			return false;
		return super.aggiungiPagina(p);		
	}
	
	/** Aggiunge una lista di pagine web a questo sito web. 
	 * 
	 * @param a La lista di pagine web da aggiungere a questo sito.
	 * @return true se e solo se almeno una pagina e' stata aggiunta.
	 */
	@Override
	public boolean aggiungiPagine(ArrayList<PaginaWeb> a) {
		boolean x = false;
		if(a == null) return false;
		for( PaginaWeb p : a ) { // Il metodo non richiama aggiungiPagina perche' il comportamento e' indefinito nel caso di dynamic binding.
			if( p.getURL().stessoHost(urlBase) ) {
				if(super.aggiungiPagina(p))
					x = true;
			}
		}
		return x;
	}
	
	
	/** Aggiunge pagine web a questo sito web leggendo da input stringhe di formato (cfr. metodo leggi di PaginaWeb).
	 *  * leggiPagine funziona iterativamente: legge dallo scanner s righe singole e termina alla prima occorrenza
	 * di una riga di formato inappropriato, oppure al primo e' sollevamento di NoSuchElementException (per esempio,
	 * alla fine di un file).
	 * 
	 * @param s Lo scanner che avvolge il flusso da cui leggere le stringhe di formato.
	 * @return Il numero di pagine aggiunte (escluse quelle aggiornate) a questo sito web.
	 * @throws UrlNonValidoException Se il formato di un URL in input non e' corretto.
	 */
	@Override
	public int leggiPagine(Scanner s) throws UrlNonValidoException {
		int x = 0;
		PaginaWeb pag;
		
		try {
			while( (pag = PaginaWeb.leggi(s)) != null ) {
				if( pag.puntaAHost(urlBase) ) { // Il metodo non richiama aggiungiPagina perche' il comportamento e' indefinito nel caso di dynamic binding.
					if(super.aggiungiPagina(pag))
						x++;
				}
			}
		} catch (NoSuchElementException e) {
			return x;
		}
		return x;
	}
	
	


	//Metodo utilizzato da websiteBuilder.
	/** Stampa una lista di scelte e acquisice una scelta da input od un segnale di arresto.
	 * 
	 * @param sc Lo scanner da cui fare input.
	 * @param pr Il PrintStream su cui fare output.
	 * @param scelte Un array list di stringhe contenenti i messaggi associati alle possibili scelte.
	 * @param stopSignal Una stringa che funzioni da segnale d'arresto, se letta da sc.
	 * @return La scelta fatta dall'utente oppure  -1 se si digita stopSignal
	 */
	static int choiceList(Scanner sc, PrintStream pr, ArrayList<String> scelte, String stopSignal) {
		
		int n = scelte.size();
		for( int i = 0; i < n; i++) {
			pr.println( (i+1) + ".\t" + scelte.get(i) );
		}
		pr.print( "\n> " );
		
		boolean redo = false;
		int ch = 1;
		do {
			try {
				ch = sc.nextInt();
				sc.nextLine();
				if (ch < 1 || ch > n) {
					pr.print("Scelta sconosciuta. Digitare un valore tra 1 e " + n + " oppure "
							+ stopSignal + " per abortire.\n> ");
					redo = true;
				}
				else redo = false;
			} catch (InputMismatchException e) {
				if( sc.nextLine().equals(stopSignal) ) { // Exit point. Qui in ogni caso e' rimosso l'input non letto da sc.nextInt
					return -1; // Return speciale per aborto.
				}
				pr.print("Scelta sconosciuta. Digitare un valore tra 1 e " + n + " oppure "
						+ stopSignal + " per abortire.\n> ");
				redo = true;
			} 
		} while (redo);
		
		return ch;
	}	
	/**Costruisce un SitoWeb leggendo quello che l'utente inserisce da tastiera
	 * 
	 * @param sc Lo scanner da cui prendere i dati
	 * @param pr Dove stampare i dati
	 * @param stopSignal Il segnale di stop
	 * @return Il sito web costruito
	 * @throws IllegalStateException
	 * @throws NoSuchElementException
	 */
	
	public static SitoWeb websiteBuilder(Scanner sc, PrintStream pr, String stopSignal) throws IllegalStateException, NoSuchElementException { //ideale per (new Scanner(System.in), System.out)
		
		String protocollo;
		String hostname = "";
		URL urlBase = null;
		SitoWeb sito = new SitoWeb(); 
		
		pr.println("Benvenuti nella procedura di creazione di un nuovo sito web. Come forse sapete, un sito web e'"
				+ " un insieme di pagine web interconnesse. Piu' precisamente, queste pagine devono condividere lo"
				+ " stesso host ed essere strutturate in percorsi, tutti facenti capo ad una homepage, il cui URL e'"
				+ " caratterizzato solo da un protocollo e un hostname. L'accesso ai diversi percorsi di un sito web,"
				+ " ovvero alle pagine web ad esso collegate, avviene per mezzo di una stringa di percorso posta a destra"
				+ " dell'hostname, costituita da parole separate da \"/\". Verrete guidati in tutti i passaggi per la costruzione"
				+ " del vostro sito web. Potete inoltre uscire dalla procedura guidata in ogni istante, digitando su una"
				+ " riga singola il comando \" " + stopSignal + " \". \n\nDigitare qualsiasi cosa per cominciare.\n");
		
		sc.nextLine();
		
		
		// PRIMO PASSO: scelta protocollo
		pr.println("Scegliere un protocollo:");
		int ch1 = SitoWeb.choiceList(sc, pr, URL.protocolli, stopSignal);
		if( ch1 == -1 ) {
			pr.println("Processo abortito. Nessuna pagina e' stata creata.");
			return sito;
		}
			
		
		protocollo = URL.protocolli.get(ch1-1);
		
		
		
		// SECONDO PASSO: inserimento hostname (auto-controllo: presenza o assenza di dominio).
		pr.println("Digitare un hostname valido.\nSi ricorda che un hostname e' un'opportuna stringa "
				+ "priva di whitespace e di slash.");
		
		boolean redo = false;
		boolean senzaDominio = false;
		
		do {
			try {
				hostname = sc.nextLine(); //esclude gia' \n
				if( hostname.equals(stopSignal) ) { // Exit point.
					pr.println("Processo abortito. Nessuna pagina e' stata creata.");
					return sito;
				}
				urlBase = new URL(protocollo, hostname, "/");
				redo = false;
			} catch (NoSuchElementException e) {
				pr.println("Errore di input. Riprovare.");
				redo = true;
			} catch (DominioNonValidoException e) {
				
				// Controllo se il problema e' circoscritto al dominio oppure no, costruendo un URL fittizio.
				senzaDominio = true;
				try {
					new URL(protocollo, hostname + ".it", "/");
				} catch (UrlNonValidoException e1) {
					pr.println("Hostname non valido. Attenersi alle istruzioni riportate sopra.");
					redo = true;
					senzaDominio = false;
				}
				if(senzaDominio) break;

			} catch (HostnameNonValidoException e) {
				pr.println("Hostname non valido. Attenersi alle istruzioni riportate sopra.");
				redo = true;
			} catch (UrlNonValidoException e) {
				pr.println("Hostname non valido. Attenersi alle istruzioni riportate sopra.");
				redo = true;
			} 
		} while (redo);
		
		//Se manca il dominio, lo faccio scegliere
		if(senzaDominio) {
			
			String dominio;
			pr.println("Scegliere un dominio:");
			int ch2 = SitoWeb.choiceList(sc, pr, URL.domini, stopSignal);
			if( ch2 == -1 ) {
				pr.println("Processo abortito. Nessuna pagina e' stata creata.");
				return sito;
			}
			
			dominio = URL.domini.get(ch2-1); //Att.ne un dominio inizia senza punto. Va aggiunto.
			
			try {
				hostname = hostname + "." + dominio;
				urlBase = new URL(protocollo, hostname, "/");
				sito = new SitoWeb(urlBase);
			} catch (UrlNonValidoException e) {
				e.printStackTrace();
				return sito;
			}
			
		}
		
		assert urlBase != null;
		pr.println("Bene, hai creato l'URL base del tuo sito.\n" + urlBase.toString() + "\n");
		
		
		
		// TERZO PASSO: Aggiunta contenuto alla homepage.
		String homepageText = "";
		String endOfTextSignal = "END";
		pr.println("Per iniziare, aggiungi contenuti alla tua homepage. Scrivi qui sotto il" 
				+ " testo che vuoi far comparire nella tua homepage. Quando hai finito, scrivi in una"
				+ " riga singola la parola \"" + endOfTextSignal + "\". Ricorda che puoi sempre"
				+ " abortire il processo digitando \"" + stopSignal + "\".\n" );
		
		String tmp = "";
		pr.print("> ");
		while(!( (tmp = sc.nextLine()).equals(endOfTextSignal) )) {
			
			if( tmp.equals(stopSignal) ) { //Exit point.
				pr.println("Sei sicuro/a di voler abortire? Digita una scelta:");
				int ch3 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
						  "Riprendi a scrivere."
						, "Salva tutto quanto fatto finora ed esci."
						, "Crea solo la homepage vuota ed esci."
						, "Abortisci tutto ed esci.")), stopSignal);
				
				switch(ch3) {
				case -1: case 4: 
					return sito;
				case 2:
					try {
						sito.aggiungiPagina( new PaginaWeb(urlBase, homepageText, new ArrayList<URL>()) );
					} catch (UrlNonValidoException e) {
						pr.println("Errore inaspettato. Nulla e' stato creato");
					} 
					return sito;
				case 3:
					try {
						sito.aggiungiPagina( new PaginaWeb(urlBase, "", new ArrayList<URL>()) );
					} catch (UrlNonValidoException e) {
						pr.println("Errore inaspettato. Nulla e' stato creato");
					} 
					return sito;
				case 1:
					continue; // Esce dal while 
				}
				
			}
			
			homepageText = homepageText + tmp + "\n";
			pr.print("> ");
			
		}
		
		
		// 	QUARTO PASSO: Aggiunta dei link alla homepage
		ArrayList<URL> tempLinks = new ArrayList<URL>();
		pr.println("\nContenuti aggiunti. Procedi ora con l'aggiunta dei links a cui la tua homepage"
				+ " puntera'. Inserisci un URL per riga qui sotto, terminando con la solita riga singola"
				+ " \"" + endOfTextSignal + "\". Ricorda che puoi sempre abortire il processo digitando"
				+ " \"" + stopSignal + "\".\n");
		
		
		pr.print("> ");
		tmp = "";
		while(!( (tmp = sc.nextLine()).equals(endOfTextSignal) )) {
			
			if( tmp.equals(stopSignal) ) { //Exit point.
				pr.println("Sei sicuro/a di voler abortire? Digita una scelta:");
				int ch4 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
						  "Riprendi a scrivere."
						, "Salva tutto quanto fatto finora ed esci."
						, "Crea la homepage senza links ed esci."
						, "Abortisci tutto ed esci.")), stopSignal);
				
				switch(ch4) {
				case -1: case 4: 
					return sito;
				case 2:
					try {
						sito.aggiungiPagina( new PaginaWeb(urlBase, homepageText, tempLinks) );
					} catch (UrlNonValidoException e) {
						pr.println("Errore inaspettato. Nulla e' stato creato");
					} 
					return sito;
				case 3:
					try {
						sito.aggiungiPagina( new PaginaWeb(urlBase, homepageText, new ArrayList<URL>()) );
					} catch (UrlNonValidoException e) {
						pr.println("Errore inaspettato. Nulla e' stato creato");
					} 
					return sito;
				case 1:
					continue; // Esce dal while 
				}
				
			}
			
			
			try {
				tempLinks.add(new URL(tmp));
			} catch (UrlNonValidoException e) {
				pr.println("URL non valido. Ritenta."); // Eventualmente specializzare la distinzione errori qui.
			}
			
			
			pr.print("> ");
			
		}
		
		try {
			sito.aggiungiPagina( new PaginaWeb(urlBase, homepageText, tempLinks) );
		} catch (UrlNonValidoException e) {
			pr.println("Errore inaspettato. Nulla e' stato creato.");
			return sito;
		}
		pr.println("Complimenti! Hai creato la homepage del tuo sito web. Come desideri procedere?");
		
		
		
		
		// 	PASSO ITERATIVO: COSTRUZIONE DEL SITE TREE.
		
		int ch5 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
				  "Aggiungi percorso."
				, "Termina.")), stopSignal);
		
		pr.println("Benvenuti nella procedura di aggiunta percorso. Vi verra' richiesto di digitare un percorso"
				+ " valido, il quale servira' a costruire una pagina web nel sito con il percorso indicato."
				+ " Si ricorda che un percorso e' una stringa ottenuta come concatenazione di stringhe nel formato"
				+ " \" /path_i \", dove path_i e' una stringa priva di whitespace e '/' per ogni i. \n");
		
		String path = "";
		URL urlPercorso = null;
		while( !( ch5 == -1 || ch5 == 2) ) {
		
			//AGGIUNTA PERCORSO
			pr.println("Digita il percorso presso il quale desideri creare una nuova pagina web.\n");
			redo = true;
			do {
				try {
					pr.print("> ");
					path = sc.nextLine();
					if( path.equals(stopSignal) ) { // Exit point.
						pr.println("Processo terminato.");
						return sito;
					}
					urlPercorso = new URL(protocollo, hostname, path);
					redo = false;
				} catch (NoSuchElementException e) {
					pr.println("Errore di input. Riprovare.");
					redo = true;
				} catch (PathNonValidoException e) {
					pr.println("Percorso non valido. Riprovare");
					redo = true;
				} catch (UrlNonValidoException e) {
					pr.println("URL non valido. Riprovare");
					redo = true;
				}
			}
			while(redo);
			
			
			// AGGIUNTA CONTENUTI
				
			String pageText = "";
			pr.println("\"Percorso istanziato. Ora aggiungi contenuti alla pagina. Quando hai finito, scrivi in una"
					+ " riga singola la parola \"" + endOfTextSignal + "\". Ricorda che puoi sempre"
					+ " abortire il processo digitando \"" + stopSignal + "\".\n" );
			
			tmp = "";
			pr.print("> ");
			while(!( (tmp = sc.nextLine()).equals(endOfTextSignal) )) {
				
				if( tmp.equals(stopSignal) ) { //Exit point.
					pr.println("Sei sicuro/a di voler abortire? Digita una scelta:");
					int ch3 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
							  "Riprendi a scrivere."
							, "Salva tutto quanto fatto finora ed esci."
							, "Crea questa pagina vuota ed esci."
							, "Abortisci tutto ed esci.")), stopSignal);
					
					switch(ch3) {
					case -1: case 4: 
						return sito;
					case 2:
						try {
							sito.aggiungiPagina( new PaginaWeb(urlPercorso, pageText, new ArrayList<URL>()) );
						} catch (UrlNonValidoException e) {
							pr.println("Errore inaspettato. Quest'ultima pagina non e' stata creata.\n");
						} 
						return sito;
					case 3:
						try {
							sito.aggiungiPagina( new PaginaWeb(urlPercorso, "", new ArrayList<URL>()) );
						} catch (UrlNonValidoException e) {
							pr.println("Errore inaspettato. Quest'ultima pagina non e' stata creata.\n");
						} 
						return sito;
					case 1:
						continue;
					}
					
				}
				
				pageText = pageText + tmp + "\n";
				pr.print("> ");
				
			}
			
			
			
		 	//AGGIUNTA LINKS
			tempLinks = new ArrayList<URL>();
			pr.println("\nContenuti aggiunti. Procedi ora con l'aggiunta dei links. Inserisci un URL per riga"
					+ " qui sotto, terminando con la solita riga singola" + " \"" + endOfTextSignal 
					+ "\". Ricorda che puoi sempre abortire il processo digitando"
					+ " \"" + stopSignal + "\".\n");
			

			pr.print("> ");
			tmp = "";
			while(!( (tmp = sc.nextLine()).equals(endOfTextSignal) )) {
				
				if( tmp.equals(stopSignal) ) { //Exit point.
					pr.println("Sei sicuro/a di voler abortire? Digita una scelta:");
					int ch4 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
							  "Riprendi a scrivere."
							, "Salva tutto quanto fatto finora ed esci."
							, "Crea questa pagina senza links ed esci."
							, "Abortisci tutto ed esci.")), stopSignal);
					
					switch(ch4) {
					case -1: case 4: 
						return sito;
					case 2:
						try {
							sito.aggiungiPagina( new PaginaWeb(urlBase, pageText, tempLinks) );
						} catch (UrlNonValidoException e) {
							pr.println("Errore inaspettato. Quest'ultima pagina non e' stata creata.\n");
						} 
						return sito;
					case 3:
						try {
							sito.aggiungiPagina( new PaginaWeb(urlBase, pageText, new ArrayList<URL>()) );
						} catch (UrlNonValidoException e) {
							pr.println("Errore inaspettato. Quest'ultima pagina non e' stata creata.\n");
						} 
						return sito;
					case 1:
						continue;
					}
					
				}
				
				try {
					tempLinks.add(new URL(tmp));
				} catch (UrlNonValidoException e) {
					pr.println("URL non valido. Ritenta."); // Eventualmente specializzare la distinzione errori qui.
				}
				
				pr.print("> ");
			}
			
			
			
			try {
				PaginaWeb show = new PaginaWeb(urlPercorso, pageText, tempLinks);
				sito.aggiungiPagina( show );
				pr.println("Complimenti! Hai creato la nuova pagina. Desideri rivederla?");
				int ch7 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
						  "Si'"
						, "No, procedi." )), stopSignal);
				
				switch(ch7) {
				case 1:
					pr.print(show.toString());
					break;
				case -1: 
					ch5 = -1; continue;
				}
			} catch (UrlNonValidoException e) {
				pr.println("Errore inaspettato. La pagina non e' stata creata.\n");
			}
			
			
			
			pr.println("\nCome vuoi procedere?");			
			ch5 = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList(
					  "Aggiungi percorso."
					, "Termina.")), stopSignal);

		}
		
		
		
		return sito;
	}

}
