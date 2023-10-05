package com.example.contextrefreshhikari.controllers;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata")
public class DataSourceMetadataController {

	@Autowired
	DataSource dataSource;

	@GetMapping
	public String getConnectionsUrl() throws SQLException {
		return dataSource.getConnection().getMetaData().getURL();
	}

}
