import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MotoreTreBiCa {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		Scanner sc = new Scanner(System.in);
		PrintStream pr = new PrintStream(System.out);
		
		MotoreDiRicerca TreBiCa = new MotoreDiRicerca();	
		ArrayList<String> scelte= new ArrayList<String>(Arrays.asList(
				"Aggiungi pagina web singola."
				,"Aggiungi gruppo di pagine web."
				,"Costuisci un sito web."
				,"Esegui una ricerca."
				,"Mi sento fortunato."
				,"Tutte le pagine web di TreBiCa."
				,"Esci."));
		
		int scelta, exit_choice = 7;
		String stopSignal = "STOP";
		
		
		pr.println("Benvenuti nel motore di ricerca TreBiCa. Digitare una scelta tra le seguenti per cominciare:");
		scelta = SitoWeb.choiceList(sc, pr, scelte, stopSignal);
		
		while(scelta!=exit_choice && scelta!=-1) { //Il caso in cui scelta = -1 (Caso in cui l'utente digita STOP) fa terminare il programma 
			
			switch(scelta) {
			
			// 1. AGGIUNGI PAGINa
			case 1: {
				
				
				pr.println("Avete scelto di aggiungere una pagina al motore di ricerca. Questa procedura permette di"
						+ " ricevere una pagina web da input nel seguente formato: "
						+ " se e' presente un solo \t assume che cio' che lo precede sia un URL e ciò che lo segue sia un"
						+ " contenuto HTML; se sono presenti due o piu' \t assume che cio' che precede il primo \t sia un URL,"
						+ " cio' che sta tra il primo e il secondo \t sia il contenuto String di una pagina e cio' che sta tra"
						+ " ogni altra coppia adiacente di \t sia un link.\nDa dove desiderate leggere?");
				int streamCh = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList
						("Da tastiera."
						,"Da file.")), stopSignal);
				switch(streamCh) {
				
				case 1: 
					boolean notDone = true;
					do {
						try {
							pr.println("Digitare la pagina web nel formato indicato:\n");
							PaginaWeb add = PaginaWeb.leggi(sc);
							if( add == null ) {
								pr.println("Formato non valido. Ritentare?");
								int redoCh = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList
										("Si'", "No")), stopSignal);
								switch(redoCh) {
								case -1: case 2: 
									notDone = false;
									break;
								case 1: notDone = true;
								}
							} else {
								if(TreBiCa.aggiungiPagina(add))
									pr.println("Pagina aggiunta!\n");
								else
									pr.println("Pagina aggiornata!\n");
								
								notDone = false;
							}
						} catch (UrlNonValidoException e) {
							pr.println("Formato URL non valido. Ritentare o digitare " + stopSignal);
							notDone = true;
						} catch (NoSuchElementException e) {
							pr.println("Errore di input. Riprovare oppue digitare " + stopSignal);
							notDone = true;
						}
					} while(notDone);
					break;
					
				case 2: 
					pr.println("Digitare il percorso del file da cui leggere:\n");
					String perc = "";
					boolean redo = true;
					
					FileInputStream file = null;
					InputStreamReader rd = null;
					Scanner fileSc = null;
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
								
								notDone = true;
								do { // L'acquisizione inizia qui
									PaginaWeb add = PaginaWeb.leggi(fileSc);
									if (add == null) {
										pr.println("Formato non valido. Leggere la prossima riga?");
										int goOn = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList
												("Si'", "No, esci.")), stopSignal);
										switch(goOn) {
										case 2: case -1:
											fileSc.close();
											notDone = false;
											redo = false;
											break;
										case 1: 
											notDone = true;
										}
									} else {
										if (TreBiCa.aggiungiPagina(add))
											pr.println("Pagina aggiunta!\n");
										else
											pr.println("Pagina aggiornata!\n");

										notDone = false;
										redo = false;
									} 
								} while (notDone);
							}

						} catch (NoSuchElementException e) {
							pr.println("Il file non ha piu' righe! Processo teminato.\n");
							redo = false;
						} catch(FileNotFoundException e) {
							pr.println("File non trovato o formato non valido. Ritentare o digitare " + stopSignal);
							redo = true;
						} catch(UnsupportedEncodingException e) {
							throw e;
						} catch(UrlNonValidoException e) {
							pr.println("Formato URL non valido. Ritentare o digitare " + stopSignal);
							redo = true;
						} catch(IOException e) {
							pr.println("Errore di input. Ritentare o digitare " + stopSignal);
							redo = true;
						} finally {
							try {
								file.close();
							} catch (IOException e2) {
								pr.println("Errore di input.\n");
							}
						}
					} while (redo);
					
					break;
				case -1: pr.println("Processo abortito.\n");
					
				}
				
				break;
				
			}
			// 2. AGGIUNGI PAGINe
			case 2: {
				
				pr.println("Avete scelto di aggiungere un gruppo di pagine al motore di ricerca. Questa procedura permette di"
						+ " ricevere una sequenza di pagine web da input, separate l'un l'altra da un 'a capo' e ciascuna nel"
						+ " seguente formato: se e' presente un solo \\t assume che cio' che lo precede sia un URL e ciò che lo"
						+ " segue sia un contenuto HTML; se sono presenti due o piu' \\t assume che cio' che precede il primo \\t"
						+ " sia un URL, cio' che sta tra il primo e il secondo \t sia il contenuto String di una pagina e cio'"
						+ " che sta tra ogni altra coppia adiacente di \\t sia un link.\nNel caso di input da tastiera, la procedura"
						+ " si blocchera' non appena ricevera' una stringa che non e' nel formato richiesto: a tal punto l'utente"
						+ " potra' decidere se ritentare oppure terminare. Dunque, se si fa input da tastiera, per terminare"
						+ " digitare qualsiasi stringa senza tab, per esempio.\n Da dove desiderate leggere?");
				
				int streamCh = SitoWeb.choiceList(sc, pr, new ArrayList<String>(Arrays.asList
						("Da tastiera."
						,"Da file.")), stopSignal);
				switch(streamCh) {
				
				case 1: {
					int counter = 0;
					boolean redo = true;
					
					do {
						try {
							counter += TreBiCa.leggiPagine(sc);
							pr.println("Formato stringa non valido. Riprovare?");
						} catch (UrlNonValidoException e) {
							pr.println("Formato URL non valido. Riprovare?");
						} finally {
							int goOnCh = SitoWeb.choiceList(sc, pr,
									new ArrayList<String>(Arrays.asList("Si'", "No, esci.")), stopSignal);
							switch (goOnCh) {
							case -1: case 2: 
								redo = false;
								break;
							case 1: redo = true;
							}
						} 
					} while (redo);
				
				switch(counter) {
				case 0: 
					pr.println("Non e' stata aggiunta nessuna pagina. :(\n");
					break;
				case 1:
					pr.println("E' stata aggiunta 1 pagina. :)\n");
					break;
				default: 
					pr.println("Sono state aggiunte " + counter + " pagine! :)\n");
				}
				
				break;
				
				}
				
				
				case 2: {
					
					pr.println("Digitare il percorso del file da cui leggere:\n");
					String perc = "";
					boolean redo = true;
					
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
					
					
					switch(counter) {
					case 0: 
						pr.println("Non e' stata aggiunta nessuna pagina. :(\n");
						break;
					case 1:
						pr.println("E' stata aggiunta 1 pagina. :)\n");
						break;
					default: 
						pr.println("Sono state aggiunte " + counter + " pagine! :)\n");
					}
					
					break;
					
				}
			
				case -1: 
					pr.println("Processo abortito.");
				
				}
				
				
				break;
				
				
			}	
				
			
			// 3. COSTRUISCI SITO WEB
			case 3: {
				SitoWeb s = SitoWeb.websiteBuilder(sc, pr, stopSignal); // Codice modulizzato
				if( TreBiCa.aggiungiPagine(s.getStore()) )
					pr.println("Sito web aggiunto a TreBiCa!! :) \n");
				else
					pr.println("Nulla e' stato aggiunto. Se hai costruito delle pagine, sono state aggiornate :)\n");
				
				break;
				
			}
				
			// 4. ESEGUI UNA RICERCA
			case 4: {
				pr.print("Digitare la stringa da cercare:\n> ");
				
				String query;
				boolean redo = true;
				do {
					try {
						query = sc.nextLine();
						TreBiCa.ricerca(sc, pr, query, false, stopSignal);
						redo = false;
					} catch (NoSuchElementException e) {
						pr.println("Errore di input, riprova.");
						redo = true;
					} 
				} while (redo);
				
				break;
				
				
			}	
				
				
			// 5. MI SENTO FORTUNATO
			case 5: {
				pr.print("Digitare la stringa da cercare:\n> ");
				
				String query2;
				boolean redo2 = true;
				do {
					try {
						query2 = sc.nextLine();
						TreBiCa.ricerca(sc, pr, query2, true, stopSignal);
						redo2 = false;
					} catch (NoSuchElementException e) {
						pr.println("Errore di input, riprova.");
						redo2 = true;
					} 
				} while (redo2);
				
				pr.println("Premere invio per continuare.");
				sc.nextLine();
				
				break;
				
			}
			
			
			
			// 6. VISUALIZZA TUTTE LE PAGINE WEB DI TREBICA
			case 6: {
				pr.println("Ecco la lista di tutte le pagine web presenti in TreBiCa attualmente, ordinate"
						+ " per indegree decrescente.\n");
				
				TreBiCa.ricerca(sc, pr, "", false, stopSignal); // Cerca la stringa vuota, presente in tutte le pagine
				
			}
			
			}
			
			
			
			pr.println("MENU");
			scelta=SitoWeb.choiceList(sc, pr,scelte, stopSignal);
		}

		return;
	}
}