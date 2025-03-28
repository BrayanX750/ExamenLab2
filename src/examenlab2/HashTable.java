/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examenlab2;

public class HashTable {
    private Entry head;

    public void add(String username, long pos) {
        Entry nuevo = new Entry(username, pos);
        if (head == null) {
            head = nuevo;
        } else {
            Entry actual = head;
            while (actual.next != null) {
                actual = actual.next;
            }
            actual.next = nuevo;
        }
    }

    public void remove(String username) {
        if (head == null) return;

        if (head.username.equals(username)) {
            head = head.next;
            return;
        }

        Entry actual = head;
        while (actual.next != null) {
            if (actual.next.username.equals(username)) {
                actual.next = actual.next.next;
                return;
            }
            actual = actual.next;
        }
    }

    public long search(String username) {
        Entry actual = head;
        while (actual != null) {
            if (actual.username.equals(username)) {
                return actual.pos;
            }
            actual = actual.next;
        }
        return -1;
    }
}

