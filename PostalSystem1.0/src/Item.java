//  Created by Ivan Jonathan Hoo
//  Copyright (c) 2016 Ivan Jonathan Hoo. All rights reserved.

public class Item
{
    //protected attributes (source and destination are post office names)
    protected String source;
    protected String recipient;
    protected String destination;
    protected int daysInSystem;
    protected int daysUnpicked;

    //private attributes
    private final boolean isLetter; //true if item is letter, false if it's package

    //constructor
    public Item(String source, String recipient, String destination, boolean isLetter)
    {
        super();
        this.source = source;
        this.recipient = recipient;
        this.destination = destination;
        this.isLetter = isLetter;
        this.daysInSystem = 0;
        this.daysUnpicked = 0;
    }

    //inrement days functions
    public void incrDaysInSystem()
    {
        daysInSystem++;
    }
    public void incrDaysUnpicked()
    {
        daysUnpicked++;
    }

    //check item type
    public boolean isLetter()
    {
        return isLetter;
    }
    public boolean isPackage()
    {
        return !isLetter;
    }

    //getters
    public String getSource()
    {
        return source;
    }
    public String getRecipient()
    {
        return recipient;
    }
    public String getDestination()
    {
        return destination;
    }
    public int getDaysInSystem()
    {
        return daysInSystem;
    }
    public int getDaysUnpicked()
    {
        return daysUnpicked;
    }
}