import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Prova {

	public static void main(String[] args) throws UrlNonValidoException, FileNotFoundException, UnsupportedEncodingException {
		
//		//PROVA 1
//		URL u1 = new URL("https", "facebook.com", "/myProfile");
//		URL u2 = new URL("https", "twitter.com", "/");
//		URL u3 = new URL("https", "instagram.com", "/1234");
//		URL u4 = new URL("https", "vimeo.com", "/myVideos");
//		
//		ArrayList<URL> l1 = new ArrayList<URL>();
//		l1.add(u4); l1.add(u3);
//		
//		ArrayList<URL> l2 = new ArrayList<URL>();
//		l2.add(u3); l2.add(u4); 
//		
//		ArrayList<URL> l3 = new ArrayList<URL>();
//		l3.add(u1); l3.add(u4); l3.add(u2);
//		
//		ArrayList<URL> l4 = new ArrayList<URL>();
//		l4.add(u2);
//		
//		PaginaWeb p1 = new PaginaWeb(u1, "ciao", l1);
//		PaginaWeb p2 = new PaginaWeb(u2, "ciao ciao", l2);
//		PaginaWeb p3 = new PaginaWeb(u3, "ciao", l3);
//		PaginaWeb p4 = new PaginaWeb(u4, "ciao", l4);
//	
//		MotoreDiRicerca m = new MotoreDiRicerca();
//		m.aggiungiPagina(p1); m.aggiungiPagina(p2); m.aggiungiPagina(p3); m.aggiungiPagina(p4);
//		
////		ArrayList<PaginaWeb> q = m.querySorted("ciao");
////		for( PaginaWeb p: q )
////			System.out.println(p.getURL().toString());
////		
////		System.out.println( m.queryOne("ciao").toString() );
//		
//		m.ricerca(new Scanner(System.in), System.out, "ciao", false, "STOP");
	
		
		//PROVA 2
//		Scanner sc = new Scanner(System.in);
//		while(true) {
//			try {
//				int n = sc.nextInt();
//				sc.nextLine();
//			} catch (InputMismatchException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				if( sc.nextLine().equals("STOP") )	break;
//			}
//		}
		
		
		//PROVA 3
//		ArrayList<String> r = new ArrayList<String>();
//		System.out.print(r.size());
		
	
		//PROVA 4
		
//		Scanner sc = new Scanner(System.in);
//		SitoWeb.websiteBuilder(sc, System.out);
		
		//PROVA 5
//		
//		Scanner sc = new Scanner(System.in);
//		String read = sc.nextLine();
//		Scanner strStream = new Scanner(read);
//		
//		if( read.equals("") )
//			System.out.println("Hai premuto invio");
//		else {
//			System.out.println(strStream.nextInt());
//		}
//		return;
		
		
		//PROVA 6
		
//		FileInputStream file = new FileInputStream("C:/Users/Fabio/Documents/prova.txt");
//		InputStreamReader rd = new InputStreamReader(file, "ISO-8859-1");
//		Scanner sc = new Scanner(rd);
//		
//		while(true) {
//			System.out.println(sc.nextLine());
//		}
		
		
		// PROVA 7
//		
//		String st = null;
//		
//		System.out.println(st.contains(""));
		
		// PROVA 8
		
		Scanner sc = new Scanner(System.in);
		PrintStream pr = new PrintStream(System.out);
		String stopSignal = "STOP";
		
		MotoreDiRicerca TreBiCa = new MotoreDiRicerca();
		
		pr.println("Digitare il percorso del file da cui leggere:\n");
		String perc = "";
		boolean redo = false;
		
		FileInputStream file = null;
		InputStreamReader rd = null;
		Scanner fileSc = null;
		int counter = 0;
		
		do {
			try {
				perc = sc.nextLine();
				if( perc.equals(stopSignal) ) {
					pr.println("Processo abortito.\n");
					redo = false;
				} else {
					file = new FileInputStream(perc);
					rd = new InputStreamReader(file, "ISO-8859-1");
					fileSc = new Scanner(rd);
					
					counter += TreBiCa.leggiPagine(fileSc);
					pr.println("File terminato o trovata una stringa non valida.");
					redo = false;
				}
			} catch (NoSuchElementException e) {
				pr.println("Errore di input. Ritentare o digitare " + stopSignal);
				redo = true;
			} catch(FileNotFoundException e) {
				pr.println("File non trovato o formato non valido. Ritentare o digitare " + stopSignal);
				redo = true;
			} catch(UnsupportedEncodingException e) {
				throw e;
			} catch(UrlNonValidoException e) {
				pr.println("Trovato un URL non valido. Processo arrestato.");
				redo = false;
			} catch(IOException e) {
				pr.println("Errore di input. Ritentare o digitare " + stopSignal);
				redo = true;
			} finally {
				try {
					file.close();
				} catch (IOException e2) {
					pr.println("Errore di input.\n");
				} catch (NullPointerException e) {
					redo = true;
				}
			}
		} while (redo);
		
		
		for(PaginaWeb pag : TreBiCa.getStore()) {
			
			pr.println( pag.toString() + "\n" + TreBiCa.indegree(pag.getURL()) +"\n");
			pr.println( pag.puntaA(new URL("http://marra.di.unimi.it/")) + "\n\n") ;
		}
		
		
		
		
	}

	
	
}
