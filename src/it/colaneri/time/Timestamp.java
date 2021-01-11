package it.colaneri.time;

import java.lang.annotation.Inherited;
import java.text.SimpleDateFormat;

public class Timestamp {

    /**
     * Formato di default
     */
    public static final String DEFAULT_FORMAT="yyyyMMddHHmmssSSS";

    private String timestamp;

    /**
     * Genera il timestamp corrente usando il formato di default
     */
    public Timestamp(){
        this(DEFAULT_FORMAT);
    }

    /**
     * Genera il timestamp corrente usando il formato specificato (vedi SimpleDateFormat)
     * @param timestampFormat il formato da usare
     */
    public Timestamp(String timestampFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
        timestamp = sdf.format(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    @Override
    public String toString() {
        return timestamp;
    }
}
