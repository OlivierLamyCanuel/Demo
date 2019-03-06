package model;


/**
 * 
 * La classe UMLRelation permet d'instancier une association entre 2 classes
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */

public class UMLRelation extends UMLAssoAggrega{
	
	/**
	 * Constructeur classe UMLRelation.
	 * @param relation
	 * @param details
	 */
	public UMLRelation(String relation,  String details) {
		this.name ="(R) " + relation;
		this.details = details.trim();
	}	
	
	@Override
   public String toString()
   {
	   return this.name;
   }
	
}
