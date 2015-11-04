import java.net.InetAddress;

public class Player {
    private String move;
    private String name;

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    private int totalScore;
    private int score;
    private InetAddress ip_address;
    private int port;

    public void setMove(String move) {
        this.move = move;
    }

    public void setScore(int score) {
        this.totalScore += score;
        this.score = score;
    }

    public String getMove() {
        return move;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getScore() {
        return score;
    }

    public InetAddress getIp_address() {
        return ip_address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public Player(String name, InetAddress ip_address, int port){
        this.name = name;
        this.ip_address = ip_address;
        this.port = port;
        totalScore = 0;
        score = 0;
        move = "";
    }

    @Override
    public String toString(){
        return name + " " + ip_address.getHostAddress() + " " + port + " " + score + " " + totalScore;
    }
}
