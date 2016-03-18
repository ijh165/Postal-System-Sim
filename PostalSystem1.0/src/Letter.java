//  Created by Ivan Jonathan Hoo
//  Copyright (c) 2016 Ivan Jonathan Hoo. All rights reserved.

public class Letter extends Item
{
    //attributes
    private String returnPersonName;

    //constructor
    public Letter(String source, String recipient, String destination, String returnPersonName)
    {
        super(source, recipient, destination, true);
        this.returnPersonName = returnPersonName;
    }

    //return the letter back to source
    public void returnLetter()
    {
        //swap source and destination
        String tmp = source;
        source = destination;
        destination = tmp;
        //reset days parameters
        daysInSystem = 0;
        daysUnpicked = 0;
        //make sure no return person to avoid endless loop
        returnPersonName = PostalSystem.NONE_TEXT;
    }

    //getters
    public String getReturnPersonName()
    {
        return returnPersonName;
    }
}