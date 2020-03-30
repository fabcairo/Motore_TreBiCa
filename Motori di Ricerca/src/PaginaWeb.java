import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PaginaWeb {
	
	//Attributi 
	/**Questo attributo rappresenta l'indirizzo URL della pagina*/
	private URL url;
	/**Questo attributo rappresenta il contenuto della pagina*/
	private String contenuto;
	/**Questo attributo rappresenta le pagina a cui punta la pagina web di riferimento*/
	private ArrayList<URL> link;
	
	//Costruttori
	
	/**Questo costruttore inizializza un oggetto della classe PaginaWeb ricevendo in ingresso i tre attributi.
	 * La lista dei link viene unicizzata dal costruttore.
	 * 
	 * @param u url della pagina
	 * @param c contenuto testuale della pagina
	 * @param l lista delle pagine a cui punta la pagina web
	 * @throws UrlNonValidoException Questa eccezione viene lanciata nel caso che qualche url tra gli attributi assuma il valore null
	 */
	public PaginaWeb(URL u, String c, ArrayList<URL> l) throws UrlNonValidoException {
		
		ArrayList<URL> newLink = new ArrayList<URL>();
		if( u == null )
			throw new UrlNonValidoException("URL non valido");
		if( l == null )
			link = new ArrayList<URL>();
		else {
			for( URL urls : l ) {
				if( urls == null ) 
					throw new UrlNonValidoException("Uno o piu' link non sono validi");
				if( !(newLink.contains(urls)) )
					newLink.add(urls);
			}
		}
		
		this.url=u;
		this.contenuto=c;
		this.link=newLink;
		
	}
	
	/**Questo costruttore inizializza un oggetto della classe PaginaWeb ricevendo in ingresso l'URL e il testo in formato HTML
	 * 
	 * @param u L'URL della pagina
	 * @param testogrezzo Il contenuto della pagina scritto in formato html
	 * @throws UrlNonValidoException nel caso che l'url della pagina sia null
	 */
	
	public PaginaWeb(URL u, String testogrezzo) throws UrlNonValidoException {
		
		ArrayList<URL> links = new ArrayList<URL>();
		ArrayList<URL> newLink = new ArrayList<URL>();
		
		if( u == null )
			throw new UrlNonValidoException("URL non valido");
		this.url=u;
		
		Document document = Jsoup.parse(testogrezzo);
		this.contenuto = document.text();
		
		Elements collegamenti = document.select("a");
		for (Element collegamento : collegamenti) {
			links.add(new URL(collegamento.attr("href")));		
		}
		
		
		for( URL urls : links ) {
			if( urls == null ) 
				throw new UrlNonValidoException("Uno o piu' link non sono validi");
			if( !(newLink.contains(urls)) )
				newLink.add(urls);
		}
		
		
		this.link=newLink;
		
	}
	
	
	//Costruttore 'vuoto'
	/** Costruisce una pagina avente l'URL specificato, contenuto vuoto e che non punta a nessun altro URL
	 * 
	 * @param u L'URL della nuova pagina web da aprire.
	 * @throws UrlNonValidoException Nel caso che l'url della pagina sia null
	 */
	public PaginaWeb(URL u) throws UrlNonValidoException {
		this(u, "", new ArrayList<URL>());
	}
	
	//Metodi
	
	/**Controlla che la pagina su cui è invocato il metodo punti alla pagina passata come argomento
	 * 
	 * @param u La pagina da verificare
	 * @return true Se la pagina su cui è invocato il metodo punta alla pagina passata come argomento
	 */
	public boolean puntaA(URL u) {
		
		if( u == null )
			return false;
		return this.link.contains(u);
	}
	
	/**Controlla che la pagina su cui è invocato il metodo punti ad una pagina avente lo stesso host della pagina passata come argomento
	 * 
	 * @param u La pagina da verificare
	 * @return true se la pagina su cui è invocato il metodo punta ad una pagina avente lo stesso host della pagina passata come argomento
	 */
	public boolean puntaAHost(URL u) {
		
		for( URL url : this.link ) {
			if( url.stessoHost(u) )
				return true;
		}
		return false;
	}
	
	/**Verifica se il contenuto della pagina contiene la stringa passata come argomento
	 * 
	 * @param x La stringa da cercare
	 * @return true se il contenuto della pagina contiene la stringa
	 * */
	public boolean contiene(String x) {
		
		return this.contenuto.toLowerCase().contains(x.toLowerCase()); //Eventualmente toLowerCase(Locale.ENGLISH) se la conversione non e' standard
	}
	
	/**Conta quante occorrenze della stringa x compaiono nel contenuto della pagina su cui e' invocato il metodo
	 * 
	 * @param x La stringa da cercare
	 * @return Il numero delle occorrenze
	 * */
	public int contieneQuanti(String x) {
		
		if (!(this.contiene(x)))
				return 0;
		return this.getContenuto().toLowerCase().split(x.toLowerCase()).length-1;
	}
	
	/**  Restituisce l'attributo url dell'istanza su cui e' invocato il metodo
	 * 
	 * @return L'attributo url
	 */
	
	public URL getURL(){
		
		return this.url;
		
	}
	
	/**  Restituisce l'attributo contenuto dell'istanza su cui e' invocato il metodo
	 * 
	 * @return L'attributo contenuto
	 */
	
	public String getContenuto() {
		
		return this.contenuto;
	}
	
	/**  Restituisce l'attributo link dell'istanza su cui e' invocato il metodo
	 * 
	 * @return L'attributo link
	 */
	
	public ArrayList<URL> getLink(){
		
		return this.link;
	}
	
	/**Override del metodo toString()
	 * @return La stringa che descrive la pagina web
	 */
	@Override
	
	public String toString() {
		
		String risultato = this.url.toString() + "\n\n" + this.contenuto +"\n\n";
		for (URL u : this.link) {
			risultato +=  "["+u.toString()+"]\n";
		}
		return risultato;
	}
	
	/** Restituisce un'antemprima della pagina web contenente la string cercata
	 * 
	 * @param s La stringa da cercare
	 * @return La stringa anteprima o la stringa vuota nel caso la stringa cercata non sia presente nel contenuto della pagina
	 */
	
	public String stringPreview(String s) {
		
		if(!(this.contiene(s)))
			
			return "";
		
		int i = this.contenuto.toLowerCase().indexOf(s); // indice d'inizio della stringa cercata
		int f = s.length()+i; // indice di fine della stringa cercata
		int previewLength = 25;
		String beginDots = "...";
		String endDots = "...";
		
		if(i < previewLength ) { // massimo tra 0 e i-previewLength
			i = 0;
			beginDots = "";
		} else
			i -= previewLength;

		if(this.contenuto.length()-f < previewLength) {  // minimo tra this.contenuto.length() e f+previewLength
			f = this.contenuto.length(); 
			endDots = "";
		} else
			f += previewLength;
		
		return this.url.toString() + "\n\n" + beginDots + this.contenuto.substring(i,f) + endDots + "\n\n";
	}

	@Override 
	
	/**Override del metodo equals
	 * @param x La stringa da confrontare
	 * @return true se le due pagine web sono uguali ovvero se hanno lo stesso URL
	 */
	public boolean equals (Object x) {
		
		if (!(x instanceof PaginaWeb))
			
			return false;
		
		PaginaWeb p = (PaginaWeb) x;
		return this.url.equals(p.getURL());
		
	}
	/** Legge da uno scanner e costruisce una nuova PaginaWeb, assumendo che l'input sia formattato
	 * nel seguente modo: se e' presente un solo \t assume che cio' che lo precede sia un URL 
	 * e ciò che lo segue sia un contenuto HTML; se sono presenti due o piu' \t assume che cio' che 
	 * precede il primo \t sia un URL, cio' che sta tra il primo e il secondo \t sia il contenuto String
	 * di una pagina e cio' che sta tra ogni altra coppia adiacente di \t sia un link.
	 * Questo metodo non gestisce l'eccezione UrlSbagliatoException, in cui si incorre se il formato degli
	 * URL in input non e' corretto, ma la solleva. Ugualmente solleva l'eccezione NoSuchElementException quando
	 * non viene trovata una prossima riga da leggere, per esempio se si e' giunti alla fine da un file.
	 * 
	 * @param s Lo scanner da cui legge.
	 * @return null se non sono presenti \t, la pagina web creata altrimenti.
	 * @throws UrlNonValidoException Se il formato di almeno un URL in input non e' corretto.
	 * @throws NoSuchElementException Se non e' trovata da s una prossima riga di input.
	 */
	public static PaginaWeb leggi(Scanner s) throws UrlNonValidoException, NoSuchElementException {
		
		String stringa = s.nextLine();
		String[] stringhe=stringa.split("\t");
		int contatore = stringhe.length;
		
		switch(contatore) {
		
		case 1:
			
			return null;
			
		case 2:
			
			
			return new PaginaWeb( new URL(stringhe[0]) , stringhe[1] );
		
		default:
			
			ArrayList<URL> links = new ArrayList<URL>();
			for (int i=2;i<contatore;i++)
				links.add(new URL(stringhe[i]));
					
			return new PaginaWeb(new URL(stringhe[0]),stringhe[1],links);
			
			
		}
		
	}

}
