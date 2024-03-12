package com.demo.helloFresh.service;



import java.util.List;

public interface IDataMasterService {
    String postEvent(List<String> inputStream);

    String getEventStatus();
}

