package com.blamejared.controlling.client.gui;

public enum SearchType {
    NAME, KEY, CATEGORY;
    
    public SearchType cycle() {
        return SearchType.values()[(this.ordinal() + 1) % SearchType.values().length];
    }
    
    public String niceName() {
        String s = this.name().toLowerCase().substring(1);
        return this.name().toUpperCase().charAt(0) + s;
    }
    
}
