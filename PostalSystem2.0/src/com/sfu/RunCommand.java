package com.sfu;

import java.io.*;
import java.util.*;

import com.sfu.Logging.LogType;

public class RunCommand {
	//system objects
	private static Set<Office> existingOfficeSet;
	private static Set<Office> destroyedOfficeSet;
	private static Set<String> criminalSet;
	private static Network network;

	//input file paths
	private static String commandsFilePath;
	private static String officesFilePath;
	private static String wantedFilePath;

	private static Office getExistingOffice(String officeName) {
		for (Office o : existingOfficeSet) {
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
			} else if (line.length() != 0) {
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

	private static void initInputFilePaths() {
		String baseDir = System.getProperty("user.dir");
		commandsFilePath = baseDir + "\\commands.txt";
		officesFilePath = baseDir + "\\offices.txt";
		wantedFilePath = baseDir + "\\wanted.txt";
	}

	private static void initOfficeSet() throws Exception {
		List<String> lines = readFileIntoLines(officesFilePath);
		for (String line : lines) {
			String[] tokens = line.split(" ");
			if (tokens.length == 6) {
				Office o = new Office(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
						Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));
				existingOfficeSet.add(o);
			}
		}
	}

	private static void initCriminalSet() throws Exception {
		List<String> lines = readFileIntoLines(wantedFilePath);
		for (String line : lines) {
			criminalSet.add(line.trim());
		}
	}

	private static void createOutputDir() {
		//create output dir (if not exists)
		String baseDir = System.getProperty("user.dir");
		File f = new File(baseDir + "\\output");
		if (!f.exists()) {
			boolean mkdirSuccess = f.mkdir();
			if(!mkdirSuccess){
				System.out.println("Failed to create \"output\" directory!");
				System.exit(1);
			}
		}
	}

	public static void init() {
		//initialize system objects
		existingOfficeSet = new HashSet<>();
		destroyedOfficeSet = new HashSet<>();
		criminalSet = new HashSet<>();
		network = new Network();

		//initialize file I/O
		initInputFilePaths();
		try {
			initOfficeSet();
			initCriminalSet();
		} catch (Exception e) {
			System.out.println("Fail to initialize offices and/or criminals!");
			e.printStackTrace();
			System.exit(1);
		}

		createOutputDir();
	}

	public static void main(String[] args) {
		//Initialize Postal System
		init();

		for (Office o : existingOfficeSet) {
			o.setCriminalSet(criminalSet);
			o.setNetwork(network);
		}

		List<String> cmdList = null;

		try {
			//parse commands file to list of commands
			cmdList = readFileIntoLines(commandsFilePath);
		} catch (Exception e) {
			System.out.println("Fail to read commands!");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			//initialize Logging
			Logging.init(existingOfficeSet);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException happened!");
			e.printStackTrace();
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 not supported");
		}

		//Run the simulation
		runCommands(cmdList);

		//Cleanup Logging
		Logging.cleanUp();
	}

	private static Map<Integer, Integer> extractDayIndexes(List<String> cmdList) {
		Map<Integer, Integer> dayIndexMap = new HashMap<>();
		dayIndexMap.put(1,0);
		for(int day=1, idx=0; idx<cmdList.size(); idx++) {
			if(isDayCommand(cmdList.get(idx)) && idx!=cmdList.size()-1) {
				day++;
				dayIndexMap.put(day,idx+1);
			}
		}

		//debug stuffz
		for(Map.Entry<Integer, Integer> entry : dayIndexMap.entrySet()) {
			System.out.println("<" + entry.getKey() + ", " + entry.getValue() + ">");
		}

		return dayIndexMap;
	}

	private static void runCommands(List<String> cmdList) {
		//map of day to index which points to the start of the day in the cmdList (use dayKey not day)
		Map<Integer, Integer> dayIndexMap = extractDayIndexes(cmdList);

		//index and day
		int idx = 0;
		int day = 1;
		int dayKey = 1;

		//flags
		boolean sneak = false;
		boolean lastPickUpSuccess = false;

		//each iteration of this loop simulates one day
		while (idx < cmdList.size())
		{
			//flags
			boolean good = false;
			boolean timeTravelSuccess = false;

			if (!timeTravelSuccess) {
				//update delayed unpicked up deliverables which are ready to be picked up
				for (Office o : existingOfficeSet) {
					o.updatePickUpAvailability(day);
				}

				//check for any transit items have arrived
				network.checkAndDeliver(day);
			}

			//a loop than run all iterations in one day
			for (int i=idx; i<cmdList.size(); i++)
			{
				String cmd = cmdList.get(i);

				//1 word cmd
				if (isDayCommand(cmd)) {
					idx = i+1;
					break;
				}
				if (isGoodCommand(cmd)) {
					if (lastPickUpSuccess) {
						good = true;
						Logging.goodDay(LogType.MASTER, null);
					}
				}

				//ignore commands for the rest of the day if good triggered
				if (!good) {
					String[] tokens = cmd.split(" ");
					//process >1 word cmd
					if (isPickupCommand(cmd)) {
						String dest = tokens[1];
						String recipient = tokens[2].trim();
						if (criminalSet.contains(recipient)) {
							Logging.criminalApprehended(LogType.FRONT, recipient, dest);
						} else {
							Office office = getExistingOffice(dest);
							if (office != null) {
								List<Deliverable> pickedUpDeliverableList = office.pickUp(recipient, day);
								//toggle lastPickupSuccess flag
								lastPickUpSuccess = !pickedUpDeliverableList.isEmpty();
								//destroy office if any letter sent by criminal picked up
								for (Deliverable d : pickedUpDeliverableList) {
									if (d instanceof Letter) {
										Letter l = (Letter) d;
										if (criminalSet.contains(l.getReturnRecipient())) {
											existingOfficeSet.remove(office);
											destroyedOfficeSet.add(office);
											Logging.officeDestroyed(LogType.MASTER, office.getName());
											Logging.officeDestroyed(LogType.OFFICE, office.getName());
											break;
										}
									}
								}
							}
						}
					} else if (isLetterCommand(cmd)) {
						//parse
						String src = tokens[1];
						String recipient = tokens[2];
						String dest = tokens[3];
						String returnRecipient = tokens[4];
						//fetch offices
						Office srcOffice = getExistingOffice(src);
						Office destOffice = getExistingOffice(dest);
						//object creation
						Letter letter = new Letter();
						letter.setIniatingOffice(srcOffice);
						letter.setDestOffice(destOffice);
						letter.setInitDay(day);
						letter.setRecipient(recipient);
						letter.setReturnRecipient(returnRecipient);
						letter.setIntendedDest(dest);
						//cmd fails if srcOffice is null (prevent null pointer exception)
						if (srcOffice != null) {
							//log new deliverable
							Logging.newDeliverable(LogType.OFFICE, letter);
							//accept/reject deliverable (and log them)
							boolean hasCriminalRecipient = criminalSet.contains(letter.getRecipient());
							boolean officeFull = srcOffice.isFull();
							if ((destOffice != null && !hasCriminalRecipient && !officeFull) || sneak) {
								srcOffice.accept(letter);
							} else {
								Logging.rejectDeliverable(LogType.MASTER, letter);
								Logging.rejectDeliverable(LogType.OFFICE, letter);
							}
						}
						//reset sneak flag
						sneak = false;

					} else if (isPackageCommand(cmd)) {
						//parse
						String src = tokens[1];
						String recipient = tokens[2];
						String dest = tokens[3];
						int money = Integer.parseInt(tokens[4]);
						int length = Integer.parseInt(tokens[5]);
						//fetch offices
						Office srcOffice = getExistingOffice(src);
						Office destOffice = getExistingOffice(dest);
						//object creation
						Package pkg = new Package();
						pkg.setIniatingOffice(srcOffice);
						pkg.setDestOffice(destOffice);
						pkg.setInitDay(day);
						pkg.setRecipient(recipient);
						pkg.setLength(length);
						pkg.setMoney(money);
						pkg.setIntendedDest(dest);
						//cmd fails if srcOffice is null (prevent null pointer exception)
						if (srcOffice != null) {
							//log new deliverable
							Logging.newDeliverable(LogType.OFFICE, pkg);
							//accept/reject deliverable (and log them)
							boolean hasCriminalRecipient = criminalSet.contains(pkg.getRecipient());
							boolean officeFull = srcOffice.isFull();
							boolean lengthFitSrc = (length <= srcOffice.getMaxPackageLength());
							boolean postageCovered = pkg.getMoney() >= srcOffice.getRequiredPostage();
							if ((!hasCriminalRecipient && !officeFull &&
								postageCovered && lengthFitSrc &&
								destOffice != null && (length <= destOffice.getMaxPackageLength()))
								||
								sneak)
							{
								srcOffice.accept(pkg);
							}
							else if (pkg.getMoney() >= srcOffice.getRequiredPostage()+srcOffice.getPersuasionAmount())
							{
								srcOffice.accept(pkg);
								Logging.briberyDetected(LogType.MASTER, pkg);
							}
							else
							{
								Logging.rejectDeliverable(LogType.MASTER, pkg);
								Logging.rejectDeliverable(LogType.OFFICE, pkg);
							}
						}
						//reset sneak flag
						sneak = false;

					} else if (isBuildCommand(cmd)) {
						//create new office object
						Office newOffice = new Office(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]),
								Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]));
						//destroy office if build existing office
						for (Office o : existingOfficeSet) {
							if (o.getName().equals(newOffice.getName())) {
								existingOfficeSet.remove(o);
								destroyedOfficeSet.add(o);
								Logging.officeDestroyed(LogType.MASTER, o.getName());
								Logging.officeDestroyed(LogType.OFFICE, o.getName());
								break;
							}
						}
						//remove office from destroyedOffices set if building is destroyed office
						for (Office o : destroyedOfficeSet) {
							if (o.getName().equals(newOffice.getName())) {
								destroyedOfficeSet.remove(o);
								break;
							}
						}
						//add to existing office set
						existingOfficeSet.add(newOffice);
						//try log office built
						try {
							Logging.officeBuilt(LogType.MASTER, newOffice.getName());
							Logging.officeBuilt(LogType.OFFICE, newOffice.getName());
						} catch (FileNotFoundException e) {
							System.out.println("FileNotFoundException happened!");
							e.printStackTrace();
							System.exit(1);
						} catch (UnsupportedEncodingException e) {
							throw new AssertionError("UTF-8 not supported");
						}

					} else if (isScienceCommand(cmd)) {
						//attempting time travel
						int targetDayKey = dayKey + Integer.parseInt(tokens[1]);
						if (dayIndexMap.get(targetDayKey) != null) {
							dayKey = targetDayKey;
							idx = dayIndexMap.get(targetDayKey);
							timeTravelSuccess = true;
							break;
						} else {
							//destroy all letters awaiting pickup
							for (Office o : existingOfficeSet) {
								o.destroyLettersAwaitingPickup();
							}
							//destroy all packages awaiting pickup
							for (Office o : existingOfficeSet) {
								o.destroyPackagesAwaitingPickup();
							}
							//destroy all offices
							for (Office o : existingOfficeSet) {
								destroyedOfficeSet.add(o);
								Logging.officeDestroyed(LogType.MASTER, o.getName());
								Logging.officeDestroyed(LogType.OFFICE, o.getName());
							}
							existingOfficeSet.clear();
							//end the simulation (aka terminate program)
							Logging.cleanUp();
							System.exit(0);
						}

					} else if (isNsaDelayCommand(cmd)) {
						String delayedRecipient = tokens[1];
						int daysDelayed = Integer.parseInt(tokens[2]);
						if (!network.delayDeliverable(delayedRecipient, daysDelayed)) {
							//check unpicked deliverables if delay on network fail
							for (Office o : existingOfficeSet) {
								o.delayDeliverable(delayedRecipient, daysDelayed);
							}
						}
					} else if (isSneakCommand(cmd)) {
						sneak = true;
					} else if (isInflationCommand(cmd)) {
						for (Office o : existingOfficeSet) {
							o.inflation();
						}
					} else if (isDeflationCommand(cmd)) {
						for (Office o : existingOfficeSet) {
							o.deflation();
						}
					}
				}
			}

			//do not change the state of the system if time travel successful
			if (!timeTravelSuccess) {
				//End of the day.
				for (Office o : existingOfficeSet) {
					// Remove deliverables longer than 14 days
					o.dropUnpickedUp(day);
					// Send accepted deliverables
					o.sendToNetwork();
				}

				//Log end of day.
				Logging.endOfDay(LogType.MASTER, day, null);
				for (Office o : existingOfficeSet) {
					Logging.endOfDay(LogType.OFFICE, day, o.getName());
				}
				for (Office o : destroyedOfficeSet) {
					Logging.endOfDay(LogType.OFFICE, day, o.getName());
				}

				//Ready for next day
				day++;
				dayKey++;
			}
		}
	}

	//checker functions
	public static boolean isDestroyedOffice(Office office) {
		return destroyedOfficeSet.contains(office);
	}
	private static boolean isDayCommand(String command) {
		return command.startsWith("DAY");
	}
	private static boolean isGoodCommand(String command) {
		return command.startsWith("GOOD");
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
	private static boolean isScienceCommand(String command) {
		return command.startsWith("SCIENCE");
	}
	private static boolean isNsaDelayCommand(String command) {
		return command.startsWith("NSADELAY");
	}
	private static boolean isSneakCommand(String command) {
		return command.startsWith("SNEAK");
	}
	private static boolean isInflationCommand(String command) {
		return command.startsWith("INFLATION");
	}
	private static boolean isDeflationCommand(String command) {
		return command.startsWith("DEFLATION");
	}
}
