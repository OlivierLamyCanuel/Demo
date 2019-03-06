package test;

import model.*;
import metrics.CalcMetrics;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

public class CalcMetricsTest {

    CalcMetrics metrics = new CalcMetrics();

    @org.junit.Test
    public void testCalcANA() {

        ArrayList<UMLMethode> arrayMethods = new ArrayList<UMLMethode>();
        UMLMethode oneArg = new UMLMethode("test(arg : int) : void", "");
        UMLMethode twoArg = new UMLMethode("test(arg : int, arg1 : Integer) : void", "");
        UMLClass testClass = new UMLClass("", null,arrayMethods,null);

        //Cas aucune methode
        assertEquals(0.0, metrics.calcANA(testClass),0.0);

        //Cas une méthode, un arg
        arrayMethods.add(oneArg);
        assertEquals(1.0, metrics.calcANA(testClass),0.0);

        //Cas une methode, plusieurs args
        arrayMethods.add(twoArg);
        assertEquals(1.5, metrics.calcANA(testClass),0.0);

        //Cas plusieurs methodes (6 args, 4 methodes => ANA == 1.5)
        arrayMethods.add(twoArg);
        arrayMethods.add(oneArg);
        assertEquals(1.5, metrics.calcANA(testClass),0.0);

    }

    @org.junit.Test
    public void testCalcNOM() {

        ArrayList<UMLMethode> arrayMethodsParent = new ArrayList<UMLMethode>();
        ArrayList<UMLMethode> arrayMethodsChild = new ArrayList<UMLMethode>();
        UMLMethode method1 = new UMLMethode("method1(arg : int) : void", "");
        UMLMethode method2 = new UMLMethode("method2(arg : int, arg1 : Integer) : void", "");
        UMLMethode method3 = new UMLMethode("method3(arg : int, arg1 : Integer) : void", "");
        UMLMethode method4 = new UMLMethode("method4(arg : int) : void", "");
        UMLClass child = new UMLClass("", null,arrayMethodsChild,null);
        UMLClass parent = new UMLClass("", null,arrayMethodsParent,null);
        child.setParent(parent);

        //Cas aucune methode
        assertEquals(0, metrics.calcNOM(parent));

        //Cas une méthode
        arrayMethodsParent.add(method1);
        assertEquals(1, metrics.calcNOM(parent));

        //Cas plusieurs methodes
        arrayMethodsParent.add(method2);
        assertEquals(2, metrics.calcNOM(parent));

        //Cas method hétité, aucune locale
        assertEquals(2, metrics.calcNOM(child));

        //Cas une method locale, deux hérité
        arrayMethodsChild.add(method3);
        assertEquals(3, metrics.calcNOM(child));

        //Cas une methode locale, une redéfinie, deux hétité
        arrayMethodsChild.add(method4);
        assertEquals(4, metrics.calcNOM(child));

    }

    @org.junit.Test
    public void testCalcNOA() {

        ArrayList<UMLAttributes> arrayAttributes = new ArrayList<UMLAttributes>();
        UMLClass child = new UMLClass("", arrayAttributes,null,null);
        UMLClass parent = new UMLClass("", arrayAttributes,null,null);
        parent.addSubClass(child);
        child.setParent(parent);
        UMLAttributes attribute = new UMLAttributes("test test", "");

        //Cas aucun argument locaux/hérité
        assertEquals(0, metrics.calcNOA(parent));

        //Cas un argument local
        arrayAttributes.add(attribute);
        assertEquals(1, metrics.calcNOA(parent));

        //Cas un argument local, un argument hérité
        assertEquals(2, metrics.calcNOA(child));
    }

    @org.junit.Test
    public void testCalcITC() {

        ArrayList<UMLMethode> arrayMethod = new ArrayList<UMLMethode>();
        ArrayList<UMLMethode> arrayOtherClassMethod = new ArrayList<UMLMethode>();
        ArrayList<UMLClass> arrayClass = new ArrayList<UMLClass>();
        UMLClass testClass = new UMLClass("testClass", null, arrayMethod,null);
        arrayClass.add(testClass);
        UMLClass otherClass = new UMLClass("otherClass", null, arrayOtherClassMethod,null);

        //Cas aucune autre classe
        assertEquals(0, metrics.calcITC(testClass,arrayClass));

        //Cas autre classe, pas de methode
        arrayClass.add(otherClass);
        assertEquals(0, metrics.calcITC(testClass,arrayClass));

        //Cas autre classe, pas de match
        UMLMethode methodNoMatch = new UMLMethode("method1(arg : int) : void", "");
        arrayOtherClassMethod.add(methodNoMatch);
        assertEquals(0, metrics.calcITC(testClass,arrayClass));

        //Cas autre classe, une method avec testClass
        UMLMethode methodMatch = new UMLMethode("method1(arg : otherClass) : void", "");
        arrayMethod.add(methodMatch);
        assertEquals(1, metrics.calcITC(testClass,arrayClass));


    }

    @org.junit.Test
    public void testCalcETC() {

        ArrayList<UMLMethode> arrayMethod = new ArrayList<UMLMethode>();
        ArrayList<UMLMethode> arrayOtherClassMethod = new ArrayList<UMLMethode>();
        ArrayList<UMLClass> arrayClass = new ArrayList<UMLClass>();
        UMLClass testClass = new UMLClass("testClass", null, arrayMethod,null);
        arrayClass.add(testClass);
        UMLClass otherClass = new UMLClass("otherClass", null, arrayOtherClassMethod,null);

        //Cas aucune autre classe
        assertEquals(0, metrics.calcETC(testClass,arrayClass));

        //Cas autre classe, pas de methode
        arrayClass.add(otherClass);
        assertEquals(0, metrics.calcETC(testClass,arrayClass));

        //Cas autre classe, une methode (pas de testClass)
        UMLMethode methodNotTestClass = new UMLMethode("method1(arg : int) : void", "");
        arrayOtherClassMethod.add(methodNotTestClass);
        assertEquals(0, metrics.calcETC(testClass,arrayClass));

        //Cas autre classe, une method avec testClass
        UMLMethode methodTestClass = new UMLMethode("method1(arg : testClass) : void", "");
        arrayOtherClassMethod.add(methodTestClass);
        assertEquals(1, metrics.calcETC(testClass,arrayClass));

    }

    @org.junit.Test
    public void testCalcCAC() {

        UMLAggregate aggregate = new UMLAggregate("", "");
        UMLRelation relation = new UMLRelation("", "");
        UMLClass child = new UMLClass("", null,null,null);
        UMLClass parent = new UMLClass("", null,null,null);
        child.setParent(parent);
        ArrayList<UMLMethode> association = new ArrayList<UMLMethode>();

        //Cas aucune association ou relation locale/hérité
        assertEquals(0, metrics.calcCAC(parent));

        //Cas une association
        parent.addAggregates(aggregate);
        assertEquals(1, metrics.calcCAC(parent));

        //Cas association hérité
        child.addAggregates(aggregate);
        assertEquals(2, metrics.calcCAC(child));

        //Cas une relation locale
        parent.addRelation(relation);
        assertEquals(2, metrics.calcCAC(parent));

        //Cas une relation hérité
        child.addRelation(relation);
        assertEquals(4, metrics.calcCAC(child));

    }

    @org.junit.Test
    public void testCalcDIT() {

        UMLClass leaf0 = new UMLClass("leaf0", null, null,null);
        UMLClass leaf1 = new UMLClass("leaf1", null, null,null);
        UMLClass oneChild = new UMLClass("oneChild", null,null,null);
        UMLClass twoChild = new UMLClass("twoChild",null,null,null);

        oneChild.addSubClass(leaf0);
        twoChild.addSubClass(oneChild);
        twoChild.addSubClass(leaf1);

        leaf0.setParent(oneChild);
        leaf1.setParent(twoChild);
        oneChild.setParent(twoChild);

        //Cas plusieurs niveaux
        assertEquals(2, metrics.calcDIT(leaf0));

        // Cas un niveau
        assertEquals(1, metrics.calcDIT(leaf1));

        // Cas de racine
        assertEquals(0, metrics.calcDIT(twoChild));

    }

    @org.junit.Test
    public void testCalcCLD() {

        UMLClass leaf0 = new UMLClass("leaf0", null, null,null);
        UMLClass leaf1 = new UMLClass("leaf1", null, null,null);
        UMLClass oneChild = new UMLClass("oneChild", null,null,null);
        UMLClass twoChild = new UMLClass("twoChild",null,null,null);

        leaf0.setParent(oneChild);
        leaf1.setParent(twoChild);
        oneChild.setParent(twoChild);

        oneChild.addSubClass(leaf0);
        twoChild.addSubClass(oneChild);
        twoChild.addSubClass(leaf1);

        //Cas de feuille
        assertEquals(0, metrics.calcCLD(leaf0));

        // Cas une feuille
        assertEquals(1, metrics.calcCLD(oneChild));

        // Cas plusieurs branches non-égal
        assertEquals(2, metrics.calcCLD(twoChild));

    }

    @org.junit.Test
    public void testCalcNOC() {

        UMLClass noChild = new UMLClass(null, null, null,null);
        UMLClass oneChild = new UMLClass(null, null,null,null);
        UMLClass twoChild = new UMLClass(null,null,null,null);

        oneChild.addSubClass(noChild);
        twoChild.addSubClass(noChild);
        twoChild.addSubClass(oneChild);

        // Cas aucune sous-classe
        assertEquals(metrics.calcNOC(noChild), 0);

        // Cas une sous-classe feuille
        assertEquals(metrics.calcNOC(oneChild),1);

        //Cas une sous-classe branche
        assertEquals(metrics.calcNOC(twoChild), 2);

        //Cas plusieurs sous-classe avec branche
        twoChild.addSubClass(noChild);
        assertEquals(metrics.calcNOC(twoChild), 3);



    }

    @org.junit.Test
    public void testCalcNOD() {

        UMLClass noChild = new UMLClass("noChild", null, null,null);
        UMLClass oneChild = new UMLClass("oneChild", null,null,null);
        UMLClass twoChild = new UMLClass("twoChild",null,null,null);

        oneChild.addSubClass(noChild);
        twoChild.addSubClass(oneChild);

        // Cas aucune sous-classe
        assertEquals(0, metrics.calcNOD(noChild));

        // Cas une sous-classe
        assertEquals(1, metrics.calcNOD(oneChild));

        // Cas plusieurs sous-classes
        assertEquals(2, metrics.calcNOD(twoChild));


    }
}