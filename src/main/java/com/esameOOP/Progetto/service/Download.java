package com.esameOOP.Progetto.service;

import com.esameOOP.Progetto.Modello.CasiLegali;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
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
    private static List<CasiLegali> record = new ArrayList<>();        //Lista di oggetti NottiNazione
    private final static String TAB_DELIMITER = "\t";
    private static List<Map> Lista = new ArrayList();                    //Lista per i Metadata

    /**
     * Costruttore della classe Download
     *
     * @throws IOException
     */
    public Download() throws IOException {
        String fileTSV = "dataset.tsv";
        if (Files.exists(Paths.get(fileTSV)))
            System.out.println("Dataset ricaricato da locale");
        else {
            String url = "http://data.europa.eu/euodp/data/api/3/action/package_show?id=CLfXgIIz02XfA2MTjWgjSQ";
            try {
                URLConnection openConnection = new URL(url).openConnection();
                openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                InputStream in = openConnection.getInputStream();

                String data = "";
                String line = "";
                try {
                    InputStreamReader inR = new InputStreamReader( in );
                    BufferedReader buf = new BufferedReader( inR );

                    while ( ( line = buf.readLine() ) != null ) {
                        data += line;
                        System.out.println( line );
                    }
                } finally {
                    in.close();
                }
                //Conversione StringBuilder in oggetto JSON
                JSONObject obj = (JSONObject) JSONValue.parseWithException(data.toString());
                JSONObject objI = (JSONObject) (obj.get("result"));
                JSONArray objA = (JSONArray) (objI.get("resources"));

                for (Object o : objA) {
                    if (o instanceof JSONObject) {
                        JSONObject o1 = (JSONObject) o;
                        String format = (String) o1.get("format");
                        String urlD = (String) o1.get("url");
                        System.out.println(format + " | " + urlD);
                        if (format.toLowerCase().contains("tsv")) {
                            //downloadTSV(urlD, fileName);
                            downloadTSV(urlD, fileTSV);
                        }
                    }
                }
                System.out.println("OK");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Parsing(fileTSV);
        Metadata(fileTSV);
    }

    /**
     * Metodo che gestisce un problema di redirect del sito che gestisce i dati
     *
     * @param url url del sito dal quale scaricare il file
     * @param fileName nome del file
     * @throws Exception
     */
    public static void downloadTSV(String url, String fileName) throws Exception {
        HttpURLConnection openConnection = (HttpURLConnection) new URL(url).openConnection();
        openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        InputStream in = openConnection.getInputStream();
        String data = "";
        String line = "";
        try {
            if(openConnection.getResponseCode() >= 300 && openConnection.getResponseCode() < 400) {
                downloadTSV(openConnection.getHeaderField("Location"),fileName);
                in.close();
                openConnection.disconnect();
                return;
            }
            Files.copy(in, Paths.get(fileName));
            System.out.println("File size " + Files.size(Paths.get(fileName)));
        } finally {
            in.close();
        }
    }

    /**
     * Metodo che effettua il parsing del file tsv
     *
     * @param fileTSV  Stringa con il nome del file tsv
     */
    private void Parsing(String fileTSV){
        try(BufferedReader bRead = new BufferedReader(new FileReader(fileTSV))){
            bRead.readLine();                  //Legge una riga a vuoto per saltare l'intestazione
            String linea;
            int a;
            while((linea = bRead.readLine()) != null) {                 //Ciclo che continua fintanto che non trova una linea nulla
                linea = linea.replace(",", TAB_DELIMITER);       //Sostituisce le virgole con i tab "\t"
                linea = linea.replace(":","0");      //Sostituisce i ":" con "0"
                linea = linea.replace("e","");       //Sostituisce "e" con ""
                linea = linea.replace("c", "");      //Sostituisce "c" con ""
                linea = linea.replace("u", "");      //Sostituisce "u" con ""
                linea = linea.replace("b","");
                String[] lineaSplittata = linea.trim().split(TAB_DELIMITER);             //Separa la linea tutte le volte che incontra il tab
                String c_resid = lineaSplittata[0].trim();                                     //Trim toglie gli spazi prima e dopo la stringa
                String unit = lineaSplittata[1].trim();
                String nace_r2 = lineaSplittata[2].trim();
                String geo = lineaSplittata[3].trim();
                float[] time = new float[CasiLegali.differenza_anni];
                for(int i = 0; i < CasiLegali.differenza_anni; i++) {
                    if (4 + i < lineaSplittata.length)                               //Gestione errore java.lang.ArrayIndexOutOfBoundsException
                        time[i] = Float.parseFloat(lineaSplittata[4 + i].trim());       //Inserisce i time della tabella dentro il vettore
                    else
                        time[i] = 0;                                                      //Per i time che non ci sono dopo lineaSplittata aggiunge "0"
                }
                CasiLegali oggettoParsato = new CasiLegali(c_resid, unit, nace_r2, geo, time);
                record.add(oggettoParsato);         //Aggiungo oggettoParsato alla lista
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Metodo per la creazione dei metadati
     *
     * @param fileTSV   Stringa con il nome del file tsv
     * @throws IOException
     */
    private void Metadata(String fileTSV) throws IOException {
        Field[] fields = CasiLegali.class.getDeclaredFields();    //Estrae le variabili della classe NottiNazione
        BufferedReader bR = new BufferedReader(new FileReader(fileTSV));         //Apre il bufferedReader
        String linea = bR.readLine();           //Legge la prima riga
        linea = linea.replace(",", TAB_DELIMITER);     //Sostituisce alla prima linea tutte le "," con "\t"
        linea = linea.replace("\\", TAB_DELIMITER);     //Sostituisce alla prima linea \ con tab
        String[] lineaSplittata = linea.trim().split(TAB_DELIMITER);     //Separa la stringa tutte le volte che incontra "\t"
        int i = 0;

        for (Field f : fields) {
            Map<String, String> map = new HashMap<>();
            map.put("Alias", f.getName());      //Aggiunge alla mappa la chiave alias
            map.put("sourceField", lineaSplittata[i]);    //Prende il nome del campo nel file tsv
            map.put("type", f.getType().getSimpleName());   //Prende il tipo di dato e lo inserisce nella mappa
            Lista.add(map);             //Aggiunge la mappa alla lista "Lista"
            i++;
        }
    }

    /**
     * Metodo che restituisce il record
     *
     * @return record
     */
    public List<CasiLegali> getRecord(){
        return record;
    }

    /**
     * Metodo che restituisce una lista di String contenente gli anni
     *
     * @return anni
     */
    public List getAnni(){
        List<String> anni = new ArrayList<>();
        for(int i = 0; i < CasiLegali.differenza_anni; i++)
            anni.add(Integer.toString(2007+i));
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
     * Metodo che restituisce il record all'indice i
     *
     * @param i
     * @return
     */
    public CasiLegali getRecord(int i){
        if(i < record.size()) return record.get(i);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Oggetto di indice " + i + " non esiste!");
    }

    /*public List<Map> getStatistics(){
        Field[] fields = NottiNazione.class.getDeclaredFields();
        List<Map> listStats = new ArrayList<>();
        for(Field f : fields){
            String fieldName = f.getName();
            if(fieldName.equals("valori"))
                for(int i = 0; i < NottiNazione.differenza_anni; i++)
                    listStats.add(getStatistics(Integer.toString(2007+i)));
                else
                    listStats.add(getStatistics(fieldName));
        }
    return listStats;
    }*/
}
