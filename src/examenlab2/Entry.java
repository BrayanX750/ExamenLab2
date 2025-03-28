/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examenlab2;

public class Entry {
    String username;
    long pos;
    Entry next;

    public Entry(String username, long pos) {
        this.username = username;
        this.pos = pos;
        this.next = null;
    }  
}
