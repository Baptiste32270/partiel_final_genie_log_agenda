package agenda;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class Event {

    /**
     * The myTitle of this event
     */
    private String myTitle;
    
    /**
     * The starting time of the event
     */
    private LocalDateTime myStart;

    /**
     * The durarion of the event 
     */
    private Duration myDuration;
    private Repetition myRepetition;
    


    /**
     * Constructs an event
     *
     * @param title the title of this event
     * @param start the start time of this event
     * @param duration the duration of this event
     */
    public Event(String title, LocalDateTime start, Duration duration) {
        this.myTitle = title;
        this.myStart = start;
        this.myDuration = duration;
    }

    public void setRepetition(ChronoUnit frequency) {
        this.myRepetition = new Repetition(frequency);
    }

    public void addException(LocalDate date) {
        if (myRepetition != null) {
            myRepetition.addException(date);
        }
    }

    public void setTermination(LocalDate terminationInclusive) {
        if (myRepetition != null) {
            Termination t = new Termination(myStart.toLocalDate(), myRepetition.getFrequency(), terminationInclusive);
            myRepetition.setTermination(t);
        }
    }

    public void setTermination(long numberOfOccurrences) {
        if (myRepetition != null) {
            Termination t = new Termination(myStart.toLocalDate(), myRepetition.getFrequency(), numberOfOccurrences);
            myRepetition.setTermination(t);
        }
    }

    public int getNumberOfOccurrences() {
        if (myRepetition != null && myRepetition.getTermination() != null) {
            return (int) myRepetition.getTermination().numberOfOccurrences();
        }
        return 0;
    }

    public LocalDate getTerminationDate() {
        if (myRepetition != null && myRepetition.getTermination() != null) {
            return myRepetition.getTermination().terminationDateInclusive();
        }
        return null;
    }

    /**
     * Tests if an event occurs on a given day
     *
     * @param aDay the day to test
     * @return true if the event occurs on that day, false otherwise
     */
    public boolean isInDay(LocalDate aDay) {
        if (myRepetition == null) {
            // Cas simple sans répétition
            return checkOverlap(myStart, myStart.plus(myDuration), aDay);
        } else {
            if (myRepetition.isException(aDay)) {
                return false;
            }
            ChronoUnit freq = myRepetition.getFrequency();
            LocalDate startDay = myStart.toLocalDate();

            if (aDay.isBefore(startDay)) return false;

            long amount = freq.between(startDay, aDay);

            for (long k = amount - 1; k <= amount; k++) {
                if (k < 0) continue;
                
                LocalDateTime occStart = myStart.plus(k, freq);
                LocalDateTime occEnd = occStart.plus(myDuration);
                
                if (myRepetition.isValid(occStart.toLocalDate())) {
                    if (checkOverlap(occStart, occEnd, aDay)) return true;
                }
            }
            return false;
        }
    }

    private boolean checkOverlap(LocalDateTime start, LocalDateTime end, LocalDate day) {
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEnd = day.plusDays(1).atStartOfDay();
        return start.isBefore(dayEnd) && end.isAfter(dayStart);
    }
   
    /**
     * @return the myTitle
     */
    public String getTitle() {
        return myTitle;
    }

    /**
     * @return the myStart
     */
    public LocalDateTime getStart() {
        return myStart;
    }


    /**
     * @return the myDuration
     */
    public Duration getDuration() {
        return myDuration;
    }

    @Override
    public String toString() {
        return "Event{title='%s', start=%s, duration=%s}".formatted(myTitle, myStart, myDuration);
    }
}
