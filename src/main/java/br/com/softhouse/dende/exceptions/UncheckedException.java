package br.com.softhouse.dende.exceptions;
public class UncheckedException extends RuntimeException {
    public UncheckedException(String message) { super(message); }
    public UncheckedException(String message, Throwable cause) { super(message, cause); }
}
