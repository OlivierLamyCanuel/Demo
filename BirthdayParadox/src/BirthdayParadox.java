import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class BirthdayParadox {


    public static void main(String[] args) {
        try {

        String outFile = "BirthdayParadox.txt";
        ArrayList<Player> array = parceFile(args[0]);
        Collections.sort(array);
        int pair = 0;

            writeData("Résultats", outFile);

            for(int i = 0; i<array.size()-1; i++) {
                Player player = array.get(i);
                Player nextPlayer = array.get(i + 1);

                if (player.compareTo(nextPlayer) == 0) {
                    writeData(player + " et " + nextPlayer, outFile);
                    pair++;
                }
            }

            System.out.println("n = " + array.size() + ", n2 = " + pair);
            writeData("n = " + array.size() + " n2 = " + pair, outFile);

        } catch (IOException e){
            e.printStackTrace();
        }

    }



    public static ArrayList<Player> parceFile(String fileName) {

        //Variables
        ArrayList<Player> array = new ArrayList<Player>();
        int line = 1;

        try {

            Scanner scan = new Scanner(new File(fileName));

            while (scan.hasNext()) {

                String tmpString = scan.nextLine().trim();


                if(true) {
                    try {

                        String[] tmpArray = tmpString.split(",");

                        String name = tmpArray[0];
                        int day = Integer.parseInt(tmpArray[2]);
                        int month = Integer.parseInt(tmpArray[1]);
                        int year = 1970;
                        array.add(new Player(name, year, month, day));
                    } catch (ArrayIndexOutOfBoundsException AIE) {

                    }
                    line++;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException en){

        }

        return array;
    }

    public static void writeData(String data, String filePath) throws IOException {

        String HEADER = "Joueurs ayant la même date de naissance (Jour/Mois)";

        File targetFile = new File(filePath);

        FileWriter filewriter = new FileWriter(targetFile, true);
        PrintWriter printWriter = new PrintWriter(filewriter);
        if (targetFile.length() == 0){  // Add a header if needed
            printWriter.write(HEADER + "\r\n");
        }
        printWriter.printf("%s \r\n",
                data);
        printWriter.close();
    }



}