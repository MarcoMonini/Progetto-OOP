package com.esameOOP.Progetto.Controller;

import com.esameOOP.Progetto.Model.CasiLegali;
import com.esameOOP.Progetto.Service.Download;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * @param service
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
}