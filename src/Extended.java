import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * 
 * @author Sebastian Tapia
 * Este es el algoritmo para encontrar el grafo extendido de Lombarde, sus entradas
 * son 3 string de la forma: archivo1.txt archivo2.txt archivo3.txt, donde:
 * el archivo 1 contiene la putative TRN
 * el archivo 2 contiene las coexpresiones
 * el archivo 3 contiene los pares validados.
 */
public class Extended {
	static int n;
	/*
	static ArrayList<Integer>[] graph;
	static ArrayList<Integer>[] costos;
	*/
	static int [][] distances;
	static int [][] matrix;
	static boolean [][] present;
	static LinkedList<Integer> ll;
	static BufferedReader br;
	static StringTokenizer st;
	static StringBuilder sb;
	static StringBuilder arcs;
	public static void main(String[] args) throws IOException {
		sb=new StringBuilder();
		arcs= new StringBuilder();
		String graphFileName = args[0];
		String coexpresedFileName = args[1];
		String validatedFileName=args[2];
		n=0;
		foundManyNodes(graphFileName);
		foundManyNodes(validatedFileName);
		initializeTrn();
		readGraph(graphFileName,validatedFileName);
		floydWarshall();
		int NonCoexp = lombardeGraph(coexpresedFileName);
		int YesValid = validatedArcs(validatedFileName);
		prepareToPrint();
		System.out.println("cantidad de coexpresiones no explicadas: "+NonCoexp);
		System.out.println("cantidad de arcos validados usados: "+YesValid);
		System.out.print(sb);
		/**
		 * Si interesa la evolución de los arcos, descomentar las siguientes 2 lineas
		 *
		 *System.out.print("La evolución de los arcos fue:");
		 *System.out.print(arcs);
		 */
	}
	/**
	 * Metodo genera el string que representa el grafo ouput del algoritmo
	 */
	static void prepareToPrint() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (present[i][j]){
					sb.append("opr");
					sb.append(i+1);
					sb.append(" opr");
					sb.append(j+1);
					sb.append(' ');
					sb.append(matrix[i][j]);
					sb.append('\n');
				}
			}
		}
	}
	/**
	 * Cuenta cuantos arcos validados se encuentran en el grafo de Lombarde.
	 * @param validatedFileName archivo con los validados.
	 * @return cantidad de arcos validados en el grafo de Lombarde.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static int validatedArcs(String validatedFileName) throws FileNotFoundException, IOException {
		int YesValid=0;
		br=new BufferedReader(new FileReader(validatedFileName));
		while (true){
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int a=Integer.parseInt(st.nextToken().substring(3))-1;
			int b=Integer.parseInt(st.nextToken().substring(3))-1;
			if (present[a][b]){
				YesValid++;
			}
		}
		br.close();
		return YesValid;
	}
	/**
	 * Aplica el algoritmo de Lombarde y da cuenta cuantas aristas entran en cada paso de la iteracion, mediante el string arcs.
	 * @param coexpresedFileName archivo con las coexpresiones.
	 * @return la cantidad de coexpresiones no explicadas.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static int lombardeGraph(String coexpresedFileName) throws FileNotFoundException, IOException {
		br=new BufferedReader(new FileReader(coexpresedFileName));
		int NonCoexp=0;
		while (true){
			int val=0;
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int a=Integer.parseInt(st.nextToken().substring(3))-1;
			int b=Integer.parseInt(st.nextToken().substring(3))-1;
			if (distances[b+n][a]<0){
				NonCoexp++;
			}
			else{
				int d=distances[b+n][a];
				int m=ll.size()/2;
				while (m-->0){
					int i=ll.poll();
					int j=ll.poll();
					int c=matrix[i][j];
					int aux1=distances[b+n][j+n];
					int aux2=distances[i+n][a];
					int aux3=distances[b+n][i];
					int aux4=distances[j][a];
					if (aux1>=0 && aux2>=0 && aux1+c+aux2==d){
						present[i][j]=true;
						val++;
					}
					else if(aux3>=0 && aux4>=0 && aux3+c+aux4==d){
						present[i][j]=true;
						val++;
					}
					else{
						ll.add(i);
						ll.add(j);
					}
				}
			}
			arcs.append(val);
			arcs.append('\n');
		}
		return NonCoexp;
	}
	/**
	 * Algoritmo Floyd Warshall
	 */
	static void floydWarshall() {
		int largo=2*n;
		for (int k = 0; k < largo; k++) {
			for (int i = 0; i < largo; i++) {
				for (int j = 0; j < largo; j++) {
					int aux=distances[i][j];
					int aux2=distances[i][k];
					int aux3=distances[k][j];
					if (aux2>=0 && aux3>=0){
						if (aux<0) distances[i][j]=aux2+aux3;
						else distances[i][j]=Math.min(aux,aux2+aux3);
					}
				}
			}
		}
	}
	/**
	 * Genera la matriz de adyacencia y la lista de adyacencia de la red en su versión extendida.
	 * @param graphFileName Archivo con la putative TRN.
	 * @param validatedFileName Archivo con los arcos validados.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static void readGraph(String graphFileName, String validatedFileName) throws FileNotFoundException, IOException {
		br=new BufferedReader(new FileReader(validatedFileName));
		ll=new LinkedList<Integer>();
		while (true){
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int a=Integer.parseInt(st.nextToken().substring(3))-1;
			int b=Integer.parseInt(st.nextToken().substring(3))-1;
			ll.add(a);
			ll.add(b);
			/*
			graph[a].add(b);
			costos[a].add(1);
			graph[n+b].add(a);
			costos[n+b].add(1);
			*/
			distances[a][b]=1;
			distances[n+b][n+a]=1;
			matrix[a][b]=1;
		}
		br.close();
		br=new BufferedReader(new FileReader(graphFileName));
		while (true){
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int a=Integer.parseInt(st.nextToken().substring(3))-1;
			int b=Integer.parseInt(st.nextToken().substring(3))-1;
			int c=Integer.parseInt(st.nextToken());
			/*Solo si el arco no era de los validados */
			if(matrix[a][b]==-1){ 
				ll.add(a);
				ll.add(b);
				/*
				graph[a].add(b);
				costos[a].add(c);
				graph[n+b].add(a);
				costos[n+b].add(c);
				*/
				distances[a][b]=c;
				distances[n+b][n+a]=c;
				matrix[a][b]=c;
			}
		}
		br.close();
		
		for (int i = 0; i < n; i++) {
			/*
			graph[i+n].add(i);
			costos[i+n].add(0);
			*/
			distances[n+i][i]=0;
		}
	}
	/**
	 * Da las condiciones iniciales para procesar la matriz de adyacencia y la lista de adyacencia del grafo.
	 */
	static void initializeTrn() {
		/*graph=new ArrayList[2*n];
		costos=new ArrayList[2*n];
		for (int i = 0; i < 2*n; i++) {
			graph[i]=new ArrayList<Integer>();
			costos[i]=new ArrayList<Integer>();
		}
		*/
		distances=new int[2*n][2*n];
		matrix=new int[n][n];
		present=new boolean[n][n];
		for (int i = 0; i < 2*n; i++) {
			for (int j = 0; j < 2*n; j++) {
				if (i==j){
					distances[i][j]=0;
				}
				else{
					distances[i][j]=-1;
				}
				try{
					matrix[i][j]=-1;
				}
				catch(IndexOutOfBoundsException e){
					continue;
				}
			}
		}
	}
	/**
	 * Encuentra la cantidad de nodos minima que tiene que tener un grafo para soportar la putativeTRN.
	 * @param fileName Archivo donde hay ids de nodos, se usa con la putativeTRN y con los arcos validados.
	 * @throws IOException
	 */
	static void foundManyNodes(String fileName) throws IOException {
		br=new BufferedReader(new FileReader(fileName));
		while (true){
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int aux=Integer.parseInt(st.nextToken().substring(3));
			int aux2=Integer.parseInt(st.nextToken().substring(3));
			n=Math.max(n, aux);
			n=Math.max(n, aux2);
		}
		br.close();
	}
}
