import java.util.HashMap;

public class Round {
    volatile HashMap<String, String> moves = new HashMap<>();

    private volatile int pCounter = 0;
    private volatile int rCounter = 0;
    private volatile int sCounter = 0;


    synchronized public boolean isRoundFinished(){
        return Server.players.size() == moves.size();
    }

    synchronized public void addMove(String name, String choice){
        System.out.println("choice was : " + choice);
        if(! (choice.equals("Paper") || choice.equals("Scissors") || choice.equals("Rock"))) {
            throw new IllegalArgumentException("Invalid choice");
        }
        if(!moves.containsKey(name)){
            moves.put(name, choice);
            if(choice.equals("Paper")) pCounter ++;
            else if(choice.equals("Scissors")) sCounter ++;
            else rCounter ++;
        }
    }

    public HashMap<String, Integer> getResults(){
        HashMap<String, Integer> results = new HashMap<>();

        for(String name: moves.keySet()) {
            if (moves.get(name).equals("Paper"))
                results.put(name, rCounter);
            else if(moves.get(name).equals("Scissors"))
                results.put(name, pCounter);
            else if(moves.get(name).equals("Rock"))
                results.put(name, sCounter);
            else
                throw new IllegalArgumentException("Choice not valid");

        }
        return results;
    }
}
