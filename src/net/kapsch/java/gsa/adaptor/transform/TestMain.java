package net.kapsch.java.gsa.adaptor.transform;

import com.google.enterprise.adaptor.DocumentTransform;
import com.google.enterprise.adaptor.Metadata;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stagl on 08.08.2014.
 */
public class TestMain {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger(TestMain.class.getName());

        logger.log(Level.INFO,"Call factory to create DocumentTransformer");

        Object o1 = MetaTransformer01.create("-");
        DocumentTransform documentTransform = (DocumentTransform) o1;

        //String testString1 = "ABC_DEF_GHI";

        Metadata metadata = new Metadata();
        metadata.add("kapsch:test","just any metadata");

        Map<String, String> params = new Hashtable<String, String>();
        params.put("DocId", "http://uat-kibsi-global.kapsch.co.at/kbc/GSATest/Manuals with Document Verson History/Forms/30-INT-20140515-1230.docx");
        //params.put("DocId", "30-INT-20140515-1230.docx");

        logger.info("call transformer");

        documentTransform.transform(metadata, params);

        logger.info("done transformation");

        Iterator it = metadata.iterator();
        while (it.hasNext()){
            Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
            System.out.println("metadata: "+entry.getKey()+": "+entry.getValue());
        }

    }

}
