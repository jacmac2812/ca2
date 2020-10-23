
package exceptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Acer
 */
@Provider
public class CityNotFoundExceptionMapper implements ExceptionMapper<CityNotFoundException> 
{
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();   
    @Override
    public Response toResponse(CityNotFoundException ex) {
       Logger.getLogger(CityNotFoundExceptionMapper.class.getName())
           .log(Level.SEVERE, null, ex);
       ExceptionDTO err = new ExceptionDTO(404,ex.getMessage());
       return Response
               .status(404)
               .entity(gson.toJson(err))
               .type(MediaType.APPLICATION_JSON)
               .build();
	}
}

