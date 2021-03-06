package com.sensedia.api.interfaces;

import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

public class Dictionary {
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");
	
	private static final Dictionary dictionary = new Dictionary();
	
	private Dictionary() {
		super();
	}
	
	public static Dictionary get(){
		return dictionary;
	}
	
	public String valueOf(String key){
		return Optional.ofNullable(MESSAGES.getString(key)).orElse(key);
	}
	
	public Iterable<String> keys(){
		return 	Collections.list(MESSAGES.getKeys());
	}
}