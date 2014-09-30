package com.google.enterprise.adaptor.transforms;

import com.google.enterprise.adaptor.DocumentTransform;
import com.google.enterprise.adaptor.Metadata;

import java.util.Map;

public class Underscoring implements DocumentTransform {
  
  // Factory method
  public static Underscoring create(Map<String, String> cfg) {
    return new Underscoring();
  }
  @Override
  public void transform(Metadata metadata, Map<String, String> params) {
    String docid = "Aadblasda/kapsch_something_some.pdf";
    System.out.println(docid);
    
    metadata.add("filename", docid.substring(docid.lastIndexOf("/")).replaceAll("_", " "));
  }
}
