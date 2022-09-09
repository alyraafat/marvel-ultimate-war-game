package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;

public class Game {
	private static ArrayList<Champion> availableChampions = new ArrayList<Champion>();
	private static ArrayList<Ability> availableAbilities = new ArrayList<Ability>();
	private Player firstPlayer;
	private Player secondPlayer;
	private Object[][] board;
	private PriorityQueue turnOrder;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private final static int BOARDWIDTH = 5;
	private final static int BOARDHEIGHT = 5;
	private ArrayList<Damageable> dead;

	public Game(Player first, Player second) {
		firstPlayer = first;
		secondPlayer = second;
//		availableChampions = new ArrayList<Champion>();
//		availableAbilities = new ArrayList<Ability>();
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		dead = new ArrayList<Damageable>();
		placeChampions();
		placeCovers();
		prepareChampionTurns();
	}

	public static void loadAbilities(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Ability a = null;
			AreaOfEffect ar = null;
			switch (content[5]) {
			case "SINGLETARGET":
				ar = AreaOfEffect.SINGLETARGET;
				break;
			case "TEAMTARGET":
				ar = AreaOfEffect.TEAMTARGET;
				break;
			case "SURROUND":
				ar = AreaOfEffect.SURROUND;
				break;
			case "DIRECTIONAL":
				ar = AreaOfEffect.DIRECTIONAL;
				break;
			case "SELFTARGET":
				ar = AreaOfEffect.SELFTARGET;
				break;

			}
			Effect e = null;
			if (content[0].equals("CC")) {
				switch (content[7]) {
				case "Disarm":
					e = new Disarm(Integer.parseInt(content[8]));
					break;
				case "Dodge":
					e = new Dodge(Integer.parseInt(content[8]));
					break;
				case "Embrace":
					e = new Embrace(Integer.parseInt(content[8]));
					break;
				case "PowerUp":
					e = new PowerUp(Integer.parseInt(content[8]));
					break;
				case "Root":
					e = new Root(Integer.parseInt(content[8]));
					break;
				case "Shield":
					e = new Shield(Integer.parseInt(content[8]));
					break;
				case "Shock":
					e = new Shock(Integer.parseInt(content[8]));
					break;
				case "Silence":
					e = new Silence(Integer.parseInt(content[8]));
					break;
				case "SpeedUp":
					e = new SpeedUp(Integer.parseInt(content[8]));
					break;
				case "Stun":
					e = new Stun(Integer.parseInt(content[8]));
					break;
				}
			}
			switch (content[0]) {
			case "CC":
				a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
				break;
			case "DMG":
				a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			case "HEL":
				a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			}
			availableAbilities.add(a);
			line = br.readLine();
		}
		br.close();
	}

	public static void loadChampions(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Champion c = null;
			switch (content[0]) {
			case "A":
				c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;

			case "H":
				c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			case "V":
				c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			}

			c.getAbilities().add(findAbilityByName(content[8]));
			c.getAbilities().add(findAbilityByName(content[9]));
			c.getAbilities().add(findAbilityByName(content[10]));
			availableChampions.add(c);
			line = br.readLine();
		}
		br.close();
	}

	private static Ability findAbilityByName(String name) {
		for (Ability a : availableAbilities) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	public void placeCovers() {
		int i = 0;
		while (i < 5) {
			int x = ((int) (Math.random() * (BOARDWIDTH - 2))) + 1;
			int y = (int) (Math.random() * BOARDHEIGHT);

			if (board[x][y] == null) {
				board[x][y] = new Cover(x, y);
				i++;
			}
		}

	}

	public void placeChampions() {
		int i = 1;
		for (Champion c : firstPlayer.getTeam()) {
			board[0][i] = c;
			c.setLocation(new Point(0, i));
			i++;
		}
		i = 1;
		for (Champion c : secondPlayer.getTeam()) {
			board[BOARDHEIGHT - 1][i] = c;
			c.setLocation(new Point(BOARDHEIGHT - 1, i));
			i++;
		}

	}

	public Champion getCurrentChampion() {
		return (Champion) turnOrder.peekMin();
	}

	public Player checkGameOver() {
		if (firstPlayer.getTeam().size() == 0)
			return secondPlayer;
		else if (secondPlayer.getTeam().size() == 0)
			return firstPlayer;
		else
			return null;
	}

	public void move(Direction d) throws NotEnoughResourcesException, UnallowedMovementException {
		if (hasEffect(getCurrentChampion(), "Root"))
			throw new UnallowedMovementException("You can not move while being rooted");
		if (getCurrentChampion().getCurrentActionPoints() < 1)
			throw new NotEnoughResourcesException("You need at least one action point to move");
		int currx = (int) getCurrentChampion().getLocation().getX();
		int curry = (int) getCurrentChampion().getLocation().getY();
		int newx = currx;
		int newy = curry;
		if (d == Direction.UP)
			newx = newx + 1;
		else if (d == Direction.DOWN)
			newx = newx - 1;
		else if (d == Direction.LEFT)
			newy = newy - 1;
		else if (d == Direction.RIGHT)
			newy = newy + 1;
		if (newx < 0 || newx >= BOARDHEIGHT || newy < 0 || newy >= BOARDWIDTH)
			throw new UnallowedMovementException("Can not move out of the board");
		if (board[newx][newy] == null) {
			board[currx][curry] = null;
			board[newx][newy] = getCurrentChampion();
			getCurrentChampion().setLocation(new Point(newx, newy));
			getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 1);
		} else
			throw new UnallowedMovementException("target cell is not empty");

	}

	private boolean hasEffect(Champion currentChampion, String s) {
		for (Effect e : currentChampion.getAppliedEffects()) {
			if (e.getName().equals(s))
				return true;
		}
		return false;
	}

	public void attack(Direction d)
			throws NotEnoughResourcesException, ChampionDisarmedException, InvalidTargetException {
		if (hasEffect(getCurrentChampion(), "Disarm"))
			throw new ChampionDisarmedException("Can not attack while being disarmed");
		if (getCurrentChampion().getCurrentActionPoints() < 2)
			throw new NotEnoughResourcesException("You need at least two action point to perform a normal attack");
		int currx = (int) getCurrentChampion().getLocation().getX();
		int curry = (int) getCurrentChampion().getLocation().getY();
		for (int i = 0; i < getCurrentChampion().getAttackRange(); i++) {
			if (d == Direction.UP)
				currx++;
			else if (d == Direction.DOWN)
				currx--;
			else if (d == Direction.LEFT)
				curry--;
			else if (d == Direction.RIGHT)
				curry++;
			if (currx < 0 || currx >= BOARDHEIGHT || curry < 0 || curry >= BOARDWIDTH)
				return;
			else if (board[currx][curry] != null) {
				if (board[currx][curry] instanceof Cover) {
					int curhp = ((Cover) board[currx][curry]).getCurrentHP();
					curhp -= getCurrentChampion().getAttackDamage();
					((Cover) board[currx][curry]).setCurrentHP(curhp);
					if (curhp <= 0)
						board[currx][curry] = null;
					return;
				} else if (board[currx][curry] instanceof Champion) {

					int damage = getCurrentChampion().getAttackDamage();
					Champion target = (Champion) board[currx][curry];
					if (firstPlayer.getTeam().contains(getCurrentChampion()) && firstPlayer.getTeam().contains(target))
						continue;
					else if (secondPlayer.getTeam().contains(getCurrentChampion())
							&& secondPlayer.getTeam().contains(target))
						continue;
					Champion curr = getCurrentChampion();
					 if (hasEffect(target, "Dodge")) {
						int r = ((int) (Math.random() * 100)) + 1;
						if (r <= 50) {
							curr.setCurrentActionPoints(curr.getCurrentActionPoints() - 2);
							return;
						}
					} 
					if (hasEffect(target, "Shield")) {
						for (Effect e : target.getAppliedEffects()) {
							if (e.getName().equals("Shield")) {
								e.remove(target);
								target.getAppliedEffects().remove(e);
								curr.setCurrentActionPoints(curr.getCurrentActionPoints() - 2);
								return;
							}
						}
					}
					
					else if ((curr instanceof Hero && !(target instanceof Hero))
							|| (curr instanceof Villain && !(target instanceof Villain))
							|| (curr instanceof AntiHero && !(target instanceof AntiHero)))
						damage = (int) (damage * 1.5);

					target.setCurrentHP(target.getCurrentHP() - damage);
					curr.setCurrentActionPoints(curr.getCurrentActionPoints() - 2);
					ArrayList<Damageable> targets = new ArrayList<Damageable>();
					targets.add(target);
					cleanup(targets);
					return;

				}

			}

		}
	}

	public void castAbility(Ability a)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		validateCastAbility(a);
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			targets.add(getCurrentChampion());
		} else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
			ArrayList<Champion> team = null;
			if (a instanceof DamagingAbility || (a instanceof CrowdControlAbility
					&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)) {
				if (firstPlayer.getTeam().contains(getCurrentChampion()))
					team = secondPlayer.getTeam();
				else
					team = firstPlayer.getTeam();
			} else if (a instanceof HealingAbility
					|| (a instanceof CrowdControlAbility && (a instanceof CrowdControlAbility
							&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF))) {
				if (firstPlayer.getTeam().contains(getCurrentChampion()))
					team = firstPlayer.getTeam();
				else
					team = secondPlayer.getTeam();
			}
			for (Champion c : team) {
				int x = (int) c.getLocation().getX();
				int y = (int) c.getLocation().getY();
				int distance = Math.abs((int) getCurrentChampion().getLocation().getX() - x)
						+ Math.abs((int) getCurrentChampion().getLocation().getY() - y);
				if (distance <= a.getCastRange())
					targets.add(c);
			}
		} else if (a.getCastArea() == AreaOfEffect.SURROUND) {
			ArrayList<Point> possiblePoints = new ArrayList<Point>();
			int currx = (int) getCurrentChampion().getLocation().getX();
			int curry = (int) getCurrentChampion().getLocation().getY();
			possiblePoints.add(new Point(currx + 1, curry));
			possiblePoints.add(new Point(currx - 1, curry));
			possiblePoints.add(new Point(currx, curry + 1));
			possiblePoints.add(new Point(currx, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry + 1));
			possiblePoints.add(new Point(currx - 1, curry - 1));
			possiblePoints.add(new Point(currx - 1, curry + 1));
			targets = prepareTargetsFromPoints(a, possiblePoints);
		}

		a.execute(targets);
		getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
		getCurrentChampion()
				.setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
		cleanup(targets);
		a.setCurrentCooldown(a.getBaseCooldown());

	}

	private void validateCastAbility(Ability a) throws NotEnoughResourcesException, AbilityUseException {
		if (getCurrentChampion().getMana() < a.getManaCost())
			throw new NotEnoughResourcesException(
					"you need at least " + a.getManaCost() + " mana to cast this ability");
		else if (getCurrentChampion().getCurrentActionPoints() < a.getRequiredActionPoints())
			throw new NotEnoughResourcesException(
					"you need at least " + a.getRequiredActionPoints() + " action points to cast this ability");
		else if (hasEffect(getCurrentChampion(), "Silence"))
			throw new AbilityUseException("You can not cast an ability while being silenced");
		else if (a.getCurrentCooldown() > 0)
			throw new AbilityUseException("You can not use an ability while it is in cooldown");
	}

	public void castAbility(Ability a, Direction d)
			throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException {
		validateCastAbility(a);
		ArrayList<Point> possiblePoints = new ArrayList<Point>();
		int currx = (int) getCurrentChampion().getLocation().getX();
		int curry = (int) getCurrentChampion().getLocation().getY();
		for (int i = 0; i < a.getCastRange(); i++) {
			if (d == Direction.UP) {
				currx++;
				if (currx == BOARDHEIGHT)
					break;
			} else if (d == Direction.DOWN) {
				currx--;
				if (currx < 0)
					break;
			} else if (d == Direction.LEFT) {
				curry--;
				if (curry < 0)
					break;
			} else if (d == Direction.RIGHT) {
				curry++;
				if (curry == BOARDWIDTH)
					break;
			}
			possiblePoints.add(new Point(currx, curry));
		}
		ArrayList<Damageable> targets = prepareTargetsFromPoints(a, possiblePoints);

		a.execute(targets);
		getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
		getCurrentChampion()
				.setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());

		a.setCurrentCooldown(a.getBaseCooldown());
		cleanup(targets);

	}

	private ArrayList<Damageable> prepareTargetsFromPoints(Ability a, ArrayList<Point> possiblePoints) {
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		for (Point p : possiblePoints) {
			int x = (int) p.getX();
			int y = (int) p.getY();
			if (x >= 0 && x < BOARDHEIGHT && y >= 0 && y < BOARDWIDTH) {
				Object o = board[x][y];
				if (o != null) {
					if (o instanceof Cover) {
						if (a instanceof DamagingAbility)
							targets.add((Damageable) o);
					} else {
						boolean friendly = ((firstPlayer.getTeam().contains(getCurrentChampion())
								&& firstPlayer.getTeam().contains(o))
								|| ((secondPlayer.getTeam().contains(getCurrentChampion())
										&& secondPlayer.getTeam().contains(o)))) ? true : false;
						if (a instanceof HealingAbility && friendly)
							targets.add((Damageable) o);
						else if (a instanceof DamagingAbility && !friendly) {
							Champion c = (Champion) o;
							if (hasEffect(c, "Shield")) {
								for (Effect e : c.getAppliedEffects()) {
									if (e instanceof Shield) {
										c.getAppliedEffects().remove(e);
										break;
									}
								}
							} else
								targets.add((Damageable) o);
						} else if (a instanceof CrowdControlAbility
								&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && !friendly)
							targets.add((Damageable) o);
						else if (a instanceof CrowdControlAbility
								&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && friendly)
							targets.add((Damageable) o);
					}
				}
			}
		}
		return targets;
	}

	public void castAbility(Ability a, int x, int y) throws NotEnoughResourcesException, AbilityUseException,
			InvalidTargetException, CloneNotSupportedException {
		validateCastAbility(a);
		if (board[x][y] == null)
			throw new InvalidTargetException("You can not cast an ability on an empty cell");
		int distance = Math.abs((int) getCurrentChampion().getLocation().getX() - x)
				+ Math.abs((int) getCurrentChampion().getLocation().getY() - y);
		if (distance > a.getCastRange())
			throw new AbilityUseException("Target out of the ability's cast range");

		if (board[x][y] instanceof Cover && !(a instanceof DamagingAbility))
			throw new InvalidTargetException("Covers can only be damaged");
		if (board[x][y] instanceof Champion) {
			Champion target = (Champion) board[x][y];
			boolean friendly = ((firstPlayer.getTeam().contains(getCurrentChampion())
					&& firstPlayer.getTeam().contains(target))
					|| ((secondPlayer.getTeam().contains(getCurrentChampion())
							&& secondPlayer.getTeam().contains(target)))) ? true : false;
			if (friendly && a instanceof DamagingAbility)
				throw new InvalidTargetException("Can not cast damaging ability on friendly targets");
			if (friendly && a instanceof CrowdControlAbility
					&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)
				throw new InvalidTargetException("Can not debuff friendly targets");
			if (!friendly && a instanceof HealingAbility)
				throw new InvalidTargetException("Can not cast healing ability on enemy targets");
			if (!friendly && a instanceof CrowdControlAbility
					&& ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
				throw new InvalidTargetException("Can not buff enemy targets");
		}
		ArrayList<Damageable> targets = new ArrayList<Damageable>();
		if (board[x][y] instanceof Cover && a instanceof DamagingAbility)
			targets.add((Cover) board[x][y]);
		else {
			Champion c = (Champion) board[x][y];
			if (hasEffect(c, "Shield")) {
				for (Effect e : c.getAppliedEffects()) {
					if (e instanceof Shield) {
						c.getAppliedEffects().remove(e);
						break;
					}
				}
			} else
				targets.add(c);
		}
		a.execute(targets);
		getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
		getCurrentChampion()
				.setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
		a.setCurrentCooldown(a.getBaseCooldown());
		cleanup(targets);
	}

	public void useLeaderAbility() throws LeaderNotCurrentException, LeaderAbilityAlreadyUsedException {
		if (getCurrentChampion() != firstPlayer.getLeader() && getCurrentChampion() != secondPlayer.getLeader())
			throw new LeaderNotCurrentException("The current champion is not a leader");
		if (getCurrentChampion() == firstPlayer.getLeader() && firstLeaderAbilityUsed)
			throw new LeaderAbilityAlreadyUsedException("This leader already used his ability");
		if (getCurrentChampion() == secondPlayer.getLeader() && secondLeaderAbilityUsed)
			throw new LeaderAbilityAlreadyUsedException("This leader already used his ability");
		ArrayList<Champion> targets = new ArrayList<Champion>();
		if (getCurrentChampion() instanceof Hero) {
			ArrayList<Champion> team = getCurrentChampion() == firstPlayer.getLeader() ? firstPlayer.getTeam()
					: secondPlayer.getTeam();
			for (Champion c : team)
				targets.add(c);
		} else if (getCurrentChampion() instanceof AntiHero) {
			for (Champion c : firstPlayer.getTeam()) {
				if (c != firstPlayer.getLeader())
					targets.add(c);
			}
			for (Champion c : secondPlayer.getTeam()) {
				if (c != secondPlayer.getLeader())
					targets.add(c);
			}
		} else if (getCurrentChampion() instanceof Villain) {
			ArrayList<Champion> enemies = getCurrentChampion() == firstPlayer.getLeader() ? secondPlayer.getTeam()
					: firstPlayer.getTeam();
			for (Champion c : enemies) {
				if (c.getCurrentHP() < (0.3 * c.getMaxHP()))
					targets.add(c);
			}
		}
		getCurrentChampion().useLeaderAbility(targets);
		if (getCurrentChampion() == firstPlayer.getLeader())
			firstLeaderAbilityUsed = true;
		else if (getCurrentChampion() == secondPlayer.getLeader())
			secondLeaderAbilityUsed = true;
		ArrayList<Damageable> targets2 = new ArrayList<Damageable>();
		for(int i = 0 ; i < targets.size(); i++) {
			targets2.add(targets.get(i));
		}
		cleanup(targets2);
	}

	private void cleanup(ArrayList<Damageable> targets) {
		for (Damageable c : targets) {
			if (c.getCurrentHP() == 0) {
				dead.add(c);
				board[(int) c.getLocation().getX()][(int) c.getLocation().getY()] = null;
				firstPlayer.getTeam().remove(c);
				secondPlayer.getTeam().remove(c);
				ArrayList<Champion> temp = new ArrayList<Champion>();
				while (!turnOrder.isEmpty()) {
					if (turnOrder.peekMin() == c) {
						turnOrder.remove();
						break;
					} else
						temp.add((Champion) turnOrder.remove());
				}
				while (!temp.isEmpty())
					turnOrder.insert(temp.remove(0));
			}
		}

	}

	public void endTurn() {
		turnOrder.remove();
		if (turnOrder.isEmpty())
			prepareChampionTurns();
		while (!turnOrder.isEmpty() && hasEffect((Champion) turnOrder.peekMin(), "Stun")) {
			Champion current = (Champion) turnOrder.peekMin();
			updateTimers(current);
			turnOrder.remove();
			if (turnOrder.isEmpty())
				prepareChampionTurns();
		}
		Champion current = (Champion) turnOrder.peekMin();
		updateTimers(current);
		current.setCurrentActionPoints(current.getMaxActionPointsPerTurn());
	}
	private void updateTimers(Champion current) {
		int i = 0;
		while (i < current.getAppliedEffects().size()) {
			Effect e = current.getAppliedEffects().get(i);
			e.setDuration(e.getDuration() - 1);
			if (e.getDuration() == 0) {
				current.getAppliedEffects().remove(e);
				e.remove(current);

			} else
				i++;
		}
		for (Ability a : current.getAbilities()) {
			if (a.getCurrentCooldown() > 0)
				a.setCurrentCooldown(a.getCurrentCooldown() - 1);
		}
	}

	private void prepareChampionTurns() {
		for (Champion c : firstPlayer.getTeam())
			turnOrder.insert(c);
		for (Champion c : secondPlayer.getTeam())
			turnOrder.insert(c);

	}

	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public static ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public Object[][] getBoard() {
		return board;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public static int getBoardwidth() {
		return BOARDWIDTH;
	}

	public static int getBoardheight() {
		return BOARDHEIGHT;
	}
	
	//computer 
	public void decision(Champion champ) throws NotEnoughResourcesException, ChampionDisarmedException, InvalidTargetException, UnallowedMovementException, AbilityUseException, CloneNotSupportedException {
		Champion c = getCurrentChampion();
			int[] priority = new int[3];
			for(int i = 0 ; i<c.getAbilities().size();i++) {
				priority[i] = getPriority(c.getAbilities().get(i));
			}
			int maxIndex = -1;
			int maxValue = -1;
			for(int i = 0 ; i < priority.length ; i++) 
				if(priority[i]>maxValue && c.getAbilities().get(i).getRequiredActionPoints() >= c.getCurrentActionPoints() && c.getAbilities().get(i).getManaCost() >= c.getMana()) {
					maxIndex = i;
					maxValue = priority[i];
				}
			int attackp = attackPriority(champ, firstPlayer.getTeam());
			if(attackp >= maxValue && 2 <= c.getCurrentActionPoints() && attackp != 0) {
				attack(searchForWeakestEnemyInRangeAttackDirection(firstPlayer.getTeam(),c));
			}
			else if (attackp < maxValue) {
				if(c.getAbilities().get(maxIndex).getCastArea() == AreaOfEffect.SINGLETARGET) {
					Ability a = c.getAbilities().get(maxIndex);
					if (a instanceof HealingAbility || (a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)) {
						Champion target = searchForWeakestAllyInRange(secondPlayer.getTeam(),c,c.getAbilities().get(maxIndex));
						castAbility(c.getAbilities().get(maxIndex),(int)target.getLocation().getX(),(int)target.getLocation().getY());
					}
					else if(a instanceof DamagingAbility) {
						Champion target = searchForWeakestEnemyInRange(firstPlayer.getTeam(),c,c.getAbilities().get(maxIndex));
						castAbility(c.getAbilities().get(maxIndex),(int)target.getLocation().getX(),(int)target.getLocation().getY());
					}
					else if(a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
						Champion target = searchForStrongestEnemyInRange(firstPlayer.getTeam(),c,c.getAbilities().get(maxIndex));
						castAbility(c.getAbilities().get(maxIndex),(int)target.getLocation().getX(),(int)target.getLocation().getY());
					}
			
					
				}
				else if(c.getAbilities().get(maxIndex).getCastArea() == AreaOfEffect.DIRECTIONAL) {
					Ability a = c.getAbilities().get(maxIndex);
					if (a instanceof HealingAbility || (a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)) {
						Champion target = searchForWeakestAllyInRangeDirection(secondPlayer.getTeam(),c,c.getAbilities().get(maxIndex));
						castAbility(c.getAbilities().get(maxIndex),getDirection(c,target));
					}
					else if(a instanceof DamagingAbility) {
						Champion target = searchForWeakestEnemyInRangeDirection(firstPlayer.getTeam(),c,c.getAbilities().get(maxIndex));
						castAbility(c.getAbilities().get(maxIndex),getDirection(c,target));
					}
					else if(a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
						Champion target = searchForStrongestAllyInRangeDirection(firstPlayer.getTeam(),c,c.getAbilities().get(maxIndex));
						castAbility(c.getAbilities().get(maxIndex),getDirection(c,target));
					}	
				}
				else
					castAbility(c.getAbilities().get(maxIndex));
			}
			else 
				throw new UnallowedMovementException(); 	
		}
	public Direction getDirection(Champion champ , Champion target) {
		if(target.getLocation().getX() > champ.getLocation().getX()) {
			return Direction.UP;
		}
		else if(target.getLocation().getX() < champ.getLocation().getX()) {
			return Direction.DOWN;
		}
		else if(target.getLocation().getY() > champ.getLocation().getY()) {
			return Direction.RIGHT;
		}
		return Direction.LEFT;
	}
	
	public int getPriority (Ability a) {
		int p = 0;
		if (a instanceof HealingAbility) {
			p = healPriority(getCurrentChampion(), secondPlayer.getTeam(), a);
		}
		if (a instanceof DamagingAbility) {
			p = damagePriority(getCurrentChampion(), secondPlayer.getTeam(), a);
		}
		if (a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) {
			p = crowdControlPriorityBuff(getCurrentChampion(), secondPlayer.getTeam(), a);
		}
		if (a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
			p = crowdControlPriorityDeBuff(getCurrentChampion(), secondPlayer.getTeam(), a);
		}
		return p;
	}
	public Champion searchForWeakestAllyInRange(ArrayList<Champion> allies, Champion champ,Ability a) {
		Champion c = champ;
		for(int i = 0 ; i < allies.size();i++) {		
			if(c.getCurrentHP() > allies.get(i).getCurrentHP() && manhattan(c.getLocation(),allies.get(i).getLocation())<= a.getCastRange())
				c = allies.get(i);
		}
		return c;
	}
	public Champion searchForWeakestEnemyInRange(ArrayList<Champion> enemies, Champion champ,Ability a) {
		Champion c = null;
		for(int i = 0 ; i < enemies.size();i++) {		
			if(c == null  && manhattan(champ.getLocation(),enemies.get(i).getLocation())<= a.getCastRange())
				c = enemies.get(i);
			if(c.getCurrentHP() > enemies.get(i).getCurrentHP() && manhattan(c.getLocation(),enemies.get(i).getLocation())<= a.getCastRange())
				c = enemies.get(i);
		}
		return c;
	}
	public Champion searchForStrongestEnemyInRange(ArrayList<Champion> enemies, Champion champ,Ability a) {
		Champion c = null;
		for(int i = 0 ; i < enemies.size();i++) {		
			if(c == null  && manhattan(champ.getLocation(),enemies.get(i).getLocation())<= a.getCastRange())
				c = enemies.get(i);
			if(c.getCurrentHP() < enemies.get(i).getCurrentHP() && manhattan(c.getLocation(),enemies.get(i).getLocation())<= a.getCastRange())
				c = enemies.get(i);
		}
		return c;
	}
	public Champion searchForWeakestAllyInRangeDirection(ArrayList<Champion> allies, Champion champ,Ability a) {
		Champion c = null;
		for(int i = 0 ; i < allies.size();i++) {		
			if(c==null && manhattan(champ.getLocation(),allies.get(i).getLocation())<= a.getCastRange() && (allies.get(i).getLocation().getX() == champ.getLocation().getX()|| allies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = allies.get(i);
			if(c.getCurrentHP() > allies.get(i).getCurrentHP() && manhattan(c.getLocation(),allies.get(i).getLocation())<= a.getCastRange() && (allies.get(i).getLocation().getX() == champ.getLocation().getX()|| allies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = allies.get(i);
		}
		return c;
	}
	public Champion searchForStrongestAllyInRangeDirection(ArrayList<Champion> allies, Champion champ,Ability a) {
		Champion c = null;
		for(int i = 0 ; i < allies.size();i++) {		
			if(c==null && manhattan(champ.getLocation(),allies.get(i).getLocation())<= a.getCastRange() && (allies.get(i).getLocation().getX() == champ.getLocation().getX()|| allies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = allies.get(i);
			if(c.getCurrentHP() < allies.get(i).getCurrentHP() && manhattan(c.getLocation(),allies.get(i).getLocation())<= a.getCastRange() && (allies.get(i).getLocation().getX() == champ.getLocation().getX()|| allies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = allies.get(i);
		}
		return c;
	}
	public Champion searchForWeakestEnemyInRangeDirection(ArrayList<Champion> enemies, Champion champ,Ability a) {
		Champion c = null;
		for(int i = 0 ; i < enemies.size();i++) {		
			if(c==null && manhattan(champ.getLocation(),enemies.get(i).getLocation())<= a.getCastRange() && (enemies.get(i).getLocation().getX() == champ.getLocation().getX()|| enemies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = enemies.get(i);
			if(c.getCurrentHP() > enemies.get(i).getCurrentHP() && manhattan(c.getLocation(),enemies.get(i).getLocation())<= a.getCastRange() && (enemies.get(i).getLocation().getX() == champ.getLocation().getX()|| enemies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = enemies.get(i);
		}
		return c;
	}
	public Champion searchForWeakestEnemyInRangeAttack(ArrayList<Champion> enemies, Champion champ) {
		Champion c = null;
		for(int i = 0 ; i < enemies.size();i++) {		
			if(c==null && manhattan(champ.getLocation(),enemies.get(i).getLocation())<= champ.getAttackDamage() && (enemies.get(i).getLocation().getX() == champ.getLocation().getX()|| enemies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = enemies.get(i);
			if(c.getCurrentHP() > enemies.get(i).getCurrentHP() && manhattan(c.getLocation(),enemies.get(i).getLocation())<=  champ.getAttackDamage() && (enemies.get(i).getLocation().getX() == champ.getLocation().getX()|| enemies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = enemies.get(i);
		}
		return c;
	}
	public Direction searchForWeakestEnemyInRangeAttackDirection(ArrayList<Champion> enemies, Champion champ) {
		Champion c = null;
		for(int i = 0 ; i < enemies.size();i++) {		
			if(c==null && manhattan(champ.getLocation(),enemies.get(i).getLocation())<= champ.getAttackDamage() && (enemies.get(i).getLocation().getX() == champ.getLocation().getX()|| enemies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = enemies.get(i);
			if(c.getCurrentHP() > enemies.get(i).getCurrentHP() && manhattan(c.getLocation(),enemies.get(i).getLocation())<=  champ.getAttackDamage() && (enemies.get(i).getLocation().getX() == champ.getLocation().getX()|| enemies.get(i).getLocation().getY() == champ.getLocation().getY()))
				c = enemies.get(i);
		}
		if(c.getLocation().getX() > champ.getLocation().getX()) {
			return Direction.UP;
		}
		else if(c.getLocation().getX() < champ.getLocation().getX()) {
			return Direction.DOWN;
		}
		else if(c.getLocation().getY() > champ.getLocation().getY()) {
			return Direction.RIGHT;
		}
		return Direction.LEFT;
		
	}
	
	public int healPriority(Champion champ , ArrayList<Champion> allies , Ability a ) { 
		int p = 0; 
		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			if (champ.getCurrentHP() < champ.getMaxHP() * 0.1)
				p = 10;
			else if (champ.getCurrentHP() < champ.getMaxHP() * 0.25)
				p = 5;
			else 
				p = 1;
		}	
		else if (a.getCastArea() == AreaOfEffect.SINGLETARGET) {
			Champion c = searchForWeakestAllyInRange(allies,champ,a);
			if (c.getCurrentHP() < c.getMaxHP() * 0.1)
				p = 3;
			else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
				p = 2;
			else 
				p = 1;
		}
		else if (a.getCastArea() == AreaOfEffect.SURROUND) {
			ArrayList<Point> possiblePoints = new ArrayList<Point>();
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			int currx = (int) getCurrentChampion().getLocation().getX();
			int curry = (int) getCurrentChampion().getLocation().getY();
			possiblePoints.add(new Point(currx + 1, curry));
			possiblePoints.add(new Point(currx - 1, curry));
			possiblePoints.add(new Point(currx, curry + 1));
			possiblePoints.add(new Point(currx, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry + 1));
			possiblePoints.add(new Point(currx - 1, curry - 1));
			possiblePoints.add(new Point(currx - 1, curry + 1));
			targets = prepareTargetsFromPoints(a, possiblePoints);
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 3;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			for (Champion c : secondPlayer.getTeam()) {
				int x = (int) c.getLocation().getX();
				int y = (int) c.getLocation().getY();
				int distance = Math.abs((int) getCurrentChampion().getLocation().getX() - x)
					+ Math.abs((int) getCurrentChampion().getLocation().getY() - y);
				if (distance <= a.getCastRange())
					targets.add(c);
			}
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 3;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if(a.getCastArea() == AreaOfEffect.DIRECTIONAL) {
			Champion c = searchForWeakestAllyInRangeDirection(allies,champ,a);
			if(c==null)
				p = 0;
			else {
				if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 5;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
				
		}
		return p;
	}
	public int damagePriority(Champion champ , ArrayList<Champion> enemies , Ability a) {
		int p = 0; 
		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			if (champ.getCurrentHP() < champ.getMaxHP() * 0.1)
				p = 1;
			else if (champ.getCurrentHP() < champ.getMaxHP() * 0.25)
				p = 5;
			else 
				p = 10;
		}	
		else if (a.getCastArea() == AreaOfEffect.SINGLETARGET) {
			Champion c = searchForWeakestEnemyInRange(enemies,champ,a);
			if (c.getCurrentHP() < champ.getAttackDamage() )
				p += 10;
			else if (c.getCurrentHP() < c.getMaxHP() * 0.1)
				p += 5;
			else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
				p += 2;
			else 
				p += 1;
		
		}
		else if (a.getCastArea() == AreaOfEffect.SURROUND) {
			ArrayList<Point> possiblePoints = new ArrayList<Point>();
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			int currx = (int) getCurrentChampion().getLocation().getX();
			int curry = (int) getCurrentChampion().getLocation().getY();
			possiblePoints.add(new Point(currx + 1, curry));
			possiblePoints.add(new Point(currx - 1, curry));
			possiblePoints.add(new Point(currx, curry + 1));
			possiblePoints.add(new Point(currx, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry + 1));
			possiblePoints.add(new Point(currx - 1, curry - 1));
			possiblePoints.add(new Point(currx - 1, curry + 1));
			targets = prepareTargetsFromPoints(a, possiblePoints);
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() < champ.getAttackDamage() )
					p += 10;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 5;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			for (Champion c : firstPlayer.getTeam()) {
				int x = (int) c.getLocation().getX();
				int y = (int) c.getLocation().getY();
				int distance = Math.abs((int) getCurrentChampion().getLocation().getX() - x)
					+ Math.abs((int) getCurrentChampion().getLocation().getY() - y);
				if (distance <= a.getCastRange())
					targets.add(c);
			}
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() < champ.getAttackDamage() )
					p += 10;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 5;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if(a.getCastArea() == AreaOfEffect.DIRECTIONAL) {
			Champion c = searchForWeakestEnemyInRangeDirection(enemies,champ,a);
			if(c==null)
				p = 0;
			else {
				if (c.getCurrentHP() < champ.getAttackDamage() )
					p += 10;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 5;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
				
		}
		return p;
	}
	public int crowdControlPriorityBuff(Champion champ , ArrayList<Champion> allies , Ability a) {
		int p = 0; 
		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			if (champ.getCurrentHP() < champ.getMaxHP() * 0.1)
				p = 10;
			else if (champ.getCurrentHP() < champ.getMaxHP() * 0.25)
				p = 5;
			else 
				p = 1;
		}	
		else if (a.getCastArea() == AreaOfEffect.SINGLETARGET) {
			Champion c = searchForWeakestAllyInRange(allies,champ,a);
			if (c.getCurrentHP() < c.getMaxHP() * 0.1)
				p = 3;
			else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
				p = 2;
			else 
				p = 1;
		}
		else if (a.getCastArea() == AreaOfEffect.SURROUND) {
			ArrayList<Point> possiblePoints = new ArrayList<Point>();
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			int currx = (int) getCurrentChampion().getLocation().getX();
			int curry = (int) getCurrentChampion().getLocation().getY();
			possiblePoints.add(new Point(currx + 1, curry));
			possiblePoints.add(new Point(currx - 1, curry));
			possiblePoints.add(new Point(currx, curry + 1));
			possiblePoints.add(new Point(currx, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry + 1));
			possiblePoints.add(new Point(currx - 1, curry - 1));
			possiblePoints.add(new Point(currx - 1, curry + 1));
			targets = prepareTargetsFromPoints(a, possiblePoints);
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 3;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			for (Champion c : secondPlayer.getTeam()) {
				int x = (int) c.getLocation().getX();
				int y = (int) c.getLocation().getY();
				int distance = Math.abs((int) getCurrentChampion().getLocation().getX() - x)
					+ Math.abs((int) getCurrentChampion().getLocation().getY() - y);
				if (distance <= a.getCastRange())
					targets.add(c);
			}
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 3;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if(a.getCastArea() == AreaOfEffect.DIRECTIONAL) {
			Champion c = searchForWeakestAllyInRangeDirection(allies,champ,a);
			if(c==null)
				p = 0;
			else {
				if (c.getCurrentHP() < c.getMaxHP() * 0.1)
					p += 5;
				else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
				
		}
		return p;
	}
	public int crowdControlPriorityDeBuff(Champion champ , ArrayList<Champion> enemies, Ability a) {
		int p = 0; 
		if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
			if (champ.getCurrentHP() < champ.getMaxHP() * 0.1)
				p = 1;
			else if (champ.getCurrentHP() < champ.getMaxHP() * 0.25)
				p = 5;
			else 
				p = 10;
		}	
		else if (a.getCastArea() == AreaOfEffect.SINGLETARGET) {
			Champion c = searchForWeakestEnemyInRange(enemies,champ,a);
			if (c.getCurrentHP() > c.getMaxHP() * 0.5)
				p += 5;
			else if (c.getCurrentHP() > c.getMaxHP() * 0.25)
				p += 2;
			else 
				p += 1;
		
		}
		else if (a.getCastArea() == AreaOfEffect.SURROUND) {
			ArrayList<Point> possiblePoints = new ArrayList<Point>();
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			int currx = (int) getCurrentChampion().getLocation().getX();
			int curry = (int) getCurrentChampion().getLocation().getY();
			possiblePoints.add(new Point(currx + 1, curry));
			possiblePoints.add(new Point(currx - 1, curry));
			possiblePoints.add(new Point(currx, curry + 1));
			possiblePoints.add(new Point(currx, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry - 1));
			possiblePoints.add(new Point(currx + 1, curry + 1));
			possiblePoints.add(new Point(currx - 1, curry - 1));
			possiblePoints.add(new Point(currx - 1, curry + 1));
			targets = prepareTargetsFromPoints(a, possiblePoints);
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() > c.getMaxHP() * 0.5)
					p += 5;
				else if (c.getCurrentHP() > c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
			ArrayList<Damageable> targets = new ArrayList<Damageable>();
			for (Champion c : firstPlayer.getTeam()) {
				int x = (int) c.getLocation().getX();
				int y = (int) c.getLocation().getY();
				int distance = Math.abs((int) getCurrentChampion().getLocation().getX() - x)
					+ Math.abs((int) getCurrentChampion().getLocation().getY() - y);
				if (distance <= a.getCastRange())
					targets.add(c);
			}
			for(int i = 0 ; i < targets.size() ; i++) {
				Champion c = (Champion) targets.get(i);
				if (c.getCurrentHP() > c.getMaxHP() * 0.5)
					p += 5;
				else if (c.getCurrentHP() > c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
		}
		else if(a.getCastArea() == AreaOfEffect.DIRECTIONAL) {
			Champion c = searchForWeakestEnemyInRangeDirection(enemies,champ,a);
			if(c==null)
				p = 0;
			else {
				if (c.getCurrentHP() > c.getMaxHP() * 0.5)
					p += 5;
				else if (c.getCurrentHP() > c.getMaxHP() * 0.25)
					p += 2;
				else 
					p += 1;
			}
				
		}
		return p;
	}
	public int attackPriority(Champion champ , ArrayList<Champion> enemies) {
		int p = 0;
		Champion c = searchForWeakestEnemyInRangeAttack(enemies,champ);
		if(c==null)
			p = 0;
		else {
			if (c.getCurrentHP() < champ.getAttackDamage() )
				p += 10;
			else if (c.getCurrentHP() < c.getMaxHP() * 0.1)
				p += 5;
			else if (c.getCurrentHP() < c.getMaxHP() * 0.25)
				p += 2;
			else 
				p += 1;
		}
		return p;	
	}
	public void moveBot() throws NotEnoughResourcesException, UnallowedMovementException {
		Champion champ = this.getCurrentChampion();
		int currX = (int) champ.getLocation().getX();
		int currY = (int) champ.getLocation().getY();
		Champion c = getNearestEnemy(champ,firstPlayer.getTeam());
		
		int cX = (int) c.getLocation().getX();
		int cY = (int) c.getLocation().getY();
		int x = 0;
		int y = 0;
		if(cX < currX)
			x = -1;
		if(cX > currX)
			x = 1;
		if(cY < currY)
			y = -1;
		if(cY > currY)
			y = 1;
		if (y == 1) {
			if (board[currX][currY+y] == null) {
				move(Direction.RIGHT);
				return;
			}
		}
		else if (y == -1) {
			if (board[currX][currY+y] == null) {
				move(Direction.LEFT);
				return;
			}
		}
		else if (x == 1) {
			if (board[currX+x][currY] == null) {
				move(Direction.UP);
				return;
			}
		}
		else if (x == -1) {
			if (board[currX+x][currY] == null) {
				move(Direction.DOWN);
				return;
			}
		}
	}
	public Champion getNearestEnemy(Champion champ , ArrayList<Champion> enemies) {
		Champion c = enemies.get(0);
		for(int i = 0 ; i < enemies.size();i++) {
			if(manhattan(champ.getLocation(),enemies.get(i).getLocation())<manhattan(champ.getLocation(),c.getLocation()))
				c = enemies.get(i);
		}
		return c;
	}
	public int manhattan(Point p1 , Point p2) {
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) ;
		
	}

	public  int getAbilityType(Ability a) {//{1,x} is healing / {2,x} is damagingability // 3 buff 4 debuff
		
			int arr = 0;
			if (a instanceof HealingAbility)
				arr = 1;
			else if (a instanceof DamagingAbility)
				arr = 2;
			else {
				CrowdControlAbility c = ((CrowdControlAbility) a);
				if(c.getEffect().getType() == EffectType.BUFF)
					arr = 3;
				else
					arr = 4;
			}
			return arr;
		
	}

	public ArrayList<Damageable> getDead() {
		return dead;
	}

	

}

