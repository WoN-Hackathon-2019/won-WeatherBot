//HOWTO https://alvinalexander.com/java/java-custom-exception-create-throw-exception
package won.bot.skeleton.WAC;

public class APIException extends Exception{
    APIException(String message){
        super(message);
    }
    APIException(Throwable cause){
        super(cause);
    }
}
