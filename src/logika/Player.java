package logika;

public enum Player {
    
    X, O;

    public Player nasprotnik() {
        return (this == X ? O : X);
    }

}
