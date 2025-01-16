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
import javax.swing.JOptionPane;
import tccjni.UnitexFunctions;
import view.TelaPrincipalView;


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
    
    public boolean constructGraph() throws Exception {
        
        createFSTList();
        
        boolean retorno = true;
        try {
            LerSequenciasDeExemplos.lerSquencias();          
            imprimirEpisodios();
            
            int countUnrecognizedCharacters = 0;
            ArrayList<String> unrecognizedCharacters = new ArrayList();
            
            EventAdapter adapter = new EventAdapter();            
            for(Episode ep: episodes) {
               adapter.adaptEvent(ep.getSequenceEvents());
               for(Event event : ep.getSequenceEvents()) {
                   if(event.getLemma() == null || event.getGrammaticalCodes() == null || event.getInflections() == null) {
                       unrecognizedCharacters.add(event.getEventType());
                       countUnrecognizedCharacters++;
                   }
               }
            }
            
            if(countUnrecognizedCharacters > 0) {
                String beginMessage = "", message = "", endMessage = "";
                if(countUnrecognizedCharacters == 1) {
                    beginMessage = "O caracter: ";
                    endMessage = " não é reconhecido no momento!\n Por favor, escolha exemplos sem caracteres parecidos.";
                } else {
                    beginMessage = "Os caracteres: ";
                    endMessage = " não são reconhecidos no momento!\n Por favor, escolha exemplos sem caracteres parecidos.";
                }
                
                for(String character : unrecognizedCharacters) {
                    message += "'" + character + "', ";
                }
                message = message.substring(0, message.length() - 2);
                JOptionPane.showMessageDialog(null, beginMessage + message + endMessage, "Caracteres não reconhecidos", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            imprimirEpisodios();
            
            ArrayList<ArrayList<String>> listTags = new ArrayList();
            ArrayList<String> tags = new ArrayList();
            
            for(String exemplo : exemplos) {
                tags = spacyClient.POStagThis(exemplo);
                listTags.add(tags);
            }
            
            int countEpisode = 0, countEvent = 0, countCode = 0;
            for(Episode episode: episodes) {
                for(Event event : episode.getSequenceEvents()){
                    if(!listTags.get(countEpisode).get(countEvent).equals("??")) {
                        ArrayList<String> eventTypes = new ArrayList();
                        ArrayList<String> inflections = new ArrayList();
                        ArrayList<String> grammaticalCodes = new ArrayList();
                        ArrayList<String> lemmas = new ArrayList();

                        String eventType = event.getEventType();
                        String inflection = event.getInflections();
                        String grammaticalCode = event.getGrammaticalCodes();
                        String lemma = event.getLemma();

                        eventTypes.addAll(Arrays.asList(eventType.split("\\}\\|\\{")));
                        inflections.addAll(Arrays.asList(inflection.split("\\|")));
                        grammaticalCodes.addAll(Arrays.asList(grammaticalCode.split("\\|")));
                        lemmas.addAll(Arrays.asList(lemma.split("\\|")));

                        String eventType_new = "", inflection_new = "", grammaticalCode_new = "", lemma_new = "";
                        
                        for(String code : grammaticalCodes){
                            if(code.contains(listTags.get(countEpisode).get(countEvent))) {
                                eventType_new = eventType_new + eventTypes.get(countCode) + "}|{";
                                inflection_new = inflection_new + inflections.get(countCode) + "|";
                                grammaticalCode_new = grammaticalCode_new + grammaticalCodes.get(countCode) + "|";
                                lemma_new = lemma_new + lemmas.get(countCode) + "|";
                            }
                            countCode++;
                        }
                        countCode = 0;
                        
                        if(eventType_new.length() != 0 && inflection_new.length() != 0 && grammaticalCode_new.length() != 0 && lemma_new.length() != 0) {
                            event.setEventType(eventType_new.substring(0, eventType_new.length() - 3));
                            event.setInflections(inflection_new.substring(0, inflection_new.length() - 1));
                            event.setGrammaticalCodes(grammaticalCode_new.substring(0, grammaticalCode_new.length() - 1));
                            event.setLemma(lemma_new.substring(0, lemma_new.length() - 1));
                        }
                    }
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
                retorno = saveGraph();
            } else {
                ArrayList<Map<String,Integer>> solution = sameGeneralizer.generalize(diffGeneralizer.generalize(episodes));
                graph = new Graph();
                graph.constructGraph(solution);
                retorno = saveGraph();
            }
        
        } catch (IOException ex) {
            Logger.getLogger(GrafosPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        deleteFiles();
        return retorno;
    }
    
    public boolean saveGraph() throws IOException {
        
        DirectorySelectorPresenter selector = new DirectorySelectorPresenter();
        this.selectedPath = selector.selectFolder();
        
        if(selectedPath == null){
            selector.dispose();
            selector = null;
            return false;
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
            return true;
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
    