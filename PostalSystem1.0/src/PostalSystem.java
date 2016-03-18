//  Created by Ivan Jonathan Hoo
//  Copyright (c) 2016 Ivan Jonathan Hoo. All rights reserved.

import java.io.*;
import java.util.*;

public class PostalSystem
{
    //dir names string constants
    public static final String OUTPUT_DIR = "output/";
    //output file names string constants
    public static final String M_LOG_FNAME = "log_master.txt";
    public static final String F_LOG_FNAME = "log_front.txt";
    //input file names string constants
    public static final String COMMANDS_FNAME = "commands.txt";
    public static final String OFFICES_FNAME = "offices.txt";
    public static final String WANTED_FNAME = "wanted.txt";
    //other file names string constants
    public static final String LOG_FNAME = "log_";
    public static final String TXT_EXT = ".txt";
    //commands string constants
    public static final String DAY_CMD = "DAY";
    public static final String PICKUP_CMD = "PICKUP"; //followed by moar stuffz
    public static final String LETTER_CMD = "LETTER"; //followed by moar stuffz
    public static final String PACKAGE_CMD = "PACKAGE"; //followed by moar stuffz
    //log entry texts string constants
    public static final String DAY_LOG_TEXT = "- - DAY ";
    public static final String OVER_LOG_TEXT = " OVER - -";
    public static final String NEW_LETTER_LOG = "- New LETTER -";
    public static final String NEW_PACKAGE_LOG = "- New PACKAGE -";
    public static final String ACCEPTED_LETTER_LOG = "- Accepted LETTER -";
    public static final String ACCEPTED_PACKAGE_LOG = "- Accepted PACKAGE -";
    public static final String REJECTED_LETTER_LOG = "- Rejected LETTER -";
    public static final String REJECTED_PACKAGE_LOG = "- Rejected PACKAGE -";
    public static final String TRANSIT_SENT_LOG = "- Standard transit departure -";
    public static final String TRANSIT_ARRIVED_LOG = "- Standard transit arrival -";
    public static final String DESTROYED_LETTER_LOG = "- Incinerated LETTER -";
    public static final String DESTROYED_PACKAGE_LOG = "- Incinerated PACKAGE -";
    public static final String ITEM_COMPLETE_LOG = "- Delivery process complete -";
    public static final String BRIBERY_DETECTED_LOG = "- Something funny going on... -";
    public static final String CRIMINAL_APPREHENDED_LOG = "- Criminal Apprehended -";
    //misc texts string constants
    public static final String SOURCE_TEXT = "Source: ";
    public static final String DESTINATION_TEXT = "Destination: ";
    public static final String DELIVERY_TOOK_TEXT = "Delivery took ";
    public static final String N_DAYS_TEXT = " days.";
    public static final String DESTROYED_AT_TEXT = "Destroyed at: ";
    public static final String EXTRA_MONEY_TEXT = "Where did that extra money at ";
    public static final String COME_FROM_TEXT = " come from?";
    public static final String EXTRA_ARRESTED_TEXT = " has been sent to jail and will be executed!";
    public static final String NONE_TEXT = "NONE";

    public static void main(String[] args) throws Exception
    {
        int day = 1;
        File f;
        FileReader fr;
        BufferedReader br;
        FileWriter fw;
        PrintWriter pw;

        //create output dir (if not exists)
        f = new File(OUTPUT_DIR);
        if (!f.exists())
            f.mkdir();

        //create master log file
        f = new File(OUTPUT_DIR + M_LOG_FNAME);
        if(f.exists())
        {
            pw = new PrintWriter(f);
            pw.print("");
            pw.close();
        }
        else
            f.createNewFile();

        //create front page log file
        f = new File(OUTPUT_DIR + F_LOG_FNAME);
        if(f.exists())
        {
            pw = new PrintWriter(f);
            pw.print("");
            pw.close();
        }
        else
            f.createNewFile();

        //read offices.txt and create offices log files
        fr = new FileReader(/*INPUT_DIR + */OFFICES_FNAME);
        br = new BufferedReader(fr);
        PostOffice[] officeArr = new PostOffice[Integer.parseInt(br.readLine())];
        String lineBuff = br.readLine();
        for(int index=0; lineBuff!=null; index++)
        {
            String name = "", transitTime = "", postageRequired = "", capacity = "", persuasionAmount = "", maxPackageLength = "";

            int i = 0;
            while(lineBuff.charAt(i)!=' ')
                name += lineBuff.charAt(i++);
            i++;
            while(lineBuff.charAt(i)!=' ')
                transitTime += lineBuff.charAt(i++);
            i++;
            while(lineBuff.charAt(i)!=' ')
                postageRequired += lineBuff.charAt(i++);
            i++;
            while(lineBuff.charAt(i)!=' ')
                capacity += lineBuff.charAt(i++);
            i++;
            while(lineBuff.charAt(i)!=' ')
                persuasionAmount += lineBuff.charAt(i++);
            i++;
            while(i<lineBuff.length())
                maxPackageLength += lineBuff.charAt(i++);

            officeArr[index] = new PostOffice(name,
                    Integer.parseInt(transitTime),
                    Integer.parseInt(postageRequired),
                    Integer.parseInt(capacity),
                    Integer.parseInt(persuasionAmount),
                    Integer.parseInt(maxPackageLength)
            );

            f = new File(OUTPUT_DIR + LOG_FNAME + name + TXT_EXT);
            if(f.exists())
            {
                pw = new PrintWriter(f);
                pw.print("");
                pw.close();
            }
            else
                f.createNewFile();

            lineBuff = br.readLine();
        }
        br.close();

        //read wanted.txt
        fr = new FileReader(/*INPUT_DIR + */WANTED_FNAME);
        br = new BufferedReader(fr);
        String[] criminalArr = new String[Integer.parseInt(br.readLine())];
        lineBuff = br.readLine();
        for(int index=0; lineBuff!=null; index++)
        {
            criminalArr[index] = lineBuff;
            lineBuff = br.readLine();
        }
        br.close();

        //read commands.txt and write logs depending on the commands
        fr = new FileReader(/*INPUT_DIR + */COMMANDS_FNAME);
        br = new BufferedReader(fr);
        int numOfCommands = Integer.parseInt(br.readLine());
        ArrayDeque<Item> transitItems = new ArrayDeque<Item>(numOfCommands);
        ArrayDeque<Item> acceptedItems = new ArrayDeque<Item>(numOfCommands);
        ArrayDeque<Item> unpickedItems = new ArrayDeque<Item>(numOfCommands);
        for(lineBuff = br.readLine(); lineBuff!=null; lineBuff = br.readLine())
        {
            if(lineBuff.equals(DAY_CMD))
            {
                //remove items from init offices and put in transitItems queue
                while(!acceptedItems.isEmpty())
                {
                    //remove item from corresponding offices
                    transitItems.addFirst(acceptedItems.removeLast());
                    getPostOffice(officeArr, transitItems.getFirst().getSource()).decrNumOfItemsStored();
                    //write transit sent logs
                    fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + transitItems.getFirst().getSource() + TXT_EXT, true);
                    pw = new PrintWriter(fw);
                    pw.println(TRANSIT_SENT_LOG);
                    pw.close();
                }

                //write day logs to master log file
                fw = new FileWriter(OUTPUT_DIR + M_LOG_FNAME, true);
                pw = new PrintWriter(fw);
                pw.println(DAY_LOG_TEXT + Integer.toString(day) + OVER_LOG_TEXT);
                pw.close();
                //write day logs to each offices log file
                for(PostOffice office : officeArr)
                {
                    fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + office.getName() + TXT_EXT, true);
                    pw = new PrintWriter(fw);
                    pw.println(DAY_LOG_TEXT + Integer.toString(day) + OVER_LOG_TEXT);
                    pw.close();
                }

                //next day...
                for(Item itemElement : transitItems) //for each transit items, add one day
                    itemElement.incrDaysInSystem();
                for(Item itemElement : unpickedItems) //for each unpicked items, add one day (in both daysInSystem and daysUnpicked)
                {
                    itemElement.incrDaysInSystem();
                    itemElement.incrDaysUnpicked();
                }
                day++;

                //check if any items has arrived
                ArrayDeque<Item> transitItemsTmp = new ArrayDeque<Item>(numOfCommands);
                while(!transitItems.isEmpty())
                {
                    Item tmpItem = transitItems.removeLast();
                    if(tmpItem.getDaysInSystem()>getPostOffice(officeArr, tmpItem.getSource()).getTransitTime()) //if item has arrived...
                    {
                        //write transit arrived entry to destination post office log
                        fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + tmpItem.getDestination() + TXT_EXT, true);
                        pw = new PrintWriter(fw);
                        pw.println(TRANSIT_ARRIVED_LOG);
                        pw.close();

                        if( getPostOffice(officeArr, tmpItem.getDestination()).isFull() || //destroy item if destination office is full when item arrive
                                itemPackageTooBig(getPostOffice(officeArr, tmpItem.getDestination()), tmpItem) ) //destroy item if it is package and it violates dest leng req
                        {
                            //write letter/package destroyed entry to destination post office log
                            fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + tmpItem.getDestination() + TXT_EXT, true);
                            pw = new PrintWriter(fw);
                            pw.println(tmpItem.isLetter() ? DESTROYED_LETTER_LOG:DESTROYED_PACKAGE_LOG);
                            pw.println(DESTROYED_AT_TEXT + tmpItem.getDestination());
                            pw.close();
                            //write letter/package destroyed entry to master log
                            fw = new FileWriter(OUTPUT_DIR + M_LOG_FNAME, true);
                            pw = new PrintWriter(fw);
                            pw.println(tmpItem.isLetter() ? DESTROYED_LETTER_LOG:DESTROYED_PACKAGE_LOG);
                            pw.println(DESTROYED_AT_TEXT + tmpItem.getDestination());
                            pw.close();
                        }
                        else //otherwise keep item in destination (put in unpickedItems queue and do other stuffz)
                        {
                            unpickedItems.addFirst(tmpItem);
                            unpickedItems.getFirst().incrDaysUnpicked(); //FIX ME!!!
                            getPostOffice(officeArr, tmpItem.getDestination()).incrNumOfItemsStored();
                        }
                    }
                    else //put back in transitItems otherwise
                    {
                        transitItemsTmp.addFirst(tmpItem);
                    }
                }
                transitItems = transitItemsTmp.clone();

                //check if any items has remained unpicked for more than 14 days
                ArrayDeque<Item> unpickedItemsTmp = new ArrayDeque<Item>(numOfCommands);
                while(!unpickedItems.isEmpty())
                {
                    Item tmpItem = unpickedItems.removeLast();
                    if(tmpItem.getDaysUnpicked()>=14) //if item has stayed for 14 days
                    {
                        if(tmpItem.isPackage() || itemLetterNoReturn(tmpItem)) //destroy if item is package or letter without returnPersonName
                        {
                            //write letter/package destroyed entry to destination post office log
                            fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + tmpItem.getDestination() + TXT_EXT, true);
                            pw = new PrintWriter(fw);
                            pw.println(tmpItem.isLetter() ? DESTROYED_LETTER_LOG:DESTROYED_PACKAGE_LOG);
                            pw.println(DESTROYED_AT_TEXT + tmpItem.getDestination());
                            pw.close();
                            //write letter/package destroyed entry to master log
                            fw = new FileWriter(OUTPUT_DIR + M_LOG_FNAME, true);
                            pw = new PrintWriter(fw);
                            pw.println(tmpItem.isLetter() ? DESTROYED_LETTER_LOG:DESTROYED_PACKAGE_LOG);
                            pw.println(DESTROYED_AT_TEXT + tmpItem.getDestination());
                            pw.close();
                        }
                        else //return if it is letter with returnPersonName (send back to source)
                        {
                            //return the letter (treat as new letter and accept it directly)
                            Letter tmpLetter = (Letter) tmpItem;
                            tmpLetter.returnLetter();
                            acceptedItems.addFirst(tmpLetter);
                            //write new letter and accepted letter entry to the office log
                            fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + tmpLetter.getSource() + TXT_EXT, true);
                            pw = new PrintWriter(fw);
                            pw.println(NEW_LETTER_LOG);
                            pw.println(SOURCE_TEXT + tmpLetter.getSource());
                            pw.println(DESTINATION_TEXT + tmpLetter.getDestination());
                            pw.println(ACCEPTED_LETTER_LOG);
                            pw.println(DESTINATION_TEXT + tmpLetter.getDestination());
                            pw.close();
                        }
                    }
                    else //put back in unpickedItems otherwise
                    {
                        unpickedItemsTmp.addFirst(tmpItem);
                    }
                }
                unpickedItems = unpickedItemsTmp.clone();

                //debug stuffz
				/*printOffices(officeArr);*/
            }

            if(lineBuff.length()>=6)
            {
                if(lineBuff.substring(0, 6).equals(PICKUP_CMD))
                {
                    String pickupOffice = "", pickupPerson = "";

                    int i = 7;
                    while(lineBuff.charAt(i)!=' ')
                        pickupOffice += lineBuff.charAt(i++);
                    i++;
                    while(i<lineBuff.length())
                        pickupPerson += lineBuff.charAt(i++);

                    //debug stuffzzzz
					/*System.out.println("unpickedItems queue:");
					for(Item itemElement : unpickedItems)
						System.out.println("Source: " + itemElement.getSource() + "; " +
										   "Recipient: " + itemElement.getRecipient() + "; " +
										   "Destination: " + itemElement.getDestination());
					System.out.println("pickup by " + pickupPerson + " at " + pickupOffice);
					System.out.println("");*/

                    //if a person on the wanted list attempts to pick up any item...
                    if(checkCriminal(criminalArr, pickupPerson))
                    {
                        fw = new FileWriter(OUTPUT_DIR + F_LOG_FNAME);
                        pw = new PrintWriter(fw);
                        pw.println(CRIMINAL_APPREHENDED_LOG);
                        pw.println(pickupPerson + EXTRA_ARRESTED_TEXT);
                        pw.close();
                    }
                    else
                    {
                        //remove any unpicked items if it is picked up
                        for(Item itemElement : unpickedItems)
                        {
                            if(itemElement.getDestination().equals(pickupOffice) && itemElement.getRecipient().equals(pickupPerson))
                            {
                                //write item complete entry to office log
                                fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + itemElement.getDestination() + TXT_EXT, true);
                                pw = new PrintWriter(fw);
                                pw.println(ITEM_COMPLETE_LOG);
                                pw.println(DELIVERY_TOOK_TEXT + (itemElement.getDaysInSystem() + 1) + N_DAYS_TEXT);
                                pw.close();
                                //remove item from destination office
                                getPostOffice(officeArr, itemElement.getDestination()).decrNumOfItemsStored();
                                unpickedItems.remove(itemElement);
                            }
                        }
                    }
                }

                if(lineBuff.substring(0, 6).equals(LETTER_CMD))
                {
                    String source = "", recipient = "", destination = "", returnPersonName = "";

                    int i = 7;
                    while(lineBuff.charAt(i)!=' ')
                        source += lineBuff.charAt(i++);
                    i++;
                    while(lineBuff.charAt(i)!=' ')
                        recipient += lineBuff.charAt(i++);
                    i++;
                    while(lineBuff.charAt(i)!=' ')
                        destination += lineBuff.charAt(i++);
                    i++;
                    while(i<lineBuff.length())
                        returnPersonName += lineBuff.charAt(i++);

                    //write new letter entry to init office log
                    fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + source + TXT_EXT, true);
                    pw = new PrintWriter(fw);
                    pw.println(NEW_LETTER_LOG);
                    pw.println(SOURCE_TEXT + source);
                    pw.println(DESTINATION_TEXT + destination);

                    //accept/reject letter
                    if(checkPostOffice(officeArr, destination) && //make sure destination exist
                            !checkCriminal(criminalArr, recipient) && //make sure recipient is not criminal
                            !getPostOffice(officeArr, source).isFull() //make sure init office still has room
                            )
                    {
                        //accept letter
                        Letter tmpLetter = new Letter(source, recipient, destination, returnPersonName);
                        acceptedItems.addFirst(tmpLetter);
                        getPostOffice(officeArr, tmpLetter.getSource()).incrNumOfItemsStored();
                        //write accepted letter entry to office log
                        pw.println(ACCEPTED_LETTER_LOG);
                        pw.println(DESTINATION_TEXT + destination);
                        pw.close();

                        //debug stuffz
						/*System.out.println("Num of items in " + getPostOffice(officeArr, tmpLetter.getSource()).getName() + ": " +
											getPostOffice(officeArr, tmpLetter.getSource()).getNumOfItemsStored());
						System.out.println("");*/
                    }
                    else //reject otherwise
                    {
                        //write rejected letter entry to office log
                        pw.println(REJECTED_LETTER_LOG);
                        pw.println(SOURCE_TEXT + source);
                        pw.close();
                        //write rejected letter entry to master log
                        fw = new FileWriter(OUTPUT_DIR + M_LOG_FNAME, true);
                        pw = new PrintWriter(fw);
                        pw.println(REJECTED_LETTER_LOG);
                        pw.println(SOURCE_TEXT + source);
                        pw.close();
                    }
                }
            }

            if(lineBuff.length()>=7)
                if(lineBuff.substring(0, 7).equals(PACKAGE_CMD))
                {
                    String source = "", recipient = "", destination = "", money = "", length = "";

                    int i = 8;
                    while(lineBuff.charAt(i)!=' ')
                        source += lineBuff.charAt(i++);
                    i++;
                    while(lineBuff.charAt(i)!=' ')
                        recipient += lineBuff.charAt(i++);
                    i++;
                    while(lineBuff.charAt(i)!=' ')
                        destination += lineBuff.charAt(i++);
                    i++;
                    while(lineBuff.charAt(i)!=' ')
                        money += lineBuff.charAt(i++);
                    i++;
                    while(i<lineBuff.length())
                        length += lineBuff.charAt(i++);

                    //write new package entry to init office log
                    fw = new FileWriter(OUTPUT_DIR + LOG_FNAME + source + TXT_EXT, true);
                    pw = new PrintWriter(fw);
                    pw.println(NEW_PACKAGE_LOG);
                    pw.println(SOURCE_TEXT + source);
                    pw.println(DESTINATION_TEXT + destination);

                    //accept/reject package
                    if( checkPostOffice(officeArr, destination) && //make sure existing destination
                            !checkCriminal(criminalArr, recipient) && //make sure recipient is not criminal
                            !getPostOffice(officeArr, source).isFull() && //accept letter if init office still has room
                            Integer.parseInt(money)>=getPostOffice(officeArr, source).getPostageRequired() && //make sure sufficient funds
                            //make sure length requirements ok or money enough to cover both postage and bribe
                            ( ( Integer.parseInt(length)<=getPostOffice(officeArr, source).getMaxPackageLength() &&
                                    Integer.parseInt(length)<=getPostOffice(officeArr, destination).getMaxPackageLength() ) ||
                                    Integer.parseInt(money)>=getPostOffice(officeArr, source).getPostageRequired()+getPostOffice(officeArr, source).getPersuasionAmount()
                            )

                            )
                    {
                        //accept package
                        Package tmpPackage = new Package(source, recipient, destination, Integer.parseInt(money), Integer.parseInt(length));
                        acceptedItems.addFirst(tmpPackage);
                        getPostOffice(officeArr, tmpPackage.getSource()).incrNumOfItemsStored();
                        //write accepted package entry to office log
                        pw.println(ACCEPTED_PACKAGE_LOG);
                        pw.println(DESTINATION_TEXT + destination);
                        pw.close();

                        if(Integer.parseInt(money)>=getPostOffice(officeArr, source).getPostageRequired()+getPostOffice(officeArr, source).getPersuasionAmount())
                        {
                            //write bribery detected entry to master log
                            fw = new FileWriter(OUTPUT_DIR + M_LOG_FNAME, true);
                            pw = new PrintWriter(fw);
                            pw.println(BRIBERY_DETECTED_LOG);
                            pw.println(EXTRA_MONEY_TEXT + source + COME_FROM_TEXT);
                            pw.close();
                        }

                        //debug stuffz
						/*System.out.println("Num of items in " + getPostOffice(officeArr, tmpPackage.getSource()).getName() + ": " +
											getPostOffice(officeArr, tmpPackage.getSource()).getNumOfItemsStored());
						System.out.println("");*/
                    }
                    else //reject otherwise
                    {
                        //write rejected package entry to office log
                        pw.println(REJECTED_PACKAGE_LOG);
                        pw.println(SOURCE_TEXT + source);
                        pw.close();
                        //write rejected package entry to master log
                        fw = new FileWriter(OUTPUT_DIR + M_LOG_FNAME, true);
                        pw = new PrintWriter(fw);
                        pw.println(REJECTED_PACKAGE_LOG);
                        pw.println(SOURCE_TEXT + source);
                        pw.close();
                    }
                }
        }
        br.close();

        //debug stuffz
		/*System.out.println("At the end of the program, is the unpickedItems queue empty?");
		System.out.println(unpickedItems.isEmpty());*/
    }

    //returns a post office object with the name 'officeName'
    public static PostOffice getPostOffice(PostOffice[] officeArr, String officeName) throws Exception
    {
        for(PostOffice office : officeArr)
            if(office.getName().equals(officeName))
                return office;
        throw new Exception();
    }

    //returns true if a post office with the name 'officeName' exists, false otherwise
    public static boolean checkPostOffice(PostOffice[] officeArr, String officeName)
    {
        for(PostOffice office : officeArr)
            if(office.getName().equals(officeName))
                return true;
        return false;
    }

    //returns true if person is criminal, false otherwise
    public static boolean checkCriminal(String[] criminalArr, String personName)
    {
        for(String crim : criminalArr)
            if(crim.equals(personName))
                return true;
        return false;
    }

    //returns true if item is package and it violates length req of specified office, false otherwise
    public static boolean itemPackageTooBig(PostOffice office, Item checkedItem)
    {
        if(checkedItem.isPackage())
        {
            Package packageTmp = (Package) checkedItem;
            if(packageTmp.getLength()>office.getMaxPackageLength())
                return true;
        }
        return false;
    }

    //returns true if item is letter and it doesn't have returnPersonName, false otherise
    public static boolean itemLetterNoReturn(Item checkedItem)
    {
        if(checkedItem.isLetter())
        {
            Letter letterTmp = (Letter) checkedItem;
            if(letterTmp.getReturnPersonName().equals(NONE_TEXT))
                return true;
        }
        return false;
    }

    //prints every office whether they're full or not (FOR DEBUGGING)
    public static void printOffices(PostOffice[] officeArr)
    {
        for(PostOffice office : officeArr)
            System.out.println(office.getName() + " numOfItemsStored:" + office.getNumOfItemsStored() + " capacity:" + office.getCapacity() + " isFull?" + office.isFull());
        System.out.println("");
    }
}
