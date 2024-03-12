package com.demo.helloFresh.service.impl;


import com.demo.helloFresh.service.IDataMasterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

@Service
@Slf4j
public class DataMasterServiceImpl implements IDataMasterService {

    @Override
    public String postEvent(List<String> inputStream) {
        String output = "";
        try {
            Instant currentTime = Instant.now();
            if(validateInputStream(inputStream)) {

                StringBuilder sb = new StringBuilder();

                for(String s : inputStream){
                    long timeStampEpoch = Long.valueOf(s.substring(0,s.indexOf(",")));
                    Instant instantOfData = Instant.ofEpochMilli ( timeStampEpoch ).atZone(ZoneId.of("GMT-4")).toInstant();
                    Duration timeElapsed = Duration.between(instantOfData, currentTime);
                    if(timeElapsed.getSeconds()<60){//taking entries in the past 60 seconds and when the data is of the future.
                        //output = "less than 60";
                        sb.append(s);
                        sb.append("/n");
                    }//else not in past 60;
                }
                if(!sb.toString().equalsIgnoreCase("")){
                    String finalStringToStoreInFile = sb.toString().trim();
                    output = finalStringToStoreInFile;

                    File f = new File("out.txt");
                    if(f.exists() && !f.isDirectory()) {
                        String appendedDataToSave = "";
                        Scanner myReader = new Scanner(f);
                        while (myReader.hasNextLine()) {
                            String fullData = myReader.nextLine();
                            appendedDataToSave = fullData+"/n"+output;
                            break;
                        }
                        if(null != appendedDataToSave && !"".equalsIgnoreCase(appendedDataToSave)){
                            output = appendedDataToSave;
                        }
                    }
                    //creating.overwriting the file out.txt with updated data recieved from input.
                    FileWriter fstream = new FileWriter("out.txt");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(output);
                    out.close();

                    output= "SUCCESS";
                }
            }else{
                output = "Incorrect format";
            }
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        return output;
    }


    /**
     * Performs validations needed on the input data.
     * @param inputStream
     * @return
     */
    private boolean validateInputStream(List<String> inputStream) {
        if(!(null != inputStream && !inputStream.isEmpty())){
            return false;
        }

        Predicate<String> checkingIfOneLineHas2Comma = l -> (l.length() - l.replace(",", "").length()) !=2 ;
        if(inputStream.stream().anyMatch(checkingIfOneLineHas2Comma)){
            return false;
        }


        Predicate<String> lengthOfTimestampLessThan13 = l -> l.substring(0,l.indexOf(",")).length()!=13;
        if(inputStream.stream().anyMatch(lengthOfTimestampLessThan13)){
            return false;
        }

        Predicate<String> checkingIfAll13AreNumeric = l -> !StringUtils.isNumeric(l.substring(0,l.indexOf(",")));
        if(inputStream.stream().anyMatch(checkingIfAll13AreNumeric)){
            return false;
        }

        Predicate<String> checkingIfXisLessThan0 = l -> !l.substring(14,l.lastIndexOf(",")).startsWith("0.");
        if(inputStream.stream().anyMatch(checkingIfXisLessThan0)){
            return false;
        }

        Predicate<String> checkingIfXis10Digits = l -> l.substring(14,l.lastIndexOf(",")).length() !=12 ;
        if(inputStream.stream().anyMatch(checkingIfXis10Digits)){
            return false;
        }

        Predicate<String> checkingIfYisNumeric = l -> !NumberUtils.isDigits(l.substring(l.lastIndexOf(",")+1));
        if(inputStream.stream().anyMatch(checkingIfYisNumeric)){
            return false;
        }

        Predicate<String> checkingIfYisInRange = l -> !(1073741823<Long.parseLong(l.substring(l.lastIndexOf(",")+1)) && Long.parseLong(l.substring(l.lastIndexOf(",")+1))<2147483647);
        if(inputStream.stream().anyMatch(checkingIfYisInRange)){
            return false;
        }

        return true;
    }


    @Override
    public String getEventStatus() {
        try {
            Instant currentTime = Instant.now();
            File myObj = new File("out.txt");
            Scanner myReader = null;
            myReader = new Scanner(myObj);

            Long sumY = Long.valueOf(0);
            BigDecimal sumX = BigDecimal.valueOf(0);

            int countValid = 0;
            String output = "";

            while (myReader.hasNextLine()) {
                String fullData = myReader.nextLine();
                String[] linesList = fullData.split("/n");
                for(String eachEntry: linesList){
                    long timeStampEpoch = Long.valueOf(eachEntry.substring(0,eachEntry.indexOf(",")));
                    Instant instantOfData = Instant.ofEpochMilli ( timeStampEpoch ).atZone(ZoneId.of("GMT-4")).toInstant();
                    Duration timeElapsed = Duration.between(instantOfData, currentTime);
                    if(timeElapsed.getSeconds()<60 && timeElapsed.getSeconds() > 0){
                        //"less than 60";
                        countValid++;
                        BigDecimal x = new BigDecimal(eachEntry.substring(14,eachEntry.lastIndexOf(",")));
                        Long y = Long.parseLong(eachEntry.substring(eachEntry.lastIndexOf(",")+1));
                        sumX =  sumX.add(x);
                        sumY+=y;
                    }
                }
                if(countValid !=0){//Data gound in past 60 seconds.
                    BigDecimal avgX = sumX.divide(BigDecimal.valueOf(countValid));
                    Long avgY = sumY/countValid;
                    output = countValid+","+sumX+","+avgX+","+sumY+","+avgY;
                }else{
                    output = "No Data Found";
                }
                return output;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "No Data Found";
    }

    @Scheduled(cron = "0 */1 * * * *")
    public synchronized void updateEventDetails() throws IOException {
        log.info("Inside scheduler");
        Instant currentTime = Instant.now();
        File myObj = new File("out.txt");


        Scanner myReader = new Scanner(myObj);

        StringBuilder sb = new StringBuilder();

        while (myReader.hasNextLine()) {
            String fullData = myReader.nextLine();
            String[] linesList = fullData.split("/n");
            for (String eachEntry : linesList) {
                long timeStampEpoch = Long.valueOf(eachEntry.substring(0, eachEntry.indexOf(",")));
                Instant instantOfData = Instant.ofEpochMilli(timeStampEpoch).atZone(ZoneId.of("GMT-4")).toInstant();
                Duration timeElapsed = Duration.between(instantOfData, currentTime);
                if (timeElapsed.getSeconds() < 60 ) {//checking and adding only those, who fall under last minute
                    //"less than 60";
                    sb.append(eachEntry);
                    sb.append("/n");
                }
            }
        }
        String updatedString = "";
        if(!sb.toString().equalsIgnoreCase("")) {//creating file again
            String finalStringToStoreInFile = sb.toString().trim();
            updatedString = finalStringToStoreInFile;
        }
        FileWriter fstream = new FileWriter("out.txt");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(updatedString);
        out.close();
    }
}
