import java.util.Scanner;

public class Test{



    public static void main(final String[] args) {

        // CONVERTIRE DA PAROLE A BINARIO   
        // si può fare un altra classe
        // considero l ascii fino a 128, uso 8 bit alla volta

        // per ora per comodità uso parole di al massimo 8 lettere, eseguo la cifratura in un solo blocco des

        System.out.println("Inserire parola da cifrare (al massimo 8 lettere) :");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        String plainTextparola = myObj.nextLine();  // Read user input

        boolean[] plainText = new boolean[64];
        int indice = 0;  // indice mi serve per sapere quanti bit del vettore ho utilizzato (in caso di testi minori di 8 lettere)

        for (int i = 0; i < plainTextparola.length(); i++) {
            int decVal = (int) plainTextparola.charAt(i);    // converto a int i singoli caratteri della stringa
            String binario = Integer.toBinaryString(decVal);  //binario del carattere
            String binarioOtto = String.format("%08d", Integer.parseInt(binario));   //binario del carattere a 8 bit
            for ( int j = 0; j < 8; j++){
                if (binarioOtto.charAt(j) == '1') plainText[indice + j] = true;
                else plainText[indice + j] = false;
            }
            indice += 8;
        }
     
        // CHIAVE         
        // l indice stavolta non serve, la chiave in teoria dovrebbe essere sempre di 64 bit ( o 8 caratteri, scelti in maniera casuale) 
        String chiaveParola = "password";
        boolean[] chiave = new boolean[64];

        for (int i = 0; i < chiaveParola.length(); i++) {
            int decVal = (int) chiaveParola.charAt(i);    // converto a int i singoli caratteri della stringa
            String binario = Integer.toBinaryString(decVal);  //binario del carattere
            String binarioOtto = String.format("%08d", Integer.parseInt(binario));   //binario del carattere a 8 bit
            for ( int j = 0; j < 8; j++){
                if (binarioOtto.charAt(j) == '1') chiave[i*8 + j] = true;
                else chiave[i*8 + j] = false;
            }
        }

        Cifrario cifrario = new Des (chiave);    
        System.out.println("Il plaintext è : " + plainTextparola + "\nE la chiave è : " + chiaveParola);  
        boolean[] cipherText = cifrario.cifra(plainText);    // ora ho il testo cifrato in bit  




        // per andare da binario a parole :
        String cipherTextStringa = "";
        String ottoBit = "";

        // for (int i = 0; i <= cipherText.length; i++){          //basta solo indice, (length sarà sempre 64, indice no)
        for (int i = 0; i <= cipherText.length && i <= indice; i++){          //basta solo indice, (length sarà sempre 64, indice no)
            if (i % 8 == 0 && i != 0) {         //prendo 8 bit alla volta
                int numero = Integer.parseInt(ottoBit, 2);   // decimale del carattere
                char riconverto = (char)numero;                 //carattere
                cipherTextStringa +=  riconverto;
                ottoBit = "";
            // }else if (i != cipherText.length)   //anche qua
            }else if (i != cipherText.length && i <= indice)   //anche qua
                if ( cipherText[i] == false) ottoBit += "0";
                else ottoBit += "1";
        }
        System.out.println("\nLa parola cifrata è : " + cipherTextStringa);
        //ora riconverto il cipherText in plainText


        boolean[] decifrato = cifrario.decifra(cipherText);

        // rifaccio gli stessi passaggi di prima per andare da binario a parole  (fare una funzione)
        String stringaDecifrata = "";
        ottoBit = "";

        for (int i = 0; i <= decifrato.length && i <= indice; i++){          //basta solo indice, (length sarà sempre 64, indice no)
        // for (int i = 0; i <= decifrato.length; i++){          //basta solo indice, (length sarà sempre 64, indice no)
            if (i % 8 == 0 && i != 0) {         //prendo 8 bit alla volta
                int numero = Integer.parseInt(ottoBit, 2);   // decimale del carattere
                char riconverto = (char)numero;                 //carattere
                stringaDecifrata +=  riconverto;
                ottoBit = "";
            // }else if (i != decifrato.length)   //anche qua
            }else if (i != decifrato.length && i <= indice)   //anche qua
                if ( decifrato[i] == false) ottoBit += "0";
                else ottoBit += "1";
        }
        System.out.println("la parola decifrata è : " + stringaDecifrata);


        // se non uso l indice mi aggiunge delle lettere a caso alla fine della parola che ho cifrato


        String s = "";
        for ( int i = 0; i < plainText.length; i++){
            if ( i % 8 == 0 && i != 0) s += " "; 
            if ( plainText[i] == false) s += "0";
            else s += "1";
            }
        System.out.println("\nIl plainText in bit è :\n" + s );
        s = "";
        for ( int i = 0; i < cipherText.length; i++){
            if ( i % 8 == 0 && i != 0) s += " "; 
            if ( cipherText[i] == false) s += "0";
            else s += "1";
            }
        System.out.println("\nIl cipherText in bit è :\n" + s + "\n");


    }



}


















        // String s = "";
        // for ( int i = 0; i < plainText.length; i++){
        //     if ( i % 8 == 0 && i != 0) s += " "; 
        //     if ( plainText[i] == false) s += "0";
        //     else s += "1";
        //     }
        // System.out.println("\nIl plainText in bit è :\n" + s );
        // System.out.println(indice);