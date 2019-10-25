package com.esameOOP.Progetto.Service;

import com.esameOOP.Progetto.Model.CasiLegali;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe che carica il dataset e effettua il parsing del file .tsv
 * Un file tsv è una tabella che ha come delimiter il carattere "\t" o ","
 */
@Service
public class Download {
    private static List<CasiLegali> record = new ArrayList<>();     //Lista di oggetti CasiLegali
    private final static String TAB_DELIMITER = "\t";
    private static List<Map> Lista = new ArrayList();               //Lista per i Metadata
    private static List<String> anni = new ArrayList<>();           //lista per i valori durante i vari anni

    /**
     * Costruttore della classe Download
     *Effettua il download e il parsing del tsv
     */
    public Download() throws IOException {

        for(int i = 0; i < CasiLegali.differenza_anni; i++)     //Inizializzo il vettore tempo
            anni.add(Integer.toString((2008+i)));               //riempio la lista con gli anni gestiti
        String fileTSV = "dataset.tsv";                         //file in cui salvare il file tsv
        if (Files.exists(Paths.get(fileTSV)))   {                //verifico l'esistenza del file in locale
            System.out.println("Dataset caricato da un file locale"); //carica il file da locale se esiste
    } else {
            String url = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=7SKEdXk2i1tLAgY0rDBbA";
            try {
                URLConnection openConnection = new URL(url).openConnection();   //apro connessione ad url della mail
                openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0"); //aggiungo user-aget alla connessione
                InputStream in = openConnection.getInputStream();               //prende in input ciò che arriva dall'url
                StringBuilder data = new StringBuilder();
                String line = "";
                try {   //lettura JSON e salvataggio su stringa
                    InputStreamReader inR = new InputStreamReader( in );
                    BufferedReader buf = new BufferedReader( inR );
                    while ( ( line = buf.readLine() ) != null ) {
                        data.append(line);
                        System.out.println( line );
                    }
                } finally {
                    in.close();
                }
                //Conversione StringBuilder in oggetto JSON
                JSONObject obj = (JSONObject) JSONValue.parseWithException(data.toString());
                JSONObject objI = (JSONObject) (obj.get("result"));
                JSONArray objA = (JSONArray) (objI.get("resources"));

                for (Object o : objA) { //scorro tutti gli oggetti fino a trovare quello di formato corretto
                    if (o instanceof JSONObject) {
                        JSONObject o1 = (JSONObject) o;             //converto il generico Object in JSONobject
                        String format = (String) o1.get("format");  //mi sposto all'interno del JSON per trovare l'url desiderato
                        String urlD = (String) o1.get("url");
                        System.out.println(format + " | " + urlD);
                        if (format.toLowerCase().contains("tsv")) { //verifico che il formato sia quello desiderato
                            downloadTSV(urlD, fileTSV);             //effettuo il download del TSV
                        }
                    }
                }
            } catch(IOException | ParseException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Parsing(fileTSV);   //effettua il parsing
        Metadata(fileTSV);
    }

    /**
     * Metodo che effettua il download del TSV su file locale passato come parametro
     * e gestisce un problema di redirect del sito che gestisce i dati
     *
     * @param url url del sito dal quale scaricare il file
     * @param fileName nome del file
     */
    private static void downloadTSV(String url, String fileName) throws Exception{
        HttpURLConnection openConnection = (HttpURLConnection) new URL(url).openConnection();
        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        InputStream input = openConnection.getInputStream();
        String data = "";
        String line = "";
        try {
            if(openConnection.getResponseCode() >= 300 && openConnection.getResponseCode() < 400) {
                downloadTSV(openConnection.getHeaderField("Location"), fileName);
                input.close();
                openConnection.disconnect();
                return;
            }
            Files.copy(input, Paths.get(fileName));
        } finally {
            input.close();
        }
    }

    /**
     * Metodo che effettua il parsing del file in formato TSV
     *
     * @param fileTSV  Stringa con il nome del file tsv
     */
    private void Parsing(String fileTSV){
        try(BufferedReader bRead = new BufferedReader(new FileReader(fileTSV))){  //inizializza il buffer per il parsing
            bRead.readLine();        //Legge una riga a vuoto per saltare l'intestazione
            String linea;
            while((linea = bRead.readLine()) != null) {                          //Ciclo che continua fintanto che non trova una linea nulla
                linea = linea.replace(",", TAB_DELIMITER);                //Sostituisce le virgole con i tab "\t"
                linea = linea.replace(":","0");               //Sostituisce i ":" con "0"
                String[] lineaSeparata = linea.trim().split(TAB_DELIMITER);    //uso split per dividere la riga in corrispondenza dei separatori
                //prendo i valori per ogni campo da lineaSeparata
                String leg_case = lineaSeparata[0].trim();                     //Trim toglie gli spazi prima e dopo la stringa
                String unit = lineaSeparata[1].trim();
                String leg_stat = lineaSeparata[2].trim();
                String geo = lineaSeparata[3].trim();
                float[] time = new float[CasiLegali.differenza_anni];                   //vettore di float che conterrà i valori nei vari anni
                for(int i = 0; i < CasiLegali.differenza_anni; i++) {
                    if (4 + i < lineaSeparata.length){
                        time[i] = Float.parseFloat(lineaSeparata[4 + i].trim());    //Inserisce i valori della tabella dentro il vettore
                    } else
                        time[i] = 0;
                }
                CasiLegali nuovoOggetto = new CasiLegali(leg_case, leg_stat, unit, geo, time);
                record.add(nuovoOggetto);         //Aggiungo nuovo oggetto alla lista
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Metodo per la creazione dei metadati
     *
     * @param fileTSV   Stringa con il nome del file tsv
     */
    private void Metadata(String fileTSV) throws IOException {
        Field[] fields = CasiLegali.class.getDeclaredFields();              //Estrae le variabili della classe NottiNazione
        BufferedReader bR = new BufferedReader(new FileReader(fileTSV));    //Apre il bufferedReader
        String linea = bR.readLine();                                       //Legge la prima riga
        linea = linea.replace(",", TAB_DELIMITER);                   //Sostituisce alla prima linea tutte le "," con "\t"
        linea = linea.replace("\\", TAB_DELIMITER);                  //Sostituisce alla prima linea \ con tab
        String[] lineaSplittata = linea.trim().split(TAB_DELIMITER);        //Separa la stringa tutte le volte che incontra "\t"
        int i = 0;

        for (Field f : fields) {
            Map<String, String> map = new HashMap<>();
            map.put("Alias", f.getName());                  //Aggiunge alla mappa la chiave alias
            map.put("SourceField", lineaSplittata[i]);      //Prende il nome del campo nel file tsv
            map.put("Type", f.getType().getSimpleName());   //Prende il tipo di dato e lo inserisce nella mappa
            Lista.add(map);                                 //Aggiunge la mappa alla lista "Lista"
            i++;
        }
    }

    /**
     * Metodo che restituisce il record
     */
    public List<CasiLegali> getData(){
        return record;
    }

    /**
     * Metodo che restituisce il record all'indice i
     *
     */

    public CasiLegali getData(int i){
        if(i < record.size()) return record.get(i);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Oggetto di indice " + i + " non esiste!");
    }
    /**
     * Metodo che restituisce una lista di String contenente gli anni
     *
     * @return anni
     */
    public List getTime(){
        return anni;
    }

    /**
     * Metodo che restituisce la lista dei metadati
     *
     * @return Lista
     */
    public List<Map> getMetadata(){
        return Lista;
    }

    /**
     * Metodo che restituisce la lista dei valori relativi ad un singolo campo
     *
     */

    private List getCampo(String nomeCampo) {
        List<Object> listField = new ArrayList<>(); //inizializzo lista che conterrà i valori del campo
        try {
            /*
            Gestisco il caso in cui il nome del campo sia un anno:
            In questo caso verifico se sia uno degli anni all'interno del vettore time
            Se questo è vero allora inserisco dentro dentro "ob" i valori relativi al nome del campo inserito
             */
            if(anni.contains(nomeCampo)){
                for(CasiLegali casi : record){
                    Object ob= casi.getTime()[Integer.parseInt(nomeCampo)-2000]; //considero solo l'elemento che mi interessa del metodo get
                    listField.add(ob);
                }
            }
            /*
            Nel caso in cui il nome del campo non sia un anno:
            Scorro tutti gli oggetti all'interno della classe record e vado ad estrarre
            i valori del campo relativo al nome del campo inserito dall'utente.
            All'interno del ciclo viene caricato l'oggetto relativo al campo
             */
            else {
                //serve per scorrere tutti gli oggetti ed estrarre i valori del campo nomeCampo
                for (CasiLegali casi : record) {
                    //costruisco il metodo get del modello di riferimento
                    Method getter = CasiLegali.class.getMethod("get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1));
                    Object value = getter.invoke(casi);       //invoco il metodo get sull'oggetto della classe modellante
                    listField.add(value);                     //aggiungo il valore alla lista
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, nomeCampo + " non esiste.");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return listField; //ritorna la lista
    }

    /**
     * Metodo che restituisce la lista di tutte le statistiche relative a tutti i campi
     *
     * @return lista delle statistiche
     */

    public List<Map> getStatistiche() {
        Field[] fields = CasiLegali.class.getDeclaredFields(); //elenco degli attributi della classe
        List<Map> list = new ArrayList<>(); //lista di mappe che conterrà le statistiche
        for(Field campo : fields){
            String NomeCampo = campo.getName(); //estrae il nome del campo corrente
            if(NomeCampo.equals("record"))
                for(int i = 0; i < CasiLegali.differenza_anni; i++)
                    list.add(Statistiche.getAllStatistics(NomeCampo, getCampo(NomeCampo)));
            else
                list.add(Statistiche.getAllStatistics(NomeCampo, getCampo(NomeCampo)));
        }
        return list;
    }

    public Map getStatistiche(String nomeCampo) {
        return Statistiche.getAllStatistics(nomeCampo, getCampo(nomeCampo));
    }
}

