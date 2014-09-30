
package net.kapsch.java.gsa.adaptor.transform;

import com.google.enterprise.adaptor.DocumentTransform;
import com.google.enterprise.adaptor.Metadata;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map.Entry;

/**
 * This Class is needed for creation of the metainformation of googles indexing
 * The elements are retrieved by a pipeline.
 * This Code is called by the Plexi adaptor from Google.
 * (Further info at https://code.google.com/p/plexi )
 *
 * The special task of this transformer to create a pseudo-wildcard-search
 * which enables substrings to be found in the GSA search.
 *
 * At the moment Google Enterprise Search cannot do wildcard searches. (searching for part of a filename).
 * As a workaround for a full wildcard search we can offer a search feature that can search for parts of a filename that has pre-defined separation characters (e.g.: underscore ‘_’ ).
 * Let’s assume a document with the name “BYT_BSC_APP_PROC” exists. Underscores are defined separation characters.
 * All words between separation characters can be used for as a search term but not parts of the words.
 * Also the full filename or parts of the full filename, as long as all characters between separation characters are used can be used for a search. This is true as long as the search term starts at the beginning or the end of the full filename but not somewhere in the middle
 * Best explained with an example.
 * The following search terms would find the document in this example :
 * •	From the beginning of the filename going forward in blocks
 * o	BYT
 * o	BYT_BSC
 * o	BYT_BSC_APP
 * •	From the end of the filename going backwards
 * o	PROC
 * o	APP_PROC
 * o	BSC_APP_PROC
 * •	The full filename
 * o	BYT_BSC_APP_PROC
 * •	Terms between separation characters
 * o	BSC
 * o	BYT
 * o	APP
 * o	PROC
 * •	A combination of above
 * o	e.g.: BYT_BSC PROC  a filename that starts with BYT_BSC and has the term PROC in the filename or in the content of a document.
 * o	The search only for filenames of documents (and ignoring the content) the following syntax can be used :  “BYT_BSC intitle:PROC”
 * o	or alternative “BYT_BSC inurl:PROC” – which would also include the whole URL in the search (that includes names of site collection and lists etc…)
 * •	Search terms in the middle of filename cannot be found :
 * o	BSC_APP only
 * o	Or any combination like : SC_APP or BSC_ or _APP or BYT_B, …
 *
 * @author Johannes Fleck, Wolfgang Stagl
 */
public class MetaTransformer01 implements DocumentTransform 
{
	private static Logger logger = Logger.getLogger(MetaTransformer01.class.getName());
	private StringSplitter pseudoStringSplitter;

    private static String PARAMNAME = "DocId";
    private static String METANAME = "kapsch:tokenizedDocId";
	
	private MetaTransformer01(String sepChars)
	{
		this.pseudoStringSplitter = new StringSplitter(this.getSeparationChars(sepChars));
	}
	
	//factory method

    /**
     * Factory Method for creating a Transfomer
     *
     * @param cfg Adaptor Configuration read from adaptor-config.properties
     * @return Object
     */
	public static MetaTransformer01 create(Map<String,String> cfg) 
	{
		logger.log(Level.INFO, "just in ");
		
		MetaTransformer01 metaTransformer01 = new MetaTransformer01(cfg.get("separationCharacters"));
		logger.log(Level.INFO, "instance of MetaTransformer01 class created");
		
		return metaTransformer01;
	}

    protected static MetaTransformer01 create(String sepChars)
    {
        logger.log(Level.INFO, "create for testing without reading configfile");

        MetaTransformer01 metaTransformer01 = new MetaTransformer01(sepChars);
        logger.log(Level.INFO, "instance of MetaTransformer01 class created");

        return metaTransformer01;
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
		
		try {
            String retval = new String();
            String docId = params.get(MetaTransformer01.PARAMNAME);

            docId = this.cleanSmbUrl(docId);

			ArrayList<String> arrayList = this.pseudoStringSplitter.splitString(docId );

            Iterator it = arrayList.iterator();
            while(it.hasNext()){
                retval += it.next()+" ";
            }

			metadata.add(this.METANAME,retval);
		} 
		catch (Exception e) 
		{
			logger.log(Level.INFO,e.getMessage());
		}
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
    protected String cleanSmbUrl(String smbUrl)
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

    /**
     * This is an inner class since GSA does officially only support single classfiles.
      */
	protected class StringSplitter
	{
		private final List<String> separatorChars;
		
	    protected  StringSplitter(List<String> separatorChars) {
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
	    
	    public ArrayList<String> splitString(String term)
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

	}

}
