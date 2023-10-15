package presenter;

import collections.ExemplosCollection;
import dao.LerSequenciasDeExemplos;
import generalization.DiffLengthGeneralizer;
import generalization.Episode;
import generalization.Event;
import generalization.EventAdapter;
import generalization.Graph;
import generalization.SameLengthGeneralizer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tccjni.UnitexFunctions;


public class GrafosPresenter {
    
    private ArrayList<String> exemplos;
    private static ArrayList<Event>  events;
    private static ArrayList<Episode> episodes;
    private Graph graph;
    private File selectedPath;
    
    public GrafosPresenter() {
        
        events = new ArrayList<>();
        episodes = new ArrayList<>();
        
    }
    
    public void createFSTList() {
        
        exemplos = ExemplosCollection.getListaExemplos();
        
        String[] arguments = new String[0];
         
         for (int i = 0; i < exemplos.size(); i++) {
             new UnitexFunctions().configUnitex(arguments, exemplos.get(i), i);  
        }
    }
    
    public void deleteFiles() {
        
        for (int i = 0; i < exemplos.size(); i++) {
            new UnitexFunctions().deleteFolder("workUnitex" + i);    
        }
    }
    
    void imprimirEpisodios() {
        
        for(Episode episode : episodes) {
            System.out.println("\n");
            for(Event event : episode.getSequenceEvents()) {
                event.printEvent();
            }
        }
        
        System.out.println("\n\n");
    }
    
    public void constructGraph() throws Exception {
        
        createFSTList();
        
        try {
            LerSequenciasDeExemplos.lerSquencias();          
            //imprimirEpisodios();
            
            EventAdapter adapter = new EventAdapter();            
            for(Episode ep: episodes) {
               adapter.adaptEvent(ep.getSequenceEvents());
            }
            
           //imprimirEpisodios();
           
           
           SameLengthGeneralizer sameGeneralizer = new SameLengthGeneralizer();
           DiffLengthGeneralizer diffGeneralizer = new DiffLengthGeneralizer();
           if(sameGeneralizer.isSameLength(episodes)) {
               ArrayList<Map<String,Integer>> solution = sameGeneralizer.generalize(episodes);
               graph = new Graph();
               graph.constructGraph(solution);
               saveGraph();
           } else {
               ArrayList<Map<String,Integer>> solution = sameGeneralizer.generalize(diffGeneralizer.generalize(episodes));
               graph = new Graph();
               graph.constructGraph(solution);
               saveGraph();
           }
        
        } catch (IOException ex) {
            Logger.getLogger(GrafosPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        deleteFiles();
    }
    public void saveGraph() throws IOException {
        
        DirectorySelectorPresenter selector = new DirectorySelectorPresenter();
        this.selectedPath = selector.selectFolder();
        
        if(selectedPath == null){
            selector.dispose();
            selector = null;
        } else {
            File newFile;
            if(selectedPath.toString().contains(".grf")) {
                newFile = new File(selectedPath.toString());
            } else {
                newFile = new File(selectedPath.toString() + ".grf");
            }
            FileWriter newFileWriter = new FileWriter(newFile);       
            BufferedWriter bWriter = new BufferedWriter(newFileWriter);
            bWriter.write(graph.getGraph());

            bWriter.close();
            newFileWriter.close();
        }
    }

    public void runGraph(String caminhoTexto) throws IOException {
        
        String caminhoGrafo;
        if(selectedPath.toString().contains(".grf")) {
            caminhoGrafo = selectedPath.toString();
        } else {
            caminhoGrafo = selectedPath.toString() + ".grf";
        }
        String rotinaUnitex = "./src/rotinaUnitex.sh " + caminhoTexto + " " + caminhoGrafo + " Ocorrências.txt";
        //System.out.println(caminhoTexto);
        //System.out.println(caminhoGrafo);
        
        try {
            Process processo = Runtime.getRuntime().exec(rotinaUnitex);
            
            int status = processo.waitFor();
            if(status == 0) {
                System.out.println("Sucesso em rodar!");
            } else {
                System.out.println("Erro em rodar:" + status);
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void addEvent(Event eventp ){
        events.add(eventp);
    }         
    public static void addEpisode(Episode episodep){
        episodes.add(episodep);
    }
}
    