package sorting;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean run = true, printAfterEachState = false;
        int pageSize = 5;
        Tape mainTape = null;
        String filename = null;
        while (run) {
            System.out.print("\n-------------------------\nFile: " + (mainTape != null ? mainTape.name : "null") +
                    " | PageSize: " + pageSize +" records\nPrinting tapes after each stage: " +
                    ((printAfterEachState)?"YES":"NO")+"\nChoose one number: \n1)Generate random tape\n2)Import tape " +
                    "from the file\n3)Read records from keyboard\n4)Change page size\n5)Enable/Disable printing while" +
                    " sorting\n6)Sort tape\n0)Exit\n-------------------------\n>");
            try {
                switch(Integer.parseInt(input.nextLine())) {
                    case 0: //exiting from the program
                        run = false;
                        break;
                    case 1: // generating scope number of random records into file
                        System.out.print("N: ");
                        int scope;
                        try {
                            scope = Integer.parseInt(input.nextLine());
                        } catch (Exception e) {
                            System.out.println("Incorrect input");
                            break;
                        }
                        System.out.println("Generating File");
                        filename = generateFile(scope, pageSize, printAfterEachState);
                        mainTape = new Tape(filename, pageSize);
                        System.out.println("File generated.");
                        break;
                    case 2: // reading filename from user and displaying all records in console
                        System.out.println("File name: ");
                        try {
                            System.out.print("> ");
                            filename = input.nextLine();
                            mainTape = new Tape(filename, pageSize);
                            if(printAfterEachState){mainTape.printTape();}
                        } catch (Exception e) {
                            System.out.println("Incorrect filename");
                            e.printStackTrace();
                        }
                        break;
                    case 3: // reading records from users' keyboard
                        filename = readFromKeyboard(pageSize, input);
                        mainTape = new Tape(filename, pageSize);
                        System.out.println("Records saved in file.");
                        if(printAfterEachState){mainTape.printTape();}
                        break;
                    case 4: //setting page size
                        System.out.print("Insert page size: ");
                        try {
                            pageSize = Integer.parseInt(input.nextLine());
                            mainTape = new Tape(filename, pageSize);
                        } catch (Exception e) {
                            System.out.println("Incorrect input");
                        }
                        break;
                    case 5: // switch for printing
                        printAfterEachState= !printAfterEachState;
                        System.out.println("Printing after each stage "+(printAfterEachState?"enabled.":"disabled."));
                        break;
                    case 6: // sorting file using natural sort 2+2
                        if (mainTape != null){
                            System.out.println("\nSorting...");
                            if(printAfterEachState){
                                mainTape.printTape();
                            }
                            sortFile(mainTape, pageSize, printAfterEachState);
                        }else{
                            System.out.println("First select or generate file to sort.");
                        }
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

    private static String generateFile(int scope,int pageSize, boolean print){
        String fileName = "randomGeneratedRecords.dat";
        IOLayer.deleteRecords(fileName);
        Random random = new Random();
        Tape randomTape = new Tape(fileName,pageSize);
        for (int i = 0; i < scope; i++) {
            float [] random_tab = new float[5];
            for (int j = 0; j < 5; j++) {
                random_tab[j] = random.nextFloat()*20*(float)Math.pow(-1.0,random.nextInt(2));
            }
            Record temp_record = new Record(random_tab);
            if(print)
                System.out.println(temp_record.toString());
            randomTape.writeRecordOnTape(temp_record);
        }
        randomTape.flushTape();
        return fileName;
    }

    private static String readFromKeyboard(int pageSize, Scanner input){
        String fileName = "RecordsFromKeyboard.dat";
        IOLayer.deleteRecords(fileName);
        Tape keyboardTape = new Tape(fileName, pageSize);
        System.out.println("Write records in format: 1 2.2 3. .4 5\nSingle 0 ends reading from keyboard");
        while(true) {
            System.out.print("> ");
            String inp = input.nextLine();
            if(inp.equals("0")){break;}     // check if ending condition has been achieved
            float[] floats = new float[5];
            String[] numbers = inp.split("\\s+");
            int i = 0;
            if(!inp.isEmpty()){
                for (String num: numbers) {
                    try {
                        if(i==5){i++;break;}    // check if there is 5 params
                        if(num.isEmpty()){continue;}    // if there was space continue
                        floats[i++] = Float.parseFloat(num.trim());
                    }catch(Exception e){
                        System.out.println("Incorrect float format.");
                        break;
                    }
                }
            }
            if(i!=5){
                System.out.println("Incorrect number of parameters. Please try again!");
                continue;
            }
            keyboardTape.writeRecordOnTape(new Record(floats));
        }
        keyboardTape.flushTape();
        System.out.println("Finished reading.");
        return fileName;
    }

    private static void sortFile( Tape mainTape, int pageSize, boolean print) {

        mainTape.flushTape();
        mainTape.writesCounter = 0;
        mainTape.readsCounter = 0;
        ArrayList<Tape> tapes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tapes.add(new Tape("Tape"+ i +".dat",pageSize));
        }
        if(distributeOnTapes(tapes,mainTape,print)==0){
            nextStages(tapes, print);
        }
        int reads=mainTape.readsCounter, writes=mainTape.writesCounter;
        for (int i = 0; i <4 ; i++) {
            reads += tapes.get(i).readsCounter;
            writes += tapes.get(i).writesCounter;
        }
        System.out.println("File is fully sorted. Reads: "+reads+", writes: "+writes+",total: "+(reads+writes)+".");
    }

    private static int distributeOnTapes(ArrayList<Tape>  tapes, Tape mainTape, boolean print){
        // reading from main file and placing it on the tapes
        int i =0;
        int tapeNum = 1;    // currentTape
        Record curr_record, prev_record = null;
        System.out.println("Distributing on tapes");
        for (Tape te:tapes) {
            te.clearTape();
        }

        while((curr_record = mainTape.readRecordFromTape(i++))!=null){
            if(prev_record==null || prev_record.getKey()>curr_record.getKey())
                tapeNum = tapeNum == 1? 0:1;

            tapes.get(tapeNum).writeRecordOnTape(curr_record);
            prev_record = curr_record;
        }
        tapes.get(0).flushTape();
        tapes.get(1).flushTape();
        System.out.println("--+--+--+--+Stage 1 (distribution) completed+--+--+--+--");
        if(print) {
            tapes.get(0).printTape();
            tapes.get(1).printTape();
        }

        if (tapes.get(1).readRecordFromTape(0) == null)
            return 1;
        return 0;
    }

    private static void nextStages(ArrayList<Tape> tapes, boolean print){
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
            k = (stage%2==1)?0:2;   // k - checker witch pair of tapes should it use to read now
            int tapeNum = (k == 2) ? 0 : 2;     // selecting tape based on the stage number
            while (true) {
                tape0_curr = tapes.get(k).readRecordFromTape(tape0);
                tape1_curr = tapes.get(k+1).readRecordFromTape(tape1);
                if (tape0_curr == null && tape1_curr == null) {
                    for (Tape tape:tapes) {
                        tape.flushTape();
                    }
                    if (tapes.get(k == 2 ? 1 : 3).readRecordFromTape(0) == null) {
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
                    tapes.get(tapeNum).writeRecordOnTape(tape0_curr);
                    tape0_prev = tape0_curr;
                    tape0++;
                } else if (t0wait == 1 && t1wait == 0) {    //run from tape0 fully consumed or tape0 has ended
                    tapes.get(tapeNum).writeRecordOnTape(tape1_curr);
                    tape1_prev = tape1_curr;
                    tape1++;
                } else if (t0wait == 0 && tape0_curr.getKey() < tape1_curr.getKey()) {      // taking higher key value
                    tapes.get(tapeNum).writeRecordOnTape(tape0_curr);
                    tape0_prev = tape0_curr;
                    tape0++;
                } else if (t1wait == 0) {
                    tapes.get(tapeNum).writeRecordOnTape(tape1_curr);
                    tape1_prev = tape1_curr;
                    tape1++;
                }
            }
            System.out.println("--+--+--+--+Stage "+ ++stage+" completed+--+--+--+--");
            if(print) {
                tapes.get(k == 2 ? 0 : 2).printTape();
                tapes.get(k == 2 ? 1 : 3).printTape();
            }
        }
    }

}
