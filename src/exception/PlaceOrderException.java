package exception;

/**
 * 下单异常
 *
 * @author Shane Tang
 * @create 2020-01-05 15:13
 */
public class PlaceOrderException extends Exception{

    public PlaceOrderException(){

    }

    public PlaceOrderException(String msg){
        super(msg);
    }
}
