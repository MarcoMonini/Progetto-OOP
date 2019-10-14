package com.esameOOP.Progetto.Controller;

import com.esameOOP.Progetto.Modello.CasiLegali;
import com.esameOOP.Progetto.service.Download;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    private Download service;

    /**
     * Costruttore della classe Controller
     *
     * @param service
     */
    @Autowired
    public Controller(Download service) {
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
    @GetMapping("/getAnni")
    public List getAnni() {
        return service.getAnni();
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

    /**
     *
     *
     * @param nameField
     * @return
     */
    /*@GetMapping("/getStatistiche")
    public List getStatistiche(@RequestParam(value = "Field", required = false, defaultValue = "") String nameField) {
        if (!nameField.equals("")) {
            List<Map> lista = new ArrayList<>();
            lista.add()
        }
    }*/
}