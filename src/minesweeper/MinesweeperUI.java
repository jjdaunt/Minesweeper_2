package minesweeper;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;;

@SuppressWarnings("serial")
public class MinesweeperUI extends JFrame {
	
	public MinesweeperUI() {
		initUI();
	}
	
	public static int playercount;
	public static int rounds;
	public static int colours;
	public static int shots;
	public String firstPlayer;
	int round = 0;
	public static Color[] colournames = {Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.WHITE};

	
	private void initUI(){
		setTitle("MINESWEEPER");
		setSize(750,650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBackground(Color.BLACK);
		
		Vector<String> players = new Vector<String>(1,1);
		Vector<JLabel> playerLabels = new Vector<JLabel>(1,1);
		Vector<JLabel> infoLabels = new Vector<JLabel>(1,1);
		Vector<Integer> colourcounts = new Vector<Integer>(1,1);
		
		// Grab setup info: player count, player names, round count, colour count
		while (playercount < 1){
			String str = JOptionPane.showInputDialog(this, "Enter player count: ", "Players", JOptionPane.PLAIN_MESSAGE);
			try {
				playercount = Integer.parseInt(str, 10);
				if (playercount < 1) System.out.println("Enter a positive integer, mate.");
			} catch (NumberFormatException e) {
				System.err.println("That's not an integer, you clown.");
			}
			if (str == null) System.exit(0);
		}

		// TODO: Big window with multiple boxes for this?
		for (int i = 0; i < playercount; i++) {
			players.add(JOptionPane.showInputDialog(this, "Player " + (i+1) + "'s name?", "Player Name", JOptionPane.PLAIN_MESSAGE));
			if (players.get(i) == null) System.exit(0);
		}
		
		while (rounds < 1){
			String str = JOptionPane.showInputDialog(this, "Enter round count: ", "Rounds", JOptionPane.PLAIN_MESSAGE);
			try {
				rounds = Integer.parseInt(str, 10);
				if (rounds < 1) System.out.println("Enter a positive integer, mate.");
			} catch (NumberFormatException e){
				System.err.println("That's not an integer, you clown.");
			}
			if (str == null) System.exit(0);
		}
		
		while (colours < 1){
			String str = JOptionPane.showInputDialog(this, "Enter colour count: ", "Colours", JOptionPane.PLAIN_MESSAGE);
			try {
				colours = Integer.parseInt(str, 10);
				if (colours < 1) System.out.println("Enter a positive integer, mate.");
			} catch (NumberFormatException e){
				System.err.println("That's not an integer, you clown.");
			}
			if (str == null) System.exit(0);
		}

		// Ensure a perfectly even colour distribution.
		shots = playercount * rounds;
		int offset = shots % colours;
		//if (offset != 0) shots += (colours - offset); // Old mode: disabled
		if (offset != 0) { // Patch: assume extras added to keep all rows even.
			shots += colours;
			offset = 0;
		}
		
		// TODO: Some way to select desired mode of handling.
		for (int i = 0; i < colours; i++) {
			colourcounts.add(shots / colours);
			// This might not be the best offset handling. Old mode: disabled
			// TODO: Set offset colours after setup, from dropdown?
			/*if (offset > 0){
				colourcounts.set(i,colourcounts.get(i)+1);
				offset--;
			}*/
		}
		
		// Style: Player Labels
		GridBagConstraints pl = new GridBagConstraints();
		pl.fill = GridBagConstraints.BOTH;
		pl.gridy = 0;
		pl.weightx = 0.8;
		pl.weighty = 0.5;
		pl.insets = new Insets(5, 0, 5, 20);
		pl.gridwidth = 2;
		pl.anchor = GridBagConstraints.WEST;
		// Style: Info Labels
		GridBagConstraints il = new GridBagConstraints();
		il.gridy = 0;
		il.weightx = 0.4;
		il.weighty = 0.5;
		il.insets = new Insets(5, 0, 5, 25);
		il.gridwidth = 1;
		il.anchor = GridBagConstraints.EAST;
		
		/// SETUP OVER ///
		for (int i = 0; i < playercount; i++){
			playerLabels.add(new JLabel(players.get(i)));
			infoLabels.add(new JLabel("0"));
			playerLabels.get(i).setForeground(Color.WHITE);
			playerLabels.get(i).setFont(new Font("Arial", Font.PLAIN, 60));
			playerLabels.get(i).setHorizontalAlignment(JLabel.RIGHT);
			infoLabels.get(i).setForeground(Color.WHITE);
			infoLabels.get(i).setFont(new Font("Arial", Font.PLAIN, 60));
			contentPane.add(playerLabels.get(i), pl);
			contentPane.add(infoLabels.get(i), il);
			pl.gridy++;
			il.gridy++;
		}
		JLabel blank = new JLabel("");
		JButton nextRound = new JButton("Start Game");
		nextRound.setFont(new Font("Arial", Font.PLAIN, 32));
		contentPane.add(blank, pl);
		contentPane.add(nextRound, il);
		
		firstPlayer = playerLabels.get(randInt(0,playercount-1)).getText();

		nextRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				nextRound.setText("Next Round");
				round++;
				if (round == rounds) nextRound.setText("Game Over");
				if (round > rounds) System.exit(0);
				int playercolour = -1;	
				// Get new rolls
				int[] rolls = new int[playercount];
				for (int i = 0; i < playercount; i++) rolls[i] = randInt(1,8);
				// Garbage tier programming: Sort the labels by roll.
				boolean nope = true;
				while (nope) {
					// TODO: Make this not garbage.
					nope = false;
					for (int j = 0; j < playercount - 1; j++){
						if (rolls[j] < rolls[j+1]){
							int t = rolls[j];
							rolls[j] = rolls[j+1];
							rolls[j+1] = t;
							String x = playerLabels.get(j).getText();
							playerLabels.get(j).setText(playerLabels.get(j+1).getText());
							playerLabels.get(j+1).setText(x);
							nope = true;
						}
					}
				}
				// Display the first player in green.
				for (int i = 0; i < playercount; i++){
					playerLabels.get(i).setForeground(Color.WHITE);
					if (playerLabels.get(i).getText() == firstPlayer) playerLabels.get(i).setForeground(Color.GREEN);
				}
				// Assign players colours, decrease available shot counts, display colour and roll in info labels.
				for (int i = 0; i < playercount; i++){
					while (playercolour == -1){
						playercolour = randInt(0,colours-1);
						if (colourcounts.get(playercolour) == 0) playercolour = -1;
						else colourcounts.set(playercolour,colourcounts.get(playercolour)-1);
					}
					infoLabels.get(i).setForeground(colournames[playercolour]);
					playercolour = -1;
					infoLabels.get(i).setText(""+rolls[i]);
				}
				// Take the low roll (from the bottom of the chart) and add every player with that roll,
				// select first player from that set at random.
				String lowRoll = infoLabels.get(playercount-1).getText();
				Vector<String> options = new Vector<String>(1,1);
				options.add(playerLabels.get(playercount-1).getText());
				for (int i = playercount - 2; i >= 0; i--){
					if (infoLabels.get(i).getText() == lowRoll) options.add(playerLabels.get(i).getText());
					else break;
				}
				firstPlayer = options.get(randInt(0,options.size()-1));
			}
		});
	}
	
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                MinesweeperUI frame = new MinesweeperUI();
                frame.setVisible(true);
			};
        });
	}
	
	public static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
