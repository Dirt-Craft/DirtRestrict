package net.dirtcraft.dirtrestrict.Utility;

public class Flipper<T> {
    boolean val = false;
    final T a;
    final T b;

    public Flipper(T a, T b){
        this.a = a;
        this.b = b;
    }

    public T get(){
        val = !val;
        return val? a : b;
    }
}
