import static org.junit.jupiter.api.Assertions.*;

class PQTest {

    public static void test (){

        int created = 0;
        PQ eventQueue = new PQ(10000);

        for(int i = 1; i<1025; i++){
            eventQueue.insert(new EventTest());
            created++;
            checkOrder(eventQueue);
        }

        assertTrue(eventQueue.emptyIndex-1 == created);

        eventQueue.deleteMin();

        checkOrder(eventQueue);
    }

    public static void checkOrder(PQ eventQueue){
        for (int i = 1; i < eventQueue.array.length; i++) {
            Event eventI = (Event) eventQueue.array[i];
            try {
                if (eventQueue.array[2*i] != null) {
                    Event event2I = (Event) eventQueue.array[2*i];
                    assertTrue((eventI.compareTo(event2I) < 0));
                }
            }catch (ArrayIndexOutOfBoundsException ex){}

            try{
                if(eventQueue.array[2*i+1] != null){
                    Event event2i1 = (Event) eventQueue.array[2*i+1];

                    assertTrue(eventI.compareTo(event2i1) < 0);
                }
            }catch (ArrayIndexOutOfBoundsException ex ){}

        }
    }
}