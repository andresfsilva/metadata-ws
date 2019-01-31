/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.ampt2d.metadata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLXML;
import java.util.List;

@Service
public class EnaDbService {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Value("${ena.analysis.query}")
    private String enaAnalysisQuery;

    public List<SQLXML> getEnaAnalysisXml(long rowFrom, long rowTo) {
        List<SQLXML> sqlxmlList;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("rowFrom", rowFrom);
        parameters.addValue("rowTo", rowTo);
        sqlxmlList = jdbcTemplate.queryForList(enaAnalysisQuery, parameters, SQLXML.class);
        return sqlxmlList;
    }
}
