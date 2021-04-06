/**
 * <javadoc destdir="javadoc" overview="src/overview.html">./</javadoc>
 */
package gioco;
import java.util.Scanner;
/**
 * Classe per il campo di gioco per la creazione del campo di gioco del giocatore
 * e del suo avversario.
 * @author Cinti Mattia, Giannini Luca 5°AIF
 *
 */
public class CampoDiGioco {
	
	/**
	 * Metodo posizionare una nave sulla matrice e con il suo intorno.
	 * @param i riga iniziale.
	 * @param j colonna iniziale.
	 * @param d orientamento nave.
	 * @param l lunghezza nave.
	 * @param p carattere da posizionare.
	 * @param m matrice da modificare.
	 * @return 0 se non è riuscito a posizione (es. nave sconfina dal campo) 1 se posiziona la nave.
	 */
	public static int rp (int i,int j,int d,int l,char p,char m[][]){  
	int uso=0;
	//controlli per navi orizzontali
	if(d==0 && j+l<=10) { 
		uso=1;
		m[i][j]=p;
		if(l>1)m[i][j+1]=p;
		if(l>2)m[i][j+2]=p;
		if(l>3)m[i][j+3]=p;
		if(i-1>=0)m[i-1][j]='+';
		if(i+1<=9)m[i+1][j]='+';
		if(j-1>=0)m[i][j-1]='+';
		if(j+4<=9&&l==4)m[i][j+4]='+';
		if(j+3<=9&&l==3)m[i][j+3]='+';
		if(j+2<=9&&l==2)m[i][j+2]='+';
		if(j+1<=9&&l==1)m[i][j+1]='+';
		if(i-1>=0&&j+1<=9&&l>1)m[i-1][j+1]='+';
		if(i+1<=9&&j+1<=9&&l>1)m[i+1][j+1]='+';
		if(i-1>=0&&j+2<=9&&l>2)m[i-1][j+2]='+';
		if(i+1<=9&&j+2<=9&&l>2)m[i+1][j+2]='+';
		if(i-1>=0&&j+3<=9&&l>3)m[i-1][j+3]='+';
		if(i+1<=9&&j+3<=9&&l>3)m[i+1][j+3]='+';
	}
	//controlli per navi verticali
    if(d==1 && i+l<=10){ 
	    uso=1;
	    m[i][j]=p;
	    if(l>1)m[i+1][j]=p;
	    if(l>2)m[i+2][j]=p;
	    if(l>3)m[i+3][j]=p;
	    if(j-1>=0)m[i][j-1]='+';
	    if(j+1<=9)m[i][j+1]='+';
	    if(i-1>=0)m[i-1][j]='+';
	    if(i+4<=9&&l==4)m[i+4][j]='+';
	    if(i+3<=9&&l==3)m[i+3][j]='+';
	    if(i+2<=9&&l==2)m[i+2][j]='+';
	    if(i+1<=9&&l==1)m[i+1][j]='+';
	    if(i+1<=9&&j-1>=0&&l>1)m[i+1][j-1]='+';
	    if(i+1<=9&j+1<=9&&l>1)m[i+1][j+1]='+';
	    if(i+2<=9&&j-1>=0&&l>2)m[i+2][j-1]='+';
	    if(i+2<=9&&j+1<=9&&l>2)m[i+2][j+1]='+';
	    if(i+3<=9&&j-1>=0&&l>3)m[i+3][j-1]='+';
	    if(i+3<=9&&j+1<=9&&l>3)m[i+3][j+1]='+';
       }	
    return uso;
	}
	/**
	 * Funzione per pulire la console. 
	 */
	public final static void clearConsole()
	{
	    try
	    {
	        final String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (final Exception e){}
	}
	/**
	 * Metodo che serve per generare il campo di gioco dell'avversario.
	 * @return campo generato.
	 */
	public static char[][] campoAvv(){
		
		char avv[][] = new char[10][10];
		
		for(int i=0;i<10;i++) for(int j=0;j<10;j++) avv[i][j]='*';
			
		return avv;
	}
	
	/**
	 * Metodo che serve per generare un campo di gioco.
	 * @return restituisce un campo da gioco per il giocatore di battaglia navale.
	 */
	public static char[][] campo(){
		//matrice da restituire
		char m[][] =new char[10][10]; 
		//riempio matrice
		for(int i=0;i<10;i++) for(int j=0;j<10;j++) m[i][j]='*'; 
		char q='0';
		inserimentoNave(m,4,q);
		q=(char) (q+1);
		for(int r=0;r<2;r++) { 
			inserimentoNave(m,3,q);
			q=(char) (q+1);
		}
		for(int r=0;r<3;r++) { 
			inserimentoNave(m,2,q);
			q=(char) (q+1);
			}
		for(int r=0;r<4;r++) {
			inserimentoNave(m,1,q);
			q=(char) (q+1);
		}
		//ripulisco la matrice dai +
		for(int h=0;h<10;h++) { 
		    for(int x=0;x<10;x++) if(m[h][x]=='+') m[h][x]='*';
		    q=(char) (q+1);
		}  
		return m;
	}
	/**
	 * Funzione che serve per stampare una matrice.
	 * @param m matrice da stampare.
	 */
	public static void stampaCampo(char m[][]) {
    	System.out.print("  ");
		for (int u=1;u<=10;u++) System.out.print(u+" ");
		System.out.println("");
		for(int u=0;u<10;u++) {
			System.out.print((char)(u+65)+" ");			       
			for(int y=0;y<10;y++) System.out.print(m[u][y]+" ");
			System.out.println("");
		}
	}
	/**
	 * Metodo per inserire una nave nella matrice.
	 * @param m matrice dove aggiungere la nave.
	 * @param l lunghezza nave.
	 * @param q carattere da posizionare.
	 */
	public static void inserimentoNave(char m[][],int l,char q) {
		int check,check1;
		do {
			Scanner sc;
			check=-1;
			check1=0;
			boolean uso;
			int i,j,d;
			i=j=d=-1;
			char c='0';
			stampaCampo(m);
			if(l==1) System.out.println("Inserire coordinate nave da 1");
			else System.out.println("Inserire coordinate iniziali nave da "+l);
			//inserimento riga
			do { 
				System.out.println("Inserire riga da A a J");				
				uso=false;
				try {
					sc=new Scanner(System.in);
					c=sc.next().charAt(0);
					i=(int)c-65;
					if((int)c<65||(int)c>74) {uso=true;System.out.println("Errore inserimento;Ripetere");} 
				}
				catch(Exception e) {uso=true;System.out.println("Errore inserimento;Ripetere");}
			}while(uso);
			//inserimento colonna
			do { 
				System.out.println("Inserire colonna da 1 a 10");				
				uso=false;
				try {
					sc=new Scanner(System.in);
					j=sc.nextInt();
					if(j<1||j>10) {uso=true;System.out.println("Errore inserimento;Ripetere");} 
					j--;
				}
				catch(Exception e) {uso=true;System.out.println("Errore inserimento;Ripetere");}
			}while(uso);
			//inserimento orientamento se necessario
			if(l!=1) { 
				do {
					System.out.println("Inserire orientamento 0=oriz 1=vert");				
					uso=false;
					try {
						sc=new Scanner(System.in);
						d=sc.nextInt();
						if(d!=0 && d!=1) {uso=true;System.out.println("Errore inserimento;Ripetere");} 
					}
					catch(Exception e) {uso=true;System.out.println("Errore inserimento;Ripetere");}
				}while(uso);
			}
			switch(l) {
			//nave da 4
			case 4:{ 
				check=rp(i,j,d,l,q,m);
				break;
			}
			//nave da 3
			case 3:{
				try {
					//controlli per coordinate nave
					if (d==0&&(m[i][j]!='*'||m[i][j+1]!='*'||m[i][j+2]!='*')) check1=2; 
					if (d==1&&(m[i][j]!='*'||m[i+1][j]!='*'||m[i+2][j]!='*')) check1=2;
				}catch(Exception e) {check1=2;}
				if(check1==0) check=rp(i,j,d,l,q,m);
				break;
			}
			//nave da 2
			case 2:{ 
				//controlli per coordinate nave
				try {
					if (d==0&&(m[i][j]!='*'||m[i][j+1]!='*')) check1=2; 
					if (d==1&&(m[i][j]!='*'||m[i+1][j]!='*')) check1=2;
				}catch(Exception e) {check1=2;}
				if(check1==0) check=rp(i,j,d,l,q,m);
				break;
			}
			//nave da 1
			case 1:{
				 //controlli per coordinate nave
				if (m[i][j]!='*') check1=2;
				if(check1==0) check=rp(i,j,1,l,q,m);
				break;
			}
			}
			clearConsole();
			if(check==0||check1==2) System.out.println("Errore inserimento coordinate;Ripetere");
		}while(check==0||check1==2);
	}
}