package com.esameOOP.Progetto.Controller;

import com.esameOOP.Progetto.Model.CasiLegali;
import com.esameOOP.Progetto.Service.Download;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller che gestisce le richieste dell'utente (client)
 */
@RestController
public class CasiLegaliControllo {
    private Download service; //variabile della classe service

    /**
     * Costruttore della classe Controller,
     * che con l'annotazoine @Autovired viene lanciato automaticamente all'avvio da Spring
     * e esegue il collegamento al Service
     *
     */
    @Autowired //stiamo dichiarando che il controllore dipende da service, ovvero stiamo iniettando una dipendenza
    public CasiLegaliControllo(Download service) { this.service = service; }

    /**
     * Metodo GET che su richiesta dell'utente restituisce l'intero dataset, la rotta è /getRecord
     *
     * @return "record" ovvero la lista con gli oggetti del dataset
     */
    @GetMapping("/getRecord")
    public List getRecord() {
        return service.getData();
    }

    /**
     * Metodo GET che su richiesta dell'utente restituisce il vettore degli anni
     *
     * @return "anni" ovvero una lista di string
     */
    @GetMapping("/getTime")
    public List getAnni() {
        return service.getTime();
    }

    /**
     * Metodo GET che su richiesta dell'utente restituisce la lista dei metadata
     *
     * @return "Lista" ovvero la lista nella classe Download che contiene i metadata
     */
    @GetMapping("/getMetadati")
    public List getMetadati() {
        return service.getMetadata();
    }

    /**
     * Metodo GET che su richiesta dell'utente restituisce un elemento all'indice i della lista "record"
     *
     * @param i indice della lista che si vuole ottenere
     * @return "record" ovvero la lista con gli oggetti CasiLegali
     */
    @GetMapping("/getRecord/{i}")
    public CasiLegali getCasiLegali(@PathVariable int i) {
        return service.getData(i);
    }

    /**
     * Metodo che gestisce la richiesta GET alla rotta "/getStatistiche"
     * ritorna le statistiche
     * @param nameField parametro per richiedere le statistiche di un solo campo
     * @return ...
     */
    @GetMapping("/getStatistiche")
    public List getStatistiche(@RequestParam(value = "Field", required = false, defaultValue = "") String nameField) {
        if (nameField.equals(""))
            return service.getAllFieldStatistics();
        else {
            return service.getField(nameField);
        }
    }
        /**
         * Metodo get che restituisce il record filtrato passando il body al metodo
         *
         * @param body body
         */
        @PostMapping("/getFilteredRecord")
        public List getFilteredRecord(@RequestBody String body){
            Map<String, Object> filter = parsingFilter(body);
            String nameField = (String) filter.get("field");
            String oper = (String) filter.get("operator");
            Object reference = filter.get("reference");
            return service.getFilteredRecord(nameField, oper, reference);
        }

        /**
         * Metodo che effettua il parsing del filtro
         *
         * @param body body
         * @return filter, restituisce la mappa filtro
         */
        private Map<String, Object> parsingFilter(String body){
            Map<String, Object> bodyParsato = new BasicJsonParser().parseMap(body);
            String nameField = bodyParsato.keySet().toArray(new String[0])[0];
            System.out.println(nameField);
            Object value = bodyParsato.get(nameField);
            String operator;
            Object reference;
            if(value instanceof Map){
                Map filter = (Map) value;
                operator = ((String) filter.keySet().toArray()[0]).toLowerCase();
                reference = filter.get(operator);
            } else {
                operator = "$gte";
                reference = value;
            }
            Map<String, Object> filter = new HashMap<>();
            filter.put("operator", operator);
            filter.put("reference", reference);
            filter.put("field", nameField);
            return filter;
        }
    }