package hundirlaflota;

import java.util.Random;
import java.util.Scanner;

public class HundirLaFlota {
	
	//Constantes para representar algunos valores
	final static char AGUA_NO_TOCADO = '.';
	final static char AGUA = 'A';
	final static char TOCADO = 'X';
	
	//Tama帽o del tablero
	final static int TAMANIO = 10;
	
	private static Scanner sc;
	

	public static void main(String[] args) {
		
		//La clase Scanner nos permite, junto con System.in leer desde el teclado
		sc = new Scanner(System.in);
		
		//Mapa del usuario y del ordenador
		char[][] mapaUsuario = new char[TAMANIO][TAMANIO];
		char[][] mapaOrdenador = new char[TAMANIO] [TAMANIO];
		
		//Mapa para anotar las tiradas que hacemos sobre el tablero del ordenador
		char[][] mapaOrdenadorParaUsuario = new char[TAMANIO][TAMANIO];
		
		//Puntos con los que comienzan las partidas
		int puntosUsuario = 24;
		int puntosOrdenador = 24;
		
		//Para controlar el juego
		boolean juegoTerminado = false;
		
		//Indica si el tiro es correcto
		boolean tiroCorrecto = false;
		
		//Posiciones de la tirada
		int[] tiro = new int[2];
		
		inicializacion(mapaUsuario, mapaOrdenador);
		
		while(!juegoTerminado) {
			
			//Al principio del turno, pintamos el mapa del usuario
			System.out.println("Mapa del Usuario");
			imprimirMapa(mapaUsuario);
			
			System.out.printf("PUNTOS RESTANTES DEL JUGADOR: %d\n", puntosUsuario);
			System.out.println("TURNO DEL JUGADOR");
			
			//Comenzamos con la tirada del usuario
			tiroCorrecto = false;
			while(!tiroCorrecto) {
				//Solicitamos los datos por teclado.
				tiro = pedirCasilla();
				
				//Verificamos si el tiro es correcto o no
				if(tiro[0] != -1 && tiro[1] != -1) {
					//Puede ser INCORRECTO porque ya haya tirado sobre esas coordenadas
					tiroCorrecto = evaluarTiro(mapaOrdenador, tiro);
					if(!tiroCorrecto) {
						System.out.println("TIRO INCORRECTO");
					}
				} else {
					System.out.println("TIRO INCORRECTO");
				}
				
				//De no serlo, el jugador debe volver a tirar
			}
			
			//Actualizamos mapa del ordenador y los puntos
			int puntosOrdenadorAnterior = puntosOrdenador;
			puntosOrdenador = actualizarMapa(mapaOrdenador, tiro, puntosOrdenador);
			
			//Actualizamos nuestro mapa de registro y lo imprimimos
			//Sabemos si la tirada ha sido AGUA o TOCADO si el n鷐ero de puntos se ha decrementado
			char tipoTiro = (puntosOrdenadorAnterior - puntosOrdenador) > 0 ? TOCADO : AGUA;
			actualizarMapaRegistro(mapaOrdenadorParaUsuario, tiro, tipoTiro);
			System.out.println("\nREGISTRO DEL MAPA DEL ORDENADOR");
			imprimirMapa(mapaOrdenadorParaUsuario);
			
			//El juego termina si el n鷐ero de puntos llega a 0
			juegoTerminado = (puntosOrdenador == 0);
			
			//Si no ha ganado el jugador, le toca a la m醧uina
			if(!juegoTerminado) {
				
				System.out.printf("PUNTOS RESTANTES DEL ORDENADOR: %d\n\n", puntosOrdenador);
				System.out.println("TURNO DEL ORDENADOR");
				
				tiroCorrecto = false;
				
				//Seguimos los mismos par醡etros de comprobaci髇 que en la tirada del Usuario
				while(!tiroCorrecto) {
					tiro = generaDisparoAleatorio();
					tiroCorrecto = evaluarTiro(mapaUsuario, tiro);
				}
			}
			
			//Actualizamos mapa
			puntosUsuario = actualizarMapa(mapaUsuario, tiro, puntosUsuario);
			
			//El juego termina si el n鷐ero de puntos llega a 0
			juegoTerminado = (puntosUsuario == 0);
			
		}//Fin de la PARTIDA, alguien ha ganado
		
		if(puntosOrdenador == 0) {
			System.out.println("EL GANADOR ES EL JUGADOR");
		}else {
			System.out.println("EL GANADOR ES EL ORDENADOR");
		}
		
		sc.close();

	}
	
	//M茅todo que devuelve un n煤mero aleatorio
	private static int aleatorio() {
		Random r = new Random(System.currentTimeMillis());
		
		return r.nextInt(TAMANIO);
	}
	
	//M茅todo para que el ordenador dispare un tiro aleatorio
	private static int[] generaDisparoAleatorio() {
		return new int[] {aleatorio(), aleatorio()};
	}
	
	//M茅todo que aglutina la inicializaci贸n de ambos mapas
	public static void inicializacion(char[][] m1, char[][] m2) {
		inicializaMapa(m1);
		inicializaMapa(m2);
	}
	
	/*
	 * M茅todo que inicializa un mapa de juego, colocando
	 * los barcos sobre el mismo.
	 */
	private static void inicializaMapa(char[][] mapa) {
		//Inicializamos el mapa entero a AGUA_NO_TOCADO
		for(int i=0; i<TAMANIO; i++) {
			for(int j=0; j<TAMANIO; j++) {
				mapa[i][j] = AGUA_NO_TOCADO;
			}
		}
		
		//2 portaaviones: 5 casillas
		//3 buques: 3 casillas
		//5 lanchas: 1 casilla
		int barcos[] = {5, 5, 3, 3, 3, 1, 1, 1, 1, 1};
		
		//Posible direcci贸n de colocaci贸n de los barcos
		char[] direccion = {'V', 'H'};
		
		//Para cada barco
		for(int b : barcos) {
			//Intentamos tantas veces como sea necesario colocar el barco en el mapa
			//Vamos de mayor tama帽o a menor
			
			boolean colocado = false;
			
			while(!colocado) {
				//Obtenemos una posici贸n y direcci贸n aleatorias.
				
				int fila = aleatorio();
				int columna = aleatorio();
				char direcc = direccion[aleatorio() % 2];
				
				//驴Cabe el barco en la posici贸n indicada?
				if(direcc == 'V') {
					if(fila + b <= (TAMANIO-1)) {
						//Comprobamos que no hay otro barco que se solape
						boolean otro = false;
						
						for(int i=0; (i<= fila+b) && !otro; i++) {
							if(mapa[i][columna] != AGUA_NO_TOCADO) {
								otro = true;
							}
						}
						
						//Si no hay otro barco, lo colocamos
						if(!otro) {
							for(int i = fila; i < fila + b; i++) {
								mapa[i][columna] = Integer.toString(b).charAt(0);
							}
							colocado = true;
						}
					}
				} else { // direcc == 'H'
					if (columna + b <= (TAMANIO - 1)) {
						// comprobamos que no hay otro barco que se solape
						boolean otro = false;
						for (int j = columna; (j <= columna + b) && !otro; j++) {
							if (mapa[fila][j] != AGUA_NO_TOCADO)
								otro = true;
						}
						// Si no hay otro barco, lo colocamos
						if (!otro) {
							for (int j = columna; j < columna + b; j++) {
								mapa[fila][j] = Integer.toString(b).charAt(0);
							}
							colocado = true;
						}
					}
				}
			}
		}
		
	}
	
	public static void imprimirMapa(char[][] mapa) {
		//Calculamos las letras seg鷑 el tama駉
		char[] letras = new char[TAMANIO];
		for(int i=0; i < TAMANIO; i++) {
			letras[i] = (char) ('A' + i);
		}
		
		//Imprimimos la fila de encabezado
		System.out.print("   ");
		for(int i=0; i<TAMANIO; i++) {
			System.out.print("[" + i + "] ");
		}
		
		System.out.println("");
		
		//Imprimimos el resto de filas
		for(int i=0; i<TAMANIO; i++) {
			System.out.print("[" + letras[i] + "] ");
			
			for(int j=0; j < TAMANIO; j++) {
				System.out.print(mapa[i][j] + "   ");
			}
			
			System.out.println("");
		}
	}
	
	//M閠odo mediante el cual el usuario introduce una casilla
	private static int[] pedirCasilla() {
		
		System.out.println("Introduzca una casilla: (por ejemplo B4)");
		String linea = sc.nextLine();
		
		//Pasamos la cadena a may鷖culas
		linea = linea.toUpperCase();
		int[] t;
		
		//Comprobamos que lo que ha introducido el usuario es cierto mediante una expresi髇 regular.
		if(linea.matches("^[A-Z][0-9]*$")) {
			//Obtenemos la letra
			//Suponemos que como mucho, obtenemos una letra del abecedario
			char letra = linea.charAt(0);
			//El numero de  fila es VALOR_NUMERICO(LETRA) - VALOR_NUMERICO(A).
			int fila = Character.getNumericValue(letra) - Character.getNumericValue('A');
			//Para la columna tan solo tenemos que procesar el n鷐ero
			int columna = Integer.parseInt(linea.substring(1, linea.length()));
			
			//Si las coordenadas est醤 dentro del tama駉 del tablero, las devolvemos
			if(fila >= 0 && fila < TAMANIO && columna <= TAMANIO) {
				t = new int[] {fila, columna};
			}else { //En caso contrario, devolvemos -1 para que vuelva a solicitar el tiro
				t = new int[] {-1, -1};
			}
		}else {
			t = new int[] {-1, -1};
		}
	
		return t;
	}
	
	/*
	 * M閠odo que nos permite evaluar si un tiro es correcto (AGUA o TOCADO)
	 * o se trata de una casilla por la que ya hemos pasado antes
	 * 
	 */
	
	public static boolean evaluarTiro(char[][] mapa,  int[] t) {
		int fila = t[0];
		int columna = t[1];
		
		return mapa[fila][columna] == AGUA_NO_TOCADO || (mapa[fila][columna] >= '1' && mapa[fila][columna] <= '5');
	}
	
	/*
	*
	* M閠odo que actualiza el mapa, con un determinado tiro.
	* Devolvemos el n鷐ero de puntos restantes
	* 
	*/
	private static int actualizarMapa(char[][] mapa, int[] t, int puntos) {
		int fila = t[0];
		int columna = t[1];
		
		if(mapa[fila][columna] == AGUA_NO_TOCADO) {
			mapa[fila][columna] = AGUA;
			System.out.println("AGUA");
		}else {
			mapa[fila][columna] = TOCADO;
			System.out.println("HAS ALCANZADO ALG贜 BARCO");
			--puntos;
		}
		
		return puntos;
	}
	
	/*
	 * M閠odo que actualiza el mapa de registro
	 */
	private static void actualizarMapaRegistro(char[][] mapa, int[] t, char valor) {
		int fila = t[0];
		int columna = t[1];
		
		mapa[fila][columna] = valor;
	}
	
}	