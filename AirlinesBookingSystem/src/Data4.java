
import javafx.beans.property.SimpleStringProperty;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RAKA
 */
public class Data4 {
    //for booking history
    public final SimpleStringProperty a;
    public final SimpleStringProperty b;
    public final SimpleStringProperty c;
    public final SimpleStringProperty d;
    public final SimpleStringProperty e;
    public final SimpleStringProperty f;
    public final SimpleStringProperty g;

    public Data4(String a, String b, String c, String d, String e, String f,String g) {
        this.a = new SimpleStringProperty(a);
        this.b = new SimpleStringProperty(b);
        this.c = new SimpleStringProperty(c);
        this.d = new SimpleStringProperty(d);
        this.e = new SimpleStringProperty(e);
        this.f = new SimpleStringProperty(f);
        this.g = new SimpleStringProperty(g);
    }

    public String getA() {
        return a.get();
    }

    public void setA(String fName) {
        a.set(fName);
    }

    public String getB() {
        return b.get();
    }

    public void setB(String fName) {
        b.set(fName);
    }

    public String getC() {
        return c.get();
    }

    public void setC(String fName) {
        c.set(fName);
    }

    public String getD() {
        return d.get();
    }

    public void setD(String fName) {
        d.set(fName);
    }

    public String getE() {
        return e.get();
    }

    public void setE(String fName) {
        e.set(fName);
    }

    public String getF() {
        return f.get();
    }

    public void setF(String fName) {
        f.set(fName);
    }
    
    public String getG() {
        return g.get();
    }

    public void setG(String fName) {
        g.set(fName);
    }
}
