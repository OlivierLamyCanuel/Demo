package model;

import java.util.ArrayList;


/**
 * Une UMLClass a un nom, puis est composée 5 listes:
 * d'une liste de UMLAttribute, d'une de UMLMethode, de UMLClass (pour les sous-classes),
 * de UMLAggregate, de UMLRelation.
 * Et également une string details qui est une string correspondant au bloc 
 * du fichier où on retrouve la classe..
 * 
 * @author Jules COHEN
 * @author Olivier LAMY-CANUEL
 */

public class UMLClass extends UMLComponent {

    //Class specs

    private ArrayList<UMLAttributes> attributes;
    private ArrayList<UMLMethode> operations;
    private ArrayList<UMLClass> subClasses = new ArrayList<UMLClass>();
    private ArrayList<UMLAggregate> aggregates = new ArrayList<UMLAggregate>();
    private ArrayList<UMLRelation> relation = new ArrayList<UMLRelation>();
    private String name;
    private String detailsGen;
    private UMLClass parent = null;
    private boolean mark = false;

    /**
     * Constructeur par défaut de la classe UMLClass
     * @param name
     * @param attributes
     * @param methods
     * @param details
     */
    public UMLClass (String name, ArrayList<UMLAttributes> attributes, ArrayList<UMLMethode> methods, String details){
        this.name = name;
        this.attributes = attributes;
        this.operations = methods;
        this.details = details;
    }

    /**
     * Constructer pour parcing
     * @param toParse
     * @param details
     */
    public UMLClass (String toParse, String details ){

        this.details = details;

        String[] tmpParsed = toParse.split("ATTRIBUTES");
        this.name = tmpParsed[0].substring(6, tmpParsed[0].length() - 1);
        tmpParsed = tmpParsed[1].split("OPERATIONS");

        String tmpAttributes = "";
        String tmpOperations = "";


        if (tmpParsed.length == 1) {
            tmpAttributes = tmpParsed[0];

        } else if (tmpParsed.length == 2) {
            tmpAttributes = tmpParsed[0];
            tmpOperations = tmpParsed[1];
        }

        parceAttributes(tmpAttributes);
        parceOperations(tmpOperations);
    }

    /**
     * Transforme la String d'attributs en ArrayList
     *
     * @param tmpAtt
     *            String d'attributs
     * @return liste d'attributs
     *
     */
    public void parceAttributes(String tmpAtt) {


        ArrayList<UMLAttributes> list = new ArrayList<UMLAttributes>();
        String[] tmpList = tmpAtt.split(",");

        for (int i = 0; i < tmpList.length; i++) {

            String[] tmpAttribute = tmpList[i].trim().split(":");

            if (tmpAttribute.length > 1) { // valide que la chaîne n'est pas vide.

                list.add(new UMLAttributes(tmpAttribute[1] + " " + tmpAttribute[0], details));

            }

        }

        this.attributes = list;
    }

    /**
     * Transforme la String d'operations en ArrayList
     *
     * @param tmpOp
     *            String d'operations
     * @return liste d'operations
     *
     */
    public void parceOperations(String tmpOp) {

        ArrayList<String> list = new ArrayList<String>();
        String[] tmpList = tmpOp.split(",");

        for (int i = 0; i < tmpList.length; i++) {
            list.add(tmpList[i].trim());
        }

        // dans le cas où une methode a plusieurs parametres
        for (int i = 0; i < list.size(); i++) {

            // if matches ex: Utilise_par(eq : Equipe
            if (list.get(i).matches("^[_\\w]+\\([_\\w]+ : [_\\w]+")) {
                list.set(i, list.get(i) + ", " + list.get(i + 1));
                list.remove(i + 1);
            }

            // if matches ex: test : test
            if (list.get(i).matches("^[_\\w]+ : [_\\w]+")) {
                list.set(i - 1, list.get(i - 1) + ", " + list.get(i));
                list.remove(i);
            }
            // if matches ex: test2 : test2) : Boolean
            if (list.get(i).matches("^[_\\w]+ : [_\\w]+\\) : [_\\w]+")) {
                list.set(i - 1, list.get(i - 1) + ", " + list.get(i));
                list.remove(i);
            }
        }
        this.operations = getListOperation(list);
    }

    /**
     * Recupère la liste de String créée par parceOperations() et retourne une liste d'objet UMLMethode
     * 
     * @param operations
     * @return list une ArrayList contenant les méthode de la classe
     */
    public ArrayList<UMLMethode> getListOperation(ArrayList<String> operations){

        ArrayList<UMLMethode> list = new ArrayList<UMLMethode>();

        for(int i= 0 ; i < operations.size(); i++) {
            list.add(new UMLMethode(operations.get(i), details));
        }

        return list;

    }
    
    // Getter & Setters

    public void setDetailsGen(String detailsGen) {
    	this.detailsGen = detailsGen;
    }
    
    /**
    * Retourne la String details pour les generalizations
    * @return detailsGen
    */
    public String getDetailsGen() {
    	return this.detailsGen;
    }
    
    public void setSubClass(ArrayList<UMLClass> subClass) {
        this.subClasses = subClass;
    }

    public ArrayList<UMLAttributes> getAttribute() {
        return this.attributes;
    }

    public ArrayList<UMLMethode> getMethods() {
        return this.operations;
    }

    public ArrayList<UMLClass> getSubClass() {
        return this.subClasses;
    }

    public void addSubClass(UMLClass newSubClass) {
        this.subClasses.add(newSubClass);
    }
    
    public ArrayList<UMLAggregate> getAggregates() {
        return this.aggregates;
    }

    /**
     * Permet d'ajouter un aggrégat à la classe
     * @param aggregate
     */
    public void addAggregates(UMLAggregate aggregate) {
        this.aggregates.add(aggregate);
    }

    public String getDetails() {
        return details;
    }
    /**
     * Permet d'ajouter une relation à la classe
     * @param relation
     */
   public void addRelation(UMLRelation relation) {
	   this.relation.add(relation);
   }
   
   public ArrayList<UMLRelation> getRelations(){
	   return this.relation;
   }

   public String getName(){
	   return this.name;
   }
   
   @Override
   public String toString()
   {
	   return this.name;
   }


    public UMLClass getParent() {
        return parent;
    }

    public void setParent(UMLClass parent) {
        this.parent = parent;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }
}
