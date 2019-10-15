package com.esameOOP.Progetto.Modello;

import java.io.Serializable;

/*Classe modellante dataset
*/

public class CasiLegali implements Serializable {       //Serializable permette di salvare gli oggetti della classe su file

    public String leg_case, leg_stat, unit, geo;
    public float[] time;
    public static final int differenza_anni =  9;
    /**
     * Costruttore della classe Oggetto
     * @param leg_case
     * @param leg_stat
     * @param unit
     * @param geo
     * @array time
     */
    public CasiLegali(String leg_case, String leg_stat, String unit, String geo, float[] time){
        this.leg_case = leg_case;
        this.leg_stat = leg_stat;
        this.unit = unit;
        this.geo = geo;
        this.time = time;
    }

    /**
     * Metodo get per leg_case
     * @return
     */
    public String getLeg_case() {
        return leg_case;
    }

    /**
     * Metodo get per leg_stat
     * @return
     */
    public String getLeg_stat() {
        return leg_stat;
    }

    /**
     * Metodo get per getUnit
     * @return
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Metodo get per geo
     * @return
     */
    public String getGeo() {
        return geo;
    }

    /**
     * Metodo get per time
     * @return
     */
    public float[] getTime() {
        return time;
    }

    /**
     * Metodo toString per stampare l'oggetto
     *
     * @return Restituisce una stringa contenente il valore dei vari campi
     */
    @Override
    public String toString() {
        StringBuilder record; //oggetto di tipo StringBuilder
        record = new StringBuilder("CasiLegali: " + "Leg_case=" + leg_case + ", Leg_stat=" + leg_stat + ", geo= " + geo + ", unit=" + unit + ";");
        for(int i = 0; i< differenza_anni; i++ )
            record.append(" anno=").append(2008 + i).append(" valori =").append(time[i]).append(";");
        return record.toString();
    }
}
