package commons;

import java.util.Objects;

public class Date {

    private int day;
    private String month;
    private int year;

    /**
     * Initializing an empty Date.
     */
    public Date() {
    }

    /**
     * Initializing a Date with proper attributes
     *
     * @param day   The day of the respective date
     * @param month The month of the respective date
     * @param year  The year of the respective date
     */
    public Date(int day, String month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    /**
     * @return the day of an instance of type Date
     */
    public int getDay() {
        return day;
    }

    /**
     * @return the month of an instance of type Date
     */
    public String getMonth() {
        return month;
    }

    /**
     * @return the year of an instance of type Date
     */
    public int getYear() {
        return year;
    }

    /**
     * Changing the value of the day
     *
     * @param day The day of a date
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Changing the value of the month
     *
     * @param month The month of a date
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Changing the value of the year
     *
     * @param year The year of a date
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * A proper equals method for the class Date
     *
     * @param o another object with which we compare
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return day == date.day && year == date.year && month.equals(date.month);
    }

    /**
     * A proper hashCode for the class Date
     *
     * @return the hashCode of an instance of this class
     */
    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    /**
     * @return the format in which the dates shall appear
     */
    @Override
    public String toString() {
        return day + "-" + month + "-" + year;
    }
}
