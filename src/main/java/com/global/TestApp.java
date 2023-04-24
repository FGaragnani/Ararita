package com.global;

import com.global.battlers.AbstractBattler;
import com.global.battlers.PC;

import java.io.IOException;
import java.util.List;

public class TestApp {

    public static void main(String[] args) throws IOException {
        //PC test1 = new PC("Carletto Giochetto", "Knight");
        PC test2 = new PC("Domenico Modugno", "Knight");

        test2.gainEXP(1000);
    }



}
