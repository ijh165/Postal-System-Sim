//  Created by Ivan Jonathan Hoo
//  Copyright (c) 2016 Ivan Jonathan Hoo. All rights reserved.

public class Package extends Item
{
    //attributes
    private int money;
    private int length;

    //constructor
    public Package(String source, String recipient, String destination, int money, int length)
    {
        super(source, recipient, destination, false);
        this.money = money;
        this.length = length;
    }

    //getters
    public int getMoney()
    {
        return money;
    }
    public int getLength()
    {
        return length;
    }
}