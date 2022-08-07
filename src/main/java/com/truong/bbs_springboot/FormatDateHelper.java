package com.truong.bbs_springboot;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@Component
public class FormatDateHelper {
    private SimpleDateFormat spm = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public String dateToString(Date date) {
        return spm.format(date);
    }

    public Date stringToDate(String date) {
        try {
            return spm.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
