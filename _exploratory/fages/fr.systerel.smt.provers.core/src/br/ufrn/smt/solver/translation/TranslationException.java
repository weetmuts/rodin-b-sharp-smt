package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.Map.Entry;

public class TranslationException extends Exception {

	private ArrayList<Pair<String,String>> causes;
	
	public TranslationException(ArrayList<Pair<String,String>> causes)
	{
		super();
		this.causes = causes;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < causes.size() ; i++)
		{	
			sb.append("Formula: " + causes.get(i).getKey() + ",Reason: " + causes.get(i).getValue() + "\n");
		}
		return sb.toString();
	}
	
	
	
}
