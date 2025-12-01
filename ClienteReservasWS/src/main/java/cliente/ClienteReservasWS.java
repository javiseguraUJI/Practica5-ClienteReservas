package cliente;

import java.util.InputMismatchException;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class ClienteReservasWS {

    // Sustituye esta clase por tu implementación.
    // Deberías copiar y modificar ligeramente la clase cliente que has implementado por ejemplo
    // en la solución con sockets o RMI sin callbacks

	public static int menu(Scanner teclado) {
        int opcion;
        System.out.println("\n\n");
        System.out.println("=====================================================");
        System.out.println("============            MENU        =================");
        System.out.println("=====================================================");
        System.out.println("0. Salir");
        System.out.println("1. Listar las reservas");
        System.out.println("2. Listar plazas disponibles de una actividad");
        System.out.println("3. Hacer una reserva");
        System.out.println("4. Modificar una reserva");
        System.out.println("5. Cancelar una reserva");
        do {
            System.out.print("\nElige una opcion (0..5): ");
            opcion = teclado.nextInt();
        } while ( (opcion<0) || (opcion>5) );
        teclado.nextLine(); // Elimina retorno de carro del buffer de entrada
        return opcion;
    }

    
    /**
     * Programa principal. Muestra el menú repetidamente y atiende las peticiones del usuario.
     *
     * @param args	no se usan argumentos de entrada al programa principal
     */
    public static void main(String[] args)  {

    	try {
            AuxiliarClienteWS gestor = new AuxiliarClienteWS();

            System.out.println("Lookup completed ");
            // invoke the remote method
            
            
            Scanner teclado = new Scanner(System.in);
            
            System.out.print("Introduce tu código de usuario: ");
            String codUsuario = teclado.nextLine();

            
            int opcion;
            do {
                opcion = menu(teclado);
                switch (opcion) {
                case 0 -> { // Guardar los datos en el fichero y salir del programa
                    // Llamamos a guardaDatos() y como opcion es igual a 0 se sale del while y finaliza el programa
                	gestor.guardaDatos();
                	System.out.println("Saliendo del programa...");
                	System.out.println("Adiós :)");
                }

                case 1 -> { // Listar los paquetes enviados por el cliente
                    // Obtenemos un JSONArray con los JSONObjects que representan las reservas del usuario
                	JSONArray jsonArrayReservas = gestor.listaReservasUsuario(codUsuario);

                	// Recorremos el JSONArray y mostramos por pantalla la información pedida
                	int i = 1;
                	for (Object o : jsonArrayReservas) {
                		JSONObject jsonReserva = (JSONObject) o;
                		System.out.println(i + "- Actividad: " + jsonReserva.get("actividad") + " Código: " + jsonReserva.get("codReserva")
                		+ "\nEl día " + jsonReserva.get("dia") + " a las " + jsonReserva.get("hora") + ".");
                		i++;
                	}
                	if (i == 1) System.out.println("No tienes reservas realizadas.");
                }

                case 2 -> { // Listar los plazas disponibles de una actividad
                	// Pedimos el nombre de la actividad de la cuál queremos obtener un array con todas sus sesiones disponibles
                	String nombreActividad = pedirNombreActividad(teclado, "¿De qué actividad quieres ver las plazas?");
                	JSONArray jsonArraySesiones= gestor.listaPlazasDisponibles(nombreActividad);
                	
                	// Recorremos el JSONArray devuelto con las JSONobjects que representan las sesiones mostrando por pantalla la información necesaria
                	int i = 1;
                	for (Object o : jsonArraySesiones) {
                		JSONObject jsonSesion = (JSONObject) o;
                		System.out.println(i + "- Día " + jsonSesion.get("dia") + " a las " + jsonSesion.get("hora")
                		+ ". Plazas: " + jsonSesion.get("plazas"));
                		i++;
                	}
                	if (i == 1) System.out.println("No hay sesiones de la actividad introducida.");
                }

                case 3 -> { // Hacer una reserva
                    // Pedimos la información necesaria para realizar la reserva
                	String nombreActividad = pedirNombreActividad(teclado, "¿Qué actividad quieres reservar?");
                    String dia = DiaSemana.leerDia(teclado);
                    long hora = pedirHora(teclado, "¿A qué hora quieres reservar?\n");
                    

                     // Intentamos realizar la reserva
                    JSONObject jsonReserva = gestor.hazReserva(codUsuario, nombreActividad, dia, hora);
                    
                   	if (!jsonReserva.isEmpty()) {
                   		System.out.println("Reserva realizada con éxito. Código: " + jsonReserva.get("codReserva"));
                  	} else {
                  		// Si no se ha podido realizar la reserva, buscamos la sesión para ver si existe
                  		JSONArray jsonArraySesiones= gestor.listaPlazasDisponibles(nombreActividad);
                        JSONObject jsonSesion = null;
                        
                        for (Object o : jsonArraySesiones) {
                        	jsonSesion = (JSONObject) o;
                        	if (dia == (String) jsonSesion.get("dia") && (long) jsonSesion.get("hora") == hora)
                        		break;
                        }
                        
                        if (jsonSesion == null) // Si no se ha encontrado la sesión
                        	System.out.println("Sesión introducida no encontrada.");
                        
                        else {
                        	System.out.println(jsonSesion.get("plazas"));
                        	if ((long) jsonSesion.get("plazas") > 0) // Si no quedan plazas, la única posibilidad es que haya una reserva de la misma sesión del mismo usuario
                        		System.out.println("Ya tienes una reserva realizada de esta sesión.");
                  		
                        	else // Si quedan plazas
                        		System.out.println("No se ha podido realizar la reserva. No quedan plazas disponibles.");
                        }
                       }
                }

                case 4 -> { // Cambiar de día y hora una reservacli05
                    // Pedimos la información necesaria para modificar la reserva
                	long codReserva = pedirCodReserva(teclado, "Introduce el código de la reserva que quieres modificar: ");
                	String dia = DiaSemana.leerDia(teclado);
                	long hora = pedirHora(teclado, "¿A que hora cambiar tu reserva?\n");
                	
                	
                	JSONArray jsonReservasUsuario = gestor.listaReservasUsuario(codUsuario);
                	
                	boolean encontrada = false;
                	for (Object o : jsonReservasUsuario) {
                		JSONObject jsonReservaUsuario = (JSONObject) o;
                		if ((long) jsonReservaUsuario.get("codReserva") == codReserva) encontrada = true;
                	}
                	
                	if (!encontrada) {
                		System.out.println("Reserva no encontrada.");
                	} else {
                	
                		// Intentamos modificarla
                		JSONObject jsonReserva = gestor.modificaReserva(codUsuario, codReserva, dia, hora);
                	
                		if (jsonReserva.isEmpty()) { // Si no se ha podido modificar, lo indicamos !!!!!!!!!!!!!! Aquí se podría hacer como los 3 casos de hazreserva(), pero ya me parece demasiado
                			System.out.println("No se ha podido modificar.");
                		} else { // Si se ha podido modificar, lo indicamos y mostramos la información de la nueva reserva
                			System.out.println("Reserva modificada con éxito. Nueva reserva:\n");
                			System.out.println("Actividad: " + jsonReserva.get("actividad") + " Código: " + jsonReserva.get("codReserva")
                			+ "\nEl día " + jsonReserva.get("dia") + " a las " + jsonReserva.get("hora") + ".");
                		}
                	}
                }
                
                case 5 -> { // Cancelar una reserva
                    // Pedimos la información necesaria para eliminar la reserva
                	long codReserva = pedirCodReserva(teclado, "Introduce el código de la reserva que quieres eliminar: ");
                	
                	
                	
                	// Intentamos cancelarla
                	JSONObject jsonReserva = gestor.cancelaReserva(codUsuario, codReserva);
                	
                	if (jsonReserva.isEmpty()) { // Si no se ha podido cancelar lo indicamos
                		System.out.println("No se ha podido cancelar, reserva no encontrada.");
                	} else { // Si se ha podido cancelar, lo indicamos
                		System.out.println("Reserva cancelada con exito.");
                	}
                }

            } // fin switch

        } while (opcion != 0);
           
            
            
            
            
            
        } // end try
        catch (Exception e) {
            System.out.println("Exception in HelloClient: " + e);
        }
    	
    	
        
 
    } // fin de main

    private static String pedirNombreActividad(Scanner teclado, String pregunta) { // pide el nombre de la actividad por teclado
    	String actividad;
    	System.out.println(pregunta);
   		actividad = teclado.nextLine();
    	return actividad;
    }
    
    private static long pedirHora(Scanner teclado, String pregunta) { // pide hora por teclado, param pregunta es para reutilizar la función en contextos diferentes
    	long hora = 0;
    	System.out.println(pregunta);
    	do {
    		System.out.println("Dame una hora (0-23): ");
    		boolean valido = false; 
    		while (!valido) {
    			System.out.println(pregunta);
    			try {
    				hora = teclado.nextLong();
    	        	valido = true;
    	        } catch (InputMismatchException e) {
    	        	System.out.println("Entrada no válida. Introduce un número.");
    	        	teclado.nextLine(); // limpiar el buffer
    	        }
    		}
    	} while (hora < 0 || 23 < hora);
    	return hora;
    }
    
    private static long pedirCodReserva(Scanner teclado, String pregunta) { // pide codReserva por teclado, param pregunta es para reutilizar la función en contextos diferentes
    	long codReserva = 0;
        boolean valido = false;
        while (!valido) {
        	System.out.println(pregunta);
        	try {
        		codReserva = teclado.nextLong();
        		valido = true;
        	} catch (InputMismatchException e) {
        		System.out.println("Entrada no válida. Introduce un número.");
        		teclado.nextLine(); // limpiar el buffer
        	}
        }
    	return codReserva;
    }
		
} // fin class
