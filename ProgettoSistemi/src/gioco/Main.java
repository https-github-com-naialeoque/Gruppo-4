/**
 * <javadoc destdir="javadoc" overview="src/overview.html">./</javadoc>
 */
package gioco;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;
/**
 * Classe che controlla tutta l'esecuzione del programma.
 * @author Cinti Mattia, Giannini Luca 5°AIF
 *
 */
public class Main {
	/**
	 * Socket per la connessione al server.
	 */
	private static Socket perServer;
	/**
	 * Canale di trasmissione.
	 */
	private static DataOutputStream out;
	/**
	 * Canale di ricezione.
	 */
	private static BufferedReader in;
	/**
	 * Scanner per I/O.
	 */
	private static Scanner sc;
	/**
	 * Variabile dove andrà l'ip dell'avversario se Scc, altrimenti
	 * sarà riempita con "vuoto".
	 */
	private static String ip="-";
	/**
	 * Variabile dove andrà il nome dell'avversario.
	 */
	private static String avversario;
	/**
	 * Variabile per capire se la connessione è ancora attiva o meno.
	 */
	private static boolean connection;
	/**
	 * Variabile per memorizzare l'username.
	 */
	private static String user;
	/**
	 * Variabile per memorizzare la password.
	 */
	private static String pass;
	
	/**
	 * Metodo main che gestirà l'intera esecuzione.
	 * @param args argomento del main.
	 */
	public static void main(String[] args) {
		String CFP="";

		do {
			connection=true;
			boolean isFirst=true;

			stabilisciConnessione();
			if(!connection) return;

			autenticazione(isFirst);
			if(!connection) return;

			ricercaAvversario();
			if(!connection) return;

			Client c=new Client(ip);

			CFP=c.incomincia();
			//a seconda del codice di fine partita eseguo delle stampe
			switch(CFP) {
				case "0": {
					System.out.println("Hai vinto");
					break;
				}
				case "1":{
					System.out.println("Hai perso");
					break;
				}
				case "2":{
					System.out.println("Errore nella connessione!");
					return;
				}
				case "3":{
					System.out.println("L'avversario non si è connesso. Ricerco una nuova partita.");
					isFirst=false;
					break;	
				}
			}
		try {
			if(!(CFP.equals("3"))) out.writeBytes(CFP+"\n");
			else out.writeBytes("2\n");
		}
		catch(Exception e) {
			System.out.println("Errore nella connessione con il server! L'esecuzione termina.");
			chiudiConnessione();
			return;
		}
		}while(CFP.equals("3"));
	}
	/**
	 * Metodo per chiudere la connessione.
	 */
	private static void chiudiConnessione() { 
		//rilascio risorse
		try {
			connection=false;
			in.close();
			out.close();
			perServer.close();
		}
		catch(Exception e) {}
	}
	/**
	 * Metodo per gestire la parte lato client della
	 * ricerca del avversario.
	 */
	private static void ricercaAvversario() {

		boolean check;
		int MW=0;
		//inserimento MW
		do { 
			check=false;
			System.out.println("Inserisci quanti secondi sei disposto ad aspettare per trovare un avversario.");
			System.out.println("Se vuoi che questo tempo sia illimitato inserisci 0.");
			sc=new Scanner(System.in);
			try {
				MW=sc.nextInt();
			}
			catch(Exception e) {
				check=true;
				System.out.println("Non hai inserito un numero! Riprova.");
			}
			if(MW<0) {
				check=true;
				System.out.println("Hai inserito un numero negativo!; Riprova.");
			}
		}while(check);

		
		//prendo i dati per stabilire la connessione con il client
		try {
			
			//invio MW al server
			out.writeBytes(MW+"\n"); 

			System.out.println("Sto cercando un avversario...");
			
			String ricevuto=in.readLine();
			//se ricevuto	->	"x.x.x.x:avversario"
			if(ricevuto.contains(":")) {
				ip=ricevuto.split(":")[0];
				avversario=ricevuto.split(":")[1];
			}
			//se ricevuto	->	"avversario"
			else {
				avversario=ricevuto;
				ip="vuoto";
			}

		} 
		//connessione caduta
		catch (IOException e) {
			System.out.println("Connessione caduta!!; L'esecuzione termina");
			chiudiConnessione();
			return;
		}
		//controllo risposta del server
		if(avversario.equalsIgnoreCase("Timeout")) {
			System.out.println("Non è stato trovato alcun avversario! L'esecuzione termina.");
			chiudiConnessione();
			return;
		}
		System.out.println("Il tuo avversario sarà:"+avversario);
	}
	/**
	 * Metodo per stabile la connessione con il server.
	 */
	private static void stabilisciConnessione() {
		try {
			//stabilisco i canali di comunicazione
			perServer=new Socket("localhost",1998);
			out=new DataOutputStream(perServer.getOutputStream());
			in=new BufferedReader(new InputStreamReader(perServer.getInputStream()));

		} 
		//errore nella connessione
		catch (IOException e) { 

			System.out.println("Server non disponibile!!; L'esecuzione termina");
			chiudiConnessione();

		}
	}
	/**
	 * Metodo per complatare l'autenticazione lato client.
	 * @param isFirst boolean che indica se true prima volta che si esegue il metodo.
	 */
	private static void autenticazione(boolean isFirst) {
		//questa parte viene eseguita se CFP=4
		if(!isFirst) {
			//creazione messaggio per il server
			String daSpedire=0+"#"+user+"#"+pass+"\n";
			
			try {
				//invio il messaggio
				out.writeBytes(daSpedire); 

				//prendo la risposta del server
				String daRicevere=in.readLine();
				return;
			}
			//errore nella connessione
			catch(IOException e) {
				System.out.println("Connnessione Caduta!; L'esecuzione termina.");
				chiudiConnessione();
				return;
			}
		}
		boolean check;
		try {
			//inserimento dell' opzione 1 se login 2 se registrazione
			do {
				check=false;
				boolean checkII;
				int var = 0;
				do {
					checkII=false;
					try {
						System.out.println("Cosa vuoi fare?");
						System.out.println("Digita 1 per effettuare il login.");
						System.out.println("Digita 2 per registrarti.");

						sc=new Scanner(System.in);
						var=sc.nextInt();
						
						if(var<1||var>2) System.out.println("Non hai inserito un numero tra 1 e 2! Riprova.");
					}
					catch(Exception e) {
						System.out.println("Non hai inserito un numero! Riprova.");
						checkII=false;
					}
				}while(checkII);
				//login
				if(var==1) {


					//inserimento dell'username
					sc=new Scanner(System.in);
					System.out.println("Inserisci il tuo username.");
					user=sc.nextLine();

					//inserimento della password
					sc=new Scanner(System.in);
					System.out.println("Inserisci la password.");
					pass=sc.nextLine();

					//applico sha512 alla stringa
					pass=encryptThisString(pass);

					//creazione messaggio per il server
					String daSpedire=0+"#"+user+"#"+pass+"\n";

					//invio il messaggio
					out.writeBytes(daSpedire); 

					//prendo la risposta del server
					String daRicevere=in.readLine();

					//controllo risposta
					if(daRicevere.equals("0")) { 
						System.out.println("Login effettuato con successo!");
					}
					else {
						System.out.println("Errore nel login! Ritenta.");
						check=true;
					}
				}
				//registrazione
				else {


					//inserimento dell'username
					sc=new Scanner(System.in);
					System.out.println("Inserisci il tuo username.");
					user=sc.nextLine();

					//inserimento della password
					sc=new Scanner(System.in);
					System.out.println("Inserisci la password.");
					pass=sc.nextLine();

					//applico sha512 alla stringa
					pass=encryptThisString(pass);

					//creazione messaggio per il server
					String daSpedire=1+"#"+user+"#"+pass+"\n";

					//invio il messaggio
					out.writeBytes(daSpedire);

					//prendo la risposta del server
					String daRicevere=in.readLine(); 

					//controllo risposta
					if(daRicevere.equals("0")) {
						System.out.println("Registrazione effettuata con successo!");
					}
					else {
						System.out.println("Errore nella registrazione! Ritenta.");
						check=true;
					}
					
				}
			}while(check);
		}
		//errore nella connessione
		catch(IOException e) {
			System.out.println("Connnessione Caduta!; L'esecuzione termina.");
			chiudiConnessione();
		}
	}
	/**
	 * Disclamer: Metodo trovato su internet Fonte: <a href="url">https://www.geeksforgeeks.org/sha-512-hash-in-java/</a>
	 * Metodo per criptare una password con argorimto "SHA-512".
	 * @param input password da criptare.
	 * @return password criptata.
	 */
	private static String encryptThisString(String input)
	{
		try {
			// getInstance() method is called with algorithm SHA-512
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			// digest() method is called
			// to calculate message digest of the input string
			// returned as array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);

			// Add preceding 0s to make it 32 bit
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			// return the HashText
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
		}
		return "";
	}
}
