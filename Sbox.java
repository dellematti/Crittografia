import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;


public class Sbox {

    int[][] box;


    public Sbox(int numeroBox) {         //ricevo numeroBox da 1 a 8 compresi
        List<String> list = new ArrayList<String>();
        try{                        //potrei toglierlo dal costruttore
            File file = new File("Sbox.txt");
            final Scanner s = new Scanner(file);
            while (s.hasNextLine()) {         //leggo 8 volte il file ma tanto sono solo 40 righe
                list.add(s.nextLine());
            }
        }catch (Exception e) {
            System.out.println(e.getClass());
        }
        box = new int[4][16];
        int rigaDaLeggere = 1 + (numeroBox - 1) * 6;       //questo perchè so il formato del input  //sarebbe meglio leggere fino a S

        for ( int i = 0; i < 4; i++) {
            String tmp = list.get(rigaDaLeggere);
            String[] strArray = tmp.split(" ");
            for(int j = 0; j < strArray.length; j++) box[i][j] = Integer.parseInt(strArray[j]);
            rigaDaLeggere++;
        }
    }



    public boolean[] comprimi (boolean[] inputSBOX) {     //ricevo i 6 bit che vanno nel box e restituisco i 4 che escono
        boolean[] outputCompressione = new boolean[4];
        
        String tmpColonna = "";
        for ( int i = 1; i < 5; i++){     //i due ai lati è la riga, i 4 centrali la colonna
            if ( inputSBOX[i] == false) tmpColonna += 0;    
            else tmpColonna += 1;
        }
        String tmpRiga = "";
        if (inputSBOX[0] == true) tmpRiga += 1;
        else tmpRiga += 0;
        if (inputSBOX[5] == true) tmpRiga += 1;
        else tmpRiga += 0;

        int riga = Integer.parseInt(tmpRiga, 2);  //trasformo da binario a decimale
        int colonna = Integer.parseInt(tmpColonna, 2);
        int numeroCompresso = box[riga][colonna];
        String s = Integer.toBinaryString(numeroCompresso);

        // s potrebbe anche essere 101 ad esempio, ma a me servono tutti e 4 bit quindi 0101
        // quindi riempio outputCompressione a partire da destra
        int j = 3;
        for (int i = s.length() - 1; i >= 0; i--) {   
            if (s.charAt(i) == '1') outputCompressione[j] = true;
            j--;
        }
        return outputCompressione;
    }



    // metodo getBox



    @Override
    public String toString(){
        String s = "";
        s += "Il box è :\n";
        for ( int i = 0; i < box.length; i++) {
            for ( int j = 0; j < box[0].length; j++) 
                s += box[i][j] + " "  ;
            s +=   "\n" ;
        }
        return s;
    }


}