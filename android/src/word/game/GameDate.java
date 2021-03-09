package word.game;

import java.util.Calendar;

import word.game.ui.calendar.Date;

public class GameDate implements Date {

    private Calendar cal;

    public GameDate(){
        cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
    }


    public GameDate(long millis){
        cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
    }


    @Override
    public void setDate(int date) {
        cal.set(Calendar.DAY_OF_MONTH, date);
    }

    @Override
    public void setMonth(int month) {
        cal.set(Calendar.MONTH, month);
    }

    @Override
    public void setYear(int year) {
        cal.set(Calendar.YEAR, year);
    }

    @Override
    public long getTime() {
        return cal.getTimeInMillis();
    }


    @Override
    public int getDay() {
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public int getDate() {
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getMonth() {
        return cal.get(Calendar.MONTH);
    }

    @Override
    public int getYear() {
        return cal.get(Calendar.YEAR);
    }




    @Override
    public int getNumDaysInMonth() {
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }




    @Override
    public boolean before(Date other) {
        return cal.before(other);
    }

    @Override
    public boolean after(Date other) {
        return cal.after(other);
    }


    @Override
    public Object getDateProvider() {
        return cal;
    }


    @Override
    public String toString() {
        return cal.getTime().toString();// cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.YEAR);
    }
}
