/**
 * <javadoc destdir="javadoc" overview="src/overview.html">./</javadoc>
 */
package gioco;
import java.io.*;

/**
 * Thread che sarà usato solo da Client_Acc
 * per ricevere il messaggio "Sono Pronto".
 * @author Cinti Mattia, Giannini Luca 5°AIF
 *
 */
public class ThreadPronto extends Thread{
	/**
	 * Canale per la ricezione.
	 */
	private BufferedReader in;
	/**
	 * Variabile da modificare per capire lo stato della connessione.
	 */
	private String cod;
	/**
	 * Costruttore.
	 * @param in canale di ricezione.
	 * @param cod codice da modificare.
	 */
	public ThreadPronto(BufferedReader in, String cod) {
		this.in = in;
		this.cod = cod;
	}

	/**
	 * Metodo per ricevere messaggio "Sono pronto".
	 * L'esecuzione è contemporanea all'inserimento della matrice del client Acc.
	 */
	public void run() {
		
		try {
			in.readLine();
		} catch (IOException e) {
			cod="2";
		}
		
	}
}
