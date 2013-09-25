package pacman.entries.pacman.neuralPacMan.nodes;

import pacman.game.Game;

public abstract class MasterNode implements Node, Comparable<MasterNode> {
	private String name="";
	
	public abstract double value(int pacManIndex, Game game);
	
	public void setName (String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	@Override
	public int compareTo(MasterNode o) {
		String myName = this.name;
		String yourName = o.getName();
		if (myName.equalsIgnoreCase(yourName)){
			return 1;
		}
		return 0;
	}
}
