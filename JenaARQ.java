package uk.ac.le.ac.demo;

import java.util.Iterator;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.PrintUtil;

public class JenaARQ {
	
    static String owlFile="C:/Users/vinay/eclipse-workspace/SW_API/event_management_system.owl";
	static String defaultPrefix="ems";
	
	public static void main(String args[]){	
		
	   	//Question 1.3
		
		String sparqls[]=new String[5];  //five competency questions
		String rules[]=new String[5];   //reasoning rules (if required)
		
		sparqls[0]= "PREFIX ems: <http://www.owl-ontologies.com/Ontology1581945722.owl#>\n" + 
				"SELECT ?user ?updated_event_name ?updated_event_date\n" + 
				"	WHERE { ?a a ems:user ;\n" + 
				"ems:has_user_name ?user ;\n" + 
				"ems:has_updated_event_name ?updated_event_name ;\n" + 
				"ems:has_updated_event_date_and_time?updated_event_date.\n" + 
				" }\n" + 
				"";
		sparqls[1]= "PREFIX ems: <http://www.owl-ontologies.com/Ontology1581945722.owl#>\n" + 
				"SELECT ?payment_id ?payed_for ?amount\n" + 
				"	WHERE { ?a a ems:payment ;\n" + 
				"ems:has_payment_id ?payment_id ;\n" + 
				"ems:has_payed_for ?payed_for ; \n" + 
				"ems:has_amount ?amount.\n" + 
				" }\n" + 
				"";
		sparqls[2]="PREFIX ems: <http://www.owl-ontologies.com/Ontology1581945722.owl#>\n" + 
				"SELECT ?user_id ?payer_name ?paid_amount \n" + 
				"	WHERE { ?a a ems:user;\n" + 
				"ems:user_id ?user_id ;\n" + 
				"ems:has_user_name ?payer_name ; \n" + 
				"ems:has_paid ?paid_amount.\n" + 
				"\n" + 
				" }";
		sparqls[3]="PREFIX ems: <http://www.owl-ontologies.com/Ontology1581945722.owl#>\r\n" + 
				"SELECT ?user_id ?event_id ?paid_amount \r\n" + 
				"	WHERE { ?a a ems:user;\r\n" + 
				"ems:user_id ?user_id ;\r\n" + 
				"ems:user_id ?event_id ; \r\n" + 
				"ems:has_paid ?paid_amount.\r\n" + 
				"\r\n" + 
				" }\r\n" + 
				"";
		sparqls[4]= "PREFIX ems: <http://www.owl-ontologies.com/Ontology1581945722.owl#>\n" + 
				"SELECT ?food_item ?number_of_plates \n" + 
				"	WHERE { ?a a ems:vegitarian_food_item;\n" + 
				"ems:food_item_menu ?food_item ;\n" + 
				"ems:has_number_of_plates ?number_of_plates .\n" + 
				"\n" + 
				" }";
		
		JenaARQ sparqlEngine=new JenaARQ();

		for(int queryCount=0;queryCount<5;queryCount++){
			ResultSet rs=sparqlEngine.executeSPARQLwithRules(owlFile, sparqls[queryCount], rules[queryCount]);	
		    ResultSetFormatter.out(System.out, rs);
		}
		
	} 
	
    public ResultSet executeSPARQLwithRules(String owlFile, String sparql, String rules){
		 	
   	 Model model= RDFDataMgr.loadModel(owlFile);
		Map<String,String> map=model.getNsPrefixMap();	
		ResultSet results=null;
		int nsSize=map.size();
		Iterator<Map.Entry<String,String>> i=map.entrySet().iterator();
		for(int count=0;count<nsSize;count++){
			 Map.Entry<String,String> entry = (Map.Entry<String,String>)i.next();
			 String name=(String)entry.getKey();				 
			 String uri=(String)entry.getValue();
			if(name!=null && !name.equals("")){
			    PrintUtil.registerPrefix(name, uri);
			}else{
				if(uri!=null){
				PrintUtil.registerPrefix(defaultPrefix, uri);
				}
			}
		}

		if(rules!=null && !rules.equals("")){
		 Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
		 InfModel infmodel = ModelFactory.createInfModel(reasoner, model);
			QueryExecution qe = QueryExecutionFactory.create(sparql, Syntax.syntaxARQ, infmodel);
			results = qe.execSelect();
		}else{
			QueryExecution qe = QueryExecutionFactory.create(sparql, Syntax.syntaxARQ, model);		
			results = qe.execSelect();
		}	
		
		return results;		

}

}