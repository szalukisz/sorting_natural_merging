package sorting;

import java.io.RandomAccessFile;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Scanner input = new Scanner(System.in);
        boolean run = true;
        IOLayer ioLayer = new IOLayer();
        String filename = null;
        while (run) {

            System.out.println("\n-------------------------\nFile: " + (filename != null ? filename : "null") +
                    "\nChoose one number: \n1)Generate random tape\n2)Import tape from the file\n" +
                    "3)Sort tape\n0)Exit\n-------------------------");
            try {
                switch(Integer.parseInt(input.nextLine())){
                    case 0: //exiting from the program
                        run = false;
                        break;
                    case 1: // generating scope number of random records into file
                        System.out.print("N: ");
                        int scope=10;
                        try {
                            scope = Integer.parseInt(input.nextLine());
                        }catch (Exception e){
                            System.out.println("Incorrect input");
                            e.printStackTrace();
                        }
                        System.out.println("Generating File");
                        filename =  generateFile(scope, ioLayer);
                        break;
                    case 2: // reading filename from user and displaying all records in console
                        System.out.println("File name: ");
                        try {
                            filename = input.nextLine();
                            int i=0;
                            Record r;
                            while((r = ioLayer.readRecord(i++,filename))!= null){
                                System.out.println(r.toString());
                            }
                        }catch (Exception e){
                            System.out.println("Incorrect filename");
                            e.printStackTrace();
                        }
                        break;
                    case 3: // sorting file using natural sort 2+2
                        System.out.println("\nSorting...");
                        sortFile(ioLayer, filename);
                        break;
                    default:
                        System.out.println("Incorrect option");
                }
            }
            catch(Exception e){
                System.out.println("It's not a number");
                e.printStackTrace();
            }
        }
    }

    private static String generateFile(int scope, IOLayer ioLayer){
        String fileName = "randomGeneratedRecords.dat";
        ioLayer.deleteRecords(fileName);
        Random random = new Random();
        for (int i = 0; i < scope; i++) {
            float [] random_tab = new float[5];
            for (int j = 0; j < 5; j++) {
                random_tab[j] = random.nextFloat()*20;
            }
            Record temp_record = new Record(random_tab);
            //System.out.println(temp_record.toString());
            ioLayer.writeRecord(fileName,temp_record);
        }
        return fileName;
    }

    private static void sortFile(IOLayer layer, String filename) {
        ArrayList<Tape> tapes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tapes.add(new Tape("Tape"+ i +".dat"));
        }

        if(distributeOnTapes(tapes, layer,filename)==0){
            nextStages(tapes);
        }
        System.out.println("File is fully sorted");
    }

    private static int distributeOnTapes(ArrayList<Tape>  tapes, IOLayer layer, String filename){
        // reading from main file and placing it on the tapes
        int stage =0;
        int i =0;
        int sorted = 0; //checker if fully sorted
        int tapeNum = 1;    // currentTape
        Record curr_record, prev_record = null;
        System.out.println("Distributing on tapes");
        for (Tape te:tapes) {
            te.clearTape();
        }
        while((curr_record = layer.readRecord(i++,filename))!= null) {
            if(i == 1 || prev_record.getKey()>curr_record.getKey())
                tapeNum = tapeNum == 1? 0:1;

            tapes.get(tapeNum).placeOnTape(curr_record);
            prev_record = curr_record;
        }
        System.out.println("Stage "+ ++stage+" completed.\n");
        if (tapes.get(1).readFromTape(0) == null) {
            sorted = 1;
        }
        return sorted;
    }

    private static void nextStages(ArrayList<Tape> tapes){
        int sorted = 0;
        int stage = 1;
        int k = 2;

        while(sorted==0) {
            // indexes for each tape; controls for ends of runs, ends of tapes
            int tape0 = 0, tape1 = 0, t0wait = 0, t1wait = 0;
            Record tape0_curr, tape0_prev = null, tape1_curr, tape1_prev = null;
            // clearing tapes before next stage
            tapes.get(k).clearTape();
            tapes.get(k+1).clearTape();
            k = (k == 2) ? 0 : 2;
            int tapeNum = (k == 2) ? 0 : 2;     // selecting tape based on the stage number
            while (true) {

                tape0_curr = tapes.get(k).readFromTape(tape0);
                tape1_curr = tapes.get(k + 1).readFromTape(tape1);
                if (tape0_curr == null && tape1_curr == null) {
                    if (tapes.get(k == 2 ? 1 : 3).readFromTape(0) == null) {
                        sorted = 1;
                    }
                    break;
                }

                // checking end of run on each tape
                if (tape0_curr == null || (tape0_prev != null && tape0_curr.getKey() < tape0_prev.getKey())) {
                    t0wait = 1;
                }
                if (tape1_curr == null || (tape1_prev != null && tape1_curr.getKey() < tape1_prev.getKey())) {
                    t1wait = 1;
                }

                if (t1wait + t0wait == 2) { //changing tape if both runs are taken
                    tapeNum = (tapeNum%2 == 1) ? tapeNum-1 : tapeNum+1;
                    t1wait = tape1_curr!=null?0:1;
                    t0wait = tape0_curr!=null?0:1;
                    tape0_prev = null;
                    tape1_prev = null;
                }

                // Analyzing key values and placing on tapes
                if (t0wait == 0 && t1wait == 1) {   //run from tape1 fully consumed or tape1 has ended
                    tapes.get(tapeNum).placeOnTape(tape0_curr);
                    tape0_prev = tape0_curr;
                    tape0++;
                } else if (t0wait == 1 && t1wait == 0) {    //run from tape0 fully consumed or tape0 has ended
                    tapes.get(tapeNum).placeOnTape(tape1_curr);
                    tape1_prev = tape1_curr;
                    tape1++;
                } else if (t0wait == 0 && tape0_curr.getKey() < tape1_curr.getKey()) {      // taking higher key value
                    tapes.get(tapeNum).placeOnTape(tape0_curr);
                    tape0_prev = tape0_curr;
                    tape0++;
                } else if (t1wait == 0) {
                    tapes.get(tapeNum).placeOnTape(tape1_curr);
                    tape1_prev = tape1_curr;
                    tape1++;
                }
            }
            System.out.println("Stage "+ ++stage+" completed.\n");
        }
    }

}
