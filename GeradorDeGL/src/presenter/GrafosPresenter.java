package presenter;

import collections.ExemplosCollection;
import dao.LerSequenciasDeExemplos;
import generalization.DiffLengthGeneralizer;
import generalization.Episode;
import generalization.Event;
import generalization.EventAdapter;
import generalization.Graph;
import generalization.SameLengthGeneralizer;
import httpClient.SpacyClient;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
//import opennlp.tools.postag.POSModel;
//import opennlp.tools.postag.POSTaggerME;
//import opennlp.tools.tokenize.SimpleTokenizer;
import tccjni.UnitexFunctions;


public class GrafosPresenter {
    
    private ArrayList<String> exemplos;
    private static ArrayList<Event> events;
    private static ArrayList<Episode> episodes;
    private Graph graph;
    private File selectedPath;
    private SpacyClient spacyClient;
    
    public GrafosPresenter() {
        
        events = new ArrayList<>();
        episodes = new ArrayList<>();
        spacyClient = new SpacyClient();
        
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
        /*
        String modelPath = "./src/models/pt-pos-perceptron.bin";
        try (FileInputStream modelStream = new FileInputStream(modelPath)) {
            POSModel model = new POSModel(modelStream);
            POSTaggerME posTagger = new POSTaggerME(model);
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            
            ArrayList<String[]> tokensList = new ArrayList();
            for (String exemplo : this.exemplos) {
                tokensList.add(tokenizer.tokenize(exemplo));
            }
            
            ArrayList<String[]> tags = new ArrayList();
            for (String[] tokens : tokensList) {
                tags.add(posTagger.tag(tokens));
            }

            for (String[] tokens : tokensList) {
                int cont = 0;
                for (int i = 0; i < tokens.length; i++) {
                    System.out.println(tokens[i] + " - " + tags.get(cont)[i]);
                }
                System.out.println();
            }
                    
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
        try {
            LerSequenciasDeExemplos.lerSquencias();          
            //imprimirEpisodios();
            
            EventAdapter adapter = new EventAdapter();            
            for(Episode ep: episodes) {
               adapter.adaptEvent(ep.getSequenceEvents());
            }
            
            imprimirEpisodios();
            
            ArrayList<ArrayList<String>> listTags = new ArrayList();
            ArrayList<String> tags = new ArrayList();
            
            for(String exemplo : exemplos){
                tags = spacyClient.POStagThis(exemplo);
                listTags.add(tags);
            }
            int countEpisode = 0, countEvent = 0;
            for(Episode episode: episodes) {
                
                for(Event event : episode.getSequenceEvents()){
                    ArrayList<String> eventTypes = new ArrayList();
                    ArrayList<String> inflections = new ArrayList();
                    ArrayList<String> grammaticalCodes = new ArrayList();
                    ArrayList<String> lemmas = new ArrayList();
                    
                    String eventType = event.getEventType();
                    String inflection = event.getInflections();
                    String grammaticalCode = event.getGrammaticalCodes();
                    String lemma = event.getLemma();
                    
                    eventTypes.addAll(Arrays.asList(eventType.split("|")));
                    System.out.println("eventTypes" + eventTypes);
                    inflections.addAll(Arrays.asList(inflection.split("|")));
                    System.out.println(inflections);
                    grammaticalCodes.addAll(Arrays.asList(grammaticalCode.split("|")));
                    System.out.println(grammaticalCodes);
                    lemmas.addAll(Arrays.asList(lemma.split("|")));
                    System.out.println(lemmas);
                    
                    String eventType_new = "", inflection_new = "", grammaticalCode_new = "", lemma_new = "";
                    
                    for(String code : grammaticalCodes){
                        if(code.equals(listTags.get(countEpisode).get(countEvent))){
                            eventType_new += eventTypes.get(countEvent) + "|";
                            inflection_new += inflections.get(countEvent) + "|";
                            grammaticalCode_new += grammaticalCodes.get(countEvent) + "|";
                            lemma_new += lemmas.get(countEvent) + "|";
                        }
                    }
                    eventType_new = eventType_new.substring(0, eventType_new.length() - 1);
                    inflection_new = inflection_new.substring(0, inflection_new.length() - 1);
                    grammaticalCode_new = grammaticalCode_new.substring(0, grammaticalCode_new.length() - 1);
                    lemma_new = lemma_new.substring(0, lemma_new.length() - 1);
                    
                    event.setEventType(eventType_new);
                    event.setInflections(inflection_new);
                    event.setGrammaticalCodes(grammaticalCode_new);
                    event.setLemma(lemma_new);
                    countEvent++;
                }
                countEvent = 0;
                countEpisode++;
            }
            
            imprimirEpisodios();
           
            SameLengthGeneralizer sameGeneralizer = new SameLengthGeneralizer();
            DiffLengthGeneralizer diffGeneralizer = new DiffLengthGeneralizer();
            if(sameGeneralizer.isSameLength(episodes)) {
                ArrayList<Map<String, Integer>> solution = sameGeneralizer.generalize(episodes);
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
        
        String caminhoGrafo, nomeGrafo;
        int cont = -1;
        if(selectedPath.toString().contains(".grf")) {
            caminhoGrafo = selectedPath.toString();
        } else {
            caminhoGrafo = selectedPath.toString() + ".grf";
        }
        
        for(int i = caminhoGrafo.length() - 1; i > 0; i--) {
            if(caminhoGrafo.charAt(i) == '/') {
                cont = i + 1;
                break;
            }
        }
        
        nomeGrafo = caminhoGrafo.substring(cont).replace(".grf", "");
        
        String rotinaUnitex = "./src/rotinaUnitex.sh " + caminhoTexto + " " + caminhoGrafo + " " + nomeGrafo + " Ocorrências";
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
    