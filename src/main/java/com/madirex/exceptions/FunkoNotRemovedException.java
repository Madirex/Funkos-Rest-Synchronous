package com.madirex.exceptions;

public class FunkoNotRemovedException extends FunkoException{
    public FunkoNotRemovedException(String message) {
        super("Funko no dliminado: " + message);
    }
}
