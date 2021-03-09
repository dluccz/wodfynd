package word.game.ui.calendar;



public interface Date {



    void setDate(int date);
    void setMonth(int month);
    void setYear(int year);

    long getTime();
    int getDay();
    int getDate();
    int getMonth();
    int getYear();

    int getNumDaysInMonth();

    boolean before(Date other);
    boolean after(Date other);

    Object getDateProvider();

}
