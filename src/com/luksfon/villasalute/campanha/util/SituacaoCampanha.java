package com.luksfon.villasalute.campanha.util;

public enum SituacaoCampanha {
	ENVIADO(1, "Enviado"),
	ENVIANDO(2, "Enviando"),
	NAO_ENVIADO(3, "Não enviado");
	
	private String stringValue;
    private int intValue;
    
    private SituacaoCampanha(int value, String toString) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
    
    public int getValue() {
    	return intValue;
    }
}
