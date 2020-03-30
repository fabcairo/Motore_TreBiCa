import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Le istanze della classe rappresentano l'URL, sequenza di caratteri che identifica univocamente l'indirizzo di una risorsa in Internet

/* @author Fabio Caironi
/* @author Manuel Trezzi
/* @author Giacomo Bianchi	*/


public class URL {
	
	/** Rappresenta il protocollo di un URL (per esempio, "http" o "https" o "ftp")*/
	private String protocollo;
	/** Rappresenta l'hostname di un URL (per esempio, "boldi.di.unimi.it")*/
	private String hostname;
	/** Rappresenta la stringa arbitraria che segue l'hostname e inizia con il carattere '/' */
	private ArrayList<String> path = new ArrayList<String>();
	/** Rappresenta la lista dei possibili protocolli */
	final static public ArrayList<String> protocolli = new ArrayList<String>(Arrays.asList("https","http","ftp","mms")); //https://it.wikipedia.org/wiki/Protocollo_di_rete
	/** Rappresenta la lista dei possibili domini  */
	final static public ArrayList<String> domini = new ArrayList<String>(Arrays.asList("it","com","gov","eu","fr","en","net")); //https://trovalost.it/domini-tld-completa/
	
	/**Crea un URL costruito per passagio dei parametri fondamentali: protocollo, hostname e path
	 * 
	 * @param protocollo Identifica il protocollo da utilizzare per l'accesso al server
	 * @param hostname Identifica il server su cui risiede la risorsa
	 * @param path Percorso nel file system del server che identifica la risorsa
	 * @throws UrlSbagliatoException Eccezione lanciata nel caso di errore nel formato dell'URL
	 */
	
	public URL(String protocollo,String hostname,String path) throws UrlNonValidoException{
		//Controllo protocollo
		if(URL.protocolli.contains(protocollo))
			this.protocollo=protocollo;
		else throw new ProtocolloNonValidoException("Il protocollo inserito e' non valido");
		//Controllo hostname
		//Controllo se sono contenuti spazi
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(hostname);
		if(matcher.find()) {
			throw new HostnameNonValidoException("L'hostname inserito e' non valido");
		}
		hostname=hostname.toLowerCase();//Questa procedura e' fatta solo sull'hostname perche' il path puo' contenere maiuscole 
		String a=hostname.substring(hostname.lastIndexOf(".")+1); //Se il '.' non e' presente a=hostname 
		if(URL.domini.contains(a) && a.length()!=hostname.length()) { //la seconda condizione serve nel caso in cui l'URL sia https://com/asd/.. 
				if(!(hostname.contains("/"))) {
					if(hostname.startsWith("www.")) {
						int ascii=hostname.charAt(4);
						if((47<ascii && ascii<58) || (96<ascii && ascii<123)) { //se l'hostname inizia con una lattera minuscola o con un numero OK
								this.hostname=hostname.substring(4);
						}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
					}else {
						int ascii=hostname.charAt(0);
						if((47<ascii && ascii<58) || (96<ascii && ascii<123)) { 
							this.hostname=hostname;
						}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
					}
				}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
		}else throw new DominioNonValidoException("Il dominio inserito e' non valido");
		//Controllo path
		if(path.indexOf('/')==0) {
			String[] b=path.split("/"); //A split non va bene '/'
			if(b.length==0) { //solo caso path='/';
				this.path.add("/");
			}else {
				for(int i=1;i<b.length;i++) { //Non uso il foreach perche' il primo elemento di b essendo vuoto non lo voglio memorizzare
					this.path.add('/'+b[i]);
				}
			}
		}else throw new PathNonValidoException("Il percorso inserito e' non valido"); //questo e' un errore dovuto alla non validita'  del path
			
	}
	
	/**Crea un URL se la stringa passata e' nel formato corretto
	 * 
	 * @param URL e' una sequenza di caratteri che identifica univocamente l'indirizzo di una risorsa in Internet
	 * @throws UrlNonValidoException Eccezione lanciata nel caso di errore nel formato dell'URL
	 */
	public URL(String url) throws UrlNonValidoException{
		
		String a,b,c;
		String[] s,s1;
		//Controllo formato
		if(url.contains("://"))
			s= url.split("://",2);
		else throw new UrlNonValidoException("L'URL inserito e' non valido"); //Errore Formato URL
		//Controllo protocollo
		if(URL.protocolli.contains(s[0]))
			a=s[0];
		else throw new ProtocolloNonValidoException("Il protocollo inserito e' non valido");
		//Aggiusto il formato nel caso l'URL sia https:////www.asd.com/.. --> https://www.asd.com/...
		while(s[1].charAt(0)=='/') {
			s[1]=s[1].substring(1);
		}
		//Controllo hostname
		if(s[1].contains("/")) {
			s1=s[1].split("/",2);
			Pattern pattern = Pattern.compile("\\s");
			Matcher matcher = pattern.matcher(s1[0]);
			if(matcher.find()) {
				throw new HostnameNonValidoException("L'hostname inserito e' non valido");
			}
			s1[0]=s1[0].toLowerCase();//Questa procedura e' fatta solo sull'hostname perche' il path puo' contenere maiuscole 
		
			String d=s1[0].substring(s1[0].lastIndexOf(".")+1);
			if(URL.domini.contains(d)) {
				if(s1[0].startsWith("www.")) {
					int ascii=s1[0].charAt(4);
					if(d.length()!=s1[0].substring(4).length()) {
						if((47<ascii && ascii<58) || (96<ascii && ascii<123)) { 
								b=s1[0].substring(4);
						}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
					}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
				}else if(d.length()!=s1[0].length()){
					int ascii=s1[0].charAt(0);
					if((47<ascii && ascii<58) || (96<ascii && ascii<123)) {
						b=s1[0];
					}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
				}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
			}else throw new DominioNonValidoException("L'URL inserito e' non valido"); 
			c=s1[1]; //Non voglio lo / all'inizio, per evitare che lo split metta uno spazio vuoto come primo
		}else {
			String d=s[1].substring(s[1].lastIndexOf(".")+1);
			if(URL.domini.contains(d)) {
				if(s[1].startsWith("www.")) {
					int ascii=s[1].charAt(4);
					if(s.length!=s[1].substring(4).length()) {
						if((47<ascii && ascii<58) || (96<ascii && ascii<123)) { 
								b=s[1].substring(4);
						}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
					}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
				}else if(s.length!=s[1].length()){
					int ascii=s[1].charAt(0);
					if((47<ascii && ascii<58) || (96<ascii && ascii<123)) {
						b=s[1];
					}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
				}else throw new HostnameNonValidoException("L'hostname inserito e' non valido");
			}else throw new DominioNonValidoException("Il dominio inserito e' non valido");
			c="/";
		}
		this.protocollo=a;
		this.hostname=b;
		String[] d=c.split("/"); //A split non va bene '/'
			if(d.length==0)
				this.path.add("/");
			else{
				for(int i=0;i<d.length;i++) { //qui parte da i=0 perche' non ho lo spazio vuoto all'inizio
					this.path.add("/"+d[i]);
				}
			}
	}
		
	/** Controlla che questo URL e quello passato per argomento abbiano lo stesso Host
	 * 
	 * @param u URL da confrontare
	 * @return true se i due hostname sono uguali usando l'equals di string ,false se i due hostname sono diversi 
	 */
	boolean stessoHost(URL u) {
		return(this.hostname.equals(u.getHostname())); //Uso l'equals di String
	}
	
	
	
	@Override
	public String toString() {
		String listString = "";
		for (String s : this.path){
		    listString += s;
		}
		return protocollo + "://" + hostname + listString;
	}
	
	
	
	@Override
	public int hashCode() { 
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((protocollo == null) ? 0 : protocollo.hashCode());
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof URL) {
			URL a=(URL)obj;
			if(a.toString().equals(this.toString()))
				return true;						
		}
		return false;
	}
	/** Ritorna il protocollo utilizzato per l'accesso al server
	 * @return il protocollo
	 */
	public String getProtocollo() {
		return protocollo;
	}
	/** Ritorna il server su cui risiede la risorsa
	 * @return l'hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/** Ritorna il percorso nel file system del server che identifica la risorsa
	 * @return il path
	 */
	public ArrayList<String> getPath() {
		return path;
	}
	
	
	
}
