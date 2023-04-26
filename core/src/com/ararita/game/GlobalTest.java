package com.ararita.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalTest {

    @Test
    void getMoney() {
        try {
            Global.setMoney(0);
            assertEquals(0, Global.getMoney());
            Global.setMoney(2000);
            assertEquals(2000, Global.getMoney());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setMoney() {
        try {
            Global.setMoney(0);
            assertEquals(0, Global.getMoney());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void canBuy() {
        try {
            Global.setMoney(3000);
            assertTrue(Global.canBuy(Global.getItem("Ether")));
            Global.setMoney(50);
            assertTrue(Global.canBuy(Global.getItem("Cosmic Ether")));
            Global.setMoney(0);
            assertFalse(Global.canBuy(Global.getItem("Ether")));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    void sell() {
    }

    @Test
    void buy() {
    }
}