package model;

public class Seat {
    private int id;
    private int screenId;
    private int row;
    private int number;
    private String seatType;
    private boolean isAvailable;

    public Seat() {}

    public Seat(int screenId, int row, int number, String seatType) {
        this.screenId = screenId;
        this.row = row;
        this.number = number;
        this.seatType = seatType;
        this.isAvailable = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getScreenId() { return screenId; }
    public void setScreenId(int screenId) { this.screenId = screenId; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getSeatNumber() {
        return String.format("%c%d", (char)('A' + row - 1), number);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Seat) {
            Seat other = (Seat) obj;
            return this.id == other.id;
        }
        return false;
    }

    @Override
    public String toString() {
        return getSeatNumber() + " (" + seatType + ")";
    }
}