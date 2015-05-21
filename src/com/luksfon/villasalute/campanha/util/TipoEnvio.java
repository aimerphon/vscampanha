package com.luksfon.villasalute.campanha.util;

public enum TipoEnvio {
	AUTOMATICO(0, "Autom�tico"),
	MANUAL(1, "Manual");
	
	private String stringValue;
    private int intValue;
    
    private TipoEnvio(int value, String toString) {
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
