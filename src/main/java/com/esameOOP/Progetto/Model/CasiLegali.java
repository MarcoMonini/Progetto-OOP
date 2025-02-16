package com.esameOOP.Progetto.Model;

import java.io.Serializable;

/*Classe modellante dataset
*/

public class CasiLegali implements Serializable {       //Serializable permette di salvare gli oggetti della classe su un file

    private String leg_case, leg_stat, unit, geo;
    private float[] time;
    public static final int differenza_anni =  9;
    /**
     * Costruttore della classe CasiLegali
     */
    public CasiLegali(String leg_case, String leg_stat, String unit, String geo, float[] time){
        this.leg_case = leg_case;
        this.leg_stat = leg_stat;
        this.unit = unit;
        this.geo = geo;
        this.time = time;
    }

    public String getLeg_case() {
        return leg_case;
    }

    public String getLeg_stat() {
        return leg_stat;
    }

    public String getUnit() {
        return unit;
    }

    public String getGeo() {
        return geo;
    }

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
