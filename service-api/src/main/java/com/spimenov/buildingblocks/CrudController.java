/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Stanislav Pimenov
 */
@RestController
@RequestMapping("/beer")
public class CrudController {

  @Autowired
  private CouchbaseClient couchbaseClient;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Object getById(String id) {
    return couchbaseClient.testClient();
  }
}
