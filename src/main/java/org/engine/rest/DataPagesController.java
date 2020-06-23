package org.engine.rest;

import org.engine.production.service.DataPagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/data")
public class DataPagesController {

    private static final Logger LOG = LoggerFactory.getLogger(DataPagesController.class);

    @Autowired
    private DataPagesService dataPagesService;

    @GetMapping("/page")
    public ResponseEntity<?> page() {

        List<String> list = new ArrayList<>();

        list.add("test 1");
        list.add("test 2");
        list.add("test 3");
        list.add("test 4");

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
