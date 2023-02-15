package com.brianaubry.helpdesk.model;

public enum ListPriorite {
  FAIBLE ("faible"),
  MOYENNE ("Moyenne"),
  HAUTE ("Haute");
  
  private final String name;


  private ListPriorite(String name) {
	this.name = name;
}

public String getName() {return name;}
}
