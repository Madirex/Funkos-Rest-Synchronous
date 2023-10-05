package com.madirex.exceptions;

public class FunkoNotSavedException extends FunkoException{
    public FunkoNotSavedException(String message) {
        super("Funko no guardado: " + message);
    }
}
