package com.esameOOP.Progetto.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe Filtri per gestione dei operatori di confronto e operatori logici.
 */
public abstract class Filtri {
    // lista degli operatori di confronto implementati
    private static final List<String> operatori = Arrays.asList("$not", "$in", "$nin", "$and", "$or", "$gt", "$gte", "$lt", "$lte", "$bt");

    /**
     *  Metodo il quale in base all'operatore inserito, il valore value e il riferimento reference vengono confrontati.
     * @param value     valore del campo
     * @param operation operatore
     * @param reference valore di riferimento
     * @return boolean
     */

    private static boolean check(Object value, String operation, Object reference) {
        if (operatori.contains(operation)) {                               // verifica che l'operatore sia uno di quelli gestiti
            if (value instanceof Number) {                                 // caso in cui il valore da controllare sia un numero
                double valueNum = ((Number) value).doubleValue();          //lo converto in double
                if (reference instanceof Number) {                         //caso in cui anche il riferimento sia un numero
                    double rifNum = ((Number) reference).doubleValue();    //lo converto in double
                    switch (operation) {                                   //effettua il confronto corrispndente all'operatore
                        case "$not":
                            return valueNum != rifNum;
                        case "$gt":
                            return valueNum > rifNum;
                        case "$gte":
                            return valueNum >= rifNum;
                        case "$lt":
                            return valueNum < rifNum;
                        case "$lte":
                            return valueNum <= rifNum;
                        default: //quando l'operatore non è adeguato per i valori passati
                            String erroreOper = "L'operatore: '" + operation + "' risulta non funzionante per gli operandi: '" + value + "' , '" + reference + "'";
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper);//restituisce il messaggio di errore in formato JSON
                    }

                } else if (reference instanceof List) { //se il riferimento risulta essere una lista
                    List rifL = ((List) reference); //lo converto in una lista generica
                    if (!rifL.isEmpty() && rifL.get(0) instanceof Number) { // la lista deve essere non vuota e deve contenere numeri
                        //le seguenti righe convertono la lista generica in lista a valori di tipo double
                        List<Double> leftReferenceNum = new ArrayList<>();
                        for (Object elem : rifL) {
                            leftReferenceNum.add(((Number) elem).doubleValue());
                        } //ciclo che effettua la conversione
                        switch (operation) {    //effettua il confronto corrispndente all'operatore
                            case "$in":         //L'Operatore Logico associa ogni valore nell'array
                                return leftReferenceNum.contains(valueNum);
                            case "$nin":        //L'Operatore Logico non associa alcun valore nell'array
                                return !leftReferenceNum.contains(valueNum);
                            case "$bt":         //Operatore Condizionale
                                double first = leftReferenceNum.get(0);
                                double second = leftReferenceNum.get(1);
                                return valueNum >= first && valueNum <= second;
                            default:            //quando l'operatore non è adeguato per i valori passati
                                String erroreOper = "L'operatore: '" + operation + "' risulta non funzionante  per gli operandi: '" + value + "' , '" + reference + "'";
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper); //restituisce un messaggio di errore in formato JSON
                        }
                    } else //altro caso di messaggio di errore nel caso di lista vuota o che non contiene numeri
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista vuota o non numerica");
                } else //altro messaggio di errore nel caso in cui il riferimento non è compatibile con il valore
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il riferimento: '" + reference + "' non è compatibile con il valore: '" + value + "'");
            } else if (value instanceof String || value instanceof Character) {// il valore da controllare è una stringa o un carattere
                if(value instanceof Character) //se il valore è un carattere
                    value=String.valueOf(value);// riporto il carattere ad una stringa
                String valueStr = ((String) value);//conversione
                if (reference instanceof String) { //se il riferimento è una stringa
                    String rifStr = ((String) reference); //conversione
                    if (operation == "$not") {
                        return  !valueStr.equals(rifStr);
                    }
                    String erroreOper = "L'operatore:'" + operation + "' risulta inadatto per gli operandi: '" + value + "' , '" + reference + "'";
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroreOper); //messaggio di errore nel caso in cui l'operatore è inadatto per gli operandi
                }
            } else {
                if (reference instanceof List) {    // il riferimento è una lista
                    List rifList = ((List) reference);
                    if (!rifList.isEmpty() && rifList.get(0) instanceof String) {
                        //la lista deve essere non vuota e deve contenere stringhe
                        // per poter effettuare la conversione da una lista generica ad una lista di stringhe
                        //le seguenti righe effettuano la conversione della lista generica in una lista di stringhe
                        List<String> rifLStr = new ArrayList<>();
                        String valueStr = ((String) value);
                        for (Object elem : rifList) {
                            rifLStr.add((String) elem);
                        }
                        switch (operation) {
                            case "$in":
                                return rifLStr.contains(valueStr);
                            case "$nin":
                                return !rifLStr.contains(valueStr);
                            default:
                                String message = "L'operatore: '" + operation + "' non funziona per gli operandi: '" + value + "' , '" + reference + "'";
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
                        }
                    } else //messaggio di errore se la lista è vuota
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista è vuota");
                } else //messaggio di errore se il riferimento non è compatibile con il valore
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Riferimento:'" + reference + "' non compatibile con il valore'" + value + "'");
            } //messaggio di errore se il valore da controllora non è valido
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valore da controllare non valido: '" + value + "'");
        } else //messaggio di errore se l'operatore non è valido
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operatore " + operation + " non è valido");
    }

    public static List<String> getOperatori() {
        return operatori;
    }

    /**
     * Metodo per l'applicazione dei filtri ad una lista.
     * @param val   lista dei valori
     * @param oper  operatore
     * @param rif   valore di riferimento
     * @return lista di interi contente gli indici dei valori che soddisfano il filtro
     */
    public static List<Integer> filtra(List val, String oper, Object rif) {
        List<Integer> filtrati = new ArrayList<>();
        for (int i = 0; i < val.size(); i++) {
            if (check(val.get(i), oper, rif))       //Controllo per ogni elemento della lista
                filtrati.add(i);                    //aggiunge l'indice alla lista
        }
        return filtrati;                     //Restituisco la lista con gli indici
    }
}
