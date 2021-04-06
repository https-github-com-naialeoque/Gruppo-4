/**
 *
 * <javadoc destdir="javadoc" overview="src/overview.html">./</javadoc>
 */
package gioco;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Classe che si occupa di fare girare il gioco di battaglia navale.
 * Per inizializzare la classe dovrà essere invocato il costruttore e per far
 * partire il gioco dovrà essere incovocato il metodo incomincia.
 * @author Cinti Mattia, Giannini Luca 5°AIF
 *
 */
public class Client {
	/**
	 * Porta su cui si mette in attesa.
	 */
	private static int port = 1422;
	/**
	 * Canale per la ricezione.
	 */
	private static BufferedReader in;
	/**
	 * Canale per la trasmissione.
	 */
	private static DataOutputStream out;
	/**
	 * Variabile che memorizza l'ultima colonna ricevuta o inviata.
	 */
	private static int colonna;
	/**
	 * Variabile che memorizza l'ultima riga ricevuta o inviate.
	 */
	private static int riga;
	/**
	 * Boolean per lo stato della connessione.
	 */
	private static boolean connection;
	/**
	 * Socket per la connessione.
	 */
	private static Socket socket;
	/**
	 * Variabile per il tavolo da gioco.
	 */
	private static Giocatore tavolo;
	/**
	 * Variabile per memorizzare le celle già scelte in precedenza.
	 */
	private static ArrayList<String> lista;
	/**
	 * Variabile per memorizzare l'ip dell'avversario se sono Scc.
	 * 
	 */
	private String ip;


	/**
	 * Costruttore 
	 * Se gli viene passato come parametro un ip contenente "vuoto" la classe si comporterà
	 * come Client_Acc altrimenti se si comporterà come Client_Scc.
	 * @param ip Indirizzo dell'avversario.
	 */
	public Client(String ip) {
		lista=new ArrayList<>();
		connection=true;
		this.ip=ip;
	}
	/**
	 * Metodo per iniziare una partita.
	 * @return 0 se vittoria, 1 se sconfitta, 2 se la connessione cade a partita gia iniziata, 3 se avversario non si è connesso.
	 */
	public String incomincia() {
		
		if(ip.equals("vuoto")) attendi();
		else connetti();
		if(!connection) return "3";
		
		System.out.println("Connessione eseguita!");
		if(ip.equals("vuoto")) prePartitaAcc();
		else prePartitaScc();
		
		if(!connection) return "2";

		int c=0;
		if(ip.equals("vuoto")) {
			c=giocaAcc();
		}
		else c=giocaScc();
		switch(c) {
		case -1:{
			chiudiConnessione();
			return "2";
		}
		case 3: return "1";
		case 4: return "0";
		}

		return "";
	}
	/**
	 * Metodo per rilasciare le risorse e chiudere la connessione.
	 */
	private static void chiudiConnessione() { 
		//rilascio risorse
		try {
			connection=false;
			in.close();
			out.close();
			socket.close();
		}
		catch(Exception e) {}
	}
	/**
	 * Metodo che spara un colpo. Il metodo continua l'esecuzione fino a:
	 * -colpisce acqua.
	 * -errore nella connessione.
	 * -colpisce tutte le navi.
	 * @return -1 se connessione cade; 0 se acqua; 4 se vittoria.
	 */
	private int spara() {
		try {
			boolean check;
			int cod_ricevuto;
			do {

				check=true;
				//invio coordinate
				
				inviaCoordinate();
				//ricevo risposta e aggiorno il campo dell'avversario
				cod_ricevuto=Integer.parseInt(in.readLine());
				
				tavolo.aggiornaCampoAvv(riga, colonna,cod_ricevuto);
				tavolo.stampaCampi();
				//a seconda del codice ricevuto eseguo delle stampe
				switch(cod_ricevuto) {
					case 0:{
						System.out.println("Nessuna nave hai colpito! Cambio turno.");
						break;
					}
					case 1:{
						System.out.println("Hai colpito una nave.");
						break;
					}
					case 2:{
						System.out.println("Hai colpito e affondato una nave.");
						break;
					}
				}
				//se ricevo acqua o vittoria il ciclo termana
				if(cod_ricevuto==0 || cod_ricevuto==3) check=false;

			}while(check);
			//incremento di 1 vittoria perchè senno si confonde con il codice di sconfitta
			if(cod_ricevuto==3) cod_ricevuto++; 
			return cod_ricevuto;
		}
		//se connessione salta
		catch(IOException e) {
			return -1;
		}
	}

	/**
	 * Metodo che riceve un colpo. Il metodo continua l'esecuzione fino a:
	 * -invia acqua.
	 * -errore nella connessione.
	 * -nessuna nave è rimata.
	 * @return -1 se connessione cade; 0 se acqua ; 3 se sconfitta.
	 */
	private int ricevi() {
		try {
			boolean check;
			int cod_inviato;
			do {

				check=true;
				
				System.out.println("Attendi al mossa del tuo avversario.");
				//prendo le coordinate richieste dall'avversaroi
				String coordinate=in.readLine();
				//il primo carattere è sempre una lettera che ha un corrispondente valore intero compreso tra 65 e 74
				//eseguo sottrazione perchè la matrice lavora con gli interi
				riga=(int)(coordinate.charAt(0))-65;
				//dal secondo carattere in poi il messaggio sarà composto solamente da numeri
				coordinate=coordinate.substring(1);
				colonna=Integer.parseInt(coordinate)-1;
				//invio la risposta al client
				cod_inviato=tavolo.generaRisposta(riga, colonna);
				out.writeBytes(cod_inviato+"\n");
				//a seconda della risposta eseguo le stampe
				switch(cod_inviato) {
				case 0:{
					System.out.println("Nessuna nave ha colpito l'avversario! Cambio turno.");
					break;
				}
				case 1:{
					System.out.println("L'avversario ha colpito una nave.");
					break;
				}
				case 2:{
					System.out.println("L'avversario ha colpito e affondato una nave.");
					break;
				}				
				}
				//se l'avversario prende acqua o vince il ciclo termina
				if(cod_inviato==0||cod_inviato==3) check=false;
				
			}while(check);

			return cod_inviato;
		}
		//se salta la connessione
		catch(IOException e) {
			return -1;
		}
	}
	/**
	 * Metodo gioca se sono Scc (inizio il gioco ricevendo il colpo).
	 * @return -1 errore nella connessione; 3 sconfitta; 4 vittoria.
	 */
	private int giocaScc() {
		int cond;
		do {

			cond=ricevi();
			if(cond==-1|| cond==3) break;

			cond=spara();
			if(cond==-1|| cond==4) break;

		}while(true);
		return cond;
	}
	/**
	 * Metodo gioca se sono Scc (inizio il gioco sparando il colpo).
	 * @return -1 errore nella connessione; 3 sconfitta; 4 vittoria.
	 */
	private int giocaAcc() {
		int cond;
		do {

			cond=spara();
			if(cond==-1|| cond==4) break;

			cond=ricevi();
			if(cond==-1|| cond==3) break;

		}while(true);
		return cond;
	}
	/**
	 * Metodo che prepara i campi se sono acc e si mette conteporamente in attesa
	 * tramite un Thread per ricevere il messeggio "Sono pronto".
	 */
	private void prePartitaAcc()  {
		try {
			//creazione + ricezione messaggio "Sono pronto"
			String cod="";
			ThreadPronto t;
			t=new ThreadPronto(in,cod);
			t.start();
			//preparo il tavolo da gioco
			tavolo=new Giocatore(); 
			tavolo.stampaCampi();
			//aspetto messaggio
			t.join();
			System.out.println("L'avversario è pronto");
			//errore nella connessione
			if(cod.equals("2")) {
				chiudiConnessione();
				return; 
			}
		}
		catch(Exception e) {}
	}
	/**
	 * Metodo per inviare le coordinate all'avversario.
	 */
	private void inviaCoordinate() {
		//boolean per controllare se l'utente inserisce una coordinata corretta
		boolean uso;
		//boolean per controllare se l'utente inserisce una coordinata non ancora scelta
		boolean uso2;
		Scanner sc;
		char c = '0';
		int j = 0;
		String daSpedire;
		do {
			uso2=false;
			do { 
				//inserimento riga
				System.out.println("Inserire riga da A a J da inviare");				
				uso=false;
				try {
					sc=new Scanner(System.in);
					c=sc.next().charAt(0);
					if((int)c<65||(int)c>74) {uso=true;System.out.println("Errore inserimento;Ripetere");} 
					else riga=(int)c-65;
				}
				catch(Exception e) {uso=true;System.out.println("Errore inserimento;Ripetere");}

			}while(uso);

			do { 
				//inserimento colonna
				System.out.println("Inserire colonna da 1 a 10 da inviare");				
				uso=false;
				try {
					sc=new Scanner(System.in);
					j=sc.nextInt();
					if(j<1||j>10) {uso=true;System.out.println("Errore inserimento;Ripetere");}
					else colonna=j-1;
				}
				catch(Exception e) {uso=true;System.out.println("Errore inserimento;Ripetere");}
				daSpedire=c+""+j;

			}while(uso);
			//controllo se la coordinata non è già stata scelta
			if(lista.contains(daSpedire)) {
				System.out.println("Hai gia inserito queste coordinate! Riprova.");
				uso2=true;
			}
		}while(uso2);
		 //aggiungo la coordinata alla liste delle coordinate gia scelte e la invio
		try {
			lista.add(daSpedire);
			out.writeBytes(daSpedire+"\n");
		}
		catch (IOException e) {
			chiudiConnessione();
		}
	}
	/**
	 * Metodo per connettersi con l'avversario se sono Acc.
	 * Starà in attesa dell'avversario per un massimo di 40 sec.
	 */
	private void attendi() {

		socket = null;

		try {
			ServerSocket serverSocket = new ServerSocket(port);

			System.out.println("Aspetto che arrivi l'avversario");

			// bloccante, in attesa del client; MAX 40 sec
			serverSocket.setSoTimeout(40000);
			socket = serverSocket.accept();
			//creazione canali di comunicazione
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
			//chiudo la server socket
			serverSocket.close();
		
		} 
		//se questa istruzione viene eseguita l'avversario non si è connesso
		catch (IOException e) {
			chiudiConnessione();
		}

	}
	/**
	 * Metodo per connettersi con l'avversario se sono Scc.
	 * Il metodo proverà per 3 volte a connettersi con l'avversario a intervalli di 15 sec.
	 */
	private void connetti() {

		System.out.println("Mi sto connettendo con l'avversario");
		
		for(int i=0;i<3;i++) {
			socket=null;
			socket= new Socket();
			try {
				System.out.println("Tentavivo:"+i);
				//connessione con l'avversario
				socket.connect(new InetSocketAddress(ip,port));
				//creazione canale di comunicazione
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new DataOutputStream(socket.getOutputStream());
				return;
			}
			//se non trova un avversario in attesa si addormenta per 15 secondi
			catch (Exception e) {
				try {
					Thread.currentThread().sleep(15000);
				} catch (InterruptedException e1) {
				}
			}
		}
		//se questa istruzione viene eseguita l'avversario non è stato trovato
		chiudiConnessione();
	}
	/**
	 * Metodo che prepara i campi se sono Scc, una volta fatto ciò
	 * invia all'avversario il messaggio "Sono pronto".
	 */
	private void prePartitaScc()  {

		tavolo=new Giocatore(); 
		tavolo.stampaCampi();
		try {
			out.writeBytes("Sono pronto\n");
		}
		catch(IOException e) {
			chiudiConnessione();
		}
	}
}
