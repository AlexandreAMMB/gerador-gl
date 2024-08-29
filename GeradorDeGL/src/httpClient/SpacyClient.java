package httpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SpacyClient {
    
    private HashMap<String, String> mapaSpacyUnitex = createMap();

    public ArrayList<String> POStagThis(String text) {
        try {
            URL url = new URL("http://localhost:5000/pos_tagging");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"text\": \"" + text + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                response.deleteCharAt(0);
                response.deleteCharAt(response.length() - 1);
                
                String responseString = response.toString();
                ArrayList<String> tags = new ArrayList();
                tags.addAll(Arrays.asList(responseString.split(", ")));
                tags = makeRelation(tags);
                
                return tags;
            }   
        } catch(IOException ex) {
            System.out.println(ex);
        }
        /*try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {;
            String responseBody = scanner.useDelimiter("\\A").next();
            System.out.println("POS Tags: " + responseBody);
        }*/
        return null;
    }
    
    private HashMap<String, String> createMap(){
        HashMap<String, String> mapaSpacyUnitex = new HashMap();
        mapaSpacyUnitex.put("ADJ","A");
        mapaSpacyUnitex.put("ADV", "ADV");
        mapaSpacyUnitex.put("CCONJ", "CONJC");
        mapaSpacyUnitex.put("SCONJ", "CONJS");
        mapaSpacyUnitex.put("DET", "DET");
        mapaSpacyUnitex.put("INTJ", "INTJ");
        mapaSpacyUnitex.put("NOUN", "N");
        mapaSpacyUnitex.put("ADP", "PREP");
        mapaSpacyUnitex.put("PRON", "PRO");
        mapaSpacyUnitex.put("VERB", "V");
        mapaSpacyUnitex.put("AUX", "V");
        mapaSpacyUnitex.put("PROPN", "N+Pr");
        mapaSpacyUnitex.put("NUM", "Num");
        return mapaSpacyUnitex;
    }
    
    private ArrayList<String> makeRelation(ArrayList<String> tags){
        ArrayList<String> newTags = new ArrayList();
        for(String tag : tags){
            String aux = mapaSpacyUnitex.get(tag);
            newTags.add(aux);
        }
        return newTags;
    }
}
