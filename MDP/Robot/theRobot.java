
import javafx.geometry.Pos;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.util.*;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot<struct> extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }

    final class Position {
        public int x;
        public int y;
        public int action;
        public Position(int x, int y, int action){
            this.x = x;
            this.y = y;
            this.action = action;
        }
    }

    List<Position> getNeighbors(int i, int j){
        List<Position> neighbors = new ArrayList<>();
        if(j != 0) {
            neighbors.add(new Position(i, j-1, NORTH));
        }
        if(j != mundo.height - 1) {
            neighbors.add(new Position(i, j+1, SOUTH));
        }
        if(i != mundo.width - 1){
            neighbors.add(new Position(i+1, j, EAST));
        }
        if(i != 0) {
            neighbors.add(new Position(i-1, j, WEST));
        }

        return neighbors;
    }
    

    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        // your code
        transitionModel(action);
        sensorModel(sonars);
        normalize();

        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }

    void transitionModel(int action){
        double incorrectMoveProb = (1 - moveProb)/4;
        double[][] transitionProb = new double[probs.length][probs[0].length];

        for(int i = 1; i < mundo.height; i++){
            for(int j = 1; j < mundo.width; j++){
                List<Position> neighbors = getNeighbors(i, j);
                neighbors.add(new Position(i, j, STAY));

                for(Position pos : neighbors){
                    if(mundo.grid[pos.x][pos.y] == 1 && pos.action == action){
                        transitionProb[i][j] += probs[i][j] * moveProb;
                    }
                    else if(mundo.grid[pos.x][pos.y] == 1){
                        transitionProb[i][j] += probs[i][j] * incorrectMoveProb;
                    }
                    else if (pos.action == action) {
                        transitionProb[pos.x][pos.y] += probs[i][j] * moveProb;
                    }
                    else {
                        transitionProb[pos.x][pos.y] += probs[i][j] * incorrectMoveProb;
                    }
                }
            }
        }
        probs = transitionProb;
    }

    void sensorModel(String sonars){
        double sensorInaccuracy = 1 - sensorAccuracy;

        for(int i = 1; i < mundo.height; i++) {
            for (int j = 1; j < mundo.width; j++) {
                if (mundo.grid[i][j] != 0) {
                    probs[i][j] = 0.0;
                    continue;
                }

                String correctSensor = correctSensor(i, j);
                double currentProb = probs[i][j];

                for(int q = 0; q < correctSensor.length(); q++){
                    if(correctSensor.charAt(q) == sonars.charAt(q)){
                        currentProb = currentProb * sensorAccuracy;
                    }
                    else {
                        currentProb = currentProb * sensorInaccuracy;
                    }
                }
                probs[i][j] = currentProb;
            }
        }
    }

    String correctSensor(int i, int j){
        StringBuilder sb = new StringBuilder();
        List<Position> neighbors = getNeighbors(i, j);

        for(Position pos: neighbors){
            switch (pos.action) {
                case NORTH:
                    if(mundo.grid[pos.x][pos.y] == 1){
                        sb.append("1");
                    }
                    else {
                        sb.append('0');
                    }
                    break;
                case SOUTH:
                    if(mundo.grid[pos.x][pos.y] == 1){
                        sb.append("1");
                    }
                    else {
                        sb.append('0');
                    }
                    break;
                case EAST:
                    if(mundo.grid[pos.x][pos.y] == 1){
                        sb.append("1");
                    }
                    else {
                        sb.append('0');
                    }
                    break;
                case WEST:
                    if(mundo.grid[pos.x][pos.y] == 1){
                        sb.append("1");
                    }
                    else {
                        sb.append('0');
                    }
                    break;
            }
        }

        return sb.toString();
    }

    void normalize(){
        double sum = 0.0;

        for(int i = 1; i < mundo.height; i++) {
            for (int j = 1; j < mundo.width; j++) {
                if (mundo.grid[i][j] == 1) {
                    continue;
                }
                sum += probs[i][j];
            }
        }
        
        for(int i = 1; i < mundo.height; i++) {
            for (int j = 1; j < mundo.width; j++) {
                if (mundo.grid[i][j] == 1) {
                    continue;
                }
                probs[i][j] = probs[i][j]/sum;
            }
        }
    }

    void intializeValueIteration() {
        Vs = new double[mundo.width][mundo.height];
        for(int i = 1; i < mundo.height; i++) {
            for (int j = 1; j < mundo.width; j++) {
                if (mundo.grid[i][j] == 0) {
                    Vs[i][j] = 0;
                }
                else if (mundo.grid[i][j] == 1) {
                    Vs[i][j] = 0;
                }
                else if (mundo.grid[i][j] == 2) {
                    Vs[i][j] = -1000;
                }
                else {
                    Vs[i][j] = 100000;
                }
            }
        }
    }

    double getValueWithAction(int i, int j, List<Position> neighbors, int action) {
        double sum = 0;
        double incorrectMoveProb = (1 - moveProb)/4;

        for(Position pos : neighbors){
            if(mundo.grid[pos.x][pos.y] == 1 && pos.action == action){
                sum += Vs[i][j] * moveProb;
            }
            else if(mundo.grid[pos.x][pos.y] == 1){
                sum += Vs[i][j] * incorrectMoveProb;
            }
            else if (pos.action == action) {
                sum += Vs[pos.x][pos.y] * moveProb;
            }
            else {
                sum += Vs[pos.x][pos.y] * incorrectMoveProb;
            }
        }
        return sum;
    }

    void valueIteration() {
        double delta = 0.001;
        double discount = 0.9;
        double currentChange = 0.0;
        intializeValueIteration();

        do {
            currentChange = 0.0;

            double[][] valueCopy = new double[mundo.width][mundo.height];
            for (int i = 1; i < mundo.height; i++) {
                for (int j = 1; j < mundo.width; j++) {
                    if (mundo.grid[i][j] == 1 || mundo.grid[i][j] == 2 || mundo.grid[i][j] == 3) {   //Don't change values for goal and walls
                        valueCopy[i][j] = Vs[i][j];
                        continue;
                    }
                    List<Position> neighbors = getNeighbors(i, j);
                    neighbors.add(new Position(i, j, STAY));
                    double sum = 0;

                    for (int a = 0; a < 5; a++) {
                        sum = Math.max(sum, getValueWithAction(i, j, neighbors, a));
                    }
                    double newValue = (discount * sum);

                    currentChange = Math.max(currentChange, Math.abs(Vs[i][j] - newValue));

                    valueCopy[i][j] = newValue;
                }
            }
            Vs = valueCopy;

        } while(currentChange > delta);
    }
    
    // This is the function you'd need to write to make the robot move using your AI;

    int automaticAction() {
        HashMap<Integer, Double> actions = new HashMap<>();
        actions.put(NORTH, 0.0);
        actions.put(SOUTH, 0.0);
        actions.put(EAST, 0.0);
        actions.put(WEST, 0.0);
        actions.put(STAY, 0.0);

        for(int i = 1; i < mundo.height; i++) {
            for (int j = 1; j < mundo.width; j++) {
                List<Position> neighbors = getNeighbors(i, j);
                double bestValue = Double.MIN_VALUE;
                Position bestAction = new Position(i, j, STAY);

                for(Position pos : neighbors){
                    if(Vs[pos.x][pos.y] > bestValue){
                        bestValue = Vs[pos.x][pos.y];
                        bestAction = pos;
                    }
                }
                // Sum of best actions based on each square and its probability
                actions.put(bestAction.action, actions.get(bestAction.action) + probs[i][j]);

            }
        }

        // Get highest possible value from probabilities of different actions
        Map.Entry<Integer, Double> maxEntry = null;
        for (Map.Entry<Integer, Double> entry : actions.entrySet()) {
            if (maxEntry == null || entry.getValue()
                    .compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();  // default action for now
    }

    int differentTraversal(){
        Position current = new Position(0,0, STAY);
        double bestValue = Double.MIN_VALUE;
        int bestAction = 0;

        for(int i = 1; i < mundo.height; i++) {
            for (int j = 1; j < mundo.width; j++) {
                if(probs[current.x][current.y] < probs[i][j]){
                    current = new Position(i, j,STAY);
                }
            }
        }

        List<Position> neighbors = getNeighbors(current.x, current.y);
        for(Position pos : neighbors){
            if(Vs[pos.x][pos.y] > bestValue){
                bestValue = Vs[pos.x][pos.y];
                bestAction = pos.action;
            }
        }

        return bestAction;
    }

    void printStuff(double[][] thing) {
        for(int i = 0; i < mundo.height; i++) {
            for (int j = 0; j < mundo.width; j++) {
                System.out.print(thing[j][i] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    void doStuff() {
        int action;
        
        valueIteration();
        initializeProbabilities();  // Initializes the location (probability) map
//        printStuff(Vs);
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // you'll need to write this function for part III
//                    action = differentTraversal(); // My own implementation
                
                sout.println(action); // send the action to the Server

                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars);
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}