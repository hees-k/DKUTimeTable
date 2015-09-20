package dk.too.timetable;

public class LoginError extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LoginError(String msg) {
        super(msg);
    }
    

    public LoginError(String msg, Throwable t) {
        super(msg, t);
    }
}
