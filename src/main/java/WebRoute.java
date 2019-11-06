import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebRoute {

    public String path() default "/";
    public RequestMethod method() default RequestMethod.GET;

    public static enum RequestMethod {
        POST, GET
    }
}
