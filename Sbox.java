import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;


public class Sbox {

    /**Sbox */
    int[][] box;


    /**costruisce un sbox
     * 
     * @param numeroBox il numero del sbox
     * @throws IllegalArgumentException se il numero box è maggiore di 8 o minore di 1
     */
    public Sbox(int numeroBox) {         
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


    /**
     * uso l' sbox per comprimere 6 bit
     * 
     * @param insputSBOX i 6 bit da comprimere
     * @return il risultato della compressione, saranno sempre 4 bit
     * @throws IllegalArgumentException se inputSBOX ha lunghezza != 6
     */
    public boolean[] comprimi (boolean[] inputSBOX) {    
        boolean[] outputCompressione = new boolean[4];
        //i due bit ai lati sono la riga, i 4 centrali la colonna
        String tmpColonna = "";
        for ( int i = 1; i < 5; i++){     
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