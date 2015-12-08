import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;


public class Main {
	static int n;
	static ArrayList<Integer>[] graph;
	static ArrayList<Integer>[] costos;
	static int [][] distances;
	static int [][] matrix;
	static boolean [][] present;
	static LinkedList<Integer> ll;
	static BufferedReader br;
	static StringTokenizer st;
	static StringBuilder sb;
	public static void main(String[] args) throws IOException {
		sb=new StringBuilder();
		String graphFileName = args[0];
		String coexpresedFileName = args[1];
		String validatedFileName=args[2];
		n=0;
		foundManyNodes(graphFileName);
		foundManyNodes(validatedFileName);
		initializeTrn();
		readGraph(graphFileName);
		floydWarshall();
		int res = lombardeGraph(coexpresedFileName);
		prepareToPrint();
		System.out.println(res);
		System.out.print(sb);
	}
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
	 * Aplica el algoritmo de Lombarde
	 * @param coexpresedFileName archivo con las coexpresiones
	 * @return la cantidad de veces que no hizo nada
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	static int lombardeGraph(String coexpresedFileName) throws FileNotFoundException, IOException {
		br=new BufferedReader(new FileReader(coexpresedFileName));
		int res=0;
		while (true){
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int a=Integer.parseInt(st.nextToken().substring(3))-1;
			int b=Integer.parseInt(st.nextToken().substring(3))-1;
			if (distances[b+n][a]<0){
				res++;
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
					}
					else if(aux3>=0 && aux4>=0 && aux3+c+aux4==d){
						present[i][j]=true;
					}
					else{
						ll.add(i);
						ll.add(j);
					}
				}
			}
		}
		return res;
	}
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
	static void readGraph(String graphFileName) throws FileNotFoundException, IOException {
		br=new BufferedReader(new FileReader(graphFileName));
		while (true){
			String linea=br.readLine();
			if (linea==null) break;
			st=new StringTokenizer(linea);
			int a=Integer.parseInt(st.nextToken().substring(3))-1;
			int b=Integer.parseInt(st.nextToken().substring(3))-1;
			int c=Integer.parseInt(st.nextToken());
			ll.add(a);
			ll.add(b);
			graph[a].add(b);
			costos[a].add(c);
			graph[n+b].add(a);
			graph[n+b].add(c);
			distances[a][b]=c;
			distances[n+b][n+a]=c;
			matrix[a][b]=c;
		}
		br.close();
		for (int i = 0; i < n; i++) {
			graph[i+n].add(i);
			costos[i+n].add(0);
			distances[n+i][i]=0;
		}
	}
	static void initializeTrn() {
		graph=new ArrayList[2*n];
		costos=new ArrayList[2*n];
		for (int i = 0; i < 2*n; i++) {
			graph[i]=new ArrayList<Integer>();
			costos[i]=new ArrayList<Integer>();
		}
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
	 * Encuentra la cantidad de nodos minimas que tiene que tener un grafo para tener las aristas mencionadas en un archivo
	 * @param fileName Archivo donde hay ids de nodos
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
