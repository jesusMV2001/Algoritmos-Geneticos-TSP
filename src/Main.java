import algoritmos.Diferencial;
import algoritmos.Evolutivo;
import algoritmos.Genetico;
import procesaFichero.Configurador;
import procesaFichero.CreaFicheros;
import procesaFichero.LectorDatos;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        ArrayList<LectorDatos> datos = new ArrayList<>();
        Configurador c = new Configurador(args[0]);


        for (int i = 0; i < c.getArchivos().size(); i++) {
            datos.add(new LectorDatos(c.getArchivos().get(i)));
        }

        CreaFicheros ficheros=new CreaFicheros();

        //datos para CSV
        HashMap<String,double[][]> datosCSV = new HashMap<>();
        int filas = c.getSemillas().size();
        int cols = c.getArchivos().size() * 2;
        int numTablasGenetico=c.getPoblacion().size()*c.getKbest().size()*c.getElite().size()*c.getCruces().size(); //numero de tablas para el genetico
        int numTablas = numTablasGenetico + c.getOperadoresSeleccion().size(); //numero de tablas sumando el diferencial




        //bucles para ejecutar los algoritmos
        for (String algoritmo: c.getAlgoritmos()) {
            for (LectorDatos dato : datos) {
                    switch (algoritmo){
                        case "genetico":
                            for (int k = 0; k < c.getCruces().size(); k++)
                                for (int i = 0; i < c.getPoblacion().size(); i++)
                                    for (int j = 0; j < c.getElite().size(); j++)
                                        for (int l = 0; l < c.getKbest().size(); l++) {
                                            String key = c.getCruces().get(k)+"_"+c.getPoblacion().get(i)+"_"+c.getElite().get(j)+"_"+c.getKbest().get(l)+"_"+algoritmo;
                                            if(!datosCSV.containsKey(key))
                                                datosCSV.put(key,new double[filas][cols]);
                                            extracted(c,//configurador
                                                    new Genetico(dato.getDistancias(), c, c.getPoblacion().get(i), c.getElite().get(j), c.getKbest().get(l), c.getCruces().get(k)),//algoritmo
                                                    datos.indexOf(dato),//que archivo se va a ejecutar
                                                    c.getCruces().get(k),//que cruce se va a usar en datosCSV
                                                    ficheros,//Para crear los ficheros
                                                    key,//key de hashmap de los datos
                                                    datosCSV,//Para guardar los datos
                                                    c.getPoblacion().get(i) + "_E" + c.getElite().get(j) + "_kB" + c.getKbest().get(l) + "_" + dato.getRuta(),
                                                    algoritmo);
                                        }
                            break;
                        case "diferencial":
                            for (String seleccion: c.getOperadoresSeleccion()) {
                                String key = seleccion + "_" + algoritmo;
                                if(!datosCSV.containsKey(key))
                                    datosCSV.put(key, new double[filas][cols]);
                                extracted(c,//configurador
                                        new Diferencial(dato.getDistancias(), c, c.getPoblacionDiferencial(), seleccion),//algoritmo
                                        datos.indexOf(dato),//que archivo se va a ejecutar
                                        seleccion,//que seleccion se va a usar
                                        ficheros,//Para crear los ficheros
                                        key,//key de hashmap de los datos
                                        datosCSV,//Para guardar los datos
                                        dato.getRuta(),
                                        algoritmo);
                            }
                            break;


                }
            }
        }
        if(c.isCrearCSV())
            ficheros.crearCSV(c,datosCSV,filas,cols);

    }

    private static void extracted(Configurador config, Evolutivo a, int i, String cruce, CreaFicheros c, String k, HashMap<String,double[][]> datosCSV,String tipo, String algoritmo) {
        for (int j = 0; j < config.getSemillas().size(); j++) {

            long inicio = System.currentTimeMillis();

            System.out.println("-------------------------------------------------------------------------------");
            System.out.println(config.getSemillas().get(j)+", "+cruce+", "+tipo);
            Double costeMejorSolucion = a.ejecutar(Long.parseLong( config.getSemillas().get(j))).getFitness();
            System.out.println( costeMejorSolucion);

            long fin = System.currentTimeMillis();
            long duracion = fin - inicio;

            int col=i*2;
            datosCSV.get(k)[j][col]=costeMejorSolucion;
            datosCSV.get(k)[j][col+1]=duracion;
            if (config.isCrearLogs())
                c.fichero(a.logs, algoritmo+"_"+config.getSemillas().get(j)+"_"+cruce+"_"+tipo, config);

            a.limpiar();
        }
    }

}