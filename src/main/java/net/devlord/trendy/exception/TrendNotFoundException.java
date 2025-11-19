package net.devlord.trendy.exception;

public class TrendNotFoundException extends RuntimeException {
    
    public TrendNotFoundException(String message) {
        super(message);
    }
    
    public TrendNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

