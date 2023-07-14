/** 
 * interfaccia che determina il comportamento di un Cifrario, rende possibile la cifratura e la decifratura di 
 * messaggi 
 */
public interface Cifrario {

    /**
     * cifra il messaggio, il messaggio che riceve è già stato convertito in binario e verrà 
     * restituito nella stessa maniera
     * 
     * @param plainText sequenza di 1 e 0, intesi come valore booleano true = 1, false = 0
     * @throws IllegalArgumentException se la lunghezza del plaintext è sbagliata per il metodo ci cifratura
     */
    public boolean[] cifra (final boolean[] plainText);

    /**
     * decifra il messaggio, il messaggio che riceve è già stato convertito in binario e verrà 
     * restituito nella stessa maniera
     * 
     * @param cipherText sequenza di 1 e 0, intesi come valore booleano true = 1, false = 0
     * @throws IllegalArgumentException se la lunghezza del plaintext è sbagliata per il metodo ci cifratura
     */
    public boolean[] decifra (boolean[] cipherText);      
    



    // si potrebbe fare anche ricevendo le stringhe e non i plaintext
    // sia le stringhe alfanumeriche sia stringhe con stringhe di 1 0
    // aggiungere metodi che cifrano e decifrano partendo da testo in stringa ( di 0 e 1) o usare generici

    // /**
    //  * decifra il messaggio, il messaggio che riceve è già stato convertito in binario e verrà 
    //  * restituito nella stessa maniera
    //  * 
    //  * @param cipherText sequenza di 1 e 0 da decifrare, la sequenza è una stringa
    //  * @throws IllegalArgumentException se la stringa contiene caratteri diversi da 1 o 0
    //  * @throws IllegalArgumentException se la stringa è vuota
    //  * @throws IllegalArgumentException se la stringa è di lunghezza diversa da quella prevista dal tipo di cifrario
    //  * @throws NullPointerException se cipherText è null
    //  */
    // public String decifra (String cipherText);      

}