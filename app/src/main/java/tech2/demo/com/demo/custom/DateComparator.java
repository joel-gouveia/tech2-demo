package tech2.demo.com.demo.custom;

import java.util.Comparator;

import tech2.demo.com.demo.model.Meals;

/**
 * Created by Joel on 01-Mar-16.
 */
public class DateComparator implements Comparator<Meals> {
    @Override
    public int compare(Meals lhs, Meals rhs) {
        return lhs.getCalendarDate().compareTo(rhs.getCalendarDate());
    }
}
