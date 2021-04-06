/**
 * <javadoc destdir="javadoc" overview="src/overview.html">./</javadoc>
 */
package gioco;
import java.util.*;
import java.io.*;
/**
 * Questa classe è quella che interagisce direttamente con le matrici di gioco.
 * @author Cinti Mattia, Giannini Luca 5°AIF
 * 
 * 
 */
public class Giocatore {
	/**
	 * Contatore delle navi rimanenti.
	 */
	private HashMap<String,Integer> contatore;
	/**
	 * Matrice creata dal giocatore.
	 */
	private char campoMio[][];
	/**
	 * Matrice dell'avversario da comporre durante l'esecuzione.
	 */
	private char campoAvv[][];
 	
	/**
	 * COstruttore che inizializza le variabili.
	 */
	public Giocatore() {
		
		contatore=new HashMap<>(); //costruttore
		inizializzaCont();
		campoMio=CampoDiGioco.campo();
		campoAvv=CampoDiGioco.campoAvv();
		
	}
	
	
	
	/**
	 * Metodo che inizializza il contatore delle navi.
	 */
	private void inizializzaCont() {
		//navi da 4
		contatore.put("0", 4); 
		//navi da 3
		contatore.put("1", 3); 
		contatore.put("2", 3);
		//navi da 2
		contatore.put("3", 2);
		contatore.put("4", 2);
		contatore.put("5", 2);
		//navi da 1
		contatore.put("6", 1);
		contatore.put("7", 1);
		contatore.put("8", 1);
		contatore.put("9", 1);
	}
	
	/**
	 * Funzione per aggiornare il contatore.
	 * @param riga della coordinata.
	 * @param colonna della coordinata.
	 * @return 0 se acqua; 1 se colpito; 2 se affondato; 3 se vittoria.
	 */
	public int generaRisposta(int riga,int colonna) {
		int i=0;
		
		String cella=campoMio[riga][colonna]+"";
		if(!(cella.equals("*"))) {
			//prendo il valore
			int app=contatore.get(cella); 
			app--; //decremento
			
			if(app!=0) { //colpito
				 //metto il valore decrementato
				contatore.put(cella,app);
				return 1;
			}
			//affondato
			contatore.remove(cella);
			i=2;
			//vittoria
			if(contatore.size()==0) i=3;
		}
		return i;
	}
	/**
	 * Metodo per aggiornare il campo avversario, dati riga, colonna e 
	 * codice di risposta(acqua,colpit, ecc...).
	 * @param riga riga della coordinata.
	 * @param colonna colonna della coordinata.
	 * @param cod codice di risposta.
	 */
	public void aggiornaCampoAvv(int riga,int colonna,int cod) {
		
		switch(cod) {
		
		case 0:{ //acqua
			campoAvv[riga][colonna]='~';
			break;
		}
		case 1:{ //colpito
			campoAvv[riga][colonna]='x';
			break;
		}
		case 2:{ //affondato
			campoAvv[riga][colonna]='-';
			//individuazione direzioni
			if(riga!=9) if(campoAvv[riga+1][colonna]=='x') affonda(riga+1,colonna,1);
			if(riga!=0) if(campoAvv[riga-1][colonna]=='x') affonda(riga-1,colonna,2);
			if(colonna!=9) if(campoAvv[riga][colonna+1]=='x') affonda(riga,colonna+1,3);
			if(colonna!=0) if(campoAvv[riga][colonna-1]=='x') affonda(riga,colonna-1,4);
			
			break;
		}
		
		}
	}
	/**
	 * 	Metodo per stampare i campi.
	 */
	public void stampaCampi() {
		//campo mio
    	System.out.print("  ");
		for (int u=1;u<=10;u++) System.out.print(u+" ");
		//campo avv
		System.out.print("\t    ");
		for (int u=1;u<=10;u++) System.out.print(u+" ");

		System.out.println("");
		
		for(int u=0;u<10;u++) {
			System.out.print((char)(u+65)+" ");			       
			for(int y=0;y<10;y++) System.out.print(campoMio[u][y]+" ");

			System.out.print("\t  ");
			System.out.print((char)(u+65)+" ");			       
			for(int y=0;y<10;y++) System.out.print(campoAvv[u][y]+" ");

			System.out.println("");
		}
	}
	


	/**
	 * Metodo ricorsivo per affondare una parte di nave.
	 * @param riga della coordinata da affondare.
	 * @param colonna della coordinata da affondare.
	 * @param dir direzione per provare a affondare un'eventuale parte di nave rimanente. 
	 */
	private void affonda(int riga,int colonna,int dir) {
		
		campoAvv[riga][colonna]='-';
		switch (dir) {
		//sopra
		case 1:{
			if(riga==9) return;
			if(campoAvv[riga+1][colonna]=='x') affonda(riga+1,colonna,dir);
			break;
		}
		//sotto
		case 2:{
			if(riga==0) return;
			if(campoAvv[riga-1][colonna]=='x') affonda(riga-1,colonna,dir);			
			break;
		}
		//destra
		case 3:{
			if(colonna==9) return;
			if(campoAvv[riga][colonna+1]=='x') affonda(riga,colonna+1,dir);
			break;
		}
		//sinistra
		case 4:{
			if(colonna==0) return;
			if(campoAvv[riga][colonna-1]=='x') affonda(riga,colonna-1,dir);	
			break;
		}
		}
	}
}
