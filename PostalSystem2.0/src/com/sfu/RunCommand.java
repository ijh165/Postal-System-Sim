package com.sfu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sfu.Logging.LogType;

public class RunCommand {
	private static Set<Office> destroyedOffices = new HashSet<>();

	private static Set<Office> offices = new HashSet<>();
	private static Set<String> wanted;
	private static Network network;

	/*private static String baseDir;*/
	private static String commandsFilePath, officesFilePath, wantedFilePath;

	private static Office getOffice(String officeName) {
		for (Office o : offices) {
			if (o.getName().equals(officeName)) {
				return o;
			}
		}
		return null;
	}

	private static List<String> readFileIntoLines(String path) throws Exception {
		int count = 0;
		List<String> lines = new ArrayList<>();
		File file = new File(path);
		FileReader fileReader = new FileReader(file);

		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		int index = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if (index == 0) {
				count = Integer.parseInt(line);
			} else if (line != null && line.length() != 0) {
				lines.add(line);
			}
			index ++;
		}
		fileReader.close();

		if (lines.size() != count) {
			throw new Exception ("Record number does not match: " + path);
		}
		return lines;
	}

	private static Set<Office> initOffices(String path) throws Exception {
		Set<Office> offices = new HashSet<>();
		List<String> lines = readFileIntoLines(path);
		for (String line : lines) {
			String[] tokens = line.split(" ");
			if (tokens.length == 6) {
				Office o = new Office(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
						Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
				offices.add(o);
			}
		}
		return offices;
	}

	private static Set<String> initWanted(String path) throws Exception {
		Set<String> wanted = new HashSet<>();
		List<String> lines = readFileIntoLines(path);
		for (String line : lines) {
			wanted.add(line.trim());
		}
		return wanted;
	}

	private static void initInputFilePaths() throws Exception {
		String baseDir = System.getProperty("user.dir");
		commandsFilePath = baseDir + "\\commands.txt";
		officesFilePath = baseDir + "\\offices.txt";
		wantedFilePath = baseDir + "\\wanted.txt";
	}

	private static void initOutputDir() {
		//create output dir (if not exists)
		String baseDir = System.getProperty("user.dir");
		File f = new File(baseDir + "\\output");
		if (!f.exists()) {
			f.mkdir();
		}
	}

	public static void main(String[] args) throws Exception {
		initInputFilePaths();
		initOutputDir();
		List<String> commands;
		network = new Network();
		try {
			wanted = initWanted(wantedFilePath);
			offices = initOffices(officesFilePath);
			for (Office o : offices) {
				o.setWanted(wanted);
				o.setNetwork(network);
			}
			network.populateOffices(offices);
			commands = readFileIntoLines(commandsFilePath);
		} catch (Exception e) {
			//File reading problem, exit the program
			throw new Exception("Problem happened", e);
		}

		//Initialize Logging
		Logging.init(offices);

		int idx = 0;
		int day = 1;

		boolean hasPendingDeliverables = hasPendingDeliverables();

		while (idx < commands.size() /*|| hasPendingDeliverables*/) {
			//Start of the day, check if any in transit items have arrived
			network.checkAndDeliver(day);

			for (int i = idx ; i< commands.size() ; i++) {
				String cmd = commands.get(i);
				if (isDayCommand(cmd)) {
					idx = i+1;
					break;
				}

				String[] tokens = cmd.split(" ");
				if (isPickupCommand(cmd)) {
					String dest = tokens[1];
					String recipient = tokens[2].trim();
					if (wanted.contains(recipient)) {
						Logging.criminalApprehended(LogType.FRONT, recipient, dest);
					} else {
						Office office = getOffice(dest);
						Deliverable d = office.pickUp(recipient, day);
						if(d instanceof Letter) {
							Letter l = (Letter) d;
							if(wanted.contains(l.getReturnRecipient())) {
								//destroy office if letter sent by criminal picked up
								offices.remove(office);
								destroyedOffices.add(office);
								Logging.officeDestroyed(LogType.MASTER, office.getName());
								Logging.officeDestroyed(LogType.OFFICE, office.getName());
							}
						}
					}
				} else if (isLetterCommand(cmd)) {
					String src = tokens[1];
					String recipient = tokens[2];
					String dest = tokens[3];
					String returnRecipient = tokens[4];
					Office srcOffice = getOffice(src);
					Office destOffice = getOffice(dest);

					Letter letter = new Letter();
					letter.setIniatingOffice(srcOffice);
					letter.setDestOffice(destOffice);
					letter.setInitDay(day);
					letter.setRecipient(recipient);
					letter.setReturnRecipient(returnRecipient);
					letter.setIntendedDest(dest);

					Logging.newDeliverable(LogType.OFFICE, letter);

					boolean hasCriminalRecipient = wanted.contains(letter.getRecipient());
					boolean officeFull = srcOffice.isFull();
					if (destOffice != null && !hasCriminalRecipient && !officeFull) {
						srcOffice.accept(letter);
					} else {
						Logging.rejectDeliverable(LogType.MASTER, letter);
						Logging.rejectDeliverable(LogType.OFFICE, letter);
					}
				} else if (isPackageCommand(cmd)) {
					String src = tokens[1];
					String recipient = tokens[2];
					String dest = tokens[3];
					int money = Integer.parseInt(tokens[4]);
					int length = Integer.parseInt(tokens[5]);

					Office srcOffice = getOffice(src);
					Office destOffice = getOffice(dest);

					Package pkg = new Package();
					pkg.setIniatingOffice(srcOffice);
					pkg.setDestOffice(destOffice);
					pkg.setInitDay(day);
					pkg.setRecipient(recipient);
					pkg.setLength(length);
					pkg.setMoney(money);
					pkg.setIntendedDest(dest);

					Logging.newDeliverable(LogType.OFFICE, pkg);

					boolean hasCriminalRecipient = wanted.contains(pkg.getRecipient());
					boolean officeFull = srcOffice.isFull();
					boolean lengthFitSrc = (length <= srcOffice.getMaxPackageLength());
					boolean postageCovered = pkg.getMoney()>=srcOffice.getRequiredPostage();

					if (!hasCriminalRecipient && !officeFull &&
						postageCovered && lengthFitSrc &&
						destOffice != null && (length <= destOffice.getMaxPackageLength()))
					{
						srcOffice.accept(pkg);
					}
					else if (pkg.getMoney() >= (srcOffice.getRequiredPostage() + srcOffice.getPersuasionAmount()))
					{
						Logging.briberyDetected(LogType.MASTER, pkg);
						srcOffice.accept(pkg);
					}
					else
					{
						Logging.rejectDeliverable(LogType.MASTER, pkg);
						Logging.rejectDeliverable(LogType.OFFICE, pkg);
					}
				} else if (isBuildCommand(cmd)) {
					Office newOffice = new Office(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]),
							Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]));

					//destroy office if build existing office
					for (Office o : offices) {
						if (o.getName().equals(newOffice.getName())) {
							offices.remove(o);
							destroyedOffices.add(o);
							Logging.officeDestroyed(LogType.MASTER, o.getName());
							Logging.officeDestroyed(LogType.OFFICE, o.getName());
							break;
						}
					}

					//remove office from destroyedOffices set if building a destroyed office
					for (Office o : destroyedOffices) {
						if (o.getName().equals(newOffice.getName())) {
							destroyedOffices.remove(o);
							break;
						}
					}

					offices.add(newOffice);

					Logging.officeBuilt(LogType.MASTER, newOffice.getName());
					Logging.officeBuilt(LogType.OFFICE, newOffice.getName());
				}
			}

			//End of the day.
			for (Office o : offices) {
				// Remove deliverables longer than 14 days
				o.drop(day);
				// Send accepted deliverables
				o.sendToNetwork();
			}

			//End of the day. Log end of day.
			Logging.endOfDay(LogType.MASTER, day, null);
			for (Office o : offices) {
				Logging.endOfDay(LogType.OFFICE, day, o.getName());
			}
			for (Office o : destroyedOffices) {
				Logging.endOfDay(LogType.OFFICE, day, o.getName());
			}

			hasPendingDeliverables = hasPendingDeliverables();

			//Ready for next day
			day++;
		}

		Logging.cleanUp();

	}

	private static boolean hasPendingDeliverables() {
		//Checks if in network, there are any deliverables.
		//Checks if in offices, if there are any deliverables.
		boolean hasPendingDeliverables = false;
		if (!network.isNetworkEmpty()) {
			hasPendingDeliverables = true;
		}
		if (!hasPendingDeliverables) {
			for (Office o : offices) {
				if (!o.isEmpty()) {
					hasPendingDeliverables = true;
				}
			}
		}
		return hasPendingDeliverables;
	}

	private static boolean isDayCommand(String command) {
		return command.startsWith("DAY");
	}

	private static boolean isPickupCommand(String command) {
		return command.startsWith("PICKUP");
	}

	private static boolean isLetterCommand(String command) {
		return command.startsWith("LETTER");
	}

	private static boolean isPackageCommand(String command) {
		return command.startsWith("PACKAGE");
	}

	private static boolean isBuildCommand(String command) {
		return command.startsWith("BUILD");
	}
}
