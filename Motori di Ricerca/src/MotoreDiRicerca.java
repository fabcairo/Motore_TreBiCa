import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Questa classe rappresenta un motore di ricerca, costituito da una lista di pagine web.
* @author Fabio Caironi
* @author Manuel Trezzi
* @author Giacomo Bianchi	
*/
public class MotoreDiRicerca {

	/** La lista contentente le pagine web nel motore di ricerca. */
    protected ArrayList<PaginaWeb> store;
	
	
	/** Crea un nuovo motore di ricerca vuoto. */
	public MotoreDiRicerca() {
		this.store = new ArrayList<PaginaWeb>();
	}
	
	/** Aggiunge una pagina web a questo motore di ricerca, o la aggiorna se gia' esistente.
	*
	* @param p La pagina web da aggiungere o aggiornare a questo motore di ricerca.
	* @return true se la pagina e' stata aggiunta, false se nulla e' stato aggiunto oppure se la pagina e' stata aggiornata. 
	*/
	public boolean aggiungiPagina(PaginaWeb p) {
		
		if( p == null )
			return false;
		int i = store.indexOf(p); // utilizza il metodo equals di PaginaWeb per il confronto. Dunque solo l'URL e' controllato.
		if( i > -1 ) {
			store.set(i, p); //Inutile gestire l'eccezione IndexOutOfBoundException qui
			return false;
		}
			store.add(p);
			return true;
	}
	
	/** Aggiunge una lista di pagine web a questo motore di ricerca, aggiornando le pagine preesistenti.
	 * 
	 * @param a L'array list di pagine web da aggiungere a questo motore di ricerca.
	 * @return true se e solo se almeno una pagina e' stata aggiunta.
	 */
	public boolean aggiungiPagine(ArrayList<PaginaWeb> a) {
		boolean x = false;
		if(a == null) return false;
		for( PaginaWeb p : a ) {
			if(this.aggiungiPagina(p))
				x = true;
		}
		
		return x;
	}

	/** Aggiunge pagine web a questo motore di ricerca sfruttando il metodo PaginaWeb.leggi(s). 
	 * Il metodo funziona iterativamente: legge dallo scanner s srighe singole e termina alla prima occorrenza di una riga di formato inappropriato, 
	 * oppure al primo sollevamento di NoSuchElementException (per esempio, alla fine di un file). 
	 * Solleva URLNonValidoException se  almeno un URL in input non e' nel formato corretto.
	 * 
	 * @param s Lo scanner che avvolge il flusso da cui leggere le stringhe di formato.
	 * @return Il numero di pagine aggiunte (escluse quelle aggiornate) a questo motore di ricerca.
	 * @throws UrlNonValidoException Se il formato di un URL in input non e' corretto.
	 */
	public int leggiPagine(Scanner s) throws UrlNonValidoException { //possibile versione piu' specifica: throw NoSuchElementException
		int x = 0;
		PaginaWeb pag;
		try {
			while( (pag = PaginaWeb.leggi(s)) != null ) {
				if(this.aggiungiPagina(pag))
					x++;
			}
		} catch (NoSuchElementException e) {
			return x;
		}
		
		return x;
	}
	
	
	/** Cerca in questo motore di ricerca una pagina web con URL u.
	 * 
	 * @param u L'URL della pagina web da cercare in questo motore di ricerca.
	 * @return La pagina web con URL u, se presente. Altrimenti null.
	 */
	public PaginaWeb cercaPag(URL u) throws UrlNonValidoException {
		PaginaWeb aux = new PaginaWeb(u);
		int i = store.indexOf(aux);
		if ( i < 0 ) 
			return null;
		return store.get(i);
	}
	
	/** Determina se e' presente in questo motore di ricerca una pagina con URL u.
	*
	* @param u L'URL della pagina web di cui si vuole stabilire l'appartenenza a questo motore di ricerca.
	* @return true se e solo se la pagina con URL u è presente in questo motore di ricerca.
	*/
	public boolean presente(URL u) throws UrlNonValidoException {
		return ( this.cercaPag(u) != null );
	}
	
	/** Conta il numero di pagine di questo motore di ricerca che puntano all'URL u. Il numero restituito e' detto indegree dell'URL u, rispetto a questo motore di ricerca.
	*
	* @param u L'URL di cui calcolare l'indegree.
	* @return L'indegree di u.
	*/
	public int indegree(URL u) {
		
		int count = 0;
		for(PaginaWeb pag: this.store) { //Forloop fail-fast (crea un ListIterator interno)
			if( pag.puntaA(u) )
				count++;
		}
		return count;
	}
	
	
	//Da utilizzare eventualmente per un ulteriore Comparator
	/** Conta il numero di pagine di questo motore di ricerca che puntano ad almeno un URL con lo stesso host di U.
	 * 
	 * @param u L'URL di cui calcolare l'host-indegree.
	 * @return L'host-indegree di u.
	 */
	public int hostIndegree(URL u) {
		
		int count = 0;
		for( PaginaWeb pag: store ) {
			if( pag.puntaAHost(u) )
				count++;
		}
		return count;
	}
	
	
	/** Restituisce lo store di questo motore di ricerca, ovvero la lista di
	 * pagine web che esso contiene.
	 * 
	 * @return Lo store di questo motore di ricerca.
	 */
	public ArrayList<PaginaWeb> getStore() {
		return store;
	}

	public void setStore(ArrayList<PaginaWeb> store) {
		this.store = store;
	}

	/** Esegue una ricerca della stringa x in questo motore di ricerca. 
	 * Restituisce tutte le pagine che contengono la stringa z.
	 * 
	 * @param x La stringa da cercare.
	 * @return La lista di pagine web che contengono la stringa x, vuota se nulla e' stato trovato.
	 */
	public ArrayList<PaginaWeb> queryAll(String x) {
		
		ArrayList<PaginaWeb> results = new ArrayList<PaginaWeb>();
		for( PaginaWeb pag: store ) {
			if( pag.contiene(x) ) {
				results.add(pag);
			}
		}
		return results;
	}

	// VERSIONE 1: NON GESTISCE CASI DI MASSIMI UGUALI
//	public PaginaWeb queryOne(String x) {
//		
//		ArrayList<PaginaWeb> all = this.queryAll(x); 
//		int max = 0, n, wmax = 0;
//		for( int i = 0; i < all.size(); i++ ) {
//			n = this.indegree( all.get(i).getURL() );
//			if( n > max ) {
//				max = n;
//				wmax = i;
//			}
//		}
//		return all.get(wmax);
//	}
	
	
	// VERSIONE 2: MOLTO PIU' GENERALE, IMPLEMENTATA GRAZIE A querySorted (SOTTO).
	/** Esegue una ricerca della stringa x in questo motore di ricerca. 
	 * Restituisce la prima pagina web che compare nei risultati di ricerca.
	 * I risultati di ricerca sono organizzati con la gerarchia esposta in querySorted.
	 * @see {@link com.my.package.MotoreDiRicerca#querySorted}
	 * 
	 * @param x La stringa da cercare in questo motore di ricerca.
	 * @return La prima pagina web tra i risultati di ricerca.
	 */
	public PaginaWeb queryOne(String x) {
		try {
			return this.querySorted(x).get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	
	/** Esegue una ricerca della stringa x in questo motore di ricerca.
	 * Restituisce un array list di pagine web contenenti x, ordinate secondo la seguente gerarchia di confronto:
	 * 	1.	Una pagina web che contiene la stringa x e' > di un'altra pagina web che contiene la stringa
	 * x se la prima ha indegree maggiore della seconda.
	 * 	2.	Una pagina web che contiene la stringa x e' > di un'altra pagina web che contiene la stringa
	 * x se le due pagine hanno lo stesso indegree e nella prima ci sono piu' occorrenze di x rispetto alla
	 * seconda.
	 * 
	 * @param x La stringa da cercare in questo motore di ricerca.
	 * @return La lista ordinata di pagine web risultanti dalla ricerca.
	 */
	public ArrayList<PaginaWeb> querySorted(String x) {
		
		ArrayList<PaginaWeb> list = this.queryAll(x);
		Collections.sort(list, new PaginaWebComparator(this, x) );
		Collections.reverse(list);
		return list;
	}
	
	
	class UrlIndegreeComparator extends MotoreDiRicerca implements Comparator<URL> {
		
		public UrlIndegreeComparator(MotoreDiRicerca m) {
			this.store = m.getStore();
		}
		@Override
		/** Implementa il confronto tra due URL tramite indegree. In questo motore di ricerca, ad ogni URL valido e'
		 * associato un intero non negativo che e' il suo indegree: la relazione d'ordine totale e'
		 * quindi indotta dall'insieme dei numeri naturali.
		 * 
		 * @param u0 Il primo URL da confrontare.
		 * @param u1 Il secondo URL da confrontare.
		 * @return La differenza tra l'indigree di u0 e l'indegree di u1.
		 * 
		 * NOTA: per ovvie ragioni, l'espressione u0.equals(u1) e' sufficiente ma non necessaria 
		 * all'espressione this.compare(u0, u1) == 0.
		 */
		public int compare(URL u0, URL u1) {
			return ( this.indegree(u0) - this.indegree(u1) );
		}
	}
		
	class PaginaWebIndegreeComparator extends MotoreDiRicerca implements Comparator<PaginaWeb> {

		public PaginaWebIndegreeComparator(MotoreDiRicerca m) {
			this.store = m.getStore();
		}
		@Override
		/** Implementa il confronto tra due PagineWeb tramite l'indegree dei loro attributi url.
		 * 
		 * @param p0 La prima pagina da confrontare.
		 * @param p1 La seconda pagina da confrontare.
		 * @return La differenza tra l'indigree dell'url di p0 e l'indegree di p1.
		 */
		public int compare(PaginaWeb p0, PaginaWeb p1) {
			return (new UrlIndegreeComparator(this).compare( p0.getURL(), p1.getURL() ) ); //Un po' inefficiente... 3 copie di store per un sort. Per contro la struttura è rigorosa. 
		}
		
	}
	
	
	class PaginaWebFrequenzaComparator implements Comparator<PaginaWeb> {
		
		private String regex;
		
		public PaginaWebFrequenzaComparator(String x) {
			this.regex = x;
		}

		@Override
		/** Implementa il confronto tra due PagineWeb tramite il numero di occorrenze di una data stringa.
		 * 
		 * @param p0 La prima pagina da confrontare.
		 * @param p1 La seconda pagina da confrontare.
		 * @return La differenza tra le occorrenze della stringa in p0 e quelle di p1.
		 */
		public int compare(PaginaWeb p0, PaginaWeb p1) {
			return ( p0.contieneQuanti(regex) - p1.contieneQuanti(regex) );
		}
	}
	
	
	class PaginaWebComparator extends MotoreDiRicerca implements Comparator<PaginaWeb> {
		
		private String regex;
		
		public PaginaWebComparator(MotoreDiRicerca m, String x) {
			this.store = m.getStore();
			this.regex = x;
		}
		
		@Override
		public int compare(PaginaWeb p0, PaginaWeb p1) {
			
			int indegreeComp = new PaginaWebIndegreeComparator(this).compare(p0, p1);
			int freqComp = new PaginaWebFrequenzaComparator(this.regex).compare(p0, p1);
			
			if ( indegreeComp == 0 )
				return freqComp;
			else
				return indegreeComp;
		}
	}

	/** Esegue UNA ricerca della stringa passata per argomento.
	 * 
	 *  @param sc Lo scanner da cui leggere i dati
	 *  @param pr Lo stream sul quale stampare i dati
	 *  @param x La stringa da cercare
	 *  @param miSentoFortunato Imposto se si effettua la ricerca in modalita' "Mi sento fortunato"
	 *  @param stopSignal Il segnale di stop 
	 *  @throws IllegalStateException In caso di chiusura dello scanner
	 *  @return true se almeno una pagina viene trovata*/
	
	public boolean ricerca(Scanner sc, PrintStream pr, String x, boolean miSentoFortunato, String stopSignal) throws IllegalStateException {
		
		if(miSentoFortunato) {
			PaginaWeb one;
			if( (one = this.queryOne(x)) == null ) {
				pr.println("Nessuna pagina trovata. :(\n");
				return false;
			}
			
			pr.println("\n" + one.toString() + "\n");
			return true;
		}
		
		ArrayList<PaginaWeb> lista = querySorted(x);
		int n=lista.size();
		if( n == 0 ) {
			pr.println("Nessuna pagina trovata. :(\n");
			return false;
		}
			
		
		int times = 0, allaVolta = 5;
		Scanner str = null;
		
		boolean redo = false, rePrint = true;
		String ch;
		int chInt;
		do {
			int maxPrint = Math.min((times+1)*allaVolta, n);
			
			
			if(rePrint) {
				for( int i = times*allaVolta ; i < maxPrint; i++ ) {
					pr.println( "\n" + (i+1) + ".\t" + lista.get(i).stringPreview(x) );
				}
				pr.print("Premere invio per visualizzare i prossimi risultati, oppure il numero corrispondente"
						+ " alla pagina a cui si vuole accedere.\n> ");
			}
			
			try {
				ch = sc.nextLine();
				if( ch.equals(stopSignal) ) { // Exit point.
					pr.println("Ricerca terminata.\n");
					redo = false;
				}
				else if( ch.equals("") ) {
					if( maxPrint != n ) {
						times++;
						redo = true;
						rePrint = true;
					} else {
						pr.println("Risultati terminati. Digitare un valore tra 1 e " + maxPrint 
							+ " oppure " + stopSignal + " per tornare al menu principale.\n> ");
						redo = true;
						rePrint = false;
					}
				}
				else {
					str = new Scanner(ch);
					chInt = str.nextInt();
					if( chInt < 1 || chInt > maxPrint ) {
						pr.print("Scelta sconosciuta. Digitare un valore tra 1 e " + maxPrint 
								+ ", invio per mostrare i prossimi risultati"
								+ " oppure " + stopSignal + " per abortire.\n> ");
						redo = true;
						rePrint = false;
					} else { // Tutti i controlli passati: stampa pagina web corrispondente
						pr.println("\n" + lista.get(chInt-1).toString() + "\n");
						pr.println("Digita una scelta:");
						int chTmp = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList
								("Torna ai risultati di ricerca."
								, "Esci dalla ricerca.")), stopSignal);
						switch(chTmp) {
						case -1: case 2:  //Exit point
							redo = false;
							break;
						case 1: 
							redo = true;
							rePrint = true;
						}
					}
				}
			} catch (InputMismatchException e) {
				pr.print("Scelta sconosciuta. Digitare un valore tra 1 e " + maxPrint 
						+ " oppure " + stopSignal + " per abortire.\n> ");
				redo = true;
				rePrint = false;
			} catch (NoSuchElementException e) {
				pr.println("Errore di input. Riprovare.");
				redo = true;
				rePrint = false;
			}
		} while (redo);			

		if( str != null )
			str.close();
		
		return true;
	}
	
	
	/** Restituisce una stringa costiutita da tutte le pagine di questo motore di ricerca, una sotto l'altra.
	 * @return La stringa che descrive il motore di ricerca */
	@Override
	public String toString() {
		
		String res = "";
		if( store != null ) {
			for( PaginaWeb pag : store ) {
				res += pag.toString();
			}
		}
		
		return res;
	}
	
	/** Confronta due motori di ricerca e stabilisce se sono uguali, ovvero se contengono le stesse pagine
	 * (le quali sono confrontabili tramite il solo URL), indipendentemente dall'ordine in cui sono inserite
	 * nei due motori.
	 * 
	 * @return true se e solo se i due motori di ricerca sono uguali.
	 */
	@Override
	public boolean equals(Object o) {
		if(!( o instanceof MotoreDiRicerca )) 
			return false;
		
		MotoreDiRicerca m1 = (MotoreDiRicerca) o;
		m1.setStore(m1.querySorted(""));
		MotoreDiRicerca m2 = new MotoreDiRicerca();
		m2.setStore(this.querySorted(""));
		
		return m1.getStore().equals(m2.getStore()); //metodo equals di AbstractList
	}
}

