package generalization;

import java.util.ArrayList;


public class Episode {
    
    private ArrayList<Event> sequenceEvents;
    private int tamanho;
    
    public Episode(){
        this.sequenceEvents= new ArrayList<>();
    }
      
    public void addSequenceEvents(Event event) {
        sequenceEvents.add(event);
    }

    public ArrayList<Event> getSequenceEvents() {
        return sequenceEvents;
    } 

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }
    
}
