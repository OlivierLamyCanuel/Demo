import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @Author Amaury Matu 
 * @Author Olivier Lamy-Canuel
 * Implemente Move-To-Front(Encode-w)
 */

public class MTFOmega {

    private static BufferedOutputStream out;
    private static long startTime;
    private static int byteBuffer;
    private static int totalBytesWritten;
    private static int usedBufferSpace;


    /**
     * Methode principale qui execute les tests et execute la conpression
     * @param args : cmdline arguments
     */
    public static void main(String[] args) {
        testOmega();
        testMTF();

        // Output Stream
        out = new BufferedOutputStream(System.out);
        try {
            // Pour permettre de lancer une batch  (eg :  *.txt )
            for (String path: args) {
                totalBytesWritten = 0;
                startTime = System.currentTimeMillis();
                writeBit(path);
                writePerformance(path);
            }
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    /**
     * Methode de test unitaire pour encodage omega
     */
    public static void testOmega(){

        assert Encode.encodeOmega(1).equals(new Boolean[]{true});
        assert Encode.encodeOmega(2).equals(new Boolean[]{true, true, false});
        assert Encode.encodeOmega(3).equals(new Boolean[]{true, true, true});
        assert Encode.encodeOmega(4).equals(new Boolean[]{true, true, false, true, false, false});
        assert Encode.encodeOmega(5).equals(new Boolean[]{true, true, false, true, false, true});
        assert Encode.encodeOmega(6).equals(new Boolean[]{true, true, false, true, true, false});
        assert Encode.encodeOmega(7).equals(new Boolean[]{true, true, false, true, true, true});

    }

    /**
     * Methode de test unitaire pour encodage Move-to-Front
     */    public static void testMTF(){
        Integer[] inputSequence = {1, 2, 5, 2, 1, 4, 3, 0};
        Integer[] indexSequence = {1, 2, 5, 1, 2, 5, 5, 5};

        // Test encode MTF
        MoveToFront mtf = new MoveToFront(6);
        for (int i=0; i < inputSequence.length; i++){
            int result = mtf.encodeMTF(inputSequence[i]);
            assert result == indexSequence[i];
        }

        // Test decode MTF
        mtf = new MoveToFront(6);
        for (int i=0; i < indexSequence.length; i++){
            int result = mtf.decodeMTF(indexSequence[i]);
            assert result == inputSequence[i];
        }
    }


    /**
     * Methode qui a partir d'un chemin de fichier execute la conpression et pipe le resultat sur StdOut
     * @param filepath : chemin du fichier a compresser
     */
    public static void writeBit(String filepath) throws IOException {

        FileReader fr = new FileReader(filepath);

        int i;
        ArrayList<Integer> inputInteger = new ArrayList();
        while ((i=fr.read()) != -1) {
            inputInteger.add(i);
        }

        int max = Collections.max(inputInteger);
        MoveToFront mtf = new MoveToFront(max + 1);

        for (Integer entier: inputInteger) {

            ArrayList<Boolean> mtfOmegaVal =   Encode.encodeOmega(mtf.encodeMTF(entier));

            for (int j = 0; j < mtfOmegaVal.size(); j++) {
                Boolean value = mtfOmegaVal.get(j);

                // add bit to buffer
                byteBuffer <<= 1;

                if (value) byteBuffer |= 1;
                // if buffer is full (8 bits), write out as a single byte
                usedBufferSpace++;

                if (usedBufferSpace == 8) clearBuffer();
            }
        }
        clearBuffer(); // Vider tout bits potentiellement restant dans le buffer
    }

    /**
     * Methode appelle quand le buffer contient 1 byte (8 bits) pour ecrire sur stdOut
     */
    private static void clearBuffer() {
        if (usedBufferSpace == 0) return; // Rien a faire le buffer est vide
        if (usedBufferSpace > 0) byteBuffer <<= (8 - usedBufferSpace); // Assure que c'est un byte (décalage sur 8 bit)
        try {
            // Ecrit le byte dans system.out
            out.write(byteBuffer);
            // Assure que buffer est vide
            out.flush();
            totalBytesWritten += 1;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        usedBufferSpace = 0;
        byteBuffer = 0;
    }

    /**
     * Methode qui ecrit les statistques de compression dans un fichier performances.csv
     * @param filePath : chemin du fichier a compresse
     */
    public  static void writePerformance(String filePath) throws IOException{
        File perfomanceReport = new File("./performances.csv");
        File uncompressedFile = new File(filePath);

        FileWriter filewriter = new FileWriter(perfomanceReport, true);
        PrintWriter printWriter = new PrintWriter(filewriter);
        if (perfomanceReport.length() == 0){  // Si le fichier etait vide, on ajoute un header
            printWriter.write("Filename; Compression time in ms; Original in Byte; Compressed Size in Byte; Compression ratio \r\n");
        }
        printWriter.printf("%s; %d; %d; %d; %f \r\n",
                           uncompressedFile.getName(),
                           System.currentTimeMillis() - startTime,
                           uncompressedFile.length(),
                           totalBytesWritten,
                           (float)totalBytesWritten / (float)uncompressedFile.length() * 100.0);
        printWriter.close();
    }
}
