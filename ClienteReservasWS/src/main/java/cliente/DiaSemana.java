package cliente;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Enumeración para representar los días de la semana en español en minúsculas.
 */
public class DiaSemana {

	private static final List<String> DIAS_VALIDOS = Arrays.asList(
			"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"
			);


	public static boolean esDiaValido(String dia) {
		return DIAS_VALIDOS.contains(dia.toLowerCase());
	}

	public static List<String> getDias() {
		return DIAS_VALIDOS;
	}

	public static String leerDia(Scanner scanner) {
		String entrada;
		do {
			System.out.print("Introduce un dia de la semana (lunes a domingo): ");
			entrada = scanner.nextLine().trim().toLowerCase();
			if (!esDiaValido(entrada)) {
				System.out.println("Dia invalido. Intenta de nuevo.");
			}
		} while (!esDiaValido(entrada));
		return entrada;
	}



}