package com.raistone.wallet.sealwallet.utils;

import java.util.Random;

public class IconCreateUtils {


    public static int getIcon(){
        Random rand = new Random();
        int num = rand.nextInt(25);
        return num;
    }

    public static void main(String[] args){
        int icon = getIcon();
        System.out.println(icon);
    }

}
