package oop.io.demo.booking;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Booking {
    @Id
    private String bookingId;
    //help with dates: https://www.youtube.com/watch?v=xibt9JG1i54
    //more help with dates: https://www.youtube.com/watch?v=2JPJ1OOVT3E
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date bookingDate;
    private String staffEmail;
    private BOOKINGSTATUS bookingStatus;
    private String PassNo;
    private String userEmail;

}
