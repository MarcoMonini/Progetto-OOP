package com.esameOOP.Progetto.Controller;

import com.esameOOP.Progetto.Model.CasiLegali;
import com.esameOOP.Progetto.Service.Download;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CasiLegaliControllo {
    private Download service;

    /**
     * Costruttore della classe Controller
     *
     * @param service
     */
    @Autowired
    public CasiLegaliControllo(Download service) {
        this.service = service;
    }

    /**
     * Metodo GET che su richiesta dell'utente restituisce l'intero dataset
     *
     * @return "record" ovvero la lista con gli oggetti del dataset
     */
    @GetMapping("/getRecord")
    public List getRecord() {
        return service.getRecord();
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
     * Metodo GET che su richiesta dell'utente restituisce un elemento all'indice i della lista "record" che contiene gli oggetti NottiNazione
     *
     * @param i indice della lista che si vuole ottenere
     * @return "record" ovvero la lista con gli oggetti NottiNazione
     */
    @GetMapping("/getRecord/{i}")
    public CasiLegali getCasiLegali(@PathVariable int i) {
        return service.getRecord(i);
    }


    @GetMapping("/getStatistiche")
    public List getStatistiche(@RequestParam(value = "Field", required = false, defaultValue = "") String nameField) {
        if (nameField.equals(""))
            return service.getAllFieldStatistics();
        else {
            return service.getField(nameField);
        }

    }
}