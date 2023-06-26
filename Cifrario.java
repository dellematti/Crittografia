import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

public class Cifrario{

    // l h modificato

    // CAMPI

    private final boolean[] chiave;


    // COSTRUTTORI


    // ricevo una chiave da 64 bit e la permuto in una da 56
    public Cifrario(final boolean[] chiave) {
        this.chiave = permutazione(chiave, "permutazioneChiave.txt", 56 );        
    } 

    // tutti i metodi private non sono metodi di istanza, potrei fare una classe di utilità tipo per permutazione e altri metodi
    // non saprei come chiamarla, ma una classe tipo con gli xor e gli shift

    // METODI


    private static boolean[] xor (final boolean[] a, final boolean[] b){
        if ( a.length != b.length) throw new IllegalArgumentException("a e b sono di lunghezza diversa");
        boolean[] risultato = new boolean[a.length];
        for(int i = 0; i < risultato.length; i++)
            if ((a[i] == true && b[i] == false) || (a[i] == false && b[i] == true)) risultato[i] = true;      
        return risultato;
    }


    // potrei fare una classe separata per "funzione"
    private static boolean[] funzione (boolean[] testo, boolean[] sottoChiave) {
       
        boolean[] testoEspanso = permutazione(testo,"espansione.txt", 48 ); 
        boolean[] inputSBOX = xor(testoEspanso, sottoChiave);   
        // ora in inputSbox ho i bit da dividere in gruppi da 6

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
        boolean[] outputFinale = permutazione(outputFunzione,"permutazioneFunzione.txt", 32 );

        return outputFinale; 
    }       



    private static void shift ( boolean[] mezzaChiave, int round){   //round da 1 a 16, parto da 1 non 0
        int shiftAmount;
        if ( round == 1 || round == 2 || round == 9 || round == 16) shiftAmount = 1;
        else shiftAmount = 2;

        boolean tmp = mezzaChiave[0];
        boolean tmp2 = mezzaChiave[1];
        for ( int i = 0; i < mezzaChiave.length - shiftAmount; i++)
            mezzaChiave[i] = mezzaChiave[i + shiftAmount];
        if (shiftAmount == 1) mezzaChiave[27] = tmp;
        else { //se non è 1 allora so già che è 2
            mezzaChiave[26] = tmp;
            mezzaChiave[27] = tmp2;
        }
    }    


   //qua prendo le due mezze chiavi, le unisco e permuto togliendo 8 bit
    private static boolean[] sottochiave (boolean[] parteSx, boolean[] parteDx) {       
        // dovrei mettere la condizione che parteSx e parteDx sono entrambe di 28 bit di lunghezza
        boolean[] tmp = new boolean[56];
        for(int i = 0; i < parteSx.length; i++) tmp[i] = parteSx[i];
        for(int i = 0; i < parteDx.length; i++) tmp[i + 28] = parteDx[i];

        boolean[] sottoChiave = permutazione(tmp, "pc2.txt", 48 );

        return sottoChiave;
    }  


    // chiedo anche la dimensione perchè non è detto che la dimensione resti uguale, rispetto al vettore da permutare
    private static boolean[] permutazione ( boolean[] daPermutare,  String nomeFile,  int dimensione) {
        boolean[] permutato = new boolean[dimensione];
        try {
            File file = new File(nomeFile); 
            Scanner s = new Scanner(file);
            int posizione;
            for ( int i = 0; i < dimensione; i++){   // && s.hasNext()
                posizione = s.nextInt() - 1;
                permutato[i] = daPermutare[posizione];
            }
        } catch (Exception e) {
        System.out.println("errore");
            System.out.println(e.getClass());
        }
        return permutato;
    }


    // static private boolean[] des (final boolean [] input, final boolean[][] sottochiavi ) {}


    // cifrario potrebbe essere un interfaccia con cifra e decifra e des implementa quell interfaccia


    public boolean[] cifra (final boolean[] plainText){          
        // permutazione iniziale
        boolean[] plainTextPermutato = permutazione(plainText, "permutazioneIniziale.txt", 64 );

        // creo le sottochiavi
        boolean[] chiaveParteSx = Arrays.copyOfRange(chiave, 0, 28);
        boolean[] chiaveParteDx = Arrays.copyOfRange(chiave, 28, 56);
        boolean[][] sottoChiavi = new boolean [16][48];        //qua avrò tutte le sottochiavi
        for ( int round = 1; round <= 16; round++){
            shift(chiaveParteSx, round);  
            shift(chiaveParteDx, round);   
            sottoChiavi[round - 1] = sottochiave(chiaveParteSx, chiaveParteDx);
        }

        // divido il plaintext nella parte sinistra e destra
        boolean[] plaintextSx = Arrays.copyOfRange(plainTextPermutato, 0, 32);
        boolean[] plaintextDx = Arrays.copyOfRange(plainTextPermutato, 32, 64);
        // qua eseguo i 16 round
        for (int round = 1; round <= 16; round++) {
            boolean[] tmp = plaintextDx.clone();
            plaintextDx = xor(plaintextSx , funzione(plaintextDx, sottoChiavi[round - 1]));
            plaintextSx = tmp.clone();
        }
        // unisco le due metà finali
        boolean[] cipherText = new boolean[64];
        for (int i = 0; i < 32; i++) cipherText[i] = plaintextDx[i];
        for (int i = 0; i < 32; i++) cipherText[i + 32] = plaintextSx[i];

        // permutazione finale
        boolean[] cipherTextPermutato = permutazione(cipherText, "permutazioneFinale.txt", 64 );

        return cipherTextPermutato;     
    }


    // dato che questa funzione è UGUALE a cifra, a parte per l ordine in cui uso le chiavi, posso cercare un modo per unirle
    public boolean[] decifra (boolean[] plainText){          

        // creo le sottochiavi                     //questa è l unica parte diversa da cifra e decifra
        boolean[] chiaveParteSx = Arrays.copyOfRange(chiave, 0, 28);
        boolean[] chiaveParteDx = Arrays.copyOfRange(chiave, 28, 56);
        boolean[][] sottoChiavi = new boolean [16][48];        //qua avrò tutte le sottochiavi
        for ( int round = 1; round <= 16; round++){
            shift(chiaveParteSx, round);  
            shift(chiaveParteDx, round);   
            // sottoChiavi[round - 1] = sottochiave(chiaveParteSx, chiaveParteDx);
            sottoChiavi[16 - round] = sottochiave(chiaveParteSx, chiaveParteDx);   // UNICA RIGA DIVERSA DA CIFRA
        }

        // return des ( plainText, sottoChiavi);

        // permutazione iniziale
        boolean[] plainTextPermutato = permutazione(plainText, "permutazioneIniziale.txt", 64 );

        // divido il plaintext nella parte sinistra e destra
        boolean[] plaintextSx = Arrays.copyOfRange(plainTextPermutato, 0, 32);
        boolean[] plaintextDx = Arrays.copyOfRange(plainTextPermutato, 32, 64);
        // qua eseguo i 16 round
        for (int round = 1; round <= 16; round++) {
            boolean[] tmp = plaintextDx.clone();
            // plaintextDx = xor(plaintextSx , funzione(plaintextDx, sottoChiavi[16 - round]));  // UNICA RIGA DIVERSA DA CIFRA
            plaintextDx = xor(plaintextSx , funzione(plaintextDx, sottoChiavi[round - 1]));
            plaintextSx = tmp.clone();
        }
        // unisco le due metà finali
        boolean[] cipherText = new boolean[64];
        for (int i = 0; i < 32; i++) cipherText[i] = plaintextDx[i];
        for (int i = 0; i < 32; i++) cipherText[i + 32] = plaintextSx[i];

        // permutazione finale
        boolean[] cipherTextPermutato = permutazione(cipherText, "permutazioneFinale.txt", 64 );

        return cipherTextPermutato;     
    }



    // metodo getchiave



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