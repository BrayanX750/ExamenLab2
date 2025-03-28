/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examenlab2;

public enum Trophy {
    BRONZE(10), SILVER(20), GOLD(30), PLATINUM(50);

    private int puntos;

    Trophy(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }
}

