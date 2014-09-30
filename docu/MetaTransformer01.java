
package com.google.enterprise.adaptor.transforms;

import com.google.enterprise.adaptor.DocumentTransform;
import com.google.enterprise.adaptor.Metadata;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
 

import java.util.Map;

public class MetaTransformer01 implements DocumentTransform 
{
	private static Logger logger = Logger.getLogger(MetaTransformer01.class.getName());
	private StringSplitter pseudoStringSplitter;
	
	//ctor
	private MetaTransformer01(String sepChars)
	{
		this.pseudoStringSplitter = new StringSplitter(this.getSeparationChars(sepChars));
	}
	
	//factory method
	public static MetaTransformer01 create(Map<String,String> cfg) 
	{
		logger.log(Level.INFO, "just in ");
		
		MetaTransformer01 tmp = new MetaTransformer01(cfg.get("separationCharacters"));
		logger.log(Level.INFO, "instance of MetaTransformer01 class created");
		
		return tmp;
	}
	
	private ArrayList<String> getSeparationChars(String sepChar)
	{
		ArrayList<String> separationChars = new ArrayList<String>();
		
		char[] charArray = sepChar.toCharArray();
		for(char c : charArray)
		{
			separationChars.add(Character.toString(c));
		}
		return separationChars;
	}
	
	@Override
	public void transform(Metadata metadata, Map<String, String> params) {
		
		//String docid = "Aadblasda/kapsch_something_some.pdf";
		try {
			this.pseudoStringSplitter.splittString("hallo_there");
			String docid = params.get("DocId");
			logger.log(Level.INFO,"params.get('DocId') = " + docid);
			metadata.add("KapschName","jf-test");
		} 
		catch (Exception e) 
		{
			logger.log(Level.INFO,e.getMessage());
		}
	}
	
		
	public class StringSplitter 
	{
		private final List<String> separatorChars;
		
	    public  StringSplitter(List<String> separatorChars) {
	        this.separatorChars = separatorChars;
	    }
	 
	    // get rid of duplicate separation chars
	    private String prepareTerm(String term, boolean removeStart, boolean removeEnd) {
	        for (String sep : this.separatorChars)
	        {
	            if (sep != null)
	            {
	                //term = term.replaceAll("[" + sep + "]+", sep); // keep a single 

	                if (removeEnd == true){
	                while (term.endsWith(sep))
	                    term=term.substring(0,term.length()-1);
	                }

	                if (removeStart == true) {
	                while (term.startsWith(sep))
	                    term=term.substring(1,term.length());
	                }
	            }
	        }   
	        return term;
	    }
	    
	    private ArrayList<String> splitForward(String term) 
	    {
	        ArrayList<String> combinations;
	        combinations = new ArrayList<String>();
	        
	        //from start
	        String c,token;
	        int idx,skipStop  = 0; //stop at first sep Char
	        Boolean stillSepChar, finished = false;
	        
	        do 
	        {
	            idx = 0; token = "";
	            while (idx < term.length()) 
	            {
	                // add up until first sep char
	                c = term.substring(idx,idx+1);
	                if (this.separatorChars.contains(c)) // is separation char
	                {
	                    if (idx < skipStop) { // can we skip the sep chars?
	                        token += c; //add anyway
	                        idx++;
	                    }
	                    else // this token is finsihed
	                    {
	                        // stop reached - continue until next non sep char
	                        do { 
	                            idx ++;
	                            if (idx <= term.length()) {
	                                c = term.substring(idx,idx+1);
	                                stillSepChar = this.separatorChars.contains(c);
	                            } else {
	                                break;
	                            }
	                        } 
	                        while (stillSepChar);
	                        break;
	                    }
	                }
	                else { // normal char 
	                    token += c;
	                    idx++;
	                }
	            }     
	            skipStop = idx; // remember how far we got
	            if (idx < term.length())
	            {
	                if (token != "")
	                    combinations.add(token);
	            }
	            else
	                break;
	            
	        } while (!finished);
	        return combinations;
	    }        
	    
	    private ArrayList<String> splitBackward(String term) 
	    {
	        //TODO add sep chars at the end of needed for google
	        ArrayList<String> combinations;
	        combinations = new ArrayList<String>();

	        //from start
	        String token,c;
	        int idx, skipStop  = term.length(); //stop at first sep Char
	        Boolean stillSepChar,finished = false;
	        do 
	        {
	            idx = term.length(); token = "";
	            while (idx > 0) 
	            {
	                // add up until first sep char
	                c = term.substring(idx-1,idx);
	                if (this.separatorChars.contains(c)) // is separation char
	                {
	                    if (idx > skipStop) { // can we skip the sep chars?
	                        token += c; //add anyway
	                        idx--;
	                    }
	                    else // this token is finsihed
	                    {
	                        // stop reached - continue until next non sep char
	                        do { 
	                            idx --;
	                            if (idx >= 0) {
	                                c = term.substring(idx-1,idx);
	                                stillSepChar = this.separatorChars.contains(c);
	                            } else {
	                                break;
	                            }
	                        } 
	                        while (stillSepChar);
	                        break;
	                    }
	                }
	                else { // normal char 
	                    token += c;
	                    idx--;
	                }
	            }     
	            skipStop = idx; // remember how far we got
	            if (idx > 0) {
	                if (token != "")
	                    combinations.add(new StringBuilder(token).reverse().toString());
	            }
	            else
	                break;
	            
	        } while (!finished);
	        return combinations;     
	    }    
	    
	    public ArrayList<String> splittString(String term) 
	    {
	        ArrayList<String> combinations;
	        combinations = new ArrayList<String>();
	        
	        if (term != null)
	        {
	            String forwardTerm = this.prepareTerm (term, false,true); 
	            String backwardTerm = this.prepareTerm(term, true,false); 

	            combinations.addAll (this.splitForward(forwardTerm));
	            combinations.addAll (this.splitBackward(backwardTerm));
	        }
	           
	        return combinations;
	    }	

		/*
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/RE  EPS12 GSA ready for testing    PARTLY.msg
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/RE  SharePoint Indexing Problem !!.msg
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/Reading the H2 Database(with attchement).msg
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/aaa_bbb.txt
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/blablabla_fufu.xxx
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/for_ef_sake.docx
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/roli_test.txt
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/smsc20clp21_ca_2013Jun07.1442.logs
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/status update after workshop with alex mario & johannes.msg
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/unix style file 5539
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/upsadaisy.txt
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/warnings.log
		FINEST: super.getPropertyValues(naame=google:docid) ==  : smb://s060b004.kapsch.co.at/kbccom$/Abteilungsdaten/5539/Fleck/GSA-Testfolder/xsl-reihenfolgen.xlsx

		*/    
	    public String cleanSmbUrl(String smbUrl)
	    {
	    	// first '/' from the end backwards
	    	String fname = smbUrl.substring(smbUrl.lastIndexOf("/")+1);
	    	// first . to rmeove file extension
	    	int ext = fname.lastIndexOf(".");
	    	if (ext > 0 & ext <=fname.length())
	    	{
	    		fname = fname.substring(0,fname.lastIndexOf("."));	
	    	}
	    	
	    	return fname;
	    }
	}

}
