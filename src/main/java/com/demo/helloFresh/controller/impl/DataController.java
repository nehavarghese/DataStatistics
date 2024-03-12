package com.demo.helloFresh.controller.impl;


import com.demo.helloFresh.controller.IDataController;
import com.demo.helloFresh.service.IDataMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/action")
@Slf4j
public class DataController implements IDataController {

    @Autowired
    IDataMasterService dataMasterService;

    @Override
    public String uploadData(List<String> dataToUpload) throws IOException {
        String response ="";
        try {



            if(null != dataToUpload && !dataToUpload.isEmpty()){
                return dataMasterService.postEvent(dataToUpload);

            }else{
                return "dataToUpload invalid";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override

    @GetMapping("/stats")
    public String getEventStatus(){
        return dataMasterService.getEventStatus();
    }
}
