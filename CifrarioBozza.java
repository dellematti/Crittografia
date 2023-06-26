import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class CifrarioBozza{


    private final boolean[] chiave;
    // private final boolean[] plainText;


    // ricevo una chiave ed un plaintext entrambi da 64 bit
    // public Cifrario(boolean[] chiave, boolean[] plainText) {
    public CifrarioBozza(boolean[] chiave) {
        this.chiave = new boolean[56];
        // this.plainText = new boolean[64];
        int i = 0;

        // for ( boolean b : plainText) this.plainText[i++] = b;
        //fare questa parte in un metodo fuori dal costruttore
        try{
            File file = new File("permutazioneChiave.txt");
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int j = 0; j < this.chiave.length; j++) {
                posizione = s.nextInt() - 1;
                this.chiave[j] = chiave[posizione];
            }
        }catch (Exception e) {
            System.out.println(e.getClass());
        }
        
    }


    // METODI


    private boolean[] xor (boolean[] a, boolean[] b){
        if ( a.length != b.length) throw new IllegalArgumentException("a e b sono di lunghezza diversa");
        boolean[] risultato = new boolean[a.length];
        for(int i = 0; i < risultato.length; i++)
            if ((a[i] == true && b[i] == false) || (a[i] == false && b[i] == true)) risultato[i] = true;      
        return risultato;
    }


    // potrei fare una classe separata per "funzione"
    private boolean[] funzione (boolean[] testo, boolean[] sottoChiave) {
        boolean[] testoEspanso = new boolean[48]; 
        // testoEspanso[0] = testo[31];
        // testoEspanso[1] = testo[0];
        // .....
        // testoEspanso[47] = testo[0];

        // System.out.println("sono dentro funzione");

        try{
            File file = new File("espansione.txt");
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < testoEspanso.length; i++) {
                posizione = s.nextInt() - 1;
                testoEspanso[i] = testo[posizione];
            }
        }catch (Exception e) {
            System.out.println(e.getClass());
        }

        boolean[] inputSBOX = xor(testoEspanso, sottoChiave);   
        // ora in inputSbox ho i bit da dividere in gruppi da 6
        // PARTE 3


        Sbox[] sbox = new Sbox [8];  
        for (int i = 1; i <= 8; i++) sbox[i-1] = new Sbox(i);
   
        // ora devo dividere inputSbox in 8, e ogni parte passarla ad un Sbox diverso, metto insieme i risultati in outputfunzione


        boolean[] outputFunzione = new boolean[32];  
        for ( int i = 0; i < 8; i++ ) {
            boolean[] tmp = sbox[i].comprimi(Arrays.copyOfRange(inputSBOX, i * 6 , (i+1) * 6 ));
            int k = 0;
            for ( int j = i*4; j < (i+1)*4; j++) outputFunzione[j] = tmp[k++];
        }

      

        // ora in outputFunzione ho i risultati dei sbox messi insieme
        // PARTE 4  permuto per l ultima volta

        boolean[] outputFinale = new boolean[32];
        try{
            File file = new File("permutazioneFunzione.txt");
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < outputFunzione.length; i++) {
                posizione = s.nextInt() - 1;
                outputFinale[i] = outputFunzione[posizione];
            }
        }catch (Exception e) {
            System.out.println(e.getClass());
        }


        return outputFinale; 
    }         //dovrebbe essere ok




    private void shift ( boolean[] mezzaChiave, int round){   //round da 1 a 16, parto da 1 non 0
        // mezzaChiave[0] = mezzaChiave[1]

        int shiftAmount;
        if ( round == 1 || round == 2 || round == 9 || round == 16) shiftAmount = 1;
        else shiftAmount = 2;

        boolean tmp = mezzaChiave[0];
        boolean tmp2 = mezzaChiave[1];
        for ( int i = 0; i < mezzaChiave.length - shiftAmount; i++){
            mezzaChiave[i] = mezzaChiave[i + shiftAmount];
        }
        mezzaChiave[27] = tmp;

        if (shiftAmount == 2){
            mezzaChiave[26] = tmp;
            mezzaChiave[27] = tmp2;
        }


    }     // Dovrebbe essere ok


    private boolean[] sottochiave (boolean[] parteSx, boolean[] parteDx) {          //qua prendo le due mezze chiavi, le unisco e permuto togliendo 8 bit
        // dovrei mettere la condizione che parteSx e parteDx sono entrambe di 28 bit di lunghezza
        boolean[] tmp = new boolean[56];
        for(int i = 0; i < parteSx.length; i++) tmp[i] = parteSx[i];
        for(int i = 0; i < parteDx.length; i++) tmp[i + 28] = parteDx[i];
        boolean[] sottoChiave = new boolean[48];       //ora dedvo permutare tmp, scartando 8 bit
        
        try {
            File file = new File("pc2.txt");  //mettere in un try catch per evitare eccezioni se non trova il file
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < sottoChiave.length; i++){
                posizione = s.nextInt() - 1;
                sottoChiave[i] = tmp[posizione];
            }

        } catch (Exception e) {
          System.out.println(e.getClass());
        }

        return sottoChiave;
    }  //dovrebbe essere ok


    public boolean[] cifra (boolean[] plainText){          

        // permutazione iniziale
        boolean[] plainTextPermutato = new boolean[64];
        try {
            File file = new File("permutazioneIniziale.txt");  
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < plainText.length; i++){
                posizione = s.nextInt() - 1;
                plainTextPermutato[i] = plainText[posizione];
            }

        } catch (Exception e) {
          System.out.println(e.getClass());
        }



        // chiavi
        boolean[] chiaveParteSx = Arrays.copyOfRange(chiave, 0, 28);
        boolean[] chiaveParteDx = Arrays.copyOfRange(chiave, 28, 56);


        boolean[][] sottoChiavi = new boolean [16][48];        //qua avrò tutte le sottochiavi
        for ( int round = 1; round <= 16; round++){
            shift(chiaveParteSx, round);  
            shift(chiaveParteDx, round);   
            sottoChiavi[round - 1] = sottochiave(chiaveParteSx, chiaveParteDx);
        }



        boolean[] plaintextSx = new boolean[32];           
        boolean[] plaintextDx = new boolean[32];             
        for ( int i = 0; i < 32; i++) {
            plaintextSx[i] = plainTextPermutato[i];
            plaintextDx[i] = plainTextPermutato[i+32];
        }

        // da qua inizio il round  
        for (int round = 1; round <= 16; round++) {
            boolean[] tmp = new boolean[32];                     
            for ( int i = 0; i < 32; i++) tmp[i] = plaintextDx[i];


            boolean[] tmp2 = new boolean[32];                     
            tmp2 = xor(plaintextSx , funzione(plaintextDx, sottoChiavi[round - 1]));

            for ( int i = 0; i < 32; i++) plaintextSx[i] = tmp[i];
            for ( int i = 0; i < 32; i++) plaintextDx[i] = tmp2[i];
   
        }


        boolean[] cipherText = new boolean[64];
        for (int i = 0; i < 32; i++) cipherText[i] = plaintextDx[i];
        for (int i = 0; i < 32; i++) cipherText[i + 32] = plaintextSx[i];

        // permutazione finale



        boolean[] cipherTextPermutato = new boolean[64];
        try {
            File file = new File("permutazioneFinale.txt");  
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < cipherText.length; i++){
                posizione = s.nextInt() - 1;
                cipherTextPermutato[i] = cipherText[posizione];
            }

        } catch (Exception e) {
          System.out.println(e.getClass());
        }

        return cipherTextPermutato;     
    }


    // dato che questa funzione è UGUALE a cifra, a parte per l ordine in cui uso le chiavi, posso cercare un modo per unirle
    public boolean[] decifra (boolean[] testoDaDecifrare){          
        // permutazione iniziale
        boolean[] plainTextPermutato = new boolean[64];     //cambiare i nomi
        try {
            File file = new File("permutazioneIniziale.txt");  
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < testoDaDecifrare.length; i++){
                posizione = s.nextInt() - 1;
                plainTextPermutato[i] = testoDaDecifrare[posizione];
            }
        } catch (Exception e) {
          System.out.println(e.getClass());
        }

        // chiavi
        boolean[] chiaveParteSx = Arrays.copyOfRange(chiave, 0, 28);
        boolean[] chiaveParteDx = Arrays.copyOfRange(chiave, 28, 56);
        boolean[][] sottoChiavi = new boolean [16][48];        //qua avrò tutte le sottochiavi
        for ( int round = 1; round <= 16; round++){
            shift(chiaveParteSx, round);  
            shift(chiaveParteDx, round);   
            sottoChiavi[round - 1] = sottochiave(chiaveParteSx, chiaveParteDx);
        }

        boolean[] plaintextSx = new boolean[32];           
        boolean[] plaintextDx = new boolean[32];             
        for ( int i = 0; i < 32; i++) {
            plaintextSx[i] = plainTextPermutato[i];
            plaintextDx[i] = plainTextPermutato[i+32];
        }

        // da qua inizio i round  
        for (int round = 1; round <= 16; round++) {
            boolean[] tmp = new boolean[32];                     
            for ( int i = 0; i < 32; i++) tmp[i] = plaintextDx[i];
            boolean[] tmp2 = new boolean[32];                     
            tmp2 = xor(plaintextSx , funzione(plaintextDx, sottoChiavi[16 - round]));  //unica riga diversa da cifra
            for ( int i = 0; i < 32; i++) plaintextSx[i] = tmp[i];
            for ( int i = 0; i < 32; i++) plaintextDx[i] = tmp2[i];
        }

        boolean[] cipherText = new boolean[64];
        for (int i = 0; i < 32; i++) cipherText[i] = plaintextDx[i];
        for (int i = 0; i < 32; i++) cipherText[i + 32] = plaintextSx[i];

        // permutazione finale            //fare funzione per le permutazioni
        boolean[] cipherTextPermutato = new boolean[64];
        try {
            File file = new File("permutazioneFinale.txt");  
            final Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < cipherText.length; i++){
                posizione = s.nextInt() - 1;
                cipherTextPermutato[i] = cipherText[posizione];
            }
        } catch (Exception e) {
          System.out.println(e.getClass());
        }

        return cipherTextPermutato;     
    }






    @Override
    public String toString(){
        String s = "";
        s += "La chiave è :\n";
        for ( int i = 0; i < chiave.length; i++) 
            if ( chiave[i] == false) s += "0";
            else s += "1";
            
        return s;
    }




}