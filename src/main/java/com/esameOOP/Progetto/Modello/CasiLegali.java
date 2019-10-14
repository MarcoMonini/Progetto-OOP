package com.esameOOP.Progetto.Modello;

public class CasiLegali {
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
     * Metodi get
     */
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

    /**
     * Metodo toString per stampare l'oggetto
     *
     * @return Restituisce una stringa contenente il valore dei vari campi
     */
    @Override
    public String toString() {
        StringBuilder record; //oggetto di tipo StringBuilder
        record = new StringBuilder("Oggetto: " + "Leg_case=" + leg_case + ", Leg_stat=" + leg_stat + ", geo= " + geo + ", unit=" + unit + ";");
        for(int i = 0; i< differenza_anni; i++ )
            record.append(" anno=").append(2008 + i).append(" valori =").append(time[i]).append(";");
        record.append('}');//ciclo per accodare i valori annuali alla stringa di ritorno
        return record.toString();
    }
}
