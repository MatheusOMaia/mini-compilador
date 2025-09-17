package util;

import java.util.Hashtable;
import java.util.Objects;

public class PalavrasReservadas {
    Hashtable<Integer, String> tabelaReservadas = new Hashtable<Integer, String>();

    public PalavrasReservadas(){
            tabelaReservadas.put(0,"int");
            tabelaReservadas.put(1,"float");
            tabelaReservadas.put(2,"print");
            tabelaReservadas.put(3, "if");
            tabelaReservadas.put(4, "else");
    }

    public boolean isReservada(String cadeia){
        for (int i = 0; i <= 4; i++){
            if(Objects.equals(cadeia, tabelaReservadas.get(i)))
                return true;
        }
        return false;
    }
}
