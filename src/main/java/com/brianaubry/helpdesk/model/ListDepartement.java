package com.brianaubry.helpdesk.model;

public enum ListDepartement {
  SUPPORT_TECHNIQUE("Support technique"),
  SUPPORT_COMMERCIAL("Support commercial"),
  SUPPORT_INCIDENT("Support incident");
  
  private final String name;

  private ListDepartement(String name) {this.name = name;}

  public String getName() {return name;}
}
