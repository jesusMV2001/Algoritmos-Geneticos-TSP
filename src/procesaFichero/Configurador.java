package procesaFichero;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configurador {

    private final ArrayList<String> archivos;
    private final ArrayList<String> algoritmos;
    private final ArrayList<String> semillas;
    private final ArrayList<String> cruces;
    private Integer greedy,limiteSegundos,evaluaciones;
    private ArrayList<Integer> poblacion,elite,kbest;
    private double generacionAleatoria,probCruce,probSeleccionOX2,probMutacion;
    private boolean crearLogs,reemplazarLogs,crearCSV;

    public Configurador(String ruta) {
        this.cruces=new ArrayList<>();
        this.kbest=new ArrayList<>();
        this.archivos = new ArrayList<>();
        this.algoritmos = new ArrayList<>();
        this.semillas = new ArrayList<>();
        this.elite = new ArrayList<>();
        this.poblacion = new ArrayList<>();
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
                    case "Cruces":
                        String[] c = split[1].split(" ");
                        cruces.addAll(Arrays.asList(c));
                        break;
                    case "Semillas":
                        String[] vsemillas = split[1].split(" ");
                        semillas.addAll(Arrays.asList(vsemillas));
                        break;
                    case "Algoritmos":
                        String[] valgoritmos = split[1].split(" ");
                        algoritmos.addAll(Arrays.asList(valgoritmos));
                        break;
                    case "Greedy":
                        greedy = Integer.parseInt(split[1]);
                        break;
                    case "Evaluaciones":
                        evaluaciones = Integer.parseInt(split[1]);
                        break;
                    case "LimiteSegundos":
                        limiteSegundos = Integer.parseInt(split[1]);
                        break;
                    case "Poblacion":
                        String[] vpob = split[1].split(" ");
                        Integer[] vpoblacion = new Integer[vpob.length];
                        for (int i = 0; i <vpoblacion.length; i++) {
                            vpoblacion[i] = Integer.parseInt(vpob[i]);
                        }
                        poblacion.addAll(List.of(vpoblacion));
                        break;
                    case "Elite":
                        String[] ve = split[1].split(" ");
                        Integer[] velite = new Integer[ve.length];
                        for (int i = 0; i <velite.length; i++) {
                            velite[i] = Integer.parseInt(ve[i]);
                        }
                        elite.addAll(List.of(velite));
                        break;
                    case "KBest":
                        String[] vkb = split[1].split(" ");
                        Integer[] vkbest = new Integer[vkb.length];
                        for (int i = 0; i <vkbest.length; i++) {
                            vkbest[i] = Integer.parseInt(vkb[i]);
                        }
                        kbest.addAll(List.of(vkbest));
                        break;
                    case "ProbSeleccionOX2":
                        probSeleccionOX2 = Double.parseDouble(split[1]);
                        break;
                    case "GeneracionAleatoria":
                        generacionAleatoria = Double.parseDouble(split[1]);
                        break;
                    case "ProbCruce":
                        probCruce = Double.parseDouble(split[1]);
                        break;
                    case "ProbMutacion":
                        probMutacion = Double.parseDouble(split[1]);
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

    public Integer getGreedy() {
        return greedy;
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
    public double getGeneracionAleatoria() {
        return generacionAleatoria;
    }

    public ArrayList<Integer> getElite() {
        return elite;
    }

    public ArrayList<Integer> getKbest() {
        return kbest;
    }

    public Integer getLimiteSegundos() {
        return limiteSegundos;
    }

    public Integer getEvaluaciones() {
        return evaluaciones;
    }

    public double getProbCruce() {
        return probCruce;
    }

    public ArrayList<String> getCruces() {
        return cruces;
    }

    public double getProbSeleccionOX2() {
        return probSeleccionOX2;
    }

    public double getProbMutacion() {
        return probMutacion;
    }

    public ArrayList<Integer> getPoblacion() {
        return poblacion;
    }
}
