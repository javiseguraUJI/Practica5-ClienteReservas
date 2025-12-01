package cliente;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AuxiliarClienteWS {


    final private String baseURI = "http://localhost:8080/ReservasWS/servicios/reservas/";
    Client cliente = null;
    final private JSONParser jsonParser = new JSONParser();
    
    public AuxiliarClienteWS() {
        this.cliente = ClientBuilder.newClient();
    }
    
    public void guardaDatos() {
        Response response = null;
        try {
        	response = cliente.target(baseURI)
        			.path("guardar")
        			.request()
        			.post(Entity.text(""));
        	
        	int estado = response.getStatus();
        	System.out.print(estado);
        	if (estado == 200) { // OK
        		System.out.print("Guardado con éxito");
        	} else if (estado == 404) { // NOT_FOUND
        		System.out.print("Ruta no encontrada");
        	} else {
        		throw new WebApplicationException("Error al obtener la lista de reservas. Estado: " + estado);
        	}
        }
        finally {
        	if (response  != null) response.close();
        }
    }

    public JSONArray listaReservasUsuario(String codUsuario) {
        Response response = null;
        try {
        	response = cliente.target(baseURI)
        			.path("usuario")
        			.path(codUsuario)
        			.request(MediaType.APPLICATION_JSON)
        			.get();
        	
        	int estado = response.getStatus();
        	System.out.print(estado);
        	if (estado == 200) { // OK
        		String jsonText = response.readEntity(String.class);
        		return (JSONArray) jsonParser.parse(jsonText);
        	} else if (estado == 404) { // NOT_FOUND
        		return new JSONArray();
        	} else {
        		throw new WebApplicationException("Error al obtener la lista de reservas. Estado: " + estado);
        	}
        } catch (ParseException ex) {throw new WebApplicationException("Error al parsear.");}
        finally {
        	if (response  != null) response.close();
        }
    }
    
    public JSONArray listaPlazasDisponibles(String actividad) {
        Response response = null;
        try {
        	response = cliente.target(baseURI)
        			.path("disponibles")
        			.queryParam("actividad", actividad)
        			.request(MediaType.APPLICATION_JSON)
        			.get();
        	
        	int estado = response.getStatus();
        	
        	if (estado == 200) { // OK
        		String jsonText = response.readEntity(String.class);
        		return (JSONArray) jsonParser.parse(jsonText);
        	} else if (estado == 404) { // NOT_FOUND
        		return new JSONArray();
        	} else {
        		throw new WebApplicationException("Error al obtener la lista de plazas disponibles. Estado: " + estado);
        	}
        } catch (ParseException ex) {throw new WebApplicationException("Error al parsear.");}
        finally {
        	if (response  != null) response.close();
        }
        
    }

    public JSONObject hazReserva(String codUsuario, String actividad, String dia, long hora) {
        Response response = null;
        try {
        	response = cliente.target(baseURI)
        			.path(codUsuario)
        			.path(actividad)
        			.path(dia)
        			.path(String.valueOf(hora))
        			.request(MediaType.APPLICATION_JSON)
        			.post(Entity.text(""));
        	
        	int estado = response.getStatus();
        	
        	if (estado == 200) { // OK
        		String jsonText = response.readEntity(String.class);
        		return (JSONObject) jsonParser.parse(jsonText);
        	} else if (estado == 404) { // NOT_FOUND
        		return new JSONObject();
        	} else {
        		throw new WebApplicationException("Error al realizar la reserva. Estado: " + estado);
        	}
        } catch (ParseException ex) {throw new WebApplicationException("Error al parsear.");}
        finally {
        	if (response  != null) response.close();
        }
    }

    public JSONObject modificaReserva(String codUsuario, long codReserva, String dia, long hora) {
        Response response = null;
        try {
        	response = cliente.target(baseURI)
        			.path(codUsuario)
        			.path(String.valueOf(codReserva))
        			.path(dia)
        			.path(String.valueOf(hora))
        			.request(MediaType.APPLICATION_JSON)
        			.put(Entity.text(""));
        	
        	int estado = response.getStatus();
        	
        	if (estado == 200) { // OK
        		String jsonText = response.readEntity(String.class);
        		return (JSONObject) jsonParser.parse(jsonText);
        	} else if (estado == 404) { // NOT_FOUND
        		return new JSONObject();
        	} else {
        		throw new WebApplicationException("Error al modificar. Estado: " + estado);
        	}
        } catch (ParseException ex) {throw new WebApplicationException("Error al parsear.");}
        finally {
        	if (response  != null) response.close();
        }
    }

    public JSONObject cancelaReserva(String codUsuario, long codReserva) {
        Response response = null;
        try {
            response = cliente.target(baseURI)
                    .path(codUsuario)
                    .path(String.valueOf(codReserva))
                    .request()
                    .delete();
            
            int estado = response.getStatus();
            
            if (estado == 200) { // OK 
            	String jsonText = response.readEntity(String.class);
                return (JSONObject) jsonParser.parse(jsonText);
            } else if (estado == 204) { // No Content 
                return new JSONObject();   
        	} else {
        		throw new WebApplicationException("Error al cancelar la reserva. Estado: " + estado);
        	}
        } catch (ParseException ex) {throw new WebApplicationException("Error al parsear.");}
        finally {
            if (response != null) response.close();
        }
    }
}

 
