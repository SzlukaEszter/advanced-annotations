import com.sun.net.httpserver.HttpExchange;

import java.net.http.HttpRequest;
import java.util.Map;

public class Routes {

    @WebRoute(path = "/test1")
    public String test1(){
        return "This is a test1 response.";
    }

    @WebRoute(path = "/test2")
    public String test2(){
        return "This is a test2 response.";
    }

    @WebRoute
    public String test3() {return " <html>\n"
            + "<body>\n"
            + "\n"
            + "<form action=\"http://localhost:8000/testpost\" method=\"post\">\n"
            + "input: <input type=\"text\" name=\"username\"><br>\n"
            + "<input type=\"submit\">\n"
            + "</form>\n"
            + "\n"
            + "</body>\n"
            + "</html> ";}

    @WebRoute(path = "/testpost", method = WebRoute.RequestMethod.POST)
    public String testPost(Map<String, String> param){
        //todo create array field for params in Webroute instead
        return "You posted username: " + param.get("username");
    }

    @WebRoute(path = "/testget<username>")
    //todo parameter annotations?
    public String testGetWithParam(Map<String, String> param){
        return "You requested data about user: " + param.get("username");
    }

}
