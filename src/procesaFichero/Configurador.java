package procesaFichero;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Configurador {

    private final ArrayList<String> archivos;
    private final ArrayList<String> algoritmos;
    private final ArrayList<String> semillas;
    private Integer iteraciones;
    private Integer tenenciaTabu;
    private double oscilacion;
    private double estancamiento;
    private boolean crearLogs,reemplazarLogs,crearCSV;

    public Configurador(String ruta) {
        this.archivos = new ArrayList<>();
        this.algoritmos = new ArrayList<>();
        this.semillas = new ArrayList<>();
        String linea;
        FileReader f;
        try{
            f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);
            while ((linea=b.readLine())!=null){
                String[] split = linea.split("=");
                switch (split[0]){
                    case "Archivos":
                        String[] v = split[1].split(" ");
                        archivos.addAll(Arrays.asList(v));
                        break;
                    case "Semillas":
                        String[] vsemillas = split[1].split(" ");
                        semillas.addAll(Arrays.asList(vsemillas));
                        break;
                    case "Algoritmos":
                        String[] valgoritmos = split[1].split(" ");
                        algoritmos.addAll(Arrays.asList(valgoritmos));
                        break;
                    case "Iteraciones":
                        iteraciones = Integer.parseInt(split[1]);
                        break;
                    case "TenenciaTabu":
                        tenenciaTabu = Integer.parseInt(split[1]);
                        break;
                    case "Oscilacion":
                        oscilacion = Double.parseDouble(split[1]);
                        break;
                    case "Estancamiento":
                        estancamiento = Double.parseDouble(split[1]);
                        break;
                    case "CrearLogs":
                        crearLogs = Boolean.parseBoolean(split[1]);
                        break;
                    case "ReemplazarLogs":
                        reemplazarLogs = Boolean.parseBoolean(split[1]);
                        break;
                    case "CrearCSV":
                        crearCSV = Boolean.parseBoolean(split[1]);
                        break;
                }
            }

        }catch (IOException ignored){
        }
    }

    public ArrayList<String> getArchivos() {
        return archivos;
    }

    public ArrayList<String> getAlgoritmos() {
        return algoritmos;
    }

    public ArrayList<String> getSemillas() {
        return semillas;
    }

    public Integer getIteraciones() {
        return iteraciones;
    }

    public Integer getTenenciaTabu() {
        return tenenciaTabu;
    }

    public double getOscilacion() {
        return oscilacion;
    }

    public double getEstancamiento() {
        return estancamiento;
    }

    public boolean isCrearLogs() {
        return crearLogs;
    }

    public boolean isReemplazarLogs() {
        return reemplazarLogs;
    }

    public boolean isCrearCSV() {
        return crearCSV;
    }
}
