import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

    public class Test {

        private static Map<String, Method> getRouter = new HashMap<>();
        private static Map<String, Method> postRouter = new HashMap<>();

        public static void main(String[] args) throws Exception {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

            for(Method m: Routes.class.getMethods()) {
                if(m.isAnnotationPresent(WebRoute.class)) {
                    Annotation annotation = m.getAnnotation(WebRoute.class);
                    String path = ((WebRoute)annotation).path();
                    String method = ((WebRoute)annotation).method().name();
                    if (!getRouter.containsKey(path) && !postRouter.containsKey(path)) {
                        server.createContext(path, new MyHandler());
                    }
                    if (method == "GET"){
                        getRouter.put(path, m);
                    }
                    else {
                        postRouter.put(path, m);
                    }


                }
            }
            server.setExecutor(null); // creates a default executor
            server.start();


        }

        static class MyHandler implements HttpHandler {

            public void handle(HttpExchange t) throws IOException {
                String response = "Not found.";

                try {
                    Class<?> type = Class.forName("Routes");
                    Object instance = type.getConstructor().newInstance();
                    if ((t.getRequestMethod()).equalsIgnoreCase("post")){
                        Method toCall = postRouter.get(t.getHttpContext().getPath());
                        String queryString = MyParser.getPostQueryString(t);
                        Map<String, String> param = MyParser.ParseQueryToMap(queryString);
                        response = (String) toCall.invoke(instance, param);
                    }
                    else {
                        Method toCall = getRouter.get(t.getHttpContext().getPath());
                        Parameter[] params = toCall.getParameters();
                        //todo: parameter annotations?
                        if (params.length>0){
                            Map<String, String> parameters = MyParser.ParseQueryToMap(MyParser.getGetQueryString(t));
                            response = (String) toCall.invoke(instance, MyParser.ParseQueryToMap(MyParser.getGetQueryString(t)));
                        }
                        else {
                            response = (String) toCall.invoke(instance);
                        }
                    }
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        static class MyParser {

            public static String getGetQueryString(HttpExchange exchange){
                URI uri = exchange.getRequestURI();
                String query = uri.getQuery();
                return query;
            }

              public static String getPostQueryString(HttpExchange exchange) throws IOException {
                  InputStreamReader isr =
                          new InputStreamReader(exchange.getRequestBody(),"utf-8");
                  BufferedReader br = new BufferedReader(isr);
                  String query = br.readLine();
                return query;
            }



            public static Map<String, String> ParseQueryToMap(String query) {
                Map<String, String> result = new HashMap<>();
                if (query != null) {
                for (String param : query.split("&")) {
                    String[] entry = param.split("=");
                    if (entry.length > 1) {
                        result.put(entry[0], entry[1]);
                    }else{
                        result.put(entry[0], "");
                    }
                }
                }
                return result;
            }

        }

        static class MyAnotherHandler implements HttpHandler {

            public void handle(HttpExchange t) throws IOException {
                String response = "This is the other response";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }




        }

    }

