//  Created by Ivan Jonathan Hoo
//  Copyright (c) 2016 Ivan Jonathan Hoo. All rights reserved.

public class PostOffice
{
    //attributes
    private String name;
    private int transitTime;
    private int postageRequired;
    private int capacity;
    private int persuasionAmount;
    private int maxPackageLength;
    private int numOfItemsStored;

    //constructors
    public PostOffice(String name,
                      int transitTime,
                      int postageRequired,
                      int capacity,
                      int persuasionAmount,
                      int maxPackageLength)
    {
        super();
        this.name = name;
        this.transitTime = transitTime;
        this.postageRequired = postageRequired;
        this.capacity = capacity;
        this.persuasionAmount = persuasionAmount;
        this.maxPackageLength = maxPackageLength;
        this.numOfItemsStored = 0;
    }

    //increment/decrement numOfItemsStored
    public void incrNumOfItemsStored()
    {
        numOfItemsStored++;
    }
    public void decrNumOfItemsStored()
    {
        numOfItemsStored--;
    }

    //returns true if office cannot store more items, false otherwise
    public boolean isFull()
    {
        return (numOfItemsStored == capacity);
    }

    //getters
    public String getName()
    {
        return name;
    }
    public int getTransitTime()
    {
        return transitTime;
    }
    public int getPostageRequired()
    {
        return postageRequired;
    }
    public int getCapacity()
    {
        return capacity;
    }
    public int getPersuasionAmount()
    {
        return persuasionAmount;
    }
    public int getMaxPackageLength()
    {
        return maxPackageLength;
    }
    public int getNumOfItemsStored()
    {
        return numOfItemsStored;
    }
}