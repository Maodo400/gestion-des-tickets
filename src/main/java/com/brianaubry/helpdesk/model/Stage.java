package com.brianaubry.helpdesk.model;

public enum Stage {

    RECU ("Recu"),
    EN_COURS ("En Cours"),
    NE_PAS_TRAITER ("Ne pas traiter"),
    EN_ATTENTE ("en attente"),
    TERMINE ("termine"),
	CLOTURE ("cloture");

    private final String name;

    Stage(String name) {this.name = name;}

    public String getName() {return name;}

}
